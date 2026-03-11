# 智能安装任务需求规格与设计文档

> **文档版本**: v1.0.0  
> **编写日期**: 2026-03-09  
> **所属模块**: skill-scene  
> **文档类型**: 需求规格说明书 + 设计文档  

---

## 一、需求规格

### 1.1 功能需求

#### FR-001: 双模式安装支持

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-001.1 | 支持LLM主导的智能安装模式 | P0 | LLM可用时自动启用 |
| FR-001.2 | 支持传统的手动安装模式 | P0 | LLM不可用时自动降级 |
| FR-001.3 | 支持安装模式自动检测和切换 | P0 | 检测延迟<3秒 |
| FR-001.4 | 支持安装过程中手动切换模式 | P1 | 用户可随时切换 |

#### FR-002: 智能依赖分析

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-002.1 | 自动分析技能依赖关系 | P0 | 依赖检测准确率>95% |
| FR-002.2 | 智能推荐依赖配置 | P0 | 推荐采纳率>80% |
| FR-002.3 | 自动检查依赖健康状态 | P0 | 健康检查覆盖率100% |
| FR-002.4 | 支持依赖自动安装 | P1 | 支持autoInstall=true的依赖 |

#### FR-003: 角色感知安装

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-003.1 | 自动识别安装者角色 | P0 | 角色识别准确率>90% |
| FR-003.2 | 根据角色展示不同安装步骤 | P0 | 步骤差异化覆盖率100% |
| FR-003.3 | 支持多角色协作安装 | P0 | 支持MANAGER+EMPLOYEE等组合 |
| FR-003.4 | 支持角色权限检查 | P0 | 权限检查覆盖率100% |

#### FR-004: 智能配置生成

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-004.1 | LLM智能生成配置建议 | P0 | 配置有效性>90% |
| FR-004.2 | 支持配置实时验证 | P0 | 验证反馈延迟<1秒 |
| FR-004.3 | 支持配置历史复用 | P1 | 支持相似场景配置复用 |
| FR-004.4 | 支持配置模板应用 | P1 | 内置5+常用模板 |

#### FR-005: 安装状态管理

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-005.1 | 实时显示安装进度 | P0 | 进度更新延迟<2秒 |
| FR-005.2 | 支持安装步骤回滚 | P0 | 回滚成功率>95% |
| FR-005.3 | 支持安装断点续传 | P1 | 支持从任意步骤恢复 |
| FR-005.4 | 安装历史记录查询 | P1 | 支持90天内历史查询 |

#### FR-006: 激活流程自动化

| 需求ID | 需求描述 | 优先级 | 验收标准 |
|--------|---------|--------|---------|
| FR-006.1 | 自动触发角色激活流程 | P0 | 激活触发成功率>98% |
| FR-006.2 | 支持按角色差异化激活 | P0 | 角色激活覆盖率100% |
| FR-006.3 | 自动绑定角色权限 | P0 | 权限绑定准确率100% |
| FR-006.4 | 自动生成角色菜单 | P0 | 菜单生成延迟<3秒 |

### 1.2 非功能需求

#### NFR-001: 性能需求

| 需求ID | 需求描述 | 目标值 |
|--------|---------|--------|
| NFR-001.1 | 安装流程启动时间 | <5秒 |
| NFR-001.2 | LLM响应时间 | <10秒 |
| NFR-001.3 | 配置验证时间 | <1秒 |
| NFR-001.4 | 状态更新延迟 | <2秒 |
| NFR-001.5 | 并发安装支持 | 10个同时安装 |

#### NFR-002: 可靠性需求

| 需求ID | 需求描述 | 目标值 |
|--------|---------|--------|
| NFR-002.1 | 安装成功率 | >95% |
| NFR-002.2 | 降级切换成功率 | >99% |
| NFR-002.3 | 数据一致性 | 100% |
| NFR-002.4 | 故障恢复时间 | <30秒 |

#### NFR-003: 安全需求

| 需求ID | 需求描述 | 目标值 |
|--------|---------|--------|
| NFR-003.1 | 权限检查覆盖率 | 100% |
| NFR-003.2 | 敏感数据加密 | 100% |
| NFR-003.3 | 操作审计日志 | 100% |
| NFR-003.4 | SQL注入防护 | 100% |

