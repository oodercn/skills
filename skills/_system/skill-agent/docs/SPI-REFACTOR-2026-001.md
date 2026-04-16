# SPI 包结构重构方案 — 统一三套命名空间

> **文档版本**: v1.0 | **日期**: 2026-04-05  
> **状态**: 方案设计 | **前置文档**: [SPI-ARCHITECTURE-2026-001.md](./SPI-ARCHITECTURE-2026-001.md)

---

## 一、现状问题诊断：三套 SPI 命名空间混乱

### 1.1 当前存在的三套 SPI 体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        当前 SPI 混乱全景                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  System A: _base 层              System B: skill-common      System C: _drivers │
│  ─────────────────              ──────────────────────      ─────────────── │
│  net.ooder.spi.*                 net.ooder.skill.common.spi.*  混合命名空间    │
│  (3个Maven模块)                  (预构建JAR, 28接口)          (3个驱动目录)     │
│                                                                             │
│  ⚠️ ImService × 2               ✅ ImService × 1            LlmService × 2   │
│     (方法签名不同!)                (与A冲突)                   (完全重复!)       │
│  ⚠️ MessageContent × 2           MessageService × 1         OrgService × 2   │
│     (字段不同!)                                              (完全重复!)       │
│  ⚠️ SendResult × 2               LLMServiceProvider × 1                       │
│     (字段不同!)                   TodoSyncService × 1                          │
│  LlmService × 1                  SceneServices(门面) × 1                     │
│  UnifiedMessagingService × 1     Agent*Storage × 3                            │
│  UnifiedSessionService × 1       Knowledge* × 3                              │
│  UnifiedWebSocketService × 1     ...共28个接口...                             │
│                                                                             │
│  ❓ 到底用哪套? → 运行时取决于 classpath 加载顺序 → 隐患!                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 精确冲突清单（已逐行代码比对）

#### 冲突 #1: `ImService` 接口 — 两定义，方法签名不一致

| 维度 | `_base` 版本 | `skill-common` 版本 | 差异 |
|------|-------------|-------------------|------|
| 包路径 | `net.ooder.spi.im.ImService` | `net.ooder.skill.common.spi.ImService` | **包名不同** |
| 方法数 | **7** 个 | **6** 个 | common 缺少 `getPlatformName()` |
| sendToUser | ✅ | ✅ | 相同 |
| sendToGroup | ✅ | ✅ | 相同 |
| sendDing | ✅ | ✅ | 相同 |
| sendMarkdown | ✅ | ✅ | 相同 |
| getAvailablePlatforms | ✅ | ✅ | 相同 |
| isPlatformAvailable | ✅ | ✅ | 相同 |
| **getPlatformName** | ✅ | ❌ **缺失** | **不兼容** |

#### 冲突 #2: `MessageContent` 类 — 两定义，字段结构不同

| 维度 | `_base` 版本 (`spi.im.model`) | `skill-common` 版本 (`common.spi.im`) |
|------|----------------------------|-------------------------------------|
| 包路径 | `net.ooder.spi.im.model.MessageContent` | `net.ooder.skill.common.spi.im.MessageContent` |
| 实现方式 | Lombok `@Data` | 手动 getter/setter |
| text 字段 | ✅ String | ❌ (改为 `content`) |
| markdown 字段 | ✅ String | ❌ 无 |
| **type 字段** | ❌ 无 | ✅ MessageType 枚举 |
| title 字段 | ✅ String | ✅ String |
| content 字段 | ❌ 无 | ✅ String |
| url 字段 | ❌ 无 | ✅ String |
| attachments 字段 | ✅ List<Attachment> | ❌ 无 |
| extra 字段 | ✅ Map<String,Object> | ❌ 无 |
| 静态工厂方法 | ❌ 无 | ✅ text()/markdown()/link() |

**结论**: 这是两个**完全不同的类**，但名字相同！

#### 冲突 #3: `SendResult` 类 — 两定义，错误信息字段不同

