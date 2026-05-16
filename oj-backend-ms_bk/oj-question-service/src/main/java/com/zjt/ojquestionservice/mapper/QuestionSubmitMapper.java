package com.zjt.ojquestionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojmodel.model.vo.QuestionSubmitRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author genshinya
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-12-13 14:14:59
* @Entity generator.domain.QuestionSubmit
*/
@Mapper
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

    IPage<QuestionSubmit> selectSubmitPage(IPage<?> page, @Param("request") QuestionSubmitQueryRequest request);

    IPage<QuestionSubmitRawVO> selectSubmitRawVOPage(IPage<?> page, @Param("request") QuestionSubmitQueryRequest request);

}




