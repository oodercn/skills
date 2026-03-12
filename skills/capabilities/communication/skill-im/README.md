# skill-im

即时通讯服务，支持实时消息、群聊、在线状态

## 功能特性

- 实时消息 - WebSocket实时通信
- 群聊功能 - 创建和管理群组
- 在线状态 - 用户在线状态管理
- 消息历史 - 消息存储和查询

## 快速开始

### 安装

```bash
skill install skill-im
```

### 配置

```yaml
skill-im:
  ws-port: 8080
  max-connections: 1000
  heartbeat-interval: 30
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/im/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user123",
    "content": "你好！"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [WebSocket指南](docs/websocket-guide.md)
- [协议说明](docs/protocol.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| WS_PORT | integer | 否 | WebSocket端口，默认8080 |
| MAX_CONNECTIONS | integer | 否 | 最大连接数，默认1000 |
| HEARTBEAT_INTERVAL | integer | 否 | 心跳间隔(秒)，默认30 |

## 许可证

Apache-2.0
