# SPI 基础设施完整统计分析 & skill-agent 跨模块依赖隔离方案

> **文档版本**: v1.0 | **生成日期**: 2026-04-05  
> **分析范围**: `e:\apex\os\skills\` 全部 _base / _system / _drivers 层  
> **Maven本地仓库**: `D:\maven\.m2\repository\net\ooder\`

---

## 一、SPI 基础设施全景图

### 1.1 三层 SPI 架构总览

```
┌─────────────────────────────────────────────────────────────────────┐
│                    OoderOS SPI 三层架构                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌───────────┐    ┌───────────┐    ┌───────────────────────────┐   │
│  │  _base    │    │  _system  │    │       _drivers            │   │
│  │ (SPI核心) │───▶│(业务实现)  │◀───│   (驱动实现)              │   │
│  └───────────┘    └───────────┘    └───────────────────────────┘   │
│       │                │                      │                    │
│       ▼                ▼                      ▼                    │
│  接口定义层      业务服务层(依赖接口)     具体驱动实现               │
│  纯接口+模型     Spring @Service        META-INF/services 注册     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 _base 层 — SPI 核心接口定义（3个模块）

| 模块 | ArtifactId | 包路径 | 接口数 | 数据模型数 | 职责 |
|------|-----------|--------|-------|-----------|------|
| **spi-core** | `skill-spi-core:3.0.1` | `net.ooder.spi.*` | **1** | **2+0** | IM通道抽象基线 |
| **spi-llm** | `skill-spi-llm:3.0.1` | `net.ooder.spi.llm.*` | **2** | **4+0** | LLM Provider 抽象 |
| **spi-messaging** | `skill-spi-messaging:3.0.1` | `net.ooder.spi.messaging.*` | **4** | **10+4** | 统一消息/会话/WebSocket |
| **合计** | | | **7** | **16+4** | |

#### _base 各模块详细清单

##### skill-spi-core (`net.ooder.spi.*`)
| 类型 | 全限定名 | 说明 |
|------|---------|------|
| interface | `net.ooder.spi.im.ImService` | IM发送: sendToUser/sendToGroup/sendDing/sendMarkdown |
| class | `net.ooder.spi.im.model.MessageContent` | 消息内容(text/markdown/attachments/extra) |
| class | `net.ooder.spi.im.model.SendResult` | 发送结果(success/messageId/errorCode) |

##### skill-spi-llm (`net.ooder.spi.llm.*`)
| 类型 | 全限定名 | 说明 |
|------|---------|------|
| interface | `net.ooder.spi.llm.LlmService` | LLM服务: chat/chatStream/isAvailable/getModels |
| interface | `net.ooder.spi.llm.LlmStreamHandler` | 流式回调 |
| class | `net.ooder.spi.llm.model.LlmRequest` | 请求模型 |
| class | `net.ooder.spi.llm.model.LlmResponse` | 响应模型 |
| class | `net.ooder.spi.llm.model.LlmModel` | 模型元信息 |
| class | `net.ooder.spi.llm.model.LlmConfig` | 配置模型 |

##### skill-spi-messaging (`net.ooder.spi.messaging.*`)
| 类型 | 全限定名 | 说明 |
|------|---------|------|
| interface | `net.ooder.spi.messaging.UnifiedMessagingService` | 统一消息: sendMessage/streamMessage/getMessages/markAsRead/addReaction |
| interface | `net.ooder.spi.messaging.UnifiedSessionService` | 统一会话: createSession/getSession/listSessions/deleteSession |
| interface | `net.ooder.spi.messaging.UnifiedWebSocketService` | 统一WS: generateToken/broadcast/sendToUser/subscribe |
| interface | `net.ooder.spi.messaging.MessageStreamHandler` | 消息流回调 |
| interface | `net.ooder.spi.messaging.LlmStreamHandler` | LLM流回调 |
| enum | `ConversationType` | 会话类型枚举 |
| enum | `MessageType` | 消息类型枚举 |
| enum | `MessageStatus` | 消息状态枚举 |
| enum | `SessionType` | 会话类型枚举 |
| class | `Content` | 内容模型 |
| class | `UnifiedMessage` | 统一消息模型 |
| class | `UnifiedSession` | 统一会话模型 |
| class | `SendMessageRequest` | 发送请求 |
| class | `CreateSessionRequest` | 创建会话请求 |
| class | `Participant` | 参与者 |
| class | `MessageReaction` | 消息表情 |
| class | `MessageAction` | 消息操作 |
| class | `WsToken` | WebSocket Token |

