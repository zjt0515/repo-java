package com.zjt.ojmodel.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题单题目关系
 * @TableName question_set_item
 */
@TableName(value ="question_set_item")
@Data
public class QuestionSetItemVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题单 id
     */
    private Long questionSetId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目信息 *
     */
    private QuestionVO questionVO;

    /**
     * 题目在题单中的排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}