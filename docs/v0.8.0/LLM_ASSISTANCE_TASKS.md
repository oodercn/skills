# LLM 协助任务说明

## 一、背景

Ooder Agent SDK v0.8.0 正在进行架构升级，部分模块已完成接口定义，需要 LLM 协助完成以下任务。

---

## 二、任务概览

| 任务ID | 任务名称 | 优先级 | 状态 | 协助类型 |
|--------|----------|--------|------|----------|
| WILL | 意志表达模型 | P0 | 待设计 | 架构设计 |
| ASSET | 数字资产治理 | P1 | 待设计 | 架构设计 |
| REACH | LLM 触达能力 | P1 | 待设计 | 架构设计 |
| IMPL | 业务逻辑实现 | P1 | 待实现 | 代码实现 |
| SKILL-MD | SKILL.md 直接解析支持 | P1 | 待设计 | 架构设计 + 代码实现 |

---

## 三、任务详情

### 任务1：意志表达模型（WILL）

**优先级**：P0  
**工作量**：6人天  
**当前状态**：未定义

**任务描述**：
设计并实现意志表达模型，支持管理者意志的解析和执行。意志分为三个层次：

#### 1.1 意志层次定义

| 层次 | 格式 | 示例 | 属性 |
|------|------|------|------|
| 战略意志 | "我们希望[目标]在[时间]内实现" | "我们希望在下个季度将生产效率提高10%" | goal, timeline, priority, constraints |
| 战术意志 | "请[动作][对象]以实现[目标]" | "请优化生产流程以降低生产成本" | action, object, goal, resources |
| 执行意志 | "[动作][对象]在[时间]前完成" | "完成生产线的维护工作在本周五前" | action, object, deadline, responsible |

#### 1.2 意志处理流程

```
意志输入 → 意志理解 → 意志转化 → 意志执行

意志理解：
├── 语义理解：理解意志的字面含义
├── 意图推理：推理意志背后的真实意图
├── 约束识别：识别意志的约束条件
└── 优先级判断：判断意志的优先级

意志转化：
├── 目标分解：将意志分解为可执行的目标
├── 方案生成：生成实现意志的方案
├── 资源规划：规划实现意志所需的资源
└── 风险评估：评估实现意志的风险

意志执行：
├── 任务分配：将任务分配给合适的人员或 Agent
├── 执行监控：监控意志执行的过程
├── 效果评估：评估意志实现的效果
└── 反馈调整：根据反馈调整执行策略
```

#### 1.3 意志类型与业务映射

| 意志类型 | 适用场景 | 对应业务 | 执行方式 |
|----------|----------|----------|----------|
| 战略意志 | 企业战略目标、发展方向 | ERP、HR 战略规划 | 长期任务分解 |
| 战术意志 | 资源分配、流程优化 | OA、CRM 流程优化 | 中期任务编排 |
| 执行意志 | 具体任务、操作指令 | 设备控制、数据处理 | 即时任务执行 |

#### 1.4 意志与 Agent 体系的关系

```
战略意志 → SceneAgent（场景创建）
├── 创建长期运行场景
├── 定义场景目标
└── 设置场景优先级

战术意志 → WorkerAgent（任务编排）
├── 编排多个 WorkerAgent
├── 定义工作流程
└── 分配资源

执行意志 → Command（即时执行）
├── 生成具体 Command
├── 调用 CAP 能力
└── 执行设备操作
```

**输入参考**：
```
协议文档：E:\github\super-Agent\protocol-release\v0.8.0\northbound\northbound-protocol-spec.md
相关章节：2.2 管理者意志体现
```

**期望输出**：
1. 意志表达模型接口定义
2. 意志解析器实现（语义理解、意图推理、约束识别、优先级判断）
3. 意志转化器（目标分解、方案生成、资源规划、风险评估）
4. 意志执行监控（任务分配、执行监控、效果评估、反馈调整）
5. 与 SceneAgent 决策机制的集成

