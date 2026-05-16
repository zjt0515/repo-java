package com.zjt.codingsandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;

/**
 * Reuses one Docker client so each code execution does not rebuild the Docker HTTP client.
 */
@Slf4j
@Component
public class DockerClientManager {

    private DockerClient dockerClient;

    private DockerHttpClient httpClient;

    public synchronized DockerClient getDockerClient() {
        if (dockerClient == null) {
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerTlsVerify(false)
                    .build();
            httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();
            dockerClient = DockerClientImpl.getInstance(config, httpClient);
        }
        return dockerClient;
    }

    @PreDestroy
    public synchronized void close() {
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (IOException e) {
                log.error("close docker client error", e);
            }
            dockerClient = null;
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("close docker http client error", e);
            }
            httpClient = null;
        }
    }
}
