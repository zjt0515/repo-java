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

