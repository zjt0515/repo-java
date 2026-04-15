package com.genshinya.weblog.admin.config;

import com.genshinya.weblog.jwt.config.JwtAuthenticationSecurityConfig;
import com.genshinya.weblog.jwt.filter.TokenAuthenticationFilter;
import com.genshinya.weblog.jwt.handler.RestAccessDeniedHandler;
import com.genshinya.weblog.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author genshinya
 * @time 2024-10-17 13:43:51
 * @description Security配置文件
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;

    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用 csrf
        http.csrf().disable().
                // 禁用表单登录
                        formLogin().disable()
                // 设置用户登录认证相关配置
                .apply(jwtAuthenticationSecurityConfig)
                .and()
                .authorizeHttpRequests()
                // 认证所有以 /admin 为前缀的 URL 资源
                .mvcMatchers("/admin/**").authenticated()
                // 其他都需要放行，无需认证
                .anyRequest().permitAll()
                .and()
                // 处理用户未登录访问受保护的资源的情况
                .httpBasic().authenticationEntryPoint(authEntryPoint)
                .and()
                // 处理登录成功后访问受保护的资源，但是权限不够的情况
                .exceptionHandling().accessDeniedHandler(deniedHandler)
                .and()
                // 前后端分离，无需创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 将 Token 校验过滤器添加到用户认证过滤器之前
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // http.csrf().disable(). // 禁用 csrf攻击防护
        //         formLogin().disable() // 禁用表单登录
        //         .apply(jwtAuthenticationSecurityConfig) // 设置JWT用户登录认证相关配置
        //         .and()
        //         .authorizeHttpRequests()
        //         .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
        //         .anyRequest().permitAll() // 其他都需要放行，无需认证
        //         .and()
        //         .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 前后端分离，无需创建会话

        // 测试代码
        // http.authorizeHttpRequests()
        //         .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
        //         .anyRequest().permitAll().and() // 其他都需要放行，无需认证
        //         .formLogin().and() // 使用表单登录
        //         .httpBasic(); // 使用 HTTP Basic 认证
    }

    /**
     * Token 校验过滤器
     *
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }


}