| 维度 | `_base` 版本 | `skill-common` 版本 |
|------|-------------|-------------------|
| success | ✅ boolean | ✅ boolean |
| messageId | ✅ String | ✅ String |
| errorCode | ✅ String | ❌ 无 |
| errorMessage | ✅ String | ❌ 无 |
| **error** | ❌ 无 | ✅ String (合并了code+message) |
| timestamp | ✅ Long | ✅ long |
| failure() 参数 | `(errorCode, errorMessage)` | `(error)` 单参数 |

#### 冲突 #4: `LlmService` 接口 — **三定义**，职责各不相同

| 维度 | `_base spi-llm` | `common LLMServiceProvider` | `_drivers llm-base` / `spi bridge` |
|------|-----------------|--------------------------|--------------------------------|
| 包路径 | `net.ooder.spi.llm.LlmService` | `net.ooder.skill.common.spi.llm.LLMServiceProvider` | `net.ooder.skill.llm.LlmService`<br>`net.ooder.os.skill.spi.llm.LlmService` |
| 职责定位 | **Provider能力描述** | **文本生成调用** | **配置/模型管理** |
| getProviderId/Name | ✅ | ❌ | ❌ |
| chat/chatStream | ✅ | ❌ | ❌ |
| generate/generateBatch | ❌ | ✅ | ❌ |
| getProviders/getModels | ❌ | ❌ | ✅ |
| getConfig/updateConfig | ❌ | ❌ | ✅ |
| getDefaultProvider/Model | ❌ | ❌ | ✅ |
| getSkillId | ❌ | ❌ | ✅ |
| isAvailable | ✅ | ✅ | ❌ |

**三个接口描述的是同一个领域（LLM）的不同切面，应该合并为一个或分层设计。**

#### 冲突 #5: `OrgService` 接口 — 完全重复

| 位置 | 包路径 | 方法数 |
|------|--------|-------|
| `_drivers/org/skill-org-web` | `net.ooder.skill.org.OrgService` | 10个方法 |
| `_drivers/spi/skill-spi` | `net.ooder.os.skill.spi.org.OrgService` | **10个方法, 完全一致** |

#### 冲突 #6: 消息模型四重奏

| 模型类 | 所在位置 | 用途 |
|--------|---------|------|
| `MessageContent` | `_base: net.ooder.spi.im.model.*` | IM消息内容(v1) |
| `MessageContent` | `common: net.ooder.skill.common.spi.im.*` | IM消息内容(v2) |
| `Content` | `_base spi-messaging: net.ooder.spi.messaging.model.*` | 统一消息内容(v3) |
| `Message` | `common: net.ooder.skill.common.spi.message.*` | 场景通知消息(v4) |
| `UnifiedMessage` | `_base spi-messaging: net.ooder.spi.messaging.model.*` | 统一消息DTO(v5) |

**5个不同的"消息"相关模型类，分布在4个不同包中。**

### 1.3 使用方困惑

当前代码中的实际引用情况：

```java
// AgentChatServiceImpl.java — 引用的是 common 版本!
import net.ooder.skill.common.spi.im.MessageContent;   // ← common版
import net.ooder.skill.common.spi.im.SendResult;        // ← common版

// MessageGateway.java (skill-im-gateway) — 也引用 common 版本
import net.ooder.skill.common.spi.ImService;             // ← common版
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.SendResult;

// 但 _base 的 spi-core 定义了一套完全不同的同名类!
// 如果两个 JAR 同时在 classpath 中 → 运行时行为不可预测
```

### 1.4 依赖关系图（当前混乱状态）

