# 第五册：A2UI扩展规格

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**源码路径**: `E:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene`

---

## 目录

1. [场景定义](#1-场景定义)
2. [活动块定义](#2-活动块定义)
3. [PageAgent配置](#3-pageagent配置)
4. [Function Calling扩展](#4-function-calling扩展)
5. [交互配置](#5-交互配置)
6. [存储配置](#6-存储配置)

---

## 1. 场景定义

### 1.1 数据模型

```java
public class SceneDef {
    private String sceneId;
    private String name;
    private String description;
    private SceneType sceneType;
    private ActivityBlock activityBlock;
    private A2UIConfig a2uiConfig;
    private InteractionConfig interactionConfig;
    private StorageConfig storageConfig;
}
```

### 1.2 基础属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| sceneId | String | 场景ID | 场景唯一标识 |
| name | String | 场景名称 | 场景名称 |
| description | String | 场景描述 | 场景描述 |
| sceneType | SceneType | 场景类型 | 表单/列表/仪表盘等 |
| activityBlock | ActivityBlock | 活动块 | 活动块定义 |
| a2uiConfig | A2UIConfig | A2UI配置 | A2UI配置 |
| interactionConfig | InteractionConfig | 交互配置 | 交互配置 |
| storageConfig | StorageConfig | 存储配置 | 存储配置 |

### 1.3 场景类型

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| FORM | FORM | 表单场景 | 数据录入和编辑场景 |
| LIST | LIST | 列表场景 | 数据展示和查询场景 |
| DASHBOARD | DASHBOARD | 仪表盘场景 | 数据可视化场景 |
| WORKFLOW | WORKFLOW | 流程场景 | 业务流程处理场景 |
| COLLABORATION | COLLABORATION | 协作场景 | 多人协作场景 |
| AGENT | AGENT | Agent场景 | Agent自动化场景 |

---

## 2. 活动块定义

### 2.1 数据模型

```java
public class ActivityBlock {
    private String blockId;
    private String name;
    private SceneContext context;
    private List<ActivityRef> activities;
    private List<CapabilityRef> capabilities;
    private List<CompositionRule> rules;
}
```

### 2.2 活动块属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| blockId | String | 活动块ID | 活动块唯一标识 |
| name | String | 活动块名称 | 活动块名称 |
| context | SceneContext | 场景上下文 | 场景上下文 |
| activities | List\<ActivityRef\> | 活动引用 | 活动引用列表 |
| capabilities | List\<CapabilityRef\> | 能力引用 | 能力引用列表 |
| rules | List\<CompositionRule\> | 组合规则 | 组合规则列表 |

### 2.3 场景上下文

```java
public class SceneContext {
    private String contextId;
    private Map<String, Object> variables;
    private List<Permission> permissions;
    private Map<String, String> dataBindings;
}
```

### 2.4 活动引用

```java
public class ActivityRef {
    private String activityId;
    private ActivityType activityType;
    private PerformerType performType;
    private List<Condition> conditions;
}
```

### 2.5 能力引用

```java
public class CapabilityRef {
    private String capabilityId;
    private CapabilityType capabilityType;
    private boolean autoBind;
}
```

---

## 3. PageAgent配置

### 3.1 数据模型

```java
public class PageAgentConfig {
    private String agentId;
    private String agentName;
    private String pageId;
    private String pageType;
    private String templatePath;
    private String stylePath;
    private String scriptPath;
    private Map<String, Object> initialData;
    private List<UIComponentDefinition> components;
    private List<FunctionDef> functions;
}
```

### 3.2 PageAgent属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| agentId | String | AgentID | PageAgent唯一标识 |
| agentName | String | Agent名称 | PageAgent名称 |
| pageId | String | 页面ID | 页面唯一标识 |
| pageType | String | 页面类型 | form/list/dashboard等 |
| templatePath | String | 模板路径 | HTML模板路径 |
| stylePath | String | 样式路径 | CSS样式路径 |
| scriptPath | String | 脚本路径 | JavaScript脚本路径 |
| initialData | Map | 初始数据 | 初始数据 |
| components | List | 组件配置 | 组件配置列表 |
| functions | List | 函数定义 | Function Calling定义 |

### 3.3 UI组件定义

```java
public class UIComponentDefinition {
    private String componentId;
    private String componentType;
    private String componentName;
    private String componentLabel;
    private Map<String, Object> properties;
    private List<UIComponentDefinition> children;
    private Map<String, String> dataBindings;
    private List<String> eventBindings;
    private String styleClass;
    private Map<String, Object> layout;
}
```

### 3.4 组件类型

| 组件类型 | 中文名 | 说明 |
|----------|--------|------|
| form | 表单 | 表单容器 |
| input | 输入框 | 文本输入 |
| select | 下拉选择 | 下拉选择框 |
| datepicker | 日期选择 | 日期选择器 |
| checkbox | 复选框 | 复选框 |
| radio | 单选框 | 单选框 |
| textarea | 多行文本 | 多行文本输入 |
| table | 表格 | 数据表格 |
| button | 按钮 | 操作按钮 |
| viewer | 查看器 | 数据查看器 |

---

## 4. Function Calling扩展

### 4.1 数据模型

```java
public class FunctionDef {
    private String functionId;
    private String functionName;
    private FunctionType functionType;
    private String description;
    private Map<String, ParamDef> parameters;
    private String executor;
    private WorkflowBinding workflowBinding;
    private UIComponentDefinition uiComponent;
    private UEInteractionDefinition ueInteraction;
}
```

### 4.2 Function类型

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| LLM | LLM | LLM函数 | 供LLM调用的函数 |
| WORKFLOW | WORKFLOW | 工作流函数 | 供Workflow调用的函数 |
| UI | UI | UI函数 | UI组件操作函数 |
| UE | UE | UE函数 | 用户交互函数 |
| HYBRID | HYBRID | 混合函数 | 同时支持LLM和Workflow的函数 |

### 4.3 Workflow绑定

```java
public class WorkflowBinding {
    private String processDefId;
    private String activityId;
    private Map<String, String> inputMappings;
    private Map<String, String> outputMappings;
}
```

### 4.4 UE交互定义

```java
public class UEInteractionDefinition {
    private String interactionId;
    private String interactionType;
    private String triggerComponent;
    private String targetComponent;
    private Map<String, Object> interactionConfig;
    private List<String> actionSequence;
    private Map<String, Object> animation;
    private String feedback;
}
```

---

## 5. 交互配置

### 5.1 数据模型

```java
public class InteractionConfig {
    private List<A2AConfig> a2a;
    private List<P2AConfig> p2a;
    private List<P2PConfig> p2p;
}
```

### 5.2 A2A配置

```java
public class A2AConfig {
    private String configId;
    private String fromAgent;
    private String toAgent;
    private ConversationType messageType;
    private String trigger;
    private Map<String, Object> config;
}
```

### 5.3 P2A配置

```java
public class P2AConfig {
    private String configId;
    private String participantType;
    private String agentId;
    private ConversationType messageType;
    private String trigger;
    private Map<String, Object> config;
}
```

### 5.4 P2P配置

```java
public class P2PConfig {
    private String configId;
    private String fromParticipant;
    private String toParticipant;
    private ConversationType messageType;
    private String trigger;
    private Map<String, Object> config;
}
```

---

## 6. 存储配置

### 6.1 数据模型

```java
public class StorageConfig {
    private StorageType type;
    private String vfsPath;
    private String tableName;
    private Map<String, Object> options;
}
```

### 6.2 存储类型

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| VFS | VFS | 虚拟文件系统 | VFS文件存储 |
| SQL | SQL | 关系数据库 | SQL数据库存储 |
| MIXED | MIXED | 混合存储 | VFS + SQL混合存储 |

### 6.3 VFS存储结构

```
/skills/{skillId}/
├── skill.yaml                    # Skill主配置
├── scenes/                       # 场景定义
│   ├── {sceneId}.yaml           # 场景配置
│   └── {sceneId}/
│       ├── a2ui.yaml            # A2UI配置
│       ├── functions.yaml       # Function Calling定义
│       └── interactions.yaml    # 交互配置
├── ui/                           # UI资源
│   ├── pages/                   # 页面
│   │   └── {pageId}.html
│   ├── scripts/                 # JavaScript
│   │   └── {scriptId}.js
│   └── styles/                  # CSS
│       └── {styleId}.css
└── components/                   # 组件定义
    └── {componentId}.yaml
```

---

## 附录

### A. 面板清单

| 面板名称 | 所属对象 | 说明 |
|----------|----------|------|
| 场景基本信息面板 | SceneDef | 场景基本信息配置 |
| 活动块配置面板 | ActivityBlock | 活动块配置 |
| PageAgent配置面板 | PageAgentConfig | PageAgent配置 |
| 组件配置面板 | UIComponentDefinition | UI组件配置 |
| 函数定义面板 | FunctionDef | Function Calling定义 |
| 交互配置面板 | InteractionConfig | 交互配置 |
| 存储配置面板 | StorageConfig | 存储配置 |

### B. 枚举清单

| 枚举名称 | 中文名 | 枚举值数量 |
|----------|--------|------------|
| SceneType | 场景类型 | 6 |
| FunctionType | Function类型 | 5 |
| StorageType | 存储类型 | 3 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-05-a2ui-extension\README.md`