**格式要求**：
```java
// 意志类型枚举
public enum WillType {
    STRATEGIC,    // 战略意志
    TACTICAL,     // 战术意志
    EXECUTION     // 执行意志
}

// 意志状态枚举
public enum WillStatus {
    EXPRESSED,    // 已表达
    UNDERSTOOD,   // 已理解
    TRANSFORMED,  // 已转化
    EXECUTING,    // 执行中
    COMPLETED,    // 已完成
    FAILED        // 失败
}

// 意志表达模型接口
public interface WillExpression {
    String getWillId();
    WillType getType();
    WillStatus getStatus();
    
    // 战略意志属性
    String getGoal();
    String getTimeline();
    int getPriority();
    List<String> getConstraints();
    
    // 战术意志属性
    String getAction();
    String getObject();
    String getResourceRequirements();
    
    // 执行意志属性
    String getDeadline();
    String getResponsible();
    
    // 元数据
    String getOriginalText();
    Instant getCreatedAt();
    String getCreatedBy();
}

// 意志解析器接口
public interface WillParser {
    WillParseResult parse(String naturalLanguage);
    
    // 语义理解
    String understandSemantics(String text);
    
    // 意图推理
    String inferIntent(String text);
    
    // 约束识别
    List<String> identifyConstraints(String text);
    
    // 优先级判断
    int judgePriority(String text);
}

// 意志转化器接口
public interface WillTransformer {
    WillTransformResult transform(WillExpression will);
    
    // 目标分解
    List<String> decomposeGoals(WillExpression will);
    
    // 方案生成
    List<ExecutionPlan> generatePlans(WillExpression will);
    
    // 资源规划
    ResourcePlan planResources(WillExpression will);
    
    // 风险评估
    RiskAssessment assessRisks(WillExpression will);
}

// 意志执行器接口
public interface WillExecutor {
    WillExecutionResult execute(WillExpression will, ExecutionPlan plan);
    
    // 任务分配
    void assignTasks(WillExpression will, List<Task> tasks);
    
    // 执行监控
    ExecutionStatus monitorExecution(String willId);
    
    // 效果评估
    EffectEvaluation evaluateEffect(String willId);
    
    // 反馈调整
    void adjustStrategy(String willId, Feedback feedback);
}

// 意志管理器接口
public interface WillManager {
    // 表达意志
    WillExpression expressWill(String willText, WillType type, int priority);
    
    // 查询意志
    Optional<WillExpression> getWill(String willId);
    List<WillExpression> getWillsByType(WillType type);
    List<WillExpression> getActiveWills();
    
    // 取消意志
    void cancelWill(String willId);
    
    // 更新意志
    void updateWill(String willId, Map<String, Object> updates);
}
```

**实现示例**：
```java
// 意志解析器实现示例
public class WillParserImpl implements WillParser {
    
    private final NlpInteractionApi nlpApi;
    
    @Override
    public WillParseResult parse(String naturalLanguage) {
        WillParseResult result = new WillParseResult();
        
        // 1. 语义理解
        String semantics = nlpApi.processNLPInput(
            NlpInput.builder()
                .text(naturalLanguage)
                .type(InputType.SEMANTIC_ANALYSIS)
                .build()
        ).getParsedText();
        result.setSemantics(semantics);
        
        // 2. 意图推理
        Intent intent = nlpApi.extractIntent(
            NlpInput.builder()
                .text(naturalLanguage)
                .type(InputType.INTENT_EXTRACTION)
                .build()
        );
        result.setIntent(intent);
        
        // 3. 约束识别
        List<Entity> entities = nlpApi.extractEntity(
            NlpInput.builder()
                .text(naturalLanguage)
                .type(InputType.ENTITY_EXTRACTION)
                .build()
        );
        List<String> constraints = entities.stream()
            .filter(e -> e.getType() == EntityType.CONSTRAINT)
            .map(Entity::getValue)
            .collect(Collectors.toList());
        result.setConstraints(constraints);
        
        // 4. 优先级判断
        int priority = inferPriority(intent, constraints);
        result.setPriority(priority);
        
        // 5. 意志类型判断
        WillType type = inferWillType(naturalLanguage, intent);
        result.setType(type);
        
        return result;
    }
    
    private WillType inferWillType(String text, Intent intent) {
        if (text.contains("我们希望") || text.contains("战略目标")) {
            return WillType.STRATEGIC;
        } else if (text.contains("请") && text.contains("以实现")) {
            return WillType.TACTICAL;
        } else {
            return WillType.EXECUTION;
        }
    }
}
```

