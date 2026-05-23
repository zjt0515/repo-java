package com.zjt.ojquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.zjt.ojcommon.annotation.AuthCheck;
import com.zjt.ojcommon.common.BaseResponse;
import com.zjt.ojcommon.common.DeleteRequest;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.common.ResultUtils;
import com.zjt.ojcommon.constant.UserConstant;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.exception.ThrowUtils;
import com.zjt.ojmodel.model.dto.question.JudgeCase;
import com.zjt.ojmodel.model.dto.question.JudgeConfig;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetAddRequest;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetEditRequest;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetItem.QuestionSetItemAddRequest;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetQueryRequest;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetUpdateRequest;
import com.zjt.ojmodel.model.entity.QuestionSet;
import com.zjt.ojmodel.model.entity.QuestionSetItem;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.vo.QuestionSetVO;
import com.zjt.ojquestionservice.service.QuestionSetItemService;
import com.zjt.ojquestionservice.service.QuestionSetService;
import com.zjt.ojserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题单接口
 *
 */
@RestController
@RequestMapping("/questionSet-set")
@Slf4j
public class QuestionSetController {

    @Resource
    private QuestionSetService questionSetService;

    @Resource
    private UserFeignClient userFeignClient;

    private final static Gson GSON = new Gson();
    @Autowired
    private QuestionSetItemService questionSetItemService;

    /**
     * 创建题单
     * @param questionSetAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionSet(@RequestBody QuestionSetAddRequest questionSetAddRequest, HttpServletRequest request) {
        if (questionSetAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSet questionSet = new QuestionSet();


        BeanUtils.copyProperties(questionSetAddRequest, questionSet);
        List<String> tags = questionSetAddRequest.getTags();
        if (tags != null) {
            questionSet.setTags(GSON.toJson(tags));
        }


        questionSetService.validQuestionSet(questionSet, true);
        User loginUser = userFeignClient.getLoginUser(request);
        questionSet.setUserId(loginUser.getId());
        questionSet.setQuestionNum(questionSetItemList.size());
        questionSet.setFavourNum(0);
        boolean result = questionSetService.save(questionSet);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionSet.getId());
    }

    /**
     * 删除题单
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionSet(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionSet questionSet = questionSetService.getById(id);
        ThrowUtils.throwIf(questionSet == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!questionSet.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionSetService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新题单（仅管理员）
     *
     * @param questionSetUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionSet(@RequestBody QuestionSetUpdateRequest questionSetUpdateRequest) {
        if (questionSetUpdateRequest == null || questionSetUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据id查询entity, 看是否存在
        Long id = questionSetUpdateRequest.getId();
        QuestionSet oldQuestionSet = questionSetService.getById(id);
        ThrowUtils.throwIf(oldQuestionSet == null, ErrorCode.NOT_FOUND_ERROR);

        // build new questionSet
        QuestionSet questionSet = new QuestionSet();
        BeanUtils.copyProperties(questionSetUpdateRequest, questionSet);
        // tags
        List<String> tags = questionSetUpdateRequest.getTags();
        if (tags != null) {
            questionSet.setTags(GSON.toJson(tags));
        }

        // 参数校验
        questionSetService.validQuestionSet(questionSet, false);

        // update
        boolean result = questionSetService.updateById(questionSet);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取 (不脱敏)
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionSet> getQuestionSetById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSet questionSet = questionSetService.getById(id);
        if (questionSet == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 鉴权
        User loginUser = userFeignClient.getLoginUser(request);
        if (!loginUser.getUserRole().equals("admin") && !questionSet.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非上传人或管理员不可获取所有数据");
        }
        return ResultUtils.success(questionSet);
    }

    /**
     * 根据 id 获取VO (脱敏)
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionSetVO> getQuestionSetVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSet questionSet = questionSetService.getById(id);
        if (questionSet == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionSetService.getQuestionSetVO(questionSet, request));
    }

    /**
     * 分页获取题单VO列表
     *
     * @param questionSetQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSetVO>> listQuestionSetVOByPage(@RequestBody QuestionSetQueryRequest questionSetQueryRequest,
                                                                     HttpServletRequest request) {
        long current = questionSetQueryRequest.getCurrent();
        long size = questionSetQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<QuestionSet> questionSetPage = questionSetService.page(new Page<>(current, size),
                questionSetService.getQueryWrapper(questionSetQueryRequest));
        return ResultUtils.success(questionSetService.getQuestionSetVOPage(questionSetPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionSetQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionSetVO>> listMyQuestionSetVOByPage(@RequestBody QuestionSetQueryRequest questionSetQueryRequest,
                                                                       HttpServletRequest request) {
        if (questionSetQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionSetQueryRequest.setUserId(loginUser.getId());
        long current = questionSetQueryRequest.getCurrent();
        long size = questionSetQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSet> questionSetPage = questionSetService.page(new Page<>(current, size),
                questionSetService.getQueryWrapper(questionSetQueryRequest));
        return ResultUtils.success(questionSetService.getQuestionSetVOPage(questionSetPage, request));
    }

    /**
     * 分页获取题单列表（管理员）
     *
     * @param questionSetQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSet>> listQuestionSetByPage(@RequestBody QuestionSetQueryRequest questionSetQueryRequest,
                                                                 HttpServletRequest request) {
        long current = questionSetQueryRequest.getCurrent();
        long size = questionSetQueryRequest.getPageSize();
        Page<QuestionSet> questionSetPage = questionSetService.page(new Page<>(current, size),
                questionSetService.getQueryWrapper(questionSetQueryRequest));
        return ResultUtils.success(questionSetPage);
    }

    /**
     * 编辑（上传人或者管理员）
     *
     * @param questionSetEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionSet(@RequestBody QuestionSetEditRequest questionSetEditRequest, HttpServletRequest request) {
    }

}
