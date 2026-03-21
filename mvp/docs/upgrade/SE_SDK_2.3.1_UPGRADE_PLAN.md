# SE SDK 2.3.1 升级计划 - 状态更新

## 一、SE 团队确认回复

### 1.1 SceneGroupBridge 注入方式 ✅

**方式**：Spring Boot Auto-Configuration 自动注入

```java
// 方式1: @Autowired 注入
@Autowired
private SceneGroupBridge sceneGroupBridge;

// 方式2: 构造函数注入
@Service
public class SceneGroupService {
    private final SceneGroupBridge bridge;
    public SceneGroupService(SceneGroupBridge bridge) {
        this.bridge = bridge;
    }
}

// 方式3: 通过 SceneGroupManager 获取
@Autowired
private SceneGroupManager sceneGroupManager;
```

### 1.2 UserSceneGroup 查询接口 ✅

```java
// 通过 SDK SceneManager 查询
@Autowired
private SceneManager sceneManager;

public UserSceneGroup getUserSceneGroup(String sceneGroupId, String userId) {
    return sceneManager.getUserSceneGroup(sceneGroupId, userId).join();
}

public List<UserSceneGroup> getUserSceneGroups(String userId) {
    return sceneManager.getUserSceneGroups(userId).join();
}
```

### 1.3 CollaborativeGroupManager 与 SceneGroupManager 关系 ✅

| 维度 | CollaborativeGroupManager | SceneGroupManager |
|------|---------------------------|-------------------|
| **层级** | 应用层 | 引擎层 |
| **职责** | 用户协作关系 | 场景组生命周期 |
| **依赖** | 依赖 SceneGroupManager | 被 CollaborativeGroupManager 依赖 |

### 1.4 CapabilityBindingService 事件监听 ✅

```java
// Spring Event 监听
@EventListener
public void onBindingCreated(CapabilityBindingCreatedEvent event) {
    log.info("Capability bound: {}", event.getBindingId());
}

// SDK SceneGroupEventListener
@Component
public class SdkEventListener implements SceneGroupBridge.SceneGroupEventListener {
    @Override
    public void onMemberJoined(SceneMemberEvent event) { }
    @Override
    public void onStatusChanged(SceneGroupStatusEvent event) { }
}
```

---

## 二、升级状态

| 阶段 | 任务 | 状态 |
|------|------|------|
| Phase 1 | 接口适配 | ✅ 已完成 |
| Phase 2 | 新功能集成 | 🔄 进行中 |
| Phase 3 | 前端升级 | 待开始 |
| Phase 4 | 数据模型升级 | 待开始 |

---

## 三、Phase 2 实施计划

### 3.1 新增服务

| 服务 | 说明 | 优先级 |
|------|------|--------|
| `UserSceneGroupService` | 用户场景组服务 | 高 |
| `CollaborativeGroupService` | 协作组服务 | 中 |
| `SceneGroupBridgeService` | 场景组桥接服务 | 中 |

### 3.2 新增 Controller

| Controller | API 前缀 | 说明 |
|------------|----------|------|
| `UserSceneGroupController` | `/api/v1/user/scene-groups` | 用户场景组 API |
| `CollaborativeGroupController` | `/api/v1/collaborative-groups` | 协作组 API |
| `SceneGroupBridgeController` | `/api/v1/scene-groups/{id}/bridge` | 桥接状态 API |

### 3.3 事件监听器

| 监听器 | 事件 | 说明 |
|--------|------|------|
| `CapabilityBindingEventListener` | `CapabilityBindingCreatedEvent` | 能力绑定创建 |
| `CapabilityBindingEventListener` | `CapabilityBindingRemovedEvent` | 能力绑定移除 |
| `SdkSceneGroupEventListener` | `SceneMemberEvent` | 成员事件 |
| `SdkSceneGroupEventListener` | `SceneGroupStatusEvent` | 状态事件 |

---

## 四、协作状态

**状态**: 🟢 SE SDK 接口已确认，可以继续开发

---

**文档版本**: 1.1  
**更新日期**: 2026-03-20
