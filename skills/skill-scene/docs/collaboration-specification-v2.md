# Skills、Engine、SDK 协作说明文档 V2

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v2.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 协作说明（修正版） |

---

## 一、核心架构理解

### 1.1 架构分层

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           系统架构分层                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Layer 4: 应用层 (Application Layer)                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  场景技能 (Scene Skills) - ABS/ASS/TBS                                  │    │
│  │  ├─ skill-daily-report (日志汇报)                                       │    │
│  │  ├─ skill-recruitment (招聘管理)                                        │    │
│  │  ├─ skill-document-assistant (文档助手)                                 │    │
│  │  └─ ...                                                                 │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  Layer 3: 服务层 (Service Layer)                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  服务技能 (Service Skills)                                              │    │
│  │  ├─ skill-scene (场景管理)                                              │    │
│  │  ├─ skill-capability (能力管理)                                         │    │
│  │  ├─ skill-management (技能管理)                                         │    │
│  │  ├─ skill-llm-conversation (LLM对话)                                    │    │
│  │  ├─ skill-knowledge-base (知识库)                                       │    │
│  │  ├─ skill-rag (RAG服务)                                                 │    │
│  │  └─ ...                                                                 │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  Layer 2: 基础设施层 (Infrastructure Layer)                                     │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  基础设施技能 (Infrastructure Skills)                                   │    │
│  │  ├─ skill-user-auth (用户认证)                                          │    │
│  │  ├─ skill-org-base (本地组织)                                           │    │
│  │  ├─ skill-org-feishu/dingding/wecom (第三方组织)                        │    │
│  │  ├─ skill-vfs-local/minio/oss/s3 (文件存储)                             │    │
│  │  ├─ skill-vfs-database (数据库存储 - 不是向量库!)                       │    │
│  │  ├─ skill-mqtt (消息服务)                                               │    │
│  │  ├─ skill-health (健康检查)                                             │    │
│  │  └─ skill-monitor (监控服务)                                            │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 关键认知修正

| 之前的误解 | 正确理解 |
|-----------|---------|
| skill-vfs-database 是向量数据库 | ❌ 错误！它是VFS的数据库存储实现，用于文件存储 |
| skill-user-auth 管理角色 | ❌ 错误！它只负责用户认证，角色管理在 skill-org-base |
| 向量存储在 skill-vfs-database | ❌ 错误！向量存储应该在 skill-knowledge-base 或 skill-rag |
| 角色识别需要修改 skill-user-auth | ❌ 错误！角色识别应该在 skill-scene 或 skill-org-base |

---

## 二、现有 Skills 检查与修改任务（修正版）

### 2.1 基础设施 Skills 检查

| Skill ID | 实际功能 | 需求满足度 | 是否需要修改 | 说明 |
|----------|---------|-----------|-------------|------|
| **skill-vfs-database** | 文件数据库存储 | 100% | ❌ 不需要 | 用于存储文件，非向量存储 |
| **skill-mqtt** | 消息推送服务 | 95% | ❌ 不需要 | 完全满足需求 |
| **skill-user-auth** | 用户认证(Token/Session) | 100% | ❌ 不需要 | 只负责认证，不负责角色 |
| **skill-org-base** | 本地组织+角色管理 | 70% | ✅ 需要扩展 | 需要扩展角色识别接口 |
| **skill-vfs-local** | 本地文件存储 | 100% | ❌ 不需要 | 完全满足需求 |
| **skill-health** | 健康检查 | 100% | ❌ 不需要 | 完全满足需求 |
| **skill-monitor** | 监控服务 | 100% | ❌ 不需要 | 完全满足需求 |

### 2.2 LLM/RAG/知识库 Skills 检查

| Skill ID | 实际功能 | 需求满足度 | 是否需要修改 | 说明 |
|----------|---------|-----------|-------------|------|
| **skill-llm-conversation** | LLM对话服务 | 60% | ✅ 需要扩展 | 缺少工具调用、结构化输出 |
| **skill-llm-context-builder** | 上下文构建 | 50% | ✅ 需要扩展 | 缺少安装上下文管理 |
| **skill-llm-config-manager** | LLM配置管理 | 40% | ✅ 需要扩展 | 缺少多模型配置、降级策略 |
| **skill-knowledge-base** | 知识库服务 | 30% | ✅ 需要扩展 | 缺少向量存储、RAG能力 |
| **skill-rag** | RAG检索服务 | 40% | ✅ 需要扩展 | 缺少向量检索、知识库集成 |

