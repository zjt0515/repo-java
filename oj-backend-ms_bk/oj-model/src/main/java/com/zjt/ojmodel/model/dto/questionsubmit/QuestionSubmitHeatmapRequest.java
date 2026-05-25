package com.zjt.ojmodel.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交热力图查询请求
 */
@Data
public class QuestionSubmitHeatmapRequest implements Serializable {

    /**
     * 用户 id；为空时查询当前登录用户
     */
    private Long userId;

    /**
     * 开始日期，格式：yyyy-MM-dd
     */
    private String startDate;

    /**
     * 结束日期，格式：yyyy-MM-dd
     */
    private String endDate;

    private static final long serialVersionUID = 1L;
}
