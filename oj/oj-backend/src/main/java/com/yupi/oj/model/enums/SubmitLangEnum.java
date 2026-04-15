package com.yupi.oj.model.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交编程语言枚举
 */
public enum SubmitLangEnum {
    JAVA("java", "java"),
    CPP("cpp", "cpp"),
    ;


    private final String lang;
    private final String value;

    SubmitLangEnum(String lang, String value) {
        this.lang = lang;
        this.value = value;
    }

    /**
     * 根据value返回Enum对象
     *
     * @param value
     * @return
     */
    public static SubmitLangEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SubmitLangEnum anEnum : values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取所有value值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getLang() {
        return lang;
    }

    public String getValue() {
        return value;
    }
}
