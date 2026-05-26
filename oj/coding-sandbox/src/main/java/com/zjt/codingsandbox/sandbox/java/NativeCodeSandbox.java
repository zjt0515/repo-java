package com.zjt.codingsandbox.sandbox.java;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import com.zjt.codingsandbox.model.JudgeInfo;
import com.zjt.codingsandbox.sandbox.CodeSandbox;
import com.zjt.codingsandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class NativeCodeSandbox implements CodeSandbox {
    private static final String GLOBAL_CODE_DIRNAME = "tmpCode";
    private static final String JAVAC_8_MAC = "/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/bin/javac";

    // 用户代码类名
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    // 超时时间
    private static final long TIME_OUT = 5000L;

    // 用户代码黑名单
    private static final List<String> blackList = Arrays.asList("Files", "exec");

    // 字典树，用于匹配字符串
    private static final WordTree WORD_TREE;

    // SecurityManager
    private static final String SECURITY_MANAGER_PATH = "/Users/zz/Code/repo-java/oj/coding-sandbox/src/main/resources/security";
    private static final String SECURITY_MANAGER_CLASS_NAME = "UserSecurityManager";

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    public static void main(String[] args) {
        DockerCodeSandbox nativeCodeSandbox = new DockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        //String codeFileRelPath ="./testcoding/unsafe/ReadFileError.java";
        String codeFileRelPath ="testcoding/simple/Main.java";
        String code = ResourceUtil.readStr("testcoding/simple/Main.java", StandardCharsets.UTF_8);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setInputList(Arrays.asList("1 3"));

        ExecuteCodeResponse executeCodeResponse = nativeCodeSandbox.execute(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        if (inputList == null) {
            inputList = new ArrayList<>();
        }

        // 校验代码，使用wordtree匹配code和黑名单中的字符串,匹配上就输出报错+直接返回
        // String match = WORD_TREE.match(code);
        // if (StrUtil.isNotBlank(match)) {
        //    return getErrResponse(new RuntimeException("代码中包含非法字符: " + match));
        //}

        // 1. Save as File(code -> file)
        File javaFile = saveAsFile(code);

        ExecuteMessage compile = compile(javaFile);
        log.info(compile.toString());


        // 3. 运行代码
        List<ExecuteMessage> executeMessageList = run(inputList, javaFile);

        Boolean b = deleteFile(javaFile);
        if (b){
            log.info("delete success");
        }

        // 4. return ExecuteCodeResponse
        return getExecuteCodeResponse(executeMessageList);
    };


    /**
     * 保存为文件
     * @param code
     */
    public File saveAsFile(String code){
        // 生成用户代码文件夹和文件路径
        String userDir = System.getProperty("user.dir");  // 获取当前运行的工作目录
        String globalDir = userDir + File.separator + GLOBAL_CODE_DIRNAME;

        // 文件夹是否存在
        if (!FileUtil.exist(globalDir)) {
            FileUtil.mkdir(globalDir);
        }

        String codeParentPath = globalDir + File.separator + UUID.randomUUID();
        String codePath = codeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;

        return FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);
    }

    /**
     * 编译
     * @param javaFile
     * @return
     */
    public ExecuteMessage compile(File javaFile){
        String compileCmd = String.format("java -encoding utf-8 %s", javaFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcess(compileProcess, "compile");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return  executeMessage;
        } catch (Exception e) {
            // 返回异常消息
            //return ResponseUtils.getErrExecuteCodeResponse(e);
            // TODO 改造？
            throw new  RuntimeException(e);
        }
    }

    /**
     * run .class
     * @param inputList
     * @param compiledFile
     * @return
     */
    public List<ExecuteMessage> run(List<String> inputList, File compiledFile){
        // 3. Execute code (run cmd)
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 构建java命令
            //String executeCmd = String.format("java -Xmx256m -cp %s Main %s", codeParentPath, inputArgs);
            String executeCmd = String.format("java -Xmx256m -cp %s Main", compiledFile.getParentFile().getAbsolutePath());
            //String executeCmd = String.format("java -Xmx256m -cp %s:%s -Djava.security.manager=%s Main %s", codeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, inputArgs);
            //String executeCmd = String.format("java -Xmx256m -cp %s:%s -Djava.security.manager=%s Main", codeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);

            try {
                Process executeProcess = Runtime.getRuntime().exec(executeCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        executeProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                // 等待process执行，收集信息
                ExecuteMessage executeMessage = ProcessUtils.runProcessWithSin(executeProcess, "execute", inputArgs);
                log.info(executeMessage.toString());
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                // 返回异常消息
                //return ResponseUtils.getErrExecuteCodeResponse(e);
                throw new RuntimeException("run error", e);
            }
        }
        return executeMessageList;
    }

    public ExecuteCodeResponse getExecuteCodeResponse(List<ExecuteMessage> executeMessageList){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();

        long maxTime = 0L;
        long maxMemory = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {
            // check errMessage
            String errMessage = executeMessage.getErrMessage();
            if (StrUtil.isNotBlank(errMessage)) {
                executeCodeResponse.setMessage(errMessage);
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
        // 完成所有输入用例
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
        // 填充response
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setStatus(1);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    public Boolean deleteFile(File codeFile){
        // 文件清理
        if (codeFile.getParentFile() != null) {
            String codeParentPath = codeFile.getParentFile().getAbsolutePath();
            return FileUtil.del(codeParentPath);
        }
        return true;
    }
}