### 2.3 关键发现：向量存储在哪里？

```
问题：向量存储应该由哪个 Skill 提供？

分析：
1. skill-vfs-database → 文件存储（不是向量存储）
2. skill-knowledge-base → 知识库，应该有向量存储
3. skill-rag → RAG服务，应该有向量检索

结论：
- 向量存储应该在 skill-knowledge-base 中实现
- 或者新建 skill-vector-store 专门提供向量存储
- skill-rag 应该依赖 skill-knowledge-base 的向量存储
```

### 2.4 Skills 修改任务清单（修正版）

#### P0 优先级修改任务

```yaml
# SKILL-MOD-001: skill-org-base 扩展（角色识别）
Task:
  id: SKILL-MOD-001
  skillId: skill-org-base
  name: 本地组织服务扩展 - 角色识别
  
  CurrentState:
    capabilities:
      - user.auth      # 用户认证
      - user.manage    # 用户管理
      - org.manage     # 组织管理
      - role.manage    # 角色管理（已有，但缺少识别接口）
      - sync           # 数据同步
      
  RequiredAdditions:
    capabilities:
      - role.detect    # 新增：角色识别
      - role.config    # 新增：角色配置
      
    endpoints:
      - POST /api/org/role/detect          # 根据用户属性识别角色
      - GET /api/org/role/config/{userId}  # 获取用户角色配置
      - POST /api/org/role/menu/{roleId}   # 获取角色菜单
      
    codeChanges:
      - RoleDetectionService.java          # 角色识别服务
      - RoleConfigService.java             # 角色配置服务
      - RoleMenuService.java               # 角色菜单服务
      
  EstimatedEffort: 5天
  Priority: P0
  Reason: 场景技能激活需要角色识别
  
# SKILL-MOD-002: skill-knowledge-base 扩展（向量存储）
Task:
  id: SKILL-MOD-002
  skillId: skill-knowledge-base
  name: 知识库服务扩展 - 向量存储与RAG
  
  CurrentState:
    description: "知识库核心服务"
    capabilities: []  # 当前能力列表为空或基础
    
  RequiredAdditions:
    capabilities:
      - knowledge.store      # 知识存储
      - knowledge.query      # 知识查询
      - vector.store         # 新增：向量存储
      - vector.search        # 新增：向量检索
      - rag.retrieve         # 新增：RAG检索
      - rag.augment          # 新增：RAG增强
      
    implementation:
      - 集成 SQLite-Vec 作为默认向量存储
      - 支持可选的向量数据库（如Milvus、Pinecone）
      - 实现文档切分和向量化
      - 实现语义检索
      
    codeChanges:
      - VectorStoreService.java            # 向量存储服务
      - VectorSearchService.java           # 向量检索服务
      - DocumentEmbeddingService.java      # 文档向量化服务
      - RAGService.java                    # RAG服务
      
  EstimatedEffort: 10天
  Priority: P0
  Reason: LLM安装激活需要RAG能力
  
# SKILL-MOD-003: skill-llm-conversation 扩展（工具调用）
Task:
  id: SKILL-MOD-003
  skillId: skill-llm-conversation
  name: LLM对话服务扩展 - 工具调用与结构化输出
  
  CurrentState:
    capabilities:
      - llm-chat           # LLM对话
      - context-manage     # 上下文管理
      
  RequiredAdditions:
    capabilities:
      - llm-tool-call      # 新增：工具调用
      - llm-structured     # 新增：结构化输出
      - llm-degrade        # 新增：降级处理
      
    implementation:
      - 支持 Function Calling
      - 支持 JSON Schema 输出
      - 支持多步工具调用
      - 支持降级策略
      
    codeChanges:
      - ToolCallingService.java            # 工具调用服务
      - StructuredOutputParser.java        # 结构化输出解析
      - LLMDegradationHandler.java         # 降级处理
      
  EstimatedEffort: 7天
  Priority: P0
  Reason: LLM安装激活需要工具调用
```

#### P1 优先级修改任务

