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
 * Docker 容器池：预先创建并复用长期存活的容器。
 * 每个容器绑定一个独立的宿主机工作目录，并挂载到容器内的 /app。
 */
@Slf4j
public class DockerContainerPool implements Closeable {

    public static final String CONTAINER_WORK_DIR = "/app";

    /**
     * 让容器保持常驻，实际代码执行通过后续 docker exec 进入该容器完成。
     */
    private static final String KEEP_ALIVE_CMD = "while true; do sleep 1000; done";

    private final DockerClient dockerClient;

    private final String poolName;

    private final String image;

    private final int poolSize;

    private final File poolWorkDir;

    private final long memoryBytes;

    private final long cpuCount;

    private final long borrowTimeoutMillis;

    /**
     * 空闲容器队列，借出时 poll，归还时 offer。
     */
    private final BlockingQueue<PooledContainer> idleContainers;

    /**
     * 记录池内所有容器，便于关闭容器池时统一清理。
     */
    private final Set<PooledContainer> allContainers = Collections.newSetFromMap(new ConcurrentHashMap<PooledContainer, Boolean>());

    private final Object initLock = new Object();

    private volatile boolean initialized;

    private volatile boolean closed;

    /**
     * 创建一个 Docker 容器池实例。
     *
     * @param dockerClient Docker Java 客户端
     * @param poolName 容器池名称，用于日志和容器命名区分
     * @param image 容器池使用的 Docker 镜像
     * @param poolSize 容器池容量
     * @param poolWorkDir 容器池在宿主机上的根工作目录
     * @param memoryBytes 单个容器的内存限制，单位字节
     * @param cpuCount 单个容器可使用的 CPU 数量
     * @param borrowTimeoutMillis 借用容器的最大等待时间，单位毫秒
     */
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

    /**
     * 从容器池中借出一个空闲容器。
     * <p>
     * 首次调用时会触发容器池懒加载初始化；借出前会清空容器工作目录，
     * 避免上一次执行残留文件影响本次执行。
     *
     * @return 可用于本次执行的池化容器
     * @throws RuntimeException 当容器池繁忙、等待超时或线程中断时抛出
     */
    public PooledContainer borrowContainer() {
        ensureInitialized();
        try {
            PooledContainer container = idleContainers.poll(borrowTimeoutMillis, TimeUnit.MILLISECONDS);
            if (container == null) {
                throw new RuntimeException("docker container pool busy, poolName = " + poolName);
            }
            // 借出前清空上一次执行留下的文件，避免不同用户代码互相影响。
            cleanWorkspace(container);
            return container;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("borrow docker container interrupted", e);
        }
    }

    /**
     * 将正常使用结束的容器归还到空闲队列。
     * <p>
     * 归还前会清理工作目录，使容器恢复到可复用状态；如果容器池已关闭、
     * 容器不属于当前池，或空闲队列无法接收该容器，则直接移除容器。
     *
     * @param container 待归还的池化容器
     */
    public void releaseContainer(PooledContainer container) {
        if (container == null) {
            return;
        }
        if (closed || !allContainers.contains(container)) {
            removeContainer(container);
            return;
        }
        try {
            // 归还前再次清理工作目录，保证容器回到可复用状态。
            cleanWorkspace(container);
            if (!idleContainers.offer(container)) {
                removeContainer(container);
            }
        } catch (Exception e) {
            log.error("release pooled container error, containerId = {}", container.getContainerId(), e);
            invalidateContainer(container);
        }
    }

