package com.genshinya.boo302demo2.service.impl;

import com.genshinya.boo302demo2.mapper.UserMapper;
import com.genshinya.boo302demo2.pojo.User;
import com.genshinya.boo302demo2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }
}
