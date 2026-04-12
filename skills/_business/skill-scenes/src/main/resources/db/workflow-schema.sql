-- Scene Engine 3.0.1 工作流管理数据库表
-- 数据库类型: SQLite/MySQL/H2

-- 场景工作流表
CREATE TABLE IF NOT EXISTS scene_workflows (
    workflow_id VARCHAR(64) PRIMARY KEY,
    scene_group_id VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    trigger_type VARCHAR(32),
    trigger_config TEXT,
    trigger_enabled BOOLEAN DEFAULT FALSE,
    variables TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id VARCHAR(64),
    version INT DEFAULT 1,
    auto_start BOOLEAN DEFAULT FALSE
);

-- 工作流步骤表
CREATE TABLE IF NOT EXISTS workflow_steps (
    step_id VARCHAR(64) PRIMARY KEY,
    workflow_id VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sequence INT NOT NULL,
    step_type VARCHAR(32) NOT NULL,
    config TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES scene_workflows(workflow_id) ON DELETE CASCADE
);

-- 工作流执行表
CREATE TABLE IF NOT EXISTS workflow_executions (
    execution_id VARCHAR(64) PRIMARY KEY,
    workflow_id VARCHAR(64) NOT NULL,
    scene_group_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    trigger_type VARCHAR(32),
    trigger_source VARCHAR(64),
    input_data TEXT,
    output_data TEXT,
    result TEXT,
    error_message TEXT,
    current_step_index INT DEFAULT 0,
    executor_id VARCHAR(64),
    FOREIGN KEY (workflow_id) REFERENCES scene_workflows(workflow_id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_workflows_scene_group ON scene_workflows(scene_group_id);
CREATE INDEX IF NOT EXISTS idx_workflows_status ON scene_workflows(status);
CREATE INDEX IF NOT EXISTS idx_steps_workflow ON workflow_steps(workflow_id);
CREATE INDEX IF NOT EXISTS idx_executions_workflow ON workflow_executions(workflow_id);
CREATE INDEX IF NOT EXISTS idx_executions_status ON workflow_executions(status);
CREATE INDEX IF NOT EXISTS idx_executions_start_time ON workflow_executions(start_time);
