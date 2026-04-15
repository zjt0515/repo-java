package com.tt.ucbackend.service;

import com.tt.ucbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author genshinya
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-09-10 18:58:34
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String studentNumber);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求
     * @return 用户id？
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);

    User getSafeUser(User user);
}
