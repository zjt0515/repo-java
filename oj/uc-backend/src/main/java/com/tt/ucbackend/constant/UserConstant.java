package com.tt.ucbackend.constant;

/**
 * 用户常量 0 -
 */
public interface UserConstant {
    /**
     * session登录态
     */
    String USER_LOGIN_STATE = "userLoginStatus";

    /**
     * 用户权限，普通用户
     */
    int DEFAULT_ROLE = 0;

    /**
     * 用户权限，管理员
     */
    int ADMIN_ROLE = 1;
}
