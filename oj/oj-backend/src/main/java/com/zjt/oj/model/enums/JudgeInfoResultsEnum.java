package com.zjt.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 */
public enum JudgeInfoResultsEnum {

    ACCEPTED("ACCEPTED", "通过"),
    WRONG_ANSWER("Wrong Answer", "答案错误"),
    COMPILE_ERROR("Compile Error", "编译错误"),
    TIME_LIMIT_EXCEEDED("TIME_LIMIT_EXCEEDED", "超时"),
    MEMORY_LIMIT_EXCEED("Memory Limit Exceed", "内存溢出"),
    RUNTIME_ERROR("Runtime Error", "运行错误"),
    UNKNOWN_ERROR("Unknown Error", "未知错误"),
    SYSTEM_ERROR("System Error", "系统错误"),
    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded","输出溢出"),
    WAITING("Waiting","等待中");


    private final String text;

    private final String value;

    JudgeInfoResultsEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoResultsEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoResultsEnum anEnum : JudgeInfoResultsEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
