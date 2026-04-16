# SE SDK 协作声明文档

## 文档信息

| 项目 | 内容 |
|-----|------|
| 文档编号 | SE-SDK-COOP-2026-001 |
| 创建日期 | 2026-04-04 |
| 需求方 | Agent-Chat 模块团队 |
| 供应方 | SE SDK 团队 |
| 优先级 | P1 - 重要 |
| 状态 | 待响应 |

---

## 一、背景说明

Agent-Chat 模块已完成前端页面和基础API实现，P0阻塞性问题已全部修复。但在后端服务层发现 **SE SDK接口调用均为占位实现**，需要SE SDK团队提供真实实现支持以完成P1任务。

**当前已实现本地Fallback适配器**，可在SE SDK不可用时提供基础功能，但需要SE SDK提供完整的企业级能力。

---

## 二、需求接口清单

### 2.1 UnifiedSessionManager (P0 - 最高优先级)

**功能描述**: 统一会话管理，支持多种会话类型

| 方法 | 当前状态 | 期望功能 | 调用位置 |
|-----|---------|---------|---------|
| `createSession()` | 占位 | 创建会话，返回会话ID | UnifiedAgentChatServiceImpl.java:42 |
| `getSession()` | 占位 | 获取会话详情 | UnifiedAgentChatServiceImpl.java:50 |
| `getActiveSessionsByScene()` | 占位 | 获取场景下所有活跃会话 | UnifiedAgentChatServiceImpl.java:58 |
| `closeSession()` | 占位 | 关闭会话 | UnifiedAgentChatServiceImpl.java:66 |

**期望接口契约**:
```java
public interface UnifiedSessionManager {
    String createSession(String sceneGroupId, String userId, SessionType type);
    Session getSession(String sessionId);
    List<Session> getActiveSessionsByScene(String sceneGroupId);
    void closeSession(String sessionId);
    
    enum SessionType { USER, AGENT, SCENE, CONVERSATION }
}
```

---

### 2.2 MessageQueueService (P0 - 最高优先级)

**功能描述**: 消息队列服务，支持离线消息、消息重试、优先级

| 方法 | 当前状态 | 期望功能 | 调用位置 |
|-----|---------|---------|---------|
| `enqueue()` | 占位 | 消息入队，支持优先级 | UnifiedAgentChatServiceImpl.java:59 |
| `dequeue()` | 占位 | 消息出队 | UnifiedAgentChatServiceImpl.java:65 |
| `getOfflineMessages()` | 占位 | 获取离线消息 | UnifiedAgentChatServiceImpl.java:79 |
| `acknowledge()` | 占位 | 消息确认 | UnifiedAgentChatServiceImpl.java:85 |

**期望接口契约**:
```java
public interface MessageQueueService {
    void enqueue(String queueName, Message message, int priority);
    Message dequeue(String queueName);
    List<Message> getOfflineMessages(String userId);
    void acknowledge(String messageId);
}
```

---

### 2.3 AgentContextManager (P1 - 高优先级)

**功能描述**: Agent上下文管理，支持虚拟/物理Agent注册

| 方法 | 当前状态 | 期望功能 | 调用位置 |
|-----|---------|---------|---------|
| `getAgentContext()` | 占位 | 获取Agent上下文 | UnifiedAgentChatServiceImpl.java:90 |
| `getAgentsByScene()` | 占位 | 获取场景下所有Agent | UnifiedA2AService.java:108 |
| `getOnlineAgents()` | 占位 | 获取在线Agent列表 | UnifiedA2AService.java:115 |
| `registerAgent()` | 占位 | 注册Agent | UnifiedA2AService.java:122 |

**期望接口契约**:
```java
public interface AgentContextManager {
    AgentContext getAgentContext(String agentId);
    List<AgentInfo> getAgentsByScene(String sceneGroupId);
    List<AgentInfo> getOnlineAgents(String sceneGroupId);
    void registerAgent(AgentRegistration registration);
}
```

