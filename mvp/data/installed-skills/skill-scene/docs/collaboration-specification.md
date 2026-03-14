# Skills、Engine、SDK 协作说明文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 协作说明 |

---

## 一、现有 Skills 检查与修改任务

### 1.1 基础设施 Skills 检查结果

| Skill ID | 当前状态 | 需求满足度 | 缺失功能 | 修改优先级 |
|----------|---------|-----------|---------|-----------|
| **skill-vfs-database** | ✅ 基本满足 | 80% | 缺少向量存储扩展 | P2 |
| **skill-mqtt** | ✅ 完全满足 | 95% | 无 | - |
| **skill-user-auth** | ⚠️ 部分满足 | 60% | 缺少角色识别接口 | P0 |
| **skill-vfs-local** | ✅ 基本满足 | 85% | 无 | - |
| **skill-health** | ✅ 完全满足 | 95% | 无 | - |
| **skill-monitor** | ✅ 完全满足 | 90% | 无 | - |

### 1.2 LLM 相关 Skills 检查结果

| Skill ID | 当前状态 | 需求满足度 | 缺失功能 | 修改优先级 |
|----------|---------|-----------|---------|-----------|
| **skill-llm-conversation** | ⚠️ 部分满足 | 70% | 缺少工具调用、结构化输出 | P0 |
| **skill-llm-context-builder** | ⚠️ 部分满足 | 60% | 缺少安装上下文管理 | P0 |
| **skill-llm-config-manager** | ⚠️ 部分满足 | 50% | 缺少多模型配置、降级策略 | P1 |
| **skill-knowledge-base** | ⚠️ 部分满足 | 40% | 缺少预制知识、向量同步 | P0 |
| **skill-rag** | ⚠️ 部分满足 | 50% | 缺少安装场景RAG | P1 |

### 1.3 Skills 修改任务清单

#### P0 优先级修改任务

```yaml
# SKILL-MOD-001: skill-llm-conversation 扩展
Task:
  id: SKILL-MOD-001
  skillId: skill-llm-conversation
  name: LLM对话服务扩展
  
  Requirements:
    - 新增工具调用能力 (Tool Calling)
    - 新增结构化输出能力 (Structured Output)
    - 新增上下文模板管理
    - 新增降级策略支持
    
  CodeChanges:
    - 新增 ToolCaller.java
    - 新增 StructuredOutputParser.java
    - 新增 ContextTemplateManager.java
    - 新增 DegradationHandler.java
    
  ConfigChanges:
    skill.yaml:
      - 新增 capabilities: tool-calling, structured-output
      - 新增 config.optional: tools, schemas
      
  EstimatedEffort: 7天
  
# SKILL-MOD-002: skill-knowledge-base 扩展
Task:
  id: SKILL-MOD-002
  skillId: skill-knowledge-base
  name: 知识库服务扩展
  
  Requirements:
    - 新增预制知识导入
    - 新增向量同步接口
    - 新增知识库维护API
    - 新增安装场景知识模板
    
  CodeChanges:
    - 新增 PreloadedKnowledgeImporter.java
    - 新增 VectorSyncService.java
    - 新增 KnowledgeMaintenanceController.java
    
  ConfigChanges:
    skill.yaml:
      - 新增 capabilities: knowledge-import, vector-sync
      - 新增 endpoints: /api/knowledge/import, /api/knowledge/sync
      
  EstimatedEffort: 5天
  
# SKILL-MOD-003: skill-user-auth 扩展
Task:
  id: SKILL-MOD-003
  skillId: skill-user-auth
  name: 用户认证服务扩展
  
  Requirements:
    - 新增角色识别接口
    - 新增角色配置管理
    - 新增角色菜单绑定
    
  CodeChanges:
    - 新增 RoleDetectionService.java
    - 新增 RoleConfigManager.java
    - 新增 RoleMenuBindingService.java
    
  ConfigChanges:
    skill.yaml:
      - 新增 capabilities: role-detection, role-config
      
  EstimatedEffort: 5天
```

#### P1 优先级修改任务