---

### 任务2：数字资产治理（ASSET）

**优先级**：P1  
**工作量**：8人天  
**当前状态**：未定义

**任务描述**：
设计并实现数字资产治理体系，与 Place/Zone/Device 体系对应。资产分为四类：
- 设备资产：生产设备、网络设备、存储设备、终端设备、传感器设备
- 数据资产：业务数据、运营数据、财务数据、客户数据、知识数据
- Agent 资产：AI 智能体、自动化代理、监控代理、协调代理
- 资源资产：计算资源、存储资源、网络资源、服务资源

**输入参考**：
```
协议文档：E:\github\super-Agent\protocol-release\v0.8.0\digital-asset\digital-asset-governance.md
架构文档：E:\github\ooder-skills\docs\v0.8.0\ARCHITECTURE-V0.8.0.md
```

**期望输出**：
1. 数字资产分类模型
2. 设备资产管理（与 Place/Zone/Device 对应）
3. 数据资产管理
4. Agent 资产管理
5. 资源资产管理
6. 资产治理接口

**格式要求**：
```java
// 数字资产接口示例
public interface DigitalAsset {
    String getAssetId();
    AssetType getType(); // DEVICE, DATA, AGENT, RESOURCE
    AssetCategory getCategory();
    String getName();
    String getDescription();
    AssetStatus getStatus();
    Map<String, Object> getMetadata();
}

// 资产治理接口示例
public interface AssetGovernance {
    void registerAsset(DigitalAsset asset);
    void updateAsset(String assetId, Map<String, Object> updates);
    void decommissionAsset(String assetId);
    List<DigitalAsset> queryAssets(AssetQuery query);
}
```

---

### 任务3：LLM 触达能力（REACH）

**优先级**：P1  
**工作量**：6人天  
**当前状态**：未定义

**任务描述**：
设计并实现 LLM 触达能力，使 LLM 能够直接操作物理设备和虚拟资源。触达协议格式：
```
REACH://[device_type]/[device_id]/[action]?[params]
```

**输入参考**：
```
协议文档：E:\github\super-Agent\protocol-release\v0.8.0\northbound\northbound-protocol-spec.md
相关概念：LLM 触达能力章节
```

**期望输出**：
1. 触达协议定义
2. 物理设备触达实现（路由器/交换机/防火墙/摄像头/传感器）
3. 虚拟资源触达实现（数据库/文件系统/API/消息队列）
4. 触达安全机制（认证/授权/审计）
5. 与 Command 体系的集成

**格式要求**：
```java
// 触达协议接口示例
public interface ReachProtocol {
    String getProtocol(); // "REACH"
    String getDeviceType();
    String getDeviceId();
    String getAction();
    Map<String, Object> getParams();
}

// 触达执行器接口示例
public interface ReachExecutor {
    ReachResult execute(ReachProtocol protocol);
    boolean supports(String deviceType);
}
```

---

### 任务4：业务逻辑实现（IMPL）

**优先级**：P1  
**工作量**：15人天  
**当前状态**：桩实现

**任务描述**：
将 LLM-SDK 的桩实现转换为具体业务逻辑实现。当前所有接口方法抛出 UnsupportedOperationException。

**输入参考**：
```
LLM-SDK 源码：E:\github\ooder-sdk\llm-sdk\
进度报告：E:\github\ooder-sdk\LLM_SDK_PROGRESS_REPORT.md
```

