package com.zjt.ojmodel.model.dto.questionset.QuestionSetItem;

import lombok.Data;

import java.io.Serializable;

/**
 * 题单题目关系
 */
@Data
public class QuestionSetItemAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目在题单中的排序；为空时由后端追加到末尾
     */
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}