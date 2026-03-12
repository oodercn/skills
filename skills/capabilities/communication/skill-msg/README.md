# skill-msg

消息服务，支持消息队列、订阅发布能力

## 功能特性

- 消息发布 - 发布消息到队列
- 消息订阅 - 订阅消息队列
- 队列管理 - 创建和管理队列
- 消息持久化 - 消息持久化存储

## 快速开始

### 安装

```bash
skill install skill-msg
```

### 配置

```yaml
skill-msg:
  msg-type: memory
  max-queue-size: 10000
  msg-ttl: 86400
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/msg/publish \
  -H "Content-Type: application/json" \
  -d '{
    "queue": "notifications",
    "message": "新消息内容"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [消息流程](docs/message-flow.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| MSG_TYPE | string | 否 | 消息类型: memory/redis/kafka |
| MAX_QUEUE_SIZE | integer | 否 | 最大队列大小 |
| MSG_TTL | integer | 否 | 消息过期时间(秒) |

## 许可证

Apache-2.0
