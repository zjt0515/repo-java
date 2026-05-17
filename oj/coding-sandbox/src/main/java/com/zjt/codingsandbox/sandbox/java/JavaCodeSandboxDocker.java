package com.zjt.codingsandbox.sandbox.java;

import com.github.dockerjava.api.DockerClient;
import com.zjt.codingsandbox.docker.DockerClientManager;
import com.zjt.codingsandbox.docker.DockerCommandExecutor;
import com.zjt.codingsandbox.docker.DockerContainerPool;
import com.zjt.codingsandbox.enums.JudgeInfoMessageEnum;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JavaCodeSandboxDocker extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final String IMAGE = "eclipse-temurin:8-jdk";

    private static final long MEMORY_LIMIT = 100 * 1000 * 1000L;

    @Resource
    private DockerClientManager dockerClientManager;

    @Value("${sandbox.docker.pool.java-size:2}")
    private int poolSize;

    @Value("${sandbox.docker.pool.borrow-timeout-ms:30000}")
    private long borrowTimeoutMillis;

    private DockerContainerPool containerPool;

    private DockerCommandExecutor commandExecutor;

    /**
     * Borrows a running container, syncs current compile outputs into its workspace, then executes them.
     */
    @Override
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        DockerContainerPool pool = getContainerPool();
        DockerContainerPool.PooledContainer container = null;
        boolean reusable = true;
        try {
            container = pool.borrowContainer();
            pool.copyToWorkspace(container, codeFile.getParentFile());

            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            String[] cmdArray = {"java", "-Xmx256m", "-Dfile.encoding=UTF-8", "-cp", DockerContainerPool.CONTAINER_WORK_DIR, "Main"};
            for (String inputArgs : inputList) {
                ExecuteMessage executeMessage = commandExecutor.execute(
                        container.getContainerId(),
                        cmdArray,
                        buildStdin(inputArgs),
                        TIME_OUT
                );
                executeMessageList.add(executeMessage);
                if (JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue().equals(executeMessage.getJudgeInfoMessage())){
                    reusable = false;
                    break;
                }
            }
            return executeMessageList;
        } catch (RuntimeException e) {
            reusable = false;
            throw e;
        } finally {
            if (container != null) {
                if (reusable) {
                    pool.releaseContainer(container);
                } else {
                    pool.invalidateContainer(container);
                }
            }
        }
    }

    private synchronized DockerContainerPool getContainerPool() {
        if (containerPool == null) {
            DockerClient dockerClient = getDockerClientManager().getDockerClient();
            File poolWorkDir = new File(System.getProperty("user.dir"), "tmpDockerPool/java");
            containerPool = new DockerContainerPool(
                    dockerClient,
                    "java",
                    IMAGE,
                    poolSize,
                    poolWorkDir,
                    MEMORY_LIMIT,
                    1L,
                    borrowTimeoutMillis
            );
            commandExecutor = new DockerCommandExecutor(dockerClient);
        }
        return containerPool;
    }

    private DockerClientManager getDockerClientManager() {
        if (dockerClientManager == null) {
            dockerClientManager = new DockerClientManager();
        }
        return dockerClientManager;
    }

    private String buildStdin(String inputArgs) {
        if (inputArgs == null) {
            return "";
        }
        String[] inputArgsArray = inputArgs.split(" ");
        StringBuilder stdinBuilder = new StringBuilder();
        for (String inputArg : inputArgsArray) {
            stdinBuilder.append(inputArg).append('\n');
        }
        return stdinBuilder.toString();
    }

    @PreDestroy
    public void destroy() {
        if (containerPool != null) {
            containerPool.close();
        }
    }
}
