# SE协作任务说明 - 知识库模块扩展

## 一、背景说明

知识库模块已完成前端页面和基础API开发，现需要SE团队支持以下核心功能的实现。

## 二、协作任务清单

### 任务1：向量数据库集成（高优先级）

**需求描述**：
当前知识库的向量存储和检索功能为模拟实现，需要集成真实的向量数据库。

**技术要求**：
- 支持主流向量数据库（Milvus/Pinecone/Chroma/PGVector等）
- 提供统一的向量存储接口
- 支持向量的CRUD操作
- 支持相似度检索（余弦相似度/欧氏距离）

**涉及接口**：
```
POST /api/v1/knowledge-bases/{kbId}/search
```

**期望返回格式**：
```json
{
  "status": "success",
  "data": {
    "results": [
      {
        "docId": "doc-001",
        "title": "文档标题",
        "content": "匹配的内容片段",
        "score": 0.85,
        "kbId": "kb-001",
        "kbName": "技术文档库",
        "layer": "GENERAL"
      }
    ],
    "total": 10
  }
}
```

**验收标准**：
- [ ] 向量数据能够正确存储和检索
- [ ] 检索结果按相似度排序
- [ ] 支持topK和threshold参数

---

### 任务2：嵌入模型调用（高优先级）

**需求描述**：
当前嵌入模型测试返回模拟数据，需要实现真实的嵌入模型调用。

**技术要求**：
- 支持OpenAI嵌入模型（text-embedding-ada-002, text-embedding-3-small/large）
- 支持本地嵌入模型（BGE系列）
- 支持通义千问嵌入模型
- 提供模型配置和切换机制

**涉及接口**：
```
POST /api/v1/embedding/test
```

**期望返回格式**：
```json
{
  "status": "success",
  "data": {
    "success": true,
    "dimensions": 1536,
    "sampleVector": [0.1234, -0.5678, ...],
    "textLength": 100,
    "elapsed": 150
  }
}
```

**验收标准**：
- [ ] 能够调用OpenAI API获取真实向量
- [ ] 能够调用本地模型获取向量
- [ ] 向量维度与模型配置一致
- [ ] 错误处理完善（API限流、网络错误等）

---

### 任务3：知识库统计聚合（中优先级）

**需求描述**：
知识中心首页需要展示统计数据，当前部分数据为占位符。

**技术要求**：
- 统计知识库总数
- 统计文档总数
- 统计场景绑定数（知识库被多少场景引用）
- 统计当前嵌入模型

**涉及接口**：
```
GET /api/v1/knowledge-bases/stats
```

**期望返回格式**：
```json
{
  "status": "success",
  "data": {
    "totalKb": 15,
    "totalDocs": 1250,
    "totalBindings": 28,
    "embeddingModel": "text-embedding-ada-002",
    "layerStats": {
      "GENERAL": 5,
      "PROFESSIONAL": 7,
      "SCENE": 3
    }
  }
}
```

**验收标准**：
- [ ] 统计数据准确
- [ ] 支持按层级统计

---

### 任务4：组织架构持久化（低优先级）

**需求描述**：
业务知识库页面的组织架构（公司级/部门级/专用业务）当前为前端硬编码，需要持久化支持。

**技术要求**：
- 支持组织节点的CRUD
- 支持知识库与组织节点的关联
- 支持组织节点排序

**涉及接口**：
```
GET    /api/v1/knowledge-organizations
POST   /api/v1/knowledge-organizations
PUT    /api/v1/knowledge-organizations/{orgId}
DELETE /api/v1/knowledge-organizations/{orgId}
POST   /api/v1/knowledge-organizations/{orgId}/knowledge-bases/{kbId}
DELETE /api/v1/knowledge-organizations/{orgId}/knowledge-bases/{kbId}
```

**期望数据结构**：
```json
{
  "orgId": "org-001",
  "name": "公司级",
  "type": "company",
  "parentId": null,
  "sort": 1,
  "description": "全公司共享知识",
  "icon": "ri-building-2-line",
  "knowledgeBases": ["kb-001", "kb-002"],
  "totalDocs": 150
}
```

**验收标准**：
- [ ] 组织架构可动态配置
- [ ] 知识库可关联到组织节点
- [ ] 支持从skills-org同步组织数据

---

### 任务5：skills-org整合（中优先级）

**需求描述**：
知识库的组织架构需要与系统组织管理模块（skills-org）打通，实现数据同步。

**技术要求**：
- 从`/api/v1/org/departments`同步部门数据
- 支持部门与知识库的关联
- 支持按部门权限控制知识库访问

**数据映射**：
```
skills-org Department → Knowledge Organization
├── departmentId → orgId
├── name → name
├── parentId → parentId
└── type = "department"
```

**涉及接口**：
```
POST /api/v1/knowledge-organizations/sync-from-org
GET  /api/v1/knowledge-organizations/{orgId}/members
```

**验收标准**：
- [ ] 可从组织管理同步部门结构
- [ ] 部门成员可访问关联的知识库
- [ ] 支持权限继承

---

## 三、接口依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                      前端页面层                              │
├─────────────────────────────────────────────────────────────┤
│  knowledge-center.html    business-knowledge.html           │
│  knowledge-base.html      llm-knowledge-config.html         │
│  knowledge-search.html                                    │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关层                               │
├─────────────────────────────────────────────────────────────┤
│  /api/v1/knowledge-bases/*                                  │
│  /api/v1/embedding/*                                        │
│  /api/v1/llm-knowledge-config/*                             │
│  /api/v1/knowledge-organizations/*  (待实现)                │
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
│  ┌─────────────┐                                          │
│  │ 组织架构服务 │                                          │
│  │ (任务4)     │                                          │
│  └─────────────┘                                          │
└─────────────────────────────────────────────────────────────┘
```

## 四、SDK接口需求

如果SE团队有SDK支持，请提供以下接口：

### KnowledgeBaseService SDK
```java
public interface KnowledgeBaseService {
    // 向量存储
    void storeVectors(String kbId, List<DocumentVector> vectors);
    
    // 向量检索
    List<SearchResult> search(String kbId, String query, int topK, double threshold);
    
    // 删除向量
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
    // 获取嵌入向量
    float[] embed(String text, String modelId);
    
    // 批量嵌入
    List<float[]> embedBatch(List<String> texts, String modelId);
    
    // 获取模型维度
    int getDimensions(String modelId);
}
```

## 五、时间节点

| 任务 | 优先级 | 期望完成时间 |
|------|--------|--------------|
| 任务1：向量数据库集成 | 高 | - |
| 任务2：嵌入模型调用 | 高 | - |
| 任务3：知识库统计聚合 | 中 | - |
| 任务4：组织架构持久化 | 低 | - |

## 六、联系方式

- 前端开发：[MVP团队]
- 文档位置：`/docs/se-collaboration/knowledge-module.md`
- 相关代码：
  - Controller: `src/main/java/net/ooder/mvp/skill/scene/controller/`
  - DTO: `src/main/java/net/ooder/mvp/skill/scene/dto/knowledge/`
  - Pages: `src/main/resources/static/console/pages/knowledge-*.html`

---

**文档版本**: v1.0  
**创建日期**: 2026-03-21  
**最后更新**: 2026-03-21
