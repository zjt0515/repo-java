package com.genshinya.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genshinya.weblog.common.domain.dos.CategoryDO;

/**
 * @author genshinya
 * @time 2024-10-18 13:51:57
 * @description 文章分类 mapper
 */
public interface CategoryMapper extends BaseMapper<CategoryDO> {
    /**
     * 根据分类名查询
     * @param categoryName 分类名称
     * @return CategoryDO
     */
    default CategoryDO selectByName(String categoryName) {
        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryDO::getName, categoryName);

        // 执行查询
        return selectOne(wrapper);
    }
}
