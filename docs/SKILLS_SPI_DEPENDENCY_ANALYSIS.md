# Skills SPI 桥接依赖关系分析报告

## 分析概要

**分析时间**: 2026-04-13  
**分析范围**: skills 中所有的 SPI 桥接  
**分析目的**: 梳理 SPI 接口与实现的关联关系及依赖关系

---

## 一、SPI 架构概览

### 1.1 SPI 模块结构

```
skills/_base/
├── ooder-spi-core/        # 核心 SPI 接口（已废弃，使用 skill-spi-core）
├── skill-spi-core/        # 核心 SPI 接口定义
├── skill-spi-llm/         # LLM SPI 接口定义
└── skill-spi-messaging/   # 消息 SPI 接口定义
```

### 1.2 SPI 接口分类

| SPI 类型 | 接口名称 | 模块 | 功能描述 |
|---------|---------|------|---------|
| **IM SPI** | ImService | skill-spi-core | IM服务基础接口 |
| **IM SPI** | ImDeliveryDriver | skill-spi-core | IM投递驱动接口（扩展ImService） |
| **RAG SPI** | RagEnhanceDriver | skill-spi-core | RAG增强驱动接口 |
| **Vector SPI** | VectorStoreProvider | skill-spi-core | 向量存储提供者接口 |
| **Database SPI** | DataSourceProvider | skill-spi-core | 数据源提供者接口 |
| **Workflow SPI** | WorkflowDriver | skill-spi-core | 工作流驱动接口 |
| **LLM SPI** | LlmService | skill-spi-llm | LLM服务接口 |
| **Messaging SPI** | UnifiedMessagingService | skill-spi-messaging | 统一消息服务接口 |
| **Messaging SPI** | UnifiedSessionService | skill-spi-messaging | 统一会话服务接口 |
| **Messaging SPI** | UnifiedWebSocketService | skill-spi-messaging | 统一WebSocket服务接口 |

---

## 二、SPI 接口详细定义

### 2.1 IM SPI

#### ImService 接口

**位置**: `skills/_base/skill-spi-core/src/main/java/net/ooder/spi/im/ImService.java`

**核心方法**:
```java
public interface ImService {
    // 发送消息给用户
    SendResult sendToUser(String platform, String userId, MessageContent content);
    
    // 发送消息给群组
    SendResult sendToGroup(String platform, String groupId, MessageContent content);
    
    // 发送DING消息
    SendResult sendDing(String userId, String title, String content);
    
    // 发送Markdown消息
    SendResult sendMarkdown(String platform, String userId, String title, String markdown);
    
    // 获取可用平台列表
    List<String> getAvailablePlatforms();
    
    // 检查平台是否可用
    boolean isPlatformAvailable(String platform);
    
    // 获取平台名称
    String getPlatformName(String platform);
}
```

#### ImDeliveryDriver 接口

**位置**: `skills/_base/skill-spi-core/src/main/java/net/ooder/spi/im/ImDeliveryDriver.java`

**继承关系**: `extends ImService`

**扩展方法**:
```java
public interface ImDeliveryDriver extends ImService {
    // 异步发送消息
    CompletableFuture<SendResult> sendAsync(MessageContent content, DeliveryContext ctx);
    
    // 广播消息
    Map<String, SendResult> broadcast(DeliveryTemplate template, List<String> channels);
    
    // 获取可用通道
    Set<String> getAvailableChannels();
    
    // 注册入站处理器
    void registerInboundHandler(String channel, InboundHandler handler);
    
    // 处理入站消息
    void handleInbound(String channel, Map<String, Object> rawMessage);
}
```

### 2.2 RAG SPI

#### RagEnhanceDriver 接口

**位置**: `skills/_base/skill-spi-core/src/main/java/net/ooder/spi/rag/RagEnhanceDriver.java`

**核心方法**:
```java
public interface RagEnhanceDriver {
    // 检查是否可用
    boolean isAvailable();
    
    // 增强Prompt
    String enhancePrompt(String query, String sceneGroupId, List<String> knowledgeBaseIds);
    
    // 构建知识配置
    RagKnowledgeConfig buildKnowledgeConfig(String sceneGroupId, String query);
    
    // 搜索相关文档
    List<RagRelatedDocument> searchRelated(String query, int limit);
}
```

### 2.3 Vector SPI

#### VectorStoreProvider 接口

