# 场景特性需求规格说明书 v2.3

> **文档版本**: v2.3.1  
> **发布日期**: 2026-03-05  
> **适用范围**: skill-scene 模块开发  
> **文档状态**: 正式发布  
> **术语版本**: GLOSSARY_V2.md
> **更新说明**: 整合场景技能分类体系、Engine协作完成状态

---

## 一、概述

### 1.1 文档目的

本文档定义 Ooder 场景特性系统的完整需求规格，基于**能力驱动架构**，包括：
- 场景特性模型与关联关系
- 场景技能分类体系（完整/技术/半自动）
- 场景特性生命周期管理
- 参与者模型（User/Agent/SuperAgent）
- 能力驱动机制（mainFirst）
- 能力调用链规范
- 安全与权限控制
- Engine协作接口规范

### 1.2 核心概念

本文档使用的核心术语请参考 [术语表v2](GLOSSARY_V2.md)，以下仅列出场景特性域核心术语：

| 概念 | 英文标识 | 定义 |
|------|----------|------|
| **场景特性** | SceneCapability | 自驱型SuperAgent能力，包含子能力和驱动能力，可涌现新行为 |
| **场景技能** | SceneSkill | 具备场景特性的Skill，类型为`scene-skill` |
| **自驱入口** | mainFirst | 场景特性的启动入口，包含自检、自启、自驱、协作启动 |
| **场景组** | SceneGroup | 共享KEY和VFS资源的Agent集合，实现场景的协作和故障切换 |
| **参与者** | Participant | 场景中的活动主体，可以是用户、Agent或SuperAgent |
| **角色** | Role | 参与者在场景中的职责定义，决定其可访问的能力 |
| **能力调用链** | capabilityChains | 能力的有序调用序列，支持条件和分支 |
| **驱动能力** | DriverCapability | 内置驱动能力（scheduler/event-listener等） |

### 1.3 能力驱动架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动架构核心                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   核心原则：场景即能力（Scene as Capability）                                │
│   ══════════════════════════════════                                        │
│                                                                             │
│   1. 场景特性 = SuperAgent能力                                              │
│      ├── 包含子能力（ATOMIC/COMPOSITE）                                     │
│      ├── 包含驱动能力（DRIVER_CAPABILITY）                                  │
│      └── 可涌现新行为                                                       │
│                                                                             │
│   2. 自驱机制 (mainFirst)                                                   │
│      ├── selfCheck(): 检查子能力就绪                                        │
│      ├── selfStart(): 初始化子能力                                          │
│      ├── startCollaboration(): 启动协作能力                                 │
│      └── selfDrive(): 驱动场景运行                                          │
│                                                                             │
│   3. 驱动能力（DRIVER_CAPABILITY）                                          │
│      ├── intent-receiver: 接收用户意图                                      │
│      ├── scheduler: 时间驱动                                                │
│      ├── event-listener: 事件监听                                           │
│      ├── capability-invoker: 能力调用                                       │
│      └── collaboration-coordinator: 协作协调                                │
│                                                                             │
│   4. 场景技能分类（v2.3新增）                                               │
│      ├── 自驱业务场景: mainFirst=true + 业务语义完整                        │
│      ├── 自驱系统场景: mainFirst=true + 业务语义弱                          │
│      └── 触发业务场景: mainFirst=false + 业务语义完整                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、场景技能分类体系（v2.3核心更新）

### 2.1 分类总览