---

### 2.4 A2AProtocolService (P1 - 高优先级)

**功能描述**: A2A协议服务，支持消息路由、协议转换

| 方法 | 当前状态 | 期望功能 | 调用位置 |
|-----|---------|---------|---------|
| `sendA2AMessage()` | 占位 | 发送A2A协议消息 | UnifiedA2AService.java:48 |
| `broadcastToAgents()` | 占位 | 广播消息给Agent | UnifiedA2AService.java:75 |
| `registerHandler()` | 占位 | 注册消息处理器 | UnifiedA2AService.java:88 |
| `routeMessage()` | 占位 | 路由消息 | UnifiedA2AService.java:95 |

**期望接口契约**:
```java
public interface A2AProtocolService {
    void sendA2AMessage(A2AMessage message);
    void broadcastToAgents(String sceneGroupId, A2AMessage message);
    void registerHandler(String messageType, MessageHandler handler);
    void routeMessage(A2AMessage message);
}
```

---

## 三、当前占位实现示例

**文件**: `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\service\impl\UnifiedAgentChatServiceImpl.java`

```java
@Override
public SceneChatContextDTO getUnifiedChatContext(String sceneGroupId, String userId) {
    if (!useUnifiedInterface()) {
        return chatService.getChatContext(sceneGroupId, userId);
    }
    // This is a placeholder - actual implementation would use the unified session manager
    SceneChatContextDTO context = new SceneChatContextDTO();
    context.setSceneGroupId(sceneGroupId);
    return context;
}
```

---

## 四、期望交付时间

| 接口 | 优先级 | 期望交付 | 验证标准 |
|-----|-------|---------|---------|
| UnifiedSessionManager | P0 | 2026-04-11 | 返回非空Session对象 |
| MessageQueueService | P0 | 2026-04-11 | 消息入队/出队正常 |
| AgentContextManager | P1 | 2026-04-18 | Agent列表正确返回 |
| A2AProtocolService | P1 | 2026-04-18 | 消息路由正常 |

---

## 五、验证标准

### 5.1 功能验证

- [ ] 接口方法返回非空值
- [ ] 接口方法有实际业务逻辑
- [ ] 接口方法支持事务
- [ ] 接口方法有异常处理

### 5.2 性能验证

- [ ] 单次调用响应时间 < 100ms
- [ ] 支持并发调用
- [ ] 支持集群部署

### 5.3 集成验证

- [ ] Spring Bean正确注入
- [ ] 配置项正确加载
- [ ] 日志正确输出

---

## 六、本地Fallback实现

Agent-Chat模块已实现本地Fallback适配器，在SE SDK不可用时提供基础功能：

| 本地接口 | 实现类 | 功能 |
|---------|-------|------|
| LocalSessionManager | LocalSessionManagerImpl | 内存会话管理 |
| LocalMessageQueueService | LocalMessageQueueServiceImpl | 内存消息队列 |
| LocalAgentContextService | LocalAgentContextServiceImpl | 内存Agent上下文 |
| LocalA2AProtocolService | LocalA2AProtocolServiceImpl | 内存A2A协议 |

**文件路径**: `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\spi\impl\`

---

## 七、文件路径索引

| 文件 | 路径 |
|-----|------|
| UnifiedAgentChatServiceImpl | `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\service\impl\UnifiedAgentChatServiceImpl.java` |
| UnifiedA2AService | `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\service\impl\UnifiedA2AService.java` |
| UnifiedInterfaceConfig | `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\config\UnifiedInterfaceConfig.java` |
| 本地SPI实现 | `e:\apex\os\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\spi\impl\` |

---

## 八、联系方式

| 角色 | 联系人 |
|-----|-------|
| 需求方 | Agent-Chat 模块团队 |
| 供应方 | SE SDK 团队 |
| 协调人 | 架构组 |