**位置**: `skills/_base/skill-spi-core/src/main/java/net/ooder/spi/vector/VectorStoreProvider.java`

**核心方法**:
```java
public interface VectorStoreProvider {
    // 获取提供者类型
    String getProviderType();
    
    // 获取提供者名称
    String getProviderName();
    
    // 初始化向量存储
    void initialize(VectorStoreConfig config);
    
    // 存储向量
    void store(String id, float[] vector, Map<String, Object> metadata);
    
    // 批量存储
    void batchStore(List<VectorData> vectors);
    
    // 相似度搜索
    List<SearchResult> search(float[] vector, int topK);
    
    // 带过滤条件的搜索
    List<SearchResult> search(float[] vector, int topK, Map<String, Object> filter);
    
    // 删除向量
    void delete(String id);
    
    // 批量删除
    void batchDelete(List<String> ids);
    
    // 获取向量
    VectorData get(String id);
    
    // 获取向量总数
    long count();
    
    // 清空所有向量
    void clear();
    
    // 关闭向量存储
    void close();
    
    // 检查是否健康
    boolean isHealthy();
}
```

### 2.4 LLM SPI

#### LlmService 接口

**位置**: `skills/_base/skill-spi-llm/src/main/java/net/ooder/spi/llm/LlmService.java`

**核心方法**:
```java
public interface LlmService {
    // 获取提供者ID
    String getProviderId();
    
    // 获取提供者名称
    String getProviderName();
    
    // 获取可用模型列表
    List<LlmModel> getAvailableModels();
    
    // 同步聊天
    LlmResponse chat(LlmRequest request);
    
    // 流式聊天
    void chatStream(LlmRequest request, LlmStreamHandler handler);
    
    // 检查是否可用
    boolean isAvailable();
    
    // 获取默认配置
    LlmConfig getDefaultConfig();
    
    // 获取最大Token数
    int getMaxTokens(String modelId);
    
    // 是否支持流式
    boolean supportsStreaming(String modelId);
}
```

### 2.5 Messaging SPI

#### UnifiedMessagingService 接口

**位置**: `skills/_base/skill-spi-messaging/src/main/java/net/ooder/spi/messaging/UnifiedMessagingService.java`

**核心方法**:
```java
public interface UnifiedMessagingService {
    // 发送消息
    UnifiedMessage sendMessage(SendMessageRequest request);
    
    // 流式发送消息
    void streamMessage(SendMessageRequest request, MessageStreamHandler handler);
    
    // 获取消息列表
    List<UnifiedMessage> getMessages(String conversationId, int limit, Long before, Long after);
    
    // 标记为已读
    void markAsRead(String conversationId, String userId, String messageId);
    
    // 添加反应
    void addReaction(String messageId, String userId, String emoji);
    
    // 移除反应
    void removeReaction(String messageId, String userId, String emoji);
    
    // 执行动作
    void executeAction(String messageId, String userId, String actionId, Map<String, Object> params);
}
```

---

## 三、SPI 实现清单

### 3.1 IM SPI 实现

#### 3.1.1 MessageGateway（消息网关）

**位置**: `skills/_system/skill-im-gateway/src/main/java/net/ooder/skill/im/gateway/MessageGateway.java`

**实现接口**: `ImDeliveryDriver`

**功能特点**:
- ✅ 作为消息网关，统一处理各种IM通道
- ✅ 支持钉钉、飞书、企业微信、MQTT等多种通道
- ✅ 通过依赖注入获取各个IM服务实现
- ✅ 支持异步消息发送和广播
- ✅ 支持入站消息处理
- ✅ 集成了审计日志功能

**依赖关系**:
```java
@Autowired(required = false)
private Map<String, ImService> imServices;  // 注入所有ImService实现
```

**支持的通道**:
- dingding（钉钉）
- feishu（飞书）
- wecom（企业微信）
- mqtt（MQTT）

#### 3.1.2 MqttChannelAdapter（MQTT通道适配器）

**位置**: `skills/_system/skill-im-gateway/src/main/java/net/ooder/skill/im/gateway/MqttChannelAdapter.java`

**实现接口**: `ImService`

**功能特点**:
- ✅ 提供MQTT协议的IM服务实现
- ✅ 支持租户隔离的消息主题
- ✅ 支持消息订阅和发布
- ✅ 条件激活（需要配置 `mqtt.enabled=true`）

**租户隔离主题格式**:
```
{topicPrefix}{tenantId}/{channelType}/{targetId}
例如: agent/chat/tenant123/direct/user456
```

