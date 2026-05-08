package com.zjt.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjt.oj.common.ErrorCode;
import com.zjt.oj.constant.CommonConstant;
import com.zjt.oj.exception.BusinessException;
import com.zjt.oj.mapper.QuestionSubmitMapper;
import com.zjt.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zjt.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zjt.oj.model.entity.Question;
import com.zjt.oj.model.entity.QuestionSubmit;
import com.zjt.oj.model.entity.User;
import com.zjt.oj.model.enums.SubmitLangEnum;
import com.zjt.oj.model.enums.SubmitStatusEnum;
import com.zjt.oj.model.vo.QuestionSubmitVO;
import com.zjt.oj.service.QuestionService;
import com.zjt.oj.service.QuestionSubmitService;
import com.zjt.oj.service.UserService;
import com.zjt.oj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author genshinya
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-12-13 14:14:59
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交请求入参
     * @param loginUser                用户
     * @return long 提交id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() == null
                || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目提交参数错误");
        }
        // 校验编程语言
        String language = questionSubmitAddRequest.getLanguage();
        SubmitLangEnum langEnum = SubmitLangEnum.getEnumByValue(language);
        if (langEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        String code = questionSubmitAddRequest.getCode();
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交代码不能为空");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();

        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        // todo：限流方式限制用户同时提交多条重复题目
        // 先直接插入数据
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        // 设置初始状态
        questionSubmit.setStatus(SubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);

        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // todo:调用代码沙箱判题
        return questionSubmit.getId();
    }

    /**
     * 工具方法
     * 获取查询提交包装类的QW (用户根据查询字段，根据前端传来的请求对象，得到对应的QueryWrapper)
     * 查询字段：题目id | userid | 运行结果 | 编程语言
     *
     * @param questionSubmitQueryRequest 请求问题
     * @return QueryWrapper<QuestionSubmit>
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long userId = questionSubmitQueryRequest.getUserId();
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Integer status = questionSubmitQueryRequest.getStatus();

        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        // 如果用户传入非法状态，则是包括所有状态
        queryWrapper.eq(SubmitStatusEnum.getEnumByValue(status) != null, "status", status);

        queryWrapper.eq("is_delete", 0);
        // 拼接查询顺序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 脱敏类、封装类 submit->submitVO
     * 优化：减少多次查询，例如减少查询登录用户，直接拿接受外层传进来的loginuser
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 先转成VO类
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏
        long id = loginUser.getId();
        if (id != questionSubmit.getUserId() && userService.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}



