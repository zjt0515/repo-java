package com.zjt.provider;

import com.zjt.common.model.User;
import com.zjt.common.service.UserService;

/**
 * @author genshinya
 * @time 2025-06-04 13:27:32
 * @description UserService实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名: " + user.getName());
        return user;
    }
}