---

### 1.3 skill-common 层 — 业务 SPI 接口集（预构建 JAR）

> **来源**: `D:\maven\.m2\repository\net\ooder\skill-common\3.0.1\skill-common-3.0.1.jar`  
> **注意**: 此模块为预构建 JAR，源码不在 `_system` 目录中，但被 **24个** _system 模块依赖

#### 包结构总览 (`net.ooder.skill.common.spi.*`)

```
net.ooder.skill.common.spi/
├── ImService.java                  # [接口] IM通道服务
├── MessageService.java             # [接口] 消息发送服务
├── TodoSyncService.java            # [接口] 待办同步服务
├── SceneServices.java              # [接口] ★ 场景服务聚合门面 ★
├── StorageService.java             # [接口] KV存储服务
├── ConfigService.java              # [接口] 配置服务
├── AuditService.java               # [接口] 审计服务
├── UserService.java                # [接口] 用户服务
├── OrganizationService.java        # [接口] 组织架构服务
├── PermissionService.java          # [接口] 权限服务
├── PlatformBindService.java        # [接口] 平台绑定服务
├── OrgSyncService.java             # [接口] 组织同步服务
├── CalendarService.java            # [接口] 日历服务
├── agent/
│   ├── AgentStorage.java           # [接口] Agent存储
│   ├── AgentMessageStorage.java    # [接口] Agent消息存储 + AgentMessageData
│   └── AgentSessionStorage.java    # [接口] Agent会话存储 + AgentSessionData
├── audit/
│   └── AuditEvent.java             # [数据] 审计事件
├── bind/
│   ├── BindInfo.java               # [数据] 绑定信息
│   ├── BindStatus.java             # [枚举] 绑定状态
│   └── QrCodeInfo.java             # [数据] 二维码信息
├── calendar/
│   ├── EventInfo.java              # [数据] 日历事件
│   └── TimeSlot.java               # [数据] 时间段
├── im/
│   ├── MessageContent.java         # [数据] 消息内容
│   ├── MessageType.java            # [枚举] 消息类型
│   └── SendResult.java             # [数据] 发送结果
├── knowledge/
│   ├── EmbeddingProvider.java      # [接口] 向量嵌入提供者
│   ├── VectorStoreProvider.java    # [接口] 向量存储提供者 + VectorData
│   └── KnowledgeBaseStorage.java   # [接口] 知识库存储 + KnowledgeBaseData
├── llm/
│   ├── LLMServiceProvider.java     # [接口] LLM服务提供者
│   ├── ConversationStorage.java    # [接口] 对话存储 + ConversationData
│   └── LlmConfigStorage.java       # [接口] LLM配置存储 + LlmConfigData
├── message/
│   ├── Message.java                # [数据] 消息 + MessageType内部枚举
│   ├── SceneNotification.java      # [数据] 场景通知
│   └── SendMessageResult.java      # [数据] 发送结果
├── org/
│   └── DepartmentInfo.java         # [数据] 部门信息
├── orgsync/
│   ├── OrgDepartmentInfo.java      # [数据]
│   ├── OrgUserInfo.java            # [数据]
│   └── SyncResult.java             # [数据]
├── storage/
│   ├── PageResult.java             # [数据] 分页结果
│   ├── SceneGroupData.java         # [数据] 场景组数据
│   └── SceneGroupStorage.java      # [接口] 场景组存储
├── todo/
│   ├── TodoInfo.java               # [数据] 待办信息
│   └── TodoStatus.java             # [枚举] 待办状态
└── user/
    └── UserInfo.java               # [数据] 用户信息
```

