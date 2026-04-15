package com.genshinya.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genshinya.weblog.common.domain.dos.UserRoleDO;
import com.genshinya.weblog.common.utils.Response;

import java.util.List;

/**
 * @author genshinya
 * @time 2024-10-18 16:24:47
 * @description 用户角色 mapper
 */
public interface UserRoleMapper extends BaseMapper<UserRoleDO> {
    /**
     * 根据用户名查询 userRole集合
     * @param username
     * @return
     */
    default List<UserRoleDO> selectByUsername(String username) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUsername, username);
        return selectList(wrapper);
    }
}
