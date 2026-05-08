package com.zjt.oj.model.dto.questionsubmit;


import com.zjt.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 创建请求
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String language;


    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 判题状态
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}