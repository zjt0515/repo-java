package com.tt.ucbackend.common;

/**
 * 返回工具类
 * 用于返回规范的BaseResponse
 */
public class ResultUtils {
    /**
     * 返回通用成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok", "wdf");
    }

    /**
     * 返回通用失败
     * @param errorCode
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
}