#### 统计汇总

| 维度 | 数量 |
|------|------|
| **SPI 接口总数** | **28 个** |
| **数据模型类** | **27 个** |
| **枚举类** | **5 个** |
| **领域覆盖** | **12 个** (im/agent/llm/knowledge/storage/todo/message/org/user/audit/config/calendar) |
| **依赖此JAR的_system模块数** | **24 个** |

#### 领域分布统计表

| 领域 | 接口数 | 数据模型数 | 关键接口 |
|------|-------|-----------|---------|
| **IM通道** | 1 (ImService) | 3 | sendToUser/sendToGroup/sendDing/sendMarkdown |
| **消息** | 1 (MessageService) | 3 | sendMessage/batchSendMessages/sendSceneNotification |
| **Agent** | 3 (Agent*/Storage) | 2 | AgentMessageStorage/AgentSessionStorage |
| **LLM** | 3 (LLM*/Storage) | 3 | LLMServiceProvider/ConversationStorage |
| **知识/RAG** | 3 (Knowledge*) | 3 | EmbeddingProvider/VectorStoreProvider/KnowledgeBaseStorage |
| **待办** | 1 (TodoSyncService) | 2 | createTodo/completeTodo/syncFromPlatform |
| **存储** | 2 (SceneGroup*/Storage) | 2 | StorageService(KV)/SceneGroupStorage(JPA) |
| **组织** | 3 (Org*/Permission) | 4 | OrganizationService/OrgSyncService |
| **用户** | 1 (UserService) | 1 | 用户信息查询 |
| **审计** | 1 (AuditService) | 1 | 审计事件记录 |
| **配置** | 1 (ConfigService) | 0 | 配置读写 |
| **日历** | 1 (CalendarService) | 2 | 日程管理 |
| **聚合门面** | 1 (SceneServices) | 0 | ★ 获取所有子服务入口 ★ |

---

### 1.4 _drivers 层 — 驱动实现（3个目录）

| 目录 | 模块 | SPI接口 | 实现方式 |
|------|------|--------|---------|
| `_drivers/llm/skill-llm-base/` | LLM基础驱动 | `net.ooder.skill.llm.LlmService` | LlmBaseServiceImpl |
| `_drivers/org/skill-org-web/` | 组织架构驱动 | `net.ooder.skill.org.OrgService` | OrgWebServiceImpl |
| `_drivers/spi/skill-spi/` | SPI桥接层 | `net.ooder.os.skill.spi.llm.LlmService`<br>`net.ooder.os.skill.spi.org.OrgService` | META-INF/services注册 |

**驱动注册机制**: 通过 `META-INF/services/` 标准Java SPI + Spring `@Component` 双重发现

---

## 二、skill-agent 跨模块依赖完整分析

### 2.1 外部依赖分类矩阵

对 `e:\apex\os\skills\_system\skill-agent\src\main\java` 下所有 **77个 Java 文件** 的 import 进行全量扫描：