**需要实现的模块**：
1. MemoryBridgeApi - 记忆桥接
2. NlpInteractionApi - NLP 交互
3. SchedulingApi - 资源调度
4. SecurityApi - 安全认证
5. MonitoringApi - 监控统计
6. MultiLlmAdapterApi - 多 LLM 适配
7. CapabilityRequestApi - 能力申请

**期望输出**：
1. 每个接口的具体实现类
2. 业务逻辑处理
3. 异常处理
4. 日志记录
5. 单元测试

**格式要求**：
```java
// 实现示例
public class MemoryBridgeApiImpl implements MemoryBridgeApi {
    
    private final MemoryStorage storage;
    
    @Override
    public BridgeResult bridgeToAgentMemory(String agentId, MemoryContent content) {
        // 1. 验证参数
        validateAgentId(agentId);
        validateContent(content);
        
        // 2. 存储记忆
        storage.store(agentId, content);
        
        // 3. 返回结果
        return BridgeResult.success(agentId, content.getId());
    }
    
    // ... 其他方法实现
}
```

---

## 四、约束条件

### 4.1 技术约束

| 约束 | 说明 |
|------|------|
| Java 版本 | Java 8+ |
| 构建工具 | Maven |
| 日志框架 | SLF4J |
| 单元测试 | JUnit 5 |
| 代码规范 | Google Java Style |

### 4.2 架构约束

| 约束 | 说明 |
|------|------|
| 包结构 | net.ooder.sdk.{module}.* |
| 接口定义 | 先定义接口，再实现 |
| 模型类 | 不可变对象，使用 Builder 模式 |
| 异常处理 | 自定义异常，不使用 RuntimeException |

### 4.3 文档约束

| 约束 | 说明 |
|------|------|
| 代码注释 | Javadoc 格式 |
| README | 每个模块需要 README.md |
| API 文档 | 使用 Swagger 注解 |

---

## 五、交付标准

### 5.1 代码交付

- [ ] 所有接口定义完成
- [ ] 所有实现类完成
- [ ] 单元测试覆盖率 > 80%
- [ ] 代码通过 Checkstyle 检查
- [ ] 代码通过 FindBugs 检查

### 5.2 文档交付

- [ ] 架构设计文档
- [ ] API 参考文档
- [ ] 使用示例文档
- [ ] 变更日志

### 5.3 验证标准

- [ ] 编译通过
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 性能测试通过

---

## 七、新增任务：SKILL.md 支持

### 任务5：SKILL.md 直接解析支持（SKILL-MD）

**优先级**：P1  
**工作量**：12人天  
**当前状态**：待设计

**背景说明**：
业界主流 AI-IDE（Trae、Claude Code、Cursor 等）已采用 Agent Skills 开放标准，支持通过 SKILL.md 文件定义技能。Ooder 需要兼容这一标准，实现 SKILL.md 直接解析执行。

**任务描述**：
实现 SKILL.md 直接解析和执行，让用户可以通过编写 Markdown 文件定义技能，无需编写 Java 代码。

#### 7.1 SKILL.md 标准格式定义

```markdown
# 技能名称

## 元数据
- id: skill-{name}
- version: 1.0.0
- author: {author}

## 描述
技能的详细描述。

## 触发条件
- 触发条件1
- 触发条件2

## 能力
- cap-id-1: 能力描述1
- cap-id-2: 能力描述2

## 执行步骤

### Step 1: 步骤名称
步骤描述和执行说明。

### Step 2: 步骤名称
步骤描述和执行说明。

## 注意事项
- 注意事项1
- 注意事项2

## 模板
{{template:template-file.md}}

## 资源
- template: templates/template-file.md
- config: config/config.json
```

#### 7.2 需要实现的组件

| 组件 | 职责 | 工作量 |
|------|------|--------|
| SkillMdParser | 解析 SKILL.md 文件 | 2天 |
| SkillMdRegistry | 注册和发现 SKILL.md 技能 | 2天 |
| SkillExecutionEngine | 执行 SKILL.md 技能 | 3天 |
| SkillRouter | 路由到不同类型的技能 | 2天 |
| SkillMdDiscovery | 扩展发现协议支持 SKILL.md | 2天 |
| 测试和文档 | 单元测试、集成测试、文档 | 1天 |

