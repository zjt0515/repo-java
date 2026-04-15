package com.zjt.rpc.server;

/**
 * @author genshinya
 * @time 2025-06-04 13:38:11
 * @description web服务器接口
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    void doStart(int port);


}
