package com.zjt.codingsandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zjt.codingsandbox.enums.JudgeInfoMessageEnum;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

/**
 * Shared Docker exec wrapper for stdout/stderr, exit code, timeout, and memory sampling.
 */
@Slf4j
public class DockerCommandExecutor {

    private final DockerClient dockerClient;

    private static final int OUTPUT_LIMIT = 1000000;

    public DockerCommandExecutor(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public ExecuteMessage execute(String containerId, String[] cmdArray, String stdinText, long timeoutMillis) throws InterruptedException {
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(cmdArray)
                .withAttachStdin(stdinText != null)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .exec();

        StringBuilder messageBuilder = new StringBuilder();
        StringBuilder errorMessageBuilder = new StringBuilder();
        String judgeInfoMessage = null;
        AtomicLong maxMemory = new AtomicLong(0L);
        AtomicLong statsSampleCount = new AtomicLong(0L);

        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        ResultCallback.Adapter<Statistics> statisticsResultCallback = new ResultCallback.Adapter<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                if (statistics.getMemoryStats() != null && statistics.getMemoryStats().getUsage() != null) {
                    Long memory = statistics.getMemoryStats().getUsage();
                    maxMemory.updateAndGet(currentMaxMemory -> Math.max(currentMaxMemory, memory));
                    statsSampleCount.incrementAndGet();
                    log.info(memory.toString());
                }
                super.onNext(statistics);
            }
        };
        statsCmd.exec(statisticsResultCallback);

        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
            @Override
            public void onNext(Frame frame) {
                String text = new String(frame.getPayload(), StandardCharsets.UTF_8);
                if (StreamType.STDERR.equals(frame.getStreamType())) {
                    errorMessageBuilder.append(text);
                } else {
                    messageBuilder.append(text);
                }
                super.onNext(frame);
            }
        };

        StopWatch stopWatch = new StopWatch();
        ExecuteMessage executeMessage = new ExecuteMessage();
        // docker内部执行代码
        try {
            waitForStatsSample(statsSampleCount, 0L);
            long sampleCountBeforeExecute = statsSampleCount.get();

            stopWatch.start();
            if (stdinText == null) {
                dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .exec(execStartResultCallback);
            } else {
                ByteArrayInputStream stdin = new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));
                dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .withStdIn(stdin)
                        .exec(execStartResultCallback);
            }

            boolean completed;
            if (timeoutMillis > 0) {
                completed = execStartResultCallback.awaitCompletion(timeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                execStartResultCallback.awaitCompletion();
                completed = true;
            }
            stopWatch.stop();
            waitForStatsSample(statsSampleCount, sampleCountBeforeExecute);

            // 超时了
            if (!completed) {
                dockerClient.stopContainerCmd(containerId).withTimeout(0).exec();
                executeMessage.setExitValue(1);
                judgeInfoMessage = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue();
                executeMessage.setJudgeInfoMessage(judgeInfoMessage);
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessage.setMemory(maxMemory.get());
                return executeMessage;
            }

            InspectExecResponse inspectExecResponse = dockerClient.inspectExecCmd(execCreateCmdResponse.getId()).exec();
            Long exitCode = inspectExecResponse.getExitCodeLong();
            int exitValue = exitCode == null ? 0 : exitCode.intValue();

            String message = emptyToNull(removeTrailingLineBreak(messageBuilder.toString()));
            String errorMessage = emptyToNull(removeTrailingLineBreak(errorMessageBuilder.toString()));
            // 运行错误
            if (exitValue != 0) {
                //errorMessage = message == null ? "Runtime Error" : message;
                judgeInfoMessage = JudgeInfoMessageEnum.RUNTIME_ERROR.getValue();
            }
            // 输出超限
            if (message != null && message.length() > OUTPUT_LIMIT){
                judgeInfoMessage = JudgeInfoMessageEnum.OUTPUT_LIMIT_EXCEEDED.getValue();
            }

            executeMessage.setExitValue(exitValue);
            executeMessage.setMessage(message);
            executeMessage.setErrMessage(errorMessage);
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            Thread.sleep(1000);
            executeMessage.setMemory(maxMemory.get());
            executeMessage.setJudgeInfoMessage(judgeInfoMessage);
            return executeMessage;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("docker exec interrupted", e);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            try {
                statisticsResultCallback.close();
            } catch (Exception e) {
                log.debug("close docker stats callback ignored", e);
            }
            try {
                statsCmd.close();
            } catch (Exception e) {
                log.debug("close docker stats cmd ignored", e);
            }
        }
    }

    private static String removeTrailingLineBreak(String text) {
        if (text == null) {
            return null;
        }
        while (text.endsWith("\r\n") || text.endsWith("\n")) {
            if (text.endsWith("\r\n")) {
                text = text.substring(0, text.length() - 2);
            } else {
                text = text.substring(0, text.length() - 1);
            }
        }
        return text;
    }

    private static String emptyToNull(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        return text;
    }

    private static void waitForStatsSample(AtomicLong statsSampleCount, long previousSampleCount) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 300L;
        while (statsSampleCount.get() <= previousSampleCount && System.currentTimeMillis() < deadline) {
            Thread.sleep(10L);
        }
    }
}