---

## 二、系统设计

### 2.1 架构设计

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           智能安装系统架构                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        表示层 (Presentation)                             │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │   │
│  │  │ 安装向导UI   │  │ 激活流程UI   │  │ 进度监控面板 │  │ 配置编辑器   │ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        应用层 (Application)                              │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐       │   │
│  │  │ InstallationService│  │ ActivationService │  │ ConfigurationService│    │   │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘       │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐       │   │
│  │  │ DegradationService│  │ ProgressService   │  │ StateMachineService │    │   │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘       │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        领域层 (Domain)                                   │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │   │
│  │  │ Installation │  │ Activation   │  │ Role         │  │ Configuration│ │   │
│  │  │ Aggregate    │  │ Aggregate    │  │ Aggregate    │  │ Aggregate    │ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                    │   │
│  │  │ Tool         │  │ Schema       │  │ StateMachine │                    │   │
│  │  │ Registry     │  │ Validator    │  │ Engine       │                    │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                    │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        基础设施层 (Infrastructure)                       │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │   │
│  │  │ LLM Client   │  │ Engine Client│  │ SDK Client   │  │ Repository   │ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                    │   │
│  │  │ Cache        │  │ Event Bus    │  │ File Storage │                    │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                    │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心领域模型

#### 2.2.1 安装聚合根 (Installation Aggregate)

```java
/**
 * 安装聚合根
 */
public class Installation {
    
    // 标识
    private InstallationId id;
    private String skillId;
    private String skillVersion;
    
    // 状态
    private InstallationStatus status;
    private InstallationMode mode; // LLM / MANUAL
    
    // 参与者
    private String installerId;
    private String installerRole;
    private List<InstallationParticipant> participants;
    
    // 配置
    private InstallationConfig config;
    private List<InstallationStep> steps;
    
    // 进度
    private InstallationProgress progress;
    private List<InstallationCheckpoint> checkpoints;
    
    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    // 领域方法
    public void start() { ... }
    public void executeStep(String stepId) { ... }
    public void completeStep(String stepId, StepResult result) { ... }
    public void failStep(String stepId, ErrorInfo error) { ... }
    public void rollbackToCheckpoint(String checkpointId) { ... }
    public void complete() { ... }
    public void fail(ErrorInfo error) { ... }
}

/**
 * 安装步骤
 */
public class InstallationStep {
    private String stepId;
    private String name;
    private StepType type; // AUTO / MANUAL / LLM
    private StepStatus status;
    private List<ToolCall> toolCalls;
    private Map<String, Object> input;
    private Map<String, Object> output;
    private ErrorInfo error;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

/**
 * 安装状态枚举
 */
public enum InstallationStatus {
    PENDING,           // 待安装
    ANALYZING,         // 分析中
    DOWNLOADING,       // 下载中
    INSTALLING,        // 安装中
    CONFIGURING,       // 配置中
    ACTIVATING,        // 激活中
    COMPLETED,         // 已完成
    FAILED,            // 失败
    ROLLING_BACK,      // 回滚中
    ROLLED_BACK        // 已回滚
}
```

#### 2.2.2 激活聚合根 (Activation Aggregate)

```java
/**
 * 激活聚合根
 */
public class Activation {
    
    private ActivationId id;
    private String installationId;
    private String skillId;
    
    // 激活流程
    private ActivationFlow flow;
    private List<ActivationStep> steps;
    
    // 角色激活状态
    private Map<String, RoleActivation> roleActivations;
    
    // 状态
    private ActivationStatus status;
    
    // 领域方法
    public void startFlow(String roleId) { ... }
    public void executeStep(String roleId, String stepId) { ... }
    public void completeRoleActivation(String roleId) { ... }
    public void complete() { ... }
}

/**
 * 角色激活
 */
public class RoleActivation {
    private String roleId;
    private String userId;
    private ActivationStatus status;
    private List<ActivationStep> completedSteps;
    private List<PrivateCapability> selectedCapabilities;
}
```

#### 2.2.3 角色聚合根 (Role Aggregate)