```
                    ┌─────────────────────┐
                    │   parent classpath  │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┬──────────────────┐
              ▼                ▼                ▼                  ▼
    ┌─────────────────┐ ┌───────────┐ ┌─────────────────┐ ┌──────────────┐
    │  skill-common   │ │ _base     │ │ scene-engine    │ │ Spring 等    │
    │  (预构建JAR)     │ │ spi-* ×3  │ │ 3.0.1           │ │ 框架         │
    │  28 interfaces  │ │ 7接口     │ │ SE SDK          │ │              │
    │  ★被24个模块依赖 │ │ ★仅3个    │ │ ★被skill-agent  │ │ PARENT_FIRST │
    └────────┬────────┘ └─────┬─────┘ └────────┬────────┘ └──────────────┘
             │                │               │
             │    ⚠️ 重叠!     │               │
             ├── ImService ───┤               │
             ├── MsgContent ──┤               │
             ├── SendResult ──┤               │
             └── LlmService? ─┘               │
                                          (无重叠)
                               
    ┌─────────────────┐ ┌─────────────────────────────────────┐
    │  plugins/ JARs  │ │         _drivers (驱动实现)           │
    │  (HotPlug加载)   │ │  ┌─────────┐ ┌──────────┐ ┌───────┐  │
    │                 │ │ │llm-base  │ │org-web   │ │spi桥接│  │
    │  skill-rag      │ │ │LlmService│ │OrgService│ │重复!  │  │
    │  skill-im-gateway│ │ └─────────┘ └──────────┘ └───────┘  │
    │  skill-scene     │ └─────────────────────────────────────┘
    │  skill-workflow  │
    │  skill-tenant    │
    └─────────────────┘
```

---

## 二、重构目标与原则

### 2.1 目标

1. **单一命名空间**: 所有 SPI 接口统一到 `net.ooder.spi.*` 一个根包下
2. **零语义重复**: 同一概念只有一个接口/模型定义
3. **领域清晰**: 按业务领域划分子包，不是按模块划分子包
4. **HotPlug 安全**: 所有 SPI 接口在单一 Maven 模块中，确保在 parent classpath
5. **渐进迁移**: 通过 `@Deprecated` + 适配器保证过渡期兼容

### 2.2 设计原则

| 原则 | 说明 |
|------|------|
| **O.C.P** | 对扩展开放（新 Driver），对修改关闭（不改接口） |
| **D.I.P** | 依赖倒置——依赖抽象，不依赖具体实现 |
| **I.S.P** | 接口隔离——细粒度接口，不用大而全 |
| **单一来源** | 每个概念只在一个地方定义 |

---

## 三、目标架构：统一 SPI 包结构

### 3.1 新包结构总览

