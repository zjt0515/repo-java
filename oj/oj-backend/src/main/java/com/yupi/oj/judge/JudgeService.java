package com.yupi.oj.judge;

import com.yupi.oj.model.vo.QuestionVO;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionId
     * @return
     */
    QuestionVO doJudge(long questionSubmitId);
}
