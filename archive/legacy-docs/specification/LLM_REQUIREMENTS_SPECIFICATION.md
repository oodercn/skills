# LLM模块需求规格说明书

> 版本: 1.0.0  
> 日期: 2026-03-01  
> 作者: ooder Team

---

## 目录

1. [概述](#1-概述)
2. [知识库完整API设计](#2-知识库完整api设计)
3. [上下文构建器实现](#3-上下文构建器实现)
4. [SQLite-Vec集成方案](#4-sqlite-vec集成方案)
5. [多级LLM配置存储结构](#5-多级llm配置存储结构)
6. [独立模块规划](#6-独立模块规划)
7. [Skills拆分与依赖](#7-skills拆分与依赖)
8. [实施路线图](#8-实施路线图)
9. [知识库建设路径](#9-知识库建设路径)

---

## 1. 概述

### 1.1 背景

LLM模块是ooder平台智能化能力的核心组件，需要支持：
- 企业级与个人级LLM配置
- 场景中的LLM角色执行
- 企业知识库与RAG能力
- 上下文感知的智能助手

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| 模块化 | 每个Skill可独立安装、独立运行 |
| 可扩展 | Provider通过SPI扩展，知识库支持多种存储 |
| 轻量级 | SQLite-Vec作为默认向量存储，无需额外依赖 |
| 多租户 | 支持企业级、个人级、场景级配置隔离 |

### 1.3 用户故事

| 角色 | 场景 | LLM归属 |
|------|------|---------|
| 企业管理员 | 配置企业统一LLM | 企业级 |
| 领导 | 使用个人配置的LLM | 个人级 |
| 场景执行者 | 场景中的LLM角色 | 场景级 |
| 普通用户 | 使用AI助手 | 继承上级配置 |

---

## 2. 知识库完整API设计

### 2.1 知识库管理API

#### 2.1.1 创建知识库

```http
POST /api/kb
Content-Type: application/json

{
  "name": "产品文档库",
  "description": "产品相关文档和技术资料",
  "visibility": "ENTERPRISE",
  "ownerId": "dept-product",
  "config": {
    "chunkSize": 500,
    "chunkOverlap": 50,
    "embeddingModel": "text-embedding-3-small"
  }
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "kbId": "kb-20260301-001",
    "name": "产品文档库",
    "visibility": "ENTERPRISE",
    "docCount": 0,
    "status": "ACTIVE",
    "createTime": 1709251200000
  }
}
```

#### 2.1.2 知识库列表

```http
GET /api/kb?visibility=ENTERPRISE&pageNum=1&pageSize=20
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "kbId": "kb-20260301-001",
        "name": "产品文档库",
        "docCount": 156,
        "chunkCount": 2340,
        "visibility": "ENTERPRISE",
        "status": "ACTIVE"
      }
    ],
    "total": 5,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

#### 2.1.3 更新知识库

```http
PUT /api/kb/{kbId}
Content-Type: application/json

{
  "name": "产品文档库v2",
  "description": "更新后的描述",
  "config": {
    "chunkSize": 800
  }
}
```

#### 2.1.4 删除知识库

```http
DELETE /api/kb/{kbId}?cascade=true
```

### 2.2 文档管理API

#### 2.2.1 上传文档

```http
POST /api/kb/{kbId}/documents
Content-Type: multipart/form-data

file: [文档文件]
metadata: {
  "title": "产品使用手册",
  "category": "manual",
  "tags": ["产品", "使用指南"]
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "docId": "doc-20260301-001",
    "title": "产品使用手册",
    "status": "PENDING",
    "message": "文档已上传，等待处理"
  }
}
```

#### 2.2.2 文档处理状态

```http
GET /api/kb/{kbId}/documents/{docId}/status
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "docId": "doc-20260301-001",
    "status": "INDEXED",
    "progress": 100,
    "chunkCount": 45,
    "processTime": 3200,
    "errors": []
  }
}
```

#### 2.2.3 批量上传

```http
POST /api/kb/{kbId}/documents/batch
Content-Type: multipart/form-data

files: [文件1, 文件2, ...]
options: {
  "autoProcess": true,
  "notifyOnComplete": true
}
```

### 2.3 知识检索API

#### 2.3.1 语义检索

```http
POST /api/kb/{kbId}/search/semantic
Content-Type: application/json

{
  "query": "如何配置场景模板",
  "topK": 5,
  "scoreThreshold": 0.7,
  "filters": {
    "category": "manual",
    "tags": ["配置"]
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "results": [
      {
        "chunkId": "chunk-001",
        "docId": "doc-001",
        "docTitle": "场景配置指南",
        "content": "场景模板配置步骤...",
        "score": 0.92,
        "metadata": {
          "pageNumber": 15,
          "category": "manual"
        }
      }
    ],
    "total": 5,
    "queryTime": 45
  }
}
```

#### 2.3.2 混合检索

```http
POST /api/kb/{kbId}/search/hybrid
Content-Type: application/json

{
  "query": "场景配置",
  "topK": 10,
  "strategy": {
    "semantic": {
      "weight": 0.7,
      "topK": 20
    },
    "keyword": {
      "weight": 0.3,
      "topK": 20
    }
  },
  "rerank": true
}
```

#### 2.3.3 多知识库检索

```http
POST /api/kb/search/cross
Content-Type: application/json

{
  "query": "如何使用LLM进行日志分析",
  "kbIds": ["kb-001", "kb-002", "kb-003"],
  "topK": 5,
  "mergeStrategy": "score"
}
```

### 2.4 RAG查询API

#### 2.4.1 RAG对话

```http
POST /api/rag/query
Content-Type: application/json

{
  "query": "如何配置日志汇报场景",
  "kbIds": ["kb-001"],
  "options": {
    "topK": 5,
    "scoreThreshold": 0.7,
    "includeSources": true,
    "stream": false
  },
  "llmConfig": {
    "provider": "qianwen",
    "model": "qwen-plus",
    "temperature": 0.7
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "response": "根据知识库内容，配置日志汇报场景需要以下步骤...",
    "sources": [
      {
        "docId": "doc-001",
        "docTitle": "场景配置指南",
        "chunkId": "chunk-015",
        "score": 0.92
      }
    ],
    "tokenUsage": {
      "promptTokens": 512,
      "completionTokens": 256,
      "totalTokens": 768
    }
  }
}
```

#### 2.4.2 流式RAG

```http
POST /api/rag/stream
Content-Type: application/json

{
  "query": "分析日志汇报流程",
  "kbIds": ["kb-001"],
  "options": {
    "topK": 5,
    "includeSources": true
  }
}
```

**响应 (SSE):**
```
event: sources
data: {"sources": [...]}

event: chunk
data: {"content": "根据"}

event: chunk
data: {"content": "知识库"}

event: done
data: {"tokenUsage": {...}}
```

### 2.5 API完整列表

| 模块 | 端点 | 方法 | 说明 |
|------|------|------|------|
| **知识库管理** | `/api/kb` | GET | 列出知识库 |
| | `/api/kb` | POST | 创建知识库 |
| | `/api/kb/{kbId}` | GET | 获取知识库详情 |
| | `/api/kb/{kbId}` | PUT | 更新知识库 |
| | `/api/kb/{kbId}` | DELETE | 删除知识库 |
| **文档管理** | `/api/kb/{kbId}/documents` | GET | 列出文档 |
| | `/api/kb/{kbId}/documents` | POST | 上传文档 |
| | `/api/kb/{kbId}/documents/batch` | POST | 批量上传 |
| | `/api/kb/{kbId}/documents/{docId}` | GET | 获取文档详情 |
| | `/api/kb/{kbId}/documents/{docId}` | DELETE | 删除文档 |
| | `/api/kb/{kbId}/documents/{docId}/status` | GET | 处理状态 |
| | `/api/kb/{kbId}/documents/{docId}/reprocess` | POST | 重新处理 |
| **知识检索** | `/api/kb/{kbId}/search/semantic` | POST | 语义检索 |
| | `/api/kb/{kbId}/search/keyword` | POST | 关键词检索 |
| | `/api/kb/{kbId}/search/hybrid` | POST | 混合检索 |
| | `/api/kb/search/cross` | POST | 跨库检索 |
| **RAG** | `/api/rag/query` | POST | RAG查询 |
| | `/api/rag/stream` | POST | 流式RAG |
| | `/api/rag/contexts` | POST | 获取上下文 |

---

## 3. 上下文构建器实现

### 3.1 上下文架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         LLM上下文构建器                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      ContextSource (上下文源)                         │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │    │
│  │  │  PAGE    │ │  SKILL   │ │  SCENE   │ │   USER   │ │ KNOWLEDGE│  │    │
│  │  │  页面    │ │  技能    │ │  场景    │ │   用户   │ │  知识库  │  │    │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      │                                       │
│                                      ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    ContextExtractor (提取器)                          │    │
│  │  ┌──────────────────────────────────────────────────────────────┐   │    │
│  │  │ extract(source): ContextData                                  │   │    │
│  │  │   - 提取结构化数据                                             │   │    │
│  │  │   - 过滤敏感信息                                               │   │    │
│  │  │   - 计算相关性                                                 │   │    │
│  │  └──────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      │                                       │
│                                      ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    ContextMerger (合并器)                             │    │
│  │  ┌──────────────────────────────────────────────────────────────┐   │    │
│  │  │ merge(contexts[]): MergedContext                              │   │    │
│  │  │   - 按优先级排序                                               │   │    │
│  │  │   - 去重和压缩                                                 │   │    │
│  │  │   - Token限制裁剪                                              │   │    │
│  │  └──────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      │                                       │
│                                      ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    ContextFormatter (格式化器)                        │    │
│  │  ┌──────────────────────────────────────────────────────────────┐   │    │
│  │  │ format(context, template): String                             │   │    │
│  │  │   - 生成Prompt                                                │   │    │
│  │  │   - 注入系统提示词                                             │   │    │
│  │  └──────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 上下文数据结构

#### 3.2.1 页面上下文

```java
public class PageContext {
    private String pageType;           // 页面类型: scene-detail, capability-create等
    private String pageUrl;            // 页面URL
    private String pageTitle;          // 页面标题
    private Map<String, Object> pageData;    // 页面数据
    private List<Map<String, Object>> selectedItems;  // 选中项
    private Map<String, Object> formData;    // 表单数据
    private String userAction;         // 用户操作
    private long timestamp;            // 时间戳
}
```

#### 3.2.2 Skill上下文

```java
public class SkillContext {
    private String skillId;            // Skill ID
    private String skillName;          // Skill名称
    private String skillType;          // Skill类型
    private List<String> capabilities; // 能力列表
    private Map<String, Object> manifest;  // Skill清单
    private Map<String, Object> config;    // Skill配置
    private Map<String, Object> params;    // 调用参数
}
```

#### 3.2.3 场景上下文

```java
public class SceneContext {
    private String sceneId;            // 场景ID
    private String sceneGroupId;       // 场景组ID
    private String sceneName;          // 场景名称
    private String templateId;         // 模板ID
    private List<ParticipantInfo> participants;  // 参与者
    private Map<String, Object> workflow;  // 工作流
    private Map<String, Object> variables; // 场景变量
    private String currentStep;        // 当前步骤
}
```

#### 3.2.4 用户上下文

```java
public class UserContext {
    private String userId;             // 用户ID
    private String userName;           // 用户名
    private String department;         // 部门
    private List<String> roles;        // 角色
    private List<String> permissions;  // 权限
    private Map<String, Object> preferences;  // 偏好设置
    private LlmConfig personalLlmConfig;  // 个人LLM配置
}
```

#### 3.2.5 合并后的上下文

```java
public class MergedContext {
    private String contextId;          // 上下文ID
    private List<ContextSource> sources;  // 来源列表
    private Map<String, Object> data;  // 合并后的数据
    private String formattedPrompt;    // 格式化后的Prompt
    private int tokenCount;            // Token数量
    private long createTime;           // 创建时间
    private long expireTime;           // 过期时间
}
```

### 3.3 上下文提取器实现

#### 3.3.1 页面上下文提取器

```java
public interface PageContextExtractor {
    
    String getSourceType();
    
    PageContext extract(HttpServletRequest request);
    
    default boolean shouldExtract(String pageType) {
        return true;
    }
}

@Component
public class SceneDetailPageExtractor implements PageContextExtractor {
    
    @Override
    public String getSourceType() {
        return "scene-detail";
    }
    
    @Override
    public PageContext extract(HttpServletRequest request) {
        PageContext context = new PageContext();
        context.setPageType("scene-detail");
        
        String sceneId = request.getParameter("id");
        if (sceneId != null) {
            SceneDTO scene = sceneService.getScene(sceneId);
            context.setPageData(toMap(scene));
            context.setPageTitle(scene.getName());
        }
        
        return context;
    }
}
```

#### 3.3.2 Skill上下文提取器

```java
@Component
public class SkillContextExtractor {
    
    private final SkillManager skillManager;
    
    public SkillContext extract(String skillId) {
        SkillDefinition skill = skillManager.getSkill(skillId);
        if (skill == null) {
            return null;
        }
        
        SkillContext context = new SkillContext();
        context.setSkillId(skillId);
        context.setSkillName(skill.getName());
        context.setSkillType(skill.getType());
        context.setCapabilities(skill.getCapabilities());
        context.setManifest(skill.getManifest());
        
        return context;
    }
    
    public List<SkillContext> extractAvailableSkills(String category) {
        List<SkillDefinition> skills = skillManager.getSkillsByCategory(category);
        return skills.stream()
            .map(this::toSkillContext)
            .collect(Collectors.toList());
    }
}
```

### 3.4 上下文合并器

```java
@Component
public class ContextMerger {
    
    private static final int MAX_TOKENS = 4096;
    
    public MergedContext merge(List<ContextData> contexts, MergeOptions options) {
        MergedContext merged = new MergedContext();
        merged.setContextId(generateContextId());
        
        // 1. 按优先级排序
        contexts.sort(Comparator.comparingInt(c -> c.getSource().getPriority()));
        
        // 2. 合并数据
        Map<String, Object> mergedData = new LinkedHashMap<>();
        for (ContextData context : contexts) {
            mergeData(mergedData, context.getData(), context.getSource());
        }
        merged.setData(mergedData);
        
        // 3. 格式化为Prompt
        String prompt = formatPrompt(mergedData, options.getPromptTemplate());
        merged.setFormattedPrompt(prompt);
        
        // 4. Token限制裁剪
        int tokens = countTokens(prompt);
        if (tokens > MAX_TOKENS) {
            prompt = truncatePrompt(prompt, MAX_TOKENS);
            merged.setFormattedPrompt(prompt);
            tokens = MAX_TOKENS;
        }
        merged.setTokenCount(tokens);
        
        // 5. 设置过期时间
        merged.setCreateTime(System.currentTimeMillis());
        merged.setExpireTime(merged.getCreateTime() + options.getTtl());
        
        return merged;
    }
    
    private String formatPrompt(Map<String, Object> data, String template) {
        StringBuilder sb = new StringBuilder();
        
        // 系统上下文
        if (data.containsKey("system")) {
            sb.append("[系统信息]\n");
            sb.append(formatSection(data.get("system")));
            sb.append("\n\n");
        }
        
        // 场景上下文
        if (data.containsKey("scene")) {
            sb.append("[场景信息]\n");
            sb.append(formatSection(data.get("scene")));
            sb.append("\n\n");
        }
        
        // 用户上下文
        if (data.containsKey("user")) {
            sb.append("[用户信息]\n");
            sb.append(formatSection(data.get("user")));
            sb.append("\n\n");
        }
        
        // 页面上下文
        if (data.containsKey("page")) {
            sb.append("[页面数据]\n");
            sb.append(formatSection(data.get("page")));
            sb.append("\n\n");
        }
        
        return sb.toString();
    }
}
```

### 3.5 前端集成

#### 3.5.1 页面上下文收集

```javascript
class PageContextCollector {
    constructor() {
        this.contextData = {};
    }
    
    collect() {
        return {
            pageType: this.getPageType(),
            pageUrl: window.location.href,
            pageTitle: document.title,
            pageData: this.getPageData(),
            selectedItems: this.getSelectedItems(),
            formData: this.getFormData(),
            userAction: this.getLastUserAction(),
            timestamp: Date.now()
        };
    }
    
    getPageType() {
        const path = window.location.pathname;
        const match = path.match(/\/pages\/(\w+)\.html/);
        return match ? match[1] : 'unknown';
    }
    
    getPageData() {
        const data = {};
        
        // 从URL参数提取
        const params = new URLSearchParams(window.location.search);
        params.forEach((value, key) => {
            data[key] = value;
        });
        
        // 从页面元素提取
        const dataElements = document.querySelectorAll('[data-context]');
        dataElements.forEach(el => {
            const key = el.getAttribute('data-context');
            data[key] = el.textContent || el.value;
        });
        
        return data;
    }
    
    getSelectedItems() {
        const selected = [];
        document.querySelectorAll('.selected, [data-selected="true"]').forEach(el => {
            selected.push({
                id: el.id || el.getAttribute('data-id'),
                type: el.getAttribute('data-type'),
                name: el.getAttribute('data-name') || el.textContent
            });
        });
        return selected;
    }
    
    getFormData() {
        const forms = document.querySelectorAll('form');
        const formData = {};
        forms.forEach(form => {
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                if (input.name) {
                    formData[input.name] = input.value;
                }
            });
        });
        return formData;
    }
}
```

#### 3.5.2 LLM助手集成

```javascript
class LlmAssistant {
    constructor() {
        this.contextCollector = new PageContextCollector();
        this.contextBuilder = new ContextBuilder();
    }
    
    async buildContext() {
        const pageContext = this.contextCollector.collect();
        
        const response = await fetch('/api/llm/context/build', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                sources: ['page', 'user', 'scene'],
                pageContext: pageContext
            })
        });
        
        return await response.json();
    }
    
    async sendMessage(message) {
        const context = await this.buildContext();
        
        const response = await fetch('/api/llm/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                prompt: message,
                contextId: context.data.contextId,
                includeContext: true
            })
        });
        
        return await response.json();
    }
}
```

### 3.6 API设计

```http
POST /api/llm/context/build
Content-Type: application/json

