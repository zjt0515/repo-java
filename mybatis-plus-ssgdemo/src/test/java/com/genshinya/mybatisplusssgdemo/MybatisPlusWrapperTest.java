package com.genshinya.mybatisplusssgdemo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.genshinya.mybatisplusssgdemo.mapper.UserMapper;
import com.genshinya.mybatisplusssgdemo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


@SpringBootTest
public class MybatisPlusWrapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test01() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("age", 20, 30).isNotNull("email")
                .like("name", "a");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test02() {
        // 先按照age降序排序，再按照id升序排序
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("age").orderByAsc("id");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test03() {
        // 邮箱为null的
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("email");
        int delete = userMapper.delete(queryWrapper);
        System.out.println(delete);
    }

    @Test
    public void test04() {
        // 年龄大于20并且用户名包含a 或者 邮箱为null
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", 20).like("name", "a")
                .or().isNull("email");
        // 将所有满足条件的，修改为下面的user对应设置的字段
        User user = new User();
        user.setName("小明");
        int update = userMapper.update(user, queryWrapper);
    }

    @Test
    public void test05() {
        // 年龄大于20 并且 (用户名包含a 或者 邮箱为null)
        // lambda中的条件优先执行
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", 20)
                .and(i -> i.like("name", "a").or().isNull("email"));

        // 将所有满足条件的，修改为下面的user对应设置的字段
        User user = new User();
        user.setName("小明");
        int update = userMapper.update(user, queryWrapper);
    }

    // 查询某些字段
    @Test
    public void test06() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查询指定字段
        queryWrapper.select("id", "name");
        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);
        maps.forEach(System.out::println);
    }

    // 子查询
    @Test
    public void test07() {
        // SELECT id,name,age,email,is_deleted FROM user WHERE is_deleted=0 AND (id IN (select id from user where id <= 100))
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 使用子查询查询id 小于等于100的用户信息
        queryWrapper.inSql("id", "select id from user where id <= 100");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test08() {
        // UPDATE user SET name=?,email=? WHERE is_deleted=0 AND (name LIKE ? AND (age > ? OR email IS NULL))
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.like("name", "a")
                .and(i -> i.gt("age", 20).or().isNull("email"));
        updateWrapper.set("name", "小黑").set("email", "666@qq.com");
        int update = userMapper.update(null, updateWrapper);
        System.out.println("result:" + update);
    }

    @Test
    public void test09() {
        String username = "";
        Integer ageBegin = 20;
        Integer ageEnd = 30;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 模拟开发中组装条件，即有时候需要加上某些条件，有时候某些条件又不需要
        if (StringUtils.isNotBlank(username)) {
            // 判断某个字符串不为空字符串，不为null，不为空白符
            queryWrapper.like("name", username);
        }
        if (ageBegin != null) {
            queryWrapper.ge("age", ageBegin);
        }
        if (ageEnd != null) {
            queryWrapper.le("age", ageEnd);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test10() {
        String username = "";
        Integer ageBegin = 20;
        Integer ageEnd = 30;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), "name", username);
        queryWrapper.ge(ageBegin != null, "age", ageBegin);
        queryWrapper.le(ageEnd != null, "age", ageEnd);
    }

    @Test
    public void test11() {
        String username = "";
        Integer ageBegin = 20;
        Integer ageEnd = 30;
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(username), User::getName, username);
        lambdaQueryWrapper.ge(ageBegin != null, User::getAge, ageBegin);
        lambdaQueryWrapper.le(ageEnd != null , User::getAge, ageEnd);
    }
    @Test
    public void test12(){
        // UPDATE user SET name=?,email=? WHERE is_deleted=0 AND (name LIKE ? AND (age > ? OR email IS NULL))
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.like(User::getName, "a")
                .and(i -> i.gt(User::getAge, 20).or().isNull(User::getEmail));
        updateWrapper.set(User::getName, "小黑").set(User::getEmail, "666@qq.com");
        int update = userMapper.update(null, updateWrapper);
        System.out.println("result:" + update);
    }
}
