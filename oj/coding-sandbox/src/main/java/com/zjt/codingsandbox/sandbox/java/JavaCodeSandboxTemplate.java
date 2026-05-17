package com.zjt.codingsandbox.sandbox.java;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import com.zjt.codingsandbox.model.JudgeInfo;
import com.zjt.codingsandbox.sandbox.CodeSandbox;
import com.zjt.codingsandbox.utils.ProcessUtils;
import com.zjt.codingsandbox.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Java 代码沙箱模板方法的实现
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;

    private static final String JAVAC_8 = "/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/bin/javac";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        ExecuteMessage executeMessage = checkJavaVersion();
        System.out.println(executeMessage);

        File userCodeFile = saveCodeToFile(code);

        ExecuteMessage compileExecuteMessage = compileFile(userCodeFile);
        System.out.println(compileExecuteMessage);
        if (compileExecuteMessage.getExitValue() == 1){
            return ResponseUtils.getCompileErrExecuteCodeResponse();
        }

        List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputList);

        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        return outputResponse;
    }


    /**
     * code -> file
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 查看java版本
     * @return
     */
    public ExecuteMessage checkJavaVersion() {
        String compileCmd = "java -version";
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcess(compileProcess, "查看java版本");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("版本查看出错");
            }
            return executeMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

            /**
             * compile
             * @param userCodeFile
             * @return
             */
    public ExecuteMessage compileFile(File userCodeFile) {
        ExecuteMessage executeMessage = null;
        String compileCmd = String.format("%s -encoding utf-8 %s", JAVAC_8, userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            executeMessage = ProcessUtils.runProcess(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception ignored) {
        }finally {
            return  executeMessage;
        }
    }

    /**
     * 3、执行文件，获得执行结果列表
     * @param codeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        String codeParentPath = codeFile.getParentFile().getAbsolutePath();

        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", codeParentPath, inputArgs);
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", codeParentPath, inputArgs);

            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", codeParentPath);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制 守护进程
                Thread timeoutThread = new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                timeoutThread.setDaemon(true);
                timeoutThread.start();

                ExecuteMessage executeMessage = ProcessUtils.runProcessWithSin(runProcess, "运行", inputArgs);
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);

                timeoutThread.interrupt();
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 获取ExecuteCodeResponse
     * @param executeMessageList List<ExecuteMessage>
     * @return ExecuteCodeResponse
     */
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

    /**
     * delete codeFile
     * @param codeFile 代码文件
     * @return Boolean isDeleted
     */
    public boolean deleteFile(File codeFile) {
        if (codeFile.getParentFile() != null) {
            String userCodeParentPath = codeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }
}