```yaml
# SKILL-MOD-004: skill-llm-context-builder 扩展
Task:
  id: SKILL-MOD-004
  skillId: skill-llm-context-builder
  name: LLM上下文构建器扩展 - 安装上下文
  
  RequiredAdditions:
    capabilities:
      - context.install      # 新增：安装上下文
      - context.template     # 新增：上下文模板
      
  EstimatedEffort: 5天
  
# SKILL-MOD-005: skill-llm-config-manager 扩展
Task:
  id: SKILL-MOD-005
  skillId: skill-llm-config-manager
  name: LLM配置管理器扩展 - 多模型与降级
  
  RequiredAdditions:
    capabilities:
      - config.multi-model   # 新增：多模型配置
      - config.degradation   # 新增：降级策略配置
      
  EstimatedEffort: 5天
  
# SKILL-MOD-006: skill-rag 扩展
Task:
  id: SKILL-MOD-006
  skillId: skill-rag
  name: RAG服务扩展 - 依赖知识库
  
  RequiredChanges:
    dependencies:
      - skill-knowledge-base   # 依赖知识库的向量存储
      
    capabilities:
      - rag.query            # 使用知识库的向量检索
      
  EstimatedEffort: 3天
```

---

## 三、Engine 支持能力检查与协作说明（修正版）

### 3.1 Engine 当前支持能力

| 能力 | 支持状态 | 说明 | 是否需要新增 |
|------|---------|------|-------------|
| **场景组管理** | ✅ 支持 | SceneGroupService | ❌ 不需要 |
| **能力注册** | ✅ 支持 | CapabilityRegistry | ❌ 不需要 |
| **能力绑定** | ✅ 支持 | CapabilityBindingService | ❌ 不需要 |
| **能力调用** | ✅ 支持 | CapabilityInvoker | ❌ 不需要 |
| **自驱配置** | ✅ 支持 | MainFirstConfig | ❌ 不需要 |
| **依赖管理** | ✅ 支持 | Dependencies | ❌ 不需要 |
| **参与者管理** | ✅ 支持 | Participant | ❌ 不需要 |
| **激活流程** | ❌ 不支持 | 需要新增 | ✅ 需要 |
| **菜单生成** | ❌ 不支持 | 需要新增 | ✅ 需要 |
| **LLM上下文** | ❌ 不支持 | 需要新增 | ✅ 需要 |
| **工具调用** | ❌ 不支持 | 需要新增 | ✅ 需要 |

### 3.2 Engine 需要新增的能力

```yaml
EngineEnhancements:
  # E-001: 激活流程引擎
  - id: E-001
    name: 激活流程引擎
    priority: P0
    
    Description: |
      场景技能激活流程的管理引擎，支持多角色、多步骤的激活流程
      
    Requirements:
      - 支持按角色定义激活步骤
      - 支持步骤状态管理（PENDING/IN_PROGRESS/COMPLETED/SKIPPED）
      - 支持步骤依赖（步骤A完成后才能执行步骤B）
      - 支持步骤回滚
      - 支持激活进度查询
      - 支持激活历史记录
      
    Collaboration:
      - 与 skill-org-base 协作获取角色信息
      - 与 skill-scene 协作管理场景组
      - 与 skill-capability 协作绑定能力
      
    Deliverables:
      - ActivationFlowEngine.java
      - ActivationStepExecutor.java
      - ActivationStateManager.java
      - ActivationHistoryService.java
      
    EstimatedEffort: 10天
    
  # E-002: 菜单生成引擎
  - id: E-002
    name: 菜单生成引擎
    priority: P0
    
    Description: |
      根据角色和场景配置动态生成用户菜单
      
    Requirements:
      - 支持按角色生成菜单
      - 支持动态菜单配置（运行时修改）
      - 支持菜单权限控制
      - 支持菜单排序和分组
      - 支持菜单图标和URL配置
      
    Collaboration:
      - 与 skill-org-base 协作识别用户角色
      - 与 skill-scene 协作获取场景能力菜单
      
    Deliverables:
      - MenuGenerator.java
      - MenuConfigManager.java
      - MenuPermissionChecker.java
      
    EstimatedEffort: 7天
    
  # E-003: LLM上下文引擎
  - id: E-003
    name: LLM上下文引擎
    priority: P0
    
    Description: |
      管理LLM对话上下文，支持安装过程中的上下文隔离
      
    Requirements:
      - 支持上下文创建、更新、销毁
      - 支持上下文模板（预定义上下文结构）
      - 支持上下文隔离（不同安装会话隔离）
      - 支持上下文持久化
      - 支持上下文历史记录
      
    Collaboration:
      - 与 skill-llm-context-builder 协作构建上下文
      - 与 skill-llm-conversation 协作管理LLM对话
      
    Deliverables:
      - LLMContextEngine.java
      - ContextTemplateManager.java
      - ContextIsolationManager.java
      - ContextPersistenceService.java
      
    EstimatedEffort: 7天
    
  # E-004: 工具调用引擎
  - id: E-004
    name: 工具调用引擎
    priority: P0
    
    Description: |
      管理工具注册和调用，支持LLM工具调用
      
    Requirements:
      - 支持工具注册和注销
      - 支持工具调用执行
      - 支持工具权限控制
      - 支持工具调用日志
      - 支持工具调用重试
      
    Collaboration:
      - 与 skill-llm-conversation 协作LLM工具调用
      - 与各服务技能协作提供工具能力
      
    Deliverables:
      - ToolRegistry.java
      - ToolInvoker.java
      - ToolPermissionChecker.java
      - ToolCallLogger.java
      
    EstimatedEffort: 7天
```

