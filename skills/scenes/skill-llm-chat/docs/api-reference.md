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
| provider | string | 否 | 指定Provider，不传使用默认 |
| model | string | 否 | 指定模型，不传使用默认 |
| temperature | number | 否 | 温度参数，覆盖默认值 |
| maxTokens | integer | 否 | 最大Token数，覆盖默认值 |

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

**请求参数**: 同上

**响应格式**: Server-Sent Events

```
event: message
data: {"content": "你"}

event: message
data: {"content": "好"}

event: done
data: {"sessionId": "session-123"}
```

### 获取会话列表

**URL**: `GET /api/llm/sessions`

**描述**: 获取当前用户的会话列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认20 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "sessions": [
      {
        "id": "session-123",
        "title": "关于AI的讨论",
        "createdAt": "2026-03-12T10:00:00Z",
        "updatedAt": "2026-03-12T10:30:00Z",
        "messageCount": 10
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  }
}
```

### 删除会话

**URL**: `DELETE /api/llm/sessions/{sessionId}`

**描述**: 删除指定会话及其历史记录

**响应示例**:

```json
{
  "code": 200,
  "message": "会话已删除"
}
```

### 获取对话历史

**URL**: `GET /api/llm/sessions/{sessionId}/history`

**描述**: 获取指定会话的对话历史

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | integer | 否 | 返回消息数量，默认50 |

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
        "content": "你好！有什么可以帮助你的？",
        "timestamp": "2026-03-12T10:00:01Z"
      }
    ]
  }
}
```

### 获取Provider列表

**URL**: `GET /api/llm/providers`

**描述**: 获取可用的LLM Provider列表

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "providers": [
      {
        "id": "baidu",
        "name": "百度文心一言",
        "models": ["ernie-bot-4", "ernie-bot-turbo"]
      },
      {
        "id": "openai",
        "name": "OpenAI",
        "models": ["gpt-4", "gpt-3.5-turbo"]
      }
    ]
  }
}
```

### 获取可用模型

**URL**: `GET /api/llm/models`

**描述**: 获取指定Provider或所有可用模型

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| provider | string | 否 | 指定Provider，不传返回所有 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "models": [
      {
        "id": "ernie-bot-4",
        "name": "文心大模型4.0",
        "provider": "baidu",
        "maxTokens": 8192
      }
    ]
  }
}
```

### 构建上下文

**URL**: `POST /api/v1/llm/context/build`

**描述**: 构建对话上下文

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | string | 是 | 会话ID |
| sources | array | 否 | 上下文来源列表 |
| maxTokens | integer | 否 | 最大上下文Token数 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "context": "构建的上下文内容...",
    "tokenCount": 500
  }
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权，请检查API Key |
| 404 | 会话或资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
| 503 | LLM服务不可用 |
