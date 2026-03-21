# SceneGroup 详情页专项优化

## 一、需求背景

**页面**：`scene-group-detail.html`  
**目标**：移除 mock 数据，使用 SE SDK 数据源，优化可视化设计

---

## 二、SE SDK 数据结构分析

### 2.1 核心类

| 类 | 包路径 | 说明 |
|----|--------|------|
| `SceneGroup` | `net.ooder.scene.group.SceneGroup` | 场景组核心实体 |
| `SceneGroupManager` | `net.ooder.scene.group.SceneGroupManager` | 场景组管理器 |
| `Participant` | `net.ooder.scene.participant.Participant` | 参与者 |
| `CapabilityBinding` | `net.ooder.scene.capability.CapabilityBinding` | 能力绑定 |
| `UserSceneGroup` | `net.ooder.scene.bridge.UserSceneGroup` | 用户场景组关联 |
| `SceneGroupInfo` | `net.ooder.scene.core.SceneGroupInfo` | 场景组信息 |
| `SceneGroupConfig` | `net.ooder.scene.group.config.SceneGroupConfig` | 场景组配置 |

### 2.2 Participant 结构

```
Participant
├── participantId: String
├── name: String
├── type: Participant.Type (USER, AGENT, SYSTEM)
├── role: Participant.Role (OWNER, ADMIN, MEMBER, OBSERVER)
├── status: Participant.Status (ACTIVE, INACTIVE, SUSPENDED)
├── joinTime: long
├── lastHeartbeat: long
└── metadata: Map<String, Object>
```

### 2.3 SceneGroup 结构

```
SceneGroup
├── sceneGroupId: String
├── templateId: String
├── name: String
├── description: String
├── status: SceneGroupStatus (CREATING, ACTIVE, SUSPENDED, DESTROYED)
├── creatorId: String
├── creatorType: CreatorType (USER, AGENT, SYSTEM)
├── createTime: long
├── lastUpdateTime: long
├── participants: List<Participant>
├── capabilityBindings: List<CapabilityBinding>
├── context: Map<String, Object>
└── config: SceneGroupConfig
```

### 2.4 UserSceneGroup 关系

```
UserSceneGroup (用户视角)
├── userId: String
├── sceneGroupId: String
├── role: Participant.Role
├── joinTime: long
├── lastActiveTime: long
├── notifications: List<Notification>
└── personalContext: Map<String, Object>
```

---

## 三、数据来源架构

### 3.1 当前实现

| API | 数据来源 | Mock 数据 |
|-----|----------|----------|
| `GET /api/v1/scene-groups/{id}` | `SceneGroupServiceSEImpl` | ❌ |
| `GET /api/v1/scene-groups/{id}/participants` | `SceneGroupServiceSEImpl` | ❌ |
| `GET /api/v1/scene-groups/{id}/snapshots` | 无实现 | ✅ |
| `GET /api/v1/scene-groups/{id}/logs/recent` | 无实现 | ✅ |

### 3.2 需要实现的 API

| API | SE SDK 数据源 | 优先级 |
|-----|--------------|--------|
| `GET /api/v1/scene-groups/{id}/snapshots` | `SceneGroup.getSnapshots()` | 高 |
| `GET /api/v1/scene-groups/{id}/logs/recent` | `SceneGroupManager.getEventLog()` | 高 |
| `GET /api/v1/user/scene-groups` | `UserSceneGroup` 关联查询 | 中 |
| `GET /api/v1/scene-groups/{id}/metrics` | `SceneGroup.getMetrics()` | 中 |

---

## 四、可视化设计方案

### 4.1 页面布局

