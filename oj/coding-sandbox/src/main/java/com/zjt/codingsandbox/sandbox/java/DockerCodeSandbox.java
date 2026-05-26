package com.zjt.codingsandbox.sandbox.java;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DockerCodeSandbox extends NativeCodeSandbox {
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

    private static final String JAVAC_8 = "/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/bin/javac";

    // SecurityManager
    private static final String SECURITY_MANAGER_PATH = "/Users/zz/Code/repo-java/oj/coding-sandbox/src/main/resources/security";
    private static final String SECURITY_MANAGER_CLASS_NAME = "UserSecurityManager";

    private static final String IMAGE_NAME = "eclipse-temurin:8-jdk";

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    public static void main(String[] args) {
        DockerCodeSandbox dockerCodeSandbox = new DockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        //String codeFileRelPath ="./testcoding/unsafe/ReadFileError.java";
        String codeFileRelPath = "testcoding/simple/Main.java";
        String code = ResourceUtil.readStr("testcoding/simple/Main.java", StandardCharsets.UTF_8);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setInputList(Arrays.asList("1 3"));

        ExecuteCodeResponse executeCodeResponse = dockerCodeSandbox.execute(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    @Override
    public List<ExecuteMessage> run(List<String> inputList, File compiledFile) {
        String codeParentPath = compiledFile.getParentFile().getAbsolutePath();

        // Build DockerClient
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
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(false)
                .withCmd("sh", "-c", "while true; do sleep 1000; done")
                .exec();

        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        // Launch Container
        dockerClient.startContainerCmd(containerId).exec();

        // Start run Loop
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
                        String successMessage = new String(frame.getPayload());
                        log.info("exec Result: {}", successMessage);
                        if (successMessage.endsWith("\r\n")) {
                            successMessage = successMessage.substring(0, successMessage.length() - 2);
                        } else if (successMessage.endsWith("\n")) {
                            successMessage = successMessage.substring(0, successMessage.length() - 1);
                        }
                        message[0] = successMessage;
                    }
                    super.onNext(frame);
                }
            };
            final long[] maxMemory = {0L};

            // 获取占用内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
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
            });
            statsCmd.exec(statisticsResultCallback);

            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                dockerClient.execStartCmd(execCreateCmdResponseId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.SECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                log.error("run error");
                throw new RuntimeException(e);
            }

            // Build one executeMessage
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setErrMessage(errMessage[0]);
            executeMessage.setMessage(message[0]);
            executeMessage.setMemory(maxMemory[0]);
            executeMessage.setTime(time);

            executeMessageList.add(executeMessage);
        }

        deleteContainer(dockerClient, containerId);

        return executeMessageList;
    }

      /**
     * 清理容器
     *
     * @param dockerClient
     * @param containerId
     */
    private void deleteContainer(DockerClient dockerClient, String containerId){
        // 清理容器
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
