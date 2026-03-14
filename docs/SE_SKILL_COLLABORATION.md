# SE 与 Skill 协作文档

## 1. 文档概述

### 1.1 目的

本文档详细说明 Scene Engine (SE) 与 Skill 插件之间的协作方式、存在的问题、解决方案和任务分配。

### 1.2 适用范围

- SE 开发团队
- Skill 开发团队
- 架构设计师
- 技术决策者

### 1.3 版本信息

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-14 | 初始版本 |

---

## 2. 架构概述

### 2.1 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     MVP 主应用 (ClassLoader A)                 │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │ Spring 容器                                                 │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │ │
│  │  │ SceneClient    │  │ JsonStorageService│  │ VectorStore │  │ │
│  │  └────────┬──────┘  └────────┬──────┘  └──────┬──────┘  │ │
│  │           │                  │                │          │ │
│  │  ┌────────┴──────────────────┴────────────────┴───────┐  │ │
│  │  │ SE 核心服务 (scene-engine-2.3.1.jar)              │  │ │
│  │  │  - ContextStorageService (接口) ✅                │  │ │
│  │  │  - JsonStorageService (实现) ✅ @Service          │  │ │
│  │  │  - ConversationService (接口) ✅                 │  │ │
│  │  │  - ConversationServiceImpl (实现) ❌ 无@Service   │  │ │
│  │  │  - MemoryContext (工具类) ✅                     │  │ │
│  │  │  - KnowledgeContext (工具类) ✅                  │  │ │
│  │  └────────────────────────────────────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼ ClassLoader 隔离
┌─────────────────────────────────────────────────────────────────┐
│                     Skill 插件 (ClassLoader B)                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │ skill-llm-chat                                              │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │ │
│  │  │ ChatController │  │ ChatStorageService│  │ KnowledgeSvc│  │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────┘  │ │
│  │                                                             │ │
│  │  问题：无法通过 @Autowired 注入 SE 服务                    │ │
│  │  原因：ClassLoader 隔离 + 控制器非 Spring Bean             │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 ClassLoader 层次

```
Bootstrap ClassLoader
        │
        ▼
Extension ClassLoader
        │
        ▼
Application ClassLoader (MVP 主应用)
        │
        ├──▶ SE 核心类 (scene-engine-2.3.1.jar)
        │
        └──▶ PluginClassLoader[skill-llm-chat]
                    │
                    └──▶ Skill 类 (skill-llm-chat-2.3.1.jar)
```

---

## 3. 当前问题分析

### 3.1 问题清单

| 编号 | 问题 | 严重程度 | 影响范围 |
|------|------|----------|----------|
| P1 | ClassLoader 隔离导致 Skill 无法访问 SE 服务 Bean | ❌ 严重 | 所有 Skill |
| P2 | ConversationServiceImpl 没有 @Service 注解 | ⚠️ 中等 | 对话功能 |
| P3 | MemoryContext 持久化未实现 (TODO) | ⚠️ 中等 | 对话历史 |
| P4 | KnowledgeContext RAG 搜索未实现 | ⚠️ 中等 | 知识库 |
| P5 | Skill 控制器非 Spring Bean，无法使用 @Autowired | ❌ 严重 | 所有 Skill |

### 3.2 问题详细分析

#### P1: ClassLoader 隔离问题

**现象**：
```
java.lang.ClassNotFoundException: net.ooder.scene.skill.engine.context.impl.JsonStorageService
    at net.ooder.skill.hotplug.classloader.PluginClassLoader.loadClass(PluginClassLoader.java:103)
```

**原因**：
1. Skill 使用独立的 `PluginClassLoader` 加载
2. SE 的实现类在主应用的 ClassLoader 中
3. 子 ClassLoader 无法访问父 ClassLoader 中的实现类

**影响**：
- Skill 无法直接使用 SE 的服务实现
- 需要在 Skill 中重复实现存储逻辑

#### P2: ConversationServiceImpl 未暴露

**现象**：
```java
// ConversationServiceImpl.java
public class ConversationServiceImpl implements ConversationService {
    // ❌ 没有 @Service 注解
}
```

