-- Scene Engine 3.0.1 监控指标数据库表
-- 数据库类型: SQLite/MySQL/H2

-- 场景组指标表
CREATE TABLE IF NOT EXISTS scene_group_metrics (
    metric_id VARCHAR(64) PRIMARY KEY,
    scene_group_id VARCHAR(64) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    metric_value DOUBLE NOT NULL,
    metric_unit VARCHAR(32),
    metric_type VARCHAR(32) DEFAULT 'GAUGE',
    tags TEXT,
    collect_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 指标统计表
CREATE TABLE IF NOT EXISTS metric_statistics (
    stat_id VARCHAR(64) PRIMARY KEY,
    scene_group_id VARCHAR(64) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    stat_type VARCHAR(32) NOT NULL,
    stat_value DOUBLE NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    sample_count INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 指标快照表
CREATE TABLE IF NOT EXISTS metric_snapshots (
    snapshot_id VARCHAR(64) PRIMARY KEY,
    scene_group_id VARCHAR(64) NOT NULL,
    snapshot_name VARCHAR(255),
    snapshot_data TEXT NOT NULL,
    snapshot_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id VARCHAR(64)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_metrics_scene_group ON scene_group_metrics(scene_group_id);
CREATE INDEX IF NOT EXISTS idx_metrics_name ON scene_group_metrics(metric_name);
CREATE INDEX IF NOT EXISTS idx_metrics_time ON scene_group_metrics(collect_time);
CREATE INDEX IF NOT EXISTS idx_metric_stats_scene_group ON metric_statistics(scene_group_id);
CREATE INDEX IF NOT EXISTS idx_metric_snapshots_scene_group ON metric_snapshots(scene_group_id);
