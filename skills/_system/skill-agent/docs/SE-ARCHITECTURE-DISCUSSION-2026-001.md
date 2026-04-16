# SE 架构边界讨论方案 — AgentChat 集成问题

> **文档版本**: v1.0 | **日期**: 2026-04-05  
> **状态**: 待讨论 | **前置**: SPI-REFACTOR-2026-001.md

---

## 一、当前问题总结

### 1.1 核心矛盾

```
┌─────────────────────────────────────────────────────────────────────┐
│                    当前架构边界模糊                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  skill-agent (Web API 层)                                           │
│       │                                                             │
│       ├── 直接依赖 scene-engine 的 NorthboundMessageQueue           │
│       │    └── MessageEnvelope 需要 MessageParticipant 类型         │
│       │        └── 但 SE SDK 3.0.1 中没有 MessageParticipant 类!    │
│       │                                                             │
│       ├── 直接依赖 scene-engine 的 MessageEnvelope                  │
│       │    └── setFrom()/setTo() 需要 MessageParticipant 参数       │
│       │        └── 类型不匹配: String ≠ MessageParticipant          │
│       │                                                             │
│       └── 期望 SE 提供: 会话管理、消息路由、WebSocket 认证等         │
│            └── 但 SE 定位是"场景引擎"，不是"消息中间件"              │
│                                                                     │
│  ❓ 问题: SE 的边界在哪里？Web API 层应该依赖什么？                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 具体编译错误清单

| # | 错误位置 | 错误描述 | 根因 |
|---|---------|---------|------|
| 1 | `MessageEnvelope.setFrom()` | `String` 无法转换为 `MessageParticipant` | SE SDK 类型定义与使用方预期不匹配 |
| 2 | `MessageEnvelope.setTo()` | 同上 | 同上 |
| 3 | `NorthboundMessageQueue.sendToUser()` | payload 类型不匹配 | SE 接口签名与调用方不一致 |
| 4 | `ChatMessage.priority` | `int` vs `String` 类型冲突 | 实体字段类型与 DTO 不一致 |
| 5 | `Todo` 实体字段名 | `assignee`/`creator` vs `assigneeId`/`creatorId` | 实体与 DTO 字段映射不一致 |
| 6 | `PageResult.total` | `long` vs `int` 可能损失精度 | ooder-spi-core 定义与接口不匹配 |

### 1.3 依赖关系现状

```
skill-agent (Web API)
    │
    ├── net.ooder.spi.* (ooder-spi-core) ───── ✅ 正确: SPI 接口层
    │
    ├── net.ooder.scene.* (scene-engine) ───── ⚠️ 争议: SE 是否应该暴露给 Web API?
    │       ├── NorthboundMessageQueue
    │       ├── MessageEnvelope
    │       ├── MessageParticipant (不存在!)
    │       └── WebSocketAuthService
    │
    ├── net.ooder.skill.scene.dto.todo.TodoDTO ─ ⚠️ 跨模块 DTO 直接依赖
    │
    └── net.ooder.skill.tenant.context.TenantContext ─ ✅ 合理: 上下文传播
```

---

## 二、设计原则提案

### 2.1 核心原则: SE 不参与 Web API

```
┌─────────────────────────────────────────────────────────────────────┐
│                    建议的架构边界                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    Web API 层 (skill-agent)                   │   │
│  │                                                               │   │
│  │  职责: HTTP 请求处理、参数校验、响应封装                        │   │
│  │  依赖: 仅依赖 SPI 接口，不直接依赖 SE SDK                       │   │
│  │                                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ 仅通过 SPI 接口调用                  │
│                              ▼                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    SPI 接口层 (ooder-spi-core)                │   │
│  │                                                               │   │
│  │  职责: 定义跨模块通信契约                                       │   │
│  │  包含: ImDeliveryDriver, RagEnhanceDriver, WorkflowDriver     │   │
│  │        MessagingService, SessionService 等                    │   │
│  │                                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ 实现                                │
│                              ▼                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    业务服务层 (各 skill 模块)                  │   │
│  │                                                               │   │
│  │  skill-im-gateway: 实现 ImDeliveryDriver                      │   │
│  │  skill-rag: 实现 RagEnhanceDriver                             │   │
│  │  skill-workflow: 实现 WorkflowDriver                          │   │
│  │  skill-messaging: 实现 MessagingService                       │   │
│  │                                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ 可选: 内部使用 SE SDK               │
│                              ▼                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    场景引擎层 (scene-engine)                   │   │
│  │                                                               │   │
│  │  职责: 场景编排、Agent 协作、A2A 协议                           │   │
│  │  不暴露: 不直接被 Web API 层调用                                │   │
│  │  消费者: 仅被 skill-messaging 等消息服务模块内部使用             │   │
│  │                                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 分层职责

| 层级 | 模块示例 | 职责 | 可依赖 |
|------|---------|------|--------|
| **Web API** | skill-agent | HTTP 处理、参数校验、响应封装 | SPI 接口 |
| **SPI 接口** | ooder-spi-core | 跨模块通信契约定义 | 无 (纯接口) |
| **业务服务** | skill-im-gateway, skill-rag | 业务逻辑实现 | SPI + SE SDK(内部) |
| **场景引擎** | scene-engine | 场景编排、Agent 协作 | 无外部依赖 |

### 2.3 SE SDK 的正确使用方式

```
❌ 错误用法 (当前):
   skill-agent → 直接 new MessageEnvelope() → 调用 NorthboundMessageQueue

✅ 正确用法 (建议):
   skill-agent → 调用 MessagingService.sendMessage() 
                        ↓
               skill-messaging (实现层) → 内部使用 NorthboundMessageQueue
```