基于四项标准，场景技能分为**三大类**：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        场景技能三大分类体系                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  第一类：自驱业务场景 (Auto Business Scene)                       │   │
│  │  [标准1✓ + 标准2✓ + 标准3✓ + 标准4✓]                              │   │
│  │  自动驱动的业务场景，具备完整自驱能力和业务语义                      │   │
│  │                                                                 │   │
│  │  核心特征:                                                       │   │
│  │  • metadata.type = scene-skill                                  │   │
│  │  • sceneCapabilities 非空                                       │   │
│  │  • mainFirst = true（可自驱）                                   │   │
│  │  • driverConditions + participants 完整（有业务语义）            │   │
│  │                                                                 │   │
│  │  典型场景: LLM对话、知识问答、团队协作、智能监控                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  第二类：自驱系统场景 (Auto System Scene)                         │   │
│  │  [标准1✓ + 标准2✓ + 标准3✓ + 标准4✗]                              │   │
│  │  自动驱动的系统场景，具备自驱能力但无业务语义                        │   │
│  │                                                                 │   │
│  │  核心特征:                                                       │   │
│  │  • metadata.type = scene-skill                                  │   │
│  │  • sceneCapabilities 非空                                       │   │
│  │  • mainFirst = true（可自驱）                                   │   │
│  │  • 无driverConditions/participants（无业务语义）                 │   │
│  │  • visibility = internal（仅系统内部可见）                       │   │
│  │                                                                 │   │
│  │  典型场景: 系统清理、缓存刷新、索引重建、数据同步                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  第三类：触发业务场景 (Trigger Business Scene)                    │   │
│  │  [标准1✓ + 标准2✓ + 标准3✗ + 标准4✓]                              │   │
│  │  外部触发的业务场景，具备业务语义但需要人工或API触发                 │   │
│  │                                                                 │   │
│  │  核心特征:                                                       │   │
│  │  • metadata.type = scene-skill                                  │   │
│  │  • sceneCapabilities 非空                                       │   │
│  │  • mainFirst = false（不可自驱）                                │   │
│  │  • driverConditions + participants 完整（有业务语义）            │   │
│  │  • 需人工/API触发启动                                           │   │
│  │                                                                 │   │
│  │  典型场景: 审批流程、手动报告、数据分析、配置变更                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 四项标准定义

| 标准 | 技术标识 | 业务含义 | 判定方法 |
|------|---------|---------|---------|
| **标准1** | `metadata.type = scene-skill` | 类型声明 | 检查skill.yaml元数据 |
| **标准2** | `spec.sceneCapabilities` 非空 | 场景特性声明 | 检查spec.sceneCapabilities |
| **标准3** | `mainFirst = true` | 自驱能力 | 检查sceneCapabilities[].mainFirst |
| **标准4** | 完整业务场景语义 | 业务价值 | 人工判定：可发现、可分发、可自驱 |

### 2.3 分类对比

| 维度 | 自驱业务场景 | 自驱系统场景 | 触发业务场景 |
|------|-------------|-------------|-------------|
| **标准组合** | 1✓2✓3✓4✓ | 1✓2✓3✓4✗ | 1✓2✓3✗4✓ |
| **自驱能力** | ✓ 有 | ✓ 有 | ✗ 无 |
| **业务语义** | ✓ 强 | ✗ 弱 | ✓ 强 |
| **触发方式** | 自动（定时/事件） | 自动（定时） | 人工/API |
| **参与者** | 有定义 | 无 | 有定义 |
| **可见性** | public | internal | public |
| **生命周期** | 完整（DRAFT→ACTIVE→COMPLETED） | 循环（ACTIVE↔SCHEDULED↔RUNNING） | 一次性（DRAFT→PENDING→RUNNING→COMPLETED） |
| **典型场景** | 对话、协作、监控 | 清理、同步 | 审批、报告 |

### 2.4 分类检测算法

```java
public SceneSkillCategory detectCategory(SkillPackage skill) {
    // 标准1: 检查 metadata.type
    if (!"scene-skill".equals(skill.getMetadata().getType())) {
        return SceneSkillCategory.NOT_SCENE_SKILL;
    }
    
    // 标准2: 检查 sceneCapabilities
    List<SceneCapability> sceneCaps = skill.getSpec().getSceneCapabilities();
    if (sceneCaps == null || sceneCaps.isEmpty()) {
        return SceneSkillCategory.NOT_SCENE_SKILL;
    }
    
    SceneCapability cap = sceneCaps.get(0);
    
    // 标准3: 检查 mainFirst
    boolean hasMainFirst = cap.isMainFirst() 
        && cap.getMainFirstConfig() != null;
    
    // 标准4: 检查业务语义
    boolean hasBusinessSemantics = cap.getDriverConditions() != null 
        && !cap.getDriverConditions().isEmpty()
        && cap.getParticipants() != null
        && !cap.getParticipants().isEmpty();
    
    // 分类判断
    if (hasMainFirst && hasBusinessSemantics) {
        return SceneSkillCategory.FULL;
    } else if (hasMainFirst && !hasBusinessSemantics) {
        return SceneSkillCategory.TECHNICAL;
    } else if (!hasMainFirst && hasBusinessSemantics) {
        return SceneSkillCategory.SEMI_AUTO;
    } else {
        return SceneSkillCategory.INVALID;
    }
}
```

---

## 三、核心问题讨论与结论

### 3.1 场景特性创建与场景组的关系

**问题**：创建场景特性时，是否同时创建场景组？

