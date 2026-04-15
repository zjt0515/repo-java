package com.yupi.oj.model.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SubmitStatusEnum {
    // 0 - 待判题 1 - 判题中 2 - 成功 3 -失败
    // 后端判题状态使用Integer
    WAITING("待判题", 0),
    JUDGING("判题中", 1),
    SUCCESS("成功", 2),
    FAILURE("失败", 3);

    private final String text;
    private final Integer value;

    SubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value返回Enum对象
     *
     * @param value
     * @return
     */
    public static SubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SubmitStatusEnum anEnum : values()) {
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
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}
