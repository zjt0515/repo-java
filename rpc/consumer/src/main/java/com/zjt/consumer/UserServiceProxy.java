package com.zjt.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zjt.common.model.User;
import com.zjt.common.service.UserService;
import com.zjt.rpc.model.RpcRequest;
import com.zjt.rpc.model.RpcResponse;
import com.zjt.rpc.serializer.JdkSerializer;
import com.zjt.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * @author genshinya
 * @time 2025-06-04 15:50:19
 * @description 静态代理UserService
 */
public class UserServiceProxy {
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

