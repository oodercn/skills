# A2UI Skill 完整方案梳理与需求覆盖度分析

> **版本**: v2.0  
> **最后更新**: 2026-02-27  
> **状态**: 已修正设计目标

---

## 一、文档概览

| 项目 | 内容 |
|------|------|
| 文档名称 | A2UI Skill 完整方案梳理与需求覆盖度分析 |
| 版本 | v2.0 |
| 日期 | 2026-02-27 |
| 基于 | SDK v2.3 架构 |

---

## 二、设计目标（正确版本）

### 2.1 核心目标

A2UI Skill 的设计目标是将 **Nexus 中的前端模块采用 Skills 架构来完成**，提供一种具有 UI 功能的 Skills。

| 目标 | 描述 | 优先级 |
|------|------|--------|
| **Nexus UI 模块 Skills 化** | 将现有 120+ 个 Nexus HTML 页面转换为 Skills | P0 |
| **UI 能力化管理** | 实现技能配置中的 UI 模块能力管理 | P0 |
| **NLP 生成 UI** | 按照 nexus 架构规范 + apilist + template + script 定义，允许用户使用 NLP 生成 UI 界面 | P1 |
| **能力绑定** | 生成页面中相关组件能够和其他能力结合（字典表、业务属性、数据源、权限等） | P1 |

### 2.2 与错误目标的对比

| 维度 | ❌ 错误目标 | ✅ 正确目标 |
|------|------------|------------|
| **核心定位** | 从设计图生成前端代码 | Nexus UI 模块 Skills 化 |
| **输入来源** | Figma/Sketch 设计文件 | Nexus 现有 HTML 页面 + NLP 指令 |
| **输出产物** | React/Vue/Angular 代码 | Nexus-UI Skill 包 |
| **主要场景** | 设计师→开发者协作 | 用户自定义 Nexus 控制台页面 |
| **技术栈** | 多框架支持 | Nexus 架构规范 (HTML/CSS/JS) |

---

## 三、SDK v2.3 架构概览

### 3.1 模块结构

```
ooder-sdk/
├── agent-sdk/
│   ├── agent-sdk-api/      # API接口层 - Agent、Capability、Skill接口
│   ├── agent-sdk-core/     # 核心实现层 - Agent实现、能力编排
│   ├── skills-framework/   # 技能框架 - Skill生命周期管理
│   ├── llm-sdk-api/        # LLM轻量级API
│   └── llm-sdk/            # LLM完整实现
├── scene-engine/           # 场景引擎 (v2.3)
├── ooder-common/           # 通用组件 (v2.3)
├── ooder-annotation/       # 注解模块 (v2.3)
└── skill-ai/               # AI能力模块 (v2.3 新增)
```

### 3.2 skill-ai 核心能力

| 能力 | 说明 | A2UI 可用性 |
|------|------|-------------|
| `aigc.text-generation` | 文本生成 | ✅ 可用于 NLP 解析、页面生成 |
| `aigc.chat` | 对话能力 | ✅ 可用于交互式页面生成 |
| `workflow.execution` | 工作流执行 | ✅ 可用于页面生成流程 |
| `workflow.management` | 工作流管理 | ✅ 可用于流程编排 |

---

## 四、A2UI 需求覆盖度分析

### 4.1 核心需求

| 需求 | 描述 | SDK 支持 | 实现状态 | 覆盖度 |
|------|------|----------|----------|--------|
| **Nexus UI Skills 化** | 将现有页面转换为 Skills | skills-framework | ⚠️ 框架就绪 | 50% |
| **页面管理** | 页面 CRUD 操作 | agent-sdk-core | ⚠️ 框架就绪 | 50% |
| **NLP 页面生成** | 通过 NLP 指令生成页面 | llm-sdk | ❌ 未实现 | 0% |
| **字典表绑定** | 自动填充字典数据 | - | ❌ 未实现 | 0% |
| **业务属性绑定** | 自动生成表单字段 | - | ❌ 未实现 | 0% |
| **数据源绑定** | 自动加载数据 | - | ❌ 未实现 | 0% |
| **权限绑定** | 自动权限控制 | - | ❌ 未实现 | 0% |

### 4.2 技术需求