```java
/**
 * 角色聚合根
 */
public class Role {
    
    private RoleId id;
    private String name; // MANAGER, EMPLOYEE, HR
    private String displayName;
    private List<String> permissions;
    
    // 安装相关
    private List<InstallationStepConfig> installationSteps;
    private List<MenuConfig> menus;
    private List<String> defaultCapabilities;
    
    // 领域方法
    public boolean hasPermission(String permission) { ... }
    public List<InstallationStep> getInstallationSteps() { ... }
}
```

### 2.3 状态机设计

#### 2.3.1 安装状态机

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           安装状态机                                             │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────┐    start     ┌──────────┐    analyze    ┌─────────────┐           │
│  │ PENDING │ ────────────▶│ ANALYZING│ ─────────────▶│ DOWNLOADING │           │
│  └─────────┘              └──────────┘               └─────────────┘           │
│                               │                           │                     │
│                               │ fail                      │ download            │
│                               ▼                           ▼                     │
│                          ┌─────────┐                 ┌──────────┐              │
│                          │ FAILED  │                 │INSTALLING│              │
│                          └─────────┘                 └──────────┘              │
│                                                          │                      │
│                    ┌─────────────────────────────────────┼─────────────────┐   │
│                    │                                     │                 │   │
│                    │ rollback                    install │                 │   │
│                    ▼                                     ▼                 │   │
│              ┌────────────┐                        ┌───────────┐           │   │
│              │ROLLED_BACK │                        │CONFIGURING│           │   │
│              └────────────┘                        └───────────┘           │   │
│                    ▲                                     │                 │   │
│                    │ rollback                    configure                 │   │
│                    │                                     ▼                 │   │
│                    │                               ┌──────────┐            │   │
│                    └───────────────────────────────│ACTIVATING│            │   │
│                                                    └──────────┘            │   │
│                                                         │                   │   │
│                              ┌──────────────────────────┘                   │   │
│                              │ activate                                      │   │
│                              ▼                                              │   │
│                         ┌──────────┐                                        │   │
│                         │COMPLETED │                                        │   │
│                         └──────────┘                                        │   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

#### 2.3.2 状态转换规则

| 当前状态 | 事件 | 目标状态 | 条件 | 动作 |
|----------|------|----------|------|------|
| PENDING | start | ANALYZING | - | 创建安装上下文 |
| ANALYZING | analyze | DOWNLOADING | 分析成功 | 保存分析结果 |
| ANALYZING | fail | FAILED | 分析失败 | 记录错误 |
| DOWNLOADING | download | INSTALLING | 下载成功 | 解压资源 |
| INSTALLING | install | CONFIGURING | 安装成功 | 注册能力 |
| CONFIGURING | configure | ACTIVATING | 配置成功 | 保存配置 |
| ACTIVATING | activate | COMPLETED | 激活成功 | 发送通知 |
| * | rollback | ROLLING_BACK | 用户触发 | 开始回滚 |
| ROLLING_BACK | complete | ROLLED_BACK | 回滚完成 | 清理资源 |

### 2.4 服务设计

#### 2.4.1 InstallationService

```java
/**
 * 安装服务
 */
public interface InstallationService {
    
    /**
     * 创建安装任务
     */
    Result<Installation> createInstallation(CreateInstallationCommand command);
    
    /**
     * 启动安装
     */
    Result<Void> startInstallation(String installationId);
    
    /**
     * 执行安装步骤
     */
    Result<StepResult> executeStep(String installationId, String stepId);
    
    /**
     * 获取安装状态
     */
    Result<InstallationStatus> getStatus(String installationId);
    
    /**
     * 获取安装进度
     */
    Result<InstallationProgress> getProgress(String installationId);
    
    /**
     * 回滚安装
     */
    Result<Void> rollback(String installationId, String checkpointId);
    
    /**
     * 取消安装
     */
    Result<Void> cancel(String installationId);
    
    /**
     * 列出安装历史
     */
    Result<List<Installation>> listInstallations(String skillId);
}
```

#### 2.4.2 SmartInstallationService (LLM智能安装)

