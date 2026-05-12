package com.zjt.codingsandbox.sandbox.cpp;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CppCodeSandboxDoceker extends CppCodeSandboxTemplate {

    private static Boolean FIRST_INIT = true;

    private static final String IMAGE = "gcc:13-bookworm";

    @Override
    public ExecuteMessage compileFile(File codeFile) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        executeMessage.setExitValue(0);
        return executeMessage;
    }

    @Override
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        String userCodeParentPath = codeFile.getParentFile().getAbsolutePath();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(false)
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        String containerId = null;

        try {
            pullImageIfNeeded(dockerClient);
            containerId = createAndStartContainer(dockerClient, userCodeParentPath);

            ExecuteMessage compileMessage = execCommand(
                    dockerClient,
                    containerId,
                    new String[]{"sh", "-c", "TMPDIR=/app g++ -std=c++17 -O2 -pipe /app/Main.cpp -o /app/main"},
                    0L
            );
            if (compileMessage.getExitValue() != 0) {
                List<ExecuteMessage> compileResult = new ArrayList<>();
                compileResult.add(compileMessage);
                return compileResult;
            }

            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            for (int i = 0; i < inputList.size(); i++) {
                String[] cmdArray = {"sh", "-c", "/app/main < /app/" + i + ".txt"};
                ExecuteMessage executeMessage = execCommand(dockerClient, containerId, cmdArray, TIME_OUT);
                executeMessageList.add(executeMessage);
                if ("Time Limit Exceeded".equals(executeMessage.getErrMessage())) {
                    break;
                }
            }
            return executeMessageList;
        } finally {
            if (containerId != null) {
                deleteContainer(dockerClient, containerId);
            }
            try {
                dockerClient.close();
                httpClient.close();
            } catch (IOException e) {
                log.error("close docker client error", e);
            }
        }
    }

    private void pullImageIfNeeded(DockerClient dockerClient) {
        if (!FIRST_INIT) {
            return;
        }
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(IMAGE);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("pull image: " + item.getStatus());
                super.onNext(item);
            }
        };
        try {
            pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            FIRST_INIT = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("pull image error", e);
        }
    }

    private String createAndStartContainer(DockerClient dockerClient, String userCodeParentPath) {
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(512 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(false)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(false)
                .withCmd("sh", "-c", "while true; do sleep 1000; done")
                .exec();
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
        return containerId;
    }

    private ExecuteMessage execCommand(DockerClient dockerClient, String containerId, String[] cmdArray, long timeoutMillis) {
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(cmdArray)
                .withAttachStdin(false)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .exec();

        ExecuteMessage executeMessage = new ExecuteMessage();
        StringBuilder messageBuilder = new StringBuilder();
        StringBuilder errorMessageBuilder = new StringBuilder();
        StopWatch stopWatch = new StopWatch();
        final long[] maxMemory = {0L};

        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        ResultCallback<Statistics> statisticsResultCallback = new ResultCallback<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                Long memory = statistics.getMemoryStats().getUsage();
                if (memory != null) {
                    maxMemory[0] = Math.max(memory, maxMemory[0]);
                }
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        };
        statsCmd.exec(statisticsResultCallback);

        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
            @Override
            public void onNext(Frame frame) {
                String text = new String(frame.getPayload());
                if (StreamType.STDERR.equals(frame.getStreamType())) {
                    errorMessageBuilder.append(text);
                } else {
                    messageBuilder.append(text);
                }
                super.onNext(frame);
            }
        };

        try {
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(execStartResultCallback);

            stopWatch.start();
            boolean completed;
            if (timeoutMillis > 0) {
                completed = execStartResultCallback.awaitCompletion(timeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                execStartResultCallback.awaitCompletion();
                completed = true;
            }
            stopWatch.stop();

            if (!completed) {
                dockerClient.stopContainerCmd(containerId).exec();
                executeMessage.setExitValue(1);
                executeMessage.setErrMessage("Time Limit Exceeded");
            } else {
                InspectExecResponse inspectExecResponse = dockerClient.inspectExecCmd(execCreateCmdResponse.getId()).exec();
                Long exitCode = inspectExecResponse.getExitCodeLong();
                int exitValue = exitCode == null ? 0 : exitCode.intValue();
                String errorMessage = removeTrailingLineBreak(errorMessageBuilder.toString());
                String message = removeTrailingLineBreak(messageBuilder.toString());
                if (errorMessage != null && errorMessage.length() == 0) {
                    errorMessage = null;
                }
                if (message != null && message.length() == 0) {
                    message = null;
                }
                if (exitValue != 0 && (errorMessage == null || errorMessage.length() == 0)) {
                    errorMessage = message == null ? "Runtime Error" : message;
                }
                executeMessage.setMessage(message);
                executeMessage.setErrMessage(errorMessage);
                executeMessage.setExitValue(exitValue);
            }
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            executeMessage.setMemory(maxMemory[0]);
            return executeMessage;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("program execution error", e);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            try {
                statsCmd.close();
            } catch (Exception e) {
                log.error("close stats cmd error", e);
            }
        }
    }

    private void deleteContainer(DockerClient dockerClient, String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (Exception e) {
            log.error("stop container error, containerId = {}", containerId, e);
        }
        try {
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (Exception e) {
            log.error("remove container error, containerId = {}", containerId, e);
        }
    }
}
