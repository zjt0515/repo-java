package com.tt.ucbackend;

import lombok.*;
import lombok.experimental.Accessors;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.tt.ucbackend.mapper")
public class UcBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcBackendApplication.class, args);
    }

}