### 3.3 Engine 协作接口定义

```java
// Engine 对外协作接口
public interface SceneEngineCollaborationApi {
    
    // ==================== 激活流程接口 ====================
    
    /**
     * 开始场景激活流程
     */
    ActivationResult startActivation(String sceneGroupId, ActivationRequest request);
    
    /**
     * 获取激活状态
     */
    ActivationStatus getActivationStatus(String sceneGroupId);
    
    /**
     * 执行激活步骤
     */
    StepResult executeStep(String sceneGroupId, String stepId, Map<String, Object> params);
    
    /**
     * 跳过激活步骤
     */
    StepResult skipStep(String sceneGroupId, String stepId, String reason);
    
    /**
     * 回滚激活流程
     */
    void rollbackActivation(String sceneGroupId, String rollbackToStepId);
    
    // ==================== 菜单生成接口 ====================
    
    /**
     * 生成角色菜单
     */
    List<MenuItem> generateMenus(String sceneGroupId, String userId, String roleId);
    
    /**
     * 更新菜单配置
     */
    void updateMenuConfig(String sceneGroupId, String roleId, MenuConfig config);
    
    /**
     * 获取菜单项
     */
    MenuItem getMenuItem(String sceneGroupId, String menuId);
    
    // ==================== LLM上下文接口 ====================
    
    /**
     * 创建LLM上下文
     */
    String createLLMContext(String sessionId, LLMContextConfig config);
    
    /**
     * 更新LLM上下文状态
     */
    void updateLLMContext(String sessionId, Map<String, Object> state);
    
    /**
     * 获取LLM上下文
     */
    LLMContext getLLMContext(String sessionId);
    
    /**
     * 销毁LLM上下文
     */
    void destroyLLMContext(String sessionId);
    
    /**
     * 应用上下文模板
     */
    String applyContextTemplate(String templateId, Map<String, Object> variables);
    
    // ==================== 工具调用接口 ====================
    
    /**
     * 注册工具
     */
    void registerTool(ToolDefinition tool);
    
    /**
     * 注销工具
     */
    void unregisterTool(String toolId);
    
    /**
     * 调用工具
     */
    ToolResult invokeTool(String toolId, Map<String, Object> params);
    
    /**
     * 获取可用工具列表
     */
    List<ToolDefinition> getAvailableTools(String roleId);
    
    /**
     * 获取工具定义
     */
    ToolDefinition getToolDefinition(String toolId);
}
```

---

## 四、LLM-SDK 和 AGENT-SDK 需求检查（修正版）

### 4.1 LLM-SDK 当前状态分析

根据 LLM_REQUIREMENTS_SPECIFICATION.md 和 SDK_COVERAGE_ANALYSIS.md：

