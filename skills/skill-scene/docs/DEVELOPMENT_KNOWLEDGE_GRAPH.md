# Agent-Link-Capability 开发知识图谱

> **文档版本**: v2.0  
> **创建日期**: 2026-03-08  
> **适用范围**: Ooder 页面与 API 开发

---

## 一、现有代码映射

### 1.1 Agent 模块

| 组件 | 文件路径 | 状态 |
|------|----------|------|
| **模型** | `skill-agent/.../model/EndAgent.java` | ✅ 已实现 |
| **DTO** | `skill-agent/.../dto/AgentInfo.java` | ✅ 已实现 |
| **服务** | `skill-agent/.../service/AgentService.java` | ✅ 已实现 |
| **控制器** | `skill-agent/.../controller/AgentController.java` | ✅ 已实现 |
| **页面** | - | ❌ 缺失 |

**现有 API**：
```
POST   /api/agent/register        # 注册 Agent
GET    /api/agent/list            # 获取 Agent 列表
GET    /api/agent/{agentId}       # 获取 Agent 详情
DELETE /api/agent/{agentId}       # 删除 Agent
POST   /api/agent/{agentId}/heartbeat  # Agent 心跳
POST   /api/agent/{agentId}/command    # 执行命令
GET    /api/agent/{agentId}/network    # 获取网络状态
GET    /api/agent/{agentId}/status     # 获取状态
```

### 1.2 Link/Network 模块

| 组件 | 文件路径 | 状态 |
|------|----------|------|
| **模型** | `skill-network/.../model/NetworkNode.java` | ✅ 已实现 |
| **Link DTO** | `skill-network/.../dto/NetworkLink.java` | ✅ 已实现 |
| **拓扑模型** | `skill-network/.../model/NetworkTopology.java` | ✅ 已实现 |
| **服务** | `skill-network/.../service/NetworkService.java` | ✅ 已实现 |
| **控制器** | `skill-network/.../controller/NetworkController.java` | ✅ 已实现 |
| **页面** | - | ❌ 缺失 |

**现有 API**：
```
GET    /api/network/status        # 获取网络状态
GET    /api/network/nodes         # 获取节点列表
GET    /api/network/nodes/{nodeId} # 获取节点详情
POST   /api/network/start         # 启动 P2P 服务
POST   /api/network/stop          # 停止 P2P 服务
GET    /api/network/topology      # 获取网络拓扑
GET    /api/network/devices       # 获取设备列表
GET    /api/network/stats         # 获取网络统计
```

### 1.3 CapabilityBinding 模块

| 组件 | 文件路径 | 状态 |
|------|----------|------|
| **模型** | `skill-scene/.../model/CapabilityBinding.java` | ✅ 已实现 |
| **状态枚举** | `skill-scene/.../model/CapabilityBindingStatus.java` | ✅ 已实现 |
| **提供者类型** | `skill-scene/.../model/CapabilityProviderType.java` | ✅ 已实现 |
| **连接器类型** | `skill-scene/.../model/ConnectorType.java` | ✅ 已实现 |
| **服务** | `skill-scene/.../service/CapabilityBindingService.java` | ✅ 已实现 |
| **控制器** | `skill-scene/.../controller/CapabilityController.java` | ✅ 已实现 |
| **页面** | - | ❌ 缺失 |

**现有 API**：
```
GET    /api/v1/capabilities/{id}/bindings    # 获取能力绑定
POST   /api/v1/capabilities/bindings         # 创建绑定
GET    /api/v1/capabilities/bindings/{id}    # 获取绑定详情
DELETE /api/v1/capabilities/bindings/{id}    # 删除绑定
POST   /api/v1/capabilities/bindings/{id}/status  # 更新绑定状态
POST   /api/v1/capabilities/bindings/{id}/test    # 测试绑定
```

---

## 二、核心关系图谱