```yaml
# SKILL-MOD-004: skill-llm-config-manager 扩展
Task:
  id: SKILL-MOD-004
  skillId: skill-llm-config-manager
  name: LLM配置管理扩展
  
  Requirements:
    - 新增多模型配置
    - 新增模型分配策略
    - 新增降级策略配置
    
  EstimatedEffort: 5天
  
# SKILL-MOD-005: skill-rag 扩展
Task:
  id: SKILL-MOD-005
  skillId: skill-rag
  name: RAG服务扩展
  
  Requirements:
    - 新增安装场景RAG模板
    - 新增向量检索增强
    
  EstimatedEffort: 3天
```

---

## 二、Engine 支持能力检查与协作说明

### 2.1 Engine 当前支持能力

| 能力 | 支持状态 | 说明 |
|------|---------|------|
| **场景组管理** | ✅ 支持 | SceneGroupService |
| **能力注册** | ✅ 支持 | CapabilityRegistry |
| **能力绑定** | ✅ 支持 | CapabilityBindingService |
| **能力调用** | ✅ 支持 | CapabilityInvoker |
| **自驱配置** | ✅ 支持 | MainFirstConfig |
| **依赖管理** | ✅ 支持 | Dependencies |
| **角色管理** | ⚠️ 部分支持 | Participant，缺少角色菜单 |
| **激活流程** | ❌ 不支持 | 需要新增 |
| **菜单生成** | ❌ 不支持 | 需要新增 |
| **LLM上下文** | ❌ 不支持 | 需要新增 |
| **工具调用** | ❌ 不支持 | 需要新增 |

### 2.2 Engine 需要新增的能力

```yaml
EngineEnhancements:
  # E-001: 激活流程引擎
  - id: E-001
    name: 激活流程引擎
    priority: P0
    
    Requirements:
      - 支持按角色定义激活步骤
      - 支持步骤状态管理
      - 支持步骤回滚
      - 支持激活进度查询
      
    Deliverables:
      - ActivationFlowEngine.java
      - ActivationStepExecutor.java
      - ActivationStateManager.java
      
    Collaboration:
      - 与 SceneGroupService 协作管理场景组
      - 与 CapabilityBindingService 协作绑定能力
      
  # E-002: 菜单生成引擎
  - id: E-002
    name: 菜单生成引擎
    priority: P0
    
    Requirements:
      - 支持按角色生成菜单
      - 支持动态菜单配置
      - 支持菜单权限控制
      
    Deliverables:
      - MenuGenerator.java
      - MenuConfigManager.java
      - MenuPermissionChecker.java
      
    Collaboration:
      - 与 RoleDetectionService 协作识别角色
      - 与 CapabilityService 协作获取能力菜单
      
  # E-003: LLM上下文引擎
  - id: E-003
    name: LLM上下文引擎
    priority: P0
    
    Requirements:
      - 支持安装上下文管理
      - 支持上下文模板
      - 支持上下文隔离
      
    Deliverables:
      - LLMContextEngine.java
      - ContextTemplateManager.java
      - ContextIsolationManager.java
      
    Collaboration:
      - 与 LLM-SDK 协作管理LLM交互
      - 与 KnowledgeBaseService 协作获取知识上下文
      
  # E-004: 工具调用引擎
  - id: E-004
    name: 工具调用引擎
    priority: P0
    
    Requirements:
      - 支持工具注册
      - 支持工具调用
      - 支持工具权限控制
      
    Deliverables:
      - ToolRegistry.java
      - ToolInvoker.java
      - ToolPermissionChecker.java
      
    Collaboration:
      - 与 LLM-SDK 协作LLM工具调用
      - 与 CapabilityService 协作能力调用
```

### 2.3 Engine 协作接口定义

