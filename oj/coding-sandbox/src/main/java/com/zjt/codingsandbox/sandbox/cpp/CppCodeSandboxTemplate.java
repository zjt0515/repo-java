package com.zjt.codingsandbox.sandbox.cpp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import com.zjt.codingsandbox.model.JudgeInfo;
import com.zjt.codingsandbox.sandbox.CodeSandbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class CppCodeSandboxTemplate implements CodeSandbox {

    protected static final long TIME_OUT = 5000L;

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCppCode";

    private static final String GLOBAL_CPP_FILE_NAME = "Main.cpp";

    private static final String EXEC_FILE_NAME = "main";

    private static final long COMPILE_TIME_OUT = 10000L;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

        File userCodeFile = saveCodeToFile(code);
        for (int i = 0; i < inputList.size(); i++) {
            saveInputToFile(userCodeFile.getParent(), inputList.get(i), i);
        }

        try {
            ExecuteMessage compileMessage = compileFile(userCodeFile);
            if (compileMessage.getExitValue() != 0) {
                List<ExecuteMessage> compileResult = new ArrayList<>();
                compileResult.add(compileMessage);
                return getOutputResponse(compileResult);
            }
            List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputList);
            return getOutputResponse(executeMessageList);
        } finally {
            boolean deleted = deleteFile(userCodeFile);
            if (!deleted) {
                log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
            }
        }
    }

    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_CPP_FILE_NAME;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    public File saveInputToFile(String parentPath, String input, Number i) {
        String inputFilePath = parentPath + File.separator + i + ".txt";
        String inputText = input == null ? "" : input;
        if (!inputText.endsWith("\n")) {
            inputText += "\n";
        }
        return FileUtil.writeString(inputText, inputFilePath, StandardCharsets.UTF_8);
    }

    public ExecuteMessage compileFile(File codeFile) {
        File execFile = getExecFile(codeFile);
        return runLocalCommand(Arrays.asList(
                "g++",
                "-std=c++17",
                "-O2",
                codeFile.getAbsolutePath(),
                "-o",
                execFile.getAbsolutePath()
        ), null, COMPILE_TIME_OUT);
    }

    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        File execFile = getExecFile(codeFile);
        for (int i = 0; i < inputList.size(); i++) {
            File inputFile = new File(codeFile.getParentFile(), i + ".txt");
            ExecuteMessage executeMessage = runLocalCommand(Arrays.asList(execFile.getAbsolutePath()), inputFile, TIME_OUT);
            executeMessageList.add(executeMessage);
            if (StrUtil.isNotBlank(executeMessage.getErrMessage())) {
                break;
            }
        }
        return executeMessageList;
    }

    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        // 取用时最大值，便于判断是否超时
        long maxTime = 0;
        long maxMemory = 0;
        String judgeInfoMessage = null;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrMessage();
            judgeInfoMessage = executeMessage.getJudgeInfoMessage();

            if (StrUtil.isNotBlank(judgeInfoMessage)){
                if (StrUtil.isNotBlank(errorMessage)) {
                    executeCodeResponse.setMessage(errorMessage);
                    // 用户提交的代码执行中存在错误
                    executeCodeResponse.setStatus(3);
                }
                break;
            }

            outputList.add(executeMessage.getMessage());
            // update maxTime and maxMemory
            Long time = executeMessage.getTime();
            Long memory = executeMessage.getMemory();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
            if (memory != null){
                maxMemory = Math.max(maxMemory, memory);
            }
        }
        // 正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMessage(judgeInfoMessage);
        judgeInfo.setMemory(maxMemory / 1024 / 1024);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    public boolean deleteFile(File codeFile) {
        if (codeFile.getParentFile() != null) {
            String userCodeParentPath = codeFile.getParentFile().getAbsolutePath();
            boolean deleted = FileUtil.del(userCodeParentPath);
            System.out.println("delete " + (deleted ? "success" : "failed"));
            return deleted;
        }
        return true;
    }

    protected File getExecFile(File codeFile) {
        return new File(codeFile.getParentFile(), EXEC_FILE_NAME);
    }

    protected String removeTrailingLineBreak(String text) {
        if (text == null) {
            return null;
        }
        if (text.endsWith("\r\n")) {
            return text.substring(0, text.length() - 2);
        }
        if (text.endsWith("\n")) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }

    private ExecuteMessage runLocalCommand(List<String> command, File inputFile, long timeoutMillis) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        Process process = null;
        StopWatch stopWatch = new StopWatch();
        StringBuilder stdoutBuilder = new StringBuilder();
        StringBuilder stderrBuilder = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (inputFile != null) {
                processBuilder.redirectInput(inputFile);
            }
            process = processBuilder.start();
            Thread stdoutThread = readStreamAsync(process.getInputStream(), stdoutBuilder);
            Thread stderrThread = readStreamAsync(process.getErrorStream(), stderrBuilder);

            stopWatch.start();
            boolean completed = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
            stopWatch.stop();

            if (!completed) {
                process.destroyForcibly();
                executeMessage.setExitValue(1);
                executeMessage.setErrMessage("Time Limit Exceeded");
                executeMessage.setTime(TIME_OUT);
                return executeMessage;
            }

            stdoutThread.join(100L);
            stderrThread.join(100L);
            int exitValue = process.exitValue();
            String stdout = removeTrailingLineBreak(stdoutBuilder.toString());
            String stderr = removeTrailingLineBreak(stderrBuilder.toString());
            executeMessage.setExitValue(exitValue);
            executeMessage.setMessage(stdout);
            if (StrUtil.isNotBlank(stderr)) {
                executeMessage.setErrMessage(stderr);
            } else if (exitValue != 0) {
                executeMessage.setErrMessage(StrUtil.isNotBlank(stdout) ? stdout : "Runtime Error");
            }
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            return executeMessage;
        } catch (Exception e) {
            throw new RuntimeException("run command error", e);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private Thread readStreamAsync(InputStream inputStream, StringBuilder outputBuilder) {
        Thread thread = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                char[] buffer = new char[1024];
                int len;
                while ((len = bufferedReader.read(buffer)) != -1) {
                    outputBuilder.append(buffer, 0, len);
                }
            } catch (IOException e) {
                outputBuilder.append(e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
