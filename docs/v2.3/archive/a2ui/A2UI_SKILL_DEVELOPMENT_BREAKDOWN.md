# A2UI Skill 开发方案分解

> **版本**: v2.0  
> **最后更新**: 2026-02-27  
> **状态**: 已修正设计目标

---

## 一、项目概述

### 1.1 项目背景

A2UI Skill 是基于 Ooder SDK v2.3 开发的 **Nexus UI 模块 Skills 化** 技能，旨在将 Nexus 现有前端模块采用 Skills 架构封装，实现 UI 能力化管理。

### 1.2 核心目标

| 目标 | 描述 | 优先级 |
|------|------|--------|
| **Nexus UI 模块 Skills 化** | 将现有 120+ 个 Nexus HTML 页面转换为 Skills | P0 |
| **UI 能力化管理** | 实现技能配置中的 UI 模块能力管理 | P0 |
| **NLP 生成 UI** | 按照 nexus 架构规范 + apilist + template + script 定义，允许用户使用 NLP 生成 UI 界面 | P1 |
| **能力绑定** | 生成页面中相关组件能够和其他能力结合（字典表、业务属性、数据源、权限等） | P1 |

### 1.3 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| SDK | Ooder SDK v2.3 | agent-sdk-api, agent-sdk-core, llm-sdk |
| LLM | GPT-4, Claude | NLP 解析、页面生成 |
| 前端 | HTML/CSS/JS | Nexus 架构规范 |
| 组件库 | Nexus UI 组件 | nx-card, nx-btn 等 |
| 图标 | Remix Icon | ri-* 前缀 |

---

## 二、功能模块分解

### 2.1 核心功能模块