```java
/**
 * 智能安装服务
 */
public interface SmartInstallationService {
    
    /**
     * 智能分析安装需求
     */
    Result<InstallationAnalysis> analyze(String skillId, String userId);
    
    /**
     * 智能生成配置
     */
    Result<InstallationConfig> generateConfig(String skillId, InstallationContext context);
    
    /**
     * 智能推荐安装步骤
     */
    Result<List<InstallationStep>> recommendSteps(String skillId, String userRole);
    
    /**
     * 智能诊断问题
     */
    Result<DiagnosisResult> diagnose(String installationId, ErrorInfo error);
    
    /**
     * 智能建议修复方案
     */
    Result<FixSuggestion> suggestFix(String installationId, ErrorInfo error);
}
```

#### 2.4.3 ActivationService

```java
/**
 * 激活服务
 */
public interface ActivationService {
    
    /**
     * 创建激活任务
     */
    Result<Activation> createActivation(CreateActivationCommand command);
    
    /**
     * 启动角色激活
     */
    Result<Void> startRoleActivation(String activationId, String roleId, String userId);
    
    /**
     * 执行激活步骤
     */
    Result<StepResult> executeActivationStep(String activationId, String roleId, String stepId);
    
    /**
     * 完成角色激活
     */
    Result<Void> completeRoleActivation(String activationId, String roleId);
    
    /**
     * 获取激活状态
     */
    Result<ActivationStatus> getStatus(String activationId);
    
    /**
     * 获取角色激活状态
     */
    Result<RoleActivationStatus> getRoleStatus(String activationId, String roleId);
}
```

### 2.5 工具设计

#### 2.5.1 工具注册表

```java
/**
 * 工具注册表
 */
@Component
public class InstallationToolRegistry {
    
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 注册内置工具
        registerTool(BuiltInTools.CHECK_DEPENDENCY);
        registerTool(BuiltInTools.CHECK_PERMISSION);
        registerTool(BuiltInTools.QUERY_USER);
        registerTool(BuiltInTools.QUERY_ORG);
        registerTool(BuiltInTools.BIND_CAPABILITY);
        registerTool(BuiltInTools.CREATE_MENU);
        registerTool(BuiltInTools.SET_PERMISSION);
        registerTool(BuiltInTools.SEND_NOTIFICATION);
    }
    
    public void registerTool(ToolDefinition tool) {
        tools.put(tool.getId(), tool);
    }
    
    public ToolDefinition getTool(String toolId) {
        return tools.get(toolId);
    }
    
    public List<ToolDefinition> listTools(String category) {
        return tools.values().stream()
            .filter(t -> t.getCategory().equals(category))
            .collect(Collectors.toList());
    }
    
    public List<FunctionDefinition> toOpenAIFunctions() {
        return tools.values().stream()
            .map(this::convertToOpenAIFunction)
            .collect(Collectors.toList());
    }
}

/**
 * 内置工具定义
 */
public enum BuiltInTools {
    
    CHECK_DEPENDENCY("check.dependency", "检查依赖", ToolCategory.CHECK),
    CHECK_PERMISSION("check.permission", "检查权限", ToolCategory.CHECK),
    QUERY_USER("query.user", "查询用户", ToolCategory.QUERY),
    QUERY_ORG("query.org", "查询组织", ToolCategory.QUERY),
    BIND_CAPABILITY("bind.capability", "绑定能力", ToolCategory.ACTION),
    CREATE_MENU("create.menu", "创建菜单", ToolCategory.ACTION),
    SET_PERMISSION("set.permission", "设置权限", ToolCategory.ACTION),
    SEND_NOTIFICATION("send.notification", "发送通知", ToolCategory.ACTION);
    
    private final String id;
    private final String name;
    private final ToolCategory category;
    
    // ...
}
```

#### 2.5.2 工具执行器

