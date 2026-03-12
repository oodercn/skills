# API参考

## 知识库管理

### 创建知识库

**URL**: `POST /api/v1/kb`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | string | 是 | 知识库名称 |
| description | string | 否 | 知识库描述 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "id": "kb-001",
    "name": "技术文档库",
    "description": "技术文档和API参考",
    "createdAt": "2026-03-12T10:00:00Z"
  }
}
```

### 列出知识库

**URL**: `GET /api/v1/kb`

**响应示例**:

```json
{
  "code": 200,
  "data": [
    {
      "id": "kb-001",
      "name": "技术文档库",
      "documentCount": 25,
      "createdAt": "2026-03-12T10:00:00Z"
    }
  ]
}
```

### 获取知识库

**URL**: `GET /api/v1/kb/{id}`

### 更新知识库

**URL**: `PUT /api/v1/kb/{id}`

### 删除知识库

**URL**: `DELETE /api/v1/kb/{id}`

## 文档管理

### 上传文档

**URL**: `POST /api/v1/kb/{kbId}/documents`

**请求参数**: multipart/form-data

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | file | 是 | 文档文件 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "id": "doc-001",
    "name": "API文档.pdf",
    "status": "processing"
  }
}
```

### 获取文档列表

**URL**: `GET /api/v1/kb/{kbId}/documents`

### 删除文档

**URL**: `DELETE /api/v1/kb/{kbId}/documents/{docId}`

### 文档处理状态

**URL**: `GET /api/v1/kb/{kbId}/documents/{docId}/status`

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "id": "doc-001",
    "status": "indexed",
    "progress": 100,
    "chunkCount": 50
  }
}
```

## 检索接口

### 关键词检索

**URL**: `POST /api/v1/kb/{kbId}/search`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 检索关键词 |
| limit | integer | 否 | 返回数量，默认10 |

### 语义检索

**URL**: `POST /api/v1/kb/{kbId}/search/semantic`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 检索语句 |
| limit | integer | 否 | 返回数量 |
| threshold | number | 否 | 相似度阈值 |

### 混合检索

**URL**: `POST /api/v1/kb/{kbId}/search/hybrid`

### 跨库检索

**URL**: `POST /api/v1/kb/search/cross`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | string | 是 | 检索语句 |
| kbIds | array | 否 | 知识库ID列表，不传则检索全部 |
| limit | integer | 否 | 返回数量 |

## 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 知识库或文档不存在 |
| 413 | 文档大小超出限制 |
| 500 | 服务器内部错误 |
