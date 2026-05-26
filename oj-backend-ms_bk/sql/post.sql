-- 题解帖子表
CREATE TABLE IF NOT EXISTS post
(
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT                             NOT NULL,
    title       VARCHAR(512)                       NOT NULL,
    content     TEXT                               NOT NULL,
    tags        VARCHAR(1024)                      NULL,
    thumb_num   INT      DEFAULT 0                 NOT NULL,
    favour_num  INT      DEFAULT 0                 NOT NULL,
    user_id     BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT  DEFAULT 0                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_post_question_id ON post (question_id);
CREATE INDEX IF NOT EXISTS idx_post_user_id ON post (user_id);
CREATE INDEX IF NOT EXISTS idx_post_question_create_time_not_deleted
    ON post (question_id, create_time)
    WHERE is_delete = 0;

COMMENT ON TABLE post IS '题解帖子';
COMMENT ON COLUMN post.id IS 'id';
COMMENT ON COLUMN post.question_id IS '题目 id';
COMMENT ON COLUMN post.title IS '题解标题';
COMMENT ON COLUMN post.content IS '题解内容';
COMMENT ON COLUMN post.tags IS '标签列表 JSON 数组';
COMMENT ON COLUMN post.thumb_num IS '点赞数';
COMMENT ON COLUMN post.favour_num IS '收藏数';
COMMENT ON COLUMN post.user_id IS '创建用户 id';
COMMENT ON COLUMN post.create_time IS '创建时间';
COMMENT ON COLUMN post.update_time IS '更新时间';
COMMENT ON COLUMN post.is_delete IS '是否删除';