{
  "sources": ["page", "user", "scene", "skill"],
  "pageContext": {
    "pageType": "scene-detail",
    "pageData": {...}
  },
  "options": {
    "maxTokens": 4096,
    "includeSystemPrompt": true,
    "ttl": 3600000
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "contextId": "ctx-20260301-001",
    "formattedPrompt": "[场景信息]\n...",
    "tokenCount": 512,
    "sources": ["page", "user"],
    "expireTime": 1709254800000
  }
}
```

---

## 4. SQLite-Vec集成方案

### 4.1 技术选型

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **SQLite-Vec** | 零依赖、轻量级、嵌入式 | 性能有限 | 中小规模、单机部署 |
| Milvus | 高性能、分布式 | 部署复杂 | 大规模、分布式 |
| Qdrant | 性能好、易用 | 需要独立部署 | 中等规模 |
| pgvector | 与PostgreSQL集成 | 依赖PostgreSQL | 已有PG环境 |

**选择SQLite-Vec作为默认方案**

### 4.2 依赖配置

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>

<!-- SQLite-Vec扩展 -->
<dependency>
    <groupId>com.github.pgvector</groupId>
    <artifactId>pgvector</artifactId>
    <version>0.1.4</version>
    <scope>provided</scope>
</dependency>
```

### 4.3 数据库Schema

