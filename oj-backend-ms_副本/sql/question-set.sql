-- 题单表
CREATE TABLE IF NOT EXISTS question_set (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    tags TEXT,
    question_num INTEGER NOT NULL DEFAULT 0,
    favour_num INTEGER NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete SMALLINT NOT NULL DEFAULT 0
    );

COMMENT ON TABLE question_set IS '题单';
COMMENT ON COLUMN question_set.id IS 'id';
COMMENT ON COLUMN question_set.title IS '题单标题';
COMMENT ON COLUMN question_set.description IS '题单描述';
COMMENT ON COLUMN question_set.tags IS '标签列表 JSON 数组';
COMMENT ON COLUMN question_set.question_num IS '题目数量';
COMMENT ON COLUMN question_set.favour_num IS '收藏数';
COMMENT ON COLUMN question_set.user_id IS '创建用户 id';
COMMENT ON COLUMN question_set.create_time IS '创建时间';
COMMENT ON COLUMN question_set.update_time IS '更新时间';
COMMENT ON COLUMN question_set.is_delete IS '是否删除';

CREATE INDEX IF NOT EXISTS idx_question_set_user_id ON question_set (user_id);
CREATE INDEX IF NOT EXISTS idx_question_set_favour_num ON question_set (favour_num);

-- 题单和题目的关系表（多对多）
CREATE TABLE IF NOT EXISTS question_set_item (
    id BIGSERIAL PRIMARY KEY,
    question_set_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete SMALLINT NOT NULL DEFAULT 0
    );

COMMENT ON TABLE question_set_item IS '题单题目关系';
COMMENT ON COLUMN question_set_item.id IS 'id';
COMMENT ON COLUMN question_set_item.question_set_id IS '题单 id';
COMMENT ON COLUMN question_set_item.question_id IS '题目 id';
COMMENT ON COLUMN question_set_item.sort_order IS '题目在题单中的排序';
COMMENT ON COLUMN question_set_item.create_time IS '创建时间';
COMMENT ON COLUMN question_set_item.update_time IS '更新时间';
COMMENT ON COLUMN question_set_item.is_delete IS '是否删除';

CREATE INDEX IF NOT EXISTS idx_question_set_item_set_sort
    ON question_set_item (question_set_id, sort_order)
    WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_question_set_item_question_id
    ON question_set_item (question_id)
    WHERE is_delete = 0;
CREATE UNIQUE INDEX IF NOT EXISTS uk_question_set_item_set_question
    ON question_set_item (question_set_id, question_id)
    WHERE is_delete = 0;
