package com.genshinya.weblog.admin.service;

import com.genshinya.weblog.admin.model.vo.AddCategoryReqVO;
import com.genshinya.weblog.common.utils.Response;

/**
 * @author genshinya
 * @time 2024-10-18 14:19:19
 * @description TODO
 */
public interface AdminCategoryService {
    /**
     * 添加文章分类
     * @param addCategoryReqVO 请求入参
     * @return 自定义响应
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);
}
