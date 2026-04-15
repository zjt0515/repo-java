package com.zjt.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author genshinya
 * @time 2025-06-04 15:54:54
 * @description TODO
 */
public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
