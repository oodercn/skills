# API参考

## 检索接口

### 执行检索

**URL**: `POST /api/v1/rag/retrieve`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 检索语句 |
| strategy | string | 否 | 检索策略: KEYWORD, SEMANTIC, HYBRID |
| maxResults | integer | 否 | 最大返回结果数 |
| kbIds | array | 否 | 指定知识库ID列表 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "results": [
      {
        "docId": "doc-001",
        "content": "相关内容片段...",
        "score": 0.95,
        "source": "kb-001"
      }
    ],
    "total": 5,
    "strategy": "HYBRID"
  }
}
```

## Prompt构建

### 构建Prompt

**URL**: `POST /api/v1/rag/prompt`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 用户问题 |
| systemPrompt | string | 否 | 系统提示词 |
| maxContextTokens | integer | 否 | 最大上下文Token数 |
| kbIds | array | 否 | 指定知识库 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "prompt": "你是一个专业的技术助手...\n\n参考信息：\n1. ...\n\n用户问题：如何配置系统？",
    "contextTokens": 500,
    "sources": [
      {"docId": "doc-001", "docName": "配置指南.pdf"}
    ]
  }
}
```

### 自定义Prompt构建

**URL**: `POST /api/v1/rag/prompt/custom`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 用户问题 |
| template | string | 是 | Prompt模板 |
| contextPlaceholder | string | 否 | 上下文占位符 |
| queryPlaceholder | string | 否 | 问题占位符 |

## 知识库注册

### 注册知识库

**URL**: `POST /api/v1/rag/kb/{kbId}/register`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | string | 是 | 知识库名称 |
| description | string | 否 | 知识库描述 |

### 注销知识库

**URL**: `DELETE /api/v1/rag/kb/{kbId}`

## 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 知识库不存在 |
| 500 | 服务器内部错误 |
| 503 | 向量服务不可用 |
