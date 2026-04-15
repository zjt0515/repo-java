package com.genshinya.boo302demo2.controller;

import com.genshinya.boo302demo2.pojo.User;
import com.genshinya.boo302demo2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping("/findById")
    public User findById(Integer id) {
        return userService.findById(id);
    }
}