**原因**：
- SE 开发时未考虑 Skill 插件的使用场景
- 只提供了接口，未暴露实现类

**影响**：
- Skill 无法使用 SE 的对话服务
- 需要自己实现对话管理逻辑

#### P3: MemoryContext 持久化未实现

**现象**：
```java
// LlmRuntimeContextAssembler.java
private MemoryContext loadMemoryContext(String sessionId) {
    MemoryContext memory = new MemoryContext();
    memory.setSessionId(sessionId);
    
    // TODO: 从持久化存储加载历史消息
    // List<Map<String, Object>> history = sessionService.getHistory(sessionId);
    // memory.setHistory(history);
    
    return memory;  // ⚠️ 返回空的 MemoryContext
}
```

**原因**：
- SE 的 TODO 未完成
- 缺少持久化存储的集成

**影响**：
- 对话历史无法持久化
- 重启后对话丢失

#### P4: KnowledgeContext RAG 搜索未实现

**现象**：
```java
// SkillsContextServiceImpl.java
public List<String> getKnowledgeContext(String query, int limit) {
    if (knowledgeService == null) {
        return new ArrayList<>();  // ⚠️ 返回空列表
    }
    return knowledgeService.search(query, limit);
}
```

**原因**：
- `knowledgeService` 依赖未注入
- RAG 搜索功能未集成

**影响**：
- 知识库搜索功能不可用
- LLM 无法获取知识库信息

#### P5: Skill 控制器非 Spring Bean

**现象**：
```java
// RouteRegistry.java
private Object createControllerInstance(Class<?> controllerClass) {
    return controllerClass.newInstance();  // ❌ 直接反射创建，非 Spring Bean
}
```

**原因**：
- Skill 控制器通过反射创建
- 不是 Spring 管理的 Bean
- 无法使用 `@Autowired` 注入

**影响**：
- 无法使用依赖注入
- 无法访问主应用的 Spring Bean

---

## 4. 解决方案

### 4.1 方案对比

| 方案 | 优点 | 缺点 | 推荐程度 |
|------|------|------|----------|
| **方案一：SE 暴露服务 Bean** | 统一管理、依赖注入、可测试性高 | 需要 SE 改动 | ⭐⭐⭐⭐⭐ |
| **方案二：SE 提供 SPI 接口** | 无需 Spring、简单直接 | 全局状态、测试困难 | ⭐⭐⭐⭐ |
| **方案三：Skill 自己实现** | 独立性、灵活性 | 重复实现、不一致性 | ⭐⭐ |

### 4.2 推荐方案：SE 暴露服务 Bean + SPI 双轨制

#### 4.2.1 SE 侧改动

**任务 S1：添加服务 Bean 配置**

```java
// SceneEngineAutoConfiguration.java
@Configuration
public class SceneEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ContextStorageService contextStorageService() {
        return new JsonStorageService();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ConversationService conversationService(
            KnowledgeBaseService knowledgeService,
            RagApi ragPipeline,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {
        return new ConversationServiceImpl(knowledgeService, ragPipeline, toolRegistry, toolOrchestrator);
    }
}
```

**任务 S2：提供 SPI 服务入口**

```java
// SceneServices.java
public final class SceneServices {
    
    private static SceneServiceFactory factory;
    
    public static void setFactory(SceneServiceFactory f) {
        factory = f;
    }
    
    public static ContextStorageService getStorageService() {
        return factory != null ? factory.getStorageService() : null;
    }
    
    public static ConversationService getConversationService() {
        return factory != null ? factory.getConversationService() : null;
    }
}

// SceneServiceFactory.java
public interface SceneServiceFactory {
    ContextStorageService getStorageService();
    ConversationService getConversationService();
}
```

**任务 S3：实现 MemoryContext 持久化**

```java
// LlmRuntimeContextAssembler.java
private MemoryContext loadMemoryContext(String sessionId) {
    MemoryContext memory = new MemoryContext();
    memory.setSessionId(sessionId);
    
    // ✅ 从持久化存储加载历史消息
    ContextStorageService storage = SceneServices.getStorageService();
    if (storage != null) {
        List<Map<String, Object>> history = storage.loadChatHistory(sessionId, 100);
        memory.setHistory(history);
    }
    
    return memory;
}
```