```
net.ooder.spi/
│
├── core/                          # 🔵 核心基础类型（所有领域共享）
│   ├── PageResult.java            # 分页结果（原 common.spi.storage.PageResult）
│   ├── Result.java                # 通用结果（原 common.Result）
│   └── SpiConstants.java          # SPI 常量
│
├── im/                            # 🟢 IM 即时通讯通道
│   ├── ImService.java             # IM发送接口（统一 A+B 两版）
│   ├── ImDeliveryDriver.java      # IM投递扩展（★ 新增，原 MessageGateway 抽象）
│   ├── model/
│   │   ├── MessageContent.java    # 消息内容（统一两版差异）
│   │   ├── SendResult.java        # 发送结果（统一两版差异）
│   │   └── MessageType.java       # 消息类型枚举
│   └── handler/
│       └── InboundHandler.java    # 入站消息处理器接口
│
├── messaging/                     # 🟣 消息与会话（统一 _base spi-messaging + common message）
│   ├── MessagingService.java      # 消息服务（合并 UnifiedMessagingService + MessageService）
│   ├── SessionService.java        # 会话服务（合并 UnifiedSessionService）
│   ├── WebSocketService.java      # WebSocket 服务（合并 UnifiedWebSocketService）
│   └── model/
│       ├── Message.java           # 统一消息模型
│       ├── Session.java           # 统一会话模型
│       ├── SendMessageRequest.java
│       ├── CreateSessionRequest.java
│       ├── Participant.java
│       ├── Content.java
│       ├── WsToken.java
│       └── enum: ConversationType, MessageType, MessageStatus, SessionType
│
├── llm/                           # 🟡 大语言模型（合并三套 LlmService 为分层设计）
│   ├── LlmProvider.java           # Provider 能力描述（原 _base spi-llm.LlmService）
│   ├── LlmChatService.java        # Chat 调用层（原 common.LLMServiceProvider 的 generate 系列）
│   ├── LlmRegistryService.java    # 模型注册/发现（原 _drivers LlmService 的 getProviders/getModels）
│   └── model/
│       ├── LlmRequest.java
│       ├── LlmResponse.java
│       ├── LlmModel.java
│       ├── LlmConfig.java
│       ├── LlmProviderDTO.java
│       └── LlmModelDTO.java
│   └── stream/
│       └── LlmStreamHandler.java  # 流式回调
│
├── agent/                         # 🔴 Agent 智能体（保留 common.agent，微调）
│   ├── AgentStorage.java          # Agent 基础存储
│   ├── AgentMessageStorage.java   # Agent 消息存储 + AgentMessageData
│   └── AgentSessionStorage.java   # Agent 会话存储 + AgentSessionData
│
├── rag/                           # 🟠 RAG 检索增强（★ 全新，填补缺口）
│   ├── RagEnhanceDriver.java      # RAG 编排驱动（从 RagPipeline 抽象）
│   └── model/
│       ├── RagKnowledgeConfig.java
│       └── RagRelatedDocument.java
│
├── workflow/                      # 🟤 工作流（★ 全新，填补缺口）
│   └── WorkflowDriver.java        # 工作流操作驱动（从 WorkflowService 精简抽象）
│
├── knowledge/                     # 🔵 知识库（保留 common.knowledge）
│   ├── EmbeddingProvider.java     # 向量嵌入
│   ├── VectorStoreProvider.java   # 向量存储
│   └── KnowledgeBaseStorage.java  # 知识库 CRUD
│
├── todo/                          # 🟢 待办事项（保留 common.todo）
│   ├── TodoSyncService.java
│   └── model/
│       ├── TodoInfo.java
│       └── TodoStatus.java (enum)
│
├── storage/                       # 💜 存储服务（保留 common.storage）
│   ├── KvStorageService.java      # KV 存储（原 StorageService）
│   ├── SceneGroupStorage.java     # 场景组存储
│   └── model/
│       ├── SceneGroupData.java
│       └── PageResult.java → core/PageResult
│
├── org/                           # 🏢 组织架构（合并 _drivers + common.org）
│   └── OrganizationService.java   # 统一组织服务（合并 OrgService + OrganizationService）
│
├── user/                          # 👤 用户
│   └── UserService.java
│   └── model/UserInfo.java
│
├── audit/                         # 📋 审计
│   └── AuditService.java
│   └── model/AuditEvent.java
│
├── config/                        # ⚙️ 配置
│   └── ConfigService.java
│
├── calendar/                      # 📅 日历
│   └── CalendarService.java
│   └── model/(EventInfo, TimeSlot)
│
├── bind/                          # 🔗 平台绑定
│   └── PlatformBindService.java
│   └── model/(BindInfo, BindStatus, QrCodeInfo)
│
└── facade/                        # 🏛️ 聚合门面（原 SceneServices）
    └── SpiServices.java           # 统一服务获取入口
```

### 3.2 模块映射关系（旧→新）

