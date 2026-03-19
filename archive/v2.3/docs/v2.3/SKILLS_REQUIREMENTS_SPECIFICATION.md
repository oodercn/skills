# Skills 需求规格说明书

> **版本**: v2.3.5  
> **日期**: 2026-03-06  
> **状态**: 正式发布  
> **遵循规范**: new-feature-guide

---

## 一、概述

### 1.1 文档目的

本文档从 Skills 视角出发，对 Ooder 平台的技能系统进行完整的需求规格分析，包括：
- 用户组织机构集成
- 知识库集成
- 用户 UI 界面
- 依赖项目清单

### 1.2 适用范围

| 范围 | 说明 |
|------|------|
| Skills 数量 | 88+ |
| 场景技能 | 18 (ABS/ASS/TBS) |
| 服务技能 | 70+ (SVC) |
| UI 技能 | 12 |

### 1.3 三闭环检查清单

```
□ 生命周期闭环：创建→查询→更新→删除 API完整
□ 数据实体闭环：关系明确、级联处理、数据一致
□ 按钮API闭环：每个操作都调用后端、操作后刷新
```

---

## 二、用户组织机构

### 2.1 组织架构技能体系

```
┌─────────────────────────────────────────────────────────────────┐
│                      组织架构技能体系                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐                                            │
│  │ skill-user-auth │ ◄── 用户认证入口                            │
│  │  用户认证服务    │                                            │
│  └────────┬────────┘                                            │
│           │                                                     │
│           ▼                                                     │
│  ┌─────────────────┐                                            │
│  │ skill-org-base  │ ◄── 本地组织（降级方案）                     │
│  │  本地组织服务    │                                            │
│  └────────┬────────┘                                            │
│           │                                                     │
│           ├──────────────────┬──────────────────┐               │
│           ▼                  ▼                  ▼               │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐   │
│  │skill-org-dingding│ │skill-org-feishu │ │skill-org-wecom  │   │
│  │   钉钉集成       │ │   飞书集成       │ │  企业微信集成    │   │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘   │
│           │                  │                  │               │
│           └──────────────────┼──────────────────┘               │
│                              ▼                                  │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐   │
│  │skill-org-ldap   │ │skill-access-    │ │skill-security   │   │
│  │   LDAP集成      │ │   control       │ │   安全管理      │   │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 组织技能详细规格

#### 2.2.1 skill-user-auth (用户认证服务)

| 属性 | 值 |
|------|-----|
| ID | skill-user-auth |
| 版本 | 0.7.3 |
| 类型 | service-skill |
| 优先级 | P0 (核心) |

**能力清单**:

| 能力ID | 名称 | 分类 | 说明 |
|--------|------|------|------|
| user-auth | 用户认证 | authentication | 用户登录认证 |
| token-validate | Token验证 | authentication | Token有效性验证 |
| session-manage | 会话管理 | session | 用户会话管理 |

**API 闭环检查**:

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 登录 | `login()` | `POST /api/auth/login` | UserAuthController.login() | ✅ |
| 登出 | `logout()` | `POST /api/auth/logout` | UserAuthController.logout() | ✅ |
| 验证 | `validate()` | `POST /api/auth/validate` | UserAuthController.validate() | ✅ |
| 刷新 | `refresh()` | `POST /api/auth/refresh` | UserAuthController.refresh() | ✅ |

**配置项**:

| 配置名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| AUTH_TOKEN_EXPIRE | number | 3600 | Token过期时间(秒) |
| AUTH_REFRESH_ENABLED | boolean | true | 是否启用Token刷新 |

---

#### 2.2.2 skill-org-base (本地组织服务)

| 属性 | 值 |
|------|-----|
| ID | skill-org-base |
| 版本 | 1.0.0 |
| 类型 | service-skill |
| 优先级 | P0 (核心/降级方案) |

**能力清单**:

| 能力ID | 名称 | 分类 | 说明 |
|--------|------|------|------|
| user.auth | 用户认证 | auth | 用户认证能力 |
| user.manage | 用户管理 | management | 用户CRUD操作 |
| org.manage | 组织管理 | management | 组织架构管理 |
| role.manage | 角色管理 | management | 角色权限管理 |
| sync | 数据同步 | sync | 数据同步能力 |

**API 闭环检查**:

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 登录 | `login()` | `POST /api/org/login` | OrgController.login() | ✅ |
| 登出 | `logout()` | `POST /api/org/logout` | OrgController.logout() | ✅ |
| 用户列表 | `listUsers()` | `GET /api/org/users` | OrgController.listUsers() | ✅ |
| 用户详情 | `getUser()` | `GET /api/org/users/{userId}` | OrgController.getUser() | ✅ |
| 组织树 | `getOrgTree()` | `GET /api/org/tree` | OrgController.getOrgTree() | ✅ |

**降级机制**:

```yaml
fallback:
  enabled: true
  priority: 100
  description: 作为其他OrgSkill实现的降级方案，当外部组织系统不可用时自动启用
