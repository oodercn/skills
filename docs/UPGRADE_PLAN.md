# skills-scene 升级计划

> **文档版本**: 1.1  
> **规划日期**: 2026-03-15  
> **更新日期**: 2026-03-15  
> **规划范围**: skills-scene 模块功能升级与闭环构建

---

## 重要说明

> ⚠️ **工作流引擎暂缓开发**：工作流引擎相关功能暂缓开发，仅保留接口定义。待核心功能稳定后再行规划。

> ⚠️ **skill-scene 是独立实力工程**：skill-scene 不是普通技能，而是独立的 Spring Boot 应用服务，提供场景管理核心能力。

---

## 一、升级目标与原则

### 1.1 核心目标

1. **以需求规格为中心**：构建完整的功能闭环，确保每个功能都有完整的操作流程
2. **功能优先移植**：将已验证的功能从 temp/ooder-Nexus 迁移到 skills-scene
3. **协作技能分离**：区分核心功能与协作场景技能，明确职责边界
4. **分期分步开发**：渐进式开发，确保每个阶段可交付

### 1.2 升级原则

| 原则 | 说明 |
|------|------|
| **需求驱动** | 以 SCENE_REQUIREMENT_SPEC.md 和 CAPABILITY_REQUIREMENT_SPEC.md 为准 |
| **闭环验证** | 每个功能必须有完整的操作闭环（创建→配置→使用→监控） |
| **复用优先** | 优先复用 temp 目录已验证的页面设计和实现 |
| **职责分离** | 核心功能在 skills-scene，协作功能由独立技能提供 |

---

## 二、功能闭环逻辑设计

### 2.1 场景管理闭环

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          场景管理闭环                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │ 场景定义 │────▶│ 场景激活 │────▶│ 场景运行 │────▶│ 场景销毁 │             │
│   └────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘             │
│        │               │               │               │                   │
│        ▼               ▼               ▼               ▼                   │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │创建场景 │     │创建场景组│     │监控状态 │     │清理资源 │             │
│   │定义能力 │     │绑定能力  │     │执行工作流│     │归档日志 │             │
│   │定义角色 │     │加入参与者│     │处理事件 │     │解除绑定 │             │
│   │定义工作流│     │知识库绑定│     │能力调用 │     │通知参与者│             │
│   └─────────┘     └─────────┘     └─────────┘     └─────────┘             │
│                                                                             │
│   闭环验证点：                                                               │
│   ✓ 场景定义后可预览能力需求                                                 │
│   ✓ 激活前检查依赖和参与者                                                   │
│   ✓ 运行时可监控状态和日志                                                   │
│   ✓ 销毁后资源完全释放                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 能力管理闭环

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          能力管理闭环                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │ 能力发现 │────▶│ 能力注册 │────▶│ 能力绑定 │────▶│ 能力调用 │             │
│   └────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘             │
│        │               │               │               │                   │
│        ▼               ▼               ▼               ▼                   │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │本地扫描 │     │注册到注册表│    │分配CAP地址│    │权限检查 │             │
│   │远程发现 │     │解析能力定义│    │创建Agent │     │链路检查 │             │
│   │市场搜索 │     │验证参数 │     │建立Link │     │执行调用 │             │
│   │LLM创建 │     │设置访问级别│    │配置连接器│     │更新统计 │             │
│   └─────────┘     └─────────┘     └─────────┘     └─────────┘             │
│                                                                             │
│   闭环验证点：                                                               │
│   ✓ 发现的能力可查看详情和评价                                               │
│   ✓ 注册后自动分类和索引                                                     │
│   ✓ 绑定后可测试调用                                                         │
│   ✓ 调用有完整的日志和统计                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 参与者管理闭环

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          参与者管理闭环                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │ 参与者注册│────▶│ 加入场景 │────▶│ 角色执行 │────▶│ 离开场景 │             │
│   └────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘             │
│        │               │               │               │                   │
│        ▼               ▼               ▼               ▼                   │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐             │
│   │Agent注册│     │选择场景 │     │执行任务 │     │释放资源 │             │
│   │能力声明 │     │分配角色 │     │心跳上报 │     │解除绑定 │             │
│   │设备绑定 │     │权限分配 │     │状态同步 │     │状态更新 │             │
│   │状态初始化│    │能力绑定 │     │事件处理 │     │日志归档 │             │
│   └─────────┘     └─────────┘     └─────────┘     └─────────┘             │
│                                                                             │
│   闭环验证点：                                                               │
│   ✓ 注册后可被场景发现                                                       │
│   ✓ 加入后可执行分配的任务                                                   │
│   ✓ 执行过程可监控和审计                                                     │
│   ✓ 离开后资源正确释放                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、功能分类与职责划分

