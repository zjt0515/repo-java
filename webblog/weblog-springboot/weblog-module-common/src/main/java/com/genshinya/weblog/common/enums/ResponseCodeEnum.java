package com.genshinya.weblog.common.enums;

import com.genshinya.weblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义异常枚举
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {
    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台正在努力修复中..."),
    /**
     * 参数错误
     */
    PARAM_NOT_VALID("10001", "参数错误"),
    // ----------- 业务异常状态码 -----------
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    PRODUCT_NOT_FOUND("20002", "该产品不存在（测试使用）"),
    CATEGORY_NAME_EXISTED("20003", "文章分类名称已存在，请勿重复添加！"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    ;

    /**
     * 异常码
     */
    private final String errorCode;
    /**
     * 异常信息
     */
    private final String errorMessage;
}
