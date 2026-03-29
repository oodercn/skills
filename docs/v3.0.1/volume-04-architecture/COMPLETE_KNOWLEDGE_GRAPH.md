# Skills 完整知识图谱 v2.3.1（含 Agent-SDK 组网模型）

## 一、核心架构模型

### 1.1 Agent-Link-Capability 三角模型

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    Agent-Link-Capability 核心三角模型                             │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│                           ┌─────────────┐                                       │
│                           │   Agent     │  智能体（执行者）                      │
│                           │             │                                       │
│                           │ - agentId   │  唯一标识                              │
│                           │ - type      │  MAIN/LLM/WORKER/DEVICE/PLATFORM      │
│                           │ - status    │  ONLINE/OFFLINE/BUSY                  │
│                           │ - capabilities │ 拥有的能力列表                      │
│                           └──────┬──────┘                                       │
│                                  │                                              │
│                     提供/执行    │    通信                                       │
│                    /────────────┼────────────\                                  │
│                   /             │             \                                 │
│                  ▼              │              ▼                                │
│           ┌─────────────┐       │       ┌─────────────┐                         │
│           │ Capability  │       │       │    Link     │  链路（通信通道）         │
│           │             │       │       │             │                         │
│           │ - capId     │       │       │ - linkId    │                         │
│           │ - type      │       │       │ - sourceId  │                         │
│           │ - ownership │       │       │ - targetId  │                         │
│           │ - visibility│       │       │ - type      │  DIRECT/RELAY/TUNNEL    │
│           └─────────────┘       │       │ - status    │  ACTIVE/INACTIVE        │
│                   \             │             /                                 │
│                    \            │            /                                  │
│                     \───────────┼───────────/                                   │
│                                 │                                               │
│                                 ▼                                               │
│                        ┌─────────────────┐                                      │
│                        │ CapabilityBinding│  能力绑定                            │
│                        │                 │                                      │
│                        │ - sceneGroupId  │                                      │
│                        │ - capabilityId  │                                      │
│                        │ - agentId       │                                      │
│                        │ - linkId        │                                      │
│                        │ - capAddress    │                                      │
│                        └─────────────────┘                                      │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Agent 类型体系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  Agent 类型体系                                                                  │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  AgentType                                                                      │
│  ├── MAIN          # 主代理 - 场景组主控 Agent                                   │
│  ├── LLM           # 大语言模型 Agent - 对话生成、内容创作                        │
│  ├── WORKER        # 工作 Agent - 任务执行、数据处理                             │
│  ├── DEVICE        # 设备 Agent - IoT 设备控制                                  │
│  ├── PLATFORM      # 平台 Agent - 系统服务、基础设施                             │
│  ├── COORDINATOR   # 协调 Agent - 多 Agent 协调                                 │
│  └── ASSISTANT     # 助手 Agent - 辅助功能                                      │
│                                                                                 │
│  SuperAgent（超级智能体）                                                         │
│  ├── subAgents: SubAgentRef[]      # 子 Agent 列表                              │
│  ├── coordination: CoordinationConfig  # 协调配置                               │
│  ├── emergentCapabilities: Capability[]  # 涌现能力                             │
│  └── selfDefined: boolean          # 是否自驱                                   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 Link 类型选择规则

| 场景 | Link 类型 | 说明 |
|------|-----------|------|
| 同域内 Agent 通信 | DIRECT | 低延迟，无中间节点 |
| 跨域通信（有网关） | RELAY | 通过中间节点转发 |
| 私有域访问公共域 | TUNNEL | 加密隧道，需权限验证 |
| 一对多通知 | MULTICAST | 组播，高效广播 |
| 北向 P2P 通信 | P2P | WebRTC 等去中心化 |

