use blog;
CREATE TABLE `t_user`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`    varchar(60)         NOT NULL COMMENT '用户名',
    `password`    varchar(60)         NOT NULL COMMENT '密码',
    `create_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`  tinyint(2)          NOT NULL DEFAULT '0' COMMENT '逻辑删除：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_role`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`    varchar(60)         NOT NULL COMMENT '用户名',
    `role`        varchar(60)         NOT NULL COMMENT '角色',
    `create_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_username` (`username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='用户角色表';


CREATE TABLE `t_category`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `name`        varchar(60)         NOT NULL DEFAULT '' COMMENT '分类名称',
    `create_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`  tinyint(2)          NOT NULL DEFAULT '0' COMMENT '逻辑删除标志位：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='文章分类表';

INSERT INTO `blog`.`t_user` (`username`, `password`, `create_time`, `update_time`, `is_deleted`) VALUES ('quanxiaoha', '$2a$10$n7RJ1q.RnXx5M3O6B0i0he04fZOPjIJpyWcKuicW1bFyFHWhlGose', now(), now(), 0);