#### 3.1.3 DingTalkImServiceImpl（钉钉IM服务）

**位置**: `skills/_drivers/im/skill-im-dingding/src/main/java/net/ooder/skill/im/dingding/spi/DingTalkImServiceImpl.java`

**实现接口**: `ImService`

**Bean名称**: `@Service("dingding")`

**功能特点**:
- ✅ 使用钉钉官方SDK
- ✅ 支持access_token自动刷新
- ✅ 支持多种消息类型（文本、Markdown、链接）
- ✅ 集成审计日志

#### 3.1.4 FeishuImServiceImpl（飞书IM服务）

**位置**: `skills/_drivers/im/skill-im-feishu/src/main/java/net/ooder/skill/im/feishu/spi/FeishuImServiceImpl.java`

**实现接口**: `ImService`

**Bean名称**: `@Service("feishu")`

**功能特点**:
- ✅ 使用飞书官方SDK
- ✅ 支持多种消息类型
- ✅ 集成审计日志

#### 3.1.5 WeComImServiceImpl（企业微信IM服务）

**位置**: `skills/_drivers/im/skill-im-wecom/src/main/java/net/ooder/skill/im/wecom/spi/WeComImServiceImpl.java`

**实现接口**: `ImService`

**Bean名称**: `@Service("wecom")`

**功能特点**:
- ✅ 使用企业微信SDK
- ✅ 支持多种消息类型
- ✅ 集成审计日志

### 3.2 Vector SPI 实现

#### LocalVectorStoreProvider（本地向量存储）

**位置**: `skills/_drivers/vector/skill-local-vector-store/src/main/java/net/ooder/skill/vector/local/LocalVectorStoreProvider.java`

**实现接口**: `VectorStoreProvider`

**功能特点**:
- ✅ 提供本地向量存储实现
- ✅ 支持向量存储和检索
- ✅ 支持相似度搜索

### 3.3 Database SPI 实现

#### SQLiteDataSourceProvider（SQLite数据源）

**位置**: `skills/_drivers/database/skill-sqlite-driver/src/main/java/net/ooder/skill/database/sqlite/SQLiteDataSourceProvider.java`

**实现接口**: `DataSourceProvider`

**功能特点**:
- ✅ 提供SQLite数据源
- ✅ 支持本地数据库

### 3.4 Messaging SPI 实现

#### UnifiedMessagingServiceImpl（统一消息服务）

**位置**: `skills/_system/skill-messaging/src/main/java/net/ooder/skill/messaging/service/impl/UnifiedMessagingServiceImpl.java`

**实现接口**: `UnifiedMessagingService`

**功能特点**:
- ✅ 提供统一的消息服务实现
- ✅ 支持消息发送、流式消息、消息查询
- ✅ 支持消息状态管理

---

## 四、SPI 依赖关系图

### 4.1 IM SPI 依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                      skill-agent                             │
│  (使用 ImDeliveryDriver 发送消息)                            │
└────────────────────┬────────────────────────────────────────┘
                     │ 依赖
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              MessageGateway (ImDeliveryDriver)               │
│  (消息网关，统一处理各种IM通道)                               │
│  @Autowired Map<String, ImService> imServices                │
└────────────────────┬────────────────────────────────────────┘
                     │ 依赖注入
        ┌────────────┼────────────┬────────────┐
        │            │            │            │
        ▼            ▼            ▼            ▼
   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
   │dingding │  │ feishu  │  │  wecom  │  │  mqtt   │
   │ImService│  │ImService│  │ImService│  │ImService│
   │(钉钉)   │  │(飞书)   │  │(企业微信)│  │(MQTT)   │
   └─────────┘  └─────────┘  └─────────┘  └─────────┘
        │            │            │            │
        ▼            ▼            ▼            ▼
   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
   │钉钉SDK  │  │飞书SDK  │  │企业微信SDK│ │MQTT库   │
   └─────────┘  └─────────┘  └─────────┘  └─────────┘
