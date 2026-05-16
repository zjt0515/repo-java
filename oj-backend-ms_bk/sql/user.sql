-- PostgreSQL 数据库初始化
-- 使用方式：先创建数据库 oj，然后执行本脚本

-- 创建库（如未创建）
-- CREATE DATABASE oj ENCODING 'UTF8' LC_COLLATE 'zh_CN.UTF-8' LC_CTYPE 'zh_CN.UTF-8' TEMPLATE template0;

-- 用户表（user 是 PG 保留关键字，需用双引号）
CREATE TABLE IF NOT EXISTS "users"
(
    id           BIGSERIAL PRIMARY KEY,
    user_account  VARCHAR(256)                           NOT NULL,
    user_password VARCHAR(512)                           NOT NULL,
    union_id      VARCHAR(256)                           NULL,
    mp_open_id     VARCHAR(256)                           NULL,
    user_name     VARCHAR(256)                           NULL,
    user_avatar   VARCHAR(1024)                          NULL,
    user_profile  VARCHAR(512)                           NULL,
    user_role     VARCHAR(256) DEFAULT 'user'            NOT NULL,
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete     SMALLINT     DEFAULT 0                 NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_union_id ON users (union_id);
COMMENT ON TABLE users IS '用户';
COMMENT ON COLUMN users.id IS 'id';
COMMENT ON COLUMN users.user_account IS '账号';
COMMENT ON COLUMN users.user_password IS '密码';
COMMENT ON COLUMN users.union_id IS '微信开放平台id';
COMMENT ON COLUMN users.mp_open_id IS '公众号openId';
COMMENT ON COLUMN users.user_name IS '用户昵称';
COMMENT ON COLUMN users.user_avatar IS '用户头像';
COMMENT ON COLUMN users.user_profile IS '用户简介';
COMMENT ON COLUMN users.user_role IS '用户角色：user/admin/ban';
COMMENT ON COLUMN users.create_time IS '创建时间';
COMMENT ON COLUMN users.update_time IS '更新时间';
COMMENT ON COLUMN users.is_delete IS '是否删除';