### 3.1 核心功能（skills-scene 负责）

| 功能模块 | 子功能 | 优先级 | 说明 |
|----------|--------|--------|------|
| **场景定义管理** | 场景CRUD、能力需求定义、角色定义、工作流定义 | P0 | 核心功能 |
| **场景组管理** | 场景组CRUD、生命周期管理、参与者管理 | P0 | 核心功能 |
| **能力注册管理** | 能力注册/注销、能力查询、能力分类 | P0 | 核心功能 |
| **能力绑定管理** | 绑定/解绑、CAP地址分配、Agent创建 | P0 | 核心功能 |
| **参与者管理** | Agent注册、心跳管理、状态同步 | P0 | 核心功能 |
| **链路管理** | Link创建/维护、链路状态、质量监控 | P1 | 核心功能 |
| **权限控制** | CAP地址权限、角色权限、访问控制 | P1 | 核心功能 |
| **工作流引擎** | 流程编排、步骤执行、事件触发 | P1 | 核心功能 |

### 3.2 协作场景技能功能（独立技能负责）

| 功能模块 | 协作技能 | 说明 | 协作需求文档 |
|----------|----------|------|--------------|
| **LLM对话** | skill-llm-chat | LLM对话界面、流式输出、对话历史 | 见 3.2.1 |
| **知识库管理** | skill-knowledge | 知识库CRUD、索引管理、RAG检索 | 见 3.2.2 |
| **日志管理** | skill-audit-log | 审计日志、执行日志、日志查询导出 | 见 3.2.3 |
| **监控告警** | skill-monitor | 系统监控、告警规则、通知推送 | 见 3.2.4 |
| **文件存储** | skill-storage | 文件上传下载、存储管理、共享链接 | 见 3.2.5 |
| **消息通知** | skill-notification | 消息推送、邮件发送、站内信 | 见 3.2.6 |

### 3.3 协作技能需求文档

#### 3.3.1 skill-llm-chat 协作需求

```yaml
skillId: skill-llm-chat
name: LLM对话技能
description: 提供LLM对话界面和对话管理功能

capabilities:
  - id: llm-chat
    name: LLM对话
    description: 与LLM进行对话交互
    parameters:
      - name: prompt
        type: string
        required: true
      - name: systemPrompt
        type: string
        required: false
      - name: stream
        type: boolean
        required: false
        defaultValue: true
    returns:
      type: ChatResponse

  - id: llm-chat-stream
    name: 流式对话
    description: SSE流式输出对话
    connectorType: WEBSOCKET

  - id: llm-model-switch
    name: 模型切换
    description: 切换当前使用的LLM模型

  - id: llm-chat-history
    name: 对话历史
    description: 获取和管理对话历史

依赖 skills-scene 提供的能力:
  - capability-register: 注册能力到场景
  - capability-invoke: 调用场景内其他能力
  - scene-context: 获取场景上下文信息
  - audit-log: 记录对话审计日志

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/llm/llm-chat.html
```

#### 3.3.2 skill-knowledge 协作需求

```yaml
skillId: skill-knowledge
name: 知识库管理技能
description: 提供知识库管理和检索功能

capabilities:
  - id: knowledge-base-create
    name: 创建知识库
    description: 创建新的知识库
  
  - id: knowledge-base-query
    name: 知识库查询
    description: 查询知识库内容
  
  - id: knowledge-rag
    name: RAG检索
    description: 向量检索增强生成
  
  - id: knowledge-index-rebuild
    name: 重建索引
    description: 重建知识库索引

依赖 skills-scene 提供的能力:
  - scene-group-bind: 绑定知识库到场景组
  - capability-invoke: 调用LLM进行知识处理
  - storage-service: 存储知识库文件

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/nexus/llm-management.html
```

#### 3.3.3 skill-audit-log 协作需求

```yaml
skillId: skill-audit-log
name: 审计日志技能
description: 提供审计日志记录和查询功能

capabilities:
  - id: audit-log-write
    name: 写入审计日志
    description: 记录操作审计日志
  
  - id: audit-log-query
    name: 查询审计日志
    description: 多条件查询审计日志
  
  - id: audit-log-export
    name: 导出审计日志
    description: 导出审计日志文件

依赖 skills-scene 提供的能力:
  - scene-event: 订阅场景生命周期事件
  - capability-event: 订阅能力调用事件
  - storage-service: 存储日志文件

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/audit/audit-logs.html
```

