package com.zjt.ojjudgeservice;

import com.zjt.ojjudgeservice.rabbitMq.InitRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.zjt")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zjt.ojserviceclient.service"})
public class OjJudgeServiceApplication {

    public static void main(String[] args) {
        //InitRabbitMq.doInit();
        SpringApplication.run(OjJudgeServiceApplication.class, args);
    }

}
