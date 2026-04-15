package com.zjt.consumer;

import com.zjt.common.model.User;
import com.zjt.common.service.UserService;
import com.zjt.rpc.proxy.ServiceProxyFactory;

/**
 * @author genshinya
 * @time 2025-06-04 13:31:20
 * @description TODO
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        // 获取 UserService 的实现类对象
        // UserService userService = new UserServiceProxy();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("zjt");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