| 需求 | 描述 | SDK 支持 | 实现状态 | 覆盖度 |
|------|------|----------|----------|--------|
| **能力注册** | 注册 A2UI 能力到 CapRegistry | ✅ CapRegistry | ⚠️ 框架就绪 | 50% |
| **Agent 通信** | 与其他 Agent 通信 | ⚠️ 接口定义 | ❌ 未实现 | 20% |
| **生命周期管理** | Skill 启动/停止/卸载 | ⚠️ 仅接口 | ❌ 未实现 | 10% |
| **类加载隔离** | 独立的类加载器 | ✅ 完整实现 | ✅ 可用 | 100% |
| **AIGC 集成** | 文本生成能力 | ✅ skill-ai | ❌ 未集成 | 0% |
| **工作流编排** | 页面生成流程 | ✅ skill-ai | ❌ 未集成 | 0% |

### 4.3 总体覆盖度

```
┌─────────────────────────────────────────────────────────────────┐
│                   A2UI 需求覆盖度 (基于 SDK v2.3)               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ 核心需求覆盖度：                                                │
│ ─────────────────────────────────────────────────────────────  │
│ Nexus UI Skills 化  ██████████░░░░░░░░░░  50%                  │
│ 页面管理            ██████████░░░░░░░░░░  50%                  │
│ NLP 页面生成        ░░░░░░░░░░░░░░░░░░░░   0%  (llm-sdk 可用)  │
│ 字典表绑定          ░░░░░░░░░░░░░░░░░░░░   0%                  │
│ 业务属性绑定        ░░░░░░░░░░░░░░░░░░░░   0%                  │
│ 数据源绑定          ░░░░░░░░░░░░░░░░░░░░   0%                  │
│ 权限绑定            ░░░░░░░░░░░░░░░░░░░░   0%                  │
│                                                                 │
│ 技术需求覆盖度：                                                │
│ ─────────────────────────────────────────────────────────────  │
│ 能力注册          ██████████░░░░░░░░░░  50%                    │
│ Agent 通信        ████░░░░░░░░░░░░░░░░  20%                    │
│ 生命周期管理      ██░░░░░░░░░░░░░░░░░░  10%                    │
│ 类加载隔离        ████████████████████  100%                   │
│ AIGC 集成         ░░░░░░░░░░░░░░░░░░░░   0%  (SDK 已支持)      │
│ 工作流编排        ░░░░░░░░░░░░░░░░░░░░   0%  (SDK 已支持)      │
│                                                                 │
│ 总体覆盖度：      ████░░░░░░░░░░░░░░░░  19%                     │
│                                                                 │
│ SDK v2.3 可用能力：                                             │
│ ─────────────────────────────────────────────────────────────  │
│ ✅ skill-ai.aigc - 文本生成、对话                               │
│ ✅ skill-ai.workflow - 工作流定义、执行                         │
│ ✅ CapRegistry - 能力注册                                       │
│ ✅ ClassLoaderManager - 类加载隔离                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 五、完整实现方案（基于 SDK v2.3）

### 5.1 架构设计

```
┌─────────────────────────────────────────────────────────────────┐
│                   A2UI Skill 完整架构 (v2.3)                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ ┌─────────────────────────────────────────────────────────┐   │
│ │                   API 层                                │   │
│ │ /api/pages  /api/generate  /api/bind  /api/health       │   │
│ └─────────────────────────────────────────────────────────┘   │
│                             │                                   │
│ ┌───────────────────────────┴───────────────────────────┐    │
│ │                  Service 层                           │    │
│ │ ├── PageManagementService    # 页面管理              │    │
│ │ ├── PageGenerationService    # NLP 页面生成          │    │
│ │ ├── CapabilityBindingService # 能力绑定              │    │
│ │ └── TemplateManager          # 模板管理              │    │
│ └─────────────────────────────────────────────────────────┘   │
│                             │                                   │
│ ┌───────────────────────────┴───────────────────────────┐    │
│ │                  Capability 层                        │    │
│ │ ├── a2ui.generate-page      # 页面生成能力           │    │
│ │ ├── a2ui.bind-dict          # 字典表绑定             │    │
│ │ ├── a2ui.bind-bizattr       # 业务属性绑定           │    │
│ │ ├── a2ui.bind-datasource    # 数据源绑定             │    │
│ │ └── a2ui.bind-permission    # 权限绑定               │    │
│ └─────────────────────────────────────────────────────────┘   │
│                             │                                   │
│ ┌───────────────────────────┴───────────────────────────┐    │
│ │             SDK v2.3 集成层                           │    │
│ │ ├── skill-ai (AIGC/工作流) - 新增核心能力            │    │
│ │ ├── CapRegistry          # 能力注册                  │    │
│ │ ├── A2ACommunication     # Agent 通信                │    │
│ │ └── EndAgent             # 终端 Agent                │    │
│ └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 核心类设计（使用 skill-ai）

