package com.genshinya.mybatisplusssgdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genshinya.mybatisplusssgdemo.mapper.UserMapper;
import com.genshinya.mybatisplusssgdemo.pojo.User;
import com.genshinya.mybatisplusssgdemo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
