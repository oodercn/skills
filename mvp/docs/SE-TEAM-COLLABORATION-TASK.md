# SE 团队协作任务开发说明

## 任务概述

**提交人**: MVP Core 团队  
**提交时间**: 2026-03-19  
**优先级**: 高  
**涉及模块**: SE SDK、SceneSdkAdapter 实现

---

## 背景

MVP 项目已完成场景管理核心功能的开发，当前使用内存存储实现。为了实现数据持久化和生产环境部署，需要 SE 团队实现 `SceneSdkAdapter` 接口，对接 SE SDK 进行数据存储和场景引擎调用。

---

## 架构说明

### 当前架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│  SceneGroupController / SceneController                      │
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

### 数据流向

```
前端请求 → Controller → HybridImpl → SDK Adapter (SE 实现)
                                    ↓ (如果 SDK 不可用)
                                MemoryImpl (回退)
```

---

## 需要实现的接口

### 1. SceneSdkAdapter 接口

**文件位置**: `src/main/java/net/ooder/mvp/skill/scene/sdk/SceneSdkAdapter.java`

```java
package net.ooder.mvp.skill.scene.sdk;

import net.ooder.mvp.skill.scene.dto.scene.*;
import java.util.List;
import java.util.Map;

public interface SceneSdkAdapter {

    /**
     * 检查 SDK 是否可用
     * @return true 表示 SDK 可用，将使用 SDK 实现
     */
    boolean isAvailable();

    /**
     * 创建场景组
     * @param templateId 模板ID
     * @param config 场景组配置
     * @return 创建的场景组
     */
    SceneGroupDTO createSceneGroup(String templateId, SceneGroupConfigDTO config);

    /**
     * 获取场景组详情
     * @param sceneGroupId 场景组ID
     * @return 场景组详情
     */
    SceneGroupDTO getSceneGroup(String sceneGroupId);

    /**
     * 获取所有场景组列表
     * @return 场景组列表
     */
    List<SceneGroupDTO> listSceneGroups();

    /**
     * 激活场景组
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean activateSceneGroup(String sceneGroupId);

    /**
     * 停用场景组
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean deactivateSceneGroup(String sceneGroupId);

    /**
     * 加入场景组
     * @param sceneGroupId 场景组ID
     * @param participant 参与者信息
     * @return 是否成功
     */
    boolean joinSceneGroup(String sceneGroupId, SceneParticipantDTO participant);

    /**
     * 离开场景组
     * @param sceneGroupId 场景组ID
     * @param participantId 参与者ID
     * @return 是否成功
     */
    boolean leaveSceneGroup(String sceneGroupId, String participantId);

    /**
     * 获取场景组参与者列表
     * @param sceneGroupId 场景组ID
     * @return 参与者列表
     */
    List<SceneParticipantDTO> listParticipants(String sceneGroupId);

    /**
     * 绑定能力到场景组
     * @param sceneGroupId 场景组ID
     * @param binding 能力绑定信息
     * @return 绑定详情
     */
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindingDTO binding);

    /**
     * 解绑能力
     * @param sceneGroupId 场景组ID
     * @param bindingId 绑定ID
     * @return 是否成功
     */
    boolean unbindCapability(String sceneGroupId, String bindingId);

    /**
     * 获取场景组的能力绑定列表
     * @param sceneGroupId 场景组ID
     * @return 能力绑定列表
     */
    List<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId);

    /**
     * 调用能力
     * @param skillId 技能ID
     * @param params 参数
     * @return 调用结果
     */
    Object invokeCapability(String skillId, Map<String, Object> params);
}
```

### 2. 实现类模板

**建议文件位置**: `src/main/java/net/ooder/mvp/skill/scene/sdk/impl/SceneSdkAdapterImpl.java`

