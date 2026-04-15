package com.genshinya.weblog.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: JSON 工具类
 */
@Slf4j
public class JsonUtil {
    // 创建了一个静态的 ObjectMapper 对象
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    // 用于将传入的对象打印成 JSON 字符串
    public static String toJsonString(Object obj) {
        try {
            return INSTANCE.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }
}
