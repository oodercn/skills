# LLM模块开发任务清单

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **状态**: 进行中

---

## 一、任务分工原则

| 负责方 | 职责范围 | 交付物 |
|--------|----------|--------|
| **当前项目(SKILLS团队)** | API实现、业务逻辑、前端集成 | Controller、Service、前端组件 |
| **外部协作(前端团队)** | UI组件开发、页面交互 | Nexus前端页面 |
| **后续规划** | 企业级功能、高级特性 | P2/P3阶段模块 |

---

## 二、Phase 0: 核心基础 (已完成 80%)

### 2.1 已完成任务 ✅

| 任务ID | 任务名称 | 模块 | 完成状态 |
|--------|----------|------|----------|
| P0-001 | LLM核心服务 | skill-scene | ✅ 已存在 |
| P0-002 | OpenAI Provider真实实现 | skill-llm-openai | ✅ 已完成 |
| P0-003 | 通义千问 Provider真实实现 | skill-llm-qianwen | ✅ 已完成 |
| P0-004 | Ollama Provider真实实现 | skill-llm-ollama | ✅ 已完成 |
| P0-005 | SQLite向量存储 | skill-vector-sqlite | ✅ 已完成 |
| P0-006 | 安全模块扩展 | skill-security | ✅ 已完成 |
| P0-007 | 本地知识能力 | skill-local-knowledge | ✅ 已完成 |

### 2.2 待完成任务

| 任务ID | 任务名称 | 负责方 | 优先级 | 预计工时 |
|--------|----------|--------|--------|----------|
| P0-008 | LLM助手UI完善 | 外部协作(前端) | P0 | 2天 |
| P0-009 | DeepSeek Provider真实实现 | 当前项目 | P1 | 1天 |
| P0-010 | 火山引擎 Provider真实实现 | 当前项目 | P2 | 1天 |

---

## 三、Phase 1: 配置与上下文 (进行中 33%)

### 3.1 当前项目任务

| 任务ID | 任务名称 | 说明 | 优先级 | 预计工时 | 状态 |
|--------|----------|------|--------|----------|------|
| P1-001 | LlmConfig实体设计 | 多级配置数据模型 | P1 | 0.5天 | ⏳ 待开始 |
| P1-002 | LlmConfigService | 配置解析、保存、加密 | P1 | 2天 | ⏳ 待开始 |
| P1-003 | LlmConfigController | 配置管理API端点 | P1 | 1天 | ⏳ 待开始 |
| P1-004 | ContextSource接口 | 上下文源定义 | P1 | 0.5天 | ⏳ 待开始 |
| P1-005 | ContextExtractor | 页面/场景/用户上下文提取 | P1 | 2天 | ⏳ 待开始 |
| P1-006 | ContextMerger | 上下文合并、Token裁剪 | P1 | 1天 | ⏳ 待开始 |
| P1-007 | ContextController | 上下文构建API | P1 | 0.5天 | ⏳ 待开始 |

### 3.2 外部协作任务

| 任务ID | 任务名称 | 负责方 | 优先级 | 预计工时 |
|--------|----------|--------|--------|----------|
| P1-FE-001 | 配置管理页面 | 外部协作(前端) | P1 | 2天 |
| P1-FE-002 | 个人LLM配置页面 | 外部协作(前端) | P1 | 1天 |
| P1-FE-003 | 上下文调试工具 | 外部协作(前端) | P2 | 1天 |

### 3.3 API端点清单

| 端点 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/api/llm/config/enterprise` | GET/PUT | 企业配置 | ⏳ 待开发 |
| `/api/llm/config/personal` | GET/PUT | 个人配置 | ⏳ 待开发 |
| `/api/llm/config/scene/{sceneId}` | GET/PUT | 场景配置 | ⏳ 待开发 |
| `/api/llm/config/resolve` | POST | 配置解析 | ⏳ 待开发 |
| `/api/llm/context/build` | POST | 构建上下文 | ⏳ 待开发 |

---

## 四、Phase 2: 知识库与RAG (未开始 0%)

### 4.1 当前项目任务

| 任务ID | 任务名称 | 说明 | 优先级 | 预计工时 | 状态 |
|--------|----------|------|--------|----------|------|
| P2-001 | DocumentProcessor | 文档解析、分块 | P2 | 2天 | ⏳ 待开始 |
| P2-002 | KnowledgeBase实体 | 知识库数据模型 | P2 | 0.5天 | ⏳ 待开始 |
| P2-003 | KnowledgeBaseService | 知识库CRUD | P2 | 2天 | ⏳ 待开始 |
| P2-004 | DocumentService | 文档管理 | P2 | 2天 | ⏳ 待开始 |
| P2-005 | RetrievalStrategy | 检索策略接口 | P2 | 1天 | ⏳ 待开始 |
| P2-006 | SemanticRetrieval | 语义检索实现 | P2 | 1天 | ⏳ 待开始 |
| P2-007 | HybridRetrieval | 混合检索实现 | P2 | 1天 | ⏳ 待开始 |
| P2-008 | RagEngine | RAG引擎核心 | P2 | 2天 | ⏳ 待开始 |
| P2-009 | ConversationManager | 会话管理 | P2 | 2天 | ⏳ 待开始 |

### 4.2 外部协作任务

| 任务ID | 任务名称 | 负责方 | 优先级 | 预计工时 |
|--------|----------|--------|--------|----------|
| P2-FE-001 | 知识库管理页面 | 外部协作(前端) | P2 | 3天 |
| P2-FE-002 | 文档上传组件 | 外部协作(前端) | P2 | 1天 |
| P2-FE-003 | RAG测试工具 | 外部协作(前端) | P2 | 1天 |
| P2-FE-004 | 会话历史页面 | 外部协作(前端) | P2 | 1天 |

### 4.3 API端点清单

| 端点 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/api/kb` | GET/POST | 知识库列表/创建 | ⏳ 待开发 |
| `/api/kb/{kbId}` | GET/PUT/DELETE | 知识库详情/更新/删除 | ⏳ 待开发 |
| `/api/kb/{kbId}/documents` | GET/POST | 文档列表/上传 | ⏳ 待开发 |
| `/api/kb/{kbId}/documents/batch` | POST | 批量上传 | ⏳ 待开发 |
| `/api/kb/{kbId}/search/semantic` | POST | 语义检索 | ⏳ 待开发 |
| `/api/kb/{kbId}/search/hybrid` | POST | 混合检索 | ⏳ 待开发 |
| `/api/rag/query` | POST | RAG查询 | ⏳ 待开发 |
| `/api/rag/stream` | POST | 流式RAG | ⏳ 待开发 |

