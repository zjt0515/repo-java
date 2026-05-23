package com.zjt.ojquestionservice.controller.inner;

import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojquestionservice.service.QuestionService;
import com.zjt.ojquestionservice.service.QuestionSubmitService;
import com.zjt.ojserviceclient.service.QuestionFeignService;
import com.zjt.ojserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 内部调用 用户接口
 */
@RestController()
@RequestMapping("/inner")
public class InnerQuestionController implements QuestionFeignService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    @Override
    @PostMapping("/question/update")
    public boolean updateQuestionById(@RequestBody Question question){
        return questionService.updateById(question);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public  QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return  questionSubmitService.updateById(questionSubmit);
    }




}