#### 3.3.4 skill-monitor 协作需求

```yaml
skillId: skill-monitor
name: 监控告警技能
description: 提供系统监控和告警功能

capabilities:
  - id: monitor-metrics
    name: 指标采集
    description: 采集系统运行指标
  
  - id: monitor-alert
    name: 告警触发
    description: 触发告警通知
  
  - id: monitor-dashboard
    name: 监控面板
    description: 提供监控数据可视化

依赖 skills-scene 提供的能力:
  - agent-heartbeat: 获取Agent心跳状态
  - capability-stats: 获取能力调用统计
  - link-status: 获取链路状态

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/monitoring/health-check.html
```

#### 3.3.5 skill-storage 协作需求

```yaml
skillId: skill-storage
name: 文件存储技能
description: 提供文件存储和管理功能

capabilities:
  - id: storage-upload
    name: 文件上传
    description: 上传文件到存储
  
  - id: storage-download
    name: 文件下载
    description: 下载存储文件
  
  - id: storage-share
    name: 文件共享
    description: 创建文件共享链接

依赖 skills-scene 提供的能力:
  - capability-register: 注册存储能力
  - scene-context: 获取场景存储配置

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/storage/storage-management.html
```

#### 3.3.6 skill-notification 协作需求

```yaml
skillId: skill-notification
name: 消息通知技能
description: 提供消息推送和通知功能

capabilities:
  - id: notification-send
    name: 发送通知
    description: 发送消息通知
  
  - id: notification-email
    name: 邮件发送
    description: 发送邮件通知
  
  - id: notification-subscribe
    name: 订阅管理
    description: 管理消息订阅

依赖 skills-scene 提供的能力:
  - participant-list: 获取场景参与者列表
  - scene-event: 订阅场景事件触发通知

UI页面参考: temp/ooder-Nexus/src/main/resources/static/console/pages/group/group-message.html
```

---

## 四、需求版本对比

### 4.1 skills-scene 现有实现 vs 最新需求

| 功能点 | 现有实现 | 需求规格 | 差距 | 处理方式 |
|--------|----------|----------|------|----------|
| 场景定义CRUD | ✅ 已实现 | 完整支持 | 无 | 保持 |
| 场景组管理 | ✅ 已实现 | 完整支持 | 无 | 保持 |
| 能力注册 | ✅ 已实现 | 完整支持 | 无 | 保持 |
| 能力绑定 | ✅ 已实现 | Agent+Link+Address | Link未实际建立 | 补充实现 |
| CAP地址权限 | ⚠️ 模型定义 | 系统区/通用区/扩展区 | 权限检查未实现 | 补充实现 |
| 工作流引擎 | ⚠️ 接口定义 | 完整工作流编排 | 执行引擎缺失 | 新增实现 |
| 参与者权限矩阵 | ❌ 未实现 | 角色-能力-权限映射 | 完全缺失 | 新增实现 |
| 持久化存储 | ❌ 仅内存 | 数据库存储 | 完全缺失 | 新增实现 |
| 发现渠道 | ⚠️ 本地FS | 9种发现渠道 | 远程渠道未实现 | 分期实现 |

### 4.2 需求版本确认

根据需求规格说明书版本记录：

| 文档 | 版本 | 日期 | 状态 |
|------|------|------|------|
| SCENE_REQUIREMENT_SPEC.md | v2.2 | 2026-03-02 | 最新 |
| CAPABILITY_REQUIREMENT_SPEC.md | v1.5 | 2026-03-02 | 最新 |

**确认结论**：当前需求规格说明书为最新版本，可直接作为开发依据。

---

## 五、分期开发计划

### 5.1 第一期：核心闭环完善（P0）

**目标**：完善核心功能闭环，确保基础功能可用

**周期**：4周

#### 5.1.1 持久化存储层

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| 数据库集成 | 集成H2/MySQL | - | DatabaseConfig.java |
| Repository层 | 实现数据访问层 | - | SceneRepository, CapabilityRepository |
| 实体映射 | JPA/MyBatis映射 | 需求规格数据模型 | Entity类 |
| 数据迁移 | 内存数据迁移到数据库 | - | 迁移脚本 |