| 旧位置 | 新位置 | 变更类型 | 说明 |
|--------|--------|---------|------|
| `net.ooder.spi.im.ImService` (_base) | `net.ooder.spi.im.ImService` | **保留+增强** | 合并 common 版的6方法 + 补齐 getPlatformName |
| `net.ooder.skill.common.spi.ImService` (common) | ~~删除~~ → `@Deprecated` 代理到新位置 | **废弃** | 代理到 `net.ooder.spi.im.ImService` |
| `net.ooder.spi.im.model.MessageContent` (_base) | `net.ooder.spi.im.model.MessageContent` | **重写字段** | 合并两版: type/text/markdown/content/url/attachments/extra |
| `net.ooder.skill.common.spi.im.MessageContent` (common) | ~~删除~~ → `@Deprecated` 代理 | **废弃** | |
| `net.ooder.spi.im.model.SendResult` (_base) | `net.ooder.spi.im.model.SendResult` | **重写字段** | 合并为: success/messageId/errorCode/errorMessage/timestamp |
| `net.ooder.skill.common.spi.im.SendResult` (common) | ~~删除~~ → `@Deprecated` 代理 | **废弃** | |
| `net.ooder.spi.llm.LlmService` (_base) | `net.ooder.spi.llm.LlmProvider` | **重命名** | 更准确反映 "Provider 描述" 定位 |
| `net.ooder.skill.common.spi.llm.LLMServiceProvider` (common) | `net.ooder.spi.llm.LlmChatService` | **重命名+增强** | 明确为 Chat 调用层 |
| `net.ooder.skill.llm.LlmService` (_drivers) | `net.ooder.spi.llm.LlmRegistryService` | **提取到SPI** | 从驱动层提升为标准接口 |
| `net.ooder.os.skill.spi.llm.LlmService` (_drivers spi) | ~~删除~~ | **冗余删除** | 与上面重复 |
| `net.ooder.spi.messaging.UnifiedMessagingService` | `net.ooder.spi.messaging.MessagingService` | **简化名** | 去掉 Unified 前缀 |
| `net.ooder.spi.messaging.UnifiedSessionService` | `net.ooder.spi.messaging.SessionService` | **简化名** | |
| `net.ooder.spi.messaging.UnifiedWebSocketService` | `net.ooder.spi.messaging.WebSocketService` | **简化名** | |
| `net.ooder.skill.common.spi.MessageService` (common) | 合并入 `MessagingService` | **合并** | batchSend/sendSceneNotification 并入 |
| `net.ooder.skill.common.spi.SceneServices` (common门面) | `net.ooder.spi.facade.SpiServices` | **移动+重命名** | 保持聚合门面模式 |
| `net.ooder.skill.org.OrgService` (_drivers) | `net.ooder.spi.org.OrganizationService` | **合并+重命名** | 与 common.OrganizationService 合并 |
| `net.ooder.os.skill.spi.org.OrgService` (_drivers spi) | ~~删除~~ | **冗余删除** | 完全重复 |
| `RagPipeline` (skill-rag 具体类) | `net.ooder.spi.rag.RagEnhanceDriver` | **新增SPI** | 抽象 RAG 高级编排能力 |
| `MessageGateway` (skill-im-gateway 具体类) | `net.ooder.spi.im.ImDeliveryDriver` | **新增SPI** | 抽象 IM 扩展投递能力 |
| `WorkflowService` (skill-workflow 具体类) | `net.ooder.spi.workflow.WorkflowDriver` | **新增SPI** | 抽象工作流操作能力 |
| 其余 common.spi.* 接口 | `net.ooder.spi.{domain}/*` | **平移** | 包路径缩短，去掉 `.common` 中间层 |

---

## 四、关键接口的统一设计方案

### 4.1 ImService 统一（解决冲突 #1）

```java
// 新位置: net.ooder.spi.im.ImService
// 合并 _base(7方法) + common(6方法) = 7方法 (取并集)
package net.ooder.spi.im;

public interface ImService {
    
    SendResult sendToUser(String platform, String userId, MessageContent content);
    
    SendResult sendToGroup(String platform, String groupId, MessageContent content);
    
    SendResult sendDing(String userId, String title, String content);
    
    SendResult sendMarkdown(String platform, String userId, String title, String markdown);
    
    List<String> getAvailablePlatforms();
    
    boolean isPlatformAvailable(String platform);
    
    String getPlatformName(String platform);        // ← 从 _base 补入
}
```

### 4.2 MessageContent 统一（解决冲突 #2）

```java
// 新位置: net.ooder.spi.im.model.MessageContent
// 合并两版所有字段 + 工厂方法
package net.ooder.spi.im.model;

public class MessageContent {
    
    private MessageType type;       // ← from common (更精确的类型区分)
    private String text;            // ← from _base (纯文本)
    private String markdown;        // ← from _base (Markdown)
    private String title;           // ← 共有
    private String content;         // ← from common (通用内容)
    private String url;             // ← from common (链接)
    private List<Attachment> attachments; // ← from _base
    private Map<String, Object> extra;     // ← from _base
    
    // 工厂方法 ← from common
    public static MessageContent text(String content) { ... }
    public static MessageContent markdown(String title, String content) { ... }
    public static MessageContent link(String title, String content, String url) { ... }
    
    // getters/setters ...
    
    public enum MessageType { TEXT, MARKDOWN, LINK, CARD, IMAGE, FILE, ACTION }
    
    @Data
    public static class Attachment {
        private String type;
        private String url;
        private String name;
        private Long size;
    }
}
```