| 接口/功能 | 当前状态 | 需求满足度 | 是否需要新增 | 说明 |
|----------|---------|-----------|-------------|------|
| **LlmService** | ✅ 已有 | 100% | ❌ 不需要 | 统一LLM服务接口 |
| **LlmConfig** | ✅ 已有 | 80% | ⚠️ 需要扩展 | 缺少多模型配置、降级策略 |
| **ChatRequest/Response** | ✅ 已有 | 100% | ❌ 不需要 | 对话请求/响应模型 |
| **FunctionDef** | ✅ 已有 | 60% | ⚠️ 需要扩展 | 需要支持更多工具类型 |
| **MemoryStore** | ✅ 已有 | 100% | ❌ 不需要 | 记忆存储接口 |
| **ConversationMemory** | ✅ 已有 | 100% | ❌ 不需要 | 对话记忆接口 |
| **AbstractLlmDriver** | ✅ 已有 | 100% | ❌ 不需要 | LLM驱动抽象基类 |
| **TokenUsage** | ✅ 已有 | 100% | ❌ 不需要 | Token使用统计 |
| **工具调用执行** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 ToolCallingApi |
| **结构化输出解析** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 StructuredOutputApi |
| **上下文模板管理** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 ContextTemplateApi |
| **降级策略管理** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 DegradationApi |

### 4.2 LLM-SDK 需要新增的接口

```java
package net.ooder.sdk.llm;

// ==================== 工具调用接口 ====================

/**
 * 工具调用API
 * 支持LLM Function Calling
 */
public interface ToolCallingApi {
    
    /**
     * 注册工具
     */
    void registerTool(ToolDefinition tool);
    
    /**
     * 批量注册工具
     */
    void registerTools(List<ToolDefinition> tools);
    
    /**
     * 注销工具
     */
    void unregisterTool(String toolId);
    
    /**
     * 获取可用工具列表
     */
    List<ToolDefinition> getAvailableTools();
    
    /**
     * 获取可用工具列表（按角色过滤）
     */
    List<ToolDefinition> getAvailableTools(String roleId);
    
    /**
     * 执行工具调用
     */
    ToolCallResult executeToolCall(String toolId, Map<String, Object> params);
    
    /**
     * 执行工具调用（异步）
     */
    CompletableFuture<ToolCallResult> executeToolCallAsync(String toolId, Map<String, Object> params);
    
    /**
     * LLM对话 with 工具
     */
    LlmResponse chatWithTools(ChatRequest request, List<ToolDefinition> tools);
    
    /**
     * LLM对话 with 工具（流式）
     */
    Flux<LlmChunk> chatWithToolsStream(ChatRequest request, List<ToolDefinition> tools);
}

// ==================== 结构化输出接口 ====================

/**
 * 结构化输出API
 * 支持JSON Schema输出
 */
public interface StructuredOutputApi {
    
    /**
     * 获取结构化输出
     */
    <T> T getStructuredOutput(String prompt, Class<T> schema);
    
    /**
     * 获取结构化输出（带上下文）
     */
    <T> T getStructuredOutput(String prompt, Class<T> schema, String contextId);
    
    /**
     * 获取结构化输出（带Schema对象）
     */
    <T> T getStructuredOutput(String prompt, JsonSchema schema, Class<T> targetClass);
    
    /**
     * 验证输出格式
     */
    <T> ValidationResult validateOutput(T output, Class<T> schema);
    
    /**
     * 解析输出
     */
    <T> T parseOutput(String output, Class<T> schema);
}

// ==================== 上下文模板接口 ====================

/**
 * 上下文模板API
 * 管理LLM对话上下文
 */
public interface ContextTemplateApi {
    
    /**
     * 创建上下文模板
     */
    ContextTemplate createTemplate(ContextTemplateRequest request);
    
    /**
     * 获取上下文模板
     */
    ContextTemplate getTemplate(String templateId);
    
    /**
     * 更新上下文模板
     */
    ContextTemplate updateTemplate(String templateId, ContextTemplateRequest request);
    
    /**
     * 删除上下文模板
     */
    void deleteTemplate(String templateId);
    
    /**
     * 列出上下文模板
     */
    List<ContextTemplate> listTemplates(String contextType);
    
    /**
     * 应用上下文模板
     */
    String applyTemplate(String templateId, Map<String, Object> variables);
    
    /**
     * 创建上下文
     */
    String createContext(ContextConfig config);
    
    /**
     * 更新上下文
     */
    void updateContext(String contextId, Map<String, Object> state);
    
    /**
     * 获取上下文
     */
    LLMContext getContext(String contextId);
    
    /**
     * 销毁上下文
     */
    void destroyContext(String contextId);
    
    /**
     * 克隆上下文
     */
    String cloneContext(String sourceContextId, String newSessionId);
}

// ==================== 降级策略接口 ====================

/**
 * 降级策略API
 * 管理LLM服务降级
 */
public interface DegradationApi {
    
    /**
     * 注册降级策略
     */
    void registerStrategy(DegradationStrategy strategy);
    
    /**
     * 获取当前降级级别
     */
    DegradationLevel getCurrentLevel();
    
    /**
     * 获取降级配置
     */
    DegradationConfig getConfig();
    
    /**
     * 执行降级
     */
    void degrade(DegradationTrigger trigger);
    
    /**
     * 恢复正常
     */
    void recover();
    
    /**
     * 检查是否需要降级
     */
    boolean shouldDegrade();
    
    /**
     * 获取降级后的备选方案
     */
    FallbackStrategy getFallbackStrategy(String featureId);
}
```