```sql
-- 向量存储表
CREATE TABLE IF NOT EXISTS vectors (
    id TEXT PRIMARY KEY,
    kb_id TEXT NOT NULL,
    doc_id TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding BLOB NOT NULL,
    metadata TEXT,
    create_time INTEGER NOT NULL,
    
    FOREIGN KEY (kb_id) REFERENCES knowledge_bases(kb_id),
    FOREIGN KEY (doc_id) REFERENCES documents(doc_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_vectors_kb_id ON vectors(kb_id);
CREATE INDEX IF NOT EXISTS idx_vectors_doc_id ON vectors(doc_id);

-- 知识库表
CREATE TABLE IF NOT EXISTS knowledge_bases (
    kb_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    owner_id TEXT NOT NULL,
    visibility TEXT DEFAULT 'PRIVATE',
    config TEXT,
    doc_count INTEGER DEFAULT 0,
    chunk_count INTEGER DEFAULT 0,
    status TEXT DEFAULT 'ACTIVE',
    create_time INTEGER NOT NULL,
    update_time INTEGER
);

-- 文档表
CREATE TABLE IF NOT EXISTS documents (
    doc_id TEXT PRIMARY KEY,
    kb_id TEXT NOT NULL,
    title TEXT NOT NULL,
    source TEXT,
    content TEXT,
    metadata TEXT,
    chunk_count INTEGER DEFAULT 0,
    status TEXT DEFAULT 'PENDING',
    process_time INTEGER,
    error_message TEXT,
    create_time INTEGER NOT NULL,
    update_time INTEGER,
    
    FOREIGN KEY (kb_id) REFERENCES knowledge_bases(kb_id)
);
```

### 4.4 向量操作实现

```java
@Component
public class SQLiteVectorStore implements VectorStore {
    
    private final String dbPath;
    private final int dimension;
    
    public SQLiteVectorStore(
        @Value("${vector.db.path:./data/vectors.db}") String dbPath,
        @Value("${vector.dimension:1536}") int dimension
    ) {
        this.dbPath = dbPath;
        this.dimension = dimension;
        initDatabase();
    }
    
    private void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS vectors (...)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_vectors_kb_id ...");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        String sql = "INSERT INTO vectors (id, kb_id, doc_id, chunk_index, content, embedding, metadata, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, (String) metadata.get("kbId"));
            pstmt.setString(3, (String) metadata.get("docId"));
            pstmt.setInt(4, (Integer) metadata.get("chunkIndex"));
            pstmt.setString(5, (String) metadata.get("content"));
            pstmt.setBytes(6, floatArrayToBytes(vector));
            pstmt.setString(7, toJson(metadata));
            pstmt.setLong(8, System.currentTimeMillis());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert vector", e);
        }
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        List<SearchResult> results = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT id, content, embedding, metadata FROM vectors WHERE kb_id = ?");
        
        if (filters.containsKey("docId")) {
            sql.append(" AND doc_id = ?");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            pstmt.setString(1, (String) filters.get("kbId"));
            if (filters.containsKey("docId")) {
                pstmt.setString(2, (String) filters.get("docId"));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            List<ScoredResult> scoredResults = new ArrayList<>();
            while (rs.next()) {
                float[] storedVector = bytesToFloatArray(rs.getBytes("embedding"));
                float score = cosineSimilarity(queryVector, storedVector);
                
                scoredResults.add(new ScoredResult(
                    rs.getString("id"),
                    rs.getString("content"),
                    score,
                    parseJson(rs.getString("metadata"))
                ));
            }
            
            // 排序并取TopK
            scoredResults.sort((a, b) -> Float.compare(b.score, a.score));
            return scoredResults.stream()
                .limit(topK)
                .map(this::toSearchResult)
                .collect(Collectors.toList());
                
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search vectors", e);
        }
    }
    
    private float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        float normA = 0;
        float normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(array.length * 4);
        for (float f : array) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }
    
    private float[] bytesToFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        float[] array = new float[bytes.length / 4];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.getFloat();
        }
        return array;
    }
}
```