**结论**：否，场景组在场景特性激活时创建。

```
场景特性自启 → 绑定子能力 → 自驱运行 → 创建场景组（如有协作能力）
```

**设计理由**：
- 场景特性自启是静态配置阶段
- 场景组是运行时协作实体
- 只有自驱运行时才需要建立场景间通信

### 3.2 依赖安装时机

**问题**：启动场景特性后，创建场景组，依赖安装在创建前还是创建后？

**结论**：依赖安装在场景组创建前。

```
Phase 1: 安装检查 → 检查/安装 Skills
Phase 2: 场景特性自启 → selfStart()
Phase 3: 场景组创建 → 创建 SceneGroupInfo
Phase 4: 场景特性自驱 → selfDrive()
```

**设计理由**：
- 场景组需要所有 Skills 已就绪
- 避免运行时发现依赖缺失
- 保证场景组初始化成功

### 3.3 依赖配置位置

**问题**：核心依赖配置是在 Skills 还是在场景特性中？

**结论**：分层配置，各司其职。

| 层级 | 位置 | 职责 | 示例 |
|------|------|------|------|
| Skill 层 | skill-manifest.yaml | 技术依赖 | skill-knowledge-ui 依赖 skill-knowledge-base |
| 场景特性层 | SceneCapabilityDef | 业务组合 | 知识问答场景特性包含 kb + rag + llm |

---

## 四、生命周期管理

### 4.1 生命周期状态定义

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          场景技能生命周期状态                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  基础状态（所有分类共有）                                                │
│  ├── DRAFT          草稿状态（创建后未激活）                              │
│  ├── PENDING        待处理（等待触发/激活）- 半自动特有                   │
│  ├── ACTIVE         激活状态（正在运行）                                  │
│  ├── SCHEDULED      已调度（定时任务已排期）                              │
│  ├── RUNNING        运行中                                               │
│  ├── PAUSED         暂停状态（临时停止）- 完整/半自动特有                 │
│  ├── ERROR          错误状态（运行异常）                                  │
│  ├── COMPLETED      完成状态（正常结束）- 完整/半自动特有                 │
│  └── ARCHIVED       归档状态（历史记录）- 完整/半自动特有                 │
│                                                                         │
│  扩展状态（特定分类特有）                                                │
│  ├── WAITING        等待中（等待人工干预）- 半自动特有                    │
│  └── INITIALIZING   初始化中 - 自驱系统场景特有                               │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.2 各分类生命周期流程

#### 4.2.1 自驱业务场景生命周期

```
DRAFT → selfCheck() → ACTIVE → selfStart() → SCHEDULED 
                                                      ↓
ARCHIVED ← COMPLETED ← RUNNING ← 定时/事件触发 ←─────┘
              ↑         ↓
              └──── ERROR (可恢复)
```

**特点**：
- 自动流转：DRAFT → ACTIVE → RUNNING → COMPLETED
- 支持暂停/恢复
- 异常可人工修复

#### 4.2.2 自驱系统场景生命周期

```
系统启动 → ACTIVE → SCHEDULED → RUNNING → SCHEDULED (循环)
                          ↓
                    ERROR → ACTIVE (自动重试)
```

**特点**：
- 无DRAFT/COMPLETED/ARCHIVED状态
- 持续运行，永不完成
- 异常自动重试

#### 4.2.3 触发业务场景生命周期

```
DRAFT → PENDING → WAITING → 人工/API触发 → RUNNING → COMPLETED → ARCHIVED
                              ↑              ↓
                              └────────── PAUSED (可恢复)
```

**特点**：
- 特有PENDING/WAITING状态
- 需人工触发启动
- 一次性执行

---

## 五、Engine协作接口规范（v2.3已完成）

### 5.1 协作概述

v2.3版本已完成Engine Team与Skills Team的协作，建立了分层接口架构：

