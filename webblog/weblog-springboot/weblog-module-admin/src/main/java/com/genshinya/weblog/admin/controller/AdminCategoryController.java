package com.genshinya.weblog.admin.controller;

import com.genshinya.weblog.admin.model.vo.AddCategoryReqVO;
import com.genshinya.weblog.admin.service.AdminCategoryService;
import com.genshinya.weblog.admin.service.impl.AdminCategoryServiceImpl;
import com.genshinya.weblog.common.aspect.ApiOperationLog;
import com.genshinya.weblog.common.domain.mapper.CategoryMapper;
import com.genshinya.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author genshinya
 * @time 2024-10-18 14:34:57
 * @description TODO
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 分类模块")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService categoryService;

    @PostMapping("/category/add")
    @ApiOperation(value = "添加分类")
    @ApiOperationLog(description = "添加分类")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }
}