```java
/**
 * 工具执行器
 */
@Component
public class ToolExecutor {
    
    @Autowired
    private InstallationToolRegistry registry;
    
    @Autowired
    private ApplicationContext context;
    
    /**
     * 执行工具调用
     */
    public ToolResult execute(ToolCall toolCall) {
        ToolDefinition tool = registry.getTool(toolCall.getToolId());
        if (tool == null) {
            return ToolResult.fail("Tool not found: " + toolCall.getToolId());
        }
        
        try {
            ToolHandler handler = getHandler(tool);
            return handler.handle(toolCall.getParams());
        } catch (Exception e) {
            return ToolResult.fail(e.getMessage());
        }
    }
    
    /**
     * 批量执行工具调用
     */
    public List<ToolResult> executeBatch(List<ToolCall> toolCalls) {
        return toolCalls.stream()
            .map(this::execute)
            .collect(Collectors.toList());
    }
    
    private ToolHandler getHandler(ToolDefinition tool) {
        // 从Spring容器获取处理器
        return context.getBean(tool.getHandlerClass());
    }
}
```

### 2.6 LLM协作设计

#### 2.6.1 LLM上下文管理

```java
/**
 * LLM安装上下文管理器
 */
@Component
public class InstallationLlmContextManager {
    
    private final Map<String, LlmContext> contexts = new ConcurrentHashMap<>();
    
    /**
     * 创建安装上下文
     */
    public LlmContext createContext(String installationId, String skillId) {
        LlmContext context = new LlmContext();
        context.setInstallationId(installationId);
        context.setSkillId(skillId);
        context.setCreatedAt(LocalDateTime.now());
        context.setMessages(new ArrayList<>());
        
        // 添加系统提示
        context.addMessage(Message.system(loadSystemPrompt(skillId)));
        
        contexts.put(installationId, context);
        return context;
    }
    
    /**
     * 获取上下文
     */
    public LlmContext getContext(String installationId) {
        return contexts.get(installationId);
    }
    
    /**
     * 添加用户消息
     */
    public void addUserMessage(String installationId, String content) {
        LlmContext context = contexts.get(installationId);
        if (context != null) {
            context.addMessage(Message.user(content));
        }
    }
    
    /**
     * 添加助手消息
     */
    public void addAssistantMessage(String installationId, String content) {
        LlmContext context = contexts.get(installationId);
        if (context != null) {
            context.addMessage(Message.assistant(content));
        }
    }
    
    /**
     * 获取完整对话历史
     */
    public List<Message> getMessages(String installationId) {
        LlmContext context = contexts.get(installationId);
        return context != null ? context.getMessages() : Collections.emptyList();
    }
    
    /**
     * 清除上下文
     */
    public void clearContext(String installationId) {
        contexts.remove(installationId);
    }
}
```

#### 2.6.2 结构化输出处理

```java
/**
 * 结构化输出处理器
 */
@Component
public class StructuredOutputProcessor {
    
    @Autowired
    private SchemaValidator schemaValidator;
    
    /**
     * 处理LLM输出
     */
    public <T> T process(String output, Class<T> targetClass, String schemaId) {
        // 验证输出是否符合Schema
        ValidationResult validation = schemaValidator.validate(schemaId, output);
        if (!validation.isValid()) {
            throw new InvalidOutputException("Output validation failed: " + validation.getErrors());
        }
        
        // 解析JSON
        try {
            return JsonUtils.parse(output, targetClass);
        } catch (Exception e) {
            throw new ParseException("Failed to parse output", e);
        }
    }
    
    /**
     * 处理并修复输出
     */
    public <T> T processAndFix(String output, Class<T> targetClass, String schemaId) {
        ValidationResult validation = schemaValidator.validate(schemaId, output);
        
        if (validation.isValid()) {
            return JsonUtils.parse(output, targetClass);
        }
        
        // 尝试修复
        String fixed = attemptFix(output, validation.getErrors());
        ValidationResult revalidation = schemaValidator.validate(schemaId, fixed);
        
        if (revalidation.isValid()) {
            return JsonUtils.parse(fixed, targetClass);
        }
        
        throw new InvalidOutputException("Unable to fix output: " + revalidation.getErrors());
    }
    
    private String attemptFix(String output, List<ValidationError> errors) {
        // 实现修复逻辑
        // 1. 添加缺失字段
        // 2. 修正类型错误
        // 3. 格式化JSON
        return FixedJsonGenerator.fix(output, errors);
    }
}
```

### 2.7 降级策略设计

