# 开发任务分配总览（修订版 v3）

> **文档版本**: 3.0  
> **创建日期**: 2026-03-15  
> **修订说明**: 明确工程职责，skill-scene是旧版本参考，任务分配到可安装的技能工程  
> **状态**: 待开发

---

## 重要澄清

### 工程定位

| 工程 | 路径 | 类型 | 说明 |
|------|------|------|------|
| **skill-scene** | skills/skill-scene/ | 旧版本参考 | ❌ 不是可安装技能，仅作移植参考 |
| **skill-scene-management** | skills/_system/skill-scene-management/ | 可安装技能 | ✅ 场景管理技能 |
| **skill-capability** | skills/_system/skill-capability/ | 可安装技能 | ✅ 能力管理技能 |
| **skill-common** | skills/_system/skill-common/ | 可安装技能 | ✅ 公共接口和存储 |

### 本期实现目标（降级实现）

| 目标 | 说明 |
|------|------|
| **JSON文件存储** | 使用SDK已有的JsonStorageService，不引入数据库 |
| **工作流简单实现** | 仅预留接口，简单实现基础触发功能 |
| **能力自发现组网** | 场景组内技能/能力自动发现和组网 |

---

## 一、任务分配

### 1.1 skill-common（公共接口模块）

**工程路径**: `skills/_system/skill-common/`

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| COMMON-001 | SceneContextApi接口定义 | 0.5d | ✅ 已完成 |
| COMMON-002 | CapabilityRegisterApi接口定义 | 0.5d | ✅ 已完成 |
| COMMON-003 | ParticipantApi接口定义 | 0.5d | ✅ 已完成 |
| COMMON-004 | LinkApi接口定义 | 0.5d | ✅ 已完成 |
| COMMON-005 | 完善JsonStorageService | 1d | 待开发 |

**产出物**:
```
skills/_system/skill-common/src/main/java/net/ooder/skill/common/
├── api/
│   ├── SceneContextApi.java        ✅
│   ├── CapabilityRegisterApi.java  ✅
│   ├── ParticipantApi.java         ✅
│   └── LinkApi.java                ✅
└── storage/
    └── JsonStorageService.java     (完善)
```

---

### 1.2 skill-scene-management（场景管理技能）

**工程路径**: `skills/_system/skill-scene-management/`

**职责**: 场景组管理、参与者管理、场景生命周期

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| SCENE-MGMT-001 | 场景组数据存储 | 2d | 待开发 |
| SCENE-MGMT-002 | 场景组生命周期管理 | 3d | 待开发 |
| SCENE-MGMT-003 | 参与者加入/离开 | 2d | 待开发 |
| SCENE-MGMT-004 | 参与者角色管理 | 1d | 待开发 |
| SCENE-MGMT-005 | 场景组管理UI | 3d | 待开发 |
| SCENE-MGMT-006 | 参与者管理UI | 2d | 待开发 |

**产出物**:
```
skills/_system/skill-scene-management/src/main/java/net/ooder/skill/scene/
├── controller/
│   ├── SceneGroupController.java
│   └── ParticipantController.java
├── service/
│   ├── SceneGroupService.java
│   ├── ParticipantService.java
│   └── impl/
│       ├── SceneGroupServiceImpl.java
│       └── ParticipantServiceImpl.java
├── model/
│   ├── SceneGroup.java
│   ├── SceneGroupStatus.java
│   ├── Participant.java
│   └── ParticipantStatus.java
└── storage/
    └── SceneGroupStorage.java
```

---

### 1.3 skill-capability（能力管理技能）

**工程路径**: `skills/_system/skill-capability/`

**职责**: 能力注册、绑定、发现、链路管理

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| CAP-001 | 能力绑定数据存储 | 1d | 待开发 |
| CAP-002 | 能力绑定管理 | 2d | 待开发 |
| CAP-003 | 能力自发现机制 | 3d | 待开发 |
| CAP-004 | Link服务实现 | 3d | 待开发 |
| CAP-005 | 链路状态监控 | 2d | 待开发 |
| CAP-006 | 能力管理UI | 2d | 待开发 |
| CAP-007 | 链路管理UI | 2d | 待开发 |

**产出物**:
```
skills/_system/skill-capability/src/main/java/net/ooder/skill/capability/
├── controller/
│   ├── CapabilityBindingController.java
│   └── LinkController.java
├── service/
│   ├── CapabilityBindingService.java
│   ├── CapabilityDiscoveryService.java
│   ├── LinkService.java
│   └── impl/
│       ├── CapabilityBindingServiceImpl.java
│       ├── CapabilityDiscoveryServiceImpl.java
│       └── LinkServiceImpl.java
├── model/
│   ├── CapabilityBinding.java
│   ├── Link.java
│   └── LinkStatus.java
├── discovery/
│   ├── SceneCapabilityDiscoverer.java
│   └── CapabilityMatcher.java
└── storage/
    └── CapabilityBindingStorage.java
```

