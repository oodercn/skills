# skill-scene 预制安装协作开发计划

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-09  
> **计划周期**: 10周  
> **协作方**: Engine Team, SDK Team, Skills Team, LLM/AI Team, 应用开发 Team

---

## 一、Engine/SDK 团队任务完成度评估

### 1.1 Engine Team 当前状态

根据 [ENGINE_COLLABORATION_STATUS_V2.3.md](e:/github/ooder-skills/docs/v2.3/volume-04-architecture/ENGINE_COLLABORATION_STATUS_V2.3.md) 分析：

| 现有能力 | 版本 | 状态 | 对新需求支持度 |
|----------|------|------|----------------|
| NetworkConfigProvider | 0.8.0 | ✅ 已完成 | 100% - 网络配置满足 |
| DeviceManagementProvider | 0.8.0 | ✅ 已完成 | 100% - 设备管理满足 |
| SecurityConfigProvider | 0.9.0 | ✅ 已完成 | 80% - 需扩展角色权限 |
| HealthCheckProvider | 0.9.0 | ✅ 已完成 | 100% - 健康检查满足 |
| Capability Invoker | 1.0.0 | ✅ 已完成 | 70% - 需支持LLM工具调用 |
| SceneCapability Manager | 1.0.0 | ✅ 已完成 | 60% - 需扩展安装流程 |

**结论**: Engine基础能力已完成，但**安装激活相关功能需要新建**

### 1.2 SDK Team 当前状态

根据代码库分析：

| 现有SDK | 位置 | 状态 | 对新需求支持度 |
|---------|------|------|----------------|
| SceneSdkAdapter | skill-scene | ✅ 基础版本 | 60% - 需扩展安装接口 |
| SkillSdkAutoConfiguration | skill-common | ✅ 已完成 | 80% - 配置管理满足 |
| ShareableCapability | skill-common | ✅ 已完成 | 70% - 需扩展工具调用 |

**结论**: SDK基础框架存在，但**LLM安装专用接口需要新建**

---

## 二、适合在 skill-scene 预制的任务识别

### 2.1 预制原则

适合在 skill-scene 预制的任务特征：
1. **不依赖** Engine 新接口（可在 Skills 层实现）
2. **不依赖** SDK 新接口（可用现有SDK能力）
3. **可独立测试**（有明确的输入输出）
4. **可为 Engine/SDK 提供参考实现**（验证接口设计）

### 2.2 可预制任务清单

#### 类别A: 纯Skills层实现（高优先级预制）

| 任务ID | 任务名称 | 原计划归属 | 预制可行性 | 预制价值 |
|--------|---------|-----------|-----------|---------|
| **SCENE-PREF-001** | 安装说明书解析器 | LLM Team | ⭐⭐⭐⭐⭐ | 高 - 为LLM提供结构化输入 |
| **SCENE-PREF-002** | 工具定义注册表 | Engine | ⭐⭐⭐⭐⭐ | 高 - 验证工具接口设计 |
| **SCENE-PREF-003** | Schema验证器 | SDK | ⭐⭐⭐⭐⭐ | 高 - 验证Schema设计 |
| **SCENE-PREF-004** | 安装状态机 | Engine | ⭐⭐⭐⭐☆ | 高 - 验证状态流转 |
| **SCENE-PREF-005** | 角色识别服务 | skill-org-base | ⭐⭐⭐⭐☆ | 中 - 可独立实现 |
| **SCENE-PREF-006** | 知识库预制内容加载 | skill-knowledge-base | ⭐⭐⭐⭐☆ | 中 - 可独立实现 |

#### 类别B: 适配层实现（中优先级预制）

| 任务ID | 任务名称 | 原计划归属 | 预制可行性 | 预制价值 |
|--------|---------|-----------|-----------|---------|
| **SCENE-PREF-007** | Engine接口适配器 | Engine | ⭐⭐⭐☆☆ | 中 - 预留Engine接口 |
| **SCENE-PREF-008** | SDK接口适配器 | SDK | ⭐⭐⭐☆☆ | 中 - 预留SDK接口 |
| **SCENE-PREF-009** | 降级策略处理器 | SDK | ⭐⭐⭐⭐☆ | 高 - 可独立运行 |

#### 类别C: 完整流程模拟（低优先级预制）

| 任务ID | 任务名称 | 原计划归属 | 预制可行性 | 预制价值 |
|--------|---------|-----------|-----------|---------|
| **SCENE-PREF-010** | 模拟LLM客户端 | SDK | ⭐⭐⭐☆☆ | 中 - 用于测试 |
| **SCENE-PREF-011** | 安装流程模拟器 | Engine | ⭐⭐☆☆☆ | 低 - 依赖较多 |

