# SE SDK 协作需求说明

## 一、需求背景

**项目**：MVP Core (mvp-core)
**版本**：2.3.1
**发起方**：MVP 团队
**接收方**：SE SDK 团队
**日期**：2026-03-22
**状态**：待确认

---

## 二、当前集成状态

### 2.1 已集成的 SE SDK 模块

| 模块 | 版本 | 用途 | 集成状态 |
|------|------|------|---------|
| scene-engine | 2.3.1 | 场景引擎核心 | 正常 |
| agent-sdk-api | 2.3.1 | Agent 核心 API | 正常 |
| agent-sdk-core | 2.3.1 | Agent 核心实现 | 正常 |
| skill-common | 2.3.1 | 技能通用工具 | 正常 |

### 2.2 核心接口使用情况

| SE SDK 接口 | MVP 使用位置 | 状态 |
|------------|-------------|------|
| SceneGroupManager | SceneGroupServiceSEImpl | 正常 |
| Participant | 场景参与者管理 | 正常 |
| CapabilityBinding | 能力绑定管理 | 正常 |
| SceneGroup.Status | 场景状态管理 | 正常 |

---

## 三、协作需求清单

### 3.1 P0 - 必须实现

#### 需求 1：Agent 独立身份 Session

**问题描述**：
当前 SE SDK 中 Agent 仅作为 Participant 存在，没有独立的身份认证和会话管理能力。Agent 无法独立登录、无法获取独立 Token、无法独立调用 API。

**影响范围**：
- Agent 无法独立运行和认证
- Agent 间无法建立可信通信
- 无法实现 Agent 权限隔离

**期望接口**：

```java
package net.ooder.scene.agent;

public interface AgentSessionManager {
    
    AgentSession register(AgentRegistration registration);
    
    AgentSession authenticate(String agentId, String credentials);
    
    void invalidate(String agentId);
    
    AgentSession getSession(String agentId);
    
    boolean isValid(String sessionToken);
    
    void heartbeat(String agentId);
}

public class AgentSession {
    private String agentId;
    private String sessionToken;
    private AgentStatus status;
    private long loginTime;
    private long lastHeartbeat;
    private long expireTime;
    private Map<String, Object> attributes;
}

public enum AgentStatus {
    ONLINE, BUSY, IDLE, OFFLINE
}
```

**MVP 当前临时方案**：
已在 MVP 层实现 AgentSessionService，但无法与 SE SDK 的 Participant 体系打通。

---

#### 需求 2：Agent-to-Agent 消息机制

**问题描述**：
SE SDK 没有 Agent 间消息传递机制，Agent 无法直接通信，无法实现任务委派、结果汇报、协作请求等场景。

**影响范围**：
- Agent 无法委派任务给其他 Agent
- Agent 无法汇报执行结果
- Agent 无法请求协作支持

**期望接口**：

```java
package net.ooder.scene.agent;

public interface AgentMessageBus {
    
    String send(AgentMessage message);
    
    List<AgentMessage> receive(String agentId);
    
    void subscribe(String agentId, MessageHandler handler);
    
    void acknowledge(String agentId, String messageId);
    
    int getPendingCount(String agentId);
}

public class AgentMessage {
    private String messageId;
    private String fromAgent;
    private String toAgent;
    private String sceneGroupId;
    private MessageType type;
    private Map<String, Object> payload;
    private int priority;
    private long createTime;
    private long expireTime;
}

public enum MessageType {
    TASK_DELEGATE,      // 任务委派
    TASK_RESULT,        // 任务结果
    COLLAB_REQUEST,     // 协作请求
    DATA_SHARE,         // 数据共享
    STATUS_UPDATE,      // 状态更新
    HEARTBEAT           // 心跳
}
```

**MVP 当前临时方案**：
已在 MVP 层实现 AgentMessageService，但消息无法与 SE SDK 场景执行流程集成。

---

#### 需求 3：任务执行状态回调

