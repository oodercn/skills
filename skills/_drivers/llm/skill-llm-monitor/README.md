# skill-llm-monitor

LLM监控服务， 提供调用统计、日志记录、性能监控、使用排名等功能。

## 功能特性

- **调用统计** - 按用户/部门/模块统计LLM调用
- **日志记录** - 完整的调用日志追踪
- **性能监控** - 响应时间、Token消耗监控
- **使用排名** - 模型使用量排名
- **成本分析** - 调用成本统计

## API端点

### 监控统计

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-monitor/stats | GET | 获取总体统计 |
| /api/v1/llm-monitor/stats/user/{userId} | GET | 获取用户统计 |
| /api/v1/llm-monitor/stats/department/{deptId} | GET | 获取部门统计 |
| /api/v1/llm-monitor/stats/module/{moduleId} | GET | 获取模块统计 |

### 调用日志

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-monitor/logs | GET | 获取调用日志列表 |
| /api/v1/llm-monitor/logs/{id} | GET | 获取日志详情 |
| /api/v1/llm-monitor/logs/search | POST | 搜索日志 |

### 性能监控

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-monitor/performance | GET | 获取性能指标 |
| /api/v1/llm-monitor/ranking | GET | 获取使用排名 |

## 快速开始

### 安装

```bash
skill install skill-llm-monitor
```

### 配置

```yaml
llm-monitor:
  enabled: true
  log-retention-days: 30
  stats-interval: 3600000  # 1小时
```

### 使用示例

```bash
# 获取总体统计
curl http://localhost:8080/api/v1/llm-monitor/stats

# 获取用户统计
curl http://localhost:8080/api/v1/llm-monitor/stats/user/user-001

# 搜索日志
curl -X POST http://localhost:8080/api/v1/llm-monitor/logs/search \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2026-04-01T00:00:00Z",
    "endTime": "2026-04-03T23:59:59Z",
    "provider": "deepseek"
  }'
```

## 统计维度

| 维度 | 说明 |
|------|------|
| 用户维度 | 按用户ID统计调用次数、Token消耗 |
| 部门维度 | 按部门统计总调用量 |
| 模块维度 | 按技能模块统计调用分布 |
| Provider维度 | 按LLM提供商统计使用量 |
| 模型维度 | 按模型统计使用排名 |

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| enabled | boolean | true | 是否启用监控 |
| log-retention-days | int | 30 | 日志保留天数 |
| stats-interval | long | 3600000 | 统计间隔(ms) |
| track-cost | boolean | true | 是否追踪成本 |

## 依赖

- skill-common (3.0.1)

## 许可证

Apache-2.0