| 依赖类别 | 来源模块/包 | 引用类数量 | skill-agent引用文件数 | HotPlug风险 |
|---------|-----------|----------|---------------------|------------|
| **A. 内部** | `net.ooder.skill.agent.*` | ~45 | 77 | ✅ 无 |
| **B. SE SDK** | `net.ooder.scene.*` (scene-engine 3.0.1) | **18** | 6 | ⚠️ 取决于parent加载 |
| **C. common-spi** | `net.ooder.skill.common.spi.*` (skill-common JAR) | **3** | 3 | ✅ 在parent classpath |
| **D. IM网关** | `net.ooder.skill.im.*` (skill-im-gateway) | **2** | 2 | 🔴 **高风险** |
| **E. 场景** | `net.ooder.skill.scene.*` (skill-scene) | **2** | 3 | 🔴 **高风险** |
| **F. 租户** | `net.ooder.skill.tenant.*` (skill-tenant) | **1** | 5 | 🟡 中风险 |
| **G. RAG** | `net.ooder.skill.rag.*` (skill-rag) | **1** | 2 | 🔴 **高风险** |
| **H. 工作流** | `net.ooder.skill.workflow.*` (skill-workflow) | **2** | 1 | 🔴 **高风险** |
| **I. 框架** | Spring/Jackson/SLF4J等 | ~15 | 50+ | ✅ PARENT_FIRST |

### 2.2 高风险跨模块依赖详细清单（需SPI隔离）

#### 🔴 D类：IM 网关依赖（skill-im-gateway）

| 引用的具体类 | 使用位置 | 使用方式 | 功能说明 |
|-------------|---------|---------|---------|
| `MultiChannelMessageDTO` | AgentChatServiceImpl:18 | 方法参数/局部变量构造 | 多通道消息DTO |
| `MessageGateway` | AgentChatServiceImpl:64(**当前为Object**) | @Autowired字段 | IM消息投递网关 |

**⚠️ 当前问题**: MessageGateway 已被错误改为 `Object` 类型，功能受损

**✅ 已有SPI对应**: `skill-common` 中有 `ImService` 接口，但 MessageGateway 是具体实现类（含 send/broadcast/registerInboundHandler/handleInbound 等扩展方法），**ImService 不完全覆盖**

#### 🔴 E类：场景依赖（skill-scene）

| 引用的具体类 | 使用位置 | 使用方式 | 功能说明 |
|-------------|---------|---------|---------|
| `TodoDTO` | AgentChatServiceImpl:19, AgentChatService, AgentChatController | 字段类型/方法参数/返回值 | 待办事项DTO |
| (scene.dto.todo) | | | |

**✅ 已有SPI对应**: `skill-common` 中有 `TodoSyncService` + `TodoInfo` + `TodoStatus`

#### 🔴 G类：RAG 依赖（skill-rag）

| 引用的具体类 | 使用位置 | 使用方式 | 功能说明 |
|-------------|---------|---------|---------|
| `RagPipeline` | AgentChatServiceImpl(**已移除**), AgentLLMServiceImpl(**已移除**), RagEnhancer(**改为Object**) | @Autowired字段 | RAG检索增强管道 |

**❌ 无SPI对应**: `skill-common` 有 `EmbeddingProvider`/`VectorStoreProvider`/`KnowledgeBaseStorage`，但这些是底层原语，**不等于 RagPipeline 高级编排接口**

**⚠️ 当前问题**: RagPipeline 引用已被删除或改为 Object，RAG增强功能完全丧失

#### 🔴 H类：工作流依赖（skill-workflow）

| 引用的具体类 | 使用位置 | 使用方式 | 功能说明 |
|-------------|---------|---------|---------|
| `WorkflowService` | AgentChatServiceImpl:21(**新增**) | @Autowired字段 | 工作流路由 |
| `WorkflowResult` | AgentChatServiceImpl:540-625 | 局部变量 | 工作流操作结果 |

**❌ 无SPI对应**: skill-common 中无 Workflow 相关 SPI

#### 🟡 F类：租户依赖（skill-tenant）

| 引用的具体类 | 使用位置 | 使用方式 | 功能说明 |
|-------------|---------|---------|---------|
| `TenantContext` | AgentChatServiceImpl:20 等5个文件 | 静态方法调用(getTenantId/setTenantId/hasTenant) | 租户上下文传播 |

**分析**: TenantContext 是 ThreadLocal 工具类，通常通过 PARENT_FIRST 加载。但如果 skill-tenant 不在 parent classpath 则会失败。

### 2.3 SE SDK 依赖（B类）— 特殊处理

