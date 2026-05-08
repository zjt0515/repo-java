package com.zjt.codingsandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;
import java.util.List;

public class DockerDemo {
    private static  final String IMAGE = "eclipse-temurin:8-jdk";

    public static void main(String[] args) throws InterruptedException {
        // config
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

        //

        // pull image
        boolean isPulled = true;
        if(!isPulled){
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像" + item.getStatus());
                    super.onNext(item);
                }
            };
            //       拉取 nginx 镜像并等待完成取
            dockerClient.pullImageCmd(IMAGE)
                    .exec(pullImageResultCallback)
                    .awaitCompletion();
        }

        // create container
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE);
        CreateContainerResponse response = containerCmd.withCmd("echo", "Hello")
                .exec();
        System.out.println(response);

        // container info
        String containerId = response.getId();

        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containerList = listContainersCmd.withShowAll(true).exec();

        // launch container
        dockerClient.startContainerCmd(containerId);

        Thread.sleep(5000L);

        // container log
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback(){
            @Override
            public void onNext(Frame item) {
                System.out.println("info:" + new String(item.getPayload()));
                super.onNext(item);
            }
        };

        dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback).awaitCompletion(
        );

        // delete container
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();

        // delete image
//        dockerClient.removeImageCmd(image).withForce(true).exec();
    }
}