    /**
     * 将异常或状态不可控的容器置为失效。
     * <p>
     * 失效容器会被停止、删除并清理工作目录；如果容器池仍处于打开状态，
     * 会尝试创建一个新的常驻容器补回池中。
     *
     * @param container 待失效的池化容器
     */
    public void invalidateContainer(PooledContainer container) {
        if (container == null) {
            return;
        }
        // 执行异常或状态不可控的容器不再复用，直接销毁并尝试补一个新容器。
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

    /**
     * 将本次执行所需的代码和输入文件复制到容器对应的工作目录。
     * <p>
     * 该工作目录是宿主机目录，并已挂载到容器内的 {@link #CONTAINER_WORK_DIR}。
     *
     * @param container 目标池化容器
     * @param sourceDir 本次执行的源文件目录
     * @throws IllegalArgumentException 当容器为空、源目录为空或源目录不存在时抛出
     */
    public void copyToWorkspace(PooledContainer container, File sourceDir) {
        if (container == null || sourceDir == null || !sourceDir.exists()) {
            throw new IllegalArgumentException("sourceDir does not exist");
        }
        cleanWorkspace(container);
        copyChildren(sourceDir, container.getWorkDir());
    }

    /**
     * 确保容器池已完成初始化。
     * 使用双重检查和锁保证并发场景下只初始化一次；初始化过程包括创建池工作目录、
     * 检查或拉取镜像，以及创建固定数量的常驻容器。
     */
    private void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (initLock) {
            if (initialized) {
                return;
            }
            // 懒加载初始化容器池，避免应用启动时立即拉镜像和创建容器。
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

    /**
     * 检查本地是否已有目标镜像，缺失时拉取镜像。
     * 本地已有镜像时直接复用，减少首次请求之后的初始化成本。
     *
     * @throws RuntimeException 当镜像拉取过程被中断时抛出
     */
    private void pullImageIfNecessary() {
        try {
            // 本地已有镜像时直接复用，只有缺失时才触发拉取。
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

    /**
     * 创建并启动一个新的常驻容器。
     * 每个容器都会分配独立宿主机工作目录并挂载到容器内 /app，同时配置资源限制、
     * 禁用网络和只读根文件系统；容器启动后通过 keep-alive 命令保持运行。
     *
     * @return 新创建并启动完成的池化容器
     * @throws RuntimeException 当容器创建或启动失败时抛出
     */
    private PooledContainer createAndStartContainer() {
        // 每个容器分配独立工作目录，目录会被挂载到容器内的 /app。
        File workDir = new File(poolWorkDir, UUID.randomUUID().toString());
        FileUtil.mkdir(workDir);

        HostConfig hostConfig = new HostConfig();
        // 限制容器资源并关闭网络，降低用户代码对宿主机和外部环境的影响。
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
                // 容器本身只负责常驻，具体编译/运行命令通过 exec 在复用容器中执行。
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

    /**
     * 从容器池记录中移除容器，并清理 Docker 容器和宿主机工作目录。
     *
     * @param container 待移除的池化容器
     */
    private void removeContainer(PooledContainer container) {
        allContainers.remove(container);
        safeRemoveContainer(container.getContainerId());
        FileUtil.del(container.getWorkDir());
    }

    /**
     * 尽最大努力停止并删除 Docker 容器。
     * <p>
     * 清理阶段会吞掉 Docker 侧状态异常，避免单个容器清理失败影响整个容器池关闭。
     *
     * @param containerId Docker 容器 ID
     */
    private void safeRemoveContainer(String containerId) {
        try {
            // 清理阶段尽量吞掉 Docker 侧状态异常，避免单个容器影响整个容器池关闭。
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

    /**
     * 清空池化容器的宿主机工作目录。
     * <p>
     * 只删除该容器独立挂载目录下的子文件和子目录，不删除容器池根目录。
     *
     * @param container 待清理工作目录的池化容器
     */
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
            // 只删除该容器自己的挂载目录内容，不直接操作容器池根目录。
            FileUtil.del(file);
        }
    }

    /**
     * 递归复制源目录下的所有子文件和子目录到目标目录。
     *
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @throws RuntimeException 当文件复制失败时抛出
     */
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

    /**
     * 关闭容器池并清理所有池化容器。
     * <p>
     * 该方法会清空空闲队列，停止并删除所有已创建容器，删除对应工作目录，
     * 并将容器池状态重置为未初始化。
     */
    @Override
    public void close() {
        closed = true;
        idleContainers.clear();
        // 复制快照后遍历，避免清理过程中修改 allContainers 导致并发遍历问题。
        for (PooledContainer container : allContainers.toArray(new PooledContainer[0])) {
            removeContainer(container);
        }
        initialized = false;
    }

    @Getter
    public static class PooledContainer {

        private final String containerId;

        private final File workDir;

        /**
         * 创建池化容器描述对象。
         *
         * @param containerId Docker 容器 ID
         * @param workDir 该容器绑定的宿主机工作目录
         */
        private PooledContainer(String containerId, File workDir) {
            this.containerId = containerId;
            this.workDir = workDir;
        }
    }
}
