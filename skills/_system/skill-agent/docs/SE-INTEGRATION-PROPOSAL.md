# SE SDK 集成方案讨论 — 分层解耦设计

> **文档版本**: v1.0 | **日期**: 2026-04-05  
> **状态**: 待讨论 | **目标**: 将 SE SDK 复杂度隔离在实现层，业务层仅依赖 SPI

---

## 一、当前问题诊断

### 1.1 核心矛盾

```
当前架构 (❌ 问题):
┌─────────────────────────────────────────────────────────────────┐
│  skill-agent (业务服务层)                                        │
│  ├─ 直接 import net.ooder.scene.message.queue.MessageEnvelope   │
│  ├─ 直接 import net.ooder.scene.message.northbound.*            │
│  └─ 直接调用 NorthboundMessageQueue.sendToUser()                │
│                                                                 │
│  问题: 业务层直接依赖 SE SDK 具体类型 → 紧耦合 + 编译错误         │
└─────────────────────────────────────────────────────────────────┘

期望架构 (✅ 目标):
┌─────────────────────────────────────────────────────────────────┐
│  skill-agent (业务服务层)                                        │
│  └─ 仅依赖 SPI 接口: MessagingService.sendMessage()              │
│                                                                 │
│  skill-messaging (实现层)                                        │
│  └─ 实现 MessagingService，内部使用 SE SDK                       │
│                                                                 │
│  优势: 业务层轻量 + SE SDK 变更不影响业务代码                     │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 具体错误点

| 错误位置 | 问题 | 影响 |
|---------|------|------|
| `AgentChatServiceImpl:148` | `MessageEnvelope.setFrom(String)` | SE SDK 要求 `MessageParticipant` 类型，不是 String |
| `AgentChatServiceImpl:148` | `MessageEnvelope.setTo(String)` | 同上 |
| `AgentChatServiceImpl:149` | `northboundQueue.sendToUser()` | 方法签名不匹配 |
| 整体架构 | skill-agent 直接依赖 SE SDK 类型 | 违反分层原则，SE 升级会导致编译失败 |

### 1.3 SE SDK 类型缺失

| 期望类型 | 实际情况 | 解决方向 |
|---------|---------|---------|
| `MessageParticipant` | **不存在** | 需要在 SPI 层定义简化版 |
| `MessageEnvelope` | 存在但字段类型复杂 | 封装在实现层内部使用 |
| `NorthboundMessageQueue` | 存在 | 仅在实现层使用，不暴露给业务层 |

---

## 二、分层架构建议

### 2.1 三层分离原则

```
┌─────────────────────────────────────────────────────────────────┐
│                    SPI 接口层 (ooder-spi-core)                   │
│                                                                 │
│  职责: 定义跨模块通信契约，不包含任何实现                          │
│  依赖: 无外部依赖，纯 Java 接口 + 简单数据模型                     │
│  消费者: 所有业务模块 (skill-agent, skill-im-gateway 等)          │
│                                                                 │
│  关键接口:                                                       │
│  ├─ ImService / ImDeliveryDriver  (IM 通道)                     │
│  ├─ RagEnhanceDriver              (RAG 增强)                    │
│  ├─ WorkflowDriver                (工作流)                      │
│  ├─ MessagingService              (消息服务) ← ★ 需新增          │
│  └─ SessionService                (会话服务) ← ★ 需新增          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 依赖接口
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    业务服务层 (各 skill 模块)                     │
│                                                                 │
│  skill-agent:                                                    │
│  ├─ AgentChatServiceImpl                                         │
│  │   ├─ @Autowired MessagingService messagingService            │
│  │   ├─ @Autowired RagEnhanceDriver ragEnhanceDriver            │
│  │   └─ @Autowired WorkflowDriver workflowDriver                │
│  │                                                               │
│  │   // 发送消息 - 不再直接操作 SE SDK                            │
│  │   messagingService.sendToSceneGroup(sceneGroupId, message);  │
│  │                                                               │
│  └─ 不再 import net.ooder.scene.* 任何类型                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 实现接口
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    实现层 (skill-messaging 等)                   │
│                                                                 │
│  skill-messaging:                                                │
│  ├─ MessagingServiceImpl implements MessagingService             │
│  │   ├─ @Autowired NorthboundMessageQueue northboundQueue       │
│  │   └─ 内部使用 MessageEnvelope / A2AMessage 等 SE 类型         │
│  │                                                               │
│  │   public String sendToSceneGroup(String sceneGroupId,        │
│  │                                   Message message) {          │
│  │       MessageEnvelope env = new MessageEnvelope();           │
│  │       env.setFrom(new MessageParticipant(message.from()));   │
│  │       env.setTo(new MessageParticipant(message.to()));       │
│  │       env.setPayload(message.toMap());                       │
│  │       return northboundQueue.sendToUser(sceneGroupId, env);  │
│  │   }                                                           │
│  │                                                               │
│  └─ SE SDK 复杂性被封装在此层                                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 依赖方向规则

