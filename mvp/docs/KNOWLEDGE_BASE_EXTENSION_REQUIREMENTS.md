# 知识资料库扩展需求说明

## 致：SCENEENGINE 团队

本文档描述知识资料库模块需要扩展的功能接口，请根据需求自行扩展相关功能，MVP前端团队将等待调用。

---

## 一、当前状态

### 已实现功能
- 知识库基础CRUD API (`/api/v1/knowledge-bases`)
- 场景知识绑定 API (`/api/v1/scene-groups/{sceneGroupId}/knowledge`)
- 三层知识架构配置 (GENERAL/PROFESSIONAL/SCENE)
- LLM集成 (DeepSeek、百度文心、通义千问)

### 待扩展功能
- 文档管理 API
- 向量存储与检索
- Embedding集成
- 数据持久化

---

## 二、需求接口定义

### 2.1 文档管理 API

#### 2.1.1 获取文档列表
```
GET /api/v1/knowledge-bases/{kbId}/documents

Response:
{
  "status": "success",
  "data": {
    "list": [
      {
        "docId": "doc-001",
        "title": "文档标题",
        "content": "文档内容摘要...",
        "source": "upload|text|url",
        "sourceUrl": "原始URL(如果是URL导入)",
        "fileSize": 1024,
        "chunkCount": 10,
        "status": "indexed|pending|failed",
        "createTime": 1234567890000,
        "updateTime": 1234567890000
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

#### 2.1.2 添加文本内容
```
POST /api/v1/knowledge-bases/{kbId}/documents/text

Request:
{
  "title": "文档标题",
  "content": "文档内容文本",
  "tags": ["标签1", "标签2"]
}

Response:
{
  "status": "success",
  "data": {
    "docId": "doc-xxx",
    "title": "文档标题",
    "status": "pending"
  }
}
```

#### 2.1.3 上传文件
```
POST /api/v1/knowledge-bases/{kbId}/documents/upload
Content-Type: multipart/form-data

参数:
- file: 文件内容
- title: 文档标题(可选)
- tags: 标签(可选，逗号分隔)

Response:
{
  "status": "success",
  "data": {
    "docId": "doc-xxx",
    "title": "文件名",
    "fileSize": 10240,
    "status": "pending"
  }
}
```

#### 2.1.4 URL导入
```
POST /api/v1/knowledge-bases/{kbId}/documents/url

Request:
{
  "url": "https://example.com/article",
  "title": "自定义标题(可选)",
  "tags": ["标签1"]
}

Response:
{
  "status": "success",
  "data": {
    "docId": "doc-xxx",
    "title": "页面标题",
    "sourceUrl": "https://example.com/article",
    "status": "pending"
  }
}
```

#### 2.1.5 删除文档
```
DELETE /api/v1/knowledge-bases/{kbId}/documents/{docId}

Response:
{
  "status": "success",
  "data": true
}
```

#### 2.1.6 重新索引文档
```
POST /api/v1/knowledge-bases/{kbId}/documents/{docId}/reindex

Response:
{
  "status": "success",
  "data": {
    "docId": "doc-xxx",
    "status": "indexing"
  }
}
```

---

### 2.2 向量检索 API

#### 2.2.1 知识检索
```
POST /api/v1/knowledge-bases/{kbId}/search

Request:
{
  "query": "检索查询文本",
  "topK": 5,
  "threshold": 0.7,
  "filters": {
    "tags": ["标签1"],
    "source": "upload"
  }
}

Response:
{
  "status": "success",
  "data": {
    "results": [
      {
        "docId": "doc-001",
        "chunkId": "chunk-001",
        "content": "匹配的文本片段...",
        "score": 0.85,
        "title": "文档标题",
        "metadata": {}
      }
    ],
    "total": 5
  }
}
```

#### 2.2.2 场景知识检索（跨知识库）
```
POST /api/v1/scene-groups/{sceneGroupId}/knowledge/search

Request:
{
  "query": "检索查询文本",
  "topK": 5,
  "threshold": 0.7,
  "layers": ["SCENE", "PROFESSIONAL", "GENERAL"]
}

Response:
{
  "status": "success",
  "data": {
    "results": [
      {
        "docId": "doc-001",
        "kbId": "kb-001",
        "kbName": "知识库名称",
        "layer": "SCENE",
        "chunkId": "chunk-001",
        "content": "匹配的文本片段...",
        "score": 0.85,
        "title": "文档标题"
      }
    ],
    "total": 5
  }
}
```

---

### 2.3 Embedding 配置 API

#### 2.3.1 获取Embedding模型列表
```
GET /api/v1/embedding/models

Response:
{
  "status": "success",
  "data": {
    "models": [
      {
        "modelId": "text-embedding-ada-002",
        "displayName": "OpenAI Ada-002",
        "dimensions": 1536,
        "provider": "openai",
        "configured": true
      },
      {
        "modelId": "text-embedding-v2",
        "displayName": "通义千问 Embedding",
        "dimensions": 1536,
        "provider": "qianwen",
        "configured": true
      }
    ],
    "currentModel": "text-embedding-ada-002"
  }
}
```

#### 2.3.2 测试Embedding连接
```
POST /api/v1/embedding/test

Request:
{
  "modelId": "text-embedding-ada-002",
  "text": "测试文本"
}

