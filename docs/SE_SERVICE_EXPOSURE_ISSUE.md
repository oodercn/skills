# SE 服务暴露问题报告与改进建议

## 1. 问题概述

### 1.1 发现的问题

在 skill-llm-chat 集成 SE 2.3.1 时，发现以下类无法在 Skill 插件的 ClassLoader 中访问：

```
java.lang.ClassNotFoundException: net.ooder.scene.skill.engine.context.impl.JsonStorageService
```

### 1.2 问题原因

SE 的以下服务类是**内部实现类**，没有通过 Spring Bean 或 SPI 暴露给 Skill 插件：

| 类 | 类型 | 问题 |
|---|---|---|
| `JsonStorageService` | 实现类 | 无法在 Skill ClassLoader 中加载 |
| `ConversationServiceImpl` | 实现类 | 无法在 Skill ClassLoader 中加载 |
| `MemoryContext` | 工具类 | 可以访问，但依赖其他内部类 |
| `KnowledgeContext` | 工具类 | 可以访问，但依赖其他内部类 |

### 1.3 当前架构限制

```
┌─────────────────────────────────────────────────────────────────┐
│                     MVP 主应用 (ClassLoader A)                 │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │ SE 核心类 (scene-engine-2.3.1.jar)                        │ │
│  │  - ContextStorageService (接口) ✅ 可访问                  │ │
│  │  - JsonStorageService (实现类) ❌ 无法访问                 │ │
│  │  - ConversationService (接口) ✅ 可访问                    │ │
│  │  - ConversationServiceImpl (实现类) ❌ 无法访问            │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼ 隔离
┌─────────────────────────────────────────────────────────────────┐
│                     Skill 插件 (ClassLoader B)                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │ skill-llm-chat                                            │ │
│  │  - ChatController                                         │ │
│  │  - 需要: JsonStorageService ❌                            │ │
│  │  - 需要: ConversationServiceImpl ❌                        │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 2. 改进建议

### 2.1 方案一：SE 暴露服务 Bean（推荐）

#### 改进内容

在 SE 的 `SceneEngineAutoConfiguration` 中添加服务 Bean：

```java
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
            LlmService llmService,
            ContextStorageService storageService,
            KnowledgeBaseService knowledgeService) {
        return new ConversationServiceImpl(llmService, storageService, knowledgeService, null);
    }
}
```

#### 期望结果

Skill 插件可以通过 `@Autowired` 注入 SE 服务：

```java
@RestController
public class ChatController {

    @Autowired
    private ConversationService conversationService;  // ✅ 可以注入
    
    @Autowired
    private ContextStorageService storageService;  // ✅ 可以注入
    
    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(...) {
        MessageResponse response = conversationService.sendMessage(conversationId, msgRequest);
        return ResultModel.success(convertToChatMessage(response));
    }
}
```

#### 优点

| 优点 | 说明 |
|------|------|
| **统一管理** | SE 统一管理服务生命周期 |
| **依赖注入** | Skill 可以使用 Spring 依赖注入 |
| **可测试性** | 易于 Mock 和单元测试 |
| **一致性** | 所有 Skill 使用相同的服务实现 |
| **可扩展性** | Skill 可以提供自己的实现覆盖默认实现 |

#### 缺点

| 缺点 | 说明 |
|------|------|
| **SE 改动** | 需要修改 SE 代码 |
| **版本依赖** | Skill 需要依赖 SE 2.3.2+ |

### 2.2 方案二：SE 提供 SPI 接口

#### 改进内容

在 SE 中添加 SPI 服务提供者：

```java
// SE 提供的服务工厂接口
public interface SceneServiceFactory {
    
    ContextStorageService getStorageService();
    
    ConversationService getConversationService();
    
    KnowledgeBaseService getKnowledgeService();
}

// SE 提供的默认实现
public class DefaultSceneServiceFactory implements SceneServiceFactory {
    // ...
}

// SE 提供的服务访问入口
public final class SceneServices {
    
    private static SceneServiceFactory factory;
    
    public static void setFactory(SceneServiceFactory f) {
        factory = f;
    }
    
    public static ContextStorageService getStorageService() {
        return factory.getStorageService();
    }
    
    public static ConversationService getConversationService() {
        return factory.getConversationService();
    }
}
```

#### 期望结果

Skill 插件可以通过静态方法访问 SE 服务：

```java
@RestController
public class ChatController {

    private final ConversationService conversationService;
    
