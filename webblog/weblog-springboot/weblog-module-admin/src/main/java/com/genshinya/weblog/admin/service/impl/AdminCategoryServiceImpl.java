package com.genshinya.weblog.admin.service.impl;

import com.genshinya.weblog.admin.model.vo.AddCategoryReqVO;
import com.genshinya.weblog.admin.service.AdminCategoryService;
import com.genshinya.weblog.common.domain.dos.CategoryDO;
import com.genshinya.weblog.common.domain.mapper.CategoryMapper;
import com.genshinya.weblog.common.enums.ResponseCodeEnum;
import com.genshinya.weblog.common.exception.BizException;
import com.genshinya.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author genshinya
 * @time 2024-10-18 14:20:55
 * @description 文章分类业务
 */
@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String categoryName = addCategoryReqVO.getName();
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);
        if (Objects.nonNull(categoryDO)){
            log.warn(categoryName + "分类名称已存在");
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        CategoryDO category = CategoryDO.builder().name(categoryName.trim()).build();
        categoryMapper.insert(category);
        return Response.success();
    }
}
