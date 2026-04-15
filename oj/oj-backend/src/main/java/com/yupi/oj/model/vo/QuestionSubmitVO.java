package com.yupi.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupi.oj.model.dto.question.JudgeConfig;
import com.yupi.oj.model.dto.questionsubmit.JudgeInfo;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.enums.SubmitStatusEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.json.Json;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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