```java
/**
 * 降级策略处理器
 */
@Component
public class DegradationHandler {
    
    @Autowired
    private LlmClient llmClient;
    
    @Autowired
    private RuleBasedInstallationService ruleBasedService;
    
    /**
     * 检查LLM可用性
     */
    public boolean isLlmAvailable() {
        try {
            LlmResponse response = llmClient.ping();
            return response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取当前降级级别
     */
    public DegradationLevel getCurrentLevel() {
        if (isLlmAvailable()) {
            return DegradationLevel.FULL_LLM;
        }
        
        // 检查是否有缓存的配置模板
        if (hasCachedTemplates()) {
            return DegradationLevel.CACHED_TEMPLATE;
        }
        
        // 检查是否支持基于规则的安装
        if (ruleBasedService.isAvailable()) {
            return DegradationLevel.RULE_BASED;
        }
        
        return DegradationLevel.MANUAL_ONLY;
    }
    
    /**
     * 执行降级安装
     */
    public InstallationResult executeDegradedInstallation(String skillId, DegradationLevel level) {
        switch (level) {
            case FULL_LLM:
                return executeLlmInstallation(skillId);
            case CACHED_TEMPLATE:
                return executeTemplateInstallation(skillId);
            case RULE_BASED:
                return ruleBasedService.install(skillId);
            case MANUAL_ONLY:
                return executeManualInstallation(skillId);
            default:
                throw new UnsupportedOperationException("Unknown degradation level: " + level);
        }
    }
    
    /**
     * 获取手动配置向导
     */
    public List<ManualConfigStep> getManualConfigWizard(String skillId) {
        List<ManualConfigStep> steps = new ArrayList<>();
        
        // 1. 基础配置
        steps.add(new ManualConfigStep("basic", "基础配置", 
            Arrays.asList("name", "description", "icon")));
        
        // 2. 角色配置
        steps.add(new ManualConfigStep("roles", "角色配置",
            Arrays.asList("roles", "permissions")));
        
        // 3. 依赖配置
        steps.add(new ManualConfigStep("dependencies", "依赖配置",
            Arrays.asList("required", "optional")));
        
        // 4. 功能配置
        steps.add(new ManualConfigStep("features", "功能配置",
            Arrays.asList("features", "menus")));
        
        return steps;
    }
}

/**
 * 降级级别
 */
public enum DegradationLevel {
    FULL_LLM,        // 完整LLM支持
    CACHED_TEMPLATE, // 缓存模板
    RULE_BASED,      // 基于规则
    MANUAL_ONLY      // 纯手动
}
```

---

## 三、接口设计

### 3.1 REST API

#### 3.1.1 安装管理API

```yaml
# 创建安装任务
POST /api/v1/installations
Request:
  skillId: string
  skillVersion: string
  mode: LLM | MANUAL | AUTO
  
Response:
  installationId: string
  status: PENDING
  createdAt: datetime

# 启动安装
POST /api/v1/installations/{id}/start
Response:
  status: ANALYZING

# 获取安装状态
GET /api/v1/installations/{id}/status
Response:
  status: string
  progress: number
  currentStep: string
  completedSteps: [string]
  remainingSteps: [string]

# 获取安装进度
GET /api/v1/installations/{id}/progress
Response:
  totalSteps: number
  completedSteps: number
  percentage: number
  estimatedRemainingTime: number
  steps: [StepProgress]

# 执行安装步骤
POST /api/v1/installations/{id}/steps/{stepId}/execute
Request:
  params: object
  
Response:
  success: boolean
  result: object
  nextStep: string

# 回滚安装
POST /api/v1/installations/{id}/rollback
Request:
  checkpointId: string
  
Response:
  success: boolean

# 取消安装
DELETE /api/v1/installations/{id}
Response:
  success: boolean
```

#### 3.1.2 智能安装API

```yaml
# 智能分析
POST /api/v1/installations/smart/analyze
Request:
  skillId: string
  
Response:
  dependencies: [DependencyInfo]
  recommendedConfig: object
  estimatedTime: number
  risks: [RiskInfo]

# 智能生成配置
POST /api/v1/installations/smart/generate-config
Request:
  skillId: string
  context: object
  
Response:
  config: InstallationConfig
  explanation: string

# 智能诊断
POST /api/v1/installations/{id}/smart/diagnose
Request:
  errorCode: string
  errorMessage: string
  
Response:
  diagnosis: string
  suggestions: [string]
  fixable: boolean
```

