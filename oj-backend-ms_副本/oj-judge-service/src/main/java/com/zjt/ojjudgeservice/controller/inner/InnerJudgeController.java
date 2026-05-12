package com.zjt.ojjudgeservice.controller.inner;

import com.zjt.ojjudgeservice.JudgeService;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojserviceclient.service.JudgeFeignService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 内部调用 用户接口
 */
@RestController
@RequestMapping("/inner")
public class InnerJudgeController implements JudgeFeignService {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public  QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}