```java
package net.ooder.mvp.skill.scene.sdk.impl;

import net.ooder.mvp.skill.scene.sdk.SceneSdkAdapter;
import net.ooder.mvp.skill.scene.dto.scene.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SceneSdkAdapterImpl implements SceneSdkAdapter {

    private static final Logger log = LoggerFactory.getLogger(SceneSdkAdapterImpl.class);

    @Value("${ooder.se.sdk.enabled:false}")
    private boolean sdkEnabled;

    @Value("${ooder.se.sdk.endpoint:http://localhost:8080}")
    private String sdkEndpoint;

    // 注入 SE SDK 客户端
    // @Autowired(required = false)
    // private SeSdkClient seSdkClient;

    @Override
    public boolean isAvailable() {
        if (!sdkEnabled) {
            log.debug("SE SDK is disabled");
            return false;
        }
        
        // 检查 SDK 连接状态
        // return seSdkClient != null && seSdkClient.isConnected();
        return false;
    }

    @Override
    public SceneGroupDTO createSceneGroup(String templateId, SceneGroupConfigDTO config) {
        log.info("Creating scene group with template: {}", templateId);
        
        // TODO: 调用 SE SDK 创建场景组
        // return seSdkClient.createSceneGroup(templateId, config);
        
        return null;
    }

    @Override
    public SceneGroupDTO getSceneGroup(String sceneGroupId) {
        log.info("Getting scene group: {}", sceneGroupId);
        
        // TODO: 调用 SE SDK 获取场景组
        // return seSdkClient.getSceneGroup(sceneGroupId);
        
        return null;
    }

    @Override
    public List<SceneGroupDTO> listSceneGroups() {
        log.info("Listing all scene groups");
        
        // TODO: 调用 SE SDK 获取场景组列表
        // return seSdkClient.listSceneGroups();
        
        return null;
    }

    @Override
    public boolean activateSceneGroup(String sceneGroupId) {
        log.info("Activating scene group: {}", sceneGroupId);
        
        // TODO: 调用 SE SDK 激活场景组
        // return seSdkClient.activateSceneGroup(sceneGroupId);
        
        return false;
    }

    @Override
    public boolean deactivateSceneGroup(String sceneGroupId) {
        log.info("Deactivating scene group: {}", sceneGroupId);
        
        // TODO: 调用 SE SDK 停用场景组
        // return seSdkClient.deactivateSceneGroup(sceneGroupId);
        
        return false;
    }

    @Override
    public boolean joinSceneGroup(String sceneGroupId, SceneParticipantDTO participant) {
        log.info("Participant {} joining scene group: {}", participant.getParticipantId(), sceneGroupId);
        
        // TODO: 调用 SE SDK 加入场景组
        // return seSdkClient.joinSceneGroup(sceneGroupId, participant);
        
        return false;
    }

    @Override
    public boolean leaveSceneGroup(String sceneGroupId, String participantId) {
        log.info("Participant {} leaving scene group: {}", participantId, sceneGroupId);
        
        // TODO: 调用 SE SDK 离开场景组
        // return seSdkClient.leaveSceneGroup(sceneGroupId, participantId);
        
        return false;
    }

    @Override
    public List<SceneParticipantDTO> listParticipants(String sceneGroupId) {
        log.info("Listing participants for scene group: {}", sceneGroupId);
        
        // TODO: 调用 SE SDK 获取参与者列表
        // return seSdkClient.listParticipants(sceneGroupId);
        
        return null;
    }

    @Override
    public CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindingDTO binding) {
        log.info("Binding capability {} to scene group: {}", binding.getCapId(), sceneGroupId);
        
        // TODO: 调用 SE SDK 绑定能力
        // return seSdkClient.bindCapability(sceneGroupId, binding);
        
        return null;
    }

    @Override
    public boolean unbindCapability(String sceneGroupId, String bindingId) {
        log.info("Unbinding capability {} from scene group: {}", bindingId, sceneGroupId);
        
        // TODO: 调用 SE SDK 解绑能力
        // return seSdkClient.unbindCapability(sceneGroupId, bindingId);
        
        return false;
    }

    @Override
    public List<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId) {
        log.info("Listing capability bindings for scene group: {}", sceneGroupId);
        
        // TODO: 调用 SE SDK 获取能力绑定列表
        // return seSdkClient.listCapabilityBindings(sceneGroupId);
        
        return null;
    }

    @Override
    public Object invokeCapability(String skillId, Map<String, Object> params) {
        log.info("Invoking capability: {}", skillId);
        
        // TODO: 调用 SE SDK 执行能力
        // return seSdkClient.invokeCapability(skillId, params);
        
        return null;
    }
}
```