#### 5.1.2 能力实例化完善

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| Link实际建立 | 实现真实链路建立 | CAPABILITY_REQUIREMENT_SPEC 4.3 | LinkServiceImpl |
| CAP地址权限 | 实现地址区域权限检查 | CAPABILITY_REQUIREMENT_SPEC 8.1 | CapAddressChecker |
| Agent创建优化 | 完善Agent创建流程 | - | AgentFactory |
| 能力调用链路 | 完善调用链路追踪 | - | CapabilityInvoker |

#### 5.1.3 场景生命周期完善

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| 状态机实现 | 完整状态转换逻辑 | SCENE_REQUIREMENT_SPEC 5.1 | SceneStateMachine |
| 事件发布 | 生命周期事件发布 | SCENE_REQUIREMENT_SPEC 5.2 | SceneEventPublisher |
| 条件校验 | 状态转换前条件校验 | - | SceneValidator |
| 快照恢复 | 完善快照恢复逻辑 | - | SceneSnapshotService |

#### 5.1.4 UI页面开发

| 页面 | 说明 | 参考设计 | 产出 |
|------|------|----------|------|
| 场景定义管理 | 场景CRUD界面 | temp/pages/scene/scene-definition.html | scene-definition.html |
| 场景组管理 | 场景组详情界面 | temp/pages/scene/scene-group.html | scene-group.html |
| 能力管理 | 能力列表和详情 | temp/pages/nexus/capability-management.html | capability-list.html |
| 参与者管理 | 参与者列表和状态 | temp/pages/nexus/endagent-management.html | participant-list.html |

### 5.2 第二期：权限控制（P1）

**目标**：实现权限控制，工作流引擎暂缓

**周期**：2周

> ⚠️ **工作流引擎暂缓**：仅保留接口定义，不实现具体逻辑。待核心功能稳定后再行规划。

#### 5.2.1 工作流接口定义（暂缓实现）

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| 接口定义 | 定义工作流引擎接口 | SCENE_REQUIREMENT_SPEC 3.x | WorkflowEngine.java (interface only) |
| 数据模型 | 定义工作流数据模型 | - | WorkflowDefinition.java, WorkflowInstance.java |
| 触发器接口 | 定义触发器接口 | - | TriggerManager.java (interface only) |

#### 5.2.2 权限控制

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| 权限矩阵 | 角色-能力-权限映射 | CAPABILITY_REQUIREMENT_SPEC 8 | PermissionMatrix |
| 访问控制 | 访问控制检查 | - | AccessController |
| 角色管理 | 角色CRUD和分配 | - | RoleService |
| 权限审计 | 权限操作审计 | - | PermissionAudit |

#### 5.2.3 UI页面开发

| 页面 | 说明 | 参考设计 | 产出 |
|------|------|----------|------|
| 权限管理 | 权限矩阵配置 | temp/pages/admin/users.html | permission-matrix.html |
| 角色管理 | 角色定义和分配 | - | role-management.html |

### 5.3 第三期：发现渠道与监控（P2）

**目标**：完善发现渠道和监控功能

**周期**：4周

#### 5.3.1 发现渠道

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| GitHub发现 | GitHub仓库集成 | CAPABILITY_REQUIREMENT_SPEC 14 | GitHubDiscoverer |
| Gitee发现 | Gitee仓库集成 | - | GiteeDiscoverer |
| 技能中心 | 技能中心API对接 | - | SkillCenterClient |
| 智能推荐 | 能力推荐算法 | - | CapabilityRecommender |

#### 5.3.2 监控告警

| 任务 | 说明 | 参考 | 产出 |
|------|------|------|------|
| 指标采集 | 系统指标采集 | - | MetricsCollector |
| 告警规则 | 告警规则配置 | - | AlertRuleEngine |
| 通知推送 | 告警通知推送 | - | NotificationSender |
| 监控面板 | 监控数据可视化 | temp/pages/monitoring/health-check.html | monitor-dashboard.html |

#### 5.3.3 UI页面开发

| 页面 | 说明 | 参考设计 | 产出 |
|------|------|----------|------|
| 能力市场 | 能力发现和安装 | CAPABILITY_REQUIREMENT_SPEC 15 | capability-market.html |
| 链路管理 | 链路状态和质量 | temp/pages/nexus/link-management.html | link-management.html |
| 网络拓扑 | P2P网络可视化 | temp/pages/nexus/p2p-visualization.html | network-topology.html |
| 系统监控 | 系统状态监控 | temp/pages/nexus/system-status.html | system-monitor.html |