### 1.4 能力三层模型

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  能力三层模型                                                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  第一层：静态能力定义（模板层）                                                    │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │  Capability (能力定义)                                                    │   │
│  │  ├── capabilityId: 全局唯一标识                                           │   │
│  │  ├── name, type, version: 基本属性                                        │   │
│  │  ├── ownership: PLATFORM | INDEPENDENT | SCENE_INTERNAL                  │   │
│  │  └── visibility: public | internal | private                              │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  第二层：能力实例化（运行层）                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │  能力实例 = Agent + Link + Address                                        │   │
│  │  ├── Agent: 能力执行者                                                    │   │
│  │  ├── Link: 通信链路                                                       │   │
│  │  └── Address: CAP地址（权限控制）                                          │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  第三层：能力调用（执行层）                                                        │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │  CapabilityBinding (能力绑定)                                             │   │
│  │  ├── capabilityId + capId: 双ID映射                                       │   │
│  │  ├── agentId + linkId: 执行者和链路                                       │   │
│  │  └── status: 绑定状态                                                     │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、完整页面层级（四级结构）

### 2.1 skill-scene-management 页面层级

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  skill-scene-management 页面层级（四级结构）                                      │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  【一级页面 - 菜单入口】                                                          │
│  ├── scene-management.html           # 场景列表                                 │
│  ├── scene-group-management.html     # 场景组管理                               │
│  ├── my-scenes.html                  # 我的场景                                 │
│  ├── template-management.html        # 模板管理                                 │
│  ├── knowledge-base.html             # 知识库管理                               │
│  └── llm-config.html                 # LLM配置                                  │
│                                                                                 │
│  【二级页面 - 详情页】                                                            │
│  └── scene/scene-group.html          # 场景组详情                               │
│                                                                                 │
│  【三级页面 - 子功能页】                                                          │
│  ├── scene/participants.html         # 参与者管理                               │
│  ├── scene/capabilities.html         # 能力绑定管理                             │
│  ├── scene/knowledge-bindings.html   # 知识库绑定                               │
│  ├── scene/llm-config.html           # LLM配置                                  │
│  ├── scene/snapshots.html            # 快照管理                                 │
│  ├── scene/workflow.html             # 工作流实例                               │
│  └── scene/history.html              # 执行历史                                 │
│                                                                                 │
│  【四级页面 - Agent组网页面】                                                     │
│  ├── scene/agent/list.html           # Agent列表                               │
│  ├── scene/agent/detail.html         # Agent详情                               │
│  ├── scene/agent/topology.html       # 组网拓扑图                               │
│  ├── scene/link/list.html            # Link列表                                │
│  ├── scene/link/detail.html          # Link详情                                │
│  ├── scene/binding/detail.html       # 能力绑定详情                             │
│  └── scene/binding/monitor.html      # 能力调用监控                             │
│                                                                                 │
│  【三级页面 - 知识库子功能】                                                       │
│  ├── knowledge/documents.html        # 文档管理                                 │
│  ├── knowledge/index-status.html     # 索引状态                                 │
│  └── knowledge/rag-config.html       # RAG配置                                  │
│                                                                                 │
│  【三级页面 - LLM子功能】                                                         │
│  ├── llm/provider-detail.html        # 提供商详情                               │
│  ├── llm/model-config.html           # 模型配置                                 │
│  └── llm/token-stats.html            # Token统计                                │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 skill-capability 页面层级

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  skill-capability 页面层级（四级结构）                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  【一级页面 - 菜单入口】                                                          │
│  ├── capability-management.html      # 能力列表                                 │
│  ├── capability-discovery.html       # 能力发现                                 │
│  ├── capability-activation.html      # 能力激活                                 │
│  ├── capability-binding.html         # 能力绑定                                 │
│  └── my-capabilities.html            # 我的能力                                 │
│                                                                                 │
│  【二级页面 - 详情页】                                                            │
│  └── capability-detail.html          # 能力详情                                 │
│                                                                                 │
│  【三级页面 - 子功能页】                                                          │
│  ├── capability/config.html          # 能力配置                                 │
│  ├── capability/provider.html        # 提供商管理                               │
│  └── capability/stats.html           # 使用统计                                 │
│                                                                                 │
│  【四级页面 - Agent管理】                                                         │
│  ├── capability/agent/list.html      # Agent列表                               │
│  ├── capability/agent/detail.html    # Agent详情                               │
│  ├── capability/agent/assign.html    # Agent分配                               │
│  └── capability/agent/monitor.html   # Agent监控                               │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、API 端点完整映射