### 4.5 性能优化

```java
@Component
public class OptimizedVectorStore extends SQLiteVectorStore {
    
    private final Cache<String, float[]> vectorCache;
    private final ExecutorService executor;
    
    public OptimizedVectorStore() {
        super();
        this.vectorCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();
        this.executor = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        // 使用缓存
        String cacheKey = buildCacheKey(queryVector, filters);
        List<SearchResult> cached = cache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 异步预加载
        CompletableFuture.runAsync(() -> preloadRelatedVectors(filters), executor);
        
        List<SearchResult> results = super.search(queryVector, topK, filters);
        cache.put(cacheKey, results);
        
        return results;
    }
    
    // 批量插入优化
    public void batchInsert(List<VectorData> vectors) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO vectors (id, kb_id, doc_id, chunk_index, content, embedding, metadata, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            for (VectorData v : vectors) {
                pstmt.setString(1, v.getId());
                pstmt.setString(2, v.getKbId());
                pstmt.setString(3, v.getDocId());
                pstmt.setInt(4, v.getChunkIndex());
                pstmt.setString(5, v.getContent());
                pstmt.setBytes(6, floatArrayToBytes(v.getEmbedding()));
                pstmt.setString(7, toJson(v.getMetadata()));
                pstmt.setLong(8, System.currentTimeMillis());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
            
        } catch (SQLException e) {
            throw new RuntimeException("Batch insert failed", e);
        }
    }
}
```

### 4.6 配置参数

```yaml
vector:
  db:
    path: ./data/vectors.db
    dimension: 1536
    pool-size: 10
    
  cache:
    enabled: true
    max-size: 10000
    expire-after-access: 3600000
    
  search:
    default-top-k: 5
    score-threshold: 0.7
    max-results: 100
    
  index:
    type: ivf
    n-lists: 100
    rebuild-interval: 86400000
```

---

## 5. 多级LLM配置存储结构

### 5.1 配置层级设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          LLM配置优先级                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  优先级: 高 ◄──────────────────────────────────────────────────► 低        │
│                                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │SCENE_STEP│  │  SCENE   │  │SCENE_GROUP│  │ PERSONAL │  │ENTERPRISE│     │
│  │  100     │  │   80     │  │    60     │  │    40    │  │    20    │     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
│       │             │             │             │             │             │
│       └─────────────┴─────────────┴─────────────┴─────────────┘             │
│                                   │                                         │
│                                   ▼                                         │
│                          ┌──────────────┐                                  │
│                          │    SYSTEM    │                                  │
│                          │      0       │                                  │
│                          └──────────────┘                                  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 数据模型

#### 5.2.1 配置实体

```java
@Entity
@Table(name = "llm_configs")
public class LlmConfig {
    
    @Id
    private String configId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigLevel configLevel;  // ENTERPRISE, PERSONAL, SCENE_GROUP, SCENE
    
    @Column(nullable = false)
    private String ownerId;  // 企业ID/用户ID/场景组ID/场景ID
    
    @Column(nullable = false)
    private String provider;  // openai, qianwen, deepseek, ollama
    
    private String model;     // gpt-4, qwen-plus
    
    @Column(length = 512)
    private String apiKey;    // 加密存储
    
    private String baseUrl;   // 自定义API地址
    
    @Column(columnDefinition = "TEXT")
    private String options;   // JSON格式的额外配置
    
    @Enumerated(EnumType.STRING)
    private ConfigStatus status;  // ACTIVE, INACTIVE, EXPIRED
    
    private Long expireTime;  // 过期时间
    
    private Long createTime;
    private Long updateTime;
    
    public enum ConfigLevel {
        SYSTEM(0),
        ENTERPRISE(20),
        PERSONAL(40),
        SCENE_GROUP(60),
        SCENE(80),
        SCENE_STEP(100);
        
        private final int priority;
        
        ConfigLevel(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;
        }
    }
}
```

#### 5.2.2 配置解析请求

```java
public class ConfigResolveRequest {
    private String enterpriseId;
    private String userId;
    private String sceneGroupId;
    private String sceneId;
    private String stepId;
    
    private boolean includePersonal;
    private boolean includeEnterprise;
}
```

#### 5.2.3 解析后的配置

```java
public class ResolvedConfig {
    private String configId;
    private ConfigLevel resolvedLevel;
    private String provider;
    private String model;
    private String apiKey;      // 解密后
    private String baseUrl;
    private Map<String, Object> options;
    
    private String sourceConfigId;  // 实际使用的配置ID
    private ConfigLevel sourceLevel;  // 实际使用的配置层级
    private String resolutionPath;  // 解析路径
}
```

### 5.3 配置解析服务

```java
@Service
public class LlmConfigService {
    
    private final LlmConfigRepository configRepository;
    private final EncryptionService encryptionService;
    
    public ResolvedConfig resolveConfig(ConfigResolveRequest request) {
        List<LlmConfig> configs = new ArrayList<>();
        
        // 1. 按优先级收集配置
        if (request.getStepId() != null) {
            configs.add(findConfig(ConfigLevel.SCENE_STEP, request.getStepId()));
        }
        if (request.getSceneId() != null) {
            configs.add(findConfig(ConfigLevel.SCENE, request.getSceneId()));
        }
        if (request.getSceneGroupId() != null) {
            configs.add(findConfig(ConfigLevel.SCENE_GROUP, request.getSceneGroupId()));
        }
        if (request.isIncludePersonal() && request.getUserId() != null) {
            configs.add(findConfig(ConfigLevel.PERSONAL, request.getUserId()));
        }
        if (request.isIncludeEnterprise() && request.getEnterpriseId() != null) {
            configs.add(findConfig(ConfigLevel.ENTERPRISE, request.getEnterpriseId()));
        }
        
        // 2. 系统默认配置
        configs.add(getSystemDefaultConfig());
        
        // 3. 找到第一个有效配置
        for (LlmConfig config : configs) {
            if (config != null && config.getStatus() == ConfigStatus.ACTIVE) {
                return toResolvedConfig(config);
            }
        }
        
        throw new LlmConfigNotFoundException("No valid LLM config found");
    }
    
    private ResolvedConfig toResolvedConfig(LlmConfig config) {
        ResolvedConfig resolved = new ResolvedConfig();
        resolved.setConfigId(config.getConfigId());
        resolved.setResolvedLevel(config.getConfigLevel());
        resolved.setProvider(config.getProvider());
        resolved.setModel(config.getModel());
        resolved.setApiKey(encryptionService.decrypt(config.getApiKey()));
        resolved.setBaseUrl(config.getBaseUrl());
        resolved.setOptions(parseJson(config.getOptions()));
        resolved.setSourceConfigId(config.getConfigId());
        resolved.setSourceLevel(config.getConfigLevel());
        return resolved;
    }
    
    // 保存配置（加密API Key）
    public LlmConfig saveConfig(LlmConfig config) {
        if (config.getApiKey() != null && !config.getApiKey().isEmpty()) {
            config.setApiKey(encryptionService.encrypt(config.getApiKey()));
        }
        config.setUpdateTime(System.currentTimeMillis());
        return configRepository.save(config);
    }
}
```

