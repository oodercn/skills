# skill-scheduler-quartz

Quartz调度服务，提供定时任务调度、执行管理

## 功能特性

- 定时调度 - 支持Cron表达式定时任务
- 任务管理 - 创建、暂停、恢复、删除任务
- 集群支持 - 支持分布式任务调度
- 持久化 - 任务持久化存储

## 快速开始

### 安装

```bash
skill install skill-scheduler-quartz
```

### 配置

```yaml
skill-scheduler-quartz:
  thread-pool-size: 10
  job-store: memory
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/scheduler/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "daily-report",
    "cron": "0 0 8 * * ?",
    "handler": "reportJobHandler"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [Cron指南](docs/cron-guide.md)
- [集群配置](docs/clustering.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| THREAD_POOL_SIZE | integer | 否 | 线程池大小，默认10 |
| JOB_STORE | string | 否 | 任务存储类型: memory/jdbc |

## 许可证

Apache-2.0