#### 3.1.3 激活管理API

```yaml
# 创建激活任务
POST /api/v1/activations
Request:
  installationId: string
  skillId: string
  
Response:
  activationId: string
  status: PENDING

# 启动角色激活
POST /api/v1/activations/{id}/roles/{roleId}/start
Request:
  userId: string
  
Response:
  success: boolean
  steps: [ActivationStep]

# 执行激活步骤
POST /api/v1/activations/{id}/roles/{roleId}/steps/{stepId}
Request:
  params: object
  
Response:
  success: boolean
  result: object

# 完成角色激活
POST /api/v1/activations/{id}/roles/{roleId}/complete
Response:
  success: boolean
```

### 3.2 事件设计

#### 3.2.1 领域事件

```java
/**
 * 安装已创建事件
 */
public class InstallationCreatedEvent {
    private String installationId;
    private String skillId;
    private String installerId;
    private LocalDateTime createdAt;
}

/**
 * 安装步骤已完成事件
 */
public class InstallationStepCompletedEvent {
    private String installationId;
    private String stepId;
    private String stepName;
    private StepResult result;
    private LocalDateTime completedAt;
}

/**
 * 安装已完成事件
 */
public class InstallationCompletedEvent {
    private String installationId;
    private String skillId;
    private InstallationConfig config;
    private LocalDateTime completedAt;
}

/**
 * 角色激活已完成事件
 */
public class RoleActivationCompletedEvent {
    private String activationId;
    private String roleId;
    private String userId;
    private List<String> grantedPermissions;
    private LocalDateTime completedAt;
}
```

---

## 四、数据模型

### 4.1 数据库表设计

```sql
-- 安装任务表
CREATE TABLE installation (
    id VARCHAR(64) PRIMARY KEY,
    skill_id VARCHAR(64) NOT NULL,
    skill_version VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    mode VARCHAR(32) NOT NULL,
    installer_id VARCHAR(64) NOT NULL,
    installer_role VARCHAR(32) NOT NULL,
    config JSON,
    progress INT DEFAULT 0,
    current_step VARCHAR(64),
    error_code VARCHAR(64),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    INDEX idx_skill_id (skill_id),
    INDEX idx_status (status),
    INDEX idx_installer (installer_id)
);

-- 安装步骤表
CREATE TABLE installation_step (
    id VARCHAR(64) PRIMARY KEY,
    installation_id VARCHAR(64) NOT NULL,
    step_id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    input JSON,
    output JSON,
    error_code VARCHAR(64),
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (installation_id) REFERENCES installation(id),
    INDEX idx_installation (installation_id)
);

-- 激活任务表
CREATE TABLE activation (
    id VARCHAR(64) PRIMARY KEY,
    installation_id VARCHAR(64) NOT NULL,
    skill_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    FOREIGN KEY (installation_id) REFERENCES installation(id),
    INDEX idx_installation (installation_id)
);

-- 角色激活表
CREATE TABLE role_activation (
    id VARCHAR(64) PRIMARY KEY,
    activation_id VARCHAR(64) NOT NULL,
    role_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    selected_capabilities JSON,
    completed_at TIMESTAMP,
    FOREIGN KEY (activation_id) REFERENCES activation(id),
    INDEX idx_activation (activation_id)
);

-- 安装检查点表
CREATE TABLE installation_checkpoint (
    id VARCHAR(64) PRIMARY KEY,
    installation_id VARCHAR(64) NOT NULL,
    checkpoint_id VARCHAR(64) NOT NULL,
    state JSON NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (installation_id) REFERENCES installation(id),
    INDEX idx_installation (installation_id)
);
```

---

## 五、安全设计

### 5.1 权限控制