### 5.4 加密服务

```java
@Service
public class EncryptionService {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;
    
    @Value("${llm.encryption.key}")
    private String encryptionKey;
    
    public String encrypt(String plainText) {
        try {
            byte[] iv = generateIv();
            SecretKey key = deriveKey(encryptionKey);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // IV + encrypted
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            
            return Base64.getEncoder().encodeToString(buffer.array());
            
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt", e);
        }
    }
    
    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_SIZE];
            buffer.get(iv);
            byte[] encrypted = new byte[decoded.length - IV_SIZE];
            buffer.get(encrypted);
            
            SecretKey key = deriveKey(encryptionKey);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt", e);
        }
    }
    
    private SecretKey deriveKey(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "ooder-llm-salt".getBytes(), 65536, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
    
    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
```

### 5.5 API设计

#### 5.5.1 企业配置

```http
GET /api/llm/config/enterprise
```

```http
PUT /api/llm/config/enterprise
Content-Type: application/json

{
  "provider": "qianwen",
  "model": "qwen-plus",
  "apiKey": "sk-xxx",
  "baseUrl": "https://dashscope.aliyuncs.com/api/v1",
  "options": {
    "temperature": 0.7,
    "maxTokens": 4096
  }
}
```

#### 5.5.2 个人配置

```http
GET /api/llm/config/personal

PUT /api/llm/config/personal
Content-Type: application/json

{
  "provider": "deepseek",
  "model": "deepseek-chat",
  "apiKey": "sk-xxx"
}
```

#### 5.5.3 场景配置

```http
GET /api/llm/config/scene/{sceneId}

PUT /api/llm/config/scene/{sceneId}
Content-Type: application/json

{
  "useEnterprise": false,
  "usePersonal": true,
  "provider": "ollama",
  "model": "llama3",
  "baseUrl": "http://localhost:11434"
}
```

#### 5.5.4 配置解析

```http
POST /api/llm/config/resolve
Content-Type: application/json

{
  "enterpriseId": "ent-001",
  "userId": "user-001",
  "sceneGroupId": "sg-001",
  "sceneId": "scene-001",
  "includePersonal": true,
  "includeEnterprise": true
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "configId": "cfg-001",
    "resolvedLevel": "PERSONAL",
    "provider": "deepseek",
    "model": "deepseek-chat",
    "baseUrl": "https://api.deepseek.com/v1",
    "options": {
      "temperature": 0.7
    },
    "sourceConfigId": "cfg-personal-001",
    "sourceLevel": "PERSONAL",
    "resolutionPath": "SCENE_STEP(null) -> SCENE(null) -> SCENE_GROUP(null) -> PERSONAL(active) -> ENTERPRISE(active)"
  }
}
```

---

## 6. 独立模块规划

### 6.1 新建模块列表

| 模块 | 类型 | 优先级 | 说明 |
|------|------|--------|------|
| skill-llm-core | SYSTEM_SERVICE | P0 | LLM核心服务 |
| skill-llm-config-manager | SYSTEM_SERVICE | P1 | 配置管理器 |
| skill-llm-context-builder | SYSTEM_SERVICE | P1 | 上下文构建器 |
| skill-llm-conversation | SYSTEM_SERVICE | P2 | 会话管理 |
| skill-vector-store-sqlite | INFRA_SERVICE | P1 | 向量存储 |
| skill-document-processor | UTILITY_SERVICE | P2 | 文档处理 |
| skill-knowledge-base | BUSINESS_SERVICE | P2 | 知识库管理 |
| skill-rag-engine | BUSINESS_SERVICE | P2 | RAG引擎 |
| skill-llm-assistant-ui | NEXUS_UI | P0 | 助手UI |
| skill-llm-management-ui | NEXUS_UI | P3 | 管理UI |
| skill-knowledge-ui | NEXUS_UI | P3 | 知识库UI |

### 6.2 模块依赖关系

```
skill-llm-assistant-ui
    ├── skill-llm-core
    └── skill-llm-context-builder
            └── skill-llm-core

skill-knowledge-base
    ├── skill-vector-store-sqlite
    └── skill-document-processor

skill-rag-engine
    ├── skill-llm-core
    ├── skill-vector-store-sqlite
    └── skill-knowledge-base (optional)

skill-llm-management-ui
    └── skill-llm-config-manager
            └── skill-llm-core

skill-knowledge-ui
    └── skill-knowledge-base
```

### 6.3 模块目录结构

```
skills/
├── skill-llm-core/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/llm/core/
│   │   ├── controller/
│   │   │   └── LlmController.java
│   │   ├── service/
│   │   │   ├── LlmService.java
│   │   │   └── impl/LlmServiceImpl.java
│   │   ├── provider/
│   │   │   ├── LlmProvider.java (SPI接口)
│   │   │   └── ProviderManager.java
│   │   └── model/
│   │       ├── ChatRequest.java
│   │       └── ChatResponse.java
│   └── src/main/resources/
│       └── skill.yaml
│
├── skill-llm-config-manager/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/llm/config/
│   │   ├── controller/
│   │   │   └── LlmConfigController.java
│   │   ├── service/
│   │   │   ├── LlmConfigService.java
│   │   │   └── EncryptionService.java
│   │   ├── repository/
│   │   │   └── LlmConfigRepository.java
│   │   └── model/
│   │       ├── LlmConfig.java
│   │       └── ResolvedConfig.java
│   └── src/main/resources/
│       └── skill.yaml
│
├── skill-llm-context-builder/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/llm/context/
│   │   ├── controller/
│   │   │   └── ContextController.java
│   │   ├── service/
│   │   │   ├── ContextBuilderService.java
│   │   │   ├── extractor/
│   │   │   │   ├── ContextExtractor.java
│   │   │   │   ├── PageContextExtractor.java
│   │   │   │   ├── SkillContextExtractor.java
│   │   │   │   └── SceneContextExtractor.java
│   │   │   └── merger/
│   │   │       └── ContextMerger.java
│   │   └── model/
│   │       ├── PageContext.java
│   │       ├── SkillContext.java
│   │       ├── SceneContext.java
│   │       └── MergedContext.java
│   └── src/main/resources/
│       └── skill.yaml
│
├── skill-vector-store-sqlite/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/vector/
│   │   ├── service/
│   │   │   ├── VectorStore.java
│   │   │   └── impl/SQLiteVectorStore.java
│   │   └── model/
│   │       ├── VectorData.java
│   │       └── SearchResult.java
│   └── src/main/resources/
│       ├── skill.yaml
│       └── schema.sql
│
├── skill-knowledge-base/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/knowledge/
│   │   ├── controller/
│   │   │   ├── KnowledgeBaseController.java
│   │   │   └── DocumentController.java
│   │   ├── service/
│   │   │   ├── KnowledgeBaseService.java
│   │   │   └── DocumentService.java
│   │   └── model/
│   │       ├── KnowledgeBase.java
│   │       └── KnowledgeDocument.java
│   └── src/main/resources/
│       └── skill.yaml
│
├── skill-rag-engine/
│   ├── pom.xml
│   ├── src/main/java/net/ooder/skill/rag/
│   │   ├── controller/
│   │   │   └── RagController.java
│   │   ├── service/
│   │   │   ├── RagService.java
│   │   │   ├── retrieval/
│   │   │   │   ├── RetrievalStrategy.java
│   │   │   │   ├── SemanticRetrieval.java
│   │   │   │   └── HybridRetrieval.java
│   │   │   └── rerank/
│   │   │       └── Reranker.java
│   │   └── model/
│   │       ├── RagRequest.java
│   │       └── RagResponse.java
│   └── src/main/resources/
│       └── skill.yaml
│
└── skill-llm-assistant-ui/
    ├── skill.yaml
    └── ui/
        ├── components/
        │   ├── assistant.js
        │   ├── context-collector.js
        │   └── styles.css
        └── pages/
            └── index.html
```

