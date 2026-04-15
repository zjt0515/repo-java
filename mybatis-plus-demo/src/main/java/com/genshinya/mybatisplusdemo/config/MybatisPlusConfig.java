package com.genshinya.mybatisplusdemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.genshinya.mybatisplusdemo.mapper")
public class MybatisPlusConfig {
}