# SE协作需求文档 - 知识库模块

**文档编号**: SE-COLLAB-2026-001  
**创建日期**: 2026-03-21  
**优先级**: 高  
**发起方**: MVP前端团队  
**接收方**: SE后端团队  

---

## 一、概述

知识库模块已完成前端页面和基础API开发，现需要SE团队支持以下核心功能的实现。本文档汇总了所有需要SE协作的任务需求。

---

## 二、协作任务清单

### ✅ SDK已提供支持（需配置）

Scene Engine 2.3.1 SDK 已提供以下接口：

| 功能 | SDK接口 | 实现类 |
|------|---------|--------|
| 向量存储 | `VectorStore` | Chroma/Milvus/PgVector/InMemory |
| 嵌入模型 | `SceneEmbeddingService` | OpenAI/Ollama/DashScope |
| 知识库服务 | `KnowledgeBaseService` | `KnowledgeBaseServiceImpl` |
| 术语服务 | `TerminologyService` | `TerminologyServiceImpl` |
| 组织服务 | `KnowledgeOrganizationService` | `IntegratedKnowledgeOrganizationService` |

### 任务1：向量数据库配置（高优先级）

**需求描述**：
配置SDK提供的向量存储实现，选择合适的向量数据库。

**技术要求**：
- 配置 `VectorStoreFactory` 使用指定存储
- 支持 Chroma / Milvus / PgVector / InMemory
- 配置向量维度和索引参数

**配置示例**：
```yaml
scene:
  vector:
    store-type: milvus  # chroma, pgvector, inmemory, json
    milvus:
      host: localhost
      port: 19530
      collection: knowledge_vectors
    embedding:
      model: text-embedding-ada-002
      dimensions: 1536
```

**验收标准**：
- [ ] 向量存储正确配置
- [ ] 能够存储和检索向量
- [ ] 支持相似度搜索

---

### 任务2：嵌入模型配置（高优先级）

**需求描述**：
配置SDK提供的嵌入模型实现。

**技术要求**：
- 配置 `SceneEmbeddingService` 使用指定模型
- 支持 OpenAI / Ollama / DashScope
- 配置API密钥和模型参数

**配置示例**：
```yaml
scene:
  embedding:
    provider: openai  # ollama, dashscope
    openai:
      api-key: ${OPENAI_API_KEY}
      model: text-embedding-3-small
    ollama:
      base-url: http://localhost:11434
      model: nomic-embed-text
```

**验收标准**：
- [ ] 嵌入模型正确配置
- [ ] 能够生成向量
- [ ] 支持模型切换

---

### 任务3：知识库服务集成（中优先级）

**需求描述**：
集成SDK的 `KnowledgeBaseService` 替换本地实现。

**技术要求**：
- 使用SDK的 `KnowledgeBaseServiceImpl`
- 配置持久化存储
- 集成向量检索

**涉及接口**：
```
KnowledgeBaseService
├── create(KnowledgeBaseCreateRequest) → KnowledgeBase
├── get(String kbId) → KnowledgeBase
├── update(KnowledgeBaseUpdateRequest) → KnowledgeBase
├── delete(String kbId) → boolean
├── search(String kbId, KnowledgeSearchRequest) → List<KnowledgeSearchResult>
└── getStats(String kbId) → KnowledgeBaseStats
```

**验收标准**：
- [ ] 知识库CRUD正常
- [ ] 向量检索正常
- [ ] 统计数据准确

---

### 任务4：组织架构同步（低优先级）

**需求描述**：
使用SDK的 `KnowledgeOrganizationService` 同步组织数据。

**技术要求**：
- 使用 `IntegratedKnowledgeOrganizationService`
- 从 skills-org 同步部门结构
- 支持知识库与组织关联

**涉及接口**：
```
KnowledgeOrganizationService
├── syncFromOrg() → SyncResult
├── getOrganizations() → List<KnowledgeOrganization>
└── bindKnowledgeBase(String orgId, String kbId) → boolean
```

**验收标准**：
- [ ] 组织数据同步
- [ ] 知识库关联正常

---

