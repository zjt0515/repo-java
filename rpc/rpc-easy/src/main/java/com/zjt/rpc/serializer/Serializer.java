package com.zjt.rpc.serializer;

import java.io.IOException;

/**
 * @author genshinya
 * @time 2025-06-04 14:35:35
 * @description TODO
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}