### 4.3 AGENT-SDK 当前状态分析

| 接口/功能 | 当前状态 | 需求满足度 | 是否需要新增 | 说明 |
|----------|---------|-----------|-------------|------|
| **组网能力** | ✅ 已有 | 100% | ❌ 不需要 | P2P组网 |
| **消息传递** | ✅ 已有 | 100% | ❌ 不需要 | MQTT消息 |
| **Agent发现** | ✅ 已有 | 100% | ❌ 不需要 | Agent发现 |
| **命令执行** | ✅ 已有 | 100% | ❌ 不需要 | 命令执行 |
| **场景协作** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 SceneCollaborationApi |
| **能力调用** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 CapabilityInvocationApi |
| **LLM协作** | ❌ 缺失 | 0% | ✅ 需要新增 | 需要 LLMCollaborationApi |

### 4.4 AGENT-SDK 需要新增的接口

```java
package net.ooder.sdk.agent;

// ==================== 场景协作接口 ====================

/**
 * 场景协作API
 * Agent与场景技能的协作接口
 */
public interface SceneCollaborationApi {
    
    /**
     * 加入场景组
     */
    void joinSceneGroup(String sceneGroupId, String roleId);
    
    /**
     * 离开场景组
     */
    void leaveSceneGroup(String sceneGroupId);
    
    /**
     * 获取场景组信息
     */
    SceneGroupInfo getSceneGroupInfo(String sceneGroupId);
    
    /**
     * 获取场景组能力列表
     */
    List<CapabilityInfo> getSceneGroupCapabilities(String sceneGroupId);
    
    /**
     * 订阅场景事件
     */
    void subscribeSceneEvents(String sceneGroupId, EventHandler handler);
    
    /**
     * 发布场景事件
     */
    void publishSceneEvent(String sceneGroupId, SceneEvent event);
    
    /**
     * 获取场景组状态
     */
    SceneGroupStatus getSceneGroupStatus(String sceneGroupId);
}

// ==================== 能力调用接口 ====================

/**
 * 能力调用API
 * Agent调用场景技能能力
 */
public interface CapabilityInvocationApi {
    
    /**
     * 发现能力
     */
    List<CapabilityInfo> discoverCapabilities(String sceneType);
    
    /**
     * 发现能力（按场景组）
     */
    List<CapabilityInfo> discoverCapabilities(String sceneGroupId, String sceneType);
    
    /**
     * 调用能力
     */
    CapabilityResult invokeCapability(String capabilityId, Map<String, Object> params);
    
    /**
     * 调用能力（异步）
     */
    CompletableFuture<CapabilityResult> invokeCapabilityAsync(String capabilityId, Map<String, Object> params);
    
    /**
     * 获取能力状态
     */
    CapabilityStatus getCapabilityStatus(String capabilityId);
    
    /**
     * 绑定能力
     */
    CapabilityBinding bindCapability(String sceneGroupId, String capabilityId);
    
    /**
     * 解绑能力
     */
    void unbindCapability(String bindingId);
}

// ==================== LLM协作接口 ====================

/**
 * LLM协作API
 * Agent与LLM服务的协作接口
 */
public interface LLMCollaborationApi {
    
    /**
     * 请求LLM能力分配
     */
    LlmCapabilityAllocation requestLLMCapability(LlmCapabilityRequest request);
    
    /**
     * 释放LLM能力
     */
    void releaseLLMCapability(String allocationId);
    
    /**
     * LLM对话
     */
    LlmResponse chat(LlmChatRequest request);
    
    /**
     * LLM对话（流式）
     */
    Flux<LlmChunk> chatStream(LlmChatRequest request);
    
    /**
     * 获取LLM上下文
     */
    LlmContext getLLMContext(String contextId);
    
    /**
     * 更新LLM上下文
     */
    void updateLLMContext(String contextId, Map<String, Object> state);
    
    /**
     * 执行工具调用
     */
    ToolCallResult executeToolCall(String toolId, Map<String, Object> params);
}
```