#### 4.2.2 Skill 侧改动

**任务 K1：使用 SE 服务**

```java
// ChatController.java
@RestController
public class ChatController {

    private final ContextStorageService storageService;
    private final ConversationService conversationService;
    
    public ChatController() {
        // ✅ 通过 SPI 获取 SE 服务
        this.storageService = SceneServices.getStorageService();
        this.conversationService = SceneServices.getConversationService();
    }
    
    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(...) {
        // ✅ 使用 SE 的对话服务
        MessageResponse response = conversationService.sendMessage(conversationId, msgRequest);
        return ResultModel.success(convertToChatMessage(response));
    }
}
```

**任务 K2：移除重复实现**

- 删除 `ChatStorageService`
- 删除 `KnowledgeStorageService`
- 删除 `SkillsContextServiceImpl` 中的重复逻辑

---

## 5. 任务分配

### 5.1 SE 团队任务

| 任务编号 | 任务名称 | 优先级 | 预计工时 | 依赖 |
|----------|----------|--------|----------|------|
| S1 | 添加 ConversationService Bean 配置 | P0 | 2h | 无 |
| S2 | 提供 SPI 服务入口 (SceneServices) | P0 | 4h | 无 |
| S3 | 实现 MemoryContext 持久化 | P1 | 4h | S2 |
| S4 | 实现 KnowledgeContext RAG 搜索 | P1 | 8h | 无 |
| S5 | 解决 ClassLoader 隔离问题 | P2 | 16h | 无 |

### 5.2 Skill 团队任务

| 任务编号 | 任务名称 | 优先级 | 预计工时 | 依赖 |
|----------|----------|--------|----------|------|
| K1 | 使用 SE SPI 获取服务 | P0 | 2h | S2 |
| K2 | 移除 ChatStorageService | P1 | 1h | K1 |
| K3 | 移除 KnowledgeStorageService | P1 | 1h | K1 |
| K4 | 更新 ChatController 使用 SE 服务 | P1 | 4h | K1 |
| K5 | 测试验证 | P1 | 4h | K4 |

### 5.3 任务依赖关系

```
SE 团队                          Skill 团队
────────                         ──────────
S1 (Bean配置) ─────┐
                    │
S2 (SPI入口) ───────┼──────────────────▶ K1 (使用SPI)
                    │                         │
S3 (持久化) ◀────────┘                         │
                                              ▼
                                        K2 (移除存储)
                                              │
                                              ▼
                                        K3 (移除知识库)
                                              │
                                              ▼
                                        K4 (更新控制器)
                                              │
                                              ▼
                                        K5 (测试验证)
```

---

## 6. 接口规范

### 6.1 ContextStorageService 接口

```java
public interface ContextStorageService {
    // 用户上下文
    void saveUserContext(String userId, Map<String, Object> context);
    Map<String, Object> loadUserContext(String userId);
    void deleteUserContext(String userId);
    
    // 会话上下文
    void saveSessionContext(String sessionId, Map<String, Object> context);
    Map<String, Object> loadSessionContext(String sessionId);
    boolean sessionExists(String sessionId);
    void deleteSession(String sessionId);
    
    // Skill 上下文
    void saveSkillContext(String skillId, String sessionId, Map<String, Object> context);
    Map<String, Object> loadSkillContext(String skillId, String sessionId);
    void deleteSkillContext(String skillId, String sessionId);
    
    // 对话历史
    void saveChatMessage(String sessionId, Map<String, Object> message);
    List<Map<String, Object>> loadChatHistory(String sessionId, int limit);
    void clearChatHistory(String sessionId);
    
    // 页面状态
    void savePageState(String sessionId, String pageId, Map<String, Object> state);
    Map<String, Object> loadPageState(String sessionId, String pageId);
}
```

### 6.2 ConversationService 接口

