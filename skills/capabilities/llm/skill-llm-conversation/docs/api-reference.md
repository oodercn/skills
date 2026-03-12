# API参考

## 接口列表

### 发送对话消息

**URL**: `POST /api/llm/chat`

**描述**: 发送对话消息并获取LLM响应

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| message | string | 是 | 用户消息内容 |
| sessionId | string | 否 | 会话ID，不传则创建新会话 |
| context | object | 否 | 额外上下文信息 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "sessionId": "session-123",
    "message": {
      "role": "assistant",
      "content": "你好！我是智能对话助手...",
      "timestamp": "2026-03-12T10:30:00Z"
    }
  }
}
```

### 流式对话

**URL**: `POST /api/llm/chat/stream`

**描述**: 流式对话，返回SSE事件流

**响应格式**: Server-Sent Events

```
event: message
data: {"content": "你"}

event: message
data: {"content": "好"}

event: done
data: {"sessionId": "session-123"}
```

### Function Calling

**URL**: `POST /api/llm/chat/function`

**描述**: 带函数调用的对话

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| message | string | 是 | 用户消息 |
| sessionId | string | 否 | 会话ID |
| functions | array | 是 | 函数定义列表 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "sessionId": "session-123",
    "functionCall": {
      "name": "get_weather",
      "arguments": "{\"city\": \"北京\"}"
    }
  }
}
```

### 获取会话列表

**URL**: `GET /api/llm/sessions`

**描述**: 获取当前用户的会话列表

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "sessions": [
      {
        "id": "session-123",
        "createdAt": "2026-03-12T10:00:00Z",
        "updatedAt": "2026-03-12T10:30:00Z",
        "messageCount": 10
      }
    ]
  }
}
```

### 删除会话

**URL**: `DELETE /api/llm/sessions/{sessionId}`

**描述**: 删除指定会话及其历史记录

### 获取对话历史

**URL**: `GET /api/llm/sessions/{sessionId}/history`

**描述**: 获取指定会话的对话历史

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "messages": [
      {
        "role": "user",
        "content": "你好",
        "timestamp": "2026-03-12T10:00:00Z"
      },
      {
        "role": "assistant",
        "content": "你好！",
        "timestamp": "2026-03-12T10:00:01Z"
      }
    ]
  }
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 会话不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