### 3.1 Agent API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/agent/list` | GET | Agent列表 | scene/agent/list.html |
| `/api/agent/page` | GET | Agent分页 | scene/agent/list.html |
| `/api/agent/{agentId}` | GET | Agent详情 | scene/agent/detail.html |
| `/api/agent/search` | GET | 搜索Agent | scene/agent/list.html |
| `/api/agent/type/{type}` | GET | 按类型查询 | scene/agent/list.html |
| `/api/agent/status/{status}` | GET | 按状态查询 | scene/agent/list.html |
| `/api/agent/{agentId}/heartbeat` | POST | 心跳 | scene/agent/detail.html |
| `/api/agent/{agentId}/status` | PUT | 更新状态 | scene/agent/detail.html |
| `/api/agent/{agentId}/binding-count` | GET | 绑定数量 | scene/agent/detail.html |
| `/api/agent/stats` | GET | 统计信息 | scene/agent/list.html |

### 3.2 场景组 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/scene-groups` | GET | 场景组列表 | scene-group-management.html |
| `/api/v1/scene-groups` | POST | 创建场景组 | scene-group-management.html |
| `/api/v1/scene-groups/{id}` | GET | 场景组详情 | scene/scene-group.html |
| `/api/v1/scene-groups/{id}` | PUT | 更新场景组 | scene/scene-group.html |
| `/api/v1/scene-groups/{id}` | DELETE | 删除场景组 | scene-group-management.html |
| `/api/v1/scene-groups/{id}/activate` | POST | 激活场景组 | scene/scene-group.html |
| `/api/v1/scene-groups/{id}/deactivate` | POST | 停用场景组 | scene/scene-group.html |

### 3.3 参与者 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/scene-groups/{id}/participants` | GET | 参与者列表 | scene/participants.html |
| `/api/v1/scene-groups/{id}/participants` | POST | 加入场景 | scene/participants.html |
| `/api/v1/scene-groups/{id}/participants/{pid}` | GET | 参与者详情 | scene/participants.html |
| `/api/v1/scene-groups/{id}/participants/{pid}` | DELETE | 离开场景 | scene/participants.html |
| `/api/v1/scene-groups/{id}/participants/{pid}/role` | PUT | 变更角色 | scene/participants.html |

### 3.4 能力绑定 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/scene-groups/{id}/capabilities` | GET | 能力绑定列表 | scene/capabilities.html |
| `/api/v1/scene-groups/{id}/capabilities` | POST | 绑定能力 | scene/capabilities.html |
| `/api/v1/scene-groups/{id}/capabilities/{bid}` | PUT | 更新绑定 | scene/binding/detail.html |
| `/api/v1/scene-groups/{id}/capabilities/{bid}` | DELETE | 解绑能力 | scene/capabilities.html |

### 3.5 知识库 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/knowledge-bases` | GET | 知识库列表 | knowledge-base.html |
| `/api/v1/knowledge-bases` | POST | 创建知识库 | knowledge-base.html |
| `/api/v1/knowledge-bases/{id}` | GET | 知识库详情 | knowledge-base.html |
| `/api/v1/knowledge-bases/{id}` | PUT | 更新知识库 | knowledge-base.html |
| `/api/v1/knowledge-bases/{id}` | DELETE | 删除知识库 | knowledge-base.html |
| `/api/v1/knowledge-bases/layer/{layer}` | GET | 按层获取 | knowledge-base.html |
| `/api/v1/scene-groups/{id}/knowledge` | GET | 场景组知识绑定 | scene/knowledge-bindings.html |
| `/api/v1/scene-groups/{id}/knowledge` | POST | 绑定知识库 | scene/knowledge-bindings.html |
| `/api/v1/scene-groups/{id}/knowledge/{kbId}` | DELETE | 解绑知识库 | scene/knowledge-bindings.html |
| `/api/v1/scene-groups/{id}/knowledge/config` | GET/PUT | RAG配置 | knowledge/rag-config.html |

