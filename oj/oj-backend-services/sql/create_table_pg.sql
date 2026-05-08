-- PostgreSQL 数据库初始化
-- 使用方式：先创建数据库 oj，然后执行本脚本

-- 创建库（如未创建）
-- CREATE DATABASE oj ENCODING 'UTF8' LC_COLLATE 'zh_CN.UTF-8' LC_CTYPE 'zh_CN.UTF-8' TEMPLATE template0;

-- 用户表（user 是 PG 保留关键字，需用双引号）
CREATE TABLE IF NOT EXISTS "user"
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
CREATE INDEX IF NOT EXISTS idx_union_id ON "user" (union_id);
COMMENT ON TABLE "user" IS '用户';
COMMENT ON COLUMN "user".id IS 'id';
COMMENT ON COLUMN "user".user_account IS '账号';
COMMENT ON COLUMN "user".user_password IS '密码';
COMMENT ON COLUMN "user".union_id IS '微信开放平台id';
COMMENT ON COLUMN "user".mp_open_id IS '公众号openId';
COMMENT ON COLUMN "user".user_name IS '用户昵称';
COMMENT ON COLUMN "user".user_avatar IS '用户头像';
COMMENT ON COLUMN "user".user_profile IS '用户简介';
COMMENT ON COLUMN "user".user_role IS '用户角色：user/admin/ban';
COMMENT ON COLUMN "user".create_time IS '创建时间';
COMMENT ON COLUMN "user".update_time IS '更新时间';
COMMENT ON COLUMN "user".is_delete IS '是否删除';

-- 题目表
CREATE TABLE IF NOT EXISTS question
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(512)                       NULL,
    content     TEXT                               NULL,
    tags        VARCHAR(1024)                      NULL,
    answer      TEXT                               NULL,
    submit_num   INT      DEFAULT 0                 NOT NULL,
    accepted_num INT      DEFAULT 0                 NOT NULL,
    judge_case   TEXT                               NULL,
    judge_config TEXT                               NULL,
    thumb_num    INT      DEFAULT 0                 NOT NULL,
    favour_num   INT      DEFAULT 0                 NOT NULL,
    user_id      BIGINT                             NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT  DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_user_id ON question (user_id);
COMMENT ON TABLE question IS '题目';

-- 题目提交表
CREATE TABLE IF NOT EXISTS question_submit
(
    id          BIGSERIAL PRIMARY KEY,
    language    VARCHAR(128)                       NOT NULL,
    code        TEXT                               NOT NULL,
    judge_info  TEXT                               NOT NULL,
    status      INT      DEFAULT 0                 NOT NULL,
    question_id  BIGINT                             NOT NULL,
    user_id      BIGINT                             NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT  DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_question_id ON question_submit (question_id);
CREATE INDEX IF NOT EXISTS idx_user_id ON question_submit (user_id);
COMMENT ON TABLE question_submit IS '题目提交';

-- 帖子点赞表（硬删除）
CREATE TABLE IF NOT EXISTS post_thumb
(
    id         BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_post_id ON post_thumb (post_id);
CREATE INDEX IF NOT EXISTS idx_user_id ON post_thumb (user_id);
COMMENT ON TABLE post_thumb IS '帖子点赞';

-- 帖子收藏表（硬删除）
CREATE TABLE IF NOT EXISTS post_favour
(
    id         BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_post_id ON post_favour (post_id);
CREATE INDEX IF NOT EXISTS idx_user_id ON post_favour (user_id);
COMMENT ON TABLE post_favour IS '帖子收藏';
