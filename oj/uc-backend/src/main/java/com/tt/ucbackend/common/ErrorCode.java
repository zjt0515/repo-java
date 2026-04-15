package com.tt.ucbackend.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 错误码
 */
@Getter
public enum ErrorCode {
    PARAMS_ERROR(40000, "参数错误", ""),
    NULL_ERROR(40001, "数据为空", ""),
    NOT_LOGIN(40100, "你未登录", ""),
    NO_AUTH(40101, "你没资格", "");
    /**
     * 错误码
     */
    private final int code;
    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