```

---

#### 2.2.3 外部组织集成技能

| Skill ID | 名称 | 集成平台 | 状态 |
|----------|------|---------|------|
| skill-org-dingding | 钉钉集成 | 钉钉开放平台 | ✅ |
| skill-org-feishu | 飞书集成 | 飞书开放平台 | ✅ |
| skill-org-wecom | 企业微信集成 | 企业微信API | ✅ |
| skill-org-ldap | LDAP集成 | LDAP/AD | ✅ |

**组织集成架构**:

```
┌─────────────────────────────────────────────────────────────┐
│                    组织集成适配器模式                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐                                        │
│  │ OrgService接口   │ ◄── 统一组织服务接口                     │
│  └────────┬────────┘                                        │
│           │                                                 │
│           ├─────────────────────────────────────────┐       │
│           │                                         │       │
│           ▼                 ▼                 ▼     ▼       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ ┌──────┐│
│  │DingdingImpl │  │ FeishuImpl  │  │ WecomImpl   │ │LDAP  ││
│  │ 钉钉实现     │  │ 飞书实现     │  │ 企业微信实现 │ │实现  ││
│  └─────────────┘  └─────────────┘  └─────────────┘ └──────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

### 2.3 用户数据实体关系

```
┌─────────────┐     1:N     ┌─────────────┐
│    User     │────────────►│   Session   │
│   用户实体   │             │   会话实体   │
└──────┬──────┘             └─────────────┘
       │
       │ N:1
       ▼
┌─────────────┐     1:N     ┌─────────────┐
│ Department  │────────────►│    Role     │
│   部门实体   │             │   角色实体   │
└──────┬──────┘             └──────┬──────┘
       │                           │
       │ N:M                       │ N:M
       ▼                           ▼
┌─────────────┐             ┌─────────────┐
│ Organization│             │ Permission  │
│   组织实体   │             │   权限实体   │
└─────────────┘             └─────────────┘
```

---

## 三、知识库集成

### 3.1 知识库技能体系

