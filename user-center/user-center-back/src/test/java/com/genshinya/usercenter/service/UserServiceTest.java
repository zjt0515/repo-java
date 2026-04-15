package com.genshinya.usercenter.service;
import java.util.Date;

import com.genshinya.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 用户服务测试
 *
 * @author genshinya
 */
@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUserName("genshinya");
        user.setUserAccount("876737761@qq.com");
        user.setUserPassword("123456");
        user.setAvatarUrl("https://baomidou.com/img/logo.svg");
        user.setGender(0);
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        // 正常
        String userAccount = "genshinya";
        String userPassword = "123456";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1 ,result);
        // 非空校验
        userAccount = "";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1 ,result);
        userAccount = "genshinya";
        // 位数校验
        userAccount = "123";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1 ,result);
        userAccount = "genshinya";
        // 密码是否相同校验
        checkPassword = "123";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1 ,result);
        checkPassword = "123456";
        // 特殊字符校验
        userAccount = "{father}";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1 ,result);
        userAccount = "genshinya";

    }
}