```

### 4.2 LLM SPI 依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                    skill-llm-chat                            │
│  (使用 LlmService 进行对话)                                  │
└────────────────────┬────────────────────────────────────────┘
                     │ 依赖
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  LlmService (SPI接口)                        │
│  (定义LLM服务标准接口)                                       │
└────────────────────┬────────────────────────────────────────┘
                     │ 实现
        ┌────────────┼────────────┬────────────┐
        │            │            │            │
        ▼            ▼            ▼            ▼
   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
   │ Baidu   │  │ OpenAI  │  │ Ollama  │  │DeepSeek │
   │LlmService│ │LlmService│ │LlmService│ │LlmService│
   │(百度千帆)│ │(OpenAI) │ │(本地)   │  │(DeepSeek)│
   └─────────┘  └─────────┘  └─────────┘  └─────────┘
```

### 4.3 Vector SPI 依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                    skill-knowledge                           │
│  (使用 VectorStoreProvider 进行知识检索)                     │
└────────────────────┬────────────────────────────────────────┘
                     │ 依赖
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              VectorStoreProvider (SPI接口)                   │
│  (定义向量存储标准接口)                                      │
└────────────────────┬────────────────────────────────────────┘
                     │ 实现
                     ▼
┌─────────────────────────────────────────────────────────────┐
│            LocalVectorStoreProvider                          │
│  (本地向量存储实现)                                          │
└─────────────────────────────────────────────────────────────┘
```

### 4.4 Messaging SPI 依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                    skill-messaging                           │
│  (提供统一消息服务)                                          │
└────────────────────┬────────────────────────────────────────┘
                     │ 实现
                     ▼
┌─────────────────────────────────────────────────────────────┐
│          UnifiedMessagingServiceImpl                         │
│  (统一消息服务实现)                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 五、SPI 模块依赖关系

### 5.1 Maven 依赖关系

#### skill-spi-core 依赖

**被以下模块依赖**:
- `skill-im-gateway` - IM网关
- `skill-local-vector-store` - 本地向量存储
- `skill-markdown-parser` - Markdown解析器
- `skill-sqlite-driver` - SQLite驱动
- `ooder-spi-core` - 核心SPI（自身）

**依赖版本**: 3.0.3

#### skill-spi-llm 依赖

**被以下模块依赖**:
- `skill-llm-baidu` - 百度千帆LLM
- `skill-llm-ollama` - Ollama LLM
- `skill-llm-openai` - OpenAI LLM
- `skill-llm-qianwen` - 通义千问LLM
- `skill-llm-volcengine` - 火山引擎LLM

#### skill-spi-messaging 依赖

**被以下模块依赖**:
- `skill-messaging` - 消息服务

### 5.2 版本依赖矩阵

| SPI模块 | 版本 | 主要实现模块 | 依赖模块数 |
|---------|------|-------------|-----------|
| **skill-spi-core** | 3.0.3 | MessageGateway, LocalVectorStore, SQLiteDriver | 5+ |
| **skill-spi-llm** | 3.0.3 | BaiduLlm, OpenAILlm, OllamaLlm, etc. | 5+ |
| **skill-spi-messaging** | 3.0.3 | UnifiedMessagingService | 1+ |

---

## 六、SPI 使用示例

### 6.1 IM SPI 使用示例

```java
@Service
public class AgentChatServiceImpl implements AgentChatService {
    
    @Autowired(required = false)
    private ImDeliveryDriver messageGateway;
    
    public void sendMessage(String platform, String userId, String content) {
        if (messageGateway != null) {
            MessageContent msg = MessageContent.text(content);
            SendResult result = messageGateway.sendToUser(platform, userId, msg);
            log.info("Message sent: {}", result.isSuccess());
        }
    }
}
```

### 6.2 LLM SPI 使用示例

```java
@Service
public class ChatService {
    
    @Autowired(required = false)
    private Map<String, LlmService> llmServices;
    
    public LlmResponse chat(String providerId, LlmRequest request) {
        LlmService llmService = llmServices.get(providerId);
        if (llmService != null && llmService.isAvailable()) {
            return llmService.chat(request);
        }
        throw new RuntimeException("LLM provider not available: " + providerId);
    }
}
```

### 6.3 Vector SPI 使用示例

```java
@Service
public class KnowledgeService {
    
    @Autowired(required = false)
    private VectorStoreProvider vectorStore;
    
