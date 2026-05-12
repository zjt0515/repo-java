package com.zjt.ojquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.exception.ThrowUtils;
import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.entity.QuestionSet;
import com.zjt.ojmodel.model.entity.QuestionSetItem;
import com.zjt.ojmodel.model.vo.QuestionSetItemVO;
import com.zjt.ojquestionservice.mapper.QuestionSetItemMapper;
import com.zjt.ojquestionservice.service.QuestionService;
import com.zjt.ojquestionservice.service.QuestionSetItemService;
import com.zjt.ojquestionservice.service.QuestionSetService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author zz
* @description 针对表【question_set_item(题单题目关系)】的数据库操作Service实现
* @createDate 2026-05-11 01:16:12
*/
@Service
public class QuestionSetItemServiceImpl extends ServiceImpl<QuestionSetItemMapper, QuestionSetItem>
    implements QuestionSetItemService {

    @Resource
    private QuestionSetService questionSetService;

    @Resource
    private QuestionService questionService;

    @Override
    public void validQuestionSetItem(QuestionSetItem questionSetItem, boolean add) {
        if (questionSetItem == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionSetId = questionSetItem.getQuestionSetId();
        Long questionId = questionSetItem.getQuestionId();
        Integer sortOrder = questionSetItem.getSortOrder();

        if (add) {
            ThrowUtils.throwIf(questionSetId == null || questionSetId <= 0, ErrorCode.PARAMS_ERROR, "题单 id 非法");
            ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR, "题目 id 非法");
        }
        if (questionSetId != null && questionSetId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题单 id 非法");
        }
        if (questionId != null && questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目 id 非法");
        }
        if (sortOrder != null && sortOrder < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序值不能小于 0");
        }
    }

    @Override
    public QuestionSetItem getQuestionSetItem(Long questionSetId, Long questionId) {
        QuestionSetItem questionSetItem = new QuestionSetItem();
        questionSetItem.setQuestionSetId(questionSetId);
        questionSetItem.setQuestionId(questionId);
        validQuestionSetItem(questionSetItem, true);

        QueryWrapper<QuestionSetItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_set_id", questionSetId);
        queryWrapper.eq("question_id", questionId);
        queryWrapper.eq("is_delete", 0);
        return this.getOne(queryWrapper, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addQuestionSetItem(Long questionSetId, Long questionId, Integer sortOrder) {
        QuestionSetItem questionSetItem = new QuestionSetItem();
        questionSetItem.setQuestionSetId(questionSetId);
        questionSetItem.setQuestionId(questionId);
        questionSetItem.setSortOrder(sortOrder);
        validQuestionSetItem(questionSetItem, true);

        checkQuestionSetExists(questionSetId);
        checkQuestionExists(questionId);

        QuestionSetItem oldQuestionSetItem = getQuestionSetItem(questionSetId, questionId);
        ThrowUtils.throwIf(oldQuestionSetItem != null, ErrorCode.OPERATION_ERROR, "题目已存在于题单中");

        if (sortOrder == null) {
            questionSetItem.setSortOrder(getNextSortOrder(questionSetId));
        }
        try {
            boolean result = this.save(questionSetItem);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于题单中");
        }
        refreshQuestionSetQuestionNum(questionSetId);
        return questionSetItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeQuestionSetItem(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "关系 id 非法");
        QuestionSetItem oldQuestionSetItem = this.getById(id);
        ThrowUtils.throwIf(oldQuestionSetItem == null, ErrorCode.NOT_FOUND_ERROR, "题单中不存在该题目");

        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        refreshQuestionSetQuestionNum(oldQuestionSetItem.getQuestionSetId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeQuestionSetItem(Long questionSetId, Long questionId) {
        QuestionSetItem questionSetItem = new QuestionSetItem();
        questionSetItem.setQuestionSetId(questionSetId);
        questionSetItem.setQuestionId(questionId);
        validQuestionSetItem(questionSetItem, true);

        checkQuestionSetExists(questionSetId);
        QuestionSetItem oldQuestionSetItem = getQuestionSetItem(questionSetId, questionId);
        ThrowUtils.throwIf(oldQuestionSetItem == null, ErrorCode.NOT_FOUND_ERROR, "题单中不存在该题目");

        return removeQuestionSetItem(oldQuestionSetItem.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestionSetItemSort(Long id, Integer sortOrder) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "关系 id 非法");
        ThrowUtils.throwIf(sortOrder == null, ErrorCode.PARAMS_ERROR, "排序值不能为空");
        ThrowUtils.throwIf(sortOrder < 0, ErrorCode.PARAMS_ERROR, "排序值不能小于 0");

        QuestionSetItem oldQuestionSetItem = this.getById(id);
        ThrowUtils.throwIf(oldQuestionSetItem == null, ErrorCode.NOT_FOUND_ERROR, "题单中不存在该题目");

        QuestionSetItem updateQuestionSetItem = new QuestionSetItem();
        updateQuestionSetItem.setId(id);
        updateQuestionSetItem.setSortOrder(sortOrder);
        boolean result = this.updateById(updateQuestionSetItem);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestionSetItemSort(Long questionSetId, Long questionId, Integer sortOrder) {
        QuestionSetItem questionSetItem = new QuestionSetItem();
        questionSetItem.setQuestionSetId(questionSetId);
        questionSetItem.setQuestionId(questionId);
        questionSetItem.setSortOrder(sortOrder);
        validQuestionSetItem(questionSetItem, true);
        ThrowUtils.throwIf(sortOrder == null, ErrorCode.PARAMS_ERROR, "排序值不能为空");

        checkQuestionSetExists(questionSetId);
        QuestionSetItem oldQuestionSetItem = getQuestionSetItem(questionSetId, questionId);
        ThrowUtils.throwIf(oldQuestionSetItem == null, ErrorCode.NOT_FOUND_ERROR, "题单中不存在该题目");

        return updateQuestionSetItemSort(oldQuestionSetItem.getId(), sortOrder);
    }

    @Override
    public List<QuestionSetItemVO> listQuestionSetItemVOByQuestionSetId(Long questionSetId,
                                                                        HttpServletRequest request) {
        ThrowUtils.throwIf(questionSetId == null || questionSetId <= 0, ErrorCode.PARAMS_ERROR, "题单 id 非法");
        Map<Long, List<QuestionSetItemVO>> questionSetItemVOMap =
                listQuestionSetItemVOMap(Collections.singletonList(questionSetId), request);
        return questionSetItemVOMap.getOrDefault(questionSetId, Collections.emptyList());
    }

    @Override
    public Map<Long, List<QuestionSetItemVO>> listQuestionSetItemVOMap(Collection<Long> questionSetIds,
                                                                       HttpServletRequest request) {
        if (CollectionUtils.isEmpty(questionSetIds)) {
            return Collections.emptyMap();
        }
        List<Long> validQuestionSetIds = questionSetIds.stream()
                .filter(questionSetId -> questionSetId != null && questionSetId > 0)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validQuestionSetIds)) {
            return Collections.emptyMap();
        }

        QueryWrapper<QuestionSetItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("question_set_id", validQuestionSetIds);
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByAsc("question_set_id", "sort_order", "create_time", "id");
        List<QuestionSetItem> questionSetItemList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(questionSetItemList)) {
            return buildEmptyResultMap(validQuestionSetIds);
        }

        Set<Long> questionIdSet = questionSetItemList.stream()
                .map(QuestionSetItem::getQuestionId)
                .filter(questionId -> questionId != null && questionId > 0)
                .collect(Collectors.toSet());
        Map<Long, Question> questionIdQuestionMap = getQuestionIdQuestionMap(questionIdSet);

        Map<Long, List<QuestionSetItemVO>> resultMap = questionSetItemList.stream()
                .map(questionSetItem -> getQuestionSetItemVO(questionSetItem, questionIdQuestionMap, request))
                .collect(Collectors.groupingBy(QuestionSetItemVO::getQuestionSetId, LinkedHashMap::new,
                        Collectors.toList()));
        for (Long questionSetId : validQuestionSetIds) {
            resultMap.putIfAbsent(questionSetId, new ArrayList<>());
        }
        return resultMap;
    }

    private void checkQuestionSetExists(Long questionSetId) {
        QuestionSet questionSet = questionSetService.getById(questionSetId);
        ThrowUtils.throwIf(questionSet == null, ErrorCode.NOT_FOUND_ERROR, "题单不存在");
    }

    private void checkQuestionExists(Long questionId) {
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
    }

    private Integer getNextSortOrder(Long questionSetId) {
        QueryWrapper<QuestionSetItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_set_id", questionSetId);
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("sort_order");
        queryWrapper.last("limit 1");
        QuestionSetItem lastQuestionSetItem = this.getOne(queryWrapper, false);
        if (lastQuestionSetItem == null || lastQuestionSetItem.getSortOrder() == null) {
            return 0;
        }
        return lastQuestionSetItem.getSortOrder() + 1;
    }

    private void refreshQuestionSetQuestionNum(Long questionSetId) {
        QueryWrapper<QuestionSetItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_set_id", questionSetId);
        queryWrapper.eq("is_delete", 0);
        long count = this.count(queryWrapper);

        QuestionSet updateQuestionSet = new QuestionSet();
        updateQuestionSet.setId(questionSetId);
        updateQuestionSet.setQuestionNum((int) count);
        boolean result = questionSetService.updateById(updateQuestionSet);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新题单题目数量失败");
    }

    private Map<Long, Question> getQuestionIdQuestionMap(Set<Long> questionIdSet) {
        if (CollectionUtils.isEmpty(questionIdSet)) {
            return Collections.emptyMap();
        }
        return questionService.listByIds(questionIdSet).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity(), (oldValue, newValue) -> oldValue));
    }   

    private QuestionSetItemVO getQuestionSetItemVO(QuestionSetItem questionSetItem,
                                                   Map<Long, Question> questionIdQuestionMap,
                                                   HttpServletRequest request) {
        QuestionSetItemVO questionSetItemVO = new QuestionSetItemVO();
        BeanUtils.copyProperties(questionSetItem, questionSetItemVO);
        Question question = questionIdQuestionMap.get(questionSetItem.getQuestionId());
        if (question != null) {
            questionSetItemVO.setQuestionVO(questionService.getQuestionVO(question, request));
        }
        return questionSetItemVO;
    }

    private Map<Long, List<QuestionSetItemVO>> buildEmptyResultMap(List<Long> questionSetIds) {
        Map<Long, List<QuestionSetItemVO>> resultMap = new LinkedHashMap<>();
        for (Long questionSetId : questionSetIds) {
            resultMap.put(questionSetId, new ArrayList<>());
        }
        return resultMap;
    }
}




