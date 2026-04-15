package com.genshinya.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author genshinya
 * @time 2024-10-18 16:22:12
 * @description 用户角色DO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_role")
public class UserRoleDO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  用户名
     */
    private String username;

    /**
     *  用户角色
     */
    private String role;

    /**
     * 创建时间
     */
    private Date createTime;
}
