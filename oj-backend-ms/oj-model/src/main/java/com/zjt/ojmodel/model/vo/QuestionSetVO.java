package com.zjt.ojmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.zjt.ojmodel.model.entity.QuestionSet;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题单
 * @TableName question_set
 */
@Data
public class QuestionSetVO implements Serializable {
    private Long id;

    /**
     * 题单标题
     */
    private String title;

    /**
     * 题单描述
     */
    private String description;

    /**
     * 标签*
     */
    private List<String> tags;

    /**
     * 题目数量
     */
    private Integer questionNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户信息*
     */
    private UserVO userVO;

    /**
     * 题单内题目列表，带排序信息*
     */
    private List<QuestionSetItemVO> questionSetItemVOList;

    private Date createTime;

    private Date updateTime;

    /**
     * 包装类转对象
     * 只有属性从vo获取的、且类型不同才要转，关联对象手动组装，不需要JSON转换
     * @param questionSetVO
     * @return
     */
    public static QuestionSet voToPo(QuestionSetVO questionSetVO) {
        if (questionSetVO == null) {
            return null;
        }
        QuestionSet questionSet = new QuestionSet();
        BeanUtils.copyProperties(questionSetVO, questionSet);

        // tags List<String> -> String
        List<String> tagList = questionSetVO.getTags();
        if (tagList != null) {
            questionSet.setTags(JSONUtil.toJsonStr(tagList));
        }
        return questionSet;
    }

    /**
     * 对象转包装类
     *
     * @param questionSet
     * @return
     */
    public static QuestionSetVO poToVo(QuestionSet questionSet) {
        if (questionSet == null) {
            return null;
        }
        QuestionSetVO questionSetVO = new QuestionSetVO();
        BeanUtils.copyProperties(questionSet, questionSetVO);

        String tags = questionSet.getTags();
        // Tags转化 | String -> List
        List<String> tagList = JSONUtil.toList(tags, String.class);
        questionSetVO.setTags(tagList);
        return questionSetVO;
    }

    private static final long serialVersionUID = 1L;

}