### 2.1 实体关系图

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              实体关系图                                       │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌───────────────┐       ┌───────────────┐       ┌───────────────┐          │
│  │    Agent      │       │  NetworkLink  │       │  Capability   │          │
│  │ (skill-agent) │       │(skill-network)│       │ (skill-scene) │          │
│  ├───────────────┤       ├───────────────┤       ├───────────────┤          │
│  │ PK agentId    │       │ PK linkId     │       │ PK capabilityId│         │
│  │    name       │       │ FK sourceNode │       │    name       │          │
│  │    type       │       │ FK targetNode │       │    type       │          │
│  │    status     │       │    linkType   │       │    ownership  │          │
│  │    ipAddress  │       │    status     │       │    visibility │          │
│  │    port       │       │    latency    │       │    parentSkill│          │
│  └───────────────┘       │    bandwidth  │       └───────────────┘          │
│          │               └───────────────┘               │                  │
│          │                       │                       │                  │
│          │               ┌───────┴───────┐               │                  │
│          │               │               │               │                  │
│          ▼               ▼               ▼               ▼                  │
│  ┌─────────────────────────────────────────────────────────────────┐        │
│  │                    CapabilityBinding                             │        │
│  │                      (skill-scene)                               │        │
│  ├─────────────────────────────────────────────────────────────────┤        │
│  │ PK bindingId                                                     │        │
│  │ FK sceneGroupId  ──────► SceneGroup                              │        │
│  │ FK capabilityId  ──────► Capability    【关联】                  │        │
│  │ FK agentId       ──────► Agent          【关联】                  │        │
│  │ FK linkId        ──────► NetworkLink    【关联】                  │        │
│  │    capId         (场景内短ID)                                     │        │
│  │    capAddress    (CAP地址)                                       │        │
│  │    providerType  (提供者类型)                                     │        │
│  │    connectorType (连接器类型)                                     │        │
│  │    status        (绑定状态)                                       │        │
│  └─────────────────────────────────────────────────────────────────┘        │
│                                      │                                       │
│                                      ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────┐        │
│  │                         SceneGroup                               │        │
│  │                        (skill-scene)                             │        │
│  ├─────────────────────────────────────────────────────────────────┤        │
│  │ PK sceneGroupId                                                  │        │
│  │    name                                                          │        │
│  │    type                                                          │        │
│  │    ownerId                                                       │        │
│  │    status                                                        │        │
│  └─────────────────────────────────────────────────────────────────┘        │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 关键关系说明

| 关系 | 类型 | 说明 | 代码位置 |
|------|------|------|----------|
| Agent → CapabilityBinding | 1:N | Agent 执行多个绑定 | `CapabilityBinding.agentId` |
| Link → CapabilityBinding | 1:N | Link 服务多个绑定 | `CapabilityBinding.linkId` |
| Capability → CapabilityBinding | 1:N | 能力被多次绑定 | `CapabilityBinding.capabilityId` |
| SceneGroup → CapabilityBinding | 1:N | 场景组有多个绑定 | `CapabilityBinding.sceneGroupId` |

**重要原则**：
- **关系是关系，关联处理即可**
- Agent、Link、Capability 各自独立管理
- CapabilityBinding 作为关联表，存储引用关系
- 不在 Agent/Link 中存储反向引用

---

## 三、开发计划

### 3.1 页面开发清单

| 页面 | 路径 | 依赖模块 | 状态 | 优先级 |
|------|------|----------|------|--------|
| Agent 列表 | `/console/pages/agent-list.html` | skill-agent | ❌ 待开发 | P0 |
| Agent 详情 | `/console/pages/agent-detail.html` | skill-agent | ❌ 待开发 | P1 |
| Link 列表 | `/console/pages/link-list.html` | skill-network | ❌ 待开发 | P0 |
| 网络拓扑 | `/console/pages/network-topology.html` | skill-network | ❌ 待开发 | P1 |
| 能力绑定 | `/console/pages/capability-binding.html` | skill-scene | ❌ 待开发 | P0 |
| 场景监控 | `/console/pages/scene-monitor.html` | skill-scene | ❌ 待开发 | P2 |

### 3.2 API 扩展清单

#### 3.2.1 Agent API（无需扩展）

现有 API 已满足需求：
- 注册、列表、详情、删除
- 心跳、命令执行
- 网络状态、状态查询

**可能需要扩展**：
```
GET  /api/agent/{agentId}/bindings    # 获取 Agent 关联的绑定列表
```

#### 3.2.2 Link API（需要扩展）

现有 API：
- 网络状态、节点列表、拓扑

**需要扩展**：
```
GET    /api/network/links             # 获取 Link 列表
GET    /api/network/links/{linkId}    # 获取 Link 详情
POST   /api/network/links             # 建立 Link
DELETE /api/network/links/{linkId}    # 断开 Link
PUT    /api/network/links/{linkId}    # 更新 Link 配置
```

#### 3.2.3 CapabilityBinding API（需要扩展）

现有 API：
- 创建绑定、获取绑定、删除绑定
- 更新状态、测试绑定

**需要扩展**：
```
GET  /api/v1/capabilities/bindings/by-scene-group/{sceneGroupId}  # 按场景组查询
PUT /api/v1/capabilities/bindings/{bindingId}/agent              # 绑定 Agent
PUT /api/v1/capabilities/bindings/{bindingId}/link               # 绑定 Link
```

---

## 四、开发顺序

### 4.1 第一阶段：基础页面（P0）

