package com.genshinya.mybatisplusssgdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genshinya.mybatisplusssgdemo.pojo.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository//将类或接口标识为持久层组件
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据id查询用户信息，返回map集合
     * @param id
     * @return
     */
    Map<String,Object> selectMapById(Long id);

    /**
     * 通过年龄查询用户信息，同时分页
     * @param page MybatisPlus提供的分页对象，必须位于第一个参数
     * @param age
     * @return
     */
    Page<User> selectPageVo(@Param("page") Page<User> page,@Param("age") Integer age);
}
