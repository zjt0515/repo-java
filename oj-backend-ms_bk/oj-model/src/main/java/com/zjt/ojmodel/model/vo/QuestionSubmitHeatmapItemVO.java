package com.zjt.ojmodel.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交热力图单日数据
 */
@Data
public class QuestionSubmitHeatmapItemVO implements Serializable {

    /**
     * 日期，格式：yyyy-MM-dd
     */
    private String date;

    /**
     * 提交次数
     */
    private Long submitCount;

    /**
     * 通过次数
     */
    private Long acceptedCount;

    private static final long serialVersionUID = 1L;
}
