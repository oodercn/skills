# 会话管理

## 会话生命周期

```
创建 -> 活跃 -> 空闲 -> 过期 -> 清理
```

## 创建会话

会话在首次发送消息时自动创建：

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好"
  }'
```

响应中会返回新创建的sessionId。

## 会话超时

默认会话超时时间为1小时，可通过配置调整：

```yaml
skill-llm-conversation:
  session:
    timeout: 7200  # 2小时
```

## 会话存储

### 内存存储

```yaml
skill-llm-conversation:
  session:
    storage: memory
```

特点：
- 速度快
- 重启后丢失

### 持久化存储

```yaml
skill-llm-conversation:
  session:
    storage: persistent
```

特点：
- 数据持久化
- 支持分布式部署

## 历史记录管理

### 限制历史记录数量

```yaml
skill-llm-conversation:
  session:
    max-history: 50
```

### 获取历史记录

```bash
curl http://localhost:8080/api/llm/sessions/{sessionId}/history
```

### 清除历史记录

```bash
curl -X DELETE http://localhost:8080/api/llm/sessions/{sessionId}
```

## 会话清理

自动清理过期会话：

```yaml
skill-llm-conversation:
  session:
    cleanup-interval: 300  # 每5分钟清理一次
```

## 多设备同步

启用持久化存储后，支持多设备会话同步：

```yaml
skill-llm-conversation:
  session:
    storage: persistent
    sync-enabled: true
```