```java
// Engine 对外协作接口
public interface SceneEngineCollaborationApi {
    
    // 激活流程接口
    ActivationResult startActivation(String sceneGroupId, ActivationRequest request);
    ActivationStatus getActivationStatus(String sceneGroupId);
    StepResult executeStep(String sceneGroupId, String stepId, Map<String, Object> params);
    void rollbackActivation(String sceneGroupId);
    
    // 菜单生成接口
    List<MenuItem> generateMenus(String sceneGroupId, String roleId);
    void updateMenuConfig(String sceneGroupId, MenuConfig config);
    
    // LLM上下文接口
    String createLLMContext(String sessionId, LLMContextConfig config);
    void updateLLMContext(String sessionId, Map<String, Object> state);
    LLMContext getLLMContext(String sessionId);
    void destroyLLMContext(String sessionId);
    
    // 工具调用接口
    void registerTool(ToolDefinition tool);
    ToolResult invokeTool(String toolId, Map<String, Object> params);
    List<ToolDefinition> getAvailableTools(String roleId);
}
```

---

## 三、LLM-SDK 和 AGENT-SDK 需求检查与协作说明

### 3.1 LLM-SDK 当前状态

| 接口 | 状态 | 说明 |
|------|------|------|
| `LlmService` | ✅ 已有 | 统一LLM服务接口 |
| `LlmConfig` | ✅ 已有 | LLM配置管理 |
| `ChatRequest` | ✅ 已有 | 对话请求模型 |
| `FunctionDef` | ✅ 已有 | 函数调用定义 |
| `TokenUsage` | ✅ 已有 | Token使用统计 |
| `AbstractLlmDriver` | ✅ 已有 | LLM驱动抽象基类 |
| `MemoryStore` | ✅ 已有 | 记忆存储接口 |
| `ConversationMemory` | ✅ 已有 | 对话记忆接口 |
| **工具调用接口** | ❌ 缺失 | 需要新增 |
| **结构化输出接口** | ❌ 缺失 | 需要新增 |
| **上下文模板接口** | ❌ 缺失 | 需要新增 |
| **降级策略接口** | ❌ 缺失 | 需要新增 |

### 3.2 LLM-SDK 需要新增的接口

```java
// LLM-SDK 新增接口定义
package net.ooder.sdk.llm;

// 工具调用接口
public interface ToolCallingApi {
    
    // 注册工具
    void registerTool(ToolDefinition tool);
    
    // 获取可用工具
    List<ToolDefinition> getAvailableTools();
    
    // 执行工具调用
    ToolCallResult executeToolCall(String toolId, Map<String, Object> params);
    
    // LLM工具调用
    LlmResponse chatWithTools(ChatRequest request, List<ToolDefinition> tools);
}

// 结构化输出接口
public interface StructuredOutputApi {
    
    // 获取结构化输出
    <T> T getStructuredOutput(String prompt, Class<T> schema);
    
    // 获取结构化输出（带上下文）
    <T> T getStructuredOutputWithContext(String prompt, Class<T> schema, String contextId);
    
    // 验证输出格式
    <T> ValidationResult validateOutput(T output, Class<T> schema);
}

// 上下文模板接口
public interface ContextTemplateApi {
    
    // 创建上下文模板
    ContextTemplate createTemplate(ContextTemplateRequest request);
    
    // 获取上下文模板
    ContextTemplate getTemplate(String templateId);
    
    // 应用上下文模板
    String applyTemplate(String templateId, Map<String, Object> variables);
    
    // 管理上下文
    String createContext(ContextConfig config);
    void updateContext(String contextId, Map<String, Object> state);
    LLMContext getContext(String contextId);
    void destroyContext(String contextId);
}

// 降级策略接口
public interface DegradationApi {
    
    // 注册降级策略
    void registerStrategy(DegradationStrategy strategy);
    
    // 获取当前降级级别
    DegradationLevel getCurrentLevel();
    
    // 执行降级
    void degrade(DegradationTrigger trigger);
    
    // 恢复正常
    void recover();
    
    // 获取降级配置
    DegradationConfig getConfig();
}
```

### 3.3 AGENT-SDK 当前状态

| 接口 | 状态 | 说明 |
|------|------|------|
| 组网能力 | ✅ 已有 | P2P组网 |
| 消息传递 | ✅ 已有 | MQTT消息 |
| Agent发现 | ✅ 已有 | Agent发现 |
| 命令执行 | ✅ 已有 | 命令执行 |
| **场景协作接口** | ❌ 缺失 | 需要新增 |
| **能力调用接口** | ❌ 缺失 | 需要新增 |
| **LLM协作接口** | ❌ 缺失 | 需要新增 |

