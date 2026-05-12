package com.zjt.ojmodel.model.dto.questionset;

import com.zjt.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 题单
 * @TableName question_set
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSetQueryRequest extends PageRequest  implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题单标题
     */
    private String title;

    /**
     * 标签列表 JSON 数组
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
