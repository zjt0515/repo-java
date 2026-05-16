package com.zjt.ojmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目封装类
 *
 * @TableName question
 */
@Data
public class QuestionSubmitRawVO implements Serializable {
    /**
     * 提交id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息(json对象)
     */
    private String judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 提交用户信息
     */
    private UserVO userVO;

    /**
     *  题目信息
     */
    private QuestionVO questionVO;

    private static final long serialVersionUID = 1L;
}