```
┌─────────────────────────────────────────────────────────────────┐
│  场景组详情: {name}                        [状态] [操作按钮]     │
├─────────────────────────────────────────────────────────────────┤
│  基本信息  │  参与者  │  能力绑定  │  知识库  │  LLM配置  │  日志 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────┐  ┌─────────────────────────────────┐  │
│  │ 场景组信息卡片       │  │ 参与者可视化                    │  │
│  │ - ID                │  │ ┌─────┐                         │  │
│  │ - 模板              │  │ │Owner│ ──┬── [User1]          │  │
│  │ - 创建者            │  │ └─────┘   ├── [Agent1]         │  │
│  │ - 创建时间          │  │            └── [Agent2]         │  │
│  │ - 状态              │  │ ┌─────┐                         │  │
│  │ - 成员数            │  │ │Admin│ ──┬── [User2]          │  │
│  └─────────────────────┘  │ └─────┘   └── [User3]          │  │
│                           │ ┌─────┐                         │  │
│  ┌─────────────────────┐  │ │Member│ ── [User4]           │  │
│  │ 能力绑定卡片         │  │ └─────┘                         │  │
│  │ - 已绑定能力列表     │  │ ┌─────┐                         │  │
│  │ - 绑定状态          │  │ │Agent│ ── [LLM-Agent]         │  │
│  │ - 执行统计          │  │ └─────┘                         │  │
│  └─────────────────────┘  └─────────────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 时间线/活动日志                                          │   │
│  │ ──●── 10:30 用户张三加入场景组                           │   │
│  │ ──●── 10:25 能力"日志提醒"绑定成功                       │   │
│  │ ──●── 10:20 场景组创建                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 参与者可视化

**按角色分组展示**：
- Owner (创建者) - 金色边框
- Admin (管理员) - 蓝色边框
- Member (成员) - 绿色边框
- Observer (观察者) - 灰色边框

**按类型区分**：
- USER - 用户图标
- AGENT - AI 图标
- SYSTEM - 系统图标

**状态指示**：
- ACTIVE - 绿点
- INACTIVE - 灰点
- SUSPENDED - 红点

### 4.3 能力绑定可视化

```
┌──────────────────────────────────────────────────────────────┐
│ 能力绑定                                                      │
├──────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │ 日志提醒    │───▶│ 日志提交    │───▶│ 日志汇总    │      │
│  │ COMMUNICATION│    │ SERVICE     │    │ SERVICE     │      │
│  │ [已绑定] ✓  │    │ [已绑定] ✓  │    │ [已绑定] ✓  │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│         │                                     │              │
│         └──────────────────┬──────────────────┘              │
│                            ▼                                  │
│                    ┌─────────────┐                            │
│                    │ 日志分析    │                            │
│                    │ AI          │                            │
│                    │ [已绑定] ✓  │                            │
│                    └─────────────┘                            │
└──────────────────────────────────────────────────────────────┘
```

---

## 五、UserSceneGroup vs SceneGroup 关系

### 5.1 数据关系

```
User (用户)
  │
  ├── UserSceneGroup (用户-场景组关联)
  │     ├── sceneGroupId
  │     ├── role
  │     └── personalContext
  │
  └── SceneGroup (场景组)
        ├── participants (包含该用户)
        └── capabilityBindings
```

### 5.2 视角差异

| 视角 | 数据来源 | 展示内容 |
|------|----------|----------|
| **SceneGroup 视角** | `SceneGroupManager.getSceneGroup()` | 场景组完整信息、所有参与者、所有绑定 |
| **UserSceneGroup 视角** | `UserSceneGroup` 关联表 | 用户参与的场景组列表、个人角色、个人通知 |

### 5.3 API 设计

```java
// SceneGroup 视角
GET /api/v1/scene-groups/{id}
GET /api/v1/scene-groups/{id}/participants
GET /api/v1/scene-groups/{id}/capabilities

// UserSceneGroup 视角
GET /api/v1/user/scene-groups
GET /api/v1/user/scene-groups/{id}/my-role
GET /api/v1/user/scene-groups/{id}/notifications
```

---

## 六、实施计划

### 6.1 Phase 1: 移除 Mock 数据

- [x] 移除 `loadMockSceneGroup()` 函数（已改为跳转提示）
- [x] 移除 `renderMockLogs()` 函数（已改为空状态提示）
- [x] 移除 `getDefaultCapabilities()` 函数（已改为返回空数组）
- [x] 移除 `getDefaultUsers()` 函数（已改为返回空数组）

### 6.2 Phase 2: 实现 SE SDK 数据源

- [x] 实现 `GET /api/v1/scene-groups/{id}/snapshots`（已实现）
- [x] 实现 `GET /api/v1/scene-groups/{id}/event-log`（已实现）
- [x] 实现 `GET /api/v1/user/scene-groups`（已实现 - UserSceneGroupController）

### 6.3 Phase 3: 可视化优化

- [x] 实现参与者角色分组可视化（已增强图标、颜色、状态指示）
- [x] 实现能力绑定流程图（已有调用链路图展示）
- [x] 实现活动时间线（已增强时间线可视化，支持事件类型图标）

### 6.4 Phase 4: UserSceneGroup 视角

- [x] 实现用户场景组列表页（my-scenes.html 已完整实现）
- [x] 实现用户角色展示（已支持角色配置和展示）
- [x] 实现个人通知功能（已有审计日志功能）

---

## 七、依赖确认

### 7.1 SE SDK 需要提供的接口

| 接口 | 方法 | 确认状态 |
|------|------|----------|
| `SceneGroup.getSnapshots()` | 获取快照列表 | ✅ 已提供 (`getAllSnapshots()`) |
| `SceneGroupManager.getEventLog()` | 获取事件日志 | ✅ 已提供 (`getEventLog(sceneGroupId, limit)`) |
| `UserSceneGroup` 查询接口 | 用户场景组关联 | ✅ 已提供 (`getUserSceneGroups(userId)`) |

### 7.2 SE SDK 2.3.1 已确认接口

#### SceneGroup 核心方法

```java
public class SceneGroup {
    // 基本信息
    public String getSceneGroupId();
    public String getTemplateId();
    public String getName();
    public String getDescription();
    public Status getStatus();
    public String getCreatorId();
    public CreatorType getCreatorType();
    public Instant getCreateTime();
    public Instant getLastUpdateTime();
    
