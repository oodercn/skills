# 配置指南

## 基础配置

```yaml
skill-llm-conversation:
  enabled: true
  max-history: 100
  session-timeout: 3600
  enable-streaming: true
  max-tokens: 4096
```

## 会话配置

```yaml
skill-llm-conversation:
  session:
    timeout: 3600
    max-history: 100
    storage: memory
    cleanup-interval: 300
```

### 存储类型

| 类型 | 说明 |
|------|------|
| memory | 内存存储，重启丢失 |
| persistent | 持久化存储 |

## 流式输出配置

```yaml
skill-llm-conversation:
  streaming:
    enabled: true
    buffer-size: 1024
    timeout: 60000
```

## Function Calling配置

```yaml
skill-llm-conversation:
  function-calling:
    enabled: true
    max-functions: 10
    timeout: 30000
```

## 性能配置

```yaml
skill-llm-conversation:
  performance:
    connection-pool-size: 10
    request-timeout: 60000
    retry-times: 3
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| max-history | integer | 100 | 最大历史记录数 |
| session-timeout | integer | 3600 | 会话超时(秒) |
| enable-streaming | boolean | true | 启用流式输出 |
| max-tokens | integer | 4096 | 最大Token数 |
