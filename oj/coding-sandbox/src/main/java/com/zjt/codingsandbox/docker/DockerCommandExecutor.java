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
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Shared Docker exec wrapper for stdout/stderr, exit code, timeout, and memory sampling.
 */
@Slf4j
public class DockerCommandExecutor {

    private final DockerClient dockerClient;

    public DockerCommandExecutor(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public ExecuteMessage execute(String containerId, String[] cmdArray, String stdinText, long timeoutMillis) {
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(cmdArray)
                .withAttachStdin(stdinText != null)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .exec();

        StringBuilder messageBuilder = new StringBuilder();
        StringBuilder errorMessageBuilder = new StringBuilder();
        final long[] maxMemory = {0L};

        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        ResultCallback.Adapter<Statistics> statisticsResultCallback = new ResultCallback.Adapter<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                Long memory = statistics.getMemoryStats().getUsage();
                if (memory != null) {
                    maxMemory[0] = Math.max(maxMemory[0], memory);
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
        try {
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

            if (!completed) {
                dockerClient.stopContainerCmd(containerId).withTimeout(0).exec();
                executeMessage.setExitValue(1);
                executeMessage.setErrMessage("Time Limit Exceeded");
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessage.setMemory(maxMemory[0]);
                return executeMessage;
            }

            InspectExecResponse inspectExecResponse = dockerClient.inspectExecCmd(execCreateCmdResponse.getId()).exec();
            Long exitCode = inspectExecResponse.getExitCodeLong();
            int exitValue = exitCode == null ? 0 : exitCode.intValue();

            String message = emptyToNull(removeTrailingLineBreak(messageBuilder.toString()));
            String errorMessage = emptyToNull(removeTrailingLineBreak(errorMessageBuilder.toString()));
            if (exitValue != 0 && errorMessage == null) {
                errorMessage = message == null ? "Runtime Error" : message;
            }

            executeMessage.setExitValue(exitValue);
            executeMessage.setMessage(message);
            executeMessage.setErrMessage(errorMessage);
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            executeMessage.setMemory(maxMemory[0]);
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
}
