# 严格复查可疑点报告

## 报告信息

| 项目 | 内容 |
|-----|------|
| 报告编号 | AUDIT-2026-001-STRICT |
| 审计日期 | 2026-04-04 |
| 审计范围 | Agent-Chat模块全部新增/修改代码 |
| 审计方法 | 静态代码分析 + 逻辑追踪 + 安全扫描 |

---

## 一、P0 - 严重问题 (必须修复)

### BUG-001: 双存储不一致 - 待办操作不同步数据库 ⚠️ **严重**

**位置**: [AgentChatServiceImpl.java:278-382](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/service/impl/AgentChatServiceImpl.java#L278-L382)

**问题**: `acceptTodo()`, `rejectTodo()`, `delegateTodo()`, `completeTodo()` 四个方法 **只操作内存中的 `todoStore`，完全未同步到数据库**。

```java
// 第281行 - 只从内存获取
TodoDTO todo = todoStore.get(todoId);
// ...
// 第292行 - 只修改内存对象
todo.setStatus(TODO_STATUS_ACCEPTED);
```

**影响**: 
- 当 `useDatabase()`=true 时，消息存入数据库但待办操作只写内存
- 服务重启后所有待办状态丢失
- 前端调用API返回成功但实际数据未持久化

**修复方案**: 所有待办方法需要同步写入 `todoRepository`

---

### BUG-002: 双存储不一致 - 消息查询只读内存 ⚠️ **严重**

**位置**: [AgentChatServiceImpl.java:155-164](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/service/impl/AgentChatServiceImpl.java#L155-L164)

**问题**: `getMessage()` 方法遍历内存 `messageStore`，当数据库模式启用时 **查不到已持久化的消息**。

```java
@Override
public AgentChatMessageDTO getMessage(String messageId) {
    for (List<AgentChatMessageDTO> messages : messageStore.values()) { // 只查内存！
        for (AgentChatMessageDTO message : messages) {
            if (messageId.equals(message.getMessageId())) {
                return message;
            }
        }
    }
    return null;
}
```

**影响**: 
- `markAsRead()` 调用 `getMessage()` → 查不到DB中的消息
- `addReaction()` / `removeReaction()` 同样受影响
- 已读状态、表情反应在数据库模式下全部失效

---

### BUG-003: getUnreadCounts() 不走数据库 ⚠️ **严重**

**位置**: [AgentChatServiceImpl.java:221-244](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/service/impl/AgentChatServiceImpl.java#L221-L244)

**问题**: 未读计数 **只统计内存数据**，数据库模式下返回0或错误值。

---

### BUG-004: markAllAsRead() 不走数据库 ⚠️ **严重**

**位置**: [AgentChatServiceImpl.java:208-218](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/service/impl/AgentChatServiceImpl.java#L208-L218)

**问题**: 标记全部已读 **只处理内存列表**，数据库模式下无效。

---

### BUG-005: 前端handleTodoAction状态映射错误 ⚠️ **严重**

**位置**: [unified-chat-window.js:330](file:///e:/apex/os/skills/_system/skill-llm-chat/src/main/resources/static/console/js/llm-chat-float/components/unified-chat-window.js#L330)

**问题**: 前端将action转为大写后直接设置status，与后端状态值不匹配：

```javascript
// 第330行
this.todos[todoIndex].status = action.toUpperCase();
// action="accept" → status="ACCEPTED" ✅ 正确
// action="reject" → status="REJECT" ❌ 应为 "REJECTED"
// action="complete" → status="COMPLETE" ❌ 应为 "COMPLETED"
```

**影响**: 拒绝和完成操作后前端显示状态不正确。

---

## 二、P1 - 重要问题 (建议修复)

### BUG-006: 内存泄漏风险 - 无界Map增长

**涉及文件**:
- [LocalMessageQueueServiceImpl.java:20-21](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/LocalMessageQueueServiceImpl.java#L20-L21) - `messageStore`, `userOfflineIndex`
- [MessageReliabilityService.java:25-27](file:///e:/apex/os/skills/_system/sskill-agent/src/main/java/net/ooder/skill/agent/spi/impl/MessageReliabilityService.java#L25-L27) - `deliveryRecords`, `retryCountMap`, `pendingAcks`
- [LocalA2AProtocolServiceImpl.java:22](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/LocalA2AProtocolServiceImpl.java#L22) - `handlers`
- [LocalSessionManagerImpl.java](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/LocalSessionManagerImpl.java) - `sessions`, `sceneSessionIndex`, `userSessionIndex`

**问题**: 所有ConcurrentHashMap **无上限、无过期清理、无容量限制**。长时间运行后导致OOM。

**建议**: 添加LRU缓存或定时清理机制。

---

### BUG-007: ChatMessage实体 @Id 策略问题

**位置**: [ChatMessage.java:15-17](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/entity/ChatMessage.java#L15-L17)

**问题**: 
1. `@Id` 字段使用手动UUID，无 `@GeneratedValue`。构造函数中自动生成UUID，但Builder可覆盖。
2. 构造函数每次new都生成UUID，JPA load时也会触发（如果JPA使用无参构造+反射set）

```java
public ChatMessage() {
    this.id = java.util.UUID.randomUUID().toString(); // JPA加载时会覆盖真实ID!
}
```

**风险**: 可能导致JPA实体ID被意外覆盖。

---

### BUG-008: Todo实体同样问题

**位置**: [Todo.java:67-72](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/entity/Todo.java#L67-L72)

同BUG-007。

---

### BUG-009: PriorityBlockingQueue容量限制100可能不够

**位置**: [LocalMessageQueueServiceImpl.java:35](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/LocalMessageQueueServiceImpl.java#L35)

```java
k -> new PriorityBlockingQueue<>(100, ...)
```

**问题**: 固定容量100，高并发时队列满会导致消息丢失（`offer()`返回false）。

---

### BUG-010: MessageReliabilityService.cleanupOldRecords需外部调用

**位置**: [MessageReliabilityService.java:136-146](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/MessageReliabilityService.java#L136-L146)

**问题**: 清理方法存在但 **没有任何定时任务调用它**，deliveryRecords会无限增长。

同时 `RETRY_DELAY_MS = 5000` 定义了但 **从未使用**。

---

### BUG-011: 前端fetch请求缺少认证信息

**位置**: [unified-chat-window.js:317-320](file:///e:/apex/os/skills/_system/skill-llm-chat/src/main/resources/static/console/js/llm-chat-float/components/unified-chat-window.js#L317-L320)

```javascript
const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
    // 缺少: credentials: 'same-origin'
    // 缺少: Authorization header 或 CSRF token
});
```

**问题**: 如果API需要认证，此请求会被403拒绝。

---

### BUG-012: LocalA2AProtocolServiceImpl.broadcastToAgents重复注册handler

**位置**: [LocalA2AProtocolServiceImpl.java:64](file:///e:/apex/os/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/spi/impl/LocalA2AProtocolServiceImpl.java#L64)

**问题**: `broadcastToAgents()` 使用 `message.getMessageType()` 查找handler，但broadcast消息的messageType可能与sendA2AMessage的不同，导致handler不会被正确触发。

---

## 三、P2 - 设计疑点

### SUS-001: useDatabase()判断时机不一致

**问题**: 同一个Service中，部分方法走了数据库分支（如`sendMessage`, `getMessages`），部分方法始终走内存（如`acceptTodo`, `getMessage`, `getUnreadCounts`）。这种 **选择性双模式** 是设计缺陷还是有意为之？如果是过渡方案应明确标注。

### SUS-002: TodoDTO vs Todo实体并存

**问题**: 新增了 `Todo` JPA实体，但待办操作仍使用旧的 `TodoDTO`（来自skill-scene模块），`convertTodoToDTO()` 方法写了但从未被调用。两套类型并存造成混乱。

### SUS-003: SPI接口命名冲突风险

**问题**: 本地SPI接口名以 `Local` 开头（`LocalSessionManager`等），但如果未来SE SDK提供同名接口（不含Local前缀），import时容易混淆。且当前 `UnifiedInterfaceConfig` 中引用的是SE SDK原始接口名。

### SUS-004: OfflineMessageProcessor未被集成

**问题**: 创建了 `OfflineMessageProcessor` 但 `UnifiedAgentChatServiceImpl` 和 `AgentChatServiceImpl` 都没有注入和使用它。这是一个死代码。

---

## 四、可疑点汇总矩阵

| 编号 | 类型 | 文件 | 严重度 | 状态 |
|------|------|------|-------|------|
| BUG-001 | 数据一致性 | AgentChatServiceImpl | P0 | 待修复 |
| BUG-002 | 数据一致性 | AgentChatServiceImpl | P0 | 待修复 |
| BUG-003 | 数据一致性 | AgentChatServiceImpl | P0 | 待修复 |
| BUG-004 | 数据一致性 | AgentChatServiceImpl | P0 | 待修复 |
| BUG-005 | 状态映射 | unified-chat-window.js | P0 | 待修复 |
| BUG-006 | 内存泄漏 | 多个SPI实现 | P1 | 待修复 |
| BUG-007 | ID策略 | ChatMessage.java | P1 | 待修复 |
| BUG-008 | ID策略 | Todo.java | P1 | 待修复 |
| BUG-009 | 容量限制 | LocalMessageQueueServiceImpl | P1 | 待修复 |
| BUG-010 | 死代码 | MessageReliabilityService | P1 | 待修复 |
| BUG-011 | 认证缺失 | unified-chat-window.js | P1 | 待修复 |
| BUG-012 | handler匹配 | LocalA2AProtocolServiceImpl | P1 | 待确认 |
| SUS-001 | 设计疑点 | AgentChatServiceImpl | P2 | 待确认 |
| SUS-002 | 类型混乱 | AgentChatServiceImpl | P2 | 待确认 |
| SUS-003 | 命名风险 | spi包 | P2 | 待确认 |
| SUS-004 | 死代码 | OfflineMessageProcessor | P2 | 待确认 |

---

## 五、修复优先级建议

### 第一批 (立即修复)
1. **BUG-001~004**: 统一所有方法的双存储逻辑，要么全走DB要么全走Memory
2. **BUG-005**: 修复前端状态映射表

### 第二批 (本周内)
3. **BUG-006**: 为所有ConcurrentHashMap添加容量限制和过期清理
4. **BUG-007~008**: 移除构造函数中的UUID自动生成，改用@PrePersist
5. **BUG-011**: fetch请求添加credentials

### 第三批 (下个迭代)
6. **BUG-009~010, 012**: 容量/清理/handler问题
7. **SUS-001~004**: 设计层面清理

---

*报告生成时间: 2026-04-04*
*审计工具: 静态代码分析*