---

## 三、skill-scene 预制开发计划

### 3.1 预制任务详细设计

#### SCENE-PREF-001: 安装说明书解析器

**任务说明**: 解析LLM安装说明书，提取结构化信息

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/parser/`

```java
// 核心接口设计
public interface InstallationManualParser {
    
    /**
     * 解析安装说明书
     */
    InstallationManual parse(String manualContent);
    
    /**
     * 提取步骤列表
     */
    List<InstallationStep> extractSteps(InstallationManual manual);
    
    /**
     * 提取工具定义
     */
    List<ToolDefinition> extractTools(InstallationManual manual);
    
    /**
     * 提取Schema定义
     */
    Map<String, JsonSchema> extractSchemas(InstallationManual manual);
}

// 数据模型
public class InstallationManual {
    private String skillId;
    private String version;
    private List<InstallationStep> steps;
    private List<ToolDefinition> tools;
    private Map<String, JsonSchema> schemas;
    private Map<String, String> prompts;
}
```

**交付物**:
- InstallationManualParser.java
- InstallationManual.java / InstallationStep.java
- MarkdownManualParser.java（Markdown格式解析）
- YamlManualParser.java（YAML格式解析）

**预计工时**: 3天

---

#### SCENE-PREF-002: 工具定义注册表

**任务说明**: 管理LLM可调用的工具定义

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/tool/`

```java
// 核心接口设计
public interface ToolRegistry {
    
    /**
     * 注册工具
     */
    void registerTool(ToolDefinition tool);
    
    /**
     * 批量注册
     */
    void registerTools(List<ToolDefinition> tools);
    
    /**
     * 获取工具定义
     */
    ToolDefinition getTool(String toolId);
    
    /**
     * 列出分类工具
     */
    List<ToolDefinition> listTools(String category);
    
    /**
     * 获取OpenAI格式Function定义
     */
    List<FunctionDefinition> toOpenAIFunctions();
    
    /**
     * 执行工具调用
     */
    ToolResult execute(String toolId, Map<String, Object> params);
}

// 内置工具实现
public enum BuiltInTools {
    CHECK_DEPENDENCY("check.dependency", "检查依赖"),
    CHECK_PERMISSION("check.permission", "检查权限"),
    QUERY_USER("query.user", "查询用户"),
    QUERY_ORG("query.org", "查询组织"),
    BIND_CAPABILITY("bind.capability", "绑定能力"),
    CREATE_MENU("create.menu", "创建菜单");
    
    // ...
}
```

**交付物**:
- ToolRegistry.java / ToolRegistryImpl.java
- ToolDefinition.java / ToolResult.java
- BuiltInTools.java（内置工具枚举）
- ToolExecutor.java（工具执行器）

**预计工时**: 4天

---

#### SCENE-PREF-003: Schema验证器

**任务说明**: 验证LLM输出是否符合JSON Schema

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/schema/`

```java
// 核心接口设计
public interface SchemaValidator {
    
    /**
     * 注册Schema
     */
    void registerSchema(String schemaId, JsonSchema schema);
    
    /**
     * 验证JSON
     */
    ValidationResult validate(String schemaId, String json);
    
    /**
     * 验证并修复
     */
    ValidationResult validateAndFix(String schemaId, String json);
    
    /**
     * 获取预置Schema
     */
    JsonSchema getPresetSchema(String schemaId);
}

// 预置Schema
public enum PresetSchemas {
    INSTALLATION_STEP("installation-step", "安装步骤输出"),
    ACTIVATION_FLOW("activation-flow", "激活流程输出"),
    CONFIG_VALIDATION("config-validation", "配置验证输出"),
    TOOL_CALL("tool-call", "工具调用输出");
    
    // ...
}
```

**交付物**:
- SchemaValidator.java / SchemaValidatorImpl.java
- ValidationResult.java
- PresetSchemas.java
- JsonSchema.java（Schema模型）

**预计工时**: 3天

---

#### SCENE-PREF-004: 安装状态机

**任务说明**: 管理安装流程的状态流转

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/state/`

```java
// 核心接口设计
public interface InstallationStateMachine {
    
    /**
     * 创建状态机实例
     */
    StateMachineInstance createInstance(String skillId, String userId);
    
    /**
     * 触发事件
     */
    StateTransition trigger(String instanceId, String event);
    
    /**
     * 获取当前状态
     */
    InstallationState getCurrentState(String instanceId);
    
    /**
     * 获取可用事件
     */
    List<String> getAvailableEvents(String instanceId);
    
    /**
     * 保存检查点
     */
    void saveCheckpoint(String instanceId, String checkpointId);
    
    /**
     * 恢复到检查点
     */
    void restoreCheckpoint(String instanceId, String checkpointId);
}

// 状态定义
public enum InstallationState {
    PENDING,           // 待安装
    DOWNLOADING,       // 下载中
    INSTALLING,        // 安装中
    CONFIGURING,       // 配置中
    ACTIVATING,        // 激活中
    COMPLETED,         // 已完成
    FAILED,            // 失败
    ROLLING_BACK       // 回滚中
}
```