#### 7.3 与现有架构的集成

```
用户请求
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  SkillRouter                                                 │
│  ├── SKILL.md 技能 → SkillMdExecutionEngine                 │
│  └── Provider 技能 → ProviderInvoker                        │
└─────────────────────────────────────────────────────────────┘
    │
    ├─── SKILL.md 路径 ───────────────────┐
    │                                     ▼
    │                          ┌─────────────────────┐
    │                          │ SkillMdParser       │
    │                          │ SkillMdRegistry     │
    │                          │ SkillExecutionEngine│
    │                          └─────────────────────┘
    │                                     │
    │                                     ▼
    │                          ┌─────────────────────┐
    │                          │ LlmProvider         │
    │                          │ (已实现)            │
    │                          └─────────────────────┘
    │
    └─── Provider 路径 ──────────────────┐
                                         ▼
                              ┌─────────────────────┐
                              │ ProviderInvoker     │
                              │ (已实现)            │
                              └─────────────────────┘
```

#### 7.4 接口定义

```java
// SKILL.md 解析器
public interface SkillMdParser {
    SkillDefinition parse(String markdown);
    SkillDefinition parse(File skillMdFile);
}

// SKILL.md 注册表
public interface SkillMdRegistry {
    void register(File skillMdFile);
    void register(String skillId, SkillDefinition definition);
    void unregister(String skillId);
    Optional<SkillDefinition> getSkill(String skillId);
    List<SkillDefinition> findSkills(String trigger);
    List<SkillDefinition> findSkillsByCapability(String capability);
    void scanDirectory(File directory);
}

// 技能执行引擎
public interface SkillExecutionEngine {
    CompletableFuture<SkillExecutionResult> execute(
        SkillDefinition skill, 
        SkillExecutionContext context
    );
}

// 技能路由器
public interface SkillRouter {
    SkillExecutionResult route(String userInput, SkillExecutionContext context);
    void registerSkillType(String type, SkillHandler handler);
}
```

#### 7.5 已有条件

| 条件 | 状态 | 说明 |
|------|------|------|
| LlmProvider | ✅ 已实现 | OpenAI, Ollama, 通义千问等 |
| DiscoveryProtocol | ⚠️ 部分实现 | 需要扩展支持 SKILL.md |
| SkillManifest | ✅ 已实现 | 可复用部分逻辑 |
| 记忆体系 | ❌ 未实现 | LLM-SDK 已定义接口 |
| SKILL.md 解析器 | ❌ 未实现 | 需要新增 |
| 执行引擎 | ❌ 未实现 | 需要新增 |

#### 7.6 交付物

- [ ] SKILL.md 标准格式文档
- [ ] SkillMdParser 实现
- [ ] SkillMdRegistry 实现
- [ ] SkillExecutionEngine 实现
- [ ] SkillRouter 实现
- [ ] 单元测试覆盖率 > 80%
- [ ] 使用示例文档

---

## 八、时间安排（更新）

| 阶段 | 时间 | 任务 |
|------|------|------|
| 第1周 | 2026-02-24 ~ 2026-03-02 | WILL 意志表达模型设计 |
| 第2周 | 2026-03-03 ~ 2026-03-09 | ASSET 数字资产治理设计 |
| 第3周 | 2026-03-10 ~ 2026-03-16 | REACH LLM 触达能力设计 |
| 第4周 | 2026-03-17 ~ 2026-03-23 | SKILL-MD 直接解析支持 |
| 第5-6周 | 2026-03-24 ~ 2026-04-06 | IMPL 业务逻辑实现 + 测试文档 |

---

## 九、联系方式

如有问题，请通过以下方式联系：
- 项目仓库：https://github.com/ooder/ooder-skills
- 问题反馈：GitHub Issues

---

**文档版本**：v1.1  
**创建日期**：2026-02-24  
**最后更新**：2026-02-24