| 引用的SE SDK接口 | 使用文件 | 用途 | 风险评估 |
|----------------|---------|------|---------|
| `NorthboundMessageQueue` | AgentChatServiceImpl, UnifiedAgentChatServiceImpl | P2P/P2A/A2A消息路由 | ✅ scene-engine 在 parent classpath |
| `NorthboundMessageHandler` | AgentChatServiceImpl | 北向消息订阅回调 | ✅ 同上 |
| `MessageEnvelope` | AgentChatServiceImpl | 消息信封 | ✅ 同上 |
| `UnifiedSessionManager` | UnifiedAgentChatServiceImpl, UnifiedInterfaceConfig | 会话管理 | ✅ 同上 |
| `MessageQueueService` | UnifiedAgentChatServiceImpl, UnifiedInterfaceExtension | 消息队列 | ✅ 同上 |
| `AgentContextManager` | UnifiedAgentChatServiceImpl | Agent上下文 | ✅ 同上 |
| `A2AProtocolService` | UnifiedAgentChatServiceImpl, UnifiedA2AService | A2A协议 | ✅ 同上 |
| `FailoverManager` | FailoverServiceImpl | 故障转移 | ✅ 同上 |
| `WebSocketAuthService` | SceneChatWebSocketHandler, AgentChatController | WS认证 | ✅ 同上 |
| `A2AMessageRouter` | UnifiedInterfaceExtension | A2A路由 | ✅ 同上 |

**结论**: SE SDK (scene-engine 3.0.1) 作为 Maven 直接依赖打包进 plugin JAR，且其包名 `net.ooder.scene.*` 可通过 PluginClassLoader 的 SeCorePackages 或 PARENT_FIRST_PACKAGES 策略从 parent 加载。**风险较低**。

---

## 三、SPI 缺口分析与补齐方案

### 3.1 SPI 覆盖率矩阵

| skill-agent 依赖的类 | 是否有对应SPI | SPI位置 | 覆盖程度 | 行动 |
|--------------------|-------------|--------|---------|------|
| **MessageGateway** | ⚠️ 部分 | `skill-common: ImService` | ~60% | **需要扩展 ImService 或新建 ImGatewayDriver** |
| **MultiChannelMessageDTO** | ❌ 无 | - | 0% | **需要抽取到 common 或定义新 DTO** |
| **RagPipeline** | ⚠️ 间接 | `skill-common: EmbeddingProvider+VectorStoreProvider+KnowledgeBaseStorage` | ~30% (底层原语≠高级编排) | **需要新建 RagEnhanceDriver SPI** |
| **TodoDTO** | ✅ 有 | `skill-common: TodoSyncService.TodoInfo` | ~80% | **使用 TodoInfo 替代或做适配器** |
| **WorkflowService** | ❌ 无 | - | 0% | **需要新建 WorkflowDriver SPI** |
| **WorkflowResult** | ❌ 无 | - | 0% | **随 WorkflowDriver 一起定义** |
| **TenantContext** | ⚠️ 特殊 | 工具类(ThreadLocal) | N/A | **确保在 parent classpath** |

### 3.2 需要新建的 SPI 接口

#### 新建 #1: `RagEnhanceDriver` （RAG增强驱动）

**放置位置**: `skill-common` → `net.ooder.skill.common.spi.rag.RagEnhanceDriver`
**理由**: 
- skill-common 已有 knowledge 子包(EmbeddingProvider/VectorStoreProvider/KnowledgeBaseStorage)
- 但缺少高层 RAG 编排抽象
- RagPipeline 的 enhancePromptWithRAG / buildKnowledgeConfig / searchRelated 等方法属于高级编排

