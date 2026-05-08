package com.zjt.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.zjt.oj.judge.codesandbox.model.JudgeInfo;
import com.zjt.oj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 题目封装类
 *
 * @TableName question
 */
@Data
public class QuestionSubmitVO implements Serializable {
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
    private JudgeInfo judgeInfo;

    /**
     * 判题状态(0,1,2,3等待判题，判题中)
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     *  提交者信息
     */
    private UserVO userVO;

    /**
     *  题目信息
     */
    private QuestionVO questionVO;



    /**
     * O -> VO，包装类转对象
     * O:
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);

        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        // 对象转成jsonStr，需要先判断对象是否为null
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类( O转VO
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        // 首先将所有属性复制
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfo = questionSubmit.getJudgeInfo();
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo, JudgeInfo.class));
        return questionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}