```
┌─────────────────────────────────────────────────────────────────┐
│                    A2UI Skill 功能架构                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    API 层                                │   │
│  │  /api/pages  /api/generate  /api/bind  /api/health       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │                   Service 层                           │    │
│  │  ├── PageManagementService    # 页面管理              │    │
│  │  ├── PageGenerationService    # NLP 页面生成          │    │
│  │  ├── CapabilityBindingService # 能力绑定              │    │
│  │  └── TemplateManager          # 模板管理              │    │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │                   Capability 层                        │    │
│  │  ├── a2ui.generate-page      # 页面生成能力           │    │
│  │  ├── a2ui.bind-dict          # 字典表绑定             │    │
│  │  ├── a2ui.bind-bizattr       # 业务属性绑定           │    │
│  │  ├── a2ui.bind-datasource    # 数据源绑定             │    │
│  │  └── a2ui.bind-permission    # 权限绑定               │    │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │              SDK v2.3 集成层                           │    │
│  │  ├── llm-sdk (NLP 解析、文本生成)                      │    │
│  │  ├── agent-sdk-core (CapRegistry, A2A 消息)           │    │
│  │  └── scene-engine (Workflow, Discovery)               │    │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 模块详细分解

#### 2.2.1 页面管理模块

| 功能 | 描述 | 输入 | 输出 |
|------|------|------|------|
| 页面列表 | 获取所有 Nexus-UI Skill 页面 | 无 | 页面列表 |
| 页面详情 | 获取单个页面配置 | 页面 ID | 页面配置 |
| 页面更新 | 更新页面配置 | 页面配置 | 更新结果 |
| 页面删除 | 删除页面 | 页面 ID | 删除结果 |

**SDK 依赖**：
- `agent-sdk-core`: `net.ooder.sdk.api.capability.CapRegistry` - 能力注册

#### 2.2.2 NLP 页面生成模块

| 功能 | 描述 | 输入 | 输出 |
|------|------|------|------|
| 意图解析 | 解析用户 NLP 指令 | NLP 文本 | 意图对象 |
| 模板匹配 | 匹配页面模板 | 意图对象 | 模板 ID |
| 页面生成 | 生成 HTML 页面 | 模板 + 参数 | HTML 文件 |

**SDK 依赖**：
- `llm-sdk`: `net.ooder.sdk.llm.LlmSdk` - NLP 处理
- `agent-sdk-core`: `net.ooder.sdk.api.nlp.NlpInteractionApi` - NLP 交互

#### 2.2.3 能力绑定模块

| 功能 | 描述 | 输入 | 输出 |
|------|------|------|------|
| 字典表绑定 | 绑定字典表到页面组件 | 字典类型列表 | 绑定结果 |
| 业务属性绑定 | 绑定业务属性到表单 | 属性配置 | 绑定结果 |
| 数据源绑定 | 绑定 API 数据源 | API 配置 | 绑定结果 |
| 权限绑定 | 绑定权限到操作 | 权限配置 | 绑定结果 |

**SDK 依赖**：
- `agent-sdk-core`: `net.ooder.sdk.api.capability.CapRegistry` - 能力注册
- `scene-engine`: `net.ooder.scene.workflow.WorkflowEngine` - 工作流编排

---

## 三、任务分解

### 3.1 Phase 1: 基础框架（1 周）

| 任务ID | 任务名称 | 模块 | 包路径 | 工作量 | 委派团队 |
|--------|----------|------|--------|--------|----------|
| A2UI-001 | 项目初始化 | skill-a2ui | `net.ooder.skill.a2ui` | 0.5天 | Skills 团队 |
| A2UI-002 | SDK 依赖配置 | skill-a2ui | `pom.xml` | 0.5天 | Skills 团队 |
| A2UI-003 | CapRegistry 注册 | skill-a2ui | `net.ooder.skill.a2ui.capability` | 1天 | Skills 团队 |
| A2UI-004 | API 端点定义 | skill-a2ui | `net.ooder.skill.a2ui.api` | 1天 | Skills 团队 |
| A2UI-005 | 页面管理服务 | skill-a2ui | `net.ooder.skill.a2ui.service` | 2天 | Skills 团队 |

### 3.2 Phase 2: 能力绑定（1 周）

| 任务ID | 任务名称 | 模块 | 包路径 | 工作量 | 委派团队 |
|--------|----------|------|--------|--------|----------|
| A2UI-006 | 字典表绑定服务 | skill-a2ui | `net.ooder.skill.a2ui.binding` | 1.5天 | Skills 团队 |
| A2UI-007 | 业务属性绑定服务 | skill-a2ui | `net.ooder.skill.a2ui.binding` | 1.5天 | Skills 团队 |
| A2UI-008 | 数据源绑定服务 | skill-a2ui | `net.ooder.skill.a2ui.binding` | 1天 | Skills 团队 |
| A2UI-009 | 权限绑定服务 | skill-a2ui | `net.ooder.skill.a2ui.binding` | 1天 | Skills 团队 |

### 3.3 Phase 3: NLP 生成（1 周）

| 任务ID | 任务名称 | 模块 | 包路径 | 工作量 | 委派团队 |
|--------|----------|------|--------|--------|----------|
| A2UI-010 | NLP 解析服务 | skill-a2ui | `net.ooder.skill.a2ui.nlp` | 2天 | LLM-SDK 团队 |
| A2UI-011 | 页面模板系统 | skill-a2ui | `net.ooder.skill.a2ui.template` | 2天 | Skills 团队 |
| A2UI-012 | 页面生成服务 | skill-a2ui | `net.ooder.skill.a2ui.generation` | 1天 | Skills 团队 |

### 3.4 Phase 4: 页面转换（2 周）

| 任务ID | 任务名称 | 模块 | 包路径 | 工作量 | 委派团队 |
|--------|----------|------|--------|--------|----------|
| A2UI-013 | P0 页面转换（5个） | nexus-ui | `skills/` | 5天 | Skills 团队 |
| A2UI-014 | P1 页面转换（10个） | nexus-ui | `skills/` | 5天 | Skills 团队 |
| A2UI-015 | 测试与优化 | skill-a2ui | - | 2天 | Skills 团队 |
| A2UI-016 | 文档编写 | skill-a2ui | - | 1天 | Skills 团队 |

---

## 四、核心类设计

### 4.1 能力定义

```java
package net.ooder.skill.a2ui.capability;

public class A2UICapabilities {
    
    // P0: 页面生成能力
    public static final String GENERATE_PAGE = "a2ui.generate-page";
    
    // P0: 页面管理能力
    public static final String LIST_PAGES = "a2ui.list-pages";
    public static final String GET_PAGE = "a2ui.get-page";
    public static final String UPDATE_PAGE = "a2ui.update-page";
    public static final String DELETE_PAGE = "a2ui.delete-page";
    
    // P1: 能力绑定
    public static final String BIND_DICT = "a2ui.bind-dict";
    public static final String BIND_BIZ_ATTR = "a2ui.bind-bizattr";
    public static final String BIND_DATA_SOURCE = "a2ui.bind-datasource";
    public static final String BIND_PERMISSION = "a2ui.bind-permission";
}
```

### 4.2 服务接口

```java
package net.ooder.skill.a2ui.service;

public interface PageManagementService {
    
    List<NexusUiPage> listPages();
    
    NexusUiPage getPage(String pageId);
    
    PageUpdateResult updatePage(String pageId, PageConfig config);
    
    PageDeleteResult deletePage(String pageId);
}

public interface PageGenerationService {
    
    GenerationResult generateFromNlp(String nlpInstruction);
    
    TemplateMatchResult matchTemplate(Intent intent);
    
    String renderPage(String templateId, Map<String, Object> params);
}

public interface CapabilityBindingService {
    
    BindingResult bindDict(String pageId, List<String> dictTypes);
    
    BindingResult bindBizAttr(String pageId, List<BizAttrConfig> attrs);
    
