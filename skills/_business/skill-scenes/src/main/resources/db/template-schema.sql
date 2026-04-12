-- Scene Engine 3.0.1 模板管理数据库表
-- 数据库类型: SQLite/MySQL/H2

-- 场景组模板表
CREATE TABLE IF NOT EXISTS scene_group_templates (
    template_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(64),
    template_data TEXT NOT NULL,
    version INT DEFAULT 1,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id VARCHAR(64),
    tags TEXT
);

-- 模板使用记录表
CREATE TABLE IF NOT EXISTS template_usage_records (
    record_id VARCHAR(64) PRIMARY KEY,
    template_id VARCHAR(64) NOT NULL,
    scene_group_id VARCHAR(64) NOT NULL,
    use_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(64),
    FOREIGN KEY (template_id) REFERENCES scene_group_templates(template_id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_templates_category ON scene_group_templates(category);
CREATE INDEX IF NOT EXISTS idx_templates_status ON scene_group_templates(status);
CREATE INDEX IF NOT EXISTS idx_template_usage_template ON template_usage_records(template_id);
