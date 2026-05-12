package com.zjt.ojquestionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjt.ojmodel.model.entity.QuestionSetItem;
import com.zjt.ojmodel.model.vo.QuestionSetItemVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @author zz
* @description 针对表【question_set_item(题单题目关系)】的数据库操作Service
* @createDate 2026-05-11 01:16:12
*/
public interface QuestionSetItemService extends IService<QuestionSetItem> {

    /**
     * 校验题单题目关系参数
     *
     * @param questionSetItem 题单题目关系
     * @param add 是否为新增场景
     */
    void validQuestionSetItem(QuestionSetItem questionSetItem, boolean add);

    /**
     * 获取未删除的题单题目关系
     *
     * @param questionSetId 题单 id
     * @param questionId 题目 id
     * @return 题单题目关系，不存在时返回 null
     */
    QuestionSetItem getQuestionSetItem(Long questionSetId, Long questionId);

    /**
     * 添加题目到题单
     *
     * @param questionSetId 题单 id
     * @param questionId 题目 id
     * @param sortOrder 排序值，为空时追加到末尾
     * @return 关系 id
     */
    long addQuestionSetItem(Long questionSetId, Long questionId, Integer sortOrder);

    /**
     * 根据关系 id 从题单移除题目
     *
     * @param id 关系 id
     * @return 是否移除成功
     */
    boolean removeQuestionSetItem(Long id);

    /**
     * 从题单移除题目
     *
     * @param questionSetId 题单 id
     * @param questionId 题目 id
     * @return 是否移除成功
     */
    boolean removeQuestionSetItem(Long questionSetId, Long questionId);

    /**
     * 根据关系 id 调整题目在题单中的排序
     *
     * @param id 关系 id
     * @param sortOrder 排序值
     * @return 是否更新成功
     */
    boolean updateQuestionSetItemSort(Long id, Integer sortOrder);

    /**
     * 调整题目在题单中的排序
     *
     * @param questionSetId 题单 id
     * @param questionId 题目 id
     * @param sortOrder 排序值
     * @return 是否更新成功
     */
    boolean updateQuestionSetItemSort(Long questionSetId, Long questionId, Integer sortOrder);

    /**
     * 获取单个题单的题目关系封装列表
     *
     * @param questionSetId 题单 id
     * @param request 请求
     * @return 题单题目关系列表
     */
    List<QuestionSetItemVO> listQuestionSetItemVOByQuestionSetId(Long questionSetId, HttpServletRequest request);

    /**
     * 批量获取题单的题目关系封装列表
     *
     * @param questionSetIds 题单 id 集合
     * @param request 请求
     * @return key 为题单 id，value 为题单题目关系列表
     */
    Map<Long, List<QuestionSetItemVO>> listQuestionSetItemVOMap(Collection<Long> questionSetIds,
                                                               HttpServletRequest request);
}
