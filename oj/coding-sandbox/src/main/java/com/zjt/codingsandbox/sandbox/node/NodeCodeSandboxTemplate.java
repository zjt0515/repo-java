package com.zjt.codingsandbox.sandbox.node;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import com.zjt.codingsandbox.model.JudgeInfo;
import com.zjt.codingsandbox.sandbox.CodeSandbox;
import com.zjt.codingsandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * node 代码沙箱模板方法的实现
 */
@Slf4j
@Component
public abstract class NodeCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpJsCode";

    private static final String GLOBAL_JS_FILE_NAME = "Main.js";

    private static final long TIME_OUT = 5000L;

    private static final String NODE18 = "/Users/zz/.nvm/versions/node/v18.20.7/bin/node";

    private static final Boolean IS_DEV = true;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        File userCodeFile = saveCodeToFile(code);

        for (int i = 0; i < inputList.size(); i++) {
            saveInputToFile(userCodeFile.getParent(), inputList.get(i), i);
        }

        List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputList);

        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        if (!IS_DEV){
            boolean b = deleteFile(userCodeFile);
            if (!b) {
                log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
            }
        }

        return outputResponse;
    }

    public File saveInputToFile(String parentPath, String input, Number i){
        String inputFilePath = parentPath + File.separator + i + ".txt";
        File file = FileUtil.writeString(input, inputFilePath, StandardCharsets.UTF_8);
        return  file;
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
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JS_FILE_NAME;
        File file = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return file;
    }

    /**
     * 3、执行文件，获得执行结果列表
     * @param codeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String runCmd = String.format("%s %s", NODE18 , codeFile.getAbsolutePath());
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
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(3);
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