```
┌─────────────────────────────────────────────────────────────────┐
│                      知识库技能体系                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    场景技能层                             │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-knowledge- │  │skill-document-  │               │   │
│  │  │     qa          │  │   assistant     │               │   │
│  │  │  知识问答场景    │  │  智能文档助手    │               │   │
│  │  └────────┬────────┘  └────────┬────────┘               │   │
│  │           │                    │                        │   │
│  │  ┌────────▼────────┐  ┌────────▼────────┐               │   │
│  │  │skill-knowledge- │  │skill-project-   │               │   │
│  │  │      ui         │  │   knowledge     │               │   │
│  │  │  知识库管理UI   │  │  项目知识沉淀    │               │   │
│  │  └────────┬────────┘  └────────┬────────┘               │   │
│  └───────────┼─────────────────────┼────────────────────────┘   │
│              │                     │                            │
│              └──────────┬──────────┘                            │
│                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    服务技能层                             │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-knowledge- │  │   skill-rag     │               │   │
│  │  │      base       │  │  RAG检索增强    │               │   │
│  │  │  知识库核心服务  │  └────────┬────────┘               │   │
│  │  └────────┬────────┘           │                        │   │
│  │           │                    │                        │   │
│  │  ┌────────▼────────┐  ┌────────▼────────┐               │   │
│  │  │skill-local-     │  │skill-vector-    │               │   │
│  │  │   knowledge     │  │    sqlite       │               │   │
│  │  │  本地知识服务    │  │  向量存储服务    │               │   │
│  │  └─────────────────┘  └─────────────────┘               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 知识库核心服务规格

#### 3.2.1 skill-knowledge-base (知识库核心服务)

| 属性 | 值 |
|------|-----|
| ID | skill-knowledge-base |
| 版本 | 1.0.0 |
| 类型 | system-service |
| 优先级 | P0 (核心) |

**能力清单**:

| 能力ID | 名称 | 分类 | 说明 |
|--------|------|------|------|
| kb-management | 知识库管理 | service | 创建、更新、删除知识库 |
| document-management | 文档管理 | service | 管理知识库中的文档 |
| search | 知识检索 | ai | BM25语义检索 |

**提供接口**:

| 接口ID | 版本 | 说明 |
|--------|------|------|
| knowledge-storage | 1.0 | 知识库存储接口 |
| knowledge-search | 1.0 | 知识库检索接口 |

**API 闭环检查**:

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 创建知识库 | `createKb()` | `POST /api/v1/kb` | KbController.create() | ✅ |
| 获取知识库 | `getKb()` | `GET /api/v1/kb/{id}` | KbController.get() | ✅ |
| 更新知识库 | `updateKb()` | `PUT /api/v1/kb/{id}` | KbController.update() | ✅ |
| 删除知识库 | `deleteKb()` | `DELETE /api/v1/kb/{id}` | KbController.delete() | ✅ |
| 添加文档 | `addDoc()` | `POST /api/v1/kb/{kbId}/documents` | KbController.addDocument() | ✅ |
| 获取文档 | `listDocs()` | `GET /api/v1/kb/{kbId}/documents` | KbController.listDocuments() | ✅ |
| 删除文档 | `deleteDoc()` | `DELETE /api/v1/kb/{kbId}/documents/{docId}` | KbController.deleteDocument() | ✅ |
| 搜索知识 | `search()` | `POST /api/v1/kb/{kbId}/search` | KbController.search() | ✅ |
| 多库搜索 | `multiSearch()` | `POST /api/v1/kb/search/multi` | KbController.multiSearch() | ✅ |

---

#### 3.2.2 skill-rag (RAG检索增强)

| 属性 | 值 |
|------|-----|
| ID | skill-rag |
| 版本 | 1.0.0 |
| 类型 | system-service |
| 优先级 | P0 (核心) |

**能力清单**:

| 能力ID | 名称 | 分类 | 说明 |
|--------|------|------|------|
| retrieval | 文档检索 | ai | 关键词、语义、混合检索 |
| prompt-building | Prompt构建 | ai | 构建带检索上下文的Prompt |
| kb-registration | 知识库注册 | service | 注册外部知识库 |

**检索策略**:

| 策略 | 说明 | 适用场景 |
|------|------|---------|
| KEYWORD | 关键词检索 | 精确匹配场景 |
| SEMANTIC | 语义检索 | 模糊语义匹配 |
| HYBRID | 混合检索 | 综合场景（默认） |

**API 闭环检查**:

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 执行检索 | `retrieve()` | `POST /api/v1/rag/retrieve` | RagController.retrieve() | ✅ |
| 构建Prompt | `buildPrompt()` | `POST /api/v1/rag/prompt` | RagController.buildPrompt() | ✅ |
| 自定义Prompt | `customPrompt()` | `POST /api/v1/rag/prompt/custom` | RagController.customPrompt() | ✅ |
| 注册知识库 | `registerKb()` | `POST /api/v1/rag/kb/{kbId}/register` | RagController.registerKb() | ✅ |
| 注销知识库 | `unregisterKb()` | `DELETE /api/v1/rag/kb/{kbId}` | RagController.unregisterKb() | ✅ |

---

### 3.3 知识库数据实体关系

```
┌─────────────┐     1:N     ┌─────────────┐
│ KnowledgeBase│────────────►│  Document   │
│   知识库实体  │             │   文档实体   │
└─────────────┘             └──────┬──────┘
                                   │
                                   │ 1:N
                                   ▼
                            ┌─────────────┐
                            │   Chunk     │
                            │   分块实体   │
                            └──────┬──────┘
                                   │
                                   │ 1:1
                                   ▼
                            ┌─────────────┐
                            │  Embedding  │
                            │  向量实体    │
                            └─────────────┘
