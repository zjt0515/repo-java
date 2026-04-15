package com.tt.ucbackend.exception;

/**
 * 自定义异常处理类
 */
public class BusinessException extends RuntimeException{
    private int code;

    private String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }
}