---

### 1.4 skill-llm-chat（LLM对话技能）

**工程路径**: `skills/_system/skill-llm-chat/`

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| LLM-001 | 对话能力完善 | 2d | 待开发 |
| LLM-002 | 流式输出实现 | 2d | 待开发 |
| LLM-003 | 对话界面开发 | 3d | 待开发 |

---

### 1.5 skill-audit（审计日志技能）

**工程路径**: `skills/capabilities/security/skill-audit/`

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| AUDIT-001 | 日志查询实现 | 2d | 待开发 |
| AUDIT-002 | 日志导出实现 | 1d | 待开发 |
| AUDIT-003 | UI界面开发 | 1d | 待开发 |

---

### 1.6 skill-notification（消息通知技能）

**工程路径**: `skills/capabilities/communication/skill-notification/`

| 任务ID | 任务名称 | 工作量 | 状态 |
|--------|----------|--------|------|
| NOTIFY-001 | 消息发送实现 | 2d | 待开发 |
| NOTIFY-002 | 订阅管理实现 | 1d | 待开发 |
| NOTIFY-003 | UI界面开发 | 1d | 待开发 |

---

## 二、开发时间线

```
Week 1-2: skill-common + skill-scene-management 核心
Week 3-4: skill-capability 能力管理 + 链路
Week 3-4: skill-llm-chat 并行开发
Week 5-6: skill-audit + skill-notification 并行开发
```

### 甘特图

```
              Week1  Week2  Week3  Week4  Week5  Week6
skill-common
  公共接口      ████
  JSON存储完善        ██

skill-scene-management
  数据存储      ████
  生命周期      ████████
  参与者管理          ████████
  UI开发                     ████████

skill-capability
  能力绑定            ████████
  能力自发现                  ████████
  链路管理                    ████████
  UI开发                             ████████

skill-llm-chat
  核心功能                    ████████
  UI开发                            ████████

skill-audit
  全部开发                                   ██████

skill-notification
  全部开发                                   ██████
```

---

## 三、依赖关系

### 3.1 开发依赖

```
skill-common (公共接口定义) ✅
    ↓
skill-scene-management (场景组管理)
    ↓
skill-capability (能力绑定)
    ↓
skill-llm-chat (可开始开发)
    ↓
skill-audit, skill-notification (可并行开发)
```

### 3.2 运行时依赖

```
skill-scene-management ──依赖──▶ skill-common (SceneContextApi)
skill-capability ──依赖──▶ skill-common (CapabilityRegisterApi, LinkApi)
skill-llm-chat ──依赖──▶ skill-common (SceneContextApi)
skill-audit ──依赖──▶ skill-common (SceneContextApi)
skill-notification ──依赖──▶ skill-common (ParticipantApi)
```

---

## 四、从 skill-scene 迁移参考

### 4.1 可复用代码

| 源文件 | 目标工程 | 说明 |
|--------|----------|------|
| SceneGroupServiceMemoryImpl.java | skill-scene-management | 场景组服务实现参考 |
| SceneGroupController.java | skill-scene-management | 控制器参考 |
| CapabilityBindingServiceImpl.java | skill-capability | 能力绑定服务参考 |
| CapabilityInvokerImpl.java | skill-capability | 能力调用参考 |
| JsonStorageService.java | skill-common | 存储服务 |

### 4.2 需要新开发

| 功能 | 目标工程 | 说明 |
|------|----------|------|
| 能力自发现机制 | skill-capability | 新功能 |
| Link服务实现 | skill-capability | 需要完善 |
| 工作流接口定义 | skill-scene-management | 仅接口 |

---

## 五、任务统计

| 分类 | 任务数 | 总工作量 |
|------|--------|----------|
| skill-common | 5个 | 3.5人天 |
| skill-scene-management | 6个 | 13人天 |
| skill-capability | 7个 | 15人天 |
| skill-llm-chat | 3个 | 7人天 |
| skill-audit | 3个 | 4人天 |
| skill-notification | 3个 | 4人天 |
| **合计** | **27个** | **46.5人天** |

---

## 六、文档索引

| 文档 | 路径 |
|------|------|
| 升级计划 | [UPGRADE_PLAN.md](file:///e:/github/ooder-skills/docs/UPGRADE_PLAN.md) |
| 功能匹配分析 | [FEATURE_MATCH_ANALYSIS.md](file:///e:/github/ooder-skills/docs/FEATURE_MATCH_ANALYSIS.md) |
| skill-scene参考 | [skills/skill-scene/](file:///e:/github/ooder-skills/skills/skill-scene/) |

---

*文档生成时间: 2026-03-15*
*修订版本: v3.0*