```

---

## 四、用户 UI 界面

### 4.1 UI 技能体系

```
┌─────────────────────────────────────────────────────────────────┐
│                       UI 技能体系                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Nexus UI 技能                          │   │
│  │                                                          │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-knowledge- │  │skill-llm-       │               │   │
│  │  │      ui         │  │  assistant-ui   │               │   │
│  │  │  知识库管理UI   │  │  LLM智能助手UI   │               │   │
│  │  │  (sidebar)      │  │  (floating)     │               │   │
│  │  └─────────────────┘  └─────────────────┘               │   │
│  │                                                          │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-llm-       │  │skill-nexus-     │               │   │
│  │  │ management-ui   │  │dashboard-ui     │               │   │
│  │  │  LLM管理UI      │  │  仪表盘UI        │               │   │
│  │  │  (sidebar)      │  │  (sidebar)      │               │   │
│  │  └─────────────────┘  └─────────────────┘               │   │
│  │                                                          │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-nexus-     │  │skill-nexus-     │               │   │
│  │  │health-check-ui  │  │system-status-ui │               │   │
│  │  │  健康检查UI     │  │  系统状态UI      │               │   │
│  │  └─────────────────┘  └─────────────────┘               │   │
│  │                                                          │   │
│  │  ┌─────────────────┐  ┌─────────────────┐               │   │
│  │  │skill-personal-  │  │skill-storage-   │               │   │
│  │  │dashboard-ui     │  │management-ui    │               │   │
│  │  │  个人仪表盘UI   │  │  存储管理UI      │               │   │
│  │  └─────────────────┘  └─────────────────┘               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 UI 技能详细规格

#### 4.2.1 skill-knowledge-ui (知识库管理UI)

| 属性 | 值 |
|------|-----|
| ID | skill-knowledge-ui |
| 版本 | 2.3.0 |
| 类型 | scene-skill |
| 分类 | ABS |
| mainFirst | true |

**Nexus UI 配置**:

```yaml
nexusUi:
  entry:
    page: index.html
    title: 知识库
    icon: ri-book-3-line
    
  menu:
    position: sidebar
    category: knowledge
    order: 10
    
  layout:
    type: default
    sidebar: true
    header: true
```

**场景能力**:

| 能力ID | 名称 | 类型 | 说明 |
|--------|------|------|------|
| kb-view | 查看知识库 | ATOMIC | 查看知识库列表和详情 |
| kb-manage | 管理知识库 | ATOMIC | 创建和管理知识库 |
| kb-search | 检索测试 | ATOMIC | 测试知识库检索功能 |
| intent-receiver | 意图接收 | DRIVER | 接收用户操作意图 |
| event-listener | 事件监听 | DRIVER | 监听文档和知识库事件 |

**依赖项**:

| 依赖ID | 版本要求 | 必需 | 说明 |
|--------|---------|------|------|
| skill-knowledge-base | >=1.0.0 | ✅ | 知识库核心服务 |
| skill-rag | >=1.0.0 | ❌ | RAG检索增强（可选） |
| skill-document-processor | >=1.0.0 | ❌ | 文档处理服务（可选） |

---

#### 4.2.2 skill-llm-assistant-ui (LLM智能助手UI)

| 属性 | 值 |
|------|-----|
| ID | skill-llm-assistant-ui |
| 版本 | 2.3.0 |
| 类型 | scene-skill |
| 分类 | ABS |
| mainFirst | true |

**Nexus UI 配置**:

```yaml
nexusUi:
  entry:
    page: index.html
    title: LLM助手
    icon: ri-robot-line
    
  menu:
    position: none          # 不在菜单中显示
    category: llm
    order: 1
    
  layout:
    type: floating          # 浮动窗口
    sidebar: false
    header: false
    
  floating:
    enabled: true
    position: bottom-right  # 右下角
    trigger: button         # 按钮触发
    width: 420
    height: 600
```

**场景能力**:

