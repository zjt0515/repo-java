package com.zjt.ojpostservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.constant.CommonConstant;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.exception.ThrowUtils;
import com.zjt.ojcommon.utils.SqlUtils;
import com.zjt.ojmodel.model.dto.post.PostQueryRequest;
import com.zjt.ojmodel.model.entity.Post;
import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.vo.PostVO;
import com.zjt.ojmodel.model.vo.QuestionVO;
import com.zjt.ojpostservice.mapper.PostMapper;
import com.zjt.ojpostservice.service.PostService;
import com.zjt.ojserviceclient.service.QuestionFeignClient;
import com.zjt.ojserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description 针对表【post(题解帖子)】的数据库操作Service实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionFeignClient questionFeignClient;

    /**
     * 校验题解帖子是否合法
     *
     * @param post
     * @param add
     */
    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionId = post.getQuestionId();
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();

        if (add) {
            ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR, "题目 id 不合法");
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content), ErrorCode.PARAMS_ERROR);
        }
        if (questionId != null && questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目 id 不合法");
        }
        if (StringUtils.isNotBlank(title) && title.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 81920) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(tags) && tags.length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签过长");
        }
    }

    /**
     * 校验题目是否存在
     *
     * @param questionId
     */
    @Override
    public void checkQuestionExists(Long questionId) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目 id 不合法");
        }
        Question question = questionFeignClient.getQuestionById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
    }

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        Long id = postQueryRequest.getId();
        Long questionId = postQueryRequest.getQuestionId();
        Long userId = postQueryRequest.getUserId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tags = postQueryRequest.getTags();
        String sortField = StrUtil.toUnderlineCase(postQueryRequest.getSortField());
        String sortOrder = postQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题解帖子封装
     *
     * @param post
     * @return
     */
    @Override
    public PostVO getPostVO(Post post) {
        PostVO postVO = PostVO.poToVo(post);
        if (postVO == null) {
            return null;
        }
        Long userId = post.getUserId();
        if (userId != null && userId > 0) {
            User user = userFeignClient.getById(userId);
            postVO.setUserVO(userFeignClient.getUserVO(user));
        }
        Long questionId = post.getQuestionId();
        if (questionId != null && questionId > 0) {
            Question question = questionFeignClient.getQuestionById(questionId);
            postVO.setQuestionVO(QuestionVO.poToVo(question));
        }
        return postVO;
    }

    /**
     * 分页获取题解帖子封装
     *
     * @param postPage
     * @return
     */
    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }

        Set<Long> userIdSet = postList.stream()
                .map(Post::getUserId)
                .filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.poToVo(post);
            Long userId = post.getUserId();
            if (userIdUserListMap.containsKey(userId)) {
                postVO.setUserVO(userFeignClient.getUserVO(userIdUserListMap.get(userId).get(0)));
            }
            Long questionId = post.getQuestionId();
            if (questionId != null && questionId > 0) {
                Question question = questionFeignClient.getQuestionById(questionId);
                postVO.setQuestionVO(QuestionVO.poToVo(question));
            }
            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }
}
