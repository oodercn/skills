# 流式输出

## 概述

流式输出使用Server-Sent Events (SSE)技术，实时返回LLM生成的内容，提升用户体验。

## 启用流式输出

```yaml
skill-llm-conversation:
  streaming:
    enabled: true
```

## 使用方式

### 请求

```bash
curl -X POST http://localhost:8080/api/llm/chat/stream \
  -H "Content-Type: application/json" \
  -d '{
    "message": "请写一首诗",
    "sessionId": "stream-session"
  }'
```

### 响应格式

```
event: message
data: {"content": "春"}

event: message
data: {"content": "风"}

event: message
data: {"content": "吹"}

event: done
data: {"sessionId": "stream-session", "totalTokens": 150}
```

## 事件类型

| 事件 | 说明 |
|------|------|
| message | 内容片段 |
| done | 完成事件 |
| error | 错误事件 |

## 前端集成

### JavaScript示例

```javascript
const eventSource = new EventSource('/api/llm/chat/stream');

eventSource.addEventListener('message', (e) => {
  const data = JSON.parse(e.data);
  console.log('收到内容:', data.content);
});

eventSource.addEventListener('done', (e) => {
  console.log('流式输出完成');
  eventSource.close();
});

eventSource.addEventListener('error', (e) => {
  console.error('发生错误');
  eventSource.close();
});
```

## 配置选项

```yaml
skill-llm-conversation:
  streaming:
    enabled: true
    buffer-size: 1024      # 缓冲区大小
    timeout: 60000         # 超时时间(毫秒)
    heartbeat: 15000       # 心跳间隔(毫秒)
```

## Nginx配置

确保Nginx不缓冲SSE响应：

```nginx
location /api/llm/chat/stream {
    proxy_buffering off;
    proxy_cache off;
    proxy_read_timeout 300s;
    proxy_http_version 1.1;
    proxy_set_header Connection '';
}
```

## 错误处理

```javascript
eventSource.onerror = (e) => {
  if (e.readyState === EventSource.CLOSED) {
    console.log('连接已关闭');
  } else if (e.readyState === EventSource.CONNECTING) {
    console.log('正在重连...');
  } else {
    console.error('连接错误');
  }
};
```