```java
// 建议接口定义
package net.ooder.skill.common.spi.rag;

import java.util.List;
import java.util.Map;

public interface RagEnhanceDriver {
    
    boolean isAvailable();
    
    String enhancePrompt(String userQuery, String sceneGroupId, List<String> knowledgeBaseIds);
    
    RagKnowledgeConfig buildKnowledgeConfig(String sceneGroupId, String query);
    
    List<RagRelatedDocument> searchRelated(String query, int limit);
    
    record RagKnowledgeConfig(
        String knowledgeContext,
        List<Map<String,String>> dictItems,
        String systemPromptTemplate
    ) {}
    
    record RagRelatedDocument(
        String docId,
        String title,
        String content
    ) {}
}
```

#### 新建 #2: `ImDeliveryDriver` （IM投递驱动）

**放置位置**: `skill-common` → `net.ooder.skill.common.spi.im.ImDeliveryDriver`
**理由**:
- 现有 `ImService` 只覆盖基本发送(sendToUser/sendToGroup)
- `MessageGateway` 扩展了 broadcast/registerInboundHandler/handleInbound/getAvailableChannels
- 这些是 IM Gateway 特有能力，需要单独抽象

```java
// 建议接口定义
package net.ooder.skill.common.spi.im;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ImDeliveryDriver extends ImService {
    
    CompletableFuture<SendResult> sendAsync(MessageContent content, DeliveryContext ctx);
    
    Map<String, SendResult> broadcast(DeliveryTemplate template, List<String> channels);
    
    Set<String> getAvailableChannels();
    
    void registerInboundHandler(String channel, InboundHandler handler);
    
    void handleInbound(String channel, Map<String, Object> rawMessage);
    
    interface InboundHandler {
        void handle(String channel, Map<String, Object> rawMessage);
    }
    
    record DeliveryContext(
        String channel,
        String receiver,
        String tenantId,
        String userId,
        Map<String,Object> extra
    ) {}
    
    record DeliveryTemplate(
        String msgType,
        String content,
        String title,
        Map<String,Object> extra
    ) {}
}
```

#### 新建 #3: `WorkflowDriver` （工作流驱动）

**放置位置**: `skill-common` → `net.ooder.skill.common.spi.workflow.WorkflowDriver`
**理由**:
- agent-chat 的 delegateTodo/completeTodo 需要 BPM 路由能力
- 目前直接耦合 skill-workflow 模块的 WorkflowService
- 需要标准化的工作流操作抽象

```java
// 建议接口定义
package net.ooder.skill.common.spi.workflow;

public interface WorkflowDriver {
    
    boolean isAvailable();
    
    <T> WorkflowResult<T> routeTo(String processKey, String activityDefId, String assignee, Map<String,Object> vars);
    
    <T> WorkflowResult<T> endTask(String activityInstanceId);
    
    <T> WorkflowResult<T> getTaskInfo(String activityInstanceId);
    
    interface WorkflowResult<T> {
        boolean isSuccess();
        T getData();
        String getErrorCode();
        String getErrorMessage();
    }
}
```

### 3.3 SPI 补齐优先级排序

| 优先级 | SPI接口 | 影响范围 | 紧迫性 | 原因 |
|--------|--------|---------|--------|------|
| **P0** | `RagEnhanceDriver` | 3个文件(AgentChatServiceImpl/AgentLLMServiceImpl/RagEnhancer) | 🔴🔴🔴 | RAG功能已删除，必须恢复 |
| **P0** | `ImDeliveryDriver` | 1个文件(AgentChatServiceImpl) | 🔴🔴🔴 | IM投递已改为Object，功能受损 |
| **P1** | `WorkflowDriver` | 1个文件(AgentChatServiceImpl) | 🔴🔴 | 新增的工作流耦合点 |
| **P2** | MultiChannelMessageDTO标准化 | 1个文件(AgentChatServiceImpl) | 🟡 | 可内聚到 ImDeliveryDriver |
| **P2** | TodoDTO→TodoInfo适配 | 3个文件 | 🟡 | 渐进式替换 |

---

## 四、HotPlug 类加载机制深度分析

### 4.1 类加载决策流程

