package com.genshinya.weblog.common.utils;

import com.genshinya.weblog.common.exception.BaseExceptionInterface;
import com.genshinya.weblog.common.exception.BizException;
import lombok.Data;

import java.io.Serializable;

@Data
public class Response<T> implements Serializable {
    // 是否成功，默认为 true
    private boolean success = true;
    // 响应消息
    private String message;
    // 异常码
    private String errorCode;
    // 响应数据
    private T data;

    /**
     * 成功响应
     * @return
     * @param <T>
     */
    public static <T> Response<T> success() {
        Response<T> response = new Response<>();
        return response;
    }

    /**
     * 包含数据的成功响应
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail() {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        return response;
    }

    /**
     * 仅包含错误信息的失败响应
     * @param errorMessage
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(String errorMessage) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response;
    }

    /**
     * 包含错误码和错误消息的失败响应
     * @param errorCode
     * @param errorMessage
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(String errorCode, String errorMessage) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }

    /**
     * 根据业务异常 返回对应的响应
     * @param bizException
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(BizException bizException) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorCode(bizException.getErrorCode());
        response.setMessage(bizException.getErrorMessage());
        return response;
    }

    /**
     * 接受自定义异常类的失败响应
     * @param baseExceptionInterface 可以传入ResponseCodeEnum
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(BaseExceptionInterface baseExceptionInterface) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorCode(baseExceptionInterface.getErrorCode());
        response.setMessage(baseExceptionInterface.getErrorMessage());
        return response;
    }
}