| 能力ID | 名称 | 类型 | 说明 |
|--------|------|------|------|
| llm-chat | 智能对话 | ATOMIC | 与LLM进行智能对话 |
| context-aware | 上下文感知 | ATOMIC | 自动收集当前页面上下文 |
| smart-suggest | 智能建议 | ATOMIC | 基于上下文提供智能建议 |
| intent-receiver | 意图接收 | DRIVER | 接收用户对话意图 |

**依赖项**:

| 依赖ID | 版本要求 | 必需 | 说明 |
|--------|---------|------|------|
| skill-llm-conversation | >=1.0.0 | ✅ | LLM对话服务 |
| skill-llm-context-builder | >=1.0.0 | ✅ | 上下文构建服务 |
| skill-local-knowledge | >=1.0.0 | ❌ | 本地知识增强（可选） |

---

### 4.3 UI 布局类型

| 布局类型 | 说明 | 适用场景 |
|---------|------|---------|
| default | 标准布局（侧边栏+头部+内容） | 管理类页面 |
| floating | 浮动窗口 | 助手类功能 |
| fullscreen | 全屏布局 | 大屏展示 |
| embedded | 嵌入式布局 | 集成到其他页面 |

### 4.4 菜单位置配置

| 位置 | 说明 | 示例技能 |
|------|------|---------|
| sidebar | 侧边栏菜单 | knowledge-ui, llm-management-ui |
| header | 顶部菜单 | - |
| none | 不显示在菜单 | llm-assistant-ui (浮动窗口) |

---

## 五、依赖项目清单

### 5.1 核心依赖

| 依赖项目 | 版本 | 说明 | 使用技能数 |
|---------|------|------|-----------|
| scene-engine | 2.3 | 场景引擎SDK | 全部 |
| llm-sdk | 2.3 | LLM服务SDK | 15+ |
| spring-boot | 2.7.x | Spring Boot框架 | 全部Java技能 |

### 5.2 技能依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                      技能依赖关系图                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-document-assistant (ABS)                                 │
│      │                                                          │
│      ├── skill-knowledge-base ◄────────────────────────────┐   │
│      │       │                                              │   │
│      │       └── skill-vector-sqlite                        │   │
│      │                                                      │   │
│      ├── skill-rag ◄────────────────────────────────────────┼───│
│      │       │                                              │   │
│      │       └── skill-knowledge-base                       │   │
│      │                                                      │   │
│      └── skill-llm-conversation ◄───────────────────────────┼───│
│              │                                              │   │
│              ├── skill-llm-context-builder                  │   │
│              │                                              │   │
│              └── skill-llm-config-manager                   │   │
│                                                             │   │
│  skill-knowledge-ui (ABS) ◄─────────────────────────────────┘   │
│      │                                                          │
│      └── skill-knowledge-base                                   │
│                                                                 │
│  skill-llm-assistant-ui (ABS)                                   │
│      │                                                          │
│      ├── skill-llm-conversation                                 │
│      │                                                          │
│      ├── skill-llm-context-builder                              │
│      │                                                          │
│      └── skill-local-knowledge                                  │
│                                                                 │
│  skill-onboarding-assistant (ABS)                               │
│      │                                                          │
│      ├── skill-knowledge-base                                   │
│      │                                                          │
│      ├── skill-llm-conversation                                 │
│      │                                                          │
│      └── skill-rag                                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.3 依赖统计

| 分类 | 必需依赖数 | 可选依赖数 |
|------|-----------|-----------|
| ABS 技能 | 2-3 | 1-2 |
| ASS 技能 | 1-2 | 0-1 |
| TBS 技能 | 1-2 | 0-1 |
| SVC 技能 | 0-1 | 0-1 |

### 5.4 常用依赖技能

| 依赖技能 | 被依赖次数 | 说明 |
|---------|-----------|------|
| skill-knowledge-base | 12 | 知识库核心服务 |
| skill-llm-conversation | 8 | LLM对话服务 |
| skill-rag | 6 | RAG检索增强 |
| skill-user-auth | 5 | 用户认证服务 |
| skill-vector-sqlite | 4 | 向量存储服务 |

---

## 六、字典表规范