### 4.3 LLM 三接口合一为分层设计（解决冲突 #4）

```
┌─────────────────────────────────────────────────┐
│                LLM 分层架构                      │
├─────────────────────────────────────────────────┤
│                                                 │
│  Layer 1: Registry (模型发现/配置)               │
│  ┌───────────────────────────────────────────┐  │
│  │ LlmRegistryService                        │  │
│  │  getProviders() / getModels() / getConfig()│  │
│  │  updateConfig() / getDefaultProvider()     │  │
│  │  ← 原: _drivers LlmService                │  │
│  └───────────────────────────────────────────┘  │
│           ↓ 发现 Provider 实例                  │
│  Layer 2: Provider (能力描述+Chat)             │
│  ┌───────────────────────────────────────────┐  │
│  │ LlmProvider (implements LlmChatService)   │  │
│  │  chat() / chatStream() / isAvailable()    │  │
│  │  getProviderId() / getName() / getModels()│  │
│  │  ← 原: _base spi-llm.LlmService          │  │
│  └───────────────────────────────────────────┘  │
│           ↓ 简化调用                           │
│  Layer 3: Convenience (便捷生成)               │
│  ┌───────────────────────────────────────────┐  │
│  │ LlmChatService                           │  │
│  │  generate() / generateWithSystem()       │  │
│  │  generateBatch() / getMaxTokens()         │  │
│  │  ← 原: common.LLMServiceProvider         │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 4.4 新增三大 SPI 接口

#### RagEnhanceDriver（RAG 增强）

```java
package net.ooder.spi.rag;

public interface RagEnhanceDriver {
    
    boolean isAvailable();
    
    String enhancePrompt(String query, String sceneGroupId, List<String> knowledgeBaseIds);
    
    RagKnowledgeConfig buildKnowledgeConfig(String sceneGroupId, String query);
    
    List<RagRelatedDocument> searchRelated(String query, int limit);
    
    record RagKnowledgeConfig(
        String knowledgeContext,
        List<Map<String, String>> dictItems,
        String systemPromptTemplate
    ) {}
    
    record RagRelatedDocument(String docId, String title, String content) {}
}
```

#### ImDeliveryDriver（IM 投递扩展）

```java
package net.ooder.spi.im;

public interface ImDeliveryDriver extends ImService {
    
    CompletableFuture<SendResult> sendAsync(MessageContent content, DeliveryContext ctx);
    
    Map<String, SendResult> broadcast(DeliveryTemplate template, List<String> channels);
    
    Set<String> getAvailableChannels();
    
    void registerInboundHandler(String channel, InboundHandler handler);
    
    void handleInbound(String channel, Map<String, Object> rawMessage);
    
    interface InboundHandler {
        void handle(String channel, Map<String, Object> rawMessage);
    }
    
    record DeliveryContext(String channel, String receiver, String tenantId,
                           String userId, Map<String, Object> extra) {}
    record DeliveryTemplate(String msgType, String content, String title,
                            Map<String, Object> extra) {}
}
```

#### WorkflowDriver（工作流操作）

```java
package net.ooder.spi.workflow;

public interface WorkflowDriver {
    
    boolean isAvailable();
    
    <T> WorkflowResult<T> routeTo(String activityInstId, String toUserId, Map<String, Object> vars);
    
    <T> WorkflowResult<T> endTask(String activityInstId);
    
    <T> WorkflowResult<T> getActivityInfo(String activityInstId);
    
    <T> WorkflowResult<T> startProcess(String processDefKey, Map<String, Object> vars);
    
