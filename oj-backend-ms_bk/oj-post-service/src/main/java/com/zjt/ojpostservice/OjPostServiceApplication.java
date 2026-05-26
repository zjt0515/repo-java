package com.zjt.ojpostservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.zjt.ojpostservice.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.zjt")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zjt.ojserviceclient.service"})
public class OjPostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjPostServiceApplication.class, args);
    }
}