    BindingResult bindDataSource(String pageId, DataSourceConfig config);
    
    BindingResult bindPermission(String pageId, List<String> permissions);
}
```

### 4.3 页面生成工作流

```java
package net.ooder.skill.a2ui.workflow;

public class PageGenerationWorkflow {
    
    public static final String WORKFLOW_ID = "page-generation";
    
    public WorkflowDefinition createWorkflow() {
        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setWorkflowId(WORKFLOW_ID);
        workflow.setName("NLP 页面生成工作流");
        
        List<WorkflowStep> steps = Arrays.asList(
            createNlpParseStep(),
            createTemplateMatchStep(),
            createCapabilityBindStep(),
            createPageRenderStep()
        );
        
        workflow.setSteps(steps);
        return workflow;
    }
    
    private WorkflowStep createNlpParseStep() {
        WorkflowStep step = new WorkflowStep();
        step.setStepId("nlp-parse");
        step.setType(WorkflowStep.StepType.AI_GENERATION);
        step.setRef("llm-sdk");
        step.setParams(Map.of(
            "task", "parse-nlp",
            "output", "intent,entity,params"
        ));
        return step;
    }
    
    private WorkflowStep createTemplateMatchStep() {
        WorkflowStep step = new WorkflowStep();
        step.setStepId("template-match");
        step.setType(WorkflowStep.StepType.TOOL_CALL);
        step.setRef("template-matcher");
        step.setParams(Map.of(
            "intent", "${nlp-parse.intent}",
            "output", "templateId"
        ));
        return step;
    }
    
    private WorkflowStep createCapabilityBindStep() {
        WorkflowStep step = new WorkflowStep();
        step.setStepId("capability-bind");
        step.setType(WorkflowStep.StepType.TOOL_CALL);
        step.setRef("capability-binder");
        step.setParams(Map.of(
            "templateId", "${template-match.templateId}",
            "params", "${nlp-parse.params}",
            "output", "boundConfig"
        ));
        return step;
    }
    
    private WorkflowStep createPageRenderStep() {
        WorkflowStep step = new WorkflowStep();
        step.setStepId("page-render");
        step.setType(WorkflowStep.StepType.TOOL_CALL);
        step.setRef("page-renderer");
        step.setParams(Map.of(
            "templateId", "${template-match.templateId}",
            "config", "${capability-bind.boundConfig}",
            "output", "htmlContent"
        ));
        return step;
    }
}
```

### 4.4 API 控制器

```java
package net.ooder.skill.a2ui.api;

@RestController
@RequestMapping("/api")
public class A2UIController {
    
    private final PageManagementService pageManagementService;
    private final PageGenerationService pageGenerationService;
    private final CapabilityBindingService capabilityBindingService;
    
    @GetMapping("/pages")
    public ResponseEntity<List<NexusUiPage>> listPages() {
        return ResponseEntity.ok(pageManagementService.listPages());
    }
    
    @GetMapping("/pages/{pageId}")
    public ResponseEntity<NexusUiPage> getPage(@PathVariable String pageId) {
        return ResponseEntity.ok(pageManagementService.getPage(pageId));
    }
    
