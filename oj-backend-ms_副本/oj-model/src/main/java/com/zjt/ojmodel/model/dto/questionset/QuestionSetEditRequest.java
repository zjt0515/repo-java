package com.zjt.ojmodel.model.dto.questionset;


import com.zjt.ojmodel.model.dto.questionset.QuestionSetItem.QuestionSetItemEditRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * Post /question-set/edit
 */
@Data
public class QuestionSetEditRequest implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 题单内题目列表；传 null 表示不调整题目关系，传空数组表示清空题单题目
     */
    private List<QuestionSetItemEditRequest> questionSetItemList;

    private static final long serialVersionUID = 1L;
}
