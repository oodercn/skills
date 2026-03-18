# skill-notify

通知服务，支持多渠道通知推送

## 功能特性

- 多渠道推送 - 支持邮件、短信、App推送
- 广播通知 - 批量推送通知
- 定时通知 - 定时发送通知
- 重试机制 - 失败自动重试

## 快速开始

### 安装

```bash
skill install skill-notify
```

### 配置

```yaml
skill-notify:
  channels: email,sms,push
  retry-count: 3
  retry-delay: 60
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/notify/push \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "channel": "email",
    "title": "系统通知",
    "content": "您有新的消息"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [渠道配置](docs/channel-config.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| CHANNELS | string | 否 | 支持的通知渠道 |
| RETRY_COUNT | integer | 否 | 重试次数 |
| RETRY_DELAY | integer | 否 | 重试延迟(秒) |

## 许可证

Apache-2.0
