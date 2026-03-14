# SE 对话存储与 Function Calling 架构设计

## 1. 枸架概述

### 1.1 设计目标

将对话存储逻辑和 Function Calling 调用结果信息向 SE 层转移，实现：

1. **多轮对话整合** - SE 统一管理对话上下文和状态
2. **知识库实时更新** - Function Calling 结果自动反馈到知识库
3. **完善审计记录** - 所有操作可追溯、可审计

### 1.2 枸构层次

```
┌─────────────────────────────────────────────────────────────────┐
│                     应用层 (Skill Layer)                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ ChatController │  │ LlmController │  │ Other Skills    │  │
│  └────────┬──────┘  └────────┬──────┘  └────────┬──────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼ 调用
                              │
┌─────────────────────────────────────────────────────────────────┐
│                     服务层 (SE Layer)                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ConversationSvc │  │ ToolOrchestrator │  │  AuditService   │  │
│  └────────┬──────┘  └────────┬──────┘  └────────┬──────┘  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ KnowledgeService│  │ FunctionCallLog │  │  AuditLog       │  │
│  └────────┬──────┘  └────────┬──────┘  └────────┬──────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼ 持久化
                              │
┌─────────────────────────────────────────────────────────────────┐
│                     存储层 (Storage Layer)                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ ConversationStore│  │ KnowledgeStore  │  │  AuditStore     │  │
│  └────────┬──────┘  └────────┬──────┘  └────────┬──────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## 2. 核心接口设计

### 2.1 ConversationService 接口

```java
package net.ooder.scene.conversation;

import net.ooder.scene.skill.tool.ToolCallResult;
import java.util.List;
import java.util.Map;

/**
 * 对话服务接口
 * 
 * <p>提供多轮对话管理能力，包括：</p>
 * <ul>
 *   <li>对话上下文管理</li>
 *   <li>消息持久化</li>
 *   <li>工具调用记录</li>
 *   <li>知识库自动更新</li>
 * </ul>
 */
public interface ConversationService {
    
    /**
     * 创建对话会话
     */
    ConversationSession createSession(String userId, String title);
    
    /**
     * 获取对话会话
     */
    ConversationSession getSession(String sessionId);
    
    /**
     * 添加消息到对话
     */
    ConversationMessage addMessage(String sessionId, String role, String content);
    
    /**
     * 获取对话历史
     */
    List<ConversationMessage> getHistory(String sessionId, int limit);
    
    /**
     * 记录工具调用结果
     * 
     * <p>自动触发：</p>
     * <ul>
     *   <li>审计日志记录</li>
     *   <li>知识库更新（可选）</li>
     * </ul>
     */
    void recordToolCall(String sessionId, ToolCallResult result);
    
    /**
     * 获取工具调用历史
     */
    List<FunctionCallLog> getToolCallHistory(String sessionId);
    
    /**
     * 从对话中学习并更新知识库
     */
    void learnFromConversation(String sessionId);
}
```

### 2.2 ConversationSession 实体

```java
package net.ooder.scene.conversation;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对话会话
 */
public class ConversationSession {
    
    private String sessionId;
    private String userId;
    private String title;
    private Date createdAt;
    private Date updatedAt;
    private List<ConversationMessage> messages;
    private Map<String, Object> metadata;
    private ConversationStatus status;
    
    public enum ConversationStatus {
        ACTIVE, PAUSED, ENDED
    }
    
    // getters and setters...
}
```

### 2.3 ConversationMessage 实体

```java
package net.ooder.scene.conversation;

import java.util.Date;

/**
 * 对话消息
 */
public class ConversationMessage {
    
    private String messageId;
    private String sessionId;
    private MessageRole role;
    private String content;
    private Date createdAt;
    private ToolCallInfo toolCall;
    
    public enum MessageRole {
        SYSTEM, USER, ASSISTANT, TOOL
    }
    