### 3.4 AGENT-SDK 需要新增的接口

```java
// AGENT-SDK 新增接口定义
package net.ooder.sdk.agent;

// 场景协作接口
public interface SceneCollaborationApi {
    
    // 加入场景组
    void joinSceneGroup(String sceneGroupId, String roleId);
    
    // 离开场景组
    void leaveSceneGroup(String sceneGroupId);
    
    // 获取场景组信息
    SceneGroupInfo getSceneGroupInfo(String sceneGroupId);
    
    // 获取场景组能力
    List<CapabilityInfo> getSceneGroupCapabilities(String sceneGroupId);
    
    // 场景组事件订阅
    void subscribeSceneEvents(String sceneGroupId, EventHandler handler);
}

// 能力调用接口
public interface CapabilityInvocationApi {
    
    // 发现能力
    List<CapabilityInfo> discoverCapabilities(String sceneType);
    
    // 调用能力
    CapabilityResult invokeCapability(String capabilityId, Map<String, Object> params);
    
    // 异步调用能力
    CompletableFuture<CapabilityResult> invokeCapabilityAsync(String capabilityId, Map<String, Object> params);
    
    // 获取能力状态
    CapabilityStatus getCapabilityStatus(String capabilityId);
}

// LLM协作接口
public interface LLMCollaborationApi {
    
    // 请求LLM能力
    LlmCapabilityAllocation requestLLMCapability(LlmCapabilityRequest request);
    
    // 释放LLM能力
    void releaseLLMCapability(String allocationId);
    
    // LLM对话
    LlmResponse chat(LlmChatRequest request);
    
    // 流式LLM对话
    Flux<LlmChunk> chatStream(LlmChatRequest request);
}
```

### 3.5 SDK 协作关系图

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           SDK 协作关系                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Skills Layer                                     │    │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐               │    │
│  │  │ skill-llm-*   │  │ skill-scene   │  │ skill-kb-*    │               │    │
│  │  └───────┬───────┘  └───────┬───────┘  └───────┬───────┘               │    │
│  │          │                  │                  │                        │    │
│  └──────────┼──────────────────┼──────────────────┼────────────────────────┘    │
│             │                  │                  │                             │
│             ▼                  ▼                  ▼                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         SDK Layer                                        │    │
│  │                                                                         │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │                     LLM-SDK                                      │   │    │
│  │  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │   │    │
│  │  │  │ToolCalling  │ │Structured   │ │Context      │ │Degradation│ │   │    │
│  │  │  │Api          │ │OutputApi    │ │TemplateApi  │ │Api        │ │   │    │
│  │  │  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                         │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │                     AGENT-SDK                                    │   │    │
│  │  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │   │    │
│  │  │  │Scene        │ │Capability   │ │LLM          │               │   │    │
│  │  │  │Collaboration│ │Invocation   │ │Collaboration│               │   │    │
│  │  │  │Api          │ │Api          │ │Api          │               │   │    │
│  │  │  └─────────────┘ └─────────────┘ └─────────────┘               │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│             │                  │                  │                             │
│             ▼                  ▼                  ▼                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Engine Layer                                     │    │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐               │    │
│  │  │ Activation    │  │ Menu          │  │ LLM Context   │               │    │
│  │  │ Engine        │  │ Generator     │  │ Engine        │               │    │
│  │  └───────────────┘  └───────────────┘  └───────────────┘               │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、协作任务分配

### 4.1 Skills 团队任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| SKILL-MOD-001 | skill-llm-conversation 扩展 | P0 | 7天 | SDK-NEW-001 |
| SKILL-MOD-002 | skill-knowledge-base 扩展 | P0 | 5天 | - |
| SKILL-MOD-003 | skill-user-auth 扩展 | P0 | 5天 | - |
| SKILL-MOD-004 | skill-llm-config-manager 扩展 | P1 | 5天 | SDK-NEW-004 |
| SKILL-MOD-005 | skill-rag 扩展 | P1 | 3天 | SKILL-MOD-002 |