| 层级 | 允许依赖 | 禁止依赖 |
|------|---------|---------|
| **SPI 接口层** | 无 | 任何实现层、SE SDK |
| **业务服务层** | SPI 接口 | SE SDK 具体类型 |
| **实现层** | SPI 接口 + SE SDK | 无限制 |

### 2.3 SE SDK 的正确使用方式

```
❌ 错误用法 (当前 skill-agent):
   AgentChatServiceImpl 
     → new MessageEnvelope()
     → northboundQueue.sendToUser()
     
   问题: 业务层直接操作 SE SDK 底层类型

✅ 正确用法 (建议):
   AgentChatServiceImpl 
     → messagingService.sendToSceneGroup(sceneGroupId, message)
     
   MessagingServiceImpl (实现层)
     → 内部使用 MessageEnvelope / NorthboundMessageQueue
```

---

## 三、需要新增的 SPI 接口

### 3.1 MessagingService (消息服务)

```java
// 位置: net.ooder.spi.messaging.MessagingService
package net.ooder.spi.messaging;

import java.util.Map;

public interface MessagingService {
    
    /**
     * 发送消息到场景组
     * @return 消息ID
     */
    String sendToSceneGroup(String sceneGroupId, Message message);
    
    /**
     * 发送消息给用户
     */
    String sendToUser(String userId, Message message);
    
    /**
     * 广播消息到通道
     */
    void broadcast(String channel, Message message);
    
    /**
     * 订阅场景组消息
     */
    void subscribe(String sceneGroupId, MessageHandler handler);
    
    /**
     * 取消订阅
     */
    void unsubscribe(String sceneGroupId);
    
    @FunctionalInterface
    interface MessageHandler {
        void handle(Message message);
    }
}
```

### 3.2 Message (简化消息模型)

```java
// 位置: net.ooder.spi.messaging.model.Message
package net.ooder.spi.messaging.model;

import java.util.Map;

/**
 * 简化的消息模型 - 不依赖 SE SDK 类型
 */
public class Message {
    
    private String messageId;
    private String from;        // 发送者ID
    private String fromName;    // 发送者名称
    private String to;          // 接收者ID
    private String toName;      // 接收者名称
    private String content;     // 消息内容
    private String type;        // 消息类型: P2P/P2A/A2A/BROADCAST
    private String priority;    // 优先级
    private long timestamp;     // 时间戳
    private Map<String, Object> metadata;
    
    // getters/setters 省略...
    
    public static Message of(String from, String to, String content) {
        Message m = new Message();
        m.setFrom(from);
        m.setTo(to);
        m.setContent(content);
        m.setTimestamp(System.currentTimeMillis());
        return m;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("messageId", messageId);
        map.put("from", from);
        map.put("to", to);
        map.put("content", content);
        map.put("type", type);
        map.put("timestamp", timestamp);
        if (metadata != null) map.putAll(metadata);
        return map;
    }
}
```

### 3.3 SessionService (会话服务)

```java
// 位置: net.ooder.spi.messaging.SessionService
package net.ooder.spi.messaging;

import java.util.List;

public interface SessionService {
    
    /**
     * 创建会话
     */
    String createSession(String sceneGroupId, String userId);
    
    /**
     * 获取会话
     */
    Session getSession(String sessionId);
    
    /**
     * 关闭会话
     */
    void closeSession(String sessionId);
    
    /**
     * 列出活跃会话
     */
    List<Session> listActiveSessions(String sceneGroupId);
    
    /**
     * 检查用户是否在线
     */
    boolean isUserOnline(String sceneGroupId, String userId);
}
```

---

## 四、迁移方案

### 4.1 Phase 1: 补齐 SPI 接口 (优先级 P0)

| 步骤 | 任务 | 预估工时 |
|------|------|---------|
| 1.1 | 在 ooder-spi-core 中新增 `MessagingService` 接口 | 0.5h |
| 1.2 | 在 ooder-spi-core 中新增 `Message` 数据模型 | 0.5h |
| 1.3 | 在 ooder-spi-core 中新增 `SessionService` 接口 | 0.5h |
| 1.4 | 重新 mvn install ooder-spi-core | 自动 |

### 4.2 Phase 2: 创建实现模块 (优先级 P0)

