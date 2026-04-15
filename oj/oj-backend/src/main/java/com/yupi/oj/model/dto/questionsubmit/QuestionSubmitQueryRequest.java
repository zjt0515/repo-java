package com.yupi.oj.model.dto.questionsubmit;


import com.yupi.oj.common.PageRequest;
import com.yupi.oj.model.enums.SubmitStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String language;


    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 判题状态
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}