    // 参与者管理
    public List<Participant> getAllParticipants();
    public Participant getParticipant(String participantId);
    public boolean addParticipant(Participant participant);
    public boolean removeParticipant(String participantId);
    public int getParticipantCount();
    
    // 能力绑定
    public List<CapabilityBinding> getAllCapabilityBindings();
    public CapabilityBinding getCapabilityBinding(String bindingId);
    public boolean addCapabilityBinding(CapabilityBinding binding);
    public boolean removeCapabilityBinding(String bindingId);
    
    // 知识绑定
    public List<KnowledgeBinding> getAllKnowledgeBindings();
    public KnowledgeBinding getKnowledgeBinding(String bindingId);
    public boolean addKnowledgeBinding(KnowledgeBinding binding);
    
    // 快照
    public List<SceneSnapshot> getAllSnapshots();
    public SceneSnapshot getSnapshot(String snapshotId);
    public boolean addSnapshot(SceneSnapshot snapshot);
    
    // 事件日志
    public List<SceneGroupEvent> getEventLog(int limit);
    public List<SceneGroupEvent> getAllEventLog();
    
    // 配置
    public Map<String, Object> getAllConfig();
    public Object getConfig(String key);
    public void setConfig(String key, Object value);
    public Map<String, Object> getAllLlmConfig();
    public Object getLlmConfig(String key);
    public void setLlmConfig(String key, Object value);
}
```

#### SceneGroupManager 核心方法

```java
public class SceneGroupManager {
    // 场景组管理
    public SceneGroup createSceneGroup(String templateId, String name, String creatorId, CreatorType creatorType);
    public SceneGroup getSceneGroup(String sceneGroupId);
    public List<SceneGroup> getAllSceneGroups();
    public List<SceneGroup> getSceneGroupsByTemplate(String templateId);
    
    // 状态管理
    public boolean activateSceneGroup(String sceneGroupId);
    public boolean suspendSceneGroup(String sceneGroupId);
    public boolean destroySceneGroup(String sceneGroupId);
    
    // 参与者管理
    public boolean addParticipant(String sceneGroupId, Participant participant);
    public boolean removeParticipant(String sceneGroupId, String participantId);
    public Participant getParticipant(String sceneGroupId, String participantId);
    
    // 事件日志
    public List<SceneGroupEvent> getEventLog(String sceneGroupId, int limit);
    
    // 用户场景组查询
    public List<SceneGroup> getUserSceneGroups(String userId);
}
```

#### UserSceneGroup 桥接类

```java
public class UserSceneGroup implements AutoCloseable {
    public static UserSceneGroup getOrCreate(String sceneGroupId, SceneGroupManager, SceneGroupBridge, SceneGroupPersistence, SceneGroupArchiver);
    
    // 基本信息
    public String getSceneGroupId();
    public String getName();
    public String getDescription();
    public Status getStatus();
    public String getTemplateId();
    public String getCreatorId();
    
    // 参与者
    public List<Participant> getParticipants();
    public Participant addParticipant(String participantId, String name, Type type, Role role);
    public boolean removeParticipant(String participantId);
    
    // 能力绑定
    public List<CapabilityBinding> getCapabilityBindings();
    public CapabilityBinding addCapabilityBinding(String capabilityId, String name, String description);
    public boolean removeCapabilityBinding(String bindingId);
    
    // 知识绑定
    public List<KnowledgeBinding> getKnowledgeBindings();
    public KnowledgeBinding addKnowledgeBinding(String knowledgeId, String name, String description);
    public boolean removeKnowledgeBinding(String bindingId);
    
    // 配置
    public Map<String, Object> getConfig();
    public Object getConfig(String key);
    public void setConfig(String key, Object value);
    public void setConfigs(Map<String, Object> configs);
    
    // 状态管理
    public boolean activate();
    public boolean suspend();
    public ArchiveResult archive(String archivePath);
    public ArchiveResult restore(String archivePath);
    
    // 同步
    public void syncFromSdk();
    public void syncToSdk();
}
```

### 7.3 待 SE 团队确认

~~1. `SceneGroup` 是否提供 `getSnapshots()` 方法？~~ ✅ 已确认：`getAllSnapshots()`
~~2. `SceneGroupManager` 是否提供事件日志查询？~~ ✅ 已确认：`getEventLog(sceneGroupId, limit)`
~~3. `UserSceneGroup` 如何查询用户参与的所有场景组？~~ ✅ 已确认：`SceneGroupManager.getUserSceneGroups(userId)`

---

## 八、联系方式

**MVP 团队负责人**：[待填写]  
**SE 团队负责人**：[待填写]  
**协作状态**：✅ SE SDK 2.3.1 已完整支持，可开始实施
