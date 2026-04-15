package com.genshinya.mybatisplusssgdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genshinya.mybatisplusssgdemo.mapper.UserMapper;
import com.genshinya.mybatisplusssgdemo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyBatisPlusPluginsTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(2,3);
        userMapper.selectPage(page, null);
        System.out.println(page.getRecords());
    }

    @Test
    public void selectPageVo(){
        Page<User> page = new Page(1,3);
        userMapper.selectPageVo(page,20);
        System.out.println(page);
    }
}
