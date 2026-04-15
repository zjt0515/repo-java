package com.genshinya.mybatisplusssgdemo;

import com.genshinya.mybatisplusssgdemo.mapper.UserMapper;
import com.genshinya.mybatisplusssgdemo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MyBatisPlusTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectList(){
        // 通过条件构造器查询一个list集合，没有条件的话可以设置参数null
        List<User> list = userMapper.selectList(null);
        list.forEach(System.out::println);
    }
    @Test
    public void testInsert() {
        // 实现新增用户信息
        // insert into user (id, name, age, email) values (?,?,?,?)
        User user = new User();
        user.setName("张三");
        user.setAge(23);
        user.setEmail("zhangsan@qq.com");
        int result = userMapper.insert(user);
        System.out.println("result:" + result);
        // 获取主键
        System.out.println("id:" + user.getId());
    }
    @Test
    public void testDelete(){
        // 通过id删除用户信息
        // delete from user where id = ?
        int result1 = userMapper.deleteById(1779360182179520513L);
        System.out.println("result:" + result1);
        // 根据map删除，map中放的键值对就是删除的条件
        Map<String, Object> map = new HashMap<>();
        map.put("age", 23);
        map.put("name", "张三");
        int result2 = userMapper.deleteByMap(map);
        System.out.println("result2:" + result2);
        // 根据id批量删除，使用List存放多个id数据
        List<Long> list = Arrays.asList(1L, 2L, 3L);
        int result3 = userMapper.deleteBatchIds(list);
        System.out.println("result3:" + result3);
    }
    @Test
    public void testUpdate(){
        // 根据id修改用户信息，设置id后再设置需要修改的字段
        User user = new User();
        user.setId(4L);
        user.setName("李四");
        int result = userMapper.updateById(user);
        System.out.println("result:" + result);
    }
    @Test
    public void testSelect(){
        // 根据id查询用户信息
        User user = userMapper.selectById(1L);
        System.out.println(user);

        // 根据idList批量查询，相当于select ... where id in
        List<Long> list = Arrays.asList(1L, 2L, 3L);
        List<User> users = userMapper.selectBatchIds(list);
        users.forEach(System.out::println);

        // 根据map集合查询
        Map<String, Object> map = new HashMap<>();
        map.put("age", 18);
        List<User> users1 = userMapper.selectByMap(map);
        users1.forEach(System.out::println);

        // 根据条件构造器查询
        // 没有条件就是null即所有数据
        List<User> users2 = userMapper.selectList(null);
        users2.forEach(System.out::println);

        // 自定义
        Map<String, Object> map1 = userMapper.selectMapById(1L);
        System.out.println(map1);

    }
}