---

## 五、Phase 3: UI完善 (未开始 0%)

### 5.1 外部协作任务

| 任务ID | 任务名称 | 负责方 | 优先级 | 预计工时 |
|--------|----------|--------|--------|----------|
| P3-FE-001 | LLM管理后台 | 外部协作(前端) | P3 | 3天 |
| P3-FE-002 | 知识库统计仪表盘 | 外部协作(前端) | P3 | 2天 |
| P3-FE-003 | 独立对话页面 | 外部协作(前端) | P3 | 1天 |
| P3-FE-004 | 使用统计报表 | 外部协作(前端) | P3 | 2天 |

---

## 六、模块依赖关系

```
Phase 0 (已完成)
├── skill-scene (核心)
├── skill-llm-openai ✅
├── skill-llm-qianwen ✅
├── skill-llm-ollama ✅
├── skill-vector-sqlite ✅
├── skill-security ✅
└── skill-local-knowledge ✅

Phase 1 (进行中)
├── skill-llm-config-manager (待开发)
│   └── 依赖: skill-security (加密服务)
└── skill-llm-context-builder (待开发)
    └── 依赖: skill-scene (场景数据)

Phase 2 (待开发)
├── skill-document-processor (待开发)
├── skill-knowledge-base (待开发)
│   └── 依赖: skill-vector-sqlite, skill-document-processor
├── skill-rag-engine (待开发)
│   └── 依赖: skill-llm-core, skill-vector-sqlite, skill-knowledge-base
└── skill-llm-conversation (待开发)
    └── 依赖: skill-llm-core
```

---

## 七、里程碑计划

| 里程碑 | 目标日期 | 交付物 | 验收标准 |
|--------|----------|--------|----------|
| **M1** | Week 1 | Phase 0完成 | 所有Provider可用 |
| **M2** | Week 2 | Phase 1后端 | 配置管理+上下文构建API可用 |
| **M3** | Week 3 | Phase 1前端 | 配置管理页面可用 |
| **M4** | Week 4 | Phase 2后端 | 知识库+RAG API可用 |
| **M5** | Week 5 | Phase 2前端 | 知识库管理页面可用 |
| **M6** | Week 6 | Phase 3 | 完整管理后台 |

---

## 八、当前优先任务

### 本周任务 (Week 1)

| 任务ID | 任务名称 | 负责方 | 状态 |
|--------|----------|--------|------|
| P0-009 | DeepSeek Provider真实实现 | 当前项目 | ⏳ 待开始 |
| P1-001 | LlmConfig实体设计 | 当前项目 | ⏳ 待开始 |
| P1-002 | LlmConfigService | 当前项目 | ⏳ 待开始 |

### 下周任务 (Week 2)

| 任务ID | 任务名称 | 负责方 | 状态 |
|--------|----------|--------|------|
| P1-003 | LlmConfigController | 当前项目 | ⏳ 待开始 |
| P1-004 | ContextSource接口 | 当前项目 | ⏳ 待开始 |
| P1-005 | ContextExtractor | 当前项目 | ⏳ 待开始 |

---

## 九、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 前端资源不足 | 影响UI交付 | 后端先行，提供API文档 |
| API Key获取困难 | 影响Provider测试 | 使用Mock模式开发 |
| 向量检索性能 | 影响用户体验 | 优化索引，添加缓存 |

---

## 十、文档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-01 | v1.0 | 初始版本，基于LLM需求规格文档创建 |

---

**文档结束**