---

## 7. Skills拆分与依赖

### 7.1 完整Skills列表

| Skill ID | 名称 | 类型 | 依赖 | 优先级 | 状态 |
|----------|------|------|------|--------|------|
| skill-llm-core | LLM核心服务 | SYSTEM_SERVICE | - | P0 | 需新建 |
| skill-llm-config-manager | LLM配置管理器 | SYSTEM_SERVICE | skill-llm-core | P1 | 需新建 |
| skill-llm-context-builder | 上下文构建器 | SYSTEM_SERVICE | skill-llm-core | P1 | 需新建 |
| skill-llm-conversation | 会话管理 | SYSTEM_SERVICE | skill-llm-core | P2 | 需新建 |
| skill-llm-openai | OpenAI Provider | PROVIDER | skill-llm-core | P0 | 已存在 |
| skill-llm-qianwen | 通义千问 Provider | PROVIDER | skill-llm-core | P0 | 已存在 |
| skill-llm-deepseek | DeepSeek Provider | PROVIDER | skill-llm-core | P1 | 已存在 |
| skill-llm-ollama | Ollama Provider | PROVIDER | skill-llm-core | P1 | 已存在 |
| skill-llm-volcengine | 火山引擎 Provider | PROVIDER | skill-llm-core | P2 | 已存在 |
| skill-vector-store-sqlite | SQLite-Vec存储 | INFRA_SERVICE | - | P1 | 需新建 |
| skill-document-processor | 文档处理器 | UTILITY_SERVICE | - | P2 | 需新建 |
| skill-knowledge-base | 企业知识库 | BUSINESS_SERVICE | skill-vector-store-sqlite, skill-document-processor | P2 | 需新建 |
| skill-rag-engine | RAG引擎 | BUSINESS_SERVICE | skill-llm-core, skill-vector-store-sqlite | P2 | 需新建 |
| skill-llm-assistant-ui | LLM助手UI | NEXUS_UI | skill-llm-core, skill-llm-context-builder | P0 | 需新建 |
| skill-llm-chat-ui | LLM对话UI | NEXUS_UI | skill-llm-core, skill-llm-conversation | P2 | 需新建 |
| skill-llm-management-ui | LLM管理UI | NEXUS_UI | skill-llm-config-manager | P3 | 需新建 |
| skill-knowledge-ui | 知识库UI | NEXUS_UI | skill-knowledge-base | P3 | 需新建 |

### 7.2 安装组合

#### 7.2.1 最小安装

```yaml
installation:
  name: "llm-minimal"
  skills:
    - skill-llm-core
    - skill-llm-openai
    - skill-llm-assistant-ui
```

#### 7.2.2 标准安装

```yaml
installation:
  name: "llm-standard"
  skills:
    - skill-llm-core
    - skill-llm-config-manager
    - skill-llm-context-builder
    - skill-llm-openai
    - skill-llm-qianwen
    - skill-llm-assistant-ui
```

#### 7.2.3 企业知识库安装

```yaml
installation:
  name: "llm-enterprise-kb"
  skills:
    - skill-llm-core
    - skill-llm-config-manager
    - skill-llm-context-builder
    - skill-llm-conversation
    - skill-vector-store-sqlite
    - skill-document-processor
    - skill-knowledge-base
    - skill-rag-engine
    - skill-llm-openai
    - skill-llm-qianwen
    - skill-llm-assistant-ui
    - skill-llm-management-ui
    - skill-knowledge-ui
```

---

## 8. 实施路线图

### 8.1 阶段一：核心基础（P0）

| 任务 | 说明 | 预计工时 |
|------|------|----------|
| skill-llm-core | LLM核心服务重构 | 3天 |
| Provider真实实现 | OpenAI/千问真实API调用 | 2天 |
| skill-llm-assistant-ui | 悬浮助手UI完善 | 2天 |

### 8.2 阶段二：配置与上下文（P1）

| 任务 | 说明 | 预计工时 |
|------|------|----------|
| skill-llm-config-manager | 多级配置管理 | 3天 |
| skill-llm-context-builder | 上下文构建器 | 3天 |
| skill-vector-store-sqlite | SQLite-Vec集成 | 2天 |

### 8.3 阶段三：知识库与RAG（P2）

| 任务 | 说明 | 预计工时 |
|------|------|----------|
| skill-document-processor | 文档解析处理 | 2天 |
| skill-knowledge-base | 知识库管理 | 3天 |
| skill-rag-engine | RAG引擎 | 3天 |
| skill-llm-conversation | 会话管理 | 2天 |

### 8.4 阶段四：UI完善（P3）

| 任务 | 说明 | 预计工时 |
|------|------|----------|
| skill-llm-management-ui | 配置管理UI | 2天 |
| skill-knowledge-ui | 知识库管理UI | 2天 |
| skill-llm-chat-ui | 独立对话页面 | 1天 |

---

## 9. 知识库建设路径

### 9.1 建设原则

采用"个人端优先、企业级渐进"的建设策略：

| 阶段 | 目标用户 | 特点 | 投入 |
|------|----------|------|------|
| 个人端 | 单用户 | 轻量级、快速见效 | 低 |
| 企业级 | 多租户 | 专业治理、高可用 | 高 |

### 9.2 个人端建设

#### 9.2.1 本地文档检索

在Nexus中提供本地端文档文本检索工具：

```http
POST /api/v1/local-search
Content-Type: application/json

{
  "query": "场景配置",
  "sources": ["skills.md", "local-docs"],
  "filters": {
    "fileTypes": [".md", ".txt"],
    "dateRange": {"days": 30}
  },
  "options": {
    "autoClassify": true,
    "topK": 10
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "results": [
      {
        "source": "skills.md",
        "filePath": "/docs/scene-config.md",
        "title": "场景配置指南",
        "snippet": "场景模板配置步骤...",
        "category": "CONFIGURATION",
        "score": 0.92
      }
    ],
    "categories": {
      "CONFIGURATION": 5,
      "TUTORIAL": 3,
      "API": 2
    }
  }
}
```

#### 9.2.2 自然语言操作分类

| 分类类型 | 说明 | 典型输入示例 |
|----------|------|--------------|
| DATA_QUERY | 数据查询 | "查询xxx"、"显示xxx列表" |
| CREATE_ACTION | 创建操作 | "新建xxx"、"创建xxx" |
| FORM_ASSIST | 表单辅助 | "帮我填写"、"这个字段填什么" |
| DOC_SEARCH | 文档检索 | "怎么配置xxx"、"xxx使用说明" |
| SYSTEM_HELP | 系统帮助 | "帮助"、"使用指南" |

**分类API:**
```http
POST /api/v1/nlp/classify
Content-Type: application/json

{
  "text": "帮我创建一个日志汇报场景",
  "context": {
    "pageType": "scene-list",
    "availableActions": ["create", "query", "edit"]
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "classification": {
      "type": "CREATE_ACTION",
      "confidence": 0.95,
      "target": "Scene"
    },
    "extractedParams": {
      "sceneType": "LOG_REPORT"
    },
    "suggestedAction": {
      "type": "NAVIGATE",
      "url": "/pages/scene-create.html?type=LOG_REPORT"
    }
  }
}
```

#### 9.2.3 专业术语映射

建立业务术语与系统概念的映射关系：

| 用户术语 | 系统映射 | 说明 |
|----------|----------|------|
| 日志汇报 | SceneType.LOG_REPORT | 场景类型 |
| 场景模板 | Template | 模板对象 |
| 能力单元 | Capability | 能力对象 |
| 参与者 | Participant | 参与者对象 |
| 工作流 | Workflow | 工作流对象 |
| 审批 | Approval | 审批流程 |

