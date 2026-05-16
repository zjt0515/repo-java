package com.zjt.codingsandbox.docker;

import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Volume;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Creates and reuses long-lived containers. Each container owns one host workspace mounted to /app.
 */
@Slf4j
public class DockerContainerPool implements Closeable {

    public static final String CONTAINER_WORK_DIR = "/app";

    private static final String KEEP_ALIVE_CMD = "while true; do sleep 1000; done";

    private final DockerClient dockerClient;

    private final String poolName;

    private final String image;

    private final int poolSize;

    private final File poolWorkDir;

    private final long memoryBytes;

    private final long cpuCount;

    private final long borrowTimeoutMillis;

    private final BlockingQueue<PooledContainer> idleContainers;

    private final Set<PooledContainer> allContainers = Collections.newSetFromMap(new ConcurrentHashMap<PooledContainer, Boolean>());

    private final Object initLock = new Object();

    private volatile boolean initialized;

    private volatile boolean closed;

    public DockerContainerPool(DockerClient dockerClient,
                               String poolName,
                               String image,
                               int poolSize,
                               File poolWorkDir,
                               long memoryBytes,
                               long cpuCount,
                               long borrowTimeoutMillis) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("poolSize must be greater than 0");
        }
        this.dockerClient = dockerClient;
        this.poolName = poolName;
        this.image = image;
        this.poolSize = poolSize;
        this.poolWorkDir = poolWorkDir;
        this.memoryBytes = memoryBytes;
        this.cpuCount = cpuCount;
        this.borrowTimeoutMillis = borrowTimeoutMillis;
        this.idleContainers = new LinkedBlockingQueue<>(poolSize);
    }

    public PooledContainer borrowContainer() {
        ensureInitialized();
        try {
            PooledContainer container = idleContainers.poll(borrowTimeoutMillis, TimeUnit.MILLISECONDS);
            if (container == null) {
                throw new RuntimeException("docker container pool busy, poolName = " + poolName);
            }
            cleanWorkspace(container);
            return container;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("borrow docker container interrupted", e);
        }
    }

    public void releaseContainer(PooledContainer container) {
        if (container == null) {
            return;
        }
        if (closed || !allContainers.contains(container)) {
            removeContainer(container);
            return;
        }
        try {
            cleanWorkspace(container);
            if (!idleContainers.offer(container)) {
                removeContainer(container);
            }
        } catch (Exception e) {
            log.error("release pooled container error, containerId = {}", container.getContainerId(), e);
            invalidateContainer(container);
        }
    }

    public void invalidateContainer(PooledContainer container) {
        if (container == null) {
            return;
        }
        idleContainers.remove(container);
        removeContainer(container);
        if (!closed) {
            try {
                PooledContainer replacement = createAndStartContainer();
                idleContainers.offer(replacement);
            } catch (Exception e) {
                log.error("create replacement pooled container error, poolName = {}", poolName, e);
            }
        }
    }

    public void copyToWorkspace(PooledContainer container, File sourceDir) {
        if (container == null || sourceDir == null || !sourceDir.exists()) {
            throw new IllegalArgumentException("sourceDir does not exist");
        }
        cleanWorkspace(container);
        copyChildren(sourceDir, container.getWorkDir());
    }

    private void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (initLock) {
            if (initialized) {
                return;
            }
            closed = false;
            FileUtil.mkdir(poolWorkDir);
            pullImageIfNecessary();
            try {
                for (int i = 0; i < poolSize; i++) {
                    idleContainers.offer(createAndStartContainer());
                }
                initialized = true;
                log.info("docker container pool initialized, poolName = {}, size = {}", poolName, poolSize);
            } catch (RuntimeException e) {
                close();
                initialized = false;
                throw e;
            }
        }
    }

    private void pullImageIfNecessary() {
        try {
            dockerClient.inspectImageCmd(image).exec();
            return;
        } catch (NotFoundException ignored) {
            log.info("docker image not found locally, pulling image = {}", image);
        }
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                log.info("pull image status, image = {}, status = {}", image, item.getStatus());
                super.onNext(item);
            }
        };
        try {
            dockerClient.pullImageCmd(image).exec(pullImageResultCallback).awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("pull docker image interrupted, image = " + image, e);
        }
    }

    private PooledContainer createAndStartContainer() {
        File workDir = new File(poolWorkDir, UUID.randomUUID().toString());
        FileUtil.mkdir(workDir);

        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(memoryBytes);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(cpuCount);
        hostConfig.setBinds(new Bind(workDir.getAbsolutePath(), new Volume(CONTAINER_WORK_DIR)));

        String containerName = "coding-sandbox-" + poolName + "-" + UUID.randomUUID().toString();
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(image)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(false)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(false)
                .withCmd("sh", "-c", KEEP_ALIVE_CMD)
                .exec();
        String containerId = createContainerResponse.getId();
        try {
            dockerClient.startContainerCmd(containerId).exec();
            PooledContainer pooledContainer = new PooledContainer(containerId, workDir);
            allContainers.add(pooledContainer);
            return pooledContainer;
        } catch (RuntimeException e) {
            safeRemoveContainer(containerId);
            FileUtil.del(workDir);
            throw e;
        }
    }

    private void removeContainer(PooledContainer container) {
        allContainers.remove(container);
        safeRemoveContainer(container.getContainerId());
        FileUtil.del(container.getWorkDir());
    }

    private void safeRemoveContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).withTimeout(0).exec();
        } catch (Exception e) {
            log.debug("stop pooled container ignored, containerId = {}", containerId, e);
        }
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception e) {
            log.debug("remove pooled container ignored, containerId = {}", containerId, e);
        }
    }

    private void cleanWorkspace(PooledContainer container) {
        File workDir = container.getWorkDir();
        if (!workDir.exists()) {
            FileUtil.mkdir(workDir);
            return;
        }
        File[] files = workDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileUtil.del(file);
        }
    }

    private void copyChildren(File sourceDir, File targetDir) {
        File[] files = sourceDir.listFiles();
        if (files == null) {
            return;
        }
        for (File sourceFile : files) {
            File targetFile = new File(targetDir, sourceFile.getName());
            if (sourceFile.isDirectory()) {
                FileUtil.mkdir(targetFile);
                copyChildren(sourceFile, targetFile);
            } else {
                try {
                    Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("copy file to docker workspace error, file = " + sourceFile.getAbsolutePath(), e);
                }
            }
        }
    }

    @Override
    public void close() {
        closed = true;
        idleContainers.clear();
        for (PooledContainer container : allContainers.toArray(new PooledContainer[0])) {
            removeContainer(container);
        }
        initialized = false;
    }

    @Getter
    public static class PooledContainer {

        private final String containerId;

        private final File workDir;

        private PooledContainer(String containerId, File workDir) {
            this.containerId = containerId;
            this.workDir = workDir;
        }
    }
}