**交付物**:
- InstallationStateMachine.java / InstallationStateMachineImpl.java
- StateMachineInstance.java
- StateTransition.java
- InstallationState.java

**预计工时**: 4天

---

#### SCENE-PREF-005: 角色识别服务

**任务说明**: 识别用户在组织中的角色

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/role/`

```java
// 核心接口设计
public interface RoleDetectionService {
    
    /**
     * 检测用户角色
     */
    List<Role> detectRoles(String userId, String sceneGroupId);
    
    /**
     * 获取角色配置
     */
    RoleConfig getRoleConfig(String roleId);
    
    /**
     * 检查角色权限
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * 获取角色菜单
     */
    List<MenuItem> getRoleMenus(String roleId);
}

// 角色定义
public enum StandardRoles {
    MANAGER("manager", "管理者", 100),
    EMPLOYEE("employee", "员工", 80),
    HR("hr", "HR", 90),
    ADMIN("admin", "管理员", 100);
    
    // ...
}
```

**交付物**:
- RoleDetectionService.java / RoleDetectionServiceImpl.java
- Role.java / RoleConfig.java
- StandardRoles.java
- MenuItem.java

**预计工时**: 3天

---

#### SCENE-PREF-006: 知识库预制内容加载

**任务说明**: 加载和管理预制知识库内容

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/knowledge/`

```java
// 核心接口设计
public interface PreloadedKnowledgeLoader {
    
    /**
     * 加载预制知识
     */
    List<KnowledgeDocument> loadPreloadedKnowledge(String category);
    
    /**
     * 导入到知识库
     */
    void importToKnowledgeBase(String kbId, List<KnowledgeDocument> documents);
    
    /**
     * 获取场景模板
     */
    List<SceneTemplate> getSceneTemplates(String sceneType);
    
    /**
     * 获取配置示例
     */
    List<ConfigExample> getConfigExamples(String skillType);
}

// 预制内容分类
public enum KnowledgeCategories {
    SCENE_TEMPLATES("scene-templates", "场景模板"),
    CONFIG_EXAMPLES("config-examples", "配置示例"),
    FAQ("faq", "常见问题"),
    TOOL_GUIDES("tool-guides", "工具使用指南"),
    ERROR_SOLUTIONS("error-solutions", "错误解决方案");
    
    // ...
}
```

**交付物**:
- PreloadedKnowledgeLoader.java
- KnowledgeDocument.java / SceneTemplate.java / ConfigExample.java
- KnowledgeCategories.java
- resources/preloaded/ 目录下的预制内容文件

**预计工时**: 3天

---

#### SCENE-PREF-009: 降级策略处理器

**任务说明**: 处理LLM不可用时的降级逻辑

**预制位置**: `skill-scene/src/main/java/net/ooder/skill/scene/install/degradation/`

```java
// 核心接口设计
public interface DegradationHandler {
    
    /**
     * 检查LLM可用性
     */
    boolean isLlmAvailable();
    
    /**
     * 获取降级级别
     */
    DegradationLevel getCurrentLevel();
    
    /**
     * 执行降级安装
     */
    InstallationResult executeDegradedInstallation(String skillId, Map<String, Object> config);
    
    /**
     * 获取手动配置向导
     */
    List<ManualConfigStep> getManualConfigWizard(String skillId);
    
    /**
     * 验证手动配置
     */
    ValidationResult validateManualConfig(String skillId, Map<String, Object> config);
}

// 降级级别
public enum DegradationLevel {
    FULL_LLM,           // 完整LLM支持
    PARTIAL_LLM,        // 部分LLM支持
    RULE_BASED,         // 基于规则
    MANUAL_ONLY         // 纯手动
}
```

**交付物**:
- DegradationHandler.java / DegradationHandlerImpl.java
- DegradationLevel.java
- ManualConfigStep.java
- ManualConfigWizard.java

**预计工时**: 3天

---

### 3.2 预制任务时间表

