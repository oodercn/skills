# 场景管理模块说明书 v2.3.1

## 模块概述

场景管理模块提供场景的创建、配置、运行和监控功能，支持场景组管理、知识库配置、LLM配置和Agent组网。

---

## 一、核心架构

### 1.1 Agent-Link-Capability 三角模型

场景组基于 Agent-SDK 组网模型实现：

```
Agent (智能体) ←→ Link (链路) ←→ Capability (能力)
        ↓
CapabilityBinding (能力绑定)
        ↓
SceneGroup (场景组)
```

### 1.2 能力三层模型

| 层级 | 说明 |
|------|------|
| 第一层 | 静态能力定义（模板层） |
| 第二层 | 能力实例化（运行层）= Agent + Link + Address |
| 第三层 | 能力调用（执行层）= CapabilityBinding |

---

## 二、页面层级结构

### 2.1 一级页面（菜单入口）

| 页面 | 路径 | 说明 |
|------|------|------|
| 场景列表 | scene-management.html | 所有场景概览 |
| 场景组 | scene-group-management.html | 场景组CRUD |
| 我的场景 | my-scenes.html | 用户参与的场景 |
| 模板管理 | template-management.html | 场景模板 |
| 知识库 | knowledge-base.html | 知识库管理 |
| LLM配置 | llm-config.html | LLM提供商配置 |
| Agent组网 | scene/agent/topology.html | 组网拓扑图 |

### 2.2 二级页面（详情页）

| 页面 | 路径 | 说明 |
|------|------|------|
| 场景组详情 | scene/scene-group.html | 场景组完整信息 |

### 2.3 三级页面（子功能页）

| 页面 | 路径 | 说明 |
|------|------|------|
| 参与者管理 | scene/participants.html | 场景组成员管理 |
| 能力绑定 | scene/capabilities.html | 能力与Agent绑定 |
| 知识库绑定 | scene/knowledge-bindings.html | 三层知识架构配置 |
| LLM配置 | scene/llm-config.html | 场景组级别LLM配置 |
| 快照管理 | scene/snapshots.html | 配置快照 |
| 执行历史 | scene/history.html | 运行历史记录 |

### 2.4 四级页面（Agent组网）

| 页面 | 路径 | 说明 |
|------|------|------|
| Agent列表 | scene/agent/list.html | 所有Agent列表 |
| Agent详情 | scene/agent/detail.html | Agent详细信息 |
| 组网拓扑 | scene/agent/topology.html | 可视化拓扑图 |
| Link列表 | scene/link/list.html | 通信链路列表 |
| 能力绑定详情 | scene/binding/detail.html | 绑定详细信息 |
| 能力调用监控 | scene/binding/monitor.html | 调用统计监控 |

---

## 三、三层知识架构

### 3.1 知识层级

| 层级 | 名称 | 说明 | 优先级 |
|------|------|------|--------|
| GENERAL | 通用知识层 | 公司制度、流程规范 | 0 |
| PROFESSIONAL | 专业知识层 | 部门专业知识 | 1 |
| PERSONAL | 个人知识层 | 个人私有知识 | 2 |

### 3.2 RAG配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| topK | 每层返回数量 | 5 |
| threshold | 相似度阈值 | 0.7 |
| crossLayerSearch | 跨层检索 | true |

---

## 四、LLM集成

### 4.1 提供商类型

| 类型 | 说明 |
|------|------|
| DeepSeek | 国产大模型 |
| OpenAI | GPT系列 |
| Ollama | 本地部署 |
| 百度文心 | 文心一言 |
| 阿里通义 | 通义千问 |

### 4.2 决策模式

| 模式 | 说明 |
|------|------|
| ONLINE_FIRST | 优先在线模型 |
| OFFLINE_FIRST | 优先离线模型 |
| HYBRID | 混合模式 |

---

## 五、Agent类型

| 类型 | 说明 | 典型能力绑定 |
|------|------|-------------|
| MAIN | 主代理 | sys.config, sys.logging |
| LLM | 大语言模型 | llm.chat, llm.vision |
| WORKER | 工作Agent | vfs.read, vfs.write |
| COORDINATOR | 协调Agent | knowledge.search |
| PLATFORM | 平台Agent | org.user, auth.login |
| DEVICE | 设备Agent | IoT设备能力 |
| ASSISTANT | 助手Agent | comm.email |

---

## 六、17种默认能力

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

---

## 七、用户故事闭环

### 7.1 场景组生命周期

```
创建场景组 → 配置参与者 → 绑定能力 → 配置知识库 → 配置LLM → 激活 → 运行监控 → 销毁
```

### 7.2 能力绑定流程

```
选择能力 → 选择Agent → 建立Link → 绑定到场景组 → 监控调用
```

### 7.3 知识库配置流程

```
创建知识库 → 上传文档 → 建立索引 → 配置RAG → 绑定到场景组
```

---

## 八、协作场景依赖

### 8.1 协作场景必要元素

| 元素 | 说明 | 来源 |
|------|------|------|
| 参与者 | 场景组成员 | org.user |
| LLM能力 | 对话生成 | llm.chat |
| 知识检索 | RAG增强 | knowledge.search |
| 通知推送 | 消息提醒 | comm.email/message |

### 8.2 跨技能链接

| 协作场景 | 关联技能 | 必要能力 |
|---------|---------|---------|
| 会议场景 | skill-meeting | llm.chat, org.user, comm.email |
| 日报场景 | skill-daily-report | llm.chat, knowledge.search |
| 审批场景 | skill-approval | auth.permission, comm.message |

---

## 九、微型版说明

### 9.1 数据存储

使用 JSON 文件存储，不引入数据库：

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

### 9.2 API端点

| API | 方法 | 说明 |
|-----|------|------|
| /api/v1/scene-groups | GET/POST | 场景组CRUD |
| /api/v1/scene-groups/{id}/participants | GET/POST | 参与者管理 |
| /api/v1/scene-groups/{id}/capabilities | GET/POST | 能力绑定 |
| /api/v1/scene-groups/{id}/knowledge | GET/POST | 知识库绑定 |
| /api/v1/scene-groups/{id}/llm/config | GET/PUT | LLM配置 |
| /api/agent/list | GET | Agent列表 |
| /api/agent/{id} | GET | Agent详情 |
