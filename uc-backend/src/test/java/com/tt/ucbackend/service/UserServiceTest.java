package com.tt.ucbackend.service;

import com.tt.ucbackend.mapper.UserMapper;
import com.tt.ucbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * @author tt
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    void testAddUeser(){
        User user = new User();
        user.setUserName("steve");
        user.setUserAccount("11233");
        user.setUserRole(0);
        user.setUserPassword("123456");
        user.setUserStatus(0);

        boolean result = userService.save(user);
        System.out.println(user.getUuid());
        assertTrue(result);
    }

    @Test
    void userRegister() {
        // 位数不够
        // long test1 = userService.userRegister("test", "123456", "123456", "B22040515");
        // assertEquals(-1 ,test1);
        // // 密码不一致
        // long test2 = userService.userRegister("test", "12345678", "123456","B22040515");
        // assertEquals(-1, test1);
        // 成功
        long testFinal = userService.userRegister("test0915", "12345678", "12345678","B22040515");
        assertTrue(testFinal > 0);
    }
}