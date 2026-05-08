package com.zjt.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjt.oj.common.BaseResponse;
import com.zjt.oj.common.ErrorCode;
import com.zjt.oj.common.ResultUtils;
import com.zjt.oj.exception.BusinessException;
import com.zjt.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zjt.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zjt.oj.model.entity.QuestionSubmit;
import com.zjt.oj.model.entity.User;
import com.zjt.oj.model.vo.QuestionSubmitVO;
import com.zjt.oj.service.QuestionSubmitService;
import com.zjt.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    //@Resource
    //private QuestionSubmitService questionSubmitService;
    //
    //@Resource
    //private UserService userService;
    //
    ///**
    // * 提交题目
    // *
    // * @param questionSubmitAddRequest 题目提交入参
    // * @param request
    // * @return 题目提交id
    // */
    //@PostMapping("/")
    //public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
    //        HttpServletRequest request) {
    //    // 校验
    //    if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //    }
    //    // 调用 service
    //    final User loginUser = userService.getLoginUser(request);
    //    long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
    //    return ResultUtils.success(questionSubmitId);
    //}
    //
    ///**
    // * 分页获取提交列表（管理员 | 用户只能看到公开信息）
    // *
    // * @param questionSubmitQueryRequest
    // * @param request
    // * @return
    // */
    //@PostMapping("/list/page")
    //public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
    //                                                                     HttpServletRequest request) {
    //    long current = questionSubmitQueryRequest.getCurrent();
    //    long size = questionSubmitQueryRequest.getPageSize();
    //    // 从数据库中查询原始 QuestionSubmit
    //    Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
    //            questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
    //    final User loginUser = userService.getLoginUser(request);
    //    // 返回脱敏信息
    //    return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    //}

}