    public List<SearchResult> searchSimilar(float[] queryVector, int topK) {
        if (vectorStore != null) {
            return vectorStore.search(queryVector, topK);
        }
        return Collections.emptyList();
    }
}
```

---

## 七、SPI 扩展指南

### 7.1 新增 IM Provider

**步骤**:
1. 创建新的 skill 模块
2. 添加 `skill-spi-core` 依赖
3. 实现 `ImService` 接口
4. 使用 `@Service("platformName")` 注解
5. 添加 Spring Boot 自动配置

**示例**:
```java
@Service("newplatform")
public class NewPlatformImServiceImpl implements ImService {
    // 实现所有接口方法
}
```

### 7.2 新增 LLM Provider

**步骤**:
1. 创建新的 skill 模块
2. 添加 `skill-spi-llm` 依赖
3. 实现 `LlmService` 接口
4. 使用 `@Service("providerId")` 注解
5. 添加 Spring Boot 自动配置

### 7.3 新增 Vector Provider

**步骤**:
1. 创建新的 skill 模块
2. 添加 `skill-spi-core` 依赖
3. 实现 `VectorStoreProvider` 接口
4. 使用 `@Service("providerType")` 注解
5. 添加 Spring Boot 自动配置

---

## 八、SPI 最佳实践

### 8.1 依赖注入

**推荐方式**:
```java
@Autowired(required = false)
private Map<String, ImService> imServices;  // 自动注入所有实现
```

**优点**:
- 自动发现所有实现
- 支持多实现共存
- 易于扩展

### 8.2 条件激活

**推荐方式**:
```java
@ConditionalOnProperty(name = "platform.enabled", havingValue = "true", matchIfMissing = true)
@Service("platform")
public class PlatformServiceImpl implements SomeService {
    // 实现
}
```

**优点**:
- 按需激活
- 避免不必要的Bean创建
- 支持配置控制

### 8.3 审计日志

**推荐方式**:
```java
@Auditable(action = "platform_action", resourceType = "Resource", logParams = true)
public SendResult sendMessage(...) {
    // 实现
}
```

**优点**:
- 自动记录操作日志
- 支持审计追踪
- 便于问题排查

---

## 九、SPI 统计总结

### 9.1 接口统计

| SPI类型 | 接口数量 | 核心接口 | 扩展接口 |
|---------|---------|---------|---------|
| IM SPI | 2 | ImService | ImDeliveryDriver |
| RAG SPI | 1 | RagEnhanceDriver | - |
| Vector SPI | 1 | VectorStoreProvider | - |
| Database SPI | 1 | DataSourceProvider | - |
| Workflow SPI | 1 | WorkflowDriver | - |
| LLM SPI | 1 | LlmService | - |
| Messaging SPI | 3 | UnifiedMessagingService | UnifiedSessionService, UnifiedWebSocketService |
| **总计** | **10** | **7** | **3** |

### 9.2 实现统计

| SPI类型 | 实现数量 | 主要实现 |
|---------|---------|---------|
| IM SPI | 5 | MessageGateway, DingTalk, Feishu, WeCom, MQTT |
| Vector SPI | 1 | LocalVectorStore |
| Database SPI | 1 | SQLite |
| Messaging SPI | 1 | UnifiedMessagingService |
| **总计** | **8** | - |

### 9.3 模块依赖统计

| SPI模块 | 被依赖次数 | 主要依赖模块 |
|---------|-----------|-------------|
| skill-spi-core | 5+ | IM, Vector, Database, Markdown |
| skill-spi-llm | 5+ | Baidu, OpenAI, Ollama, Qianwen, Volcengine |
| skill-spi-messaging | 1+ | Messaging |

---

## 十、总结与建议

### 10.1 架构优势

✅ **松耦合设计**: 通过SPI接口实现松耦合  
✅ **易于扩展**: 可以轻松添加新的实现  
✅ **自动发现**: 通过Spring依赖注入自动发现实现  
✅ **条件激活**: 支持按需激活实现  
✅ **统一标准**: 提供统一的服务接口标准  

### 10.2 改进建议

1. **完善RAG SPI实现**: 当前缺少RagEnhanceDriver的实现
2. **完善Workflow SPI实现**: 当前缺少WorkflowDriver的实现
3. **统一版本管理**: 建议使用父POM统一管理SPI版本
4. **增加文档**: 为每个SPI接口添加详细的使用文档
5. **增加测试**: 为每个SPI实现添加单元测试

### 10.3 后续工作

1. ✅ 更新所有依赖到最新版本
2. ⏳ 实现缺失的SPI接口
3. ⏳ 完善SPI文档
4. ⏳ 添加SPI测试用例
5. ⏳ 优化SPI性能

---

**生成时间**: 2026-04-13  
**分析工具**: Ooder Skills SPI Analyzer