**问题描述**：
SE SDK 执行场景/能力时，没有状态变更回调机制，外部系统无法感知执行进度和结果。

**影响范围**：
- 用户无法收到 Agent 任务执行状态通知
- 无法生成待办事项通知用户
- 无法实现任务进度追踪

**期望接口**：

```java
package net.ooder.scene.execution;

public interface ExecutionListener {
    
    void onStarted(ExecutionContext context);
    
    void onProgress(ExecutionContext context, int progress, String message);
    
    void onCompleted(ExecutionContext context, ExecutionResult result);
    
    void onFailed(ExecutionContext context, Throwable error);
    
    void onTimeout(ExecutionContext context);
}

public class ExecutionContext {
    private String executionId;
    private String sceneGroupId;
    private String agentId;
    private String capabilityId;
    private long startTime;
    private Map<String, Object> parameters;
}
```

**MVP 当前临时方案**：
通过 WebSocket 广播执行状态，但无法获取 SE SDK 内部的执行细节。

---

### 3.2 P1 - 应该实现

#### 需求 4：知识库绑定扩展

**问题描述**：
SE SDK 的 SceneGroup 不支持知识库绑定，无法为场景配置专属知识库。

**当前代码**：
```java
// SceneGroupServiceSEImpl.java:420-447
@Override
public boolean bindKnowledgeBase(String sceneGroupId, KnowledgeBindingDTO binding) {
    throw new UnsupportedOperationException("SE SDK does not support knowledge base binding...");
}
```

**期望接口**：

```java
public interface KnowledgeBindingManager {
    
    String bindKnowledgeBase(String sceneGroupId, KnowledgeBinding binding);
    
    void unbindKnowledgeBase(String sceneGroupId, String knowledgeBaseId);
    
    List<KnowledgeBinding> getKnowledgeBindings(String sceneGroupId);
}

public class KnowledgeBinding {
    private String bindingId;
    private String sceneGroupId;
    private String knowledgeBaseId;
    private String knowledgeBaseName;
    private BindingScope scope;
    private int priority;
}

public enum BindingScope {
    SCENE_GROUP,    // 场景组级别
    SCENE,          // 场景级别
    CAPABILITY      // 能力级别
}
```

---

#### 需求 5：场景级 LLM 配置

**问题描述**：
SE SDK 没有场景级 LLM 配置能力，无法为不同场景配置不同的 LLM 模型或参数。

**当前代码**：
```java
// SceneGroupServiceSEImpl.java:455-475
@Override
public LlmConfigDTO getLlmConfig(String sceneGroupId) {
    throw new UnsupportedOperationException("SE SDK does not support LLM config...");
}
```

**期望接口**：

```java
public interface SceneLlmConfigManager {
    
    LlmConfig getLlmConfig(String sceneGroupId);
    
    void setLlmConfig(String sceneGroupId, LlmConfig config);
    
    void resetLlmConfig(String sceneGroupId);
}

public class LlmConfig {
    private String provider;        // openai, azure, local
    private String model;          // gpt-4, gpt-3.5-turbo
    private double temperature;
    private int maxTokens;
    private Map<String, Object> extensions;
}
```

---

### 3.2 P2 - 可以实现

#### 需求 6：场景快照增强

**问题描述**：
当前快照功能不完整，无法完整恢复场景状态。

**期望增强**：
- 支持快照版本管理
- 支持增量快照
- 支持跨环境快照迁移

#### 需求 7：故障转移机制

**问题描述**：
Agent 故障时没有自动转移机制。

**期望增强**：
- Agent 心跳超时自动检测
- 任务自动重新分配
- 故障恢复通知

---

## 四、接口映射对照表

