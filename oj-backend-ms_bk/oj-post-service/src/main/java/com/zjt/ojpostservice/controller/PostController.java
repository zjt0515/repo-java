package com.zjt.ojpostservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.zjt.ojcommon.common.BaseResponse;
import com.zjt.ojcommon.common.DeleteRequest;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.common.ResultUtils;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.exception.ThrowUtils;
import com.zjt.ojmodel.model.dto.post.PostAddRequest;
import com.zjt.ojmodel.model.dto.post.PostEditRequest;
import com.zjt.ojmodel.model.dto.post.PostQueryRequest;
import com.zjt.ojmodel.model.dto.post.PostUpdateRequest;
import com.zjt.ojmodel.model.entity.Post;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.vo.PostVO;
import com.zjt.ojpostservice.service.PostService;
import com.zjt.ojserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题解帖子接口
 */
@RestController
@RequestMapping("/")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserFeignClient userFeignClient;

    private static final Gson GSON = new Gson();

    /**
     * 创建题解帖子
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        postService.validPost(post, true);
        postService.checkQuestionExists(post.getQuestionId());

        User loginUser = userFeignClient.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setThumbNum(0);
        post.setFavourNum(0);
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(post.getId());
    }

    /**
     * 删除题解帖子
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        Long id = deleteRequest.getId();
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非本人或管理员不可删除");
        }
        boolean result = postService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新题解帖子（仅管理员）
     *
     * @param postUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest request) {
        if (postUpdateRequest == null || postUpdateRequest.getId() == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (!userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        postService.validPost(post, false);
        if (post.getQuestionId() != null) {
            postService.checkQuestionExists(post.getQuestionId());
        }
        Post oldPost = postService.getById(postUpdateRequest.getId());
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取题解帖子（不脱敏）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Post> getPostById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userFeignClient.getLoginUser(request);
        if (!post.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非本人或管理员不可获取所有数据");
        }
        return ResultUtils.success(post);
    }

    /**
     * 根据 id 获取题解帖子 VO
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PostVO> getPostVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(postService.getPostVO(post));
    }

    /**
     * 分页获取题解帖子 VO 列表
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest) {
        if (postQueryRequest == null) {
            postQueryRequest = new PostQueryRequest();
        }
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage));
    }

    /**
     * 分页获取当前用户创建的题解帖子
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        if (postQueryRequest == null) {
            postQueryRequest = new PostQueryRequest();
        }
        User loginUser = userFeignClient.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage));
    }

    /**
     * 分页获取题解帖子列表（仅管理员）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Post>> listPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                   HttpServletRequest request) {
        if (postQueryRequest == null) {
            postQueryRequest = new PostQueryRequest();
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (!userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postPage);
    }

    /**
     * 编辑题解帖子（本人或管理员）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null || postEditRequest.getId() == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        postService.validPost(post, false);
        if (post.getQuestionId() != null) {
            postService.checkQuestionExists(post.getQuestionId());
        }

        User loginUser = userFeignClient.getLoginUser(request);
        Post oldPost = postService.getById(postEditRequest.getId());
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非本人或管理员不可编辑");
        }
        boolean result = postService.updateById(post);
        return ResultUtils.success(result);
    }
}
