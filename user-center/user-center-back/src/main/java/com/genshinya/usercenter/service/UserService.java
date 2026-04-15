package com.genshinya.usercenter.service;

import com.genshinya.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author genshinya
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-05-03 22:27:52
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount    账号
     * @param userPassword  密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request
     * @return 脱敏后的User
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user 用户
     * @return 脱敏用户
     */
    User getSafetyUser(User user);

    /**
     * 用户退出
     *
     * @param user
     * @param request
     * @return
     */
    int userLogout( HttpServletRequest request);
}
