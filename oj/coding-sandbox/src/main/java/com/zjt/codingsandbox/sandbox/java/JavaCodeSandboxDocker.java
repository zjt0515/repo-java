package com.zjt.codingsandbox.sandbox.java;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JavaCodeSandboxDocker extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static  Boolean FIRST_INIT = false;

    private static  final String IMAGE = "eclipse-temurin:8-jdk";

    public static void main(String[] args) {
        JavaCodeSandboxDocker javaNativeCodeSandbox = new JavaCodeSandboxDocker();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    /**
     * 3、创建容器，把文件复制到容器内
     * @param codeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runCode(File codeFile, List<String> inputList)  {
        String userCodeParentPath = codeFile.getParentFile().getAbsolutePath();

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                //.withDockerHost("tcp://localhost:2376")
                .withDockerTlsVerify(false)
                //.withDockerCertPath("/home/zz/.docker")
                //.withRegistryUsername(registryUser)
                //.withRegistryPassword(registryPass)
                //.withRegistryEmail(registryMail)
                //.withRegistryUrl(registryUrl)
                .build();

        // 构建DockerHttpClient
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        // 构建DockerClient
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        //DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // 拉取镜像
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(IMAGE);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            FIRST_INIT = false;
        }

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        //hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .withCmd("sh", "-c", "while true; do sleep 1000; done")
                .exec();

        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // docker exec keen_blackwell java -cp /app Main 1 3
        // 执行命令并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 处理标准输入
            String[] inputArgsArray = inputArgs.split(" ");

            PipedInputStream stdin = new PipedInputStream();
            PipedOutputStream stdinWriter = null;
            try {
                stdinWriter = new PipedOutputStream(stdin);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //String[] cmdArray1 = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            //ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
            //        .withCmd(cmdArray1)
            //        .withAttachStderr(true)
            //        .withAttachStdin(true)
            //        .withAttachStdout(true)
            //        .exec();

            // test
            // 不要把 inputArgs 拼到命令参数里
            String[] cmdArray = {"java", "-cp", "/app", "Main"};

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(false)
                    .exec();

            //ByteArrayInputStream stdinStream =
            //        new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));
            //ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
            //ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();

            System.out.println("创建执行命令：" + execCreateCmdResponse);

            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            // 判断是否超时
            final boolean[] timeout = {true};
            String execId = execCreateCmdResponse.getId();

            // Callback
            //ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    // 如果执行完成，则表示没超时
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("输出错误结果：" + errorMessage[0]);
                    } else {
                        String successMessage = new String(frame.getPayload());
                        if (successMessage.endsWith("\r\n")) {
                            successMessage = successMessage.substring(0, successMessage.length() - 2);
                        } else if (successMessage.endsWith("\n")) {
                            successMessage = successMessage.substring(0, successMessage.length() - 1);
                        }
                        log.info("exec Result: {}", successMessage);
                        message[0] = successMessage;
                    }
                    super.onNext(frame);
                }
            };

            final long[] maxMemory = {0L};

            // 获取占用的内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                @Override
                public void onNext(Statistics statistics) {
                    Long memory = statistics.getMemoryStats().getUsage();
                    System.out.println("内存占用：" + memory);
                    maxMemory[0] = Math.max(memory, maxMemory[0]);
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

            StopWatch stopWatch = new StopWatch();
            // StartCmd
            try {
                dockerClient.execStartCmd(execId)
                        .withStdIn(stdin)
                        .exec(execStartResultCallback);

                // stdin
                for (String s : inputArgsArray) {
                    s = s + "\n";
                    stdinWriter.write(s.getBytes(StandardCharsets.UTF_8));
                    stdinWriter.flush();
                }
                stdinWriter.close();
                stopWatch.start();
                execStartResultCallback.awaitCompletion();

                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException | IOException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setMessage(message[0]);
            executeMessage.setErrMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
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