```java
/**
 * 安装权限检查
 */
@Component
public class InstallationPermissionChecker {
    
    /**
     * 检查安装权限
     */
    public boolean canInstall(String userId, String skillId) {
        // 1. 检查用户是否有安装权限
        if (!hasPermission(userId, "skill:install")) {
            return false;
        }
        
        // 2. 检查技能是否允许该用户安装
        Skill skill = skillRepository.findById(skillId);
        if (!skill.isInstallableBy(userId)) {
            return false;
        }
        
        // 3. 检查是否已达到安装限制
        if (hasReachedInstallLimit(userId, skillId)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查激活权限
     */
    public boolean canActivate(String userId, String activationId, String roleId) {
        // 1. 检查用户是否属于该角色
        if (!hasRole(userId, roleId)) {
            return false;
        }
        
        // 2. 检查激活任务是否分配给该用户
        Activation activation = activationRepository.findById(activationId);
        if (!activation.isAssignedTo(userId, roleId)) {
            return false;
        }
        
        return true;
    }
}
```

### 5.2 审计日志

```java
/**
 * 安装审计日志
 */
@Component
public class InstallationAuditLogger {
    
    /**
     * 记录安装操作
     */
    public void logInstallationOperation(String operation, String installationId, 
                                         String userId, Map<String, Object> details) {
        AuditLog log = new AuditLog();
        log.setOperation(operation);
        log.setTargetType("installation");
        log.setTargetId(installationId);
        log.setUserId(userId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
    
    /**
     * 记录配置变更
     */
    public void logConfigChange(String installationId, String userId,
                                Map<String, Object> oldConfig, 
                                Map<String, Object> newConfig) {
        AuditLog log = new AuditLog();
        log.setOperation("config_change");
        log.setTargetType("installation");
        log.setTargetId(installationId);
        log.setUserId(userId);
        log.setDetails(Map.of(
            "oldConfig", oldConfig,
            "newConfig", newConfig
        ));
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
}
```

---

## 六、部署架构

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           部署架构                                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        负载均衡层 (Nginx)                                │   │
│  │                    SSL终止 / 请求分发 / 静态资源                          │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        应用服务层 (K8s)                                  │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │   │
│  │  │ skill-scene  │  │ skill-scene  │  │ skill-scene  │  │ skill-scene  │ │   │
│  │  │ Pod 1        │  │ Pod 2        │  │ Pod 3        │  │ Pod N        │ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │   │
│  │                         HPA自动扩缩容                                    │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        数据存储层                                        │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │   │
│  │  │ MySQL        │  │ Redis        │  │ MinIO        │  │ Elasticsearch│ │   │
│  │  │ 主从集群     │  │ Cluster      │  │ 对象存储     │  │ 日志搜索     │ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        外部依赖层                                        │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                    │   │
│  │  │ LLM Service  │  │ MQTT Broker  │  │ Email Service│                    │   │
│  │  │ OpenAI/千问  │  │ EMQ X        │  │ SMTP         │                    │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                    │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、测试策略

### 7.1 测试金字塔

```
                    ┌─────────┐
                    │  E2E测试 │  ← 10% (关键流程)
                    │  (Selenium)│
                    ├─────────┤
                    │ 集成测试 │  ← 20% (服务间交互)
                    │  (TestContainers)│
                    ├─────────┤
                    │ 单元测试 │  ← 70% (核心业务逻辑)
                    │  (JUnit/Mockito)│
                    └─────────┘
```

### 7.2 关键测试场景

| 测试类型 | 测试场景 | 覆盖率目标 |
|----------|---------|-----------|
| 单元测试 | 状态机转换 | 100% |
| 单元测试 | 工具执行 | 90% |
| 单元测试 | 权限检查 | 100% |
| 集成测试 | 安装流程 | 80% |
| 集成测试 | 激活流程 | 80% |
| 集成测试 | 降级策略 | 80% |
| E2E测试 | 完整安装+激活 | 关键路径 |

---

## 八、文档清单

| 文档名称 | 类型 | 状态 |
|----------|------|------|
| 智能安装任务需求规格与设计文档 | 设计文档 | ✅ 本文档 |
| API接口文档 | 技术文档 | 待编写 |
| 部署运维手册 | 运维文档 | 待编写 |
| 用户操作手册 | 用户文档 | 待编写 |
| 测试用例文档 | 测试文档 | 待编写 |

---

**文档版本**: v1.0.0  
**编写日期**: 2026-03-09  
**编写人**: AI Assistant  
**审核状态**: 待审核