### 6.1 场景分类字典

```java
@Dict(code = "scene_category", name = "场景分类", description = "场景技能的分类")
public enum SceneCategory implements DictItem {
    
    ABS("ABS", "自驱业务场景", "自动启动、业务闭环的场景技能", "ri-robot-line", 1),
    ASS("ASS", "自驱系统场景", "自动启动、系统功能的场景技能", "ri-settings-3-line", 2),
    TBS("TBS", "触发业务场景", "人工触发、被动响应的场景技能", "ri-cursor-line", 3),
    SVC("SVC", "服务技能", "纯服务提供的技能", "ri-service-line", 4);
    
    // 实现 DictItem 接口...
}
```

### 6.2 技能类型字典

```java
@Dict(code = "skill_type", name = "技能类型", description = "技能的类型分类")
public enum SkillType implements DictItem {
    
    SCENE_SKILL("SCENE_SKILL", "场景技能", "具备场景能力的技能", "ri-layout-grid-line", 1),
    SERVICE_SKILL("SERVICE_SKILL", "服务技能", "纯服务提供的技能", "ri-server-line", 2),
    UI_SKILL("UI_SKILL", "UI技能", "提供用户界面的技能", "ri-layout-line", 3);
    
    // 实现 DictItem 接口...
}
```

### 6.3 能力类型字典

```java
@Dict(code = "capability_type", name = "能力类型", description = "能力的类型分类")
public enum CapabilityType implements DictItem {
    
    ATOMIC("ATOMIC", "原子能力", "不可分割的基础能力", "ri-flashlight-line", 1),
    COMPOSITE("COMPOSITE", "组合能力", "由多个原子能力组合而成", "ri-stack-line", 2),
    DRIVER("DRIVER", "驱动能力", "驱动场景运行的能力", "ri-steering-line", 3),
    SCENE("SCENE", "场景能力", "完整的场景能力", "ri-layout-grid-line", 4);
    
    // 实现 DictItem 接口...
}
```

---

## 七、验收标准

### 7.1 三闭环验收

#### 7.1.1 生命周期闭环

| 技能 | 创建 | 查询 | 更新 | 删除 | 状态 |
|------|------|------|------|------|------|
| skill-knowledge-base | ✅ | ✅ | ✅ | ✅ | 完整 |
| skill-rag | ✅ | ✅ | ✅ | ✅ | 完整 |
| skill-user-auth | ✅ | ✅ | ✅ | ✅ | 完整 |
| skill-org-base | ✅ | ✅ | ✅ | ✅ | 完整 |

#### 7.1.2 数据实体闭环

| 实体 | 关系定义 | 级联处理 | 数据一致性 | 状态 |
|------|---------|---------|-----------|------|
| User | ✅ | ✅ | ✅ | 完整 |
| KnowledgeBase | ✅ | ✅ | ✅ | 完整 |
| Document | ✅ | ✅ | ✅ | 完整 |
| Session | ✅ | ✅ | ✅ | 完整 |

#### 7.1.3 按钮API闭环

| UI技能 | 按钮数量 | API闭环数 | 闭环率 | 状态 |
|--------|---------|----------|--------|------|
| skill-knowledge-ui | 8 | 8 | 100% | 完整 |
| skill-llm-assistant-ui | 5 | 5 | 100% | 完整 |
| skill-llm-management-ui | 6 | 6 | 100% | 完整 |

### 7.2 功能验收清单

- [ ] 用户可通过组织技能完成登录认证
- [ ] 用户可创建和管理知识库
- [ ] 用户可上传文档并自动索引
- [ ] 用户可通过RAG进行智能问答
- [ ] UI技能可正确显示在菜单中
- [ ] 浮动窗口技能可正常触发
- [ ] 依赖技能可自动安装

---

## 八、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 2.3.5 | 2026-03-06 | 新增5个场景技能需求规格 |
| 2.3.0 | 2026-03-01 | 完善三闭环检查规范 |
| 2.2.0 | 2026-02-20 | 新增知识库集成规范 |
| 2.1.0 | 2026-02-15 | 新增组织机构集成规范 |

---

**文档维护**: Skills Team  
**最后更新**: 2026-03-06