---

## DTO 数据结构

### SceneGroupDTO

```java
public class SceneGroupDTO {
    private String sceneGroupId;           // 场景组唯一标识，格式: sg-{timestamp}
    private String templateId;             // 模板ID
    private String name;                   // 场景组名称
    private String description;            // 描述
    private SceneGroupStatus status;       // 状态枚举
    private String creatorId;              // 创建者ID
    private ParticipantType creatorType;   // 创建者类型: USER, AGENT, SUPER_AGENT
    private SceneGroupConfigDTO config;    // 配置信息
    private int memberCount;               // 成员数量
    private List<SceneParticipantDTO> participants;      // 参与者列表
    private List<CapabilityBindingDTO> capabilityBindings; // 能力绑定列表
    private List<KnowledgeBindingDTO> knowledgeBases;    // 知识库绑定
    private long createTime;               // 创建时间戳
    private long lastUpdateTime;           // 最后更新时间戳
}
```

### SceneGroupStatus 枚举

```java
public enum SceneGroupStatus {
    CREATING,      // 创建中
    ACTIVE,        // 活跃
    SUSPENDED,     // 暂停
    DESTROYING,    // 销毁中
    DESTROYED      // 已销毁
}
```

### SceneParticipantDTO

```java
public class SceneParticipantDTO {
    private String participantId;          // 参与者ID
    private String name;                   // 名称
    private ParticipantType participantType; // 类型: USER, AGENT, SUPER_AGENT
    private String role;                   // 角色: MANAGER, EMPLOYEE, LLM_ASSISTANT, COORDINATOR, HR
    private ParticipantStatus status;      // 状态: JOINED, LEFT, SUSPENDED
    private long joinTime;                 // 加入时间戳
    private long lastHeartbeat;            // 最后心跳时间戳
}
```

### CapabilityBindingDTO

```java
public class CapabilityBindingDTO {
    private String bindingId;              // 绑定ID，格式: cb-{timestamp}-{priority}
    private String sceneGroupId;           // 场景组ID
    private String capId;                  // 能力ID
    private String capName;                // 能力名称
    private CapabilityProviderType providerType; // 提供者类型: AGENT, PLATFORM, EXTERNAL
    private ConnectorType connectorType;   // 连接器类型: INTERNAL, EXTERNAL, HYBRID
    private int priority;                  // 优先级 (1-10)
    private boolean fallback;              // 是否允许降级
    private CapabilityBindingStatus status; // 状态: ACTIVE, INACTIVE, ERROR
    private Map<String, Object> connectorConfig; // 连接器配置
}
```

### SceneGroupConfigDTO

```java
public class SceneGroupConfigDTO {
    private String name;                   // 名称
    private String description;            // 描述
    private String creatorId;              // 创建者ID
    private ParticipantType creatorType;   // 创建者类型
    private Integer minMembers;            // 最小成员数
    private Integer maxMembers;            // 最大成员数
    private Integer knowledgeTopK;         // 知识库检索TopK
    private Double knowledgeThreshold;     // 知识库检索阈值
    private Boolean crossLayerSearch;      // 是否跨层检索
}
```

---

## 配置要求

### application.yml 配置

```yaml
ooder:
  se:
    sdk:
      enabled: true
      endpoint: ${SE_SDK_ENDPOINT:http://localhost:8080}
      api-key: ${SE_API_KEY:}
      timeout: 30000
      retry:
        max-attempts: 3
        backoff: 1000
```

---

## API 端点对照表