### 3.6 LLM API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/llm/providers` | GET | 提供商列表 | llm-config.html |
| `/api/v1/llm/providers` | POST | 创建提供商 | llm-config.html |
| `/api/v1/llm/providers/{id}` | GET | 提供商详情 | llm/provider-detail.html |
| `/api/v1/llm/providers/{id}` | PUT | 更新提供商 | llm/provider-detail.html |
| `/api/v1/llm/providers/{id}` | DELETE | 删除提供商 | llm-config.html |
| `/api/v1/llm/providers/{id}/test` | POST | 测试连接 | llm/provider-detail.html |
| `/api/v1/llm/config` | GET/PUT | 全局配置 | llm-config.html |
| `/api/v1/scene-groups/{id}/llm/config` | GET/PUT | 场景组LLM配置 | scene/llm-config.html |
| `/api/v1/scene-groups/{id}/llm/stats` | GET | Token统计 | llm/token-stats.html |
| `/api/v1/scene-groups/{id}/llm/reset-tokens` | POST | 重置Token | llm/token-stats.html |

### 3.7 快照 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/scene-groups/{id}/snapshots` | GET | 快照列表 | scene/snapshots.html |
| `/api/v1/scene-groups/{id}/snapshots` | POST | 创建快照 | scene/snapshots.html |
| `/api/v1/scene-groups/{id}/snapshots/{sid}` | DELETE | 删除快照 | scene/snapshots.html |
| `/api/v1/scene-groups/{id}/snapshots/{sid}/restore` | POST | 恢复快照 | scene/snapshots.html |

### 3.8 模板 API

| API | 方法 | 说明 | 页面 |
|-----|------|------|------|
| `/api/v1/templates` | GET | 模板列表 | template-management.html |
| `/api/v1/templates/{id}` | GET | 模板详情 | template-management.html |
| `/api/v1/templates/{id}/deploy` | POST | 部署模板 | template-management.html |
| `/api/v1/templates/{id}/install` | POST | 安装模板 | template-management.html |
| `/api/v1/templates/{id}/dependencies/health` | GET | 依赖健康检查 | template-management.html |
| `/api/v1/templates/{id}/dependencies/missing` | GET | 缺失依赖 | template-management.html |
| `/api/v1/templates/{id}/dependencies/auto-install` | POST | 自动安装依赖 | template-management.html |

---

## 四、能力调用流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  能力调用流程                                                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  用户请求                                                                        │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 1. 场景组   │  验证用户权限                                                   │
│  │   鉴权      │  检查场景组状态                                                 │
│  └─────────────┘                                                                │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 2. 能力     │  根据 capId 查找 CapabilityBinding                              │
│  │   路由      │  获取 agentId 和 linkId                                        │
│  └─────────────┘                                                                │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 3. Agent    │  检查 Agent 状态 (RUNNING?)                                    │
│  │   选择      │  负载均衡选择实例                                               │
│  └─────────────┘                                                                │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 4. Link     │  检查 Link 状态 (ACTIVE?)                                      │
│  │   建立      │  选择 Link 类型 (DIRECT/RELAY/TUNNEL)                          │
│  └─────────────┘                                                                │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 5. 能力     │  通过 Link 调用 Agent                                          │
│  │   执行      │  Agent 执行能力                                                │
│  └─────────────┘                                                                │
│      │                                                                          │
│      ▼                                                                          │
│  ┌─────────────┐                                                                │
│  │ 6. 结果     │  返回执行结果                                                   │
│  │   返回      │  更新调用统计                                                  │
│  └─────────────┘                                                                │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、17种默认能力

### 5.1 能力列表

