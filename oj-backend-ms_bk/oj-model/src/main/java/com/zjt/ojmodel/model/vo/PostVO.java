package com.zjt.ojmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.zjt.ojmodel.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题解帖子封装类
 */
@Data
public class PostVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题解标题
     */
    private String title;

    /**
     * 题解内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 关联题目信息
     */
    private QuestionVO questionVO;

    /**
     * vo -> po
     *
     * @param postVO
     * @return
     */
    public static Post voToPo(PostVO postVO) {
        if (postVO == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postVO, post);
        List<String> tagList = postVO.getTags();
        if (tagList != null) {
            post.setTags(JSONUtil.toJsonStr(tagList));
        }
        return post;
    }

    /**
     * po -> vo
     *
     * @param post
     * @return
     */
    public static PostVO poToVo(Post post) {
        if (post == null) {
            return null;
        }
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        if (post.getTags() != null) {
            postVO.setTags(JSONUtil.toList(post.getTags(), String.class));
        }
        return postVO;
    }

    private static final long serialVersionUID = 1L;
}