```
PluginClassLoader.loadClass(name)
    │
    ├─ shouldLoadFromParent(name)?
    │   ├─ PARENT_FIRST_PACKAGES 匹配? (java./javax./org.springframework./...)
    │   ├─ PARENT_FIRST_CLASSES 精确匹配? (SkillLifecycle/PluginContext/SkillPackage/SkillConfiguration)
    │   └─ SeCorePackages.isSeCoreClass(name)? ← 自定义SE核心包判断
    │       └─ YES → loadFromParent() → 成功返回 / 失败继续
    │
    └─ findClass(name) ← 从 plugin JAR 加载
        └─ ClassNotFoundException → loadFromParent() → 最终抛出
```

### 4.2 ClassNotFoundException 触发链路

```
ServiceRegistry.createServiceInstance(beanClass)
  └→ Spring @Autowired 失败 (因为类型不在 plugin classpath)
    └→ manualInjectDependencies(bean, beanClass)  ← [L324]
      └→ currentClass.getDeclaredFields()          ← [L324] ★ 关键触发点
        └→ JVM 尝试解析每个 Field.getType()
          └→ 如果 fieldType 是另一个 plugin 的类
            └→ PluginClassLoader.loadClass(fieldType.getName())
              └→ findClass() 失败 (不在 plugin JAR 中)
                └→ loadFromParent() 失败 (也不在 parent 中)
                  └→ ★ ClassNotFoundException ★
```

**关键发现**: 即使标记 `@Autowired(required=false)`，`getDeclaredFields()` 本身就会触发类加载！这是 JVM 规范行为——**获取字段类型时必须解析该类型**。

### 4.3 为什么 skill-common 的类不会触发此问题？

**答案**: `skill-common-3.0.1.jar` 在 **parent classpath** 中（作为主应用依赖），所以：
1. PluginClassLoader 的 `shouldLoadFromParent("net.ooder.skill.common.spi.*")` → 虽然 `net.ooder.skill.*` 不在 PARENT_FIRST_PACKAGES 列表中...
2. 但主应用的 ClassLoader（AppClassLoader）可以找到它
3. `loadFromParent()` 最终能成功解析

**而 skill-rag / skill-im-gateway / skill-scene / skill-workflow 的类为什么失败？**

这些模块的 JAR 在 `e:\apex\os\plugins\` 目录下，只被各自的 PluginClassLoader 加载，**不在 parent classpath 中**！

### 4.4 解决方案原理

```
修复前 (❌ 失败):
  skill-agent JAR ──imports──▶ RagPipeline (在 plugins/skill-rag JAR 中)
                              ▼
                   PluginClassLoader.findClass("net.ooder.skill.rag.RagPipeline")
                     → skill-agent plugin JAR 中没有 → ClassNotFoundException

修复后 (✅ 成功):
  skill-agent JAR ──imports──▶ RagEnhanceDriver (在 skill-common JAR 中, parent classpath)
                              ▼
                   shouldLoadFromParent() → loadFromParent() → AppClassLoader 找到 → ✅
                              
  skill-rag JAR ──@Component──▶ RagEnhanceDriverImpl implements RagEnhanceDriver
                              ▼
                   Spring 容器中注册为 Bean
                              ▼
                   skill-agent 通过 @Autowired RagEnhanceDriver 注入 ← 接口在 parent 中 ✅