```
1. Agent 列表页面
   ├── 复用现有 API: /api/agent/list
   ├── 展示: Agent ID、名称、类型、状态、IP
   └── 操作: 查看、删除、心跳检测

2. Link 列表页面
   ├── 扩展 API: /api/network/links
   ├── 展示: Link ID、源节点、目标节点、类型、状态
   └── 操作: 建立、断开、查看详情

3. 能力绑定页面
   ├── 扩展 API: /api/v1/capabilities/bindings/by-scene-group/{id}
   ├── 展示: 绑定列表、Agent 选择、Link 选择
   └── 操作: 创建绑定、绑定 Agent/Link、测试
```

### 4.2 第二阶段：详情页面（P1）

```
1. Agent 详情页面
   ├── 复用现有 API: /api/agent/{agentId}
   ├── 展示: 详细信息、关联绑定、网络状态
   └── 操作: 编辑、命令执行

2. 网络拓扑页面
   ├── 复用现有 API: /api/network/topology
   ├── 展示: 可视化拓扑图、节点状态
   └── 操作: 节点详情、Link 管理
```

### 4.3 第三阶段：监控页面（P2）

```
1. 场景监控页面
   ├── 新增 API: 场景运行状态、能力调用统计
   ├── 展示: 场景状态、Agent 状态、Link 状态
   └── 操作: 启停、日志查看
```

---

## 五、页面设计规范

### 5.1 统一布局

```html
<!-- 页面结构 -->
<div class="nx-page">
  <aside class="nx-page__sidebar"><!-- 导航菜单 --></aside>
  <main class="nx-page__content">
    <header class="nx-page__header"><!-- 页面标题 --></header>
    <div class="nx-page__main">
      <!-- 统计卡片 -->
      <div class="stats-row">...</div>
      
      <!-- 筛选栏 -->
      <div class="filter-bar">...</div>
      
      <!-- 数据列表 -->
      <div class="data-grid">...</div>
    </div>
  </main>
</div>
```

### 5.2 统一组件

| 组件 | 说明 |
|------|------|
| 统计卡片 | 显示总数、在线数、离线数等 |
| 筛选栏 | 搜索框、类型筛选、状态筛选 |
| 数据表格 | 分页、排序、操作按钮 |
| 状态标签 | 不同颜色表示不同状态 |
| 详情弹窗 | 查看详细信息 |

### 5.3 状态颜色规范

| 状态 | 颜色 | CSS 类 |
|------|------|--------|
| 在线/活跃/正常 | 绿色 | `status-success` |
| 离线/不活跃 | 灰色 | `status-secondary` |
| 忙碌/绑定中 | 蓝色 | `status-info` |
| 错误/故障 | 红色 | `status-danger` |
| 维护/待定 | 黄色 | `status-warning` |

---

## 六、关联处理原则

### 6.1 不重复建设

| 原则 | 说明 |
|------|------|
| Agent 管理 | 只在 skill-agent 中实现 |
| Link 管理 | 只在 skill-network 中实现 |
| Capability 管理 | 只在 skill-scene 中实现 |
| 关联关系 | 通过 CapabilityBinding 存储 |

### 6.2 关联查询

```java
// 查询 Agent 关联的绑定
List<CapabilityBinding> bindings = bindingService.listByAgent(agentId);

// 查询 Link 关联的绑定
List<CapabilityBinding> bindings = bindingService.listByLink(linkId);

// 查询场景组的所有绑定
List<CapabilityBinding> bindings = bindingService.listBySceneGroup(sceneGroupId);
```

### 6.3 页面跳转

```
Agent 列表 → 点击 Agent → Agent 详情（显示关联绑定）
Link 列表 → 点击 Link → Link 详情（显示关联绑定）
场景组详情 → 能力绑定 Tab → 绑定列表（选择 Agent/Link）
```

---

## 七、开发检查清单

### 7.1 页面开发检查

- [ ] 使用统一的页面布局
- [ ] 使用统一的 CSS 样式
- [ ] 实现统计卡片
- [ ] 实现筛选功能
- [ ] 实现分页功能
- [ ] 实现状态标签
- [ ] 实现操作按钮
- [ ] 实现详情弹窗

### 7.2 API 开发检查

- [ ] 复用现有 API
- [ ] 只扩展必要的新 API
- [ ] 不重复实现已有功能
- [ ] 关联关系通过 ID 引用
- [ ] 返回数据包含必要的关联信息

### 7.3 关联处理检查

- [ ] 不在 Agent 中存储绑定列表
- [ ] 不在 Link 中存储绑定列表
- [ ] 通过 CapabilityBinding 查询关联
- [ ] 页面展示时动态查询关联数据

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08
