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
public class ExecuteCodeRequest {
    /**
     * 输入
     */
    private List<String> inputList;
    /**
     * 语言
     */
    private String language;
    /**
     * 代码
     */
    private String code;

}
