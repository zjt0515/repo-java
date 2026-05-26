package com.zjt.codingsandbox.sandbox.cpp;

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
public class CppCodeSandboxDocker extends CppCodeSandboxTemplate {

    private static final String IMAGE = "gcc:13-bookworm";

    private static final long MEMORY_LIMIT = 512 * 1000 * 1000L;

    private static final long COMPILE_TIME_OUT = 10000L;

    @Resource
    private DockerClientManager dockerClientManager;

    @Value("${sandbox.docker.pool.cpp-size:1}")
    private int poolSize;

    @Value("${sandbox.docker.pool.borrow-timeout-ms:30000}")
    private long borrowTimeoutMillis;

    private DockerContainerPool containerPool;

    private DockerCommandExecutor commandExecutor;

    /**
     * Docker 模式在容器内编译，避免依赖宿主机 g++ 或产生宿主机平台的可执行文件。
     */
    // @Override
    // public ExecuteMessage compileFile(File codeFile) {
    //     ExecuteMessage executeMessage = new ExecuteMessage();
    //     executeMessage.setExitValue(0);
    //     return executeMessage;
    // }

    /**
     * 借用常驻容器，把当前 C++ 源码和输入文件同步到容器工作目录后编译并执行。
     */
    @Override
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        DockerContainerPool pool = getContainerPool();
        DockerContainerPool.PooledContainer container = null;
        boolean reusable = true;
        try {
            container = pool.borrowContainer();
            pool.copyToWorkspace(container, codeFile.getParentFile());

            ExecuteMessage compileMessage = commandExecutor.execute(
                    container.getContainerId(),
                    new String[]{"sh", "-c", "TMPDIR=/app g++ -std=c++17 -O2 -pipe /app/Main.cpp -o /app/main"},
                    null,
                    COMPILE_TIME_OUT
            );
            if (JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue().equals(compileMessage.getJudgeInfoMessage())) {
                reusable = false;
                return singleResult(compileMessage);
            }
            if (compileMessage.getExitValue() != 0) {
                compileMessage.setJudgeInfoMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
                return singleResult(compileMessage);
            }

            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            for (int i = 0; i < inputList.size(); i++) {
                String[] cmdArray = {"sh", "-c", "/app/main < /app/" + i + ".txt"};
                ExecuteMessage executeMessage = commandExecutor.execute(
                        container.getContainerId(),
                        cmdArray,
                        null,
                        TIME_OUT
                );
                executeMessageList.add(executeMessage);
                if (JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue().equals(executeMessage.getJudgeInfoMessage())) {
                    reusable = false;
                    break;
                }
            }
            return executeMessageList;
        } catch (RuntimeException e) {
            reusable = false;
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            File poolWorkDir = new File(System.getProperty("user.dir"), "tmpDockerPool/cpp");
            containerPool = new DockerContainerPool(
                    dockerClient,
                    "cpp",
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

    private List<ExecuteMessage> singleResult(ExecuteMessage executeMessage) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        executeMessageList.add(executeMessage);
        return executeMessageList;
    }

    @PreDestroy
    public void destroy() {
        if (containerPool != null) {
            containerPool.close();
        }
    }
}
