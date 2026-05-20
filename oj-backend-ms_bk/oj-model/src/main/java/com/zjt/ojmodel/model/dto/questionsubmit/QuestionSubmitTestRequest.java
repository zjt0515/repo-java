package com.zjt.ojmodel.model.dto.questionsubmit;


import com.zjt.ojmodel.model.dto.question.JudgeCase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 测试代码请求
 */
@Data
public class QuestionSubmitTestRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题用例(json 数组)
     */
    private List<String> judgeInputCase;

    private static final long serialVersionUID = 1L;
}