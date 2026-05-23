package com.zjt.ojquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.constant.CommonConstant;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.exception.ThrowUtils;
import com.zjt.ojcommon.utils.SqlUtils;
import com.zjt.ojmodel.model.dto.questionset.QuestionSetQueryRequest;
import com.zjt.ojmodel.model.entity.QuestionSet;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.vo.QuestionSetVO;
import com.zjt.ojmodel.model.vo.UserVO;
import com.zjt.ojquestionservice.mapper.QuestionSetMapper;
import com.zjt.ojquestionservice.service.QuestionService;
import com.zjt.ojquestionservice.service.QuestionSetService;
import com.zjt.ojserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author zz
* @description 针对表【question_set(题单)】的数据库操作Service实现
* @createDate 2026-05-10 16:03:08
*/
@Service
public class QuestionSetServiceImpl extends ServiceImpl<QuestionSetMapper, QuestionSet>
    implements QuestionSetService {
    @Resource
    private UserFeignClient userFeignClient;
    @Autowired
    private QuestionService questionService;

    /**
     * 参数校验
     * @param questionSet
     * @param add
     */
    @Override
    public void validQuestionSet(QuestionSet questionSet, boolean add) {
        // 如果问题为空，抛出异常
        if (questionSet == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = questionSet.getTitle();
        String description = questionSet.getDescription();
        String tags = questionSet.getTags();

        // 不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title,  tags), ErrorCode.PARAMS_ERROR);
        }
        // 长度校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }

    }

    @Override
    public QueryWrapper<QuestionSet> getQueryWrapper(QuestionSetQueryRequest questionSetQueryRequest) {
        QueryWrapper<QuestionSet> queryWrapper = new QueryWrapper<>();
        if (questionSetQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionSetQueryRequest.getId();
        String title = questionSetQueryRequest.getTitle();
        List<String> tags = questionSetQueryRequest.getTags();
        Long userId = questionSetQueryRequest.getUserId();
        String sortField = questionSetQueryRequest.getSortField();
        String sortOrder = questionSetQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        // todo: 可以优化掉isDelete吗
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSetVO getQuestionSetVO(QuestionSet questionSet, HttpServletRequest request) {
        QuestionSetVO questionSetVO = QuestionSetVO.poToVo(questionSet);
        // 关联查询userVO
        Long userId = questionSet.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionSetVO.setUserVO(userVO);
        // TODO 关联查询questions
        return questionSetVO;
    }

    @Override
    public Page<QuestionSetVO> getQuestionSetVOPage(Page<QuestionSet> questionSetPage, HttpServletRequest request) {
        List<QuestionSet> questionSetList = questionSetPage.getRecords();
        Page<QuestionSetVO> questionSetVOPage = new Page<>(questionSetPage.getCurrent(), questionSetPage.getSize(), questionSetPage.getTotal());
        if (CollectionUtils.isEmpty(questionSetList)) {
            return questionSetVOPage;
        }
        // 1. 关联查询UserVO
        Set<Long> userIdSet = questionSetList.stream().map(QuestionSet::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        List<QuestionSetVO> questionVOList = questionSetList.stream().map(questionSet -> {
            QuestionSetVO questionSetVO = QuestionSetVO.poToVo(questionSet);
            Long userId = questionSet.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSetVO.setUserVO(userFeignClient.getUserVO(user));
            return questionSetVO;
        }).collect(Collectors.toList());

        // 2. 关联查询List<QuestionSetItemVO>



        questionSetVOPage.setRecords(questionVOList);
        return questionSetVOPage;
    }
}