```java
// 1. A2UI 能力定义
public class A2UICapabilities {
    
    // P0: 页面生成能力
    public static final String GENERATE_PAGE = "a2ui.generate-page";
    
    // P0: 页面管理能力
    public static final String LIST_PAGES = "a2ui.list-pages";
    public static final String GET_PAGE = "a2ui.get-page";
    public static final String UPDATE_PAGE = "a2ui.update-page";
    
    // P1: 能力绑定
    public static final String BIND_DICT = "a2ui.bind-dict";
    public static final String BIND_BIZ_ATTR = "a2ui.bind-bizattr";
    public static final String BIND_DATA_SOURCE = "a2ui.bind-datasource";
    public static final String BIND_PERMISSION = "a2ui.bind-permission";
}

// 2. NLP 页面生成服务 (使用 skill-ai.aigc)
public class PageGenerationService {
    
    private final AISkill aiSkill;  // SDK v2.3 skill-ai
    
    public PageGenerationService(AISkill aiSkill) {
        this.aiSkill = aiSkill;
    }
    
    public GenerationResult generateFromNlp(String instruction) {
        // 1. 使用 skill-ai.aigc 解析 NLP 指令
        AIGCResult parseResult = aiSkill.generateText(
            "gpt-4",
            buildParsePrompt(instruction),
            Map.of("task", "parse-nlp")
        ).join();
        
        Intent intent = parseIntent(parseResult.getText());
        
        // 2. 匹配页面模板
        String templateId = templateMatcher.match(intent);
        
        // 3. 能力绑定
        BindingConfig config = capabilityBinder.bind(templateId, intent);
        
        // 4. 渲染页面
        return pageRenderer.render(templateId, config);
    }
    
    private String buildParsePrompt(String instruction) {
        return String.format(
            "解析以下 NLP 指令，提取页面类型、功能需求、实体信息：\n%s",
            instruction
        );
    }
}

// 3. 能力绑定服务
public class CapabilityBindingService {
    
    public BindingResult bindDict(String pageId, List<String> dictTypes) {
        // 绑定字典表到页面组件
        BindingConfig config = new BindingConfig();
        config.setPageId(pageId);
        config.setDictTypes(dictTypes);
        
        // 自动填充下拉框、标签等组件
        return bindingRepository.save(config);
    }
    
    public BindingResult bindPermission(String pageId, List<String> permissions) {
        // 绑定权限到页面操作
        BindingConfig config = new BindingConfig();
        config.setPageId(pageId);
        config.setPermissions(permissions);
        
        // 自动控制按钮、操作的显示/隐藏
        return bindingRepository.save(config);
    }
}

// 4. 页面生成工作流 (使用 skill-ai.workflow)
public class PageGenerationWorkflow {
    
    private final AISkill aiSkill;  // SDK v2.3 skill-ai
    
    public void registerWorkflow() {
        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setWorkflowId("page-generation");
        workflow.setName("NLP 页面生成工作流");
        
        List<WorkflowStep> steps = new ArrayList<>();
        
        // Step 1: NLP 解析 (AIGC)
        WorkflowStep step1 = new WorkflowStep();
        step1.setStepId("nlp-parse");
        step1.setType(WorkflowStep.StepType.AI_GENERATION);
        step1.setRef("llm-sdk");
        steps.add(step1);
        
        // Step 2: 模板匹配
        WorkflowStep step2 = new WorkflowStep();
        step2.setStepId("template-match");
        step2.setType(WorkflowStep.StepType.TOOL_CALL);
        step2.setRef("template-matcher");
        steps.add(step2);
        
        // Step 3: 能力绑定
        WorkflowStep step3 = new WorkflowStep();
        step3.setStepId("capability-bind");
        step3.setType(WorkflowStep.StepType.TOOL_CALL);
        step3.setRef("capability-binder");
        steps.add(step3);
        
        // Step 4: 页面渲染
        WorkflowStep step4 = new WorkflowStep();
        step4.setStepId("page-render");
        step4.setType(WorkflowStep.StepType.TOOL_CALL);
        step4.setRef("page-renderer");
        steps.add(step4);
        
        workflow.setSteps(steps);
        
        aiSkill.registerWorkflow(workflow).join();
    }
}

// 5. A2UI Skill 主类 (集成 SDK v2.3)
public class A2UISkill implements SkillService {
    
    private final CapRegistry capRegistry;
    private final AISkill aiSkill;  // SDK v2.3 skill-ai
    
    private final PageManagementService pageManagementService;
    private final PageGenerationService pageGenerationService;
    private final CapabilityBindingService capabilityBindingService;
    private final PageGenerationWorkflow pageGenerationWorkflow;
    
    public A2UISkill(CapRegistry capRegistry, AISkill aiSkill) {
        this.capRegistry = capRegistry;
        this.aiSkill = aiSkill;
        
        this.pageManagementService = new PageManagementService();
        this.pageGenerationService = new PageGenerationService(aiSkill);
        this.capabilityBindingService = new CapabilityBindingService();
        this.pageGenerationWorkflow = new PageGenerationWorkflow(aiSkill);
    }
    
    @Override
    public void start() {
        // 注册工作流
        pageGenerationWorkflow.registerWorkflow();
        
        // 注册能力
        registerCapabilities();
    }
    
    private void registerCapabilities() {
        // 注册页面生成能力
        Capability generateCap = Capability.builder()
            .capId(A2UICapabilities.GENERATE_PAGE)
            .name("Generate Page")
            .description("通过 NLP 生成 Nexus UI 页面")
            .handler(this::handleGeneratePage)
            .build();
        capRegistry.register(generateCap);
        
        // 注册字典表绑定能力
        Capability bindDictCap = Capability.builder()
            .capId(A2UICapabilities.BIND_DICT)
            .name("Bind Dictionary")
            .description("绑定字典表到页面组件")
            .handler(this::handleBindDict)
            .build();
        capRegistry.register(bindDictCap);
        
        // 注册权限绑定能力
        Capability bindPermCap = Capability.builder()
            .capId(A2UICapabilities.BIND_PERMISSION)
            .name("Bind Permission")
            .description("绑定权限到页面操作")
            .handler(this::handleBindPermission)
            .build();
        capRegistry.register(bindPermCap);
    }
    
    private Object handleGeneratePage(Map<String, Object> params) {
        String instruction = (String) params.get("instruction");
        return pageGenerationService.generateFromNlp(instruction);
    }
    
    private Object handleBindDict(Map<String, Object> params) {
        String pageId = (String) params.get("pageId");
        List<String> dictTypes = (List<String>) params.get("dictTypes");
        return capabilityBindingService.bindDict(pageId, dictTypes);
    }
    
    private Object handleBindPermission(Map<String, Object> params) {
        String pageId = (String) params.get("pageId");
        List<String> permissions = (List<String>) params.get("permissions");
        return capabilityBindingService.bindPermission(pageId, permissions);
    }
}
```