| 前端调用 | 后端端点 | Service 方法 | SDK Adapter 方法 |
|---------|---------|-------------|-----------------|
| POST /api/v1/scene-groups/create | SceneGroupController.create | create | createSceneGroup |
| GET /api/v1/scene-groups/{id} | SceneGroupController.get | get | getSceneGroup |
| POST /api/v1/scene-groups/list | SceneGroupController.list | listAll | listSceneGroups |
| POST /api/v1/scene-groups/{id}/activate | SceneGroupController.activate | activate | activateSceneGroup |
| POST /api/v1/scene-groups/{id}/deactivate | SceneGroupController.deactivate | deactivate | deactivateSceneGroup |
| POST /api/v1/scene-groups/{id}/join | SceneGroupController.join | join | joinSceneGroup |
| POST /api/v1/scene-groups/{id}/leave | SceneGroupController.leave | leave | leaveSceneGroup |
| GET /api/v1/scene-groups/{id}/participants | SceneGroupController.listParticipants | listParticipants | listParticipants |
| POST /api/v1/scene-groups/{id}/capabilities/bind | SceneGroupController.bindCapability | bindCapability | bindCapability |
| POST /api/v1/scene-groups/{id}/capabilities/unbind | SceneGroupController.unbindCapability | unbindCapability | unbindCapability |
| GET /api/v1/scene-groups/{id}/capabilities | SceneGroupController.listCapabilities | listCapabilityBindings | listCapabilityBindings |

---

## 验证步骤

### 1. 单元测试

```java
@SpringBootTest
class SceneSdkAdapterImplTest {

    @Autowired
    private SceneSdkAdapter sdkAdapter;

    @Test
    void testIsAvailable() {
        assertTrue(sdkAdapter.isAvailable());
    }

    @Test
    void testCreateSceneGroup() {
        SceneGroupConfigDTO config = new SceneGroupConfigDTO();
        config.setName("测试场景组");
        config.setCreatorId("user-001");
        config.setCreatorType(ParticipantType.USER);

        SceneGroupDTO result = sdkAdapter.createSceneGroup("tpl-daily-report", config);
        
        assertNotNull(result);
        assertNotNull(result.getSceneGroupId());
        assertEquals("测试场景组", result.getName());
    }

    @Test
    void testJoinAndLeave() {
        String sceneGroupId = "sg-test-001";
        
        SceneParticipantDTO participant = new SceneParticipantDTO();
        participant.setParticipantId("user-002");
        participant.setName("测试用户");
        participant.setParticipantType(ParticipantType.USER);
        participant.setRole("EMPLOYEE");

        assertTrue(sdkAdapter.joinSceneGroup(sceneGroupId, participant));
        assertTrue(sdkAdapter.leaveSceneGroup(sceneGroupId, "user-002"));
    }
}
```

### 2. 集成测试

1. 启动应用，检查日志确认 SDK 初始化成功
2. 调用场景组创建 API，验证数据持久化
3. 重启应用，验证数据不丢失
4. 验证参与者加入/离开功能
5. 验证能力绑定/解绑功能

### 3. 前端验证

1. 访问 http://localhost:8084/console/pages/scene-group-management.html
2. 创建新场景组
3. 刷新页面，验证数据持久化
4. 添加参与者
5. 绑定能力

---

## 注意事项

### 1. 数据一致性

- SDK 实现需要保证与内存实现的行为一致
- 所有操作应该是幂等的
- 并发操作需要考虑线程安全

### 2. 错误处理

- SDK 调用失败时，`SceneGroupServiceHybridImpl` 会自动回退到内存实现
- 建议在 SDK 实现中添加重试机制
- 关键操作需要记录日志

### 3. 性能考虑

- 批量查询接口需要支持分页
- 考虑添加缓存层
- 避免频繁的数据库查询

### 4. 事务处理

- 创建场景组时需要同时创建参与者和能力绑定
- 建议使用 Spring @Transactional 注解
- 考虑分布式事务场景

---

## 时间节点

| 阶段 | 时间 | 负责人 |
|------|------|--------|
| 接口定义确认 | 2026-03-20 | 双方 |
| SDK 实现开发 | 待定 | SE 团队 |
| 单元测试 | 待定 | SE 团队 |
| 集成测试 | 待定 | 双方 |
| 生产部署 | 待定 | 双方 |

---

## 联系方式

如有疑问，请联系 MVP Core 团队。

---

**任务状态**: 待处理  
**文档版本**: 1.0  
**创建日期**: 2026-03-19  
**作者**: MVP Core Team
