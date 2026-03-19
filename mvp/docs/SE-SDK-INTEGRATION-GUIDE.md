# SE/SDK 集成指南 - SceneService 实现说明

## 背景

当前 MVP 项目中，场景管理服务使用内存实现（`SceneServiceMemoryImpl` 和 `SceneGroupServiceMemoryImpl`）。这些实现虽然功能完整，但数据仅存储在内存中，重启后数据丢失。

## 当前架构

### 服务层次结构

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│  SceneController / SceneGroupController                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ SceneGroupServiceHybridImpl (@Primary)              │    │
│  │   - 优先使用 SDK Adapter                             │    │
│  │   - 回退到 Memory 实现                               │    │
│  └─────────────────────────────────────────────────────┘    │
│                              │                               │
│              ┌───────────────┴───────────────┐              │
│              ▼                               ▼              │
│  ┌─────────────────────┐      ┌─────────────────────────┐  │
│  │ SceneSdkAdapter     │      │ SceneGroupServiceMemoryImpl│
│  │ (接口，待实现)       │      │ (当前使用)               │  │
│  └─────────────────────┘      └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 关键接口

#### 1. SceneService (简单场景定义)

```java
// 文件: src/main/java/net/ooder/mvp/skill/scene/service/SceneService.java
public interface SceneService {
    SceneDefinitionDTO create(SceneDefinitionDTO definition);
    boolean delete(String sceneId);
    SceneDefinitionDTO get(String sceneId);
    PageResult<SceneDefinitionDTO> listAll(int pageNum, int pageSize);
    boolean activate(String sceneId);
    boolean deactivate(String sceneId);
    // ... 更多方法
}
```

#### 2. SceneGroupService (完整场景组管理)

```java
// 文件: src/main/java/net/ooder/mvp/skill/scene/service/SceneGroupService.java
public interface SceneGroupService {
    // 场景组生命周期
    SceneGroupDTO create(String templateId, SceneGroupConfigDTO config);
    SceneGroupDTO update(String sceneGroupId, SceneGroupConfigDTO config);
    boolean destroy(String sceneGroupId);
    SceneGroupDTO get(String sceneGroupId);
    
    // 列表查询
    PageResult<SceneGroupDTO> listAll(int pageNum, int pageSize);
    PageResult<SceneGroupDTO> listByTemplate(String templateId, int pageNum, int pageSize);
    PageResult<SceneGroupDTO> listByCreator(String creatorId, int pageNum, int pageSize);
    PageResult<SceneGroupDTO> listByParticipant(String participantId, int pageNum, int pageSize);
    
    // 激活/停用
    boolean activate(String sceneGroupId);
    boolean deactivate(String sceneGroupId);
    
    // 参与者管理
    boolean join(String sceneGroupId, SceneParticipantDTO participant);
    boolean leave(String sceneGroupId, String participantId);
    boolean changeRole(String sceneGroupId, String participantId, String newRole);
    PageResult<SceneParticipantDTO> listParticipants(String sceneGroupId, int pageNum, int pageSize);
    
    // 能力绑定
    boolean bindCapability(String sceneGroupId, CapabilityBindingDTO binding);
    boolean updateCapabilityBinding(String sceneGroupId, String bindingId, CapabilityBindingDTO binding);
    boolean unbindCapability(String sceneGroupId, String bindingId);
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, int pageNum, int pageSize);
    
    // 快照管理
    SceneSnapshotDTO createSnapshot(String sceneGroupId);
    List<SceneSnapshotDTO> listSnapshots(String sceneGroupId);
    boolean restoreSnapshot(String sceneGroupId, SceneSnapshotDTO snapshot);
    
    // 知识库绑定
    boolean bindKnowledgeBase(String sceneGroupId, KnowledgeBindingDTO binding);
    boolean unbindKnowledgeBase(String sceneGroupId, String kbId);
    List<KnowledgeBindingDTO> listKnowledgeBindings(String sceneGroupId);
    
    // LLM 配置
    Map<String, Object> getLlmConfig(String sceneGroupId);
    boolean updateLlmConfig(String sceneGroupId, Map<String, Object> config);
}
```

#### 3. SceneSdkAdapter (SDK 适配器接口)

```java
// 文件: src/main/java/net/ooder/mvp/skill/scene/sdk/SceneSdkAdapter.java
public interface SceneSdkAdapter {
    boolean isAvailable();
    
    SceneGroupDTO createSceneGroup(String templateId, SceneGroupConfigDTO config);
    SceneGroupDTO getSceneGroup(String sceneGroupId);
    List<SceneGroupDTO> listSceneGroups();
    
    boolean activateSceneGroup(String sceneGroupId);
    boolean deactivateSceneGroup(String sceneGroupId);
    
    boolean joinSceneGroup(String sceneGroupId, SceneParticipantDTO participant);
    boolean leaveSceneGroup(String sceneGroupId, String participantId);
    List<SceneParticipantDTO> listParticipants(String sceneGroupId);
    
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindingDTO binding);
    boolean unbindCapability(String sceneGroupId, String bindingId);
    List<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId);
    
    Object invokeCapability(String skillId, Map<String, Object> params);
}
```

## SE 团队需要实现的内容

### 1. SceneSdkAdapter 实现类

创建 `SceneSdkAdapterImpl` 类，实现 `SceneSdkAdapter` 接口：

