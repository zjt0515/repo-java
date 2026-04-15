package com.tt.ucbackend;

import com.tt.ucbackend.mapper.UserMapper;
import com.tt.ucbackend.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {UcBackendApplication.class})
// @RunWith(SpringRunner.class)
public class UcBackendApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Autowired
    private UserMapper userMapper;

    @org.junit.jupiter.api.Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

}