---

## 六、依赖关系

### 6.1 SDK v2.3 依赖

| 依赖 | 版本 | 状态 | 说明 |
|------|------|------|------|
| agent-sdk-api | 2.3 | ✅ 可用 | 核心 API |
| agent-sdk-core | 2.3 | ✅ 可用 | 核心实现 |
| skill-ai | 2.3 | ✅ 可用 | AIGC/工作流 |
| llm-sdk | 2.3 | ✅ 可用 | LLM 能力 |
| scene-engine | 2.3 | ✅ 可用 | 场景引擎 |

### 6.2 Maven 依赖配置

```xml
<dependencies>
    <!-- ⚠️ 重要：agent-sdk 是父工程，不能作为依赖 -->
    
    <!-- SDK v2.3 核心 API -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-api</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- SDK v2.3 核心实现 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- skill-ai: AIGC/工作流能力 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-ai</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- LLM SDK -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>2.3</version>
    </dependency>
</dependencies>
```

---

## 七、实现路线

### 7.1 Phase 1: SDK 升级与基础能力（1 周）

| 任务 | 工作量 | 依赖 | 交付物 |
|------|--------|------|--------|
| 升级 SDK 到 v2.3 | 0.5天 | SDK v2.3 | pom.xml 更新 |
| A2UISkill 主类实现 | 1天 | agent-sdk-api | A2UISkill.java |
| 能力注册到 CapRegistry | 0.5天 | CapRegistry | 能力注册逻辑 |
| PageManagementService 实现 | 2天 | agent-sdk-core | 页面管理服务 |
| API 端点实现 | 1天 | Spring Boot | REST API |

