# API参考

## 接口列表

### 知识库管理

#### 获取知识库列表

**URL**: `GET /api/knowledge/bases`

**描述**: 获取所有知识库列表

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "bases": [
      {
        "id": "kb-001",
        "name": "技术文档库",
        "description": "技术文档和API参考",
        "documentCount": 25,
        "createdAt": "2026-03-12T10:00:00Z"
      }
    ]
  }
}
```

#### 创建知识库

**URL**: `POST /api/knowledge/bases`

**描述**: 创建新的知识库

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
    "description": "技术文档和API参考"
  }
}
```

### 文档管理

#### 获取文档列表

**URL**: `GET /api/knowledge/documents`

**描述**: 获取指定知识库的文档列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| kbId | string | 是 | 知识库ID |
| page | integer | 否 | 页码 |
| size | integer | 否 | 每页数量 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "documents": [
      {
        "id": "doc-001",
        "name": "API文档.pdf",
        "size": 1024000,
        "status": "indexed",
        "createdAt": "2026-03-12T10:00:00Z"
      }
    ],
    "total": 25
  }
}
```

#### 上传文档

**URL**: `POST /api/knowledge/documents`

**描述**: 上传文档到知识库

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | file | 是 | 文档文件 |
| kbId | string | 是 | 知识库ID |

**支持格式**: PDF, Word (.docx), Markdown (.md), TXT

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

#### 检查文档状态

**URL**: `GET /api/knowledge/documents/{docId}/status`

**描述**: 检查文档处理状态

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

### 知识检索

#### 关键词检索

**URL**: `POST /api/knowledge/search`

**描述**: BM25关键词检索

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| kbId | string | 否 | 知识库ID，不传则跨库检索 |
| query | string | 是 | 检索关键词 |
| topK | integer | 否 | 返回数量，默认10 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "results": [
      {
        "docId": "doc-001",
        "docName": "API文档.pdf",
        "content": "相关内容片段...",
        "score": 0.95,
        "metadata": {
          "page": 5
        }
      }
    ]
  }
}
```

#### RAG智能问答

**URL**: `POST /api/knowledge/rag`

**描述**: 基于检索增强生成的智能问答

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| kbId | string | 否 | 知识库ID |
| question | string | 是 | 用户问题 |
| topK | integer | 否 | 检索数量，默认5 |
| stream | boolean | 否 | 是否流式输出 |

**响应示例**:

```json
{
  "code": 200,
  "data": {
    "answer": "根据文档，系统支持以下功能...",
    "sources": [
      {
        "docId": "doc-001",
        "docName": "API文档.pdf",
        "content": "相关内容片段..."
      }
    ]
  }
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 知识库或文档不存在 |
| 413 | 文档大小超出限制 |
| 415 | 不支持的文档格式 |
| 500 | 服务器内部错误 |
| 503 | 向量服务不可用 |