## 三、接口依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                      前端页面层                              │
├─────────────────────────────────────────────────────────────┤
│  knowledge-center.html    knowledge-base.html               │
│  llm-knowledge-config.html  knowledge-search.html           │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关层                               │
├─────────────────────────────────────────────────────────────┤
│  /api/v1/knowledge-bases/*                                  │
│  /api/v1/embedding/*                                        │
│  /api/v1/llm-knowledge-config/*                             │
│  /api/v1/knowledge-organizations/*                          │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      SE服务层 (需协作)                       │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ 向量数据库   │  │ 嵌入模型服务 │  │ 统计聚合服务 │         │
│  │ (任务1)     │  │ (任务2)     │  │ (任务3)     │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│  ┌─────────────┐  ┌─────────────┐                          │
│  │ 组织架构服务 │  │ skills-org  │                          │
│  │ (任务4)     │  │ 整合(任务5) │                          │
│  └─────────────┘  └─────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 四、SDK接口需求

如果SE团队有SDK支持，请提供以下接口：

### KnowledgeBaseService SDK
```java
public interface KnowledgeBaseService {
    void storeVectors(String kbId, List<DocumentVector> vectors);
    List<SearchResult> search(String kbId, String query, int topK, double threshold);
    void deleteVectors(String kbId, List<String> docIds);
}

public class DocumentVector {
    private String docId;
    private String content;
    private float[] vector;
    private Map<String, Object> metadata;
}

public class SearchResult {
    private String docId;
    private String content;
    private double score;
    private Map<String, Object> metadata;
}
```

### EmbeddingService SDK
```java
public interface EmbeddingService {
    float[] embed(String text, String modelId);
    List<float[]> embedBatch(List<String> texts, String modelId);
    int getDimensions(String modelId);
}
```

---

## 五、时间节点

| 任务 | 优先级 | 期望完成时间 |
|------|--------|--------------|
| 任务1：向量数据库集成 | 高 | - |
| 任务2：嵌入模型调用 | 高 | - |
| 任务3：知识库统计聚合 | 中 | - |
| 任务4：组织架构持久化 | 低 | - |
| 任务5：skills-org整合 | 中 | - |

---

## 六、联系方式

- **前端开发**: MVP团队
- **文档位置**: `/docs/se-collaboration/knowledge-module.md`
- **相关代码**:
  - Controller: `src/main/java/net/ooder/mvp/skill/scene/controller/`
  - DTO: `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/`
  - Pages: `src/main/resources/static/console/pages/knowledge-*.html`

---

## 七、附录

### 已实现的API（供参考）

| 接口 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/api/v1/knowledge-bases` | GET/POST | 知识库列表/创建 | ✅ 已实现 |
| `/api/v1/knowledge-bases/{kbId}` | GET/PUT/DELETE | 知识库详情/更新/删除 | ✅ 已实现 |
| `/api/v1/knowledge-bases/{kbId}/documents` | GET | 文档列表 | ✅ 已实现 |
| `/api/v1/knowledge-bases/{kbId}/documents/text` | POST | 添加文本文档 | ✅ 已实现 |
| `/api/v1/knowledge-bases/{kbId}/search` | POST | 知识检索 | ⚠️ 需SE实现 |
| `/api/v1/embedding/config` | GET/PUT | 嵌入配置 | ✅ 已实现 |
| `/api/v1/embedding/test` | POST | 嵌入测试 | ⚠️ 需SE实现 |
| `/api/v1/llm-knowledge-config/dictionaries` | CRUD | 字典表 | ✅ 已实现 |
| `/api/v1/llm-knowledge-config/synonyms` | CRUD | 同义词 | ✅ 已实现 |
| `/api/v1/llm-knowledge-config/interfaces` | CRUD | 接口定义 | ✅ 已实现 |
| `/api/v1/llm-knowledge-config/prompt-templates` | CRUD | 提示词模板 | ✅ 已实现 |
| `/api/v1/knowledge-organizations` | CRUD | 知识组织 | ✅ 已实现 |

---

**文档版本**: v1.0  
**最后更新**: 2026-03-21

---

**请SE团队确认以下事项**：
- [ ] 已阅读并理解所有协作任务
- [ ] 确认各任务的优先级和时间节点
- [ ] 评估技术可行性和资源需求
- [ ] 指定对接负责人

**回复方式**：请在本文档中添加SE团队反馈，或通过邮件/即时通讯工具联系MVP团队。