    interface WorkflowResult<T> {
        boolean isSuccess();
        T getData();
        String getErrorCode();
        String getErrorMessage();
    }
}
```

---

## 五、Maven 模块重组方案

### 5.1 目标模块结构

```
skills/_base/
├── ooder-spi-core/              # 🔵 核心: 基础类型 + im + facade
│   ├── pom.xml (group: net.ooder, artifact: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/
│       ├── core/               # PageResult, Result
│       ├── im/                 # ImService, ImDeliveryDriver, model/*
│       ├── facade/             # SpiServices (聚合门面)
│       └── ...其他轻量 domain
│
├── ooder-spi-messaging/         # 🟣 消息: messaging 完整子包
│   ├── pom.xml (depends: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/messaging/
│
├── ooder-spi-llm/              # 🟡 LLM: llm 完整子包 (含分层3接口)
│   ├── pom.xml (depends: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/llm/
│
├── ooder-spi-agent/            # 🔴 Agent: agent 子包
│   ├── pom.xml (depends: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/agent/
│
├── ooder-spi-rag/              # 🟠 RAG: rag 子包 (★ 新模块)
│   ├── pom.xml (depends: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/rag/
│
├── ooder-spi-workflow/         # 🟤 workflow 子包 (★ 新模块)
│   ├── pom.xml (depends: ooder-spi-core)
│   └── src/main/java/net/ooder/spi/workflow/
│
└── ooder-spi-business/         # ⚪ 其余业务: knowledge/todo/storage/org/user/audit/config/calendar/bind
    ├── pom.xml (depends: ooder-spi-core)
    └── src/main/java/net/ooder/spi/
        ├── knowledge/
        ├── todo/
        ├── storage/
        ├── org/
        ├── user/
        ├── audit/
        ├── config/
        ├── calendar/
        └── bind/
```

### 5.2 模块依赖关系（重构后）

```
ooder-spi-core (零外部SPI依赖，纯接口)
    │
    ├──▶ ooder-spi-messaging (依赖 core)
    ├──▶ ooder-spi-llm (依赖 core)
    ├──▶ ooder-spi-agent (依赖 core)
    ├──▶ ooder-spi-rag (依赖 core)         ← ★ 新增
    ├──▶ ooder-spi-workflow (依赖 core)     ← ★ 新增
    └──▶ ooder-spi-business (依赖 core)
    
所有 _system 模块 → 按需依赖对应的 ooder-spi-* 模块
skill-common → 不再需要! (其28个接口全部迁移到 ooder-spi-* 各模块)
```

### 5.3 与现有模块的兼容策略

| 现有模块 | 重构前依赖 | 重构后依赖 | 迁移方式 |
|---------|-----------|-----------|---------|
| **skill-common** (JAR) | 被24个模块依赖 | **逐步废弃** | 内容拆分到 ooder-spi-* 各模块；保留 JAR 做 `@Deprecated` 代理 |
| **_base/skill-spi-core** | 被0个 _system 模块直接依赖(!) | 合并入 ooder-spi-core | 直接替换 |
| **_base/skill-spi-llm** | 被 skill-messaging, skill-llm-chat 依赖 | 替换为 ooder-spi-llm | artifactId 改名 |
| **_base/skill-spi-messaging** | 被 skill-messaging 依赖 | 替换为 ooder-spi-messaging | artifactId 改名 |
| **_system 各业务模块** | 依赖 skill-common + 部分 _base | 仅依赖 ooder-spi-* | import 路径更新 |

---

## 六、实施路线图

### Phase 0: 准备（1天）

| 步骤 | 任务 | 产出 |
|------|------|------|
| 0.1 | 创建 `ooder-spi-core` 模块骨架 | Maven 项目结构 |
| 0.2 | 创建其余 6 个 ooder-spi-* 模块骨架 | Maven 多模块项目 |
| 0.3 | 设定统一的 groupId=`net.ooder`, version=3.2.0 | 版本号规则 |

### Phase 1: 核心接口迁移（2-3天）

| 步骤 | 任务 | 详情 |
|------|------|------|
| 1.1 | 迁移 `core/` 基础类型 | PageResult, Result |
| 1.2 | **统一 ImService + MessageContent + SendResult** | 解决冲突 #1 #2 #3 |
| 1.3 | 迁移 `facade/SpiServices` 门面 | 原 SceneServices |
| 1.4 | 迁移 `im/ImDeliveryDriver` | 新增 SPI |
| 1.5 | 安装 ooder-spi-core 到本地 Maven | `mvn install` |

### Phase 2: 领域接口迁移（3-4天）

| 步骤 | 任务 | 详情 |
|------|------|------|
| 2.1 | 迁移 `llm/` 三层接口 | 解决冲突 #4 |
| 2.2 | 迁移 `messaging/` 合并消息模型 | 解决冲突 #6 |
| 2.3 | 迁移 `agent/` | 平移 |
| 2.4 | 新建 `rag/RagEnhanceDriver` | 新增 |
| 2.5 | 新建 `workflow/WorkflowDriver` | 新增 |
| 2.6 | 迁移 `business/` 其余10个领域 | 平移 |

### Phase 3: 废弃代理层（1-2天）

| 步骤 | 任务 | 详情 |
|------|------|------|
| 3.1 | 在 skill-common JAR 中创建 `@Deprecated` 代理类 | 每个旧接口委托到新位置 |
| 3.2 | 在 _base 旧模块中标记 `@Deprecated` | 代理到 ooder-spi-* |
| 3.3 | 发布 skill-common 3.1.1-deprecated | 仅含代理类 |

### Phase 4: 消费端切换（按模块逐步）

| 步骤 | 任务 | 影响范围 |
|------|------|---------|
| 4.1 | **skill-agent** 切换（最高优先级） | 修改 import + Object→SPI接口恢复功能 |
| 4.2 | skill-im-gateway 切换 | MessageGateway → ImDeliveryDriverImpl |
| 4.3 | skill-rag 切换 | RagPipeline → RagEnhanceDriverImpl |
| 4.4 | skill-workflow 切换 | WorkflowService → WorkflowDriverImpl |
| 4.5 | 其余 20 个 _system 模块逐步切换 | 按 dependency graph 拓扑序 |

### Phase 5: 清理（最后执行）

| 步骤 | 任务 |
|------|------|
| 5.1 | 删除 _base 下旧的 3 个 spi-* 模块 |
| 5.2 | 删除 _drivers/spi/skill-spi 冗余桥接模块 |
| 5.3 | skill-common JAR 标记为 `end of life` |
| 5.4 | 全量编译验证 `mvn clean compile` 0 errors |

---

## 七、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| skill-common 源码不可用（只有预构建JAR） | 无法直接修改 | 通过 `@Deprecated` 子类代理方式过渡；新建 ooder-spi-* 模块重新定义 |
| 24个消费模块同时切换工作量太大 | 改动面广 | Phase 3 代理层保证二进制兼容；分批切换 |
| HotPlug PluginClassLoader 的 SeCorePackages 需更新 | 新包路径不在白名单 | 将 `net.ooder.spi.*` 加入 PARENT_FIRST_PACKAGES |
| _drivers 层已有实现需要同步更新 | 驱动实现可能断裂 | 先改接口定义，再改实现；保持编译通过 |
| 第三方插件依赖旧包路径 | 外部插件失效 | 保留 deprecated 代理至少一个 major version |

---

## 八、新旧对比总结

| 维度 | 重构前 | 重构后 |
|------|--------|--------|
| **SPI 根包数量** | **3套** (`net.ooder.spi.*`, `net.ooder.skill.common.spi.*`, `net.ooder.skill.llm.*`等) | **1套** (`net.ooder.spi.*`) |
| **ImService 定义** | **2个** (方法签名不同) | **1个** (7方法完整版) |
| **MessageContent 定义** | **2个** (字段不同) | **1个** (合并全部字段) |
| **LlmService 定义** | **3个** (职责不同) | **3个分层接口** (Registry/Provider/Chat) |
| **OrgService 定义** | **2个** (完全重复) | **1个** |
| **消息模型类** | **5个** 不同类 | **2个** (Message + Content) |
| **RAG SPI** | ❌ 缺失 | ✅ RagEnhanceDriver |
| **Workflow SPI** | ❌ 缺失 | ✅ WorkflowDriver |
| **IM Gateway SPI** | ❌ 缺失 | ✅ ImDeliveryDriver |
| **Maven 模块数** | _base 3个 + skill-common 1个(黑盒) = 4个 | ooder-spi-* 共 7个 (全白盒) |
| **HotPlug 安全性** | ⚠️ 部分接口在 plugin JAR 中 | ✅ 全部在 parent classpath |