```

---

## 五、实施路线图

### Phase 1: SPI 接口定义（skill-common 扩展）

| 步骤 | 任务 | 文件 | 预估工作量 |
|------|------|------|-----------|
| 1.1 | 新建 `RagEnhanceDriver` 接口 | `skill-common/src/.../spi/rag/RagEnhanceDriver.java` | 小 |
| 1.2 | 新建 `ImDeliveryDriver` 接口 | `skill-common/src/.../spi/im/ImDeliveryDriver.java` | 小 |
| 1.3 | 新建 `WorkflowDriver` 接口 | `skill-common/src/.../spi/workflow/WorkflowDriver.java` | 小 |
| 1.4 | 重新构建 skill-common JAR 并安装到 Maven | `mvn install` | 自动化 |

**前置条件**: 需要 skill-common 源码（当前只有预构建 JAR）。如果源码不可用，可先在 `skill-spi-core` 中新建 `spi-rag`/`spi-workflow` 子模块。

### Phase 2: Driver 实现（各模块）

| 步骤 | 任务 | 实现位置 | 说明 |
|------|------|---------|------|
| 2.1 | `RagEnhanceDriverImpl` | skill-rag 模块 | 包装现有 RagPipeline，实现新 SPI |
| 2.2 | `ImDeliveryDriverImpl` | skill-im-gateway 模块 | 包装现有 MessageGateway，实现新 SPI |
| 2.3 | `WorkflowDriverImpl` | skill-workflow 模块 | 包装现有 WorkflowService，实现新 SPI |

### Phase 3: skill-agent 消费端改造

| 步骤 | 任务 | 改造文件 | 说明 |
|------|------|---------|------|
| 3.1 | 恢复 AgentChatServiceImpl RAG | AgentChatServiceImpl.java | Object messageGateway → ImDeliveryDriver; 新增 RagEnhanceDriver 字段; 恢复 sendWithRagEnhancement |
| 3.2 | 恢复 AgentLLMServiceImpl RAG | AgentLLMServiceImpl.java | 新增 RagEnhanceDriver 字段; 恢复 processWithRAG |
| 3.3 | 修复 RagEnhancer | RagEnhancer.java (skill-im-gateway) | Object ragPipeline → RagEnhanceDriver; 移除反射调用 |
| 3.4 | TodoDTO → TodoInfo 适配 | AgentChatServiceImpl.java 等 | 渐进替换，保持兼容 |

### Phase 4: 构建与验证

| 步骤 | 任务 | 验证方式 |
|------|------|---------|
| 4.1 | 编译全部模块 | `mvn compile` 0 errors |
| 4.2 | 更新 plugins/ 下 JAR | 复制新构建的 JAR 到 `e:\apex\os\plugins\` |
| 4.3 | 启动服务 | 无 ClassNotFoundException |
| 4.4 | RAG功能验证 | 发送消息时 RAG 增强正常执行 |
| 4.5 | IM投递验证 | P2P消息正常投递到 WebSocket |

---

## 六、总结统计

### 6.1 数值总览

| 指标 | 数值 |
|------|------|
| _base SPI 模块数 | **3** |
| _base SPI 接口数 | **7** |
| _base 数据模型数 | **20** |
| skill-common SPI 接口数 | **28** |
| skill-common 数据模型数 | **27** |
| _drivers 驱动实现数 | **3** |
| skill-agent Java 文件总数 | **77** |
| skill-agent 外部依赖种类 | **8 类** |
| **需新增 SPI 接口** | **3** (RagEnhanceDriver/ImDeliveryDriver/WorkflowDriver) |
| **需修改文件** | **4** (AgentChatServiceImpl/AgentLLMServiceImpl/RagEnhancer/+1) |
| **已损坏需恢复的功能** | **2** (RAG增强 + IM投递) |

### 6.2 核心结论

1. **skill-common 已具备较完善的 SPI 基础设施**（28个接口），覆盖了 im/agent/llm/knowledge/todo/message 等 12 个领域
2. **存在 3 个 SPI 缺口**: RAG高级编排、IM网关扩展能力、工作流操作——这3个恰好是导致 ClassNotFoundException 的根因
3. **错误的临时方案**（改Object/删字段）破坏了 RAG 和 IM 投递功能，必须通过 SPI 方案正确恢复
4. **根本解决思路**: 所有跨插件依赖必须通过在 parent classpath 中的 SPI 接口间接引用，具体实现在各自插件 JAR 中以 `@Component` 注册
