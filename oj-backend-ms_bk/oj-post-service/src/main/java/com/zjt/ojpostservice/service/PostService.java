package com.zjt.ojpostservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjt.ojmodel.model.dto.post.PostQueryRequest;
import com.zjt.ojmodel.model.entity.Post;
import com.zjt.ojmodel.model.vo.PostVO;

/**
 * @description 针对表【post(题解帖子)】的数据库操作Service
 */
public interface PostService extends IService<Post> {

    /**
     * 校验题解帖子
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 校验题目是否存在
     *
     * @param questionId
     */
    void checkQuestionExists(Long questionId);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 获取题解帖子封装
     *
     * @param post
     * @return
     */
    PostVO getPostVO(Post post);

    /**
     * 分页获取题解帖子封装
     *
     * @param postPage
     * @return
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage);
}