    public ChatController() {
        this.conversationService = SceneServices.getConversationService();
    }
}
```

#### 优点

| 优点 | 说明 |
|------|------|
| **无需 Spring** | 不依赖 Spring 容器 |
| **简单直接** | 静态方法调用简单 |
| **灵活性** | 可以在任何地方使用 |

#### 缺点

| 缺点 | 说明 |
|------|------|
| **全局状态** | 静态方法引入全局状态 |
| **测试困难** | 难以 Mock |
| **不符合 Spring 风格** | 与 Spring 的依赖注入风格不一致 |

### 2.3 方案三：Skill 自己实现（当前方案）

#### 改进内容

Skill 自己实现存储服务，不依赖 SE：

```java
@RestController
public class ChatController {

    private final ChatStorageService chatStorage;  // Skill 自己实现
    private final KnowledgeStorageService knowledgeStorage;  // Skill 自己实现
    
    public ChatController() {
        this.chatStorage = new ChatStorageService();
        this.knowledgeStorage = new KnowledgeStorageService();
    }
}
```

#### 优点

| 优点 | 说明 |
|------|------|
| **独立性** | Skill 完全独立，不依赖 SE 内部实现 |
| **灵活性** | Skill 可以根据需求定制实现 |
| **向后兼容** | 不受 SE 版本升级影响 |

#### 缺点

| 缺点 | 说明 |
|------|------|
| **重复实现** | 每个 Skill 都需要实现存储逻辑 |
| **不一致性** | 不同 Skill 可能有不同的实现 |
| **维护成本** | 需要维护多份代码 |
| **数据隔离** | 不同 Skill 的数据无法共享 |

## 3. 方案对比

| 对比项 | 方案一 (SE Bean) | 方案二 (SPI) | 方案三 (Skill 自己实现) |
|--------|-----------------|--------------|------------------------|
| **实现难度** | 中等 | 中等 | 低 |
| **SE 改动** | 需要 | 需要 | 不需要 |
| **Skill 改动** | 小 | 小 | 大 |
| **可维护性** | 高 | 中 | 低 |
| **可测试性** | 高 | 中 | 高 |
| **数据共享** | 支持 | 支持 | 不支持 |
| **推荐程度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ |

## 4. 最终建议

### 4.1 短期方案（当前）

使用**方案三**，Skill 自己实现存储服务，确保功能可用。

### 4.2 长期方案（推荐）

采用**方案一**，SE 通过 Spring Bean 暴露服务：

1. **SE 2.3.2 版本** 添加服务 Bean 配置
2. **Skill** 通过 `@Autowired` 注入服务
3. **统一管理** 所有 Skill 使用相同的服务实现

### 4.3 实施步骤

| 阶段 | 内容 | 负责方 |
|------|------|--------|
| Phase 1 | Skill 使用自己的存储实现 | Skill 团队 |
| Phase 2 | SE 添加服务 Bean 配置 | SE 团队 |
| Phase 3 | Skill 切换到 SE 服务 | Skill 团队 |
| Phase 4 | 移除 Skill 中的重复实现 | Skill 团队 |

## 5. 期望 SE 提供的服务

### 5.1 核心服务

| 服务接口 | 说明 | 优先级 |
|----------|------|--------|
| `ContextStorageService` | 上下文存储服务 | 高 |
| `ConversationService` | 对话服务 | 高 |
| `KnowledgeBaseService` | 知识库服务 | 中 |
| `LlmService` | LLM 服务 | 高 |
| `ToolOrchestrator` | 工具编排服务 | 中 |

### 5.2 期望的 Bean 配置

```java
@Configuration
public class SceneEngineAutoConfiguration {

    // 存储服务
    @Bean
    @ConditionalOnMissingBean
    public ContextStorageService contextStorageService() {
        return new JsonStorageService();
    }
    
    // 对话服务
    @Bean
    @ConditionalOnMissingBean
    public ConversationService conversationService(
            LlmService llmService,
            ContextStorageService storageService) {
        return new ConversationServiceImpl(llmService, storageService, null, null);
    }
    
    // 知识库服务
    @Bean
    @ConditionalOnMissingBean
    public KnowledgeBaseService knowledgeBaseService() {
        return new KnowledgeBaseServiceImpl();
    }
}
```

## 6. 总结

当前 SE 的服务实现类没有暴露给 Skill 插件，导致 Skill 无法使用 SE 提供的核心功能。建议 SE 团队：

1. **添加服务 Bean 配置** - 将核心服务通过 Spring Bean 暴露
2. **提供 SPI 接口** - 作为备选方案，提供静态访问入口
3. **统一服务管理** - 确保所有 Skill 使用相同的服务实现

这样可以：
- 减少 Skill 的重复实现
- 提高代码的可维护性
- 实现数据共享和统一管理