| MVP 需求 | SE SDK 现有接口 | 差距 | 优先级 |
|---------|----------------|------|--------|
| Agent 独立登录 | Participant | 无 Session 概念 | P0 |
| Agent 间消息 | 无 | 完全缺失 | P0 |
| 任务状态回调 | 无 | 完全缺失 | P0 |
| 知识库绑定 | 无 | 完全缺失 | P1 |
| 场景 LLM 配置 | 无 | 完全缺失 | P1 |
| 快照增强 | SceneGroupSnapshot | 功能不完整 | P2 |
| 故障转移 | 无 | 完全缺失 | P2 |

---

## 五、数据模型扩展建议

### 5.1 SceneGroup 扩展

```java
public class SceneGroup {
    // 现有字段...
    
    // 建议新增
    private String defaultAgentId;          // 默认执行 Agent
    private String llmConfigId;             // LLM 配置 ID
    private List<String> knowledgeBaseIds;  // 关联知识库
    private Map<String, Object> extensions; // 扩展属性
}
```

### 5.2 Participant 扩展

```java
public class Participant {
    // 现有字段...
    
    // 建议新增
    private String sessionToken;            // 会话 Token
    private AgentCapabilities capabilities; // Agent 能力声明
    private long sessionExpireTime;        // 会话过期时间
}

public class AgentCapabilities {
    private List<String> supportedCapabilities;
    private int maxConcurrentTasks;
    private Map<String, Object> resources;
}
```

---

## 六、集成测试建议

### 6.1 Agent 身份测试

```java
@Test
void testAgentSessionLifecycle() {
    // 1. 注册
    AgentSession session = agentSessionManager.register(registration);
    assertNotNull(session.getSessionToken());
    
    // 2. 认证
    AgentSession authed = agentSessionManager.authenticate(
        session.getAgentId(), credentials);
    assertTrue(authed.isValid());
    
    // 3. 心跳
    agentSessionManager.heartbeat(session.getAgentId());
    
    // 4. 失效
    agentSessionManager.invalidate(session.getAgentId());
    assertFalse(agentSessionManager.isValid(session.getSessionToken()));
}
```

### 6.2 A2A 消息测试

```java
@Test
void testAgentMessageDelivery() {
    // 1. 发送消息
    AgentMessage msg = new AgentMessage();
    msg.setFromAgent("agent-001");
    msg.setToAgent("agent-002");
    msg.setType(MessageType.TASK_DELEGATE);
    String msgId = agentMessageBus.send(msg);
    
    // 2. 接收消息
    List<AgentMessage> messages = agentMessageBus.receive("agent-002");
    assertEquals(1, messages.size());
    assertEquals(msgId, messages.get(0).getMessageId());
    
    // 3. 确认消息
    agentMessageBus.acknowledge("agent-002", msgId);
    assertEquals(0, agentMessageBus.getPendingCount("agent-002"));
}
```

---

## 七、时间计划建议

| 阶段 | 内容 | 预计时间 | 负责方 |
|------|------|---------|--------|
| 需求确认 | 双方确认需求细节 | 1 周 | 双方 |
| 接口设计 | SE SDK 接口设计 | 1 周 | SE SDK 团队 |
| P0 实现 | Agent Session + A2A + 回调 | 4 周 | SE SDK 团队 |
| MVP 集成 | MVP 对接新接口 | 2 周 | MVP 团队 |
| 联调测试 | 集成测试 | 1 周 | 双方 |
| P1 实现 | 知识库 + LLM 配置 | 2 周 | SE SDK 团队 |

---

## 八、联系方式

**MVP 团队负责人**：[待填写]
**SE SDK 团队负责人**：[待填写]
**协作状态**：待 SE SDK 团队确认

---

## 九、附录

### 9.1 MVP 临时实现文件

| 文件 | 说明 |
|------|------|
| AgentSessionService.java | Agent 会话服务接口 |
| AgentSessionServiceImpl.java | Agent 会话服务实现 |
| AgentMessageService.java | A2A 消息服务接口 |
| AgentMessageServiceImpl.java | A2A 消息服务实现 |

### 9.2 相关文档

- 协作开发需求说明
- SE SDK 与 MVP 协作实现需求说明
