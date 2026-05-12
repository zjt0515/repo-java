package com.zjt.ojquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetQueryRequest;
import com.zjt.ojmodel.model.vo.QuestionSetVO;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import com.zjt.ojmodel.model.entity.QuestionSet;

import javax.servlet.http.HttpServletRequest;

/**
* @author zz
* @description 针对表【question_set(题单)】的数据库操作Service
* @createDate 2026-05-10 16:03:08
*/
public interface QuestionSetService extends IService<QuestionSet> {
    /**
     * 校验
     *
     * @param
     * @param add
     */
    void validQuestionSet(QuestionSet questionSet, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionSetQueryRequest
     * @return
     */
    QueryWrapper<QuestionSet> getQueryWrapper(QuestionSetQueryRequest questionSetQueryRequest);

    /**
     * 获取entityVO
     *
     * @param questionSet
     * @param request
     * @return
     */
    QuestionSetVO getQuestionSetVO(QuestionSet questionSet, HttpServletRequest request);

    /**
     * 分页获取entityVO
     *
     * @param questionSetPage
     * @param request
     * @return
     */
    Page<QuestionSetVO> getQuestionSetVOPage(Page<QuestionSet> questionSetPage, HttpServletRequest request);

}
