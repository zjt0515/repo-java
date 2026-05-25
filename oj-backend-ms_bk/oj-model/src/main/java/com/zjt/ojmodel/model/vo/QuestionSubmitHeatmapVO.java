package com.zjt.ojmodel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 题目提交热力图数据
 */
@Data
public class QuestionSubmitHeatmapVO implements Serializable {

    /**
     * 开始日期，格式：yyyy-MM-dd
     */
    private String startDate;

    /**
     * 结束日期，格式：yyyy-MM-dd
     */
    private String endDate;

    /**
     * 统计时区
     */
    private String timezone;

    /**
     * 总提交次数
     */
    private Long totalCount;

    /**
     * 单日最大提交次数
     */
    private Long maxCount;

    /**
     * 每日数据
     */
    private List<QuestionSubmitHeatmapItemVO> items;

    private static final long serialVersionUID = 1L;
}