| 步骤 | 任务 | 说明 |
|------|------|------|
| 2.1 | 创建 `skill-messaging` 模块 | 或在现有模块中添加实现类 |
| 2.2 | 实现 `MessagingServiceImpl` | 内部使用 SE SDK |
| 2.3 | 实现 `SessionServiceImpl` | 内部使用 SE SDK |
| 2.4 | 处理 SE SDK 类型转换 | String → MessageParticipant 等 |

### 4.3 Phase 3: 重构 skill-agent (优先级 P1)

| 步骤 | 变更 |
|------|------|
| 3.1 | 移除所有 `import net.ooder.scene.*` |
| 3.2 | 改为 `@Autowired MessagingService` |
| 3.3 | 调用 `messagingService.sendToSceneGroup()` |
| 3.4 | 删除 `deliverViaNorthbound()` 方法 |

### 4.4 Phase 4: 验证 (优先级 P1)

| 验证项 | 期望结果 |
|--------|---------|
| skill-agent 编译 | 0 errors |
| skill-agent 不依赖 SE SDK | `mvn dependency:tree` 无 scene-engine |
| 消息发送功能 | 正常工作 |

---

## 五、讨论要点

### 5.1 SE SDK 是否应该暴露给业务层？

| 观点 | 论据 |
|------|------|
| **不应该暴露** | SE SDK 是基础设施层，变更频繁；业务层应只依赖稳定的 SPI |
| **可以暴露** | 减少一层封装；性能损耗小；当前时间紧迫 |

**建议**: 采用分层封装，长期收益大于短期成本。

### 5.2 MessageParticipant 缺失问题

| 方案 | 优点 | 缺点 |
|------|------|------|
| **在 SPI 层定义简化版** | 业务层简单使用 | 需要在实现层转换 |
| **在 SE SDK 中补充** | 类型统一 | 需要 SE SDK 发版 |
| **直接用 String** | 最简单 | 语义不清晰 |

**建议**: 在 SPI 层定义 `Message.from(String userId)` 简化模型，实现层负责转换。

### 5.3 是否需要独立的 skill-messaging 模块？

| 方案 | 适用场景 |
|------|---------|
| **独立模块** | 多个模块需要消息能力，便于复用 |
| **内嵌在 skill-agent** | 仅 skill-agent 使用，减少模块数 |

**建议**: 先内嵌实现类，后续按需抽取。

---

## 六、快速修复方案 (临时)

如果时间紧迫，可采用临时方案：

### 6.1 移除 Northbound 调用

```java
// AgentChatServiceImpl.java
// 临时移除 SE SDK 依赖，仅保留 IM Gateway 投递

private void deliverViaNorthbound(String sceneGroupId, AgentChatMessageDTO m) {
    // TODO: 等待 MessagingService SPI 完成后再实现
    log.debug("[Northbound] Skipped - pending MessagingService SPI");
}
```

### 6.2 仅保留核心功能

- ✅ 消息存储 (DB/Memory)
- ✅ IM Gateway 投递 (通过 ImDeliveryDriver SPI)
- ⏸️ Northbound 消息路由 (待 MessagingService SPI)

---

## 七、决策请求

请确认以下决策点：

| # | 决策项 | 选项 |
|---|--------|------|
| 1 | 是否采用分层封装方案？ | A. 是，创建 MessagingService SPI<br>B. 否，临时移除 Northbound 调用 |
| 2 | MessageParticipant 如何处理？ | A. SPI 层定义简化模型<br>B. 实现层内部定义<br>C. 等 SE SDK 补充 |
| 3 | 实现类放置位置？ | A. 独立 skill-messaging 模块<br>B. 内嵌在 skill-agent |
| 4 | 优先级？ | A. 立即实施<br>B. 先临时方案，后续重构 |

---

## 八、附录：当前编译错误清单

| 文件 | 行号 | 错误 | 解决方向 |
|------|------|------|---------|
| AgentChatServiceImpl.java | 148 | `String` 无法转换为 `MessageParticipant` | 移除 SE SDK 直接调用 |
| AgentChatServiceImpl.java | 149 | `northboundQueue.sendToUser()` 签名不匹配 | 封装在 MessagingService |
| AgentChatServiceImpl.java | 102 | `long` 转 `int` 可能有损失 | PageResult 泛型调整 |
| AgentChatServiceImpl.java | 129 | 二元运算符操作数类型错误 | priority 字段类型修复 |

---

**文档路径**: `e:\apex\os\skills\_system\skill-agent\docs\SE-INTEGRATION-PROPOSAL.md`