Response:
{
  "status": "success",
  "data": {
    "success": true,
    "dimensions": 1536,
    "elapsed": 150
  }
}
```

---

### 2.4 索引状态 API

#### 2.4.1 获取索引任务状态
```
GET /api/v1/knowledge-bases/{kbId}/index-status

Response:
{
  "status": "success",
  "data": {
    "kbId": "kb-001",
    "status": "indexing|completed|failed",
    "progress": 75,
    "totalDocs": 100,
    "indexedDocs": 75,
    "failedDocs": 0,
    "currentIndexTime": 1234567890000,
    "estimatedRemaining": 60000,
    "errorMessage": null
  }
}
```

#### 2.4.2 获取文档索引状态
```
GET /api/v1/knowledge-bases/{kbId}/documents/{docId}/index-status

Response:
{
  "status": "success",
  "data": {
    "docId": "doc-001",
    "status": "indexed|pending|indexing|failed",
    "chunkCount": 10,
    "errorMessage": null
  }
}
```

---

## 三、数据模型建议

### 3.1 知识库实体
```java
public class KnowledgeBase {
    private String kbId;
    private String name;
    private String description;
    private String ownerId;
    private String visibility; // private, team, public
    private String embeddingModel;
    private int chunkSize;
    private int chunkOverlap;
    private String vectorStoreType; // milvus, pinecone, etc.
    private String vectorStoreConfig; // JSON配置
    private long documentCount;
    private long createTime;
    private long updateTime;
}
```

### 3.2 文档实体
```java
public class KnowledgeDocument {
    private String docId;
    private String kbId;
    private String title;
    private String content;
    private String source; // upload, text, url
    private String sourceUrl;
    private long fileSize;
    private String fileType;
    private int chunkCount;
    private String status; // pending, indexing, indexed, failed
    private List<String> tags;
    private Map<String, Object> metadata;
    private long createTime;
    private long updateTime;
}
```

### 3.3 文档分块
```java
public class DocumentChunk {
    private String chunkId;
    private String docId;
    private String kbId;
    private String content;
    private float[] embedding;
    private int chunkIndex;
    private int startPosition;
    private int endPosition;
    private Map<String, Object> metadata;
}
```

---

## 四、集成要点

### 4.1 向量数据库选择
建议支持以下向量数据库：
- **Milvus** - 开源，高性能
- **Pinecone** - 云服务，易用
- **Chroma** - 轻量级，适合MVP
- **PGVector** - PostgreSQL扩展

### 4.2 Embedding模型集成
建议支持：
- **OpenAI text-embedding-ada-002**
- **通义千问 text-embedding-v2**
- **百度文心 Embedding**
- **本地模型** (如 sentence-transformers)

### 4.3 文档解析
建议支持：
- PDF解析 (Apache PDFBox)
- Word解析 (Apache POI)
- HTML解析 (Jsoup)
- Markdown解析
- 纯文本

---

## 五、前端调用时机

前端页面将在以下场景调用上述API：

| 页面 | 触发动作 | 调用API |
|------|----------|---------|
| knowledge-base.html | 文档管理Tab | GET documents |
| knowledge-base.html | 添加文本按钮 | POST documents/text |
| knowledge-base.html | 上传文件按钮 | POST documents/upload |
| knowledge-base.html | URL导入按钮 | POST documents/url |
| knowledge-base.html | 重建索引按钮 | POST rebuild-index |
| scene-knowledge.html | 场景知识检索 | POST search |
| LLM对话 | RAG增强 | POST search |

---

## 六、接口状态追踪

| 接口 | 状态 | 负责人 | 预计完成 |
|------|------|--------|----------|
| 文档列表 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 添加文本 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 上传文件 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| URL导入 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 删除文档 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 重新索引文档 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 知识检索 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 场景知识检索 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| Embedding配置 | ✅ 已完成 | MVP团队 | 2026-03-19 |
| 索引状态 | ✅ 已完成 | MVP团队 | 2026-03-19 |

### 已实现的Controller

| Controller | 文件路径 | 说明 |
|------------|----------|------|
| KnowledgeBaseController | `src/main/java/net/ooder/mvp/skill/scene/controller/KnowledgeBaseController.java` | 知识库CRUD + 文档管理 + 检索 |
| SceneKnowledgeController | `src/main/java/net/ooder/mvp/skill/scene/controller/SceneKnowledgeController.java` | 场景知识绑定 + 场景检索 |
| EmbeddingController | `src/main/java/net/ooder/mvp/skill/scene/controller/EmbeddingController.java` | Embedding模型配置 |

### 已实现的DTO

| DTO | 文件路径 | 说明 |
|-----|----------|------|
| KnowledgeDocumentDTO | `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/KnowledgeDocumentDTO.java` | 文档数据传输对象 |
| KnowledgeSearchRequestDTO | `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/KnowledgeSearchRequestDTO.java` | 检索请求参数 |
| KnowledgeSearchResultDTO | `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/KnowledgeSearchResultDTO.java` | 检索结果 |
| EmbeddingModelDTO | `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/EmbeddingModelDTO.java` | Embedding模型信息 |

---

## 七、联系方式

前端开发团队将在API就绪后进行集成测试。

如有接口定义调整需求，请及时沟通。

---

**文档版本**: v1.0  
**创建日期**: 2026-03-19  
**创建人**: MVP前端团队