**术语解析API:**
```http
POST /api/v1/term/resolve
Content-Type: application/json

{
  "text": "帮我创建一个日志汇报场景",
  "context": {
    "pageType": "scene-list"
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "resolvedTerms": [
      {"term": "日志汇报", "mappedTo": "SceneType.LOG_REPORT", "confidence": 0.95},
      {"term": "场景", "mappedTo": "Scene", "confidence": 0.98}
    ],
    "intent": {
      "type": "CREATE_ACTION",
      "target": "Scene",
      "params": {"sceneType": "LOG_REPORT"}
    }
  }
}
```

#### 9.2.4 表单填写NLP支持

**场景：用户通过自然语言填写表单**

```
用户输入                          系统解析
─────────────────────────────────────────────────────
"场景名称填日志汇报"        →    sceneName = "日志汇报"
"参与者加上张三和李四"      →    participants += [张三, 李四]
"时间设为每周五下午"        →    schedule = "FRI:14:00"
"模板选日报模板"            →    templateId = "tpl-daily-report"
```

**表单辅助API:**
```http
POST /api/v1/form/assist
Content-Type: application/json

{
  "formId": "scene-create-form",
  "userInput": "参与者加上张三",
  "currentData": {
    "sceneName": "日志汇报",
    "participants": []
  },
  "formSchema": {
    "participants": {
      "type": "user[]",
      "label": "参与者"
    }
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "fieldUpdates": {
      "participants": {
        "action": "APPEND",
        "value": [
          {"userId": "user-001", "userName": "张三"}
        ]
      }
    },
    "suggestions": [
      "是否需要添加更多参与者？",
      "已找到用户'张三'，是否确认添加？"
    ]
  }
}
```

#### 9.2.5 列表检索NLP支持

**场景：用户通过自然语言构建查询条件**

```
用户输入                                    构建查询
─────────────────────────────────────────────────────────────
"显示我创建的日志汇报场景"    →    ?creator=me&type=LOG_REPORT
"查找上周完成的审批"          →    ?status=COMPLETED&start=xxx&end=xxx
"搜索标题包含汇报的场景"      →    ?keyword=汇报
"显示待处理的任务"            →    ?status=PENDING&assignee=me
```

**查询构建API:**
```http
POST /api/v1/query/build
Content-Type: application/json

{
  "text": "显示我创建的日志汇报场景",
  "entityType": "Scene",
  "context": {
    "userId": "user-001"
  }
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "queryUrl": "/api/v1/scenes?creator=user-001&type=LOG_REPORT",
    "filters": [
      {"field": "creator", "operator": "EQ", "value": "user-001"},
      {"field": "type", "operator": "EQ", "value": "LOG_REPORT"}
    ],
    "explanation": "查询您创建的日志汇报类型场景"
  }
}
```

### 9.3 企业级建设

#### 9.3.1 RAG维护管理

| 功能 | 说明 | API |
|------|------|-----|
| 索引重建 | 重建向量索引 | POST /api/kb/{kbId}/index/rebuild |
| 增量更新 | 文档变更后增量更新 | POST /api/kb/{kbId}/index/increment |
| 质量监控 | 检索准确率统计 | GET /api/kb/{kbId}/metrics |
| 版本控制 | 文档版本管理 | GET /api/kb/{kbId}/documents/{docId}/versions |

#### 9.3.2 知识库治理

| 功能 | 说明 | 角色 |
|------|------|------|
| 权限管理 | 谁可见、谁可编辑 | 知识管理员 |
| 审核流程 | 专家审核后发布 | 领域专家 |
| 反馈机制 | 错误标记、更新建议 | 所有用户 |
| 生命周期 | 创建→审核→发布→归档 | 知识管理员 |

#### 9.3.3 Skills分级切换

| 级别 | Skills组合 | 适用场景 |
|------|------------|----------|
| 轻量级 | skill-local-search + skill-nlp-classifier | 个人端、快速部署 |
| 标准级 | + skill-knowledge-base + skill-rag-engine | 小团队、基础RAG |
| 企业级 | + skill-knowledge-governance + skill-vector-store-distributed | 大型企业、高可用 |

### 9.4 实施优先级

| 优先级 | 模块 | 说明 |
|--------|------|------|
| P0 | skill-local-search | 本地文档检索 |
| P0 | skill-nlp-classifier | 自然语言分类 |
| P1 | skill-term-mapper | 专业术语映射 |
| P1 | skill-form-assist | 表单NLP辅助 |
| P1 | skill-query-builder | 查询构建器 |
| P2 | skill-knowledge-base | 企业知识库 |
| P2 | skill-rag-engine | RAG引擎 |
| P3 | skill-knowledge-governance | 知识库治理 |

---

## 附录

### A. LLM SDK API摸底总结

#### A.1 现有Provider实现

| Provider | 模块 | 状态 | 支持模型 |
|----------|------|------|----------|
| OpenAI | skill-llm-openai | ✅ 已实现 | gpt-4, gpt-4-turbo, gpt-4o, gpt-3.5-turbo |
| 通义千问 | skill-llm-qianwen | ✅ 已实现 | qwen-turbo, qwen-plus, qwen-max, qwen2.5系列 |
| DeepSeek | skill-llm-deepseek | ✅ 已实现 | deepseek-chat, deepseek-coder, deepseek-reasoner |
| 火山引擎 | skill-llm-volcengine | ✅ 已实现 | 豆包系列模型 |
| Ollama | skill-llm-ollama | ✅ 已实现 | llama3, mistral, qwen2.5等本地模型 |

#### A.2 LlmProvider接口定义

```java
public interface LlmProvider {
    
    String getProviderType();
    
    List<String> getSupportedModels();
    
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    
    String complete(String model, String prompt, Map<String, Object> options);
    
    List<double[]> embed(String model, List<String> texts);
    
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    
    String summarize(String model, String text, int maxLength);
    
    boolean supportsStreaming();
    
    boolean supportsFunctionCalling();
}
```

#### A.3 LlmController API端点

| 端点 | 方法 | 说明 | 实现状态 |
|------|------|------|----------|
| `/api/llm/chat` | POST | 对话补全 | ✅ 已实现 |
| `/api/llm/chat/stream` | POST | 流式对话(SSE) | ✅ 已实现 |
| `/api/llm/complete` | POST | 文本补全 | ✅ 已实现 |
| `/api/llm/translate` | POST | 翻译 | ✅ 已实现 |
| `/api/llm/summarize` | POST | 摘要 | ✅ 已实现 |
| `/api/llm/providers` | POST | 获取Provider列表 | ✅ 已实现 |
| `/api/llm/models` | POST | 获取可用模型 | ✅ 已实现 |
| `/api/llm/models/set` | POST | 设置当前模型 | ✅ 已实现 |
| `/api/llm/health` | POST | 健康检查 | ✅ 已实现 |

#### A.4 Provider加载机制

```java
private void loadProviders() {
    ServiceLoader<LlmProvider> loader = ServiceLoader.load(LlmProvider.class);
    for (LlmProvider provider : loader) {
        String type = provider.getProviderType();
        providers.put(type, provider);
    }
}
```

**SPI配置路径**: `META-INF/services/net.ooder.scene.skill.LlmProvider`

#### A.5 待完善功能

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 真实API调用 | P0 | 当前为Mock实现，需接入真实API |
| 流式输出优化 | P1 | SSE实现需要真实流式API支持 |
| Function Calling | P1 | 函数调用能力待实现 |
| Embedding存储 | P2 | 向量嵌入结果持久化 |
| Token计费统计 | P2 | 调用成本统计 |
| 多轮对话上下文 | P1 | 会话级上下文管理 |

