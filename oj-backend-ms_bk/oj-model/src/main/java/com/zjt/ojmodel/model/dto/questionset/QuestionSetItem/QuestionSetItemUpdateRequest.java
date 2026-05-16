package com.zjt.ojmodel.model.dto.questionset.QuestionSetItem;

import lombok.Data;

import java.io.Serializable;

/**
 * 题单题目关系
 */
@Data
public class QuestionSetItemUpdateRequest implements Serializable {

    /**
     * 关系 id，已有关系可传
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目在题单中的排序
     */
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}