```
┌─────────────────────────────────────────────────────────────────┐
│                    接口分层架构 (v2.3 已完成)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Layer 1: SEC 通用接口 (Engine Team 定义) ✅                      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ NetworkConfigProvider    - 网络配置管理 ✅               │   │
│  │ DeviceManagementProvider - 设备管理 ✅                   │   │
│  │ SecurityConfigProvider   - 安全配置管理 ✅               │   │
│  │ HealthCheckProvider      - 健康检查 ✅                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ 继承/扩展 ✅                      │
│                              ▼                                  │
│  Layer 2: 驱动特有接口 (Skills Team 定义) ✅                      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ OpenWrtDriver            - OpenWrt特有方法 ✅            │   │
│  │ KubernetesDriver         - K8s特有方法 ✅                │   │
│  │ AliyunDriver             - 阿里云特有方法 ✅             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ 实现 ✅                          │
│                              ▼                                  │
│  Layer 3: 驱动实现 (Skills Team 实现) ✅                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ OpenWrtDriverImpl        - OpenWrt具体实现 ✅            │   │
│  │ KubernetesDriverImpl     - K8s具体实现 ✅                │   │
│  │ AliyunDriverImpl         - 阿里云具体实现 ✅             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 SEC通用接口清单

| 接口 | 版本 | 方法数 | 状态 |
|------|------|--------|------|
| NetworkConfigProvider | 0.8.0 | 6 | ✅ 已完成 |
| DeviceManagementProvider | 0.8.0 | 8 | ✅ 已完成 |
| SecurityConfigProvider | 0.9.0 | 7 | ✅ 已完成 |
| HealthCheckProvider | 0.9.0 | 4 | ✅ 已完成 |

### 5.3 Provider实现清单

| Provider | Skill | 版本 | 方法数 | 状态 |
|---------|-------|------|--------|------|
| NetworkProviderImpl | skill-network | 0.7.3 | 11 | ✅ 已完成 |
| SecurityProviderImpl | skill-security | 0.7.3 | 18 | ✅ 已完成 |
| HostingProviderImpl | skill-hosting | 0.7.3 | 16 | ✅ 已完成 |
| AgentProviderImpl | skill-agent | 0.8.0 | 12 | ✅ 已完成 |
| HealthProviderImpl | skill-health | 0.9.0 | 10 | ✅ 已完成 |
| ProtocolProviderImpl | skill-protocol | 0.9.0 | 8 | ✅ 已完成 |
| OpenWrtProviderImpl | skill-openwrt | 1.0.0 | 20 | ✅ 已完成 |
| SkillShareProviderImpl | skill-share | 1.0.0 | 6 | ✅ 已完成 |

---

## 六、用户用例

### 6.1 用例一：日志汇报场景特性（自驱业务场景）

#### 用户故事

> **我是部门领导**，我要求下属员工每天下班前5:00将工作日志发给我统计。

#### 需求分析

- 定时提醒员工提交日志
- 员工提交后自动汇总
- LLM自动分析日志内容
- 发送汇总结果给领导

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-daily-report
  name: 日志汇报场景特性
  type: SCENE_CAPABILITY
  mainFirst: true                    # ✓ 标准3: 自驱能力

spec:
  # 驱动能力（内置）
  driverCapabilities:
    - id: scheduler
      config:
        timezone: "Asia/Shanghai"
    - id: event-listener
    - id: capability-invoker
    - id: collaboration-coordinator

  # 业务能力
  capabilities:
    - id: report-remind
      name: 日志提醒
      type: ATOMIC_CAPABILITY
      
    - id: report-submit
      name: 日志提交
      type: ATOMIC_CAPABILITY
      
    - id: report-aggregate
      name: 日志汇总
      type: COMPOSITE_CAPABILITY
      
    - id: report-analyze
      name: 日志分析
      type: ATOMIC_CAPABILITY

  # 协作能力入口
  collaborativeCapabilities:
    - capabilityId: scene-email-notification
      role: PROVIDER
      interface: notification-service
      autoStart: true

  # ✓ 标准4: 业务语义 - 驱动条件
  driverConditions:
    - type: scheduler
      config:
        cron: "0 17 * * 1-5"
    - type: event-listener
      config:
        events: [report.submitted]

  # ✓ 标准4: 业务语义 - 参与者
  participants:
    - role: LEADER
      name: manager
      permissions: [activate, configure, view-all]
    - role: COLLABORATOR
      name: employee
      permissions: [participate, submit]

  # 自驱配置
  mainFirstConfig:
    selfCheck:
      - checkCapabilities: [report-remind, report-submit, report-aggregate]
      - checkDriverCapabilities: [scheduler, event-listener]
      
    selfStart:
      - initDriverCapabilities: [scheduler, event-listener, capability-invoker]
      - initCapabilities: [report-remind, report-submit, report-aggregate, report-analyze]
      - bindAddresses: auto
      
    startCollaboration:
      - startScene: scene-email-notification
      - bindInterface: notification-service
      
    selfDrive:
      scheduleRules:
        - trigger: "0 17 * * 1-5"
          action: remind-flow
        - trigger: "0 18 * * 1-5"
          action: aggregate-flow
          
      capabilityChains:
        remind-flow:
          - capability: report-remind
            input: { targetUsers: "${role.employee}" }
            
        aggregate-flow:
          - capability: report-aggregate
          - capability: report-analyze
          - capability: report-remind
            input: { targetUsers: "${role.manager}", message: "${analysisResult}" }

  # 角色定义
  roles:
    - name: manager
      capabilities: [report-remind, report-aggregate, report-analyze]
      
    - name: employee
      capabilities: [report-submit]
```

