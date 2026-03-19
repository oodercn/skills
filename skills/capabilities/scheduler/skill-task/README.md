# skill-task

任务管理服务，提供任务创建、执行、状态管理

## 功能特性

- 任务创建 - 创建异步任务
- 任务执行 - 执行任务并跟踪状态
- 状态管理 - 管理任务生命周期
- 超时控制 - 任务超时自动终止

## 快速开始

### 安装

```bash
skill install skill-task
```

### 配置

```yaml
skill-task:
  max-tasks: 1000
  task-timeout: 3600
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "data-import",
    "handler": "importHandler",
    "params": {"file": "data.csv"}
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [任务生命周期](docs/task-lifecycle.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| MAX_TASKS | integer | 否 | 最大任务数，默认1000 |
| TASK_TIMEOUT | integer | 否 | 任务超时(秒)，默认3600 |

## 许可证

Apache-2.0
