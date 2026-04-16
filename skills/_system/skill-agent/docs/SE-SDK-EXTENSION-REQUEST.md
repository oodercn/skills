# SE SDK 扩展需求协作文档

> **文档版本**: v1.0 | **日期**: 2026-04-05  
> **状态**: 待 SE 团队确认 | **优先级**: P0  
> **发起方**: skill-agent 团队 | **接收方**: SE (scene-engine) 团队

---

## 一、背景说明

### 1.1 当前问题

skill-agent 模块在集成 SE SDK 3.0.1 时遇到以下问题：

| # | 问题 | 影响 |
|---|------|------|
| 1 | `MessageParticipant` 类不存在 | 编译失败 |
| 2 | `MessageEnvelope.setFrom/setTo` 需要 `MessageParticipant` 类型 | 无法直接使用 String |
| 3 | `NorthboundMessageQueue.sendToUser()` 方法签名与调用方不匹配 | 编译失败 |

### 1.2 期望目标

通过扩展 SE SDK，使其能够被业务层直接使用，同时保持向后兼容。

---

## 二、需求清单

### 2.1 新增类：MessageParticipant

**位置**: `net.ooder.scene.message.participant.MessageParticipant`

```java
package net.ooder.scene.message.participant;

/**
 * 消息参与者 - 表示消息的发送者或接收者
 */
public class MessageParticipant {
    
    private String id;          // 参与者ID
    private String name;        // 参与者名称
    private String type;        // 参与者类型: user/agent/system
    private String avatar;      // 头像URL (可选)
    
    // 构造函数
    public MessageParticipant() {}
    
    public MessageParticipant(String id) {
        this.id = id;
    }
    
    public MessageParticipant(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // 静态工厂方法
    public static MessageParticipant of(String id) {
        return new MessageParticipant(id);
    }
    
    public static MessageParticipant of(String id, String name) {
        return new MessageParticipant(id, name);
    }
    
    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    @Override
    public String toString() {
        return "MessageParticipant{id='" + id + "', name='" + name + "'}";
    }
}
```

### 2.2 扩展 MessageEnvelope

**位置**: `net.ooder.scene.message.queue.MessageEnvelope`

**新增便捷方法**:

```java
// 在现有 MessageEnvelope 类中新增以下方法

public class MessageEnvelope {
    
    // ... 现有代码 ...
    
    /**
     * 便捷方法：设置发送者 (String 类型)
     * 自动转换为 MessageParticipant
     */
    public MessageEnvelope from(String fromId) {
        this.from = MessageParticipant.of(fromId);
        return this;
    }
    
    /**
     * 便捷方法：设置发送者 (带名称)
     */
    public MessageEnvelope from(String fromId, String fromName) {
        this.from = MessageParticipant.of(fromId, fromName);
        return this;
    }
    
    /**
     * 便捷方法：设置接收者 (String 类型)
     */
    public MessageEnvelope to(String toId) {
        this.to = MessageParticipant.of(toId);
        return this;
    }
    
    /**
     * 便捷方法：设置接收者 (带名称)
     */
    public MessageEnvelope to(String toId, String toName) {
        this.to = MessageParticipant.of(toId, toName);
        return this;
    }
    
    /**
     * 便捷方法：设置消息内容 (Map 类型)
     */
    public MessageEnvelope payload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }
    
    /**
     * 便捷方法：设置创建时间 (自动使用当前时间)
     */
    public MessageEnvelope createdAtNow() {
        this.createdAt = java.time.LocalDateTime.now().toString();
        return this;
    }
    
    /**
     * 构建器模式 (可选，推荐)
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private MessageEnvelope envelope = new MessageEnvelope();
        
        public Builder from(String id) { envelope.from(id); return this; }
        public Builder from(String id, String name) { envelope.from(id, name); return this; }
        public Builder to(String id) { envelope.to(id); return this; }
        public Builder to(String id, String name) { envelope.to(id, name); return this; }
        public Builder payload(Map<String, Object> payload) { envelope.setPayload(payload); return this; }
        public Builder createdAt(String time) { envelope.setCreatedAt(time); return this; }
        public Builder createdAtNow() { envelope.createdAtNow(); return this; }
        
        public MessageEnvelope build() {
            return envelope;
        }
    }
}
```

### 2.3 扩展 NorthboundMessageQueue

**位置**: `net.ooder.scene.message.northbound.NorthboundMessageQueue`

**新增简化方法**:

```java
// 在现有 NorthboundMessageQueue 接口中新增以下方法

public interface NorthboundMessageQueue {
    
    // ... 现有方法 ...
    
    /**
     * 简化方法：发送消息到场景组
     * @param sceneGroupId 场景组ID
     * @param fromId 发送者ID
     * @param toId 接收者ID
     * @param content 消息内容
     * @return 消息ID
     */
    default String send(String sceneGroupId, String fromId, String toId, String content) {
        MessageEnvelope envelope = MessageEnvelope.builder()
            .from(fromId)
            .to(toId)
            .payload(Map.of("content", content))
            .createdAtNow()
            .build();
        return sendToUser(sceneGroupId, envelope);
    }
    
    /**
     * 简化方法：发送消息到场景组 (Map payload)
     */
    default String send(String sceneGroupId, String fromId, String toId, Map<String, Object> payload) {
        MessageEnvelope envelope = MessageEnvelope.builder()
            .from(fromId)
            .to(toId)
            .payload(payload)
            .createdAtNow()
            .build();
        return sendToUser(sceneGroupId, envelope);
    }
    
    /**
     * 简化方法：广播消息到场景组
     */
    default void broadcast(String sceneGroupId, String fromId, Map<String, Object> payload) {
        MessageEnvelope envelope = MessageEnvelope.builder()
            .from(fromId)
            .payload(payload)
            .createdAtNow()
            .build();
        broadcastToSceneGroup(sceneGroupId, envelope);
    }
}
```

---

## 三、使用示例

### 3.1 业务层调用方式 (期望)

```java
// skill-agent 中的使用方式

@Autowired
private NorthboundMessageQueue northboundQueue;

public void sendMessage(String sceneGroupId, AgentChatMessageDTO message) {
    // 方式 1: 使用便捷方法
    String messageId = northboundQueue.send(
        sceneGroupId,
        message.getSenderId(),
        message.getReceiverId(),
        message.getContent()
    );
    
    // 方式 2: 使用 Builder 模式
    MessageEnvelope envelope = MessageEnvelope.builder()
        .from(message.getSenderId(), message.getSender())
        .to(message.getReceiverId(), message.getReceiverName())
        .payload(Map.of(
            "messageId", message.getMessageId(),
            "content", message.getContent(),
            "type", message.getMessageType()
        ))
        .createdAtNow()
        .build();
    
    northboundQueue.sendToUser(sceneGroupId, envelope);
}
```

### 3.2 向后兼容

现有使用 `MessageParticipant` 的代码保持不变：

```java
// 现有方式仍然有效
MessageEnvelope envelope = new MessageEnvelope();
envelope.setFrom(new MessageParticipant("user-001", "张三"));
envelope.setTo(new MessageParticipant("agent-001", "智能助手"));
envelope.setPayload(payload);
```

---

## 四、交付清单

| # | 交付物 | 说明 |
|---|--------|------|
| 1 | `MessageParticipant.java` | 新增类 |
| 2 | `MessageEnvelope.java` | 新增便捷方法和 Builder |
| 3 | `NorthboundMessageQueue.java` | 新增简化方法 |
| 4 | scene-engine 3.1.0 | 新版本发布 |
| 5 | 更新文档 | API 使用说明 |

---

## 五、时间安排

| 阶段 | 任务 | 负责方 | 预计完成 |
|------|------|--------|---------|
| 1 | 确认需求 | 双方 | 2026-04-05 |
| 2 | SE SDK 开发 | SE 团队 | 2026-04-06 |
| 3 | SE SDK 发版 3.1.0 | SE 团队 | 2026-04-06 |
| 4 | skill-agent 集成测试 | skill-agent 团队 | 2026-04-07 |

---

## 六、验证标准

### 6.1 编译验证

```bash
# skill-agent 应能编译通过
cd e:\apex\os\skills\_system\skill-agent
mvn clean compile
# 期望: BUILD SUCCESS, 0 errors
```

### 6.2 功能验证

| 测试项 | 期望结果 |
|--------|---------|
| `MessageParticipant.of("user-001")` | 返回有效对象 |
| `MessageEnvelope.builder().from("u1").to("u2").build()` | 返回有效 envelope |
| `northboundQueue.send(sceneGroupId, from, to, content)` | 消息成功发送 |

---

## 七、联系信息

| 角色 | 联系人 |
|------|--------|
| 发起方 | skill-agent 团队 |
| 接收方 | SE (scene-engine) 团队 |
| 协作文档位置 | `e:\apex\os\skills\_system\skill-agent\docs\SE-SDK-EXTENSION-REQUEST.md` |

---

## 附录：相关文档

| 文档 | 路径 |
|------|------|
| SPI 架构分析 | `e:\apex\os\skills\_system\skill-agent\docs\SPI-ARCHITECTURE-2026-001.md` |
| SPI 重构方案 | `e:\apex\os\skills\_system\skill-agent\docs\SPI-REFACTOR-2026-001.md` |
| SE 扩展分析 | `e:\apex\os\skills\_system\skill-agent\docs\SE-EXTENSION-ANALYSIS.md` |
| SE 集成提案 | `e:\apex\os\skills\_system\skill-agent\docs\SE-INTEGRATION-PROPOSAL.md` |

---

**请 SE 团队确认后回复，谢谢！**