**分类判定**: 自驱业务场景（1✓2✓3✓4✓）

---

### 6.2 用例二：系统清理场景特性（自驱系统场景）

#### 用户故事

> **我是系统管理员**，我希望系统每天自动清理临时文件和日志，保持系统健康。

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-system-cleanup
  name: 系统清理场景特性
  type: SCENE_CAPABILITY
  mainFirst: true                    # ✓ 标准3: 自驱能力
  labels:
    scene.visibility: internal       # 自驱系统场景，不对外暴露

spec:
  driverCapabilities:
    - id: scheduler
      config:
        timezone: "Asia/Shanghai"
    - id: capability-invoker

  capabilities:
    - id: temp-cleanup
      name: 临时文件清理
      type: ATOMIC_CAPABILITY
      
    - id: log-cleanup
      name: 日志清理
      type: ATOMIC_CAPABILITY
      
    - id: cache-cleanup
      name: 缓存清理
      type: ATOMIC_CAPABILITY

  # ✗ 标准4: 无业务语义 - 无driverConditions
  # ✗ 标准4: 无业务语义 - 无participants

  mainFirstConfig:
    selfCheck:
      - checkCapabilities: [temp-cleanup, log-cleanup, cache-cleanup]
      - checkDriverCapabilities: [scheduler]
      
    selfStart:
      - initDriverCapabilities: [scheduler, capability-invoker]
      - initCapabilities: [temp-cleanup, log-cleanup, cache-cleanup]
      
    selfDrive:
      scheduleRules:
        - trigger: "0 2 * * *"       # 每天凌晨2点执行
          action: cleanup-flow
          
      capabilityChains:
        cleanup-flow:
          - capability: temp-cleanup
          - capability: log-cleanup
          - capability: cache-cleanup
```

**分类判定**: 自驱系统场景（1✓2✓3✓4✗）

---

### 6.3 用例三：审批流程场景特性（触发业务场景）

#### 用户故事

> **我是部门主管**，我希望员工提交的申请需要我审批后才能生效。

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-approval-flow
  name: 审批流程场景特性
  type: SCENE_CAPABILITY
  mainFirst: false                   # ✗ 标准3: 无自驱能力

spec:
  driverCapabilities:
    - id: event-listener
    - id: capability-invoker

  capabilities:
    - id: request-submit
      name: 申请提交
      type: ATOMIC_CAPABILITY
      
    - id: request-approve
      name: 申请审批
      type: ATOMIC_CAPABILITY
      
    - id: request-notify
      name: 结果通知
      type: ATOMIC_CAPABILITY

  # ✓ 标准4: 业务语义 - 驱动条件（人工触发）
  driverConditions:
    - type: manual
      config:
        trigger: api
        description: "需主管人工审批后触发"

  # ✓ 标准4: 业务语义 - 参与者
  participants:
    - role: LEADER
      name: approver
      permissions: [activate, approve, reject]
    - role: COLLABORATOR
      name: requester
      permissions: [participate, submit]

  # ✗ 标准3: 无mainFirstConfig
  # 启动方式：外部API调用或人工触发

  workflow:
    states:
      - DRAFT
      - PENDING      # 等待提交
      - WAITING      # 等待审批
      - APPROVED
      - REJECTED
      - COMPLETED
      
    transitions:
      - from: DRAFT
        to: PENDING
        trigger: submit
        
      - from: PENDING
        to: WAITING
        trigger: auto
        
      - from: WAITING
        to: APPROVED
        trigger: approve
        actor: approver
        
      - from: WAITING
        to: REJECTED
        trigger: reject
        actor: approver
        
      - from: [APPROVED, REJECTED]
        to: COMPLETED
        trigger: notify
```

**分类判定**: 触发业务场景（1✓2✓3✗4✓）

---

## 七、API规范

### 7.1 场景特性发现API