---

## 五、协作关系图

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           协作关系图 V2                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         应用层 (场景技能)                                │    │
│  │  skill-daily-report, skill-recruitment, skill-document-assistant        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         SDK Layer                                        │    │
│  │                                                                         │    │
│  │  ┌─────────────────────────┐  ┌─────────────────────────────────────┐   │    │
│  │  │       LLM-SDK           │  │           AGENT-SDK                 │   │    │
│  │  │  ┌─────────────────┐    │  │  ┌─────────────────────────────┐    │   │    │
│  │  │  │ ToolCallingApi  │◄───┼──┼──┤ CapabilityInvocationApi     │    │   │    │
│  │  │  └─────────────────┘    │  │  └─────────────────────────────┘    │   │    │
│  │  │  ┌─────────────────┐    │  │  ┌─────────────────────────────┐    │   │    │
│  │  │  │ StructuredOutApi│    │  │  │ SceneCollaborationApi       │    │   │    │
│  │  │  └─────────────────┘    │  │  └─────────────────────────────┘    │   │    │
│  │  │  ┌─────────────────┐    │  │  ┌─────────────────────────────┐    │   │    │
│  │  │  │ ContextTemplate │    │  │  │ LLMCollaborationApi         │    │   │    │
│  │  │  └─────────────────┘    │  │  └─────────────────────────────┘    │   │    │
│  │  │  ┌─────────────────┐    │  │                                     │   │    │
│  │  │  │ DegradationApi  │    │  │                                     │   │    │
│  │  │  └─────────────────┘    │  │                                     │   │    │
│  │  └─────────────────────────┘  └─────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Engine Layer                                     │    │
│  │  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌────────────┐  │    │
│  │  │ Activation    │ │ Menu          │ │ LLM Context   │ │ Tool       │  │    │
│  │  │ Engine        │ │ Generator     │ │ Engine        │ │ Engine     │  │    │
│  │  └───────┬───────┘ └───────┬───────┘ └───────┬───────┘ └─────┬──────┘  │    │
│  └──────────┼─────────────────┼─────────────────┼───────────────┼─────────┘    │
│             │                 │                 │               │               │
│             ▼                 ▼                 ▼               ▼               │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Skills Layer                                     │    │
│  │                                                                         │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │    │
│  │  │ skill-org-base  │  │ skill-knowledge │  │ skill-llm-conversation  │  │    │
│  │  │  (角色识别)     │  │ -base           │  │  (工具调用扩展)         │  │    │
│  │  │                 │  │  (向量存储扩展) │  │                         │  │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────────────┘  │    │
│  │                                                                         │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │    │
│  │  │ skill-scene     │  │ skill-rag       │  │ skill-llm-context       │  │    │
│  │  │  (场景管理)     │  │  (依赖知识库)   │  │ -builder                │  │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────────────┘  │    │
│  │                                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、任务分配与时间线

### 6.1 Skills 修改任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| SKILL-MOD-001 | skill-org-base 扩展（角色识别） | P0 | 5天 | - |
| SKILL-MOD-002 | skill-knowledge-base 扩展（向量存储） | P0 | 10天 | - |
| SKILL-MOD-003 | skill-llm-conversation 扩展（工具调用） | P0 | 7天 | SDK-NEW-001 |
| SKILL-MOD-004 | skill-llm-context-builder 扩展 | P1 | 5天 | SDK-NEW-003 |
| SKILL-MOD-005 | skill-llm-config-manager 扩展 | P1 | 5天 | SDK-NEW-004 |
| SKILL-MOD-006 | skill-rag 扩展 | P1 | 3天 | SKILL-MOD-002 |

