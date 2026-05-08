package com.zjt.codingsandbox.sandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import com.zjt.codingsandbox.model.JudgeInfo;
import com.zjt.codingsandbox.utils.ProcessUtils;
import com.zjt.codingsandbox.utils.ResponseUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class CodeSandboxTemplate implements CodeSandbox{

    private static final String GLOBAL_CODE_DIRNAME = "tmpCode";

    // 用户代码类名
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    // 超时时间
    private static final long TIME_OUT = 5000L;

    // 用户代码黑名单
    private static final List<String> blackList = Arrays.asList("Files", "exec");

    // 字典树，用于匹配字符串
    private static final WordTree WORD_TREE;

    private static Boolean IS_PULLED = true;

    // SecurityManager
    private static final String SECURITY_MANAGER_PATH = "/Users/zz/Code/repo-java/oj/coding-sandbox/src/main/resources/security";
    private static final String SECURITY_MANAGER_CLASS_NAME = "UserSecurityManager";

    private static final String IMAGE_NAME = "eclipse-temurin:8-jdk";

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        // 1. 校验请求参数
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();



        // 校验代码，使用wordtree匹配code和黑名单中的字符串,匹配上就输出报错+直接返回
        //String match = WORD_TREE.match(code);
        //if (StrUtil.isNotBlank(match)) {
        //    return getErrResponse(new RuntimeException("代码中包含非法字符: " + match));
        //}

        // 2. code -> file

        // 生成代码文件夹和文件路径
        String userDir = System.getProperty("user.dir");  // 获取当前运行的工作目录
        String globalDir = userDir + File.separator + GLOBAL_CODE_DIRNAME;

        if (!FileUtil.exist(globalDir)) {
            FileUtil.mkdir(globalDir);
        }

        String codeParentPath = globalDir + File.separator + UUID.randomUUID();
        String codePath = codeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;

        File codeFile = FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);

        // 3. Compile
        String compileCmd = String.format("javac -encoding utf-8 -source 8 -target 8 %s", codePath);
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcess(compileProcess, "compile");
            System.out.println(executeMessage);
        } catch (Exception e) {
            // 返回异常消息
            return ResponseUtils.getErrExecuteCodeResponse(e);
        }

        // 4. Docker
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // Pull Image
        if (!IS_PULLED) {
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("Downloading Image..." + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                // 拉取镜像并等待完成
                dockerClient.pullImageCmd(IMAGE_NAME)
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("Pull Image Error!");
                throw new RuntimeException(e);
            }
            IS_PULLED = true;
        }

        // Create Container
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE_NAME);

        HostConfig hostConfig = new HostConfig(); // Set Container Config
        hostConfig.setBinds(new Bind(codeParentPath, new Volume("/app"))); // 文件路径映射(容器挂载目录)
        hostConfig.withMemory(1024 * 1024 * 100L).withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        //hostConfig.withSecurityOpts(Arrays.asList("seccomp="));

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                //.withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(false)
                .withCmd("sh", "-c", "while true; do sleep 1000; done")
                .exec();

        String containerId = createContainerResponse.getId();

        // Launch Container
        dockerClient.startContainerCmd(containerId).exec();

        // 5. Exec Cmd In Docker (Loop)
        // docker exec admiring_ride java -cp /app Main 1 3
        List<ExecuteMessage> executeMessageList = new ArrayList<>(); // Execute List
        for (String inputArgs: inputList){
            final String[] message = new String[1];
            final String[] errMessage = new String[1];

            // Max Exec Time
            long time = 0L;
            final boolean[] isTimeout = {true}; // 每次运行开始都是没超时

            String[] args = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main",}, args);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();

            System.out.println("exec cmd: " + execCreateCmdResponse);
            String execCreateCmdResponseId = execCreateCmdResponse.getId();

            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(){
                @Override
                public void onComplete() {
                    isTimeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)){
                        errMessage[0] = new String(frame.getPayload());
                        System.out.println("exec errResult: " + errMessage[0]);
                    }else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("exec Result: " + message[0]);
                    }
                    super.onNext(frame);
                }
            };
            try {
                dockerClient.execStartCmd(execCreateCmdResponseId).exec(execStartResultCallback).awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Exec Error!");
                throw new RuntimeException(e);
            }

            // Build one executeMessage
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setErrMessage(errMessage[0]);
            executeMessage.setMessage(message[0]);

            executeMessageList.add(executeMessage);
        }

        // 6. Build ExecuteCodeResponse
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();

        // loop executeMessage
        long maxTime = 0;
        for (ExecuteMessage executeMessage: executeMessageList){
            String errorMessage = executeMessage.getErrMessage();
            // One Exec Error!
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }

        // ac
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }

        // 填充response
        executeCodeResponse.setOutputList(outputList);

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        // TODO: 借助第三方库实现
        //judgeInfo.setMemory();

        // 7. Clear File
        if (codeFile.getParentFile() != null) {
            boolean del = FileUtil.del(codeParentPath);
            System.out.println("Delete " + (del ? "success" : "failure"));
        }

        return executeCodeResponse;
    };
}
