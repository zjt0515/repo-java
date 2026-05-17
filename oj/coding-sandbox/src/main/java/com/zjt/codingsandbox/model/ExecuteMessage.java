package com.zjt.codingsandbox.model;

import lombok.Data;

@Data
public class ExecuteMessage {
    private int exitValue;
    // 标准输出
    private String message;
    // 错误输出
    private String errMessage;

    private String JudgeInfoMessage;

    private Long time;

    private Long memory;
}