```
Week 1: 基础预制组件
├── Day 1-2: SCENE-PREF-001 安装说明书解析器
├── Day 2-4: SCENE-PREF-002 工具定义注册表
└── Day 4-5: SCENE-PREF-003 Schema验证器

Week 2: 核心预制组件
├── Day 1-3: SCENE-PREF-004 安装状态机
├── Day 3-4: SCENE-PREF-005 角色识别服务
└── Day 4-5: SCENE-PREF-006 知识库预制内容加载

Week 3: 高级预制组件
├── Day 1-2: SCENE-PREF-009 降级策略处理器
└── Day 3-5: 集成测试与文档编写
```

---

## 四、与 Engine/SDK 团队的协作接口

### 4.1 预留 Engine 接口

```java
// skill-scene/src/main/java/net/ooder/skill/scene/engine/adapter/

/**
 * Engine接口适配器
 * 在Engine正式实现前，使用本地模拟实现
 */
public interface EngineAdapter {
    
    /**
     * 激活流程引擎接口（预留）
     */
    ActivationEngine getActivationEngine();
    
    /**
     * 菜单生成引擎接口（预留）
     */
    MenuGenerator getMenuGenerator();
    
    /**
     * LLM上下文管理接口（预留）
     */
    LlmContextManager getLlmContextManager();
    
    /**
     * 工具注册中心接口（预留）
     */
    ToolRegistry getToolRegistry();
}

// 模拟实现
@Component
public class MockEngineAdapter implements EngineAdapter {
    
    @Override
    public ActivationEngine getActivationEngine() {
        return new MockActivationEngine(); // 模拟实现
    }
    
    // ...
}
```

### 4.2 预留 SDK 接口

```java
// skill-scene/src/main/java/net/ooder/skill/scene/sdk/adapter/

/**
 * SDK接口适配器
 * 在SDK正式实现前，使用本地模拟实现
 */
public interface SdkAdapter {
    
    /**
     * 工具调用API（预留）
     */
    ToolCallingApi getToolCallingApi();
    
    /**
     * 结构化输出API（预留）
     */
    StructuredOutputApi getStructuredOutputApi();
    
    /**
     * 上下文模板API（预留）
     */
    ContextTemplateApi getContextTemplateApi();
    
    /**
     * 场景协作API（预留）
     */
    SceneCollaborationApi getSceneCollaborationApi();
}

// 模拟实现
@Component
public class MockSdkAdapter implements SdkAdapter {
    
    @Override
    public ToolCallingApi getToolCallingApi() {
        return new MockToolCallingApi(); // 模拟实现
    }
    
    // ...
}
```

---

## 五、预制组件交付标准

### 5.1 代码标准

- [ ] 所有接口有完整的JavaDoc注释
- [ ] 所有实现类有单元测试（覆盖率≥80%）
- [ ] 所有配置有默认值和校验
- [ ] 所有错误有明确的错误码和消息

### 5.2 文档标准

- [ ] 每个预制组件有独立的README.md
- [ ] 接口设计文档完整
- [ ] 使用示例代码可运行
- [ ] 与Engine/SDK的协作接口说明清晰

### 5.3 测试标准

- [ ] 单元测试通过
- [ ] 集成测试通过（使用Mock Engine/SDK）
- [ ] 性能测试通过（响应时间<100ms）
- [ ] 边界条件测试通过

---

## 六、协作沟通机制

### 6.1 每日同步

| 时间 | 参与方 | 内容 |
|------|--------|------|
| 09:30 | Skills Team内部 | 预制任务进度同步 |
| 14:00 | Skills + Engine | 接口设计讨论 |
| 16:00 | Skills + SDK | 接口设计讨论 |

### 6.2 周会安排

| 周次 | 主题 | 参与方 |
|------|------|--------|
| Week 1 | 预制组件设计评审 | 全部 |
| Week 2 | 中期进度检查 | 全部 |
| Week 3 | 预制组件验收 | 全部 |
| Week 4 | 与Engine/SDK集成计划 | 全部 |

### 6.3 文档共享

| 文档 | 位置 | 维护方 |
|------|------|--------|
| 接口设计文档 | docs/api/ | Skills Team |
| 进度跟踪表 | docs/progress/ | Skills Team |
| 问题记录 | docs/issues/ | 全部 |

---

## 七、风险与缓解

### 7.1 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| Engine接口设计变更 | 中 | 高 | 使用适配器模式，隔离变化 |
| SDK接口设计变更 | 中 | 高 | 使用适配器模式，隔离变化 |
| 预制组件性能不达标 | 低 | 中 | 提前进行性能测试 |

### 7.2 协作风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| Engine/SDK延迟交付 | 中 | 高 | 预制组件可独立运行 |
| 接口理解不一致 | 中 | 高 | 每日同步+文档评审 |

---

**文档状态**: 已完成  
**下一步**: 启动Week 1预制组件开发