### 6.2 Engine 新增任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| E-001 | 激活流程引擎 | P0 | 10天 | SKILL-MOD-001 |
| E-002 | 菜单生成引擎 | P0 | 7天 | SKILL-MOD-001 |
| E-003 | LLM上下文引擎 | P0 | 7天 | SDK-NEW-003 |
| E-004 | 工具调用引擎 | P0 | 7天 | SDK-NEW-001 |

### 6.3 SDK 新增任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 依赖 |
|--------|---------|--------|--------|------|
| SDK-NEW-001 | ToolCallingApi | P0 | 5天 | - |
| SDK-NEW-002 | StructuredOutputApi | P0 | 3天 | - |
| SDK-NEW-003 | ContextTemplateApi | P0 | 5天 | - |
| SDK-NEW-004 | DegradationApi | P0 | 3天 | - |
| SDK-NEW-005 | SceneCollaborationApi | P1 | 5天 | E-001 |
| SDK-NEW-006 | CapabilityInvocationApi | P1 | 3天 | - |
| SDK-NEW-007 | LLMCollaborationApi | P1 | 5天 | SDK-NEW-001~004 |

### 6.4 时间线

```
Week 1-2: SDK 基础接口
├── SDK-NEW-001: ToolCallingApi (5天)
├── SDK-NEW-002: StructuredOutputApi (3天)
├── SDK-NEW-003: ContextTemplateApi (5天)
└── SDK-NEW-004: DegradationApi (3天)

Week 3-4: Skills 基础扩展
├── SKILL-MOD-001: skill-org-base 扩展 (5天)
└── SKILL-MOD-002: skill-knowledge-base 扩展 (10天)

Week 5-6: Engine 引擎 + SDK 协作接口
├── E-001: 激活流程引擎 (10天)
├── E-002: 菜单生成引擎 (7天)
├── SDK-NEW-005: SceneCollaborationApi (5天)
└── SDK-NEW-006: CapabilityInvocationApi (3天)

Week 7-8: LLM 相关扩展
├── SKILL-MOD-003: skill-llm-conversation 扩展 (7天)
├── E-003: LLM上下文引擎 (7天)
├── E-004: 工具调用引擎 (7天)
└── SDK-NEW-007: LLMCollaborationApi (5天)

Week 9-10: 剩余扩展 + 集成测试
├── SKILL-MOD-004~006: 剩余Skills扩展 (13天)
└── 集成测试
```

---

## 七、总结

### 7.1 关键修正点

| 修正项 | 之前误解 | 正确理解 |
|--------|---------|---------|
| skill-vfs-database | 向量数据库 | 文件数据库存储，**不需要修改** |
| skill-user-auth | 角色管理 | 用户认证，**不需要修改** |
| 角色识别 | 修改 skill-user-auth | 扩展 skill-org-base |
| 向量存储 | 在 skill-vfs-database | 在 skill-knowledge-base |

### 7.2 真正需要修改的 Skills

1. **skill-org-base** - 扩展角色识别接口
2. **skill-knowledge-base** - 扩展向量存储和RAG
3. **skill-llm-conversation** - 扩展工具调用和结构化输出
4. **skill-llm-context-builder** - 扩展安装上下文
5. **skill-llm-config-manager** - 扩展多模型和降级
6. **skill-rag** - 依赖 skill-knowledge-base

### 7.3 Engine 需要新增的引擎

1. **Activation Engine** - 激活流程管理
2. **Menu Generator** - 菜单生成
3. **LLM Context Engine** - LLM上下文管理
4. **Tool Engine** - 工具调用管理

### 7.4 SDK 需要新增的接口

**LLM-SDK:**
- ToolCallingApi
- StructuredOutputApi
- ContextTemplateApi
- DegradationApi

**AGENT-SDK:**
- SceneCollaborationApi
- CapabilityInvocationApi
- LLMCollaborationApi

---

**文档状态**: 协作说明（修正版）
**下一步**: 启动 SDK 基础接口开发
