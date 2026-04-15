package com.genshinya.mybatisplusssgdemo.pojo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


// @NoArgsConstructor//自动添加无参构造
// @AllArgsConstructor//自动添加有参构造
@Data //代替下面所有？
// @Getter
// @Setter
// @EqualsAndHashCode
@TableName("user")
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    @TableLogic
    private Integer isDeleted;
}