#### A.6 依赖关系

```
skill-scene (主模块)
    ├── scene-engine (0.7.3) - 可选
    ├── agent-sdk (0.7.3) - 可选
    └── skill-llm-* (0.7.3) - 可选
        ├── skill-llm-openai
        ├── skill-llm-qianwen
        ├── skill-llm-deepseek
        ├── skill-llm-volcengine
        └── skill-llm-ollama
```

---

### B. 配置示例

```yaml
llm:
  core:
    default-provider: qianwen
    default-model: qwen-plus
    timeout: 60000
    max-retries: 3
    
  config:
    encryption:
      algorithm: AES-256-GCM
      key: ${LLM_ENCRYPTION_KEY}
    
  context:
    max-tokens: 4096
    ttl: 3600000
    cache-enabled: true
    
  vector:
    db-path: ./data/vectors.db
    dimension: 1536
    
  knowledge:
    chunk-size: 500
    chunk-overlap: 50
    supported-formats:
      - txt
      - md
      - pdf
      - docx
```

### B. 错误码定义

| 错误码 | 说明 |
|--------|------|
| LLM001 | Provider不可用 |
| LLM002 | API Key无效 |
| LLM003 | 模型不支持 |
| LLM004 | Token超限 |
| LLM005 | 配置未找到 |
| KB001 | 知识库不存在 |
| KB002 | 文档处理失败 |
| KB003 | 向量索引失败 |
| CTX001 | 上下文构建失败 |
| CTX002 | 上下文过期 |

---

### C. 知识资料库设计方案

#### C.1 轻量级(PC)运行方案

| 级别 | Skills组合 | 存储方案 | 向量计算 | 适用场景 |
|------|------------|----------|----------|----------|
| **轻量级** | skill-local-search + skill-nlp-classifier | 本地文件系统 | 无/LLM嵌入 | 个人PC、离线使用 |
| **标准级** | + skill-knowledge-base + skill-rag-engine | SQLite | SQLite-Vec | 小团队、单机部署 |
| **企业级** | + skill-knowledge-governance | PostgreSQL/Milvus | 专业向量库 | 大型企业、分布式 |

**轻量级运行条件：**

| 条件类型 | 要求 |
|----------|------|
| 硬件 | CPU无特殊要求，内存≥4GB，存储≥1GB |
| 软件 | JDK 8+，无需外部数据库/向量库/消息队列 |
| Skills | skill-local-search(必需), skill-nlp-classifier(必需), skill-llm-*(可选) |

**降级策略：**

| 功能 | 降级方案 |
|------|----------|
| 向量检索 | 关键词检索 (BM25/TF-IDF) |
| 语义理解 | LLM本地模型(Ollama) 或 规则匹配 |
| 向量嵌入 | 跳过 或 使用LLM Provider的embed API |
| RAG查询 | 纯检索+LLM生成 |

#### C.2 SDK需扩充的API

**C.2.1 本地文档检索API**

```java
public interface LocalSearchApi {
    SearchResult searchLocal(String query, LocalSearchOptions options);
    List<DocumentInfo> listLocalDocuments(String path, DocumentFilter filter);
    DocumentContent getDocumentContent(String docPath);
    void indexLocalDirectory(String path, IndexOptions options);
    List<String> getSupportedFormats();
    AutoClassifyResult autoClassify(List<DocumentInfo> documents);
}
```

**C.2.2 自然语言分类API**

```java
public interface NlpClassifierApi {
    IntentClassification classifyIntent(String text, ClassifyContext context);
    List<IntentType> getSupportedIntents();
    EntityExtractionResult extractEntities(String text, EntityType[] types);
}

public enum IntentType {
    DATA_QUERY,        // 数据查询
    CREATE_ACTION,     // 创建操作
    FORM_ASSIST,       // 表单辅助
    DOC_SEARCH,        // 文档检索
    SYSTEM_HELP,       // 系统帮助
    UNKNOWN            // 未知意图
}
```

**C.2.3 术语映射API**

```java
public interface TermMappingApi {
    TermResolution resolveTerm(String text, TermContext context);
    void registerTermMapping(String term, String systemConcept, TermMappingOptions options);
    List<TermMapping> getTermMappings(String domain);
    TermMapping getMappingByTerm(String term);
}
```

**C.2.4 表单辅助API**

```java
public interface FormAssistApi {
    FormAssistResult assistForm(FormAssistRequest request);
    FormSchema getFormSchema(String formId);
    List<FieldSuggestion> suggestFieldValues(String formId, String fieldId, String partial);
}
```

**C.2.5 查询构建API**

```java
public interface QueryBuildApi {
    QueryBuildResult buildQuery(QueryBuildRequest request);
    List<FilterSuggestion> suggestFilters(String entityType, String partialQuery);
    QueryValidation validateQuery(String queryUrl);
}
```

#### C.3 个人端工具架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    个人端知识工具架构                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Nexus前端                                                                   │
│  ├── 智能助手    表单辅助    列表检索    文档搜索    术语管理                  │
│                                                                              │
│  skill-local-knowledge                                                       │
│  ├── LocalKnowledgeService (统一服务入口)                                    │
│  │   ├── searchLocal()      本地文档检索                                     │
│  │   ├── classifyIntent()   意图分类                                         │
│  │   ├── resolveTerm()      术语映射                                         │
│  │   ├── assistForm()       表单辅助                                         │
│  │   └── buildQuery()       查询构建                                         │
│                                                                              │
│  底层依赖                                                                    │
│  ├── LocalIndex (本地索引: BM25/文件扫描)                                    │
│  ├── TermDict (术语字典: 内置+用户自定义)                                     │
│  └── LLM Provider (可选: Ollama本地模型/云服务API)                            │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### C.4 模块结构

```
skills/
└── skill-local-knowledge/
    ├── pom.xml
    ├── src/main/java/net/ooder/skill/knowledge/local/
    │   ├── controller/
    │   │   ├── LocalSearchController.java
    │   │   ├── NlpController.java
    │   │   ├── TermController.java
    │   │   └── FormAssistController.java
    │   ├── service/
    │   │   ├── LocalKnowledgeService.java
    │   │   ├── LocalIndexService.java
    │   │   ├── TermMappingService.java
    │   │   └── impl/
    │   ├── indexer/
    │   │   ├── DocumentIndexer.java
    │   │   ├── Bm25Indexer.java
    │   │   └── FileScanner.java
    │   ├── model/
    │   │   ├── LocalDocument.java
    │   │   ├── SearchResult.java
    │   │   ├── TermMapping.java
    │   │   └── IntentClassification.java
    │   └── config/
    └── src/main/resources/
        ├── skill.yaml
        └── terms/
            ├── builtin-terms.json
            └── user-terms.json
```

#### C.5 实施路线图

| 阶段 | 任务 | 优先级 | 负责方 |
|------|------|--------|--------|
| Phase 1 | skill-local-knowledge基础框架 | P0 | 当前项目 |
| | 本地文档扫描与索引 | P0 | 当前项目 |
| | BM25关键词检索 | P0 | 当前项目 |
| Phase 2 | 术语映射服务 | P1 | 当前项目 |
| | 意图分类服务 | P1 | 当前项目 |
| | 内置术语字典 | P1 | 当前项目 |
| Phase 3 | 表单辅助API | P1 | 当前项目 |
| | 查询构建API | P1 | 当前项目 |
| | Nexus前端集成 | P1 | 当前项目 |
| Phase 4 | LLM真实API调用 | P0 | SDK团队 |
| | 流式输出优化 | P1 | SDK团队 |
| | Function Calling | P1 | SDK团队 |
| | 向量检索(SQLite-Vec) | P2 | SDK团队 |

---

**文档结束**