### 7.2 Phase 2: 能力绑定实现（1 周）

| 任务 | 工作量 | 依赖 | 交付物 |
|------|--------|------|--------|
| 字典表绑定实现 | 1.5天 | - | DictBindingService |
| 业务属性绑定实现 | 1.5天 | - | BizAttrBindingService |
| 数据源绑定实现 | 1天 | - | DataSourceBindingService |
| 权限绑定实现 | 1天 | - | PermissionBindingService |

### 7.3 Phase 3: NLP 生成（1 周）

| 任务 | 工作量 | 依赖 | 交付物 |
|------|--------|------|--------|
| NLP 解析引擎集成 | 2天 | skill-ai.aigc | NlpParseService |
| 页面模板系统 | 2天 | - | TemplateManager |
| 页面生成服务 | 1天 | skill-ai.workflow | PageGenerationService |

### 7.4 Phase 4: 页面转换（2 周）

| 任务 | 工作量 | 依赖 | 交付物 |
|------|--------|------|--------|
| P0 页面转换（5个） | 5天 | 全部完成 | Nexus-UI Skills |
| P1 页面转换（10个） | 5天 | 全部完成 | Nexus-UI Skills |
| 测试与文档 | 2天 | 全部完成 | 测试用例、文档 |

---

## 八、风险与建议

### 8.1 风险评估

| 风险 | 等级 | 说明 | 应对措施 |
|------|------|------|----------|
| SDK 组件未完成 | 🔴 高 | LifecycleManager 等仅接口 | 优先实现 P0 组件 |
| NLP 解析准确率 | 🟡 中 | 意图识别可能不准确 | 建立评估机制，迭代优化 |
| 页面模板覆盖度 | 🟡 中 | 模板可能不够全面 | 先覆盖高频场景，逐步扩展 |

### 8.2 建议

1. **优先使用 skill-ai 能力**：直接使用 AIGC/工作流，避免重复开发
2. **升级到 SDK v2.3**：利用新架构的模块化和标准化能力
3. **渐进式实现**：先实现核心功能，再扩展高级功能
4. **建立测试机制**：NLP 解析质量评估、页面生成回归测试

---

## 九、总结

| 项目 | 当前状态 | 目标状态 | 差距 |
|------|----------|----------|------|
| SDK 版本 | 0.7.1 | 2.3 | 需升级 |
| 核心需求覆盖度 | 19% | 80% | 需实现全部核心功能 |
| 技术需求覆盖度 | 38% | 90% | 需完成 SDK 组件集成 |
| skill-ai 集成 | 0% | 100% | 需集成 AIGC/工作流 |
| 总体覆盖度 | 19% | 85% | 需 5 周工作量 |

**关键路径**：
```
SDK v2.3 升级 → skill-ai 集成 → A2UI 基础能力 → 能力绑定 → NLP 生成 → 页面转换
    Day 1         Day 2-3        Day 4-7       Day 8-14    Day 15-21   Day 22-35
```

---

## 十、相关文档

| 文档 | 说明 |
|------|------|
| [A2UI_SKILL_DESIGN_SUMMARY.md](A2UI_SKILL_DESIGN_SUMMARY.md) | 设计方案汇总 |
| [A2UI_SKILL_CORRECTED_DESIGN.md](A2UI_SKILL_CORRECTED_DESIGN.md) | 修正后的设计目标 |
| [A2UI_SKILL_DEVELOPMENT_BREAKDOWN.md](A2UI_SKILL_DEVELOPMENT_BREAKDOWN.md) | 开发方案分解 |
| [NEXUS_UI_SKILL_ARCHITECTURE.md](../NEXUS_UI_SKILL_ARCHITECTURE.md) | Nexus-UI Skill 类型规范 |
| [NEXUS_UI_SKILL_COMPLETE_SPEC.md](../NEXUS_UI_SKILL_COMPLETE_SPEC.md) | 完整技术规范 |

---

**文档版本**：v2.0  
**创建日期**：2026-02-27  
**最后更新**：2026-02-27
