package com.zjt.ojquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitHeatmapRequest;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.vo.QuestionSubmitHeatmapVO;
import com.zjt.ojmodel.model.vo.QuestionSubmitVO;

/**
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交请求
     * @param loginUser                提交用户
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取提交封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取提交封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 获取提交热力图数据
     *
     * @param questionSubmitHeatmapRequest 查询请求
     * @param loginUser                    当前登录用户
     * @return
     */
    QuestionSubmitHeatmapVO getQuestionSubmitHeatmap(QuestionSubmitHeatmapRequest questionSubmitHeatmapRequest,
                                                     User loginUser);

}
