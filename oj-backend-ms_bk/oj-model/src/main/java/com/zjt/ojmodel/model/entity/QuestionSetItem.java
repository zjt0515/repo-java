package com.zjt.ojmodel.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题单题目关系
 * @TableName question_set_item
 */
@TableName(value ="question_set_item")
@Data
public class QuestionSetItem  implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}