```java
public interface ConversationService {
    // 对话管理
    Conversation createConversation(String userId, ConversationCreateRequest request);
    Conversation getConversation(String conversationId);
    void deleteConversation(String conversationId);
    List<Conversation> listConversations(String userId, int limit);
    
    // 消息发送
    MessageResponse sendMessage(String conversationId, MessageRequest request);
    void sendMessageStream(String conversationId, MessageRequest request, StreamMessageHandler handler);
    
    // 历史管理
    List<Message> getHistory(String conversationId, int limit);
    void clearHistory(String conversationId);
    
    // 统计信息
    ConversationStats getStats(String conversationId);
}
```

### 6.3 SceneServices SPI 接口

```java
public final class SceneServices {
    
    /**
     * 获取上下文存储服务
     * @return 存储服务实例，如果未初始化返回 null
     */
    public static ContextStorageService getStorageService();
    
    /**
     * 获取对话服务
     * @return 对话服务实例，如果未初始化返回 null
     */
    public static ConversationService getConversationService();
    
    /**
     * 检查服务是否已初始化
     * @return true 如果服务已初始化
     */
    public static boolean isInitialized();
}
```

---

## 7. 测试计划

### 7.1 单元测试

| 测试项 | 测试内容 | 负责团队 |
|--------|----------|----------|
| ContextStorageService | 存储和加载各种上下文 | SE 团队 |
| ConversationService | 对话创建、消息发送、历史管理 | SE 团队 |
| SceneServices SPI | 服务获取、空值处理 | SE 团队 |
| ChatController | 使用 SE 服务的控制器逻辑 | Skill 团队 |

### 7.2 集成测试

| 测试项 | 测试内容 | 负责团队 |
|--------|----------|----------|
| SE + Skill 集成 | Skill 通过 SPI 访问 SE 服务 | 双方协作 |
| 持久化测试 | 重启后对话历史保留 | Skill 团队 |
| 知识库测试 | RAG 搜索功能 | Skill 团队 |

### 7.3 验收标准

| 标准 | 说明 |
|------|------|
| ✅ Skill 可以通过 SPI 获取 SE 服务 | `SceneServices.getStorageService()` 返回非空 |
| ✅ 对话历史持久化 | 重启后对话历史保留 |
| ✅ 知识库搜索可用 | RAG 搜索返回相关结果 |
| ✅ 无重复代码 | Skill 中无存储相关重复实现 |

---

## 8. 风险评估

### 8.1 技术风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| ClassLoader 隔离无法解决 | 中 | 高 | 使用 SPI 作为备选方案 |
| SPI 初始化时机问题 | 低 | 中 | 提供延迟初始化机制 |
| 性能问题 | 低 | 中 | 添加缓存机制 |

### 8.2 进度风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| SE 团队资源不足 | 中 | 高 | 优先完成 S1、S2 任务 |
| 接口变更频繁 | 低 | 中 | 建立接口评审机制 |

---

## 9. 里程碑

### 9.1 Phase 1: 基础设施 (Week 1)

- [ ] S1: 添加 ConversationService Bean 配置
- [ ] S2: 提供 SPI 服务入口
- [ ] K1: 使用 SE SPI 获取服务

### 9.2 Phase 2: 功能完善 (Week 2)

- [ ] S3: 实现 MemoryContext 持久化
- [ ] S4: 实现 KnowledgeContext RAG 搜索
- [ ] K2-K4: 移除重复实现，更新控制器

### 9.3 Phase 3: 测试验收 (Week 3)

- [ ] 单元测试
- [ ] 集成测试
- [ ] 验收测试

---

## 10. 联系方式

### 10.1 团队负责人

| 团队 | 负责人 | 联系方式 |
|------|--------|----------|
| SE 团队 | - | - |
| Skill 团队 | - | - |

### 10.2 沟通渠道

- 技术讨论：GitHub Issues
- 紧急问题：即时通讯工具
- 周例会：每周一 10:00

---

## 11. 附录

### 11.1 相关文档

- [SE_CONVERSATION_ARCHITECTURE.md](./SE_CONVERSATION_ARCHITECTURE.md) - SE 对话架构设计
- [SE_SERVICE_EXPOSURE_ISSUE.md](./SE_SERVICE_EXPOSURE_ISSUE.md) - SE 服务暴露问题报告

### 11.2 变更历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0 | 2026-03-14 | 初始版本 | - |