```java
package net.ooder.mvp.skill.scene.sdk.impl;

import net.ooder.mvp.skill.scene.sdk.SceneSdkAdapter;
import net.ooder.mvp.skill.scene.dto.scene.*;
import org.springframework.stereotype.Component;

@Component
public class SceneSdkAdapterImpl implements SceneSdkAdapter {
    
    // 注入 SE SDK 客户端
    // @Autowired
    // private SeSdkClient seSdkClient;
    
    @Override
    public boolean isAvailable() {
        // 检查 SDK 是否可用
        // return seSdkClient != null && seSdkClient.isConnected();
        return false; // 当前返回 false，使用内存回退
    }
    
    @Override
    public SceneGroupDTO createSceneGroup(String templateId, SceneGroupConfigDTO config) {
        // 调用 SE SDK 创建场景组
        // return seSdkClient.createSceneGroup(templateId, config);
        return null;
    }
    
    // ... 实现其他方法
}
```

### 2. DTO 映射

SE SDK 的数据模型需要映射到项目的 DTO：

| SE SDK 模型 | 项目 DTO | 说明 |
|------------|----------|------|
| SceneGroup | SceneGroupDTO | 场景组 |
| Participant | SceneParticipantDTO | 参与者 |
| CapabilityBinding | CapabilityBindingDTO | 能力绑定 |
| KnowledgeBinding | KnowledgeBindingDTO | 知识库绑定 |
| SceneSnapshot | SceneSnapshotDTO | 场景快照 |

### 3. 关键 DTO 字段说明

#### SceneGroupDTO

```java
public class SceneGroupDTO {
    private String sceneGroupId;      // 场景组唯一标识
    private String templateId;        // 模板ID
    private String name;              // 场景组名称
    private String description;       // 描述
    private SceneGroupStatus status;  // 状态: CREATING, ACTIVE, SUSPENDED, DESTROYING, DESTROYED
    private String creatorId;         // 创建者ID
    private ParticipantType creatorType; // 创建者类型: USER, AGENT
    private SceneGroupConfigDTO config; // 配置
    private int memberCount;          // 成员数量
    private List<SceneParticipantDTO> participants; // 参与者列表
    private List<CapabilityBindingDTO> capabilityBindings; // 能力绑定
    private List<KnowledgeBindingDTO> knowledgeBases; // 知识库绑定
    private long createTime;          // 创建时间
    private long lastUpdateTime;      // 最后更新时间
}
```

#### SceneParticipantDTO

```java
public class SceneParticipantDTO {
    private String participantId;     // 参与者ID
    private String name;              // 名称
    private ParticipantType participantType; // 类型: USER, AGENT, SUPER_AGENT
    private String role;              // 角色: MANAGER, EMPLOYEE, LLM_ASSISTANT, COORDINATOR 等
    private ParticipantStatus status; // 状态: JOINED, LEFT, SUSPENDED
    private long joinTime;            // 加入时间
    private long lastHeartbeat;       // 最后心跳时间
}
```

#### CapabilityBindingDTO

```java
public class CapabilityBindingDTO {
    private String bindingId;         // 绑定ID
    private String sceneGroupId;      // 场景组ID
    private String capId;             // 能力ID
    private String capName;           // 能力名称
    private CapabilityProviderType providerType; // 提供者类型: AGENT, PLATFORM, EXTERNAL
    private ConnectorType connectorType; // 连接器类型: INTERNAL, EXTERNAL, HYBRID
    private int priority;             // 优先级
    private boolean fallback;         // 是否允许降级
    private CapabilityBindingStatus status; // 状态: ACTIVE, INACTIVE, ERROR
}
```

## 集成步骤

### Step 1: 添加 SE SDK 依赖

```xml
<!-- pom.xml -->
<dependency>
    <groupId>net.ooder.se</groupId>
    <artifactId>se-sdk</artifactId>
    <version>${se-sdk.version}</version>
</dependency>
```

### Step 2: 配置 SDK 连接

```yaml
# application.yml
ooder:
  se:
    sdk:
      enabled: true
      endpoint: ${SE_SDK_ENDPOINT:http://localhost:8080}
      apiKey: ${SE_API_KEY:}
      timeout: 30000
```

### Step 3: 实现 SceneSdkAdapter

参考上面的代码模板，实现所有接口方法。

### Step 4: 验证集成

1. 启动应用，检查日志确认 SDK 初始化成功
2. 调用场景组 API，验证数据持久化
3. 重启应用，验证数据不丢失

## API 端点对照表

| 前端调用 | 后端端点 | Service 方法 | 说明 |
|---------|---------|-------------|------|
| POST /api/v1/scenes/list | SceneController.listAll | SceneService.listAll | 场景列表 |
| POST /api/v1/scenes/get | SceneController.get | SceneService.get | 场景详情 |
| POST /api/v1/scenes/create | SceneController.create | SceneService.create | 创建场景 |
| POST /api/v1/scenes/delete | SceneController.delete | SceneService.delete | 删除场景 |
| POST /api/v1/scenes/activate | SceneController.activate | SceneService.activate | 激活场景 |
| POST /api/v1/scenes/deactivate | SceneController.deactivate | SceneService.deactivate | 停用场景 |

## 注意事项

1. **数据一致性**: SDK 实现需要保证与内存实现的行为一致
2. **错误处理**: SDK 调用失败时，`SceneGroupServiceHybridImpl` 会自动回退到内存实现
3. **性能考虑**: 批量查询接口需要支持分页
4. **事务处理**: 创建场景组时需要同时创建参与者和能力绑定

## 联系方式

如有问题，请联系 MVP 团队。

---

**文档版本**: 1.0  
**创建日期**: 2026-03-19  
**作者**: MVP Team
