package com.genshinya.weblog.common.exception;

/**
 * @author genshinya
 * @description 通用异常接口
 */
public interface BaseExceptionInterface {
    /**
     * @return 异常码
     */
    String getErrorCode();

    /**
     * @return 异常信息
     */
    String getErrorMessage();
}