| 地址 | 代码 | 名称 | 类型 |
|------|------|------|------|
| 0x28 | llm.chat | LLM对话 | llm |
| 0x29 | llm.vision | LLM视觉 | llm |
| 0x2A | llm.embedding | LLM嵌入 | llm |
| 0x30 | knowledge.search | 知识检索 | knowledge |
| 0x31 | knowledge.index | 知识索引 | knowledge |
| 0x18 | vfs.read | 文件读取 | vfs |
| 0x19 | vfs.write | 文件写入 | vfs |
| 0x20 | db.query | 数据库查询 | driver |
| 0x21 | db.transaction | 数据库事务 | driver |
| 0x00 | sys.config | 系统配置 | sys |
| 0x01 | sys.logging | 日志服务 | sys |
| 0x08 | org.user | 用户管理 | org |
| 0x09 | org.dept | 部门管理 | org |
| 0x10 | auth.login | 登录认证 | auth |
| 0x11 | auth.permission | 权限管理 | auth |
| 0x48 | comm.email | 邮件通知 | comm |
| 0x49 | comm.message | 消息推送 | comm |

### 5.2 能力与 Agent 绑定关系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  能力与 Agent 绑定关系                                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Agent Type        │  典型能力绑定                                               │
│  ──────────────────┼───────────────────────────────────────────────────────────│
│  MAIN              │  sys.config, sys.logging                                   │
│  LLM               │  llm.chat, llm.vision, llm.embedding                       │
│  WORKER            │  vfs.read, vfs.write, db.query                             │
│  COORDINATOR       │  knowledge.search, knowledge.index                         │
│  PLATFORM          │  org.user, org.dept, auth.login, auth.permission           │
│  DEVICE            │  (IoT设备特定能力)                                          │
│  ASSISTANT         │  comm.email, comm.message                                  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、缺失页面清单

### 6.1 四级页面（Agent组网）- 需创建

| 页面 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| agent/list.html | scene/ | Agent列表 | P1 |
| agent/detail.html | scene/ | Agent详情 | P1 |
| agent/topology.html | scene/ | 组网拓扑图 | P1 |
| link/list.html | scene/ | Link列表 | P1 |
| link/detail.html | scene/ | Link详情 | P2 |
| binding/detail.html | scene/ | 能力绑定详情 | P1 |
| binding/monitor.html | scene/ | 能力调用监控 | P2 |

### 6.2 三级页面 - 需创建

| 页面 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| participants.html | scene/ | 参与者管理 | P1 |
| capabilities.html | scene/ | 能力绑定管理 | P1 |
| knowledge-bindings.html | scene/ | 知识库绑定 | P1 |
| llm-config.html | scene/ | 场景组LLM配置 | P1 |
| snapshots.html | scene/ | 快照管理 | P2 |
| workflow.html | scene/ | 工作流实例 | P2 |
| history.html | scene/ | 执行历史 | P2 |
| documents.html | knowledge/ | 文档管理 | P1 |
| index-status.html | knowledge/ | 索引状态 | P1 |
| rag-config.html | knowledge/ | RAG配置 | P1 |
| provider-detail.html | llm/ | 提供商详情 | P1 |
| model-config.html | llm/ | 模型配置 | P2 |
| token-stats.html | llm/ | Token统计 | P2 |

---

## 七、微型版说明

### 7.1 不引入数据库

微型版使用 JSON 文件存储，不引入数据库：

```
mvp/data/
├── capabilities.json     # 能力数据
├── users.json            # 用户数据
├── departments.json      # 部门数据
├── scenes.json           # 场景数据
├── scene-groups.json     # 场景组数据
├── knowledge-bases.json  # 知识库数据
├── llm-providers.json    # LLM提供商数据
├── agents.json           # Agent数据
└── links.json            # Link数据
```

### 7.2 数据存储服务

使用 `JsonStorageService` 实现 CRUD 操作：

```java
JsonStorageService<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    T save(T entity);
    void deleteById(String id);
}
```
