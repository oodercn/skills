# 知识资料库开发任务清单

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **状态**: 待分配

---

## 一、任务分配原则

| 负责方 | 职责范围 | 交付物 |
|--------|----------|--------|
| **当前项目** | API实现、页面交互、业务逻辑 | Controller、Service、前端组件 |
| **SDK团队** | 核心接口、技术框架、LLM集成 | SDK接口、Provider实现 |
| **独立Skills** | 独立功能模块 | 完整Skill模块 |

---

## 二、当前项目任务清单

### 2.1 Phase 1: 基础框架 (P0)

| 任务ID | 任务名称 | 说明 | 预计工时 | 依赖 |
|--------|----------|------|----------|------|
| P1-001 | 创建skill-local-knowledge模块 | Maven项目结构、skill.yaml | 0.5天 | 无 |
| P1-002 | LocalSearchController | 本地检索API端点 | 1天 | P1-001 |
| P1-003 | LocalIndexService | 文件扫描、索引管理 | 2天 | P1-001 |
| P1-004 | Bm25Indexer | BM25算法实现 | 1.5天 | P1-003 |
| P1-005 | FileScanner | 文件系统扫描 | 1天 | P1-003 |
| P1-006 | 本地检索API测试 | 单元测试、集成测试 | 1天 | P1-002~P1-005 |

**API端点:**

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/local-search` | POST | 本地文档检索 |
| `/api/v1/local-search/index` | POST | 索引指定目录 |
| `/api/v1/local-search/documents` | GET | 列出已索引文档 |

### 2.2 Phase 2: 核心服务 (P1)

| 任务ID | 任务名称 | 说明 | 预计工时 | 依赖 |
|--------|----------|------|----------|------|
| P2-001 | NlpController | 自然语言处理API端点 | 1天 | P1-001 |
| P2-002 | IntentClassifier | 意图分类服务 | 1.5天 | P2-001 |
| P2-003 | TermMappingService | 术语映射服务 | 1.5天 | P1-001 |
| P2-004 | builtin-terms.json | 内置术语字典 | 0.5天 | 无 |
| P2-005 | TermController | 术语管理API端点 | 1天 | P2-003 |
| P2-006 | 核心服务测试 | 单元测试、集成测试 | 1天 | P2-001~P2-005 |

**API端点:**

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/nlp/classify` | POST | 意图分类 |
| `/api/v1/term/resolve` | POST | 术语解析 |
| `/api/v1/terms` | GET | 获取术语列表 |
| `/api/v1/terms` | POST | 注册术语映射 |

### 2.3 Phase 3: 业务集成 (P1)

| 任务ID | 任务名称 | 说明 | 预计工时 | 依赖 |
|--------|----------|------|----------|------|
| P3-001 | FormAssistController | 表单辅助API端点 | 1天 | P2-002, P2-003 |
| P3-002 | FormAssistService | 表单辅助业务逻辑 | 2天 | P3-001 |
| P3-003 | QueryBuildController | 查询构建API端点 | 1天 | P2-002, P2-003 |
| P3-004 | QueryBuildService | 查询构建业务逻辑 | 1.5天 | P3-003 |
| P3-005 | Nexus前端集成 | 智能助手组件集成 | 2天 | P3-001~P3-004 |
| P3-006 | 业务集成测试 | 端到端测试 | 1天 | P3-001~P3-005 |