    // getters and setters...
}
```

### 2.4 FunctionCallLog 实体

```java
package net.ooder.scene.conversation;

import java.util.Date;
import java.util.Map;

import net.ooder.scene.skill.tool.ToolCallResult;

/**
 * 工具调用日志
 * 
 * <p>记录 Function Calling 的完整执行过程</p>
 */
public class FunctionCallLog {
    
    private String logId;
    private String sessionId;
    private String messageId;
    private String toolCallId;
    private String toolName;
    private Map<String, Object> arguments;
    private ToolCallResult result;
    private long executionTime;
    private Date createdAt;
    
    // getters and setters...
}
```

## 3. 技术选型说明

### 3.1 为什么选择 SE 层统一管理？

| 技术选择 | 说明 | 优势 |
|----------|------|------|
| **统一上下文管理** | SE 层管理所有对话会话 | 跨 Skill 共享对话状态 |
| **审计追溯** | AuditService 统一记录 | 所有操作可审计 |
| **知识库联动** | 工具结果自动更新知识库 | 实时学习优化 |
| **工具编排** | ToolOrchestrator 统一调度 | 复用工具执行逻辑 |

### 3.2 为什么使用 fastjson2？

| 特性 | 说明 |
|------|------|
| **高性能** | 比 Jackson 快 2-3 倍 |
| **低内存** | 适合大量对话存储 |
| **API 简洁** | `JSON.parseArray()` 和 `JSON.toJSONString()` |
| **兼容性好** | 支持 JDK 8+ |

### 3.3 为什么使用文件存储？

| 特性 | 说明 |
|------|------|
| **简单部署** | 无需额外数据库依赖 |
| **易于备份** | 直接复制文件即可 |
| **可移植性** | 支持嵌入式和云端部署 |
| **可扩展** | 后期可迁移到数据库 |

## 4. 更简洁的调用方式

### 4.1 当前调用方式（复杂）

```java
// 当前方式：需要手动管理历史、解析工具调用、格式化结果
ChatController controller = new ChatController();
controller.sendMessage(sessionId, request);

// 需要手动处理：
// 1. 解析 LLM 响应中的 tool_calls
// 2. 执行工具调用
// 3. 格式化工具结果
// 4. 再次调用 LLM
// 5. 保存消息历史
// 6. 更新知识库
```

### 4.2 期望的简洁调用方式

```java
// 方式 1: 一行代码完成对话
ConversationService conv = SE.getConversationService();
ConversationMessage response = conv.chat(sessionId, "你好");

// 方式 2: 带工具执行的对话
ConversationMessage response = conv.chatWithTools(sessionId, "现在几点了？", tools);

// 方式 3: 流式对话
conv.chatStream(sessionId, "讲个故事", chunk -> {
    System.out.print(chunk);
});

// 方式 4: 自动学习模式
conv.setAutoLearn(true);  // 开启自动学习
ConversationMessage response = conv.chat(sessionId, "记住我的名字是小明");
// 知识库自动更新

// 方式 5: 获取对话分析
ConversationAnalysis analysis = conv.analyze(sessionId);
System.out.println("用户意图: " + analysis.getIntent());
System.out.println("情感倾向: " + analysis.getSentiment());
```

### 4.3 工具调用简化

```java
// 当前方式：需要手动处理 tool_call_id、历史消息格式等
ToolOrchestrator orchestrator = new ToolOrchestratorImpl(toolRegistry);
ToolCallResult result = orchestrator.executeToolCall(toolCall, context);
// 需要手动处理 tool_call_id、消息格式等

// 期望方式：自动处理所有细节
ConversationService conv = SE.getConversationService();

