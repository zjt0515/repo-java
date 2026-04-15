package com.zjt.provider;

import com.zjt.common.model.User;
import com.zjt.common.service.UserService;
import com.zjt.rpc.registry.LocalRegistry;
import com.zjt.rpc.server.HttpServer;
import com.zjt.rpc.server.VertxHttpServer;

/**
 * @author genshinya
 * @time 2025-06-04 13:28:22
 * @description TODO
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
