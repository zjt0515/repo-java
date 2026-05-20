package com.zjt.ojserviceclient.service;


import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitTestRequest;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务
 */
@FeignClient(name = "oj-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);

    @PostMapping("/test")
    ExecuteCodeResponse testJudge(@RequestBody QuestionSubmitTestRequest questionSubmitTestRequest);
}
