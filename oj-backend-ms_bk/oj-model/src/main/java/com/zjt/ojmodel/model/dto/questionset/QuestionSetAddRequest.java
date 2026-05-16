package com.zjt.ojmodel.model.dto.questionset;


import com.zjt.ojmodel.model.dto.questionset.QuestionSetItem.QuestionSetItemAddRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * Post /question-set/add
 */
@Data
public class QuestionSetAddRequest implements Serializable {

    /**
     * 题单标题
     */
    private String title;

    /**
     * 题单描述
     */
    private String description;

    /**
     * 标签列表 JSON 数组
     */
    private List<String> tags;

    /**
     * 题单内题目列表
     */
    private List<QuestionSetItemAddRequest> questionSetItemList;

    private static final long serialVersionUID = 1L;
}