### 5.4 第四期：协作技能集成（P3）

**目标**：集成协作场景技能，完善生态

**周期**：4周

#### 5.4.1 协作技能开发

| 技能 | 优先级 | 说明 |
|------|--------|------|
| skill-llm-chat | P0 | LLM对话技能 |
| skill-audit-log | P0 | 审计日志技能 |
| skill-knowledge | P1 | 知识库管理技能 |
| skill-monitor | P1 | 监控告警技能 |
| skill-storage | P2 | 文件存储技能 |
| skill-notification | P2 | 消息通知技能 |

#### 5.4.2 技能集成

| 任务 | 说明 | 产出 |
|------|------|------|
| 技能注册 | 技能自动注册到场景 | SkillRegistration |
| 能力绑定 | 技能能力绑定到场景组 | CapabilityBinding |
| UI集成 | 技能UI页面集成 | 集成页面 |

---

## 六、开发任务清单

### 6.1 第一期任务明细

#### Phase 1.1: 持久化存储（Week 1-2）

```yaml
tasks:
  - id: DB-001
    name: 数据库配置集成
    description: 集成H2/MySQL数据库配置
    priority: P0
    effort: 2d
    dependencies: []
    outputs:
      - DatabaseConfig.java
      - application-db.yml
    
  - id: DB-002
    name: 实体类定义
    description: 根据需求规格定义JPA实体
    priority: P0
    effort: 3d
    dependencies: [DB-001]
    outputs:
      - SceneEntity.java
      - SceneGroupEntity.java
      - CapabilityEntity.java
      - CapabilityBindingEntity.java
      - ParticipantEntity.java
    
  - id: DB-003
    name: Repository层实现
    description: 实现数据访问层
    priority: P0
    effort: 3d
    dependencies: [DB-002]
    outputs:
      - SceneRepository.java
      - SceneGroupRepository.java
      - CapabilityRepository.java
    
  - id: DB-004
    name: Service层迁移
    description: 将内存实现迁移到数据库
    priority: P0
    effort: 4d
    dependencies: [DB-003]
    outputs:
      - SceneServiceDbImpl.java
      - SceneGroupServiceDbImpl.java
      - CapabilityServiceDbImpl.java
```

#### Phase 1.2: 能力实例化（Week 2-3）

```yaml
tasks:
  - id: CAP-001
    name: Link服务实现
    description: 实现真实的链路建立和维护
    priority: P0
    effort: 4d
    dependencies: [DB-004]
    reference: CAPABILITY_REQUIREMENT_SPEC.md#4.3
    outputs:
      - LinkServiceImpl.java
      - LinkStore.java
    
  - id: CAP-002
    name: CAP地址权限检查
    description: 实现地址区域权限检查
    priority: P0
    effort: 3d
    dependencies: [CAP-001]
    reference: CAPABILITY_REQUIREMENT_SPEC.md#8.1
    outputs:
      - CapAddressChecker.java
      - AddressZone.java
    
  - id: CAP-003
    name: 能力调用链路完善
    description: 完善能力调用链路和追踪
    priority: P0
    effort: 3d
    dependencies: [CAP-002]
    outputs:
      - CapabilityInvokerImpl.java
      - InvokeTracer.java
```

#### Phase 1.3: UI开发（Week 3-4）

```yaml
tasks:
  - id: UI-001
    name: 场景定义管理页面
    description: 场景CRUD界面
    priority: P0
    effort: 3d
    dependencies: []
    reference: temp/pages/scene/scene-definition.html
    outputs:
      - scene-definition.html
      - scene-definition.js
    
  - id: UI-002
    name: 场景组管理页面
    description: 场景组详情和管理界面
    priority: P0
    effort: 3d
    dependencies: [UI-001]
    reference: temp/pages/scene/scene-group.html
    outputs:
      - scene-group.html
      - scene-group.js
    
  - id: UI-003
    name: 能力管理页面
    description: 能力列表和详情界面
    priority: P0
    effort: 2d
    dependencies: []
    reference: temp/pages/nexus/capability-management.html
    outputs:
      - capability-list.html
      - capability-list.js
    
  - id: UI-004
    name: 参与者管理页面
    description: 参与者列表和状态界面
    priority: P0
    effort: 2d
    dependencies: []
    reference: temp/pages/nexus/endagent-management.html
    outputs:
      - participant-list.html
      - participant-list.js
```

### 6.2 第二期任务明细

#### Phase 2.1: 工作流引擎（Week 5-6）

