package com.zjt.ojjudgeservice;


import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitTestRequest;
import com.zjt.ojmodel.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

    /**
     * 测试判题
     *
     * @param questionSubmitTestRequest
     * @return
     */
    ExecuteCodeResponse doTest(QuestionSubmitTestRequest questionSubmitTestRequest);
}