### 4.2 Engine 团队任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| E-001 | 激活流程引擎 | P0 | 10天 | - |
| E-002 | 菜单生成引擎 | P0 | 7天 | SKILL-MOD-003 |
| E-003 | LLM上下文引擎 | P0 | 7天 | SDK-NEW-003 |
| E-004 | 工具调用引擎 | P0 | 7天 | SDK-NEW-001 |

### 4.3 SDK 团队任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| SDK-NEW-001 | ToolCallingApi | P0 | 5天 | - |
| SDK-NEW-002 | StructuredOutputApi | P0 | 3天 | - |
| SDK-NEW-003 | ContextTemplateApi | P0 | 5天 | - |
| SDK-NEW-004 | DegradationApi | P0 | 3天 | - |
| SDK-NEW-005 | SceneCollaborationApi | P1 | 5天 | E-001 |
| SDK-NEW-006 | CapabilityInvocationApi | P1 | 3天 | - |
| SDK-NEW-007 | LLMCollaborationApi | P1 | 5天 | SDK-NEW-001~004 |

---

## 五、协作时间线

```
Week 1-2: SDK 基础接口
├── SDK-NEW-001: ToolCallingApi
├── SDK-NEW-002: StructuredOutputApi
├── SDK-NEW-003: ContextTemplateApi
└── SDK-NEW-004: DegradationApi

Week 3-4: Skills 扩展
├── SKILL-MOD-001: skill-llm-conversation 扩展
├── SKILL-MOD-002: skill-knowledge-base 扩展
└── SKILL-MOD-003: skill-user-auth 扩展

Week 5-6: Engine 引擎
├── E-001: 激活流程引擎
├── E-002: 菜单生成引擎
├── E-003: LLM上下文引擎
└── E-004: 工具调用引擎

Week 7-8: SDK 协作接口
├── SDK-NEW-005: SceneCollaborationApi
├── SDK-NEW-006: CapabilityInvocationApi
└── SDK-NEW-007: LLMCollaborationApi

Week 9-10: 集成测试
├── Skills + SDK 集成测试
├── Engine + SDK 集成测试
└── 端到端测试
```

---

## 六、接口版本兼容性

### 6.1 版本策略

| 组件 | 当前版本 | 目标版本 | 兼容性 |
|------|---------|---------|--------|
| LLM-SDK | 1.0.0 | 2.0.0 | 向后兼容 |
| AGENT-SDK | 1.0.0 | 2.0.0 | 向后兼容 |
| skill-scene | 2.3.0 | 2.4.0 | 向后兼容 |

### 6.2 迁移策略

```yaml
MigrationStrategy:
  # 阶段1：并行运行
  phase1:
    description: "新旧接口并行运行"
    duration: "2周"
    actions:
      - 新增接口标记为 @Beta
      - 旧接口继续支持
      - 提供迁移文档
      
  # 阶段2：渐进迁移
  phase2:
    description: "渐进式迁移到新接口"
    duration: "4周"
    actions:
      - 新接口标记为 @Stable
      - 旧接口标记为 @Deprecated
      - 提供迁移工具
      
  # 阶段3：完全迁移
  phase3:
    description: "完全迁移到新接口"
    duration: "2周"
    actions:
      - 移除旧接口
      - 更新文档
```

---

## 七、总结

### 7.1 关键协作点

| 协作点 | Skills | Engine | SDK |
|--------|--------|--------|-----|
| 工具调用 | skill-llm-conversation | ToolInvoker | ToolCallingApi |
| 结构化输出 | skill-llm-conversation | - | StructuredOutputApi |
| 激活流程 | - | ActivationEngine | SceneCollaborationApi |
| 菜单生成 | - | MenuGenerator | - |
| LLM上下文 | skill-llm-context-builder | LLMContextEngine | ContextTemplateApi |
| 能力调用 | skill-scene | CapabilityInvoker | CapabilityInvocationApi |

### 7.2 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| SDK接口变更 | 高 | 版本兼容、渐进迁移 |
| Engine性能问题 | 中 | 性能测试、优化 |
| Skills依赖冲突 | 中 | 依赖隔离、版本管理 |

---

**文档状态**: 协作说明  
**下一步**: 启动SDK基础接口开发
