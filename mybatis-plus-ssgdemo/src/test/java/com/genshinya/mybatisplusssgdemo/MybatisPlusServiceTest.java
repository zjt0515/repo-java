package com.genshinya.mybatisplusssgdemo;

import com.genshinya.mybatisplusssgdemo.pojo.User;
import com.genshinya.mybatisplusssgdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MybatisPlusServiceTest {

    @Autowired // 自动装配注解
    private UserService userService;

    @Test
    public void testGetCount(){
        // 查询总记录数
        long count = userService.count();
        System.out.println(count);
    }

    @Test
    public void testInsert(){
        // 批量添加
        // INSERT INTO user ( id, name, age ) VALUES ( ?, ?, ? )
        List<User> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            User user = new User();
            user.setName("zjt" + i);
            user.setAge(20+i);
            list.add(user);
        }
        boolean b = userService.saveBatch(list);
        System.out.println(b);
    }

}
