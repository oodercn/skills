# SkillFlow 节点类型与属性体系设计文档 V5

## 文档信息
- **版本**: 5.0
- **创建日期**: 2026-04-20
- **目的**: 从三个分类维度重新构建SkillNode属性树，设计递进式配置UI，场景+Workflow强绑定

---

## 目录
1. [SkillNode三维度分类体系](#1-skillnode三维度分类体系)
2. [SkillNode属性树形结构](#2-skillnode属性树形结构)
3. [属性默认枚举值设计](#3-属性默认枚举值设计)
4. [递进式配置UI交互方案](#4-递进式配置ui交互方案)
5. [场景+Workflow强绑定设计](#5-场景workflow强绑定设计)
6. [多级多维度管理约束](#6-多级多维度管理约束)
7. [迁移与兼容策略](#7-迁移与兼容策略)

---

## 1. SkillNode三维度分类体系

### 1.1 三维度分类架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SkillNode三维度分类体系                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  维度一：形态 [Form]                                         │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  决定Skill的运行形态和生命周期                               │    │
│  │                                                              │    │
│  │  ├── SCENE      - 场景Skill（容器型，需要激活，有独立上下文）│    │
│  │  └── STANDALONE - 独立Skill（原子型，可直接调用，无状态）    │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  维度二：功能 [Category]                                     │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  决定Skill提供的能力类型                                     │    │
│  │                                                              │    │
│  │  ├── LLM        - 大模型推理（对话、生成、分析）              │    │
│  │  ├── FORM       - 表单交互（数据采集、审批）                  │    │
│  │  ├── SERVICE    - 服务调用（API、工具）                       │    │
│  │  ├── WORKFLOW   - 流程编排（子流程、活动块）                  │    │
│  │  ├── KNOWLEDGE  - 知识管理（RAG、问答、知识库）               │    │
│  │  ├── DATA       - 数据处理（存储、查询、同步）                │    │
│  │  ├── COMM       - 通讯服务（消息、通知、推送）                │    │
│  │  └── TOOL       - 工具服务（文档、报表、转换）                │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  维度三：提供者 [Provider]                                   │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  决定Skill的来源和管理方式                                   │    │
│  │                                                              │    │
│  │  ├── SYSTEM    - 系统内置（核心能力，不可删除）              │    │
│  │  ├── DRIVER    - 驱动适配（第三方平台集成，互斥）            │    │
│  │  ├── BUSINESS  - 业务定制（企业级，可配置）                  │    │
│  │  └── USER      - 用户自定义（个人级，可分享）                │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  三维度组合示例：                                                    │
│  • LLM Provider: STANDALONE + LLM + DRIVER                         │
│  • 知识问答场景: SCENE + KNOWLEDGE + BUSINESS                       │
│  • 审批表单: STANDALONE + FORM + SYSTEM                             │
│  • 工作流引擎: SCENE + WORKFLOW + SYSTEM                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 维度组合矩阵

| Form | Category | Provider | 典型Skill | 说明 |
|------|----------|----------|----------|------|
| STANDALONE | LLM | DRIVER | skill-llm-deepseek | DeepSeek大模型驱动 |
| STANDALONE | LLM | DRIVER | skill-llm-qianwen | 通义千问驱动 |
| STANDALONE | FORM | SYSTEM | skill-form-default | 默认表单能力 |
| STANDALONE | SERVICE | SYSTEM | skill-email | 邮件服务 |
| STANDALONE | SERVICE | DRIVER | skill-im-wecom | 企业微信IM驱动 |
| STANDALONE | DATA | DRIVER | skill-vfs-oss | OSS存储驱动 |
| SCENE | KNOWLEDGE | BUSINESS | skill-knowledge-qa | 知识问答场景 |
| SCENE | WORKFLOW | SYSTEM | skill-bpm | BPM工作流场景 |
| SCENE | WORKFLOW | BUSINESS | skill-approval-form | 审批流程场景 |
| SCENE | KNOWLEDGE | BUSINESS | skill-knowledge-management | 知识管理场景 |

### 1.3 维度约束规则

```
┌─────────────────────────────────────────────────────────────────────┐
│                    维度约束规则                                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Form约束                                                    │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • SCENE形态：必须绑定Workflow，有独立上下文                  │    │
│  │  • STANDALONE形态：可独立调用，无状态                         │    │
│  │  • SCENE + WORKFLOW = 强绑定关系                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Category约束                                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • KNOWLEDGE类：必须关联知识库配置                            │    │
│  │  • WORKFLOW类：必须关联流程定义                               │    │
│  │  • FORM类：必须关联表单Schema                                 │    │
│  │  • LLM类：必须关联模型配置                                    │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Provider约束                                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • SYSTEM：不可删除，不可修改，系统级权限                     │    │
│  │  • DRIVER：同类互斥，需要配置认证信息                         │    │
│  │  • BUSINESS：企业级管理，可配置权限                           │    │
│  │  • USER：个人级管理，可分享给他人                             │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. SkillNode属性树形结构

### 2.1 完整属性树

```
SkillNode属性树
│
├── 基础属性 [basic]
│   ├── skillId: String                    // Skill唯一标识
│   ├── name: String                       // Skill名称
│   ├── description: String                // Skill描述
│   ├── icon: String                       // 图标
│   ├── version: String                    // 版本号
│   └── status: DRAFT | PUBLISHED | ARCHIVED  // 状态
│
├── 分类属性 [classification]
│   │
│   ├── 形态分类 [form]
│   │   ├── form: SCENE | STANDALONE       // 形态类型
│   │   │
│   │   └── SCENE形态专属配置 (form=SCENE)
│   │       ├── sceneType: AUTO | TRIGGER | HYBRID  // 场景类型
│   │       ├── lifecycle: AUTO | TRIGGER | MANUAL  // 生命周期
│   │       ├── activationSteps: [...]     // 激活流程
│   │       └── driverConditions: [...]    // 驱动条件
│   │
│   ├── 功能分类 [category]
│   │   ├── category: LLM | FORM | SERVICE | WORKFLOW | KNOWLEDGE | DATA | COMM | TOOL
│   │   │
│   │   ├── LLM类专属配置 (category=LLM)
│   │   │   ├── provider: deepseek | qianwen | openai | volcengine | ollama | baidu
│   │   │   ├── model: String              // 模型名称
│   │   │   ├── capabilities: [...]        // 能力列表
│   │   │   └── config: { temperature, maxTokens, ... }
│   │   │
│   │   ├── FORM类专属配置 (category=FORM)
│   │   │   ├── formType: STATIC | DYNAMIC | A2UI
│   │   │   ├── formSchema: Object         // 表单Schema
│   │   │   ├── uiConfig: Object           // UI配置
│   │   │   └── validation: Object         // 校验规则
│   │   │
│   │   ├── SERVICE类专属配置 (category=SERVICE)
│   │   │   ├── serviceType: API | TOOL | CAPABILITY
│   │   │   ├── endpoint: Object           // 端点配置
│   │   │   ├── inputSchema: Object        // 输入Schema
│   │   │   └── outputSchema: Object       // 输出Schema
│   │   │
│   │   ├── WORKFLOW类专属配置 (category=WORKFLOW)
│   │   │   ├── workflowType: SUBFLOW | BLOCK | SCENE
│   │   │   ├── refProcessId: String       // 引用流程ID
│   │   │   ├── contextIsolation: {...}    // 上下文隔离
│   │   │   └── ioMapping: {...}           // 输入输出映射
│   │   │
│   │   ├── KNOWLEDGE类专属配置 (category=KNOWLEDGE)
│   │   │   ├── knowledgeType: RAG | QA | MANAGEMENT | SHARE
│   │   │   ├── knowledgeBase: {...}       // 知识库配置
│   │   │   ├── embedding: {...}           // 向量化配置
│   │   │   └── retrieval: {...}           // 检索配置
│   │   │
│   │   ├── DATA类专属配置 (category=DATA)
│   │   │   ├── dataType: STORAGE | QUERY | SYNC
│   │   │   ├── storageType: LOCAL | DATABASE | OSS | S3 | MINIO
│   │   │   └── config: {...}
│   │   │
│   │   ├── COMM类专属配置 (category=COMM)
│   │   │   ├── commType: MESSAGE | NOTIFICATION | PUSH
│   │   │   ├── channels: [...]            // 通道列表
│   │   │   └── templates: [...]           // 模板列表
│   │   │
│   │   └── TOOL类专属配置 (category=TOOL)
│   │       ├── toolType: DOCUMENT | REPORT | CONVERT
│   │       ├── supportedFormats: [...]    // 支持格式
│   │       └── config: {...}
│   │
│   └── 提供者分类 [provider]
│       ├── provider: SYSTEM | DRIVER | BUSINESS | USER
│       │
│       ├── SYSTEM提供者配置 (provider=SYSTEM)
│       │   ├── core: Boolean              // 是否核心
│       │   ├── immutable: Boolean         // 不可修改
│       │   └── permissions: [...]         // 系统权限
│       │
│       ├── DRIVER提供者配置 (provider=DRIVER)
│       │   ├── driverGroup: String        // 驱动组
│       │   ├── exclusive: Boolean         // 是否互斥
│       │   ├── tier: MICRO | SMALL | MEDIUM | LARGE
│       │   ├── authConfig: {...}          // 认证配置
│       │   └── healthCheck: {...}         // 健康检查
│       │
│       ├── BUSINESS提供者配置 (provider=BUSINESS)
│       │   ├── orgId: String              // 组织ID
│       │   ├── permissions: [...]         // 权限配置
│       │   └── audit: {...}               // 审计配置
│       │
│       └── USER提供者配置 (provider=USER)
│           ├── userId: String             // 用户ID
│           ├── shared: Boolean            // 是否分享
│           │   └── sharedWith: [...]      // 分享对象
│           └── visibility: PRIVATE | SHARED | PUBLIC
│
├── 执行属性 [execution]
│   │
│   ├── 执行者配置 [performer]
│   │   ├── performerType: HUMAN | AGENT | SYSTEM
│   │   │
│   │   ├── HUMAN执行者配置 (performerType=HUMAN)
│   │   │   ├── assignee: String           // 执行人
│   │   │   ├── assigneeType: USER | ROLE | DEPT | EXPRESSION
│   │   │   ├── performType: SINGLE | JOINTSIGN | COUNTERSIGN
│   │   │   ├── dueTime: Number            // 时限
│   │   │   └── reminder: {...}            // 提醒配置
│   │   │
│   │   ├── AGENT执行者配置 (performerType=AGENT)
│   │   │   ├── agentId: String            // Agent ID
│   │   │   ├── agentType: LLM | TOOL | HYBRID
│   │   │   ├── capabilities: [...]        // 能力列表
│   │   │   └── constraints: {...}         // 约束条件
│   │   │
│   │   └── SYSTEM执行者配置 (performerType=SYSTEM)
│   │       ├── systemUser: String         // 系统用户
│   │       └── permissions: [...]         // 系统权限
│   │
│   ├── 输入输出配置 [io]
│   │   ├── inputSchema: Object            // 输入Schema
│   │   ├── outputSchema: Object           // 输出Schema
│   │   ├── inputMapping: {...}            // 输入映射
│   │   └── outputMapping: {...}           // 输出映射
│   │
│   └── 执行控制 [control]
│       ├── timeout: Number                // 超时(ms)
│       ├── retry: { maxAttempts, interval, backoff }
│       ├── async: Boolean                 // 异步执行
│       ├── fallback: { skillId, condition }
│       └── onError: CONTINUE | STOP | RETRY | FALLBACK
│
├── 依赖属性 [dependencies]
│   ├── skills: [...]                      // 依赖的Skill
│   ├── services: [...]                    // 依赖的服务
│   ├── resources: {...}                   // 资源需求
│   └── environment: {...}                 // 环境变量
│
├── 上下文属性 [context]
│   │   // 仅SCENE形态有效
│   │
│   ├── 隔离配置 [isolation]
│   │   ├── level: SHARED | PARTIAL | ISOLATED
│   │   ├── variableIsolation: {...}
│   │   ├── dataIsolation: {...}
│   │   └── permissionIsolation: {...}
│   │
│   ├── 角色配置 [roles]
│   │   └── roles: [
│   │         { name, description, permissions, minCount, maxCount }
│   │       ]
│   │
│   ├── 菜单配置 [menus]
│   │   └── menus: [
│   │         { id, name, icon, url, order, roles, visible }
│   │       ]
│   │
│   └── 能力配置 [capabilities]
│       ├── capabilities: [...]            // 能力清单
│       ├── skills: [...]                  // 依赖Skill
│       └── resources: {...}               // 资源配额
│
└── 元数据 [metadata]
    ├── createdBy: String
    ├── createdTime: Timestamp
    ├── modifiedBy: String
    ├── modifiedTime: Timestamp
    ├── tags: [...]
    └── labels: {...}
```

### 2.2 属性继承关系

```
┌─────────────────────────────────────────────────────────────────────┐
│                    属性继承关系                                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  全局Skill定义                                                       │
│  ───────────────────────────────────────────────────────────────    │
│  ├── basic: { skillId, name, description, ... }                     │
│  ├── classification: { form, category, provider }                   │
│  ├── execution: { performer, io, control }                          │
│  └── dependencies: { skills, services, ... }                        │
│                                                                      │
│                              │                                       │
│                              ▼ 继承                                  │
│                                                                      │
│  流程中的Activity节点                                                │
│  ───────────────────────────────────────────────────────────────    │
│  ├── 继承自Skill:                                                    │
│  │   ├── skillId (引用)                                             │
│  │   ├── classification (只读)                                      │
│  │   └── execution.io (可覆盖)                                      │
│  │                                                                  │
│  ├── 节点专属:                                                       │
│  │   ├── nodeId: String                                             │
│  │   ├── name: String (可覆盖Skill名称)                             │
│  │   ├── position: { x, y }                                         │
│  │   └── flowControl: { join, split, condition }                    │
│  │                                                                  │
│  └── 运行时覆盖:                                                     │
│      ├── performer: {...} (可覆盖)                                   │
│      ├── inputMapping: {...} (可覆盖)                               │
│      ├── outputMapping: {...} (可覆盖)                              │
│      └── control.config: {...} (可覆盖)                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. 属性默认枚举值设计

### 3.1 形态分类默认值

| 属性 | 枚举值 | 默认值 | 说明 |
|------|--------|--------|------|
| form | SCENE, STANDALONE | STANDALONE | 大多数Skill是独立形态 |
| sceneType | AUTO, TRIGGER, HYBRID | TRIGGER | 默认触发型场景 |
| lifecycle | AUTO, TRIGGER, MANUAL | TRIGGER | 默认触发型生命周期 |

### 3.2 功能分类默认值

| Category | 默认属性值 |
|----------|-----------|
| **LLM** | provider: deepseek, model: deepseek-chat, temperature: 0.7, maxTokens: 4096 |
| **FORM** | formType: STATIC, validation: enabled |
| **SERVICE** | serviceType: API, timeout: 30000 |
| **WORKFLOW** | workflowType: SUBFLOW, contextIsolation.level: PARTIAL |
| **KNOWLEDGE** | knowledgeType: RAG, embedding.model: text-embedding-ada-002 |
| **DATA** | dataType: STORAGE, storageType: LOCAL |
| **COMM** | commType: MESSAGE, channels: [system] |
| **TOOL** | toolType: DOCUMENT, supportedFormats: [pdf, doc, docx] |

### 3.3 提供者分类默认值

| Provider | 默认属性值 |
|----------|-----------|
| **SYSTEM** | core: false, immutable: true, permissions: [read, execute] |
| **DRIVER** | exclusive: true, tier: MEDIUM, healthCheck.enabled: true |
| **BUSINESS** | permissions: [read, execute, configure], audit.enabled: true |
| **USER** | shared: false, visibility: PRIVATE |

### 3.4 执行者默认值

| PerformerType | 默认属性值 |
|--------------|-----------|
| **HUMAN** | assigneeType: USER, performType: SINGLE, dueTime: 86400000 (24h) |
| **AGENT** | agentType: LLM, capabilities: [] |
| **SYSTEM** | systemUser: system, permissions: [all] |

### 3.5 上下文隔离默认值

| NestingType | contextIsolation.level | 说明 |
|-------------|------------------------|------|
| SUBFLOW | PARTIAL | 子流程部分隔离 |
| BLOCK_NORMAL | SHARED | 活动块共享上下文 |
| SCENE | ISOLATED | 场景完全隔离 |
| EXTERNAL | ISOLATED | 外部流程完全隔离 |

---

## 4. 递进式配置UI交互方案

### 4.1 配置层级设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                    递进式配置层级                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  层级一：基础配置（必填）                                            │
│  ───────────────────────────────────────────────────────────────    │
│  • Skill选择/创建                                                    │
│  • 名称、描述                                                        │
│  • 三维度分类（Form + Category + Provider）                         │
│  • 执行者类型                                                        │
│                                                                      │
│                              ▼                                       │
│                                                                      │
│  层级二：核心配置（根据分类动态显示）                                │
│  ───────────────────────────────────────────────────────────────    │
│  • LLM类：Provider、Model、Temperature、MaxTokens                   │
│  • FORM类：FormType、FormSchema、UIConfig                           │
│  • SERVICE类：Endpoint、InputSchema、OutputSchema                   │
│  • WORKFLOW类：WorkflowType、RefProcessId、ContextIsolation         │
│  • KNOWLEDGE类：KnowledgeType、KnowledgeBase、Embedding             │
│  • ... 其他类别专属配置                                              │
│                                                                      │
│                              ▼                                       │
│                                                                      │
│  层级三：高级配置（可折叠，按需展开）                                │
│  ───────────────────────────────────────────────────────────────    │
│  • 输入输出映射                                                      │
│  • 执行控制（超时、重试、降级）                                      │
│  • 依赖管理                                                          │
│  • 上下文配置（仅SCENE形态）                                         │
│                                                                      │
│                              ▼                                       │
│                                                                      │
│  层级四：专家配置（默认隐藏，高级用户）                              │
│  ───────────────────────────────────────────────────────────────    │
│  • 自定义Schema                                                      │
│  • 脚本扩展                                                          │
│  • 性能调优                                                          │
│  • 调试选项                                                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 面板布局设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SkillNode配置面板                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ▼ 基础配置 [必填]                                           │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  Skill: [选择或创建 ▼]  或  [从模板创建]                     │    │
│  │                                                              │    │
│  │  名称: [________________]                                    │    │
│  │  描述: [________________]                                    │    │
│  │                                                              │    │
│  │  ┌─────────────────────────────────────────────────────┐    │    │
│  │  │  三维度分类                                           │    │    │
│  │  │  ─────────────────────────────────────────────────── │    │    │
│  │  │                                                      │    │    │
│  │  │  形态:   ○ STANDALONE (独立)  ● SCENE (场景)         │    │    │
│  │  │                                                      │    │    │
│  │  │  功能:   [WORKFLOW ▼]                                │    │    │
│  │  │          ┌────────────────────────────────────────┐  │    │    │
│  │  │          │ LLM      - 大模型推理                  │  │    │    │
│  │  │          │ FORM     - 表单交互                    │  │    │    │
│  │  │          │ SERVICE  - 服务调用                    │  │    │    │
│  │  │          │ WORKFLOW - 流程编排  ●                 │  │    │    │
│  │  │          │ KNOWLEDGE - 知识管理                   │  │    │    │
│  │  │          │ DATA     - 数据处理                    │  │    │    │
│  │  │          │ COMM     - 通讯服务                    │  │    │    │
│  │  │          │ TOOL     - 工具服务                    │  │    │    │
│  │  │          └────────────────────────────────────────┘  │    │    │
│  │  │                                                      │    │    │
│  │  │  提供者: [SYSTEM ▼]                                  │    │    │
│  │  │          ┌────────────────────────────────────────┐  │    │    │
│  │  │          │ SYSTEM   - 系统内置  ●                 │  │    │    │
│  │  │          │ DRIVER   - 驱动适配                    │  │    │    │
│  │  │          │ BUSINESS - 业务定制                    │  │    │    │
│  │  │          │ USER     - 用户自定义                  │  │    │    │
│  │  │          └────────────────────────────────────────┘  │    │    │
│  │  │                                                      │    │    │
│  │  └─────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  │  执行者: ○ HUMAN (人)  ● AGENT (智能体)  ○ SYSTEM (系统)    │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ▼ 核心配置 [根据分类动态显示]                               │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  // WORKFLOW类专属配置                                       │    │
│  │  ┌─────────────────────────────────────────────────────┐    │    │
│  │  │  Workflow配置                                        │    │    │
│  │  │  ─────────────────────────────────────────────────── │    │    │
│  │  │                                                      │    │    │
│  │  │  Workflow类型: ○ SUBFLOW  ● BLOCK  ○ SCENE          │    │    │
│  │  │                                                      │    │    │
│  │  │  引用流程: [选择流程 ▼]                              │    │    │
│  │  │                                                      │    │    │
│  │  │  上下文隔离:                                         │    │    │
│  │  │  ┌────────────────────────────────────────────────┐ │    │    │
│  │  │  │ 隔离级别: ● SHARED  ○ PARTIAL  ○ ISOLATED     │ │    │    │
│  │  │  │                                                │ │    │    │
│  │  │  │ 变量隔离:                                      │ │    │    │
│  │  │  │ [✓] 继承父变量  [✓] 暴露本地变量              │ │    │    │
│  │  │  │                                                │ │    │    │
│  │  │  │ 数据隔离:                                      │ │    │    │
│  │  │  │ [✓] 继承表单数据  [ ] 共享本地数据            │ │    │    │
│  │  │  └────────────────────────────────────────────────┘ │    │    │
│  │  │                                                      │    │    │
│  │  └─────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ▶ 高级配置 [可折叠]                                         │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ▶ 专家配置 [默认隐藏]                                       │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  [保存] [取消] [应用] [预览]                                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.3 动态显示规则

```javascript
const panelDisplayRules = {
    'classification.form': {
        SCENE: ['context.isolation', 'context.roles', 'context.menus', 'context.capabilities'],
        STANDALONE: []
    },
    
    'classification.category': {
        LLM: ['categoryConfig.llm'],
        FORM: ['categoryConfig.form'],
        SERVICE: ['categoryConfig.service'],
        WORKFLOW: ['categoryConfig.workflow'],
        KNOWLEDGE: ['categoryConfig.knowledge'],
        DATA: ['categoryConfig.data'],
        COMM: ['categoryConfig.comm'],
        TOOL: ['categoryConfig.tool']
    },
    
    'classification.provider': {
        SYSTEM: ['providerConfig.system'],
        DRIVER: ['providerConfig.driver'],
        BUSINESS: ['providerConfig.business'],
        USER: ['providerConfig.user']
    },
    
    'execution.performerType': {
        HUMAN: ['performerConfig.human'],
        AGENT: ['performerConfig.agent'],
        SYSTEM: ['performerConfig.system']
    },
    
    'categoryConfig.workflow.workflowType': {
        SUBFLOW: ['workflowConfig.subflow'],
        BLOCK: ['workflowConfig.block'],
        SCENE: ['workflowConfig.scene']
    }
};
```

### 4.4 配置验证规则

```javascript
const validationRules = {
    'basic.name': { required: true, minLength: 2, maxLength: 100 },
    'classification.form': { required: true },
    'classification.category': { required: true },
    'classification.provider': { required: true },
    'execution.performerType': { required: true },
    
    'categoryConfig.llm.provider': { 
        required: { when: 'classification.category === LLM' } 
    },
    'categoryConfig.workflow.refProcessId': { 
        required: { when: 'categoryConfig.workflow.workflowType === SUBFLOW' } 
    },
    'context.isolation.level': { 
        required: { when: 'classification.form === SCENE' } 
    }
};
```

---

## 5. 场景+Workflow强绑定设计

### 5.1 强绑定关系

```
┌─────────────────────────────────────────────────────────────────────┐
│                    场景+Workflow强绑定关系                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  核心规则：                                                          │
│  ───────────────────────────────────────────────────────────────    │
│  • Form=SCENE 必须绑定 Category=WORKFLOW                            │
│  • Category=WORKFLOW 的 Skill 必须以 SCENE 形态运行                  │
│  • 场景的多级配置约束到 Workflow 定义中                              │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  场景 [SCENE]                                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  必须绑定 ↓                                                  │    │
│  │                                                              │    │
│  │  ┌─────────────────────────────────────────────────────┐    │    │
│  │  │  Workflow [WORKFLOW]                                 │    │    │
│  │  │  ─────────────────────────────────────────────────── │    │    │
│  │  │                                                      │    │    │
│  │  │  Workflow定义包含：                                  │    │    │
│  │  │  ├── 流程定义 (ProcessDef)                           │    │    │
│  │  │  ├── 活动定义 (ActivityDef)                          │    │    │
│  │  │  ├── 路由定义 (RouteDef)                             │    │    │
│  │  │  └── 场景配置 (SceneConfig) ← 场景多级配置绑定于此   │    │    │
│  │  │                                                      │    │    │
│  │  └─────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 场景配置绑定到Workflow

```
┌─────────────────────────────────────────────────────────────────────┐
│                    场景配置绑定到Workflow                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Workflow定义结构：                                                  │
│  ───────────────────────────────────────────────────────────────    │
│                                                                      │
│  Workflow {                                                          │
│      workflowId: string,                                             │
│      name: string,                                                   │
│      description: string,                                            │
│                                                                      │
│      // 流程定义                                                     │
│      processDef: {                                                   │
│          processDefId: string,                                       │
│          activities: [...],                                          │
│          routes: [...]                                               │
│      },                                                              │
│                                                                      │
│      // 场景配置（从SCENE绑定过来）                                  │
│      sceneConfig: {                                                  │
│          // 场景类型                                                 │
│          sceneType: AUTO | TRIGGER | HYBRID,                         │
│                                                                      │
│          // 生命周期                                                 │
│          lifecycle: {                                                │
│              type: AUTO | TRIGGER | MANUAL,                          │
│              activateTime: timestamp,                                │
│              expireTime: timestamp,                                  │
│              autoDestroy: boolean                                    │
│          },                                                          │
│                                                                      │
│          // 角色系统                                                 │
│          roles: [                                                    │
│              {                                                       │
│                  name: string,                                       │
│                  description: string,                                │
│                  permissions: [...],                                 │
│                  minCount: number,                                   │
│                  maxCount: number                                    │
│              }                                                       │
│          ],                                                          │
│                                                                      │
│          // 菜单系统                                                 │
│          menus: [                                                    │
│              {                                                       │
│                  id: string,                                         │
│                  name: string,                                       │
│                  icon: string,                                       │
│                  url: string,                                        │
│                  order: number,                                      │
│                  roles: [...],                                       │
│                  visible: boolean                                    │
│              }                                                       │
│          ],                                                          │
│                                                                      │
│          // 能力配置                                                 │
│          capabilities: {                                             │
│              skills: [...],                                          │
│              capabilities: [...],                                    │
│              resources: {...}                                        │
│          },                                                          │
│                                                                      │
│          // 激活流程                                                 │
│          activationSteps: [                                          │
│              {                                                       │
│                  stepId: string,                                     │
│                  name: string,                                       │
│                  required: boolean,                                  │
│                  autoExecute: boolean,                               │
│                  actions: [...]                                      │
│              }                                                       │
│          ],                                                          │
│                                                                      │
│          // 驱动条件（AUTO类型）                                     │
│          driverConditions: [                                         │
│              {                                                       │
│                  type: CRON | EVENT | CONDITION,                     │
│                  config: {...}                                       │
│              }                                                       │
│          ],                                                          │
│                                                                      │
│          // 知识库配置（KNOWLEDGE类场景）                            │
│          knowledgeConfig: {                                          │
│              knowledgeBaseId: string,                                │
│              embeddingConfig: {...},                                 │
│              retrievalConfig: {...}                                  │
│          }                                                           │
│      }                                                               │
│  }                                                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.3 场景类型与Workflow绑定规则

| 场景类型 | Workflow绑定规则 | 说明 |
|---------|-----------------|------|
| **AUTO** | 必须绑定driverConditions | 自驱动场景需要定义触发条件 |
| **TRIGGER** | 必须定义入口活动 | 触发型场景需要定义入口 |
| **HYBRID** | 同时支持AUTO和TRIGGER配置 | 混合型场景支持两种模式 |

### 5.4 Workflow中的场景约束

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Workflow中的场景约束                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  约束一：生命周期约束                                                │
│  ───────────────────────────────────────────────────────────────    │
│  • 场景激活 → Workflow实例创建                                       │
│  • 场景暂停 → Workflow实例挂起                                       │
│  • 场景终止 → Workflow实例结束                                       │
│  • 场景销毁 → Workflow实例清理                                       │
│                                                                      │
│  约束二：角色权限约束                                                │
│  ───────────────────────────────────────────────────────────────    │
│  • 场景角色 → 映射到Workflow参与者                                   │
│  • 场景权限 → 约束Workflow活动权限                                   │
│  • 角色变更 → 触发Workflow权限更新                                   │
│                                                                      │
│  约束三：菜单导航约束                                                │
│  ───────────────────────────────────────────────────────────────    │
│  • 场景菜单 → 绑定到Workflow活动入口                                 │
│  • 菜单可见性 → 根据角色和状态动态控制                               │
│  • 菜单操作 → 触发Workflow活动执行                                   │
│                                                                      │
│  约束四：能力配置约束                                                │
│  ───────────────────────────────────────────────────────────────    │
│  • 场景能力 → 注入到Workflow上下文                                   │
│  • 能力依赖 → 自动安装到Workflow环境                                 │
│  • 资源配额 → 限制Workflow资源使用                                   │
│                                                                      │
│  约束五：知识库约束（KNOWLEDGE类场景）                               │
│  ───────────────────────────────────────────────────────────────    │
│  • 知识库配置 → 绑定到Workflow全局上下文                             │
│  • 向量化配置 → 自动应用于知识处理活动                               │
│  • 检索配置 → 约束知识查询活动行为                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 6. 多级多维度管理约束

### 6.1 知识治理库的多级管理

```
┌─────────────────────────────────────────────────────────────────────┐
│                    知识治理库多级管理约束                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  知识治理库需要多级多维度管理：                                      │
│  • 知识库级别：全局/组织/团队/个人                                   │
│  • 知识类型：文档/FAQ/规则/案例                                     │
│  • 访问权限：公开/内部/私密                                         │
│  • 生命周期：创建/更新/归档/删除                                     │
│                                                                      │
│  约束到不同层级：                                                    │
│  ───────────────────────────────────────────────────────────────    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  层级一：全局知识库 [Global Knowledge Base]                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  约束位置：System级 KNOWLEDGE Skill                          │    │
│  │  管理维度：                                                  │    │
│  │  ├── 知识库类型：全局共享知识                                │    │
│  │  ├── 访问权限：公开/内部                                     │    │
│  │  ├── 生命周期：系统管理员管理                                │    │
│  │  └── 配置位置：skill-knowledge (SYSTEM provider)             │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  层级二：组织知识库 [Organization Knowledge Base]            │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  约束位置：Workflow的sceneConfig.knowledgeConfig             │    │
│  │  管理维度：                                                  │    │
│  │  ├── 知识库类型：组织专属知识                                │    │
│  │  ├── 访问权限：组织内部                                      │    │
│  │  ├── 生命周期：组织管理员管理                                │    │
│  │  └── 配置位置：Workflow.sceneConfig.knowledgeConfig          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  层级三：场景知识库 [Scene Knowledge Base]                   │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  约束位置：SCENE形态的WORKFLOW Skill                         │    │
│  │  管理维度：                                                  │    │
│  │  ├── 知识库类型：场景专属知识                                │    │
│  │  ├── 访问权限：场景参与者                                    │    │
│  │  ├── 生命周期：场景生命周期绑定                              │    │
│  │  └── 配置位置：SCENE.sceneConfig.knowledgeConfig             │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  层级四：活动知识库 [Activity Knowledge Base]                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  约束位置：Activity节点的KNOWLEDGE Skill配置                 │    │
│  │  管理维度：                                                  │    │
│  │  ├── 知识库类型：活动专属知识                                │    │
│  │  ├── 访问权限：活动执行者                                    │    │
│  │  ├── 生命周期：活动生命周期绑定                              │    │
│  │  └── 配置位置：Activity.skill.config.knowledgeConfig         │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.2 多级管理约束表

| 管理对象 | 约束层级 | 约束位置 | 生命周期绑定 |
|---------|---------|---------|-------------|
| 全局知识库 | System | skill-knowledge (SYSTEM) | 系统级 |
| 组织知识库 | Workflow | sceneConfig.knowledgeConfig | 流程级 |
| 场景知识库 | SCENE | sceneConfig.knowledgeConfig | 场景级 |
| 活动知识库 | Activity | skill.config.knowledgeConfig | 活动级 |
| 全局角色 | System | skill-role (SYSTEM) | 系统级 |
| 场景角色 | SCENE | sceneConfig.roles | 场景级 |
| 全局菜单 | System | skill-menu (SYSTEM) | 系统级 |
| 场景菜单 | SCENE | sceneConfig.menus | 场景级 |
| 全局能力 | System | skill-capability (SYSTEM) | 系统级 |
| 场景能力 | SCENE | sceneConfig.capabilities | 场景级 |

### 6.3 约束继承规则

```
┌─────────────────────────────────────────────────────────────────────┐
│                    约束继承规则                                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  继承方向：从上到下                                                  │
│  ───────────────────────────────────────────────────────────────    │
│                                                                      │
│  System级约束                                                        │
│      │                                                               │
│      ├── 可被 Workflow 继承（通过引用）                              │
│      │   └── Workflow.sceneConfig 可引用 System级配置               │
│      │                                                               │
│      └── 可被 SCENE 继承（通过引用）                                 │
│          └── SCENE.sceneConfig 可引用 System级配置                  │
│                                                                      │
│  Workflow级约束                                                      │
│      │                                                               │
│      └── 可被 SCENE 继承（通过绑定）                                 │
│          └── SCENE.sceneConfig 绑定到 Workflow.sceneConfig          │
│                                                                      │
│  SCENE级约束                                                         │
│      │                                                               │
│      └── 可被 Activity 继承（通过上下文注入）                        │
│          └── Activity.context 注入 SCENE.sceneConfig               │
│                                                                      │
│  覆盖规则：                                                          │
│  ───────────────────────────────────────────────────────────────    │
│  • 下级可覆盖上级配置（显式声明）                                    │
│  • 未覆盖则继承上级配置                                              │
│  • 覆盖需要权限验证                                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 7. 迁移与兼容策略

### 7.1 旧属性到新属性映射

| 旧属性路径 | 新属性路径 | 说明 |
|-----------|-----------|------|
| execution.type | classification.category | 执行类型迁移到功能分类 |
| skill.form | classification.form | Skill形态 |
| skill.provider | classification.provider | Skill提供者 |
| sceneConfig | 绑定到 Workflow.sceneConfig | 场景配置绑定到Workflow |
| knowledgeConfig | 根据层级约束到不同位置 | 知识库多级管理 |

### 7.2 数据迁移脚本

```javascript
function migrateSkillNodeV4ToV5(oldNode) {
    const newNode = { ...oldNode };
    
    newNode.classification = {
        form: oldNode.form || 'STANDALONE',
        category: oldNode.category || mapOldTypeToCategory(oldNode.execution?.type),
        provider: oldNode.provider || 'SYSTEM'
    };
    
    if (newNode.classification.form === 'SCENE') {
        if (!newNode.classification.category) {
            newNode.classification.category = 'WORKFLOW';
        }
        
        if (newNode.sceneConfig) {
            newNode.workflowConfig = {
                sceneConfig: newNode.sceneConfig
            };
            delete newNode.sceneConfig;
        }
    }
    
    return newNode;
}

function mapOldTypeToCategory(oldType) {
    const mapping = {
        'LLM': 'LLM',
        'FORM': 'FORM',
        'SKILL': 'SERVICE',
        'SERVICE': 'SERVICE',
        'WORKFLOW': 'WORKFLOW',
        'KNOWLEDGE': 'KNOWLEDGE'
    };
    return mapping[oldType] || 'SERVICE';
}
```

---

## 附录

### A. 相关设计文档索引

| 文档名称 | 路径 | 说明 |
|---------|------|------|
| SkillFlow节点类型与属性体系设计文档 V4 | `SkillFlow节点类型与属性体系设计文档V4.md` | V4版本 |
| Skills分类统计报告 | `e:\github\ooder-skills\archive\skills_classification_report.md` | Skills分类 |
| 技能分类框架 | `e:\github\ooder-skills\archive\v2.3.1-docs\archive\scene\skill-classification-framework-v2.md` | 分类框架 |

### B. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| 1.0 | 2026-04-20 | 初始版本 |
| 2.0 | 2026-04-20 | 重新定义核心概念：执行者×执行内容、场景⊂活动块 |
| 3.0 | 2026-04-20 | 新增组网协议层约定设计、定义期概念与流程、详细UI/UE方案 |
| 4.0 | 2026-04-20 | 重大重构：执行体=Skill、嵌套节点与上下文环境设计 |
| 5.0 | 2026-04-20 | **重大重构**：三维度分类体系、递进式配置UI、场景+Workflow强绑定、多级管理约束 |

---

**文档结束**