**API端点:**

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/form/assist` | POST | 表单辅助 |
| `/api/v1/query/build` | POST | 查询构建 |

### 2.4 Phase 4: 前端交互 (P1)

| 任务ID | 任务名称 | 说明 | 预计工时 | 依赖 |
|--------|----------|------|----------|------|
| P4-001 | 智能助手组件增强 | 集成本地知识能力 | 2天 | P3-005 |
| P4-002 | 表单NLP辅助UI | 表单字段智能填充 | 1.5天 | P3-002 |
| P4-003 | 列表检索NLP UI | 自然语言查询构建 | 1.5天 | P3-004 |
| P4-004 | 文档搜索页面 | 本地文档检索界面 | 1天 | P1-002 |
| P4-005 | 术语管理页面 | 用户自定义术语管理 | 1天 | P2-005 |

---

## 三、SDK团队任务清单

### 3.1 LLM核心能力 (P0)

| 任务ID | 任务名称 | 说明 | 预计工时 | 交付物 |
|--------|----------|------|----------|--------|
| SDK-001 | LlmProvider真实实现 | OpenAI/千问真实API调用 | 3天 | Provider实现类 |
| SDK-002 | 流式输出优化 | SSE真实流式API支持 | 2天 | StreamChat实现 |
| SDK-003 | LlmProvider接口扩展 | 新增方法定义 | 1天 | 接口定义 |

### 3.2 向量能力 (P1)

| 任务ID | 任务名称 | 说明 | 预计工时 | 交付物 |
|--------|----------|------|----------|--------|
| SDK-004 | SQLite-Vec集成 | 向量存储实现 | 3天 | VectorStore实现 |
| SDK-005 | Embedding服务 | 文本嵌入服务 | 2天 | EmbeddingService |
| SDK-006 | 向量检索API | 语义检索接口 | 2天 | VectorSearchApi |

### 3.3 高级能力 (P2)

| 任务ID | 任务名称 | 说明 | 预计工时 | 交付物 |
|--------|----------|------|----------|--------|
| SDK-007 | Function Calling | 函数调用能力 | 3天 | FunctionCall实现 |
| SDK-008 | 多轮对话上下文 | 会话级上下文管理 | 2天 | ConversationManager |
| SDK-009 | Token计费统计 | 调用成本统计 | 1天 | UsageStatsService |

---

## 四、独立Skills任务清单

### 4.1 skill-local-knowledge (当前项目负责)

完整的本地知识能力模块，包含：
- 本地文档检索
- 意图分类
- 术语映射
- 表单辅助
- 查询构建

### 4.2 skill-knowledge-base (后续规划)

企业级知识库管理模块，包含：
- 知识库CRUD
- 文档上传处理
- 向量索引管理
- 权限控制

### 4.3 skill-rag-engine (后续规划)

RAG引擎模块，包含：
- 混合检索
- 重排序
- 知识增强生成

---

## 五、接口依赖关系

```
当前项目                    SDK团队
─────────────────────────────────────────────────
skill-local-knowledge
    │
    ├── LocalSearchApi ──────────→ 无依赖
    │
    ├── NlpClassifierApi ─────────→ LlmProvider.chat() (可选)
    │
    ├── TermMappingApi ──────────→ 无依赖
    │
    ├── FormAssistApi ───────────→ LlmProvider.chat() (可选)
    │
    └── QueryBuildApi ───────────→ LlmProvider.chat() (可选)

SDK团队交付物
─────────────────────────────────────────────────
LlmProvider
    ├── chat()          - 对话补全
    ├── complete()      - 文本补全
    ├── embed()         - 文本嵌入
    ├── translate()     - 翻译
    └── summarize()     - 摘要

VectorStore (可选)
    ├── insert()        - 插入向量
    ├── search()        - 向量检索
    └── delete()        - 删除向量
```

---

## 六、里程碑计划

| 里程碑 | 日期 | 交付物 | 负责方 |
|--------|------|--------|--------|
| M1 | Week 1 | skill-local-knowledge基础框架 | 当前项目 |
| M2 | Week 2 | 本地检索+意图分类+术语映射 | 当前项目 |
| M3 | Week 3 | 表单辅助+查询构建+前端集成 | 当前项目 |
| M4 | Week 2 | LLM真实API调用 | SDK团队 |
| M5 | Week 4 | 向量检索(SQLite-Vec) | SDK团队 |

---

## 七、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| SDK交付延迟 | 影响LLM增强功能 | 采用规则匹配降级方案 |
| 向量库集成复杂 | 影响语义检索 | 先用BM25关键词检索 |
| 前端集成工作量 | 影响用户体验 | 分阶段交付，优先核心功能 |

---

**文档结束**