---

## 三、需要新增的 SPI 接口

### 3.1 MessagingService (消息服务)

```java
// 位置: net.ooder.spi.messaging.MessagingService
package net.ooder.spi.messaging;

public interface MessagingService {
    
    // 发送消息到场景组
    String sendToSceneGroup(String sceneGroupId, Message message);
    
    // 发送消息给用户
    String sendToUser(String userId, Message message);
    
    // 广播消息
    void broadcast(String channel, Message message);
    
    // 订阅消息
    void subscribe(String sceneGroupId, MessageHandler handler);
    
    interface MessageHandler {
        void handle(Message message);
    }
}

// 消息模型 (简化，不依赖 SE 类型)
record Message(
    String messageId,
    String from,
    String to,
    String content,
    String type,
    Map<String, Object> metadata
) {}
```

### 3.2 SessionService (会话服务)

```java
// 位置: net.ooder.spi.messaging.SessionService
package net.ooder.spi.messaging;

public interface SessionService {
    
    String createSession(String sceneGroupId, String userId);
    
    Session getSession(String sessionId);
    
    void closeSession(String sessionId);
    
    List<Session> listActiveSessions(String sceneGroupId);
    
    record Session(
        String sessionId,
        String sceneGroupId,
        String userId,
        long createdAt,
        String status
    ) {}
}
```

---

## 四、迁移方案

### 4.1 Phase 1: 补齐 SPI 接口 (优先级 P0)

| 任务 | 说明 |
|------|------|
| 1.1 | 在 ooder-spi-core 中新增 `MessagingService` 接口 |
| 1.2 | 在 ooder-spi-core 中新增 `SessionService` 接口 |
| 1.3 | 定义简化的 `Message`/`Session` 数据模型 (不依赖 SE 类型) |

### 4.2 Phase 2: 实现层迁移 (优先级 P1)

| 任务 | 说明 |
|------|------|
| 2.1 | skill-messaging 模块实现 `MessagingService` (内部使用 SE SDK) |
| 2.2 | skill-messaging 模块实现 `SessionService` |
| 2.3 | 移除 skill-agent 对 SE SDK 的直接依赖 |

### 4.3 Phase 3: Web API 层简化 (优先级 P1)

| 任务 | 说明 |
|------|------|
| 3.1 | AgentChatServiceImpl 仅依赖 SPI 接口 |
| 3.2 | 移除 `NorthboundMessageQueue`/`MessageEnvelope` 的直接使用 |
| 3.3 | 通过 `MessagingService` 发送消息 |

---

## 五、讨论要点

### 5.1 SE SDK 的定位

**问题**: scene-engine 应该暴露给哪些模块？

**选项 A**: SE SDK 仅被 skill-messaging 内部使用 (推荐)
- 优点: 边界清晰，Web API 层不感知 SE
- 缺点: 需要新增 MessagingService SPI

**选项 B**: SE SDK 可被所有 skill 模块使用
- 优点: 灵活性高
- 缺点: 边界模糊，类型耦合严重

### 5.2 MessageParticipant 缺失问题

**问题**: SE SDK 3.0.1 中 `MessageEnvelope.setFrom/setTo` 需要 `MessageParticipant`，但该类不存在

**选项 A**: 在 SE SDK 中补充 `MessageParticipant` 类
- 需要修改 SE SDK 源码

**选项 B**: 在 SPI 层定义简化版消息模型，不使用 `MessageEnvelope`
- 推荐方案，符合"SE 不参与 Web API"原则

### 5.3 DTO 跨模块引用问题

**问题**: skill-agent 直接引用 skill-scene 的 `TodoDTO`

**选项 A**: 在 SPI 层定义 `TodoInfo` 接口，skill-scene 提供实现
- 符合 SPI 架构原则

**选项 B**: 保持现状，允许跨模块 DTO 引用
- 简单但有耦合风险

---

## 六、建议决策

| # | 决策点 | 建议方案 | 理由 |
|---|--------|---------|------|
| 1 | SE SDK 暴露范围 | **仅被 skill-messaging 内部使用** | 边界清晰，符合单一职责 |
| 2 | MessageParticipant 缺失 | **SPI 层定义简化消息模型** | 不修改 SE SDK，向后兼容 |
| 3 | TodoDTO 跨模块引用 | **SPI 层定义 TodoInfo** | 解耦，支持多实现 |
| 4 | AgentChatServiceImpl 依赖 | **仅依赖 SPI 接口** | 符合依赖倒置原则 |

---

## 七、下一步行动

1. **确认架构原则**: SE 不参与 Web API 层
2. **补齐 SPI 接口**: MessagingService + SessionService
3. **实现层迁移**: skill-messaging 封装 SE SDK
4. **简化 Web API**: AgentChatServiceImpl 仅依赖 SPI

---

## 附录: 文件路径

| 文件 | 绝对路径 |
|------|---------|
| **本文档** | `e:\apex\os\skills\_system\skill-agent\docs\SE-ARCHITECTURE-DISCUSSION-2026-001.md` |
| **SPI 重构方案** | `e:\apex\os\skills\_system\skill-agent\docs\SPI-REFACTOR-2026-001.md` |
| **SPI 架构分析** | `e:\apex\os\skills\_system\skill-agent\docs\SPI-ARCHITECTURE-2026-001.md` |
| **ooder-spi-core** | `e:\apex\os\skills\_base\ooder-spi-core\` |