// 自动执行工具调用并记录
conv.executeToolCall(sessionId, "get_current_time", args);
// 自动记录到 FunctionCallLog
// 自动更新审计日志
// 自动更新知识库（如果配置）
```

## 5. 实现路线图

### Phase 1: SE 层接口定义
- [ ] 定义 ConversationService 接口
- [ ] 定义 ConversationSession 实体
- [ ] 定义 ConversationMessage 实体
- [ ] 定义 FunctionCallLog 实体

### Phase 2: SE 层实现
- [ ] 实现 ConversationServiceImpl
- [ ] 实现 ConversationStorageService
- [ ] 集成 AuditService
- [ ] 集成 ToolOrchestrator

### Phase 3: Skill 层适配
- [ ] 修改 ChatController 使用 ConversationService
- [ ] 移除重复的存储逻辑
- [ ] 简化工具调用流程

### Phase 4: 测试验证
- [ ] 单元测试
- [ ] 集成测试
- [ ] 性能测试

## 6. 数据流图

```
用户请求
    │
    ▼
ChatController.chat()
    │
    ▼
ConversationService.chat()
    │
    ├──▶ AuditService.log() ──────▶ 审计日志
    │
    ├──▶ ToolOrchestrator.executeToolCall()
    │       │
    │       ▼
    │   FunctionCallLog ──────▶ 工具调用记录
    │
    ├──▶ KnowledgeService.update() ─▶ 知识库更新
    │
    ▼
ConversationMessage ──────▶ 对话历史
```

## 7. 配置示例

```yaml
# application.yml
se:
  conversation:
    enabled: true
    storage:
      type: file  # file, memory, database
      path: ${user.home}/.ooder/data/conversations
    auto-learn: true
    max-history: 100
    audit:
      enabled: true
      include-tool-calls: true
    knowledge:
      auto-update: true
      min-content-length: 50
```

## 8. SE 功能完成情况

### 8.1 SE 已提供的功能

| 功能模块 | 接口 | 状态 | 说明 |
|----------|------|------|------|
| **LlmService** | `chat()`, `chatStream()`, `registerFunction()` | ✅ 完整 | LLM 服务接口 |
| **ChatRequest** | 消息、函数、流式支持 | ✅ 完整 | 聊天请求模型 |
| **ToolOrchestrator** | `executeToolCall()`, `parseToolCalls()` | ✅ 完整 | 工具编排器 |
| **AuditService** | `log()`, `query()`, `export()` | ✅ 完整 | 审计服务 |
| **ToolRegistry** | `register()`, `getToolDefinitions()` | ✅ 完整 | 工具注册表 |

### 8.2 SE 未提供的功能

| 功能模块 | 状态 | 说明 |
|----------|------|------|
| **ConversationService** | ❌ 缺失 | 对话会话管理 |
| **ConversationStorage** | ❌ 缺失 | 对话持久化存储 |
| **KnowledgeStorage** | ❌ 缺失 | 知识库持久化存储 |

### 8.3 当前策略

由于 SE 的 `ToolOrchestratorImpl` 是内部实现类，在 skill 插件的 ClassLoader 中无法直接访问：

1. **保留当前的 Function Calling 实现** - 因为 SE 的实现类无法在插件中直接使用
2. **等待 SE 提供 SPI 接口** - 让 SE 通过 SPI 暴露 `ToolOrchestrator` 服务
3. **保留 ChatStorageService 和 KnowledgeStorageService** - SE 尚未提供对话存储功能

## 9. 总结

通过将对话存储和 Function Calling 逻辑转移到 SE 层：

1. **统一管理** - SE 统一管理对话上下文，跨 Skill 共享
2. **自动审计** - 所有操作自动记录审计日志
3. **知识联动** - 工具结果自动更新知识库
4. **简化调用** - Skill 层只需一行代码完成对话
5. **可扩展性** - 后期可轻松迁移到数据库存储

### 当前状态

- ✅ SE 已提供 `LlmService`、`ToolOrchestrator`、`AuditService` 接口
- ❌ SE 未提供 `ConversationService`、`ConversationStorage`、`KnowledgeStorage`
- ⏳ 等待 SE 提供 SPI 接口后，skill-llm-chat 可以完全切换到 SE 实现