```yaml
tasks:
  - id: WF-001
    name: 工作流定义解析
    description: 解析YAML工作流定义
    priority: P1
    effort: 3d
    reference: SCENE_REQUIREMENT_SPEC.md#3.x
    outputs:
      - WorkflowParser.java
      - WorkflowDef.java
    
  - id: WF-002
    name: 步骤执行器
    description: 实现步骤执行逻辑
    priority: P1
    effort: 4d
    dependencies: [WF-001]
    outputs:
      - StepExecutor.java
      - StepContext.java
    
  - id: WF-003
    name: 触发器管理
    description: 定时/事件/手动触发
    priority: P1
    effort: 3d
    dependencies: [WF-002]
    outputs:
      - TriggerManager.java
      - CronTrigger.java
      - EventTrigger.java
```

#### Phase 2.2: 权限控制（Week 7-8）

```yaml
tasks:
  - id: PERM-001
    name: 权限矩阵实现
    description: 角色-能力-权限映射
    priority: P1
    effort: 4d
    reference: CAPABILITY_REQUIREMENT_SPEC.md#8
    outputs:
      - PermissionMatrix.java
      - PermissionEntry.java
    
  - id: PERM-002
    name: 访问控制检查
    description: 访问控制检查逻辑
    priority: P1
    effort: 3d
    dependencies: [PERM-001]
    outputs:
      - AccessController.java
      - PermissionChecker.java
```

---

## 七、页面设计参考索引

### 7.1 temp 目录页面参考

| 目标页面 | 参考文件 | 复用程度 |
|----------|----------|----------|
| 场景定义管理 | temp/pages/scene/scene-definition.html | 高 |
| 场景组管理 | temp/pages/scene/scene-group.html | 高 |
| 能力管理 | temp/pages/nexus/capability-management.html | 高 |
| 参与者管理 | temp/pages/nexus/endagent-management.html | 高 |
| 链路管理 | temp/pages/nexus/link-management.html | 高 |
| 网络拓扑 | temp/pages/nexus/p2p-visualization.html | 中 |
| 系统监控 | temp/pages/nexus/system-status.html | 中 |
| LLM对话 | temp/pages/llm/llm-chat.html | 高（协作技能） |
| 审计日志 | temp/pages/audit/audit-logs.html | 高（协作技能） |
| 存储管理 | temp/pages/storage/storage-management.html | 中（协作技能） |

### 7.2 设计规范

```yaml
UI设计规范:
  框架: 原生JavaScript + CSS（参考temp实现）
  图标: RemixIcon
  主题: 支持明暗主题切换
  响应式: 支持移动端适配
  
组件规范:
  卡片: nx-card
  按钮: nx-btn (primary/secondary/ghost/danger)
  表格: nx-table
  表单: nx-form-group, nx-input
  模态框: modal
  统计卡片: nx-stat-card
  
交互规范:
  列表页: 搜索 + 过滤 + 表格 + 分页
  详情页: 左侧列表 + 右侧详情
  表单页: 模态框或独立页面
  操作反馈: Toast提示 + 确认对话框
```

---

## 八、验收标准

### 8.1 第一期验收标准

| 功能 | 验收标准 |
|------|----------|
| 持久化存储 | 数据重启后不丢失，支持事务 |
| 能力实例化 | Link可建立、可监控、可断开重连 |
| CAP地址权限 | 系统区/通用区/扩展区权限正确隔离 |
| 场景生命周期 | 状态转换正确，事件正确发布 |
| UI页面 | 页面可正常使用，操作闭环完整 |

### 8.2 第二期验收标准

| 功能 | 验收标准 |
|------|----------|
| 工作流引擎 | 可解析YAML定义，可执行步骤，可触发 |
| 权限控制 | 权限矩阵正确，访问控制有效 |
| UI页面 | 工作流可设计，权限可配置 |

### 8.3 第三期验收标准

| 功能 | 验收标准 |
|------|----------|
| 发现渠道 | GitHub/Gitee可发现技能 |
| 监控告警 | 指标可采集，告警可触发 |
| UI页面 | 能力市场可用，监控面板完整 |

---

## 九、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| 需求变更 | 开发返工 | 小步迭代，及时同步 |
| 技术难点 | 进度延迟 | 提前调研，备选方案 |
| 资源不足 | 功能裁剪 | 优先级排序，分期交付 |
| 集成问题 | 系统不稳定 | 充分测试，灰度发布 |

---

*文档生成时间: 2026-03-15*