```http
# 发现场景特性（支持分类筛选）
GET /api/v1/capabilities/discover

# 请求参数
{
  "type": "SCENE",                    # 能力类型
  "sceneType": "DAILY_REPORT",        # 场景类型
  "category": "FULL",                 # 场景技能分类（v2.3新增）
  "mainFirst": true,                  # 是否自驱（v2.3新增）
  "visibility": "public",             # 可见性（v2.3新增）
  "query": "日志"                     # 关键词搜索
}

# 响应
{
  "sceneCapabilities": [
    {
      "capabilityId": "scene-daily-report",
      "name": "日志汇报场景特性",
      "type": "SCENE",
      "category": "FULL",              # 分类标识
      "mainFirst": true,
      "driverConditions": [...],
      "participants": [...]
    }
  ],
  "collaborationCapabilities": [...],
  "totalScene": 1,
  "totalCollaboration": 0
}
```

### 7.2 场景技能安装API

```http
# 创建安装
POST /api/v1/scene-capabilities/install

# 请求体
{
  "capabilityId": "scene-daily-report",
  "name": "部门日报",
  "participants": {
    "leader": "user-001",
    "collaborators": ["user-002", "user-003"]
  },
  "driverConfig": {
    "scheduler": {
      "timezone": "Asia/Shanghai"
    }
  }
}

# 响应
{
  "installId": "install-xxx",
  "status": "PENDING",                # 自驱业务场景: PENDING, 触发业务场景: DRAFT
  "category": "FULL",                 # 分类信息
  "nextSteps": [...]
}
```

### 7.3 生命周期管理API

```http
# 激活场景（自驱业务场景自动，触发业务场景需调用）
POST /api/v1/scene-capabilities/{id}/activate

# 暂停场景
POST /api/v1/scene-capabilities/{id}/pause

# 恢复场景
POST /api/v1/scene-capabilities/{id}/resume

# 触发场景（触发业务场景特有）
POST /api/v1/scene-capabilities/{id}/trigger
{
  "action": "approve",
  "params": {...}
}

# 归档场景
POST /api/v1/scene-capabilities/{id}/archive
```

---

## 八、安全与权限

### 8.1 权限模型

```
┌─────────────────────────────────────────────────────────────────┐
│                        权限层级模型                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 系统级权限                                                   │
│     └── 场景特性发现、安装、管理                                  │
│                                                                 │
│  2. 场景级权限                                                   │
│     ├── 主导者(LEADER): activate, configure, terminate           │
│     └── 协作者(COLLABORATOR): participate, view                  │
│                                                                 │
│  3. 能力级权限                                                   │
│     └── 基于角色的能力访问控制                                    │
│                                                                 │
│  4. 数据级权限                                                   │
│     └── 基于VFS的KEY隔离                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 安全控制点

| 控制点 | 自驱业务场景 | 自驱系统场景 | 触发业务场景 |
|--------|-------------|-------------|-------------|
| 安装权限 | 管理员 | 系统 | 管理员 |
| 激活权限 | 自动/主导者 | 自动 | 主导者 |
| 暂停权限 | 主导者 | 无 | 主导者 |
| 触发权限 | 自动 | 自动 | 主导者/协作者 |
| 归档权限 | 主导者 | 无 | 主导者 |

---

## 九、版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v2.0 | 2026-02-15 | 初始版本，定义场景能力基础模型 | Engine Team |
| v2.1 | 2026-02-22 | 增加mainFirst自驱机制 | Engine Team |
| v2.2 | 2026-03-01 | 增加协作能力和场景组 | Engine Team |
| **v2.3** | **2026-03-05** | **增加场景技能分类体系、Engine协作完成** | **Skills Team** |

### v2.3 主要更新

1. **场景技能分类体系**
   - 定义完整/技术/半自动三大分类
   - 明确四项标准检测规则
   - 区分不同分类的生命周期

2. **Engine协作完成**
   - SEC通用接口定义完成
   - Provider实现完成
   - interface.yaml发布完成

3. **API增强**
   - 增加分类查询参数
   - 增加生命周期管理API
   - 增加触发API（触发业务场景）

---

## 十、参考资料

- [术语表v2](GLOSSARY_V2.md)
- [场景技能分类与生命周期](SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md)
- [Engine协作状态报告](ENGINE_COLLABORATION_STATUS_V2.3.md)
- [场景特性架构设计](CAPABILITY_DRIVEN_ARCHITECTURE.md)
- [Ooder 2.3规范](OODER_2.3_SPECIFICATION.md)

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-05  
**文档状态**: 正式发布 v2.3.1
