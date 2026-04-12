-- Scene Engine 3.0.1 场景组配置管理数据库表
-- 数据库类型: SQLite/MySQL/H2

-- 场景组配置表
CREATE TABLE IF NOT EXISTS scene_group_config (
    config_id VARCHAR(64) PRIMARY KEY,
    scene_group_id VARCHAR(64) NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value TEXT,
    config_type VARCHAR(32) DEFAULT 'STRING',
    description TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id VARCHAR(64),
    UNIQUE(scene_group_id, config_key)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_config_scene_group ON scene_group_config(scene_group_id);
CREATE INDEX IF NOT EXISTS idx_config_key ON scene_group_config(config_key);
