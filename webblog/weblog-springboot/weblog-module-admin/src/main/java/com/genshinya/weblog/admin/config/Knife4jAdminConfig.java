package com.genshinya.weblog.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author genshinya
 * @time 2024-10-18 09:28:03
 * @description Knife4j配置, 启用Swagger2
 */
@Configuration
@EnableSwagger2WebMvc
@Profile("dev")
public class Knife4jAdminConfig {
    /**
     * Swagger配置信息
     * @return Docket Bean
     */
    @Bean("adminApi")
    public Docket createApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                // 分组名称
                .groupName("Admin 后台接口")
                .select()
                // 这里指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.genshinya.weblog.admin.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 ApiInfo
     * @return ApiInfo
     */
    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("blog 博客后台接口文档") // 标题
                .description("由 Spring Boot + Vue 3.2 + Vite 4.3 开发的前后端分离博客。") // 描述
                .termsOfServiceUrl("https://www.zjtwiki.top/") // API 服务条款
                .contact(new Contact("zjt", "https://www.zjtwiki.top", "876737761@qq.com")) // 联系人
                .version("1.0") // 版本号
                .build();
    }
}
