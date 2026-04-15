package com.genshinya.boo302demo2.mapper;

import com.genshinya.boo302demo2.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where id = #{id}")
    public User findById(Integer id);

}