    @PostMapping("/generate")
    public ResponseEntity<GenerationResult> generate(
        @RequestBody NlpRequest request
    ) {
        GenerationResult result = pageGenerationService.generateFromNlp(
            request.getInstruction()
        );
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/pages/{pageId}/bind/dict")
    public ResponseEntity<BindingResult> bindDict(
        @PathVariable String pageId,
        @RequestBody DictBindingRequest request
    ) {
        BindingResult result = capabilityBindingService.bindDict(
            pageId, 
            request.getDictTypes()
        );
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/pages/{pageId}/bind/permission")
    public ResponseEntity<BindingResult> bindPermission(
        @PathVariable String pageId,
        @RequestBody PermissionBindingRequest request
    ) {
        BindingResult result = capabilityBindingService.bindPermission(
            pageId, 
            request.getPermissions()
        );
        return ResponseEntity.ok(result);
    }
}
```

---

## 五、依赖配置

### 5.1 Maven 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>skills-parent</artifactId>
        <version>2.3</version>
    </parent>
    
    <artifactId>skill-a2ui</artifactId>
    <version>2.3</version>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- ⚠️ 重要：agent-sdk 是父工程，不能作为依赖 -->
        
        <!-- API 接口层 -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk-api</artifactId>
            <version>2.3</version>
        </dependency>
        
        <!-- 核心实现层 -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk-core</artifactId>
            <version>2.3</version>
        </dependency>
        
        <!-- LLM 完整实现 -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>llm-sdk</artifactId>
            <version>2.3</version>
        </dependency>
        
        <!-- 场景引擎 -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>scene-engine</artifactId>
            <version>2.3</version>
        </dependency>
        
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 5.2 Skill Manifest

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillManifest

metadata:
  id: skill-a2ui
  name: A2UI Nexus UI Skill
  version: 2.3
  description: Nexus UI 模块 Skills 化能力

spec:
  type: tool-skill
  
  capabilities:
    - id: a2ui.generate-page
      name: Page Generation
      description: 通过 NLP 生成 Nexus UI 页面
      category: ui-management
    
    - id: a2ui.list-pages
      name: List Pages
      description: 获取所有 Nexus-UI Skill 页面
      category: ui-management
    
    - id: a2ui.bind-dict
      name: Dictionary Binding
      description: 绑定字典表到页面组件
      category: capability-binding
    
    - id: a2ui.bind-bizattr
      name: Business Attribute Binding
      description: 绑定业务属性到表单
      category: capability-binding
    
    - id: a2ui.bind-datasource
      name: Data Source Binding
      description: 绑定 API 数据源
      category: capability-binding
    
    - id: a2ui.bind-permission
      name: Permission Binding
      description: 绑定权限到操作
      category: capability-binding
  
  scenes:
    - name: page-generation
      capabilities:
        - a2ui.generate-page
        - a2ui.bind-dict
        - a2ui.bind-datasource
        - a2ui.bind-permission
    - name: page-management
      capabilities:
        - a2ui.list-pages
        - a2ui.get-page
        - a2ui.update-page
  
  endpoints:
    - path: /api/pages
      method: GET
      capability: a2ui.list-pages
    - path: /api/generate
      method: POST
      capability: a2ui.generate-page
    - path: /api/pages/{pageId}/bind/dict
      method: POST
      capability: a2ui.bind-dict
    - path: /api/pages/{pageId}/bind/permission
      method: POST
      capability: a2ui.bind-permission
  
  dependencies:
    - skillId: llm-sdk
      version: ">=2.3"
    - skillId: scene-engine
      version: ">=2.3"
```

---

## 六、工作量汇总

### 6.1 按阶段汇总

| 阶段 | 工作量 | 主要内容 |
|------|--------|----------|
| Phase 1 | 5天 | 基础框架、SDK 集成、页面管理 |
| Phase 2 | 5天 | 能力绑定服务 |
| Phase 3 | 5天 | NLP 生成、模板系统 |
| Phase 4 | 13天 | 页面转换、测试、文档 |
| **总计** | **28天** | - |

### 6.2 按团队汇总

| 团队 | 任务数 | 工作量 |
|------|--------|--------|
| Skills 团队 | 15 | 25天 |
| LLM-SDK 团队 | 1 | 2天 |

---

## 七、风险与依赖

### 7.1 技术风险

| 风险 | 等级 | 应对措施 |
|------|------|----------|
| NLP 解析准确率 | 🟡 中 | 建立评估机制，迭代优化 Prompt |
| 页面模板覆盖度 | 🟡 中 | 先覆盖高频场景，逐步扩展 |
| 能力绑定复杂度 | 🟢 低 | 模块化设计，按需加载 |

### 7.2 外部依赖

| 依赖 | 状态 | 说明 |
|------|------|------|
| Ooder SDK v2.3 | ✅ 已发布 | 核心依赖 |
| llm-sdk | ✅ 已发布 | NLP 能力 |
| scene-engine | ✅ 已发布 | 场景引擎 |
| Nexus 页面清单 | ✅ 已完成 | 120+ 页面 |

---

## 八、交付物清单

### 8.1 代码交付物

- [ ] `skill-a2ui` 完整项目
- [ ] API 端点实现
- [ ] 服务层实现
- [ ] 工作流定义
- [ ] 单元测试
- [ ] 集成测试

### 8.2 文档交付物

- [ ] README.md
- [ ] API 文档
- [ ] 使用指南
- [ ] 模板开发指南

### 8.3 配置交付物

- [ ] skill-manifest.yaml
- [ ] application.yaml
- [ ] 页面模板配置

---

## 九、相关文档

| 文档 | 说明 |
|------|------|
| [A2UI_SKILL_DESIGN_SUMMARY.md](A2UI_SKILL_DESIGN_SUMMARY.md) | 设计方案汇总 |
| [A2UI_SKILL_CORRECTED_DESIGN.md](A2UI_SKILL_CORRECTED_DESIGN.md) | 修正后的设计目标 |
| [NEXUS_UI_SKILL_ARCHITECTURE.md](../NEXUS_UI_SKILL_ARCHITECTURE.md) | Nexus-UI Skill 类型规范 |
| [NEXUS_UI_SKILL_COMPLETE_SPEC.md](../NEXUS_UI_SKILL_COMPLETE_SPEC.md) | 完整技术规范 |

---

**文档版本**：v2.0  
**创建日期**：2026-02-27  
**最后更新**：2026-02-27
