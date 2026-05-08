package com.zjt.oj.judge;

import com.zjt.oj.model.vo.QuestionVO;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionVO doJudge(long questionSubmitId);
}
