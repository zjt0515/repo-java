package com.zjt.codingsandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 输出
     */
    private List<String> outputList;
    /**
     * 信息
     */
    private String message;
    /**
     * 判题状态 0 1 2
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

}
