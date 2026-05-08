package com.zjt.codingsandbox.model;

import lombok.Data;

@Data
public class ExecuteMessage {
    private int exitValue;

    private String message;

    private String errMessage;

    private Long time;

    private Long memory;
}
