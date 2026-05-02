# SkillFlow 节点类型与属性体系设计文档

## 文档信息
- **版本**: 1.0
- **创建日期**: 2026-04-20
- **目的**: 定义 SkillFlow 节点类型体系和属性结构，统一流程定义、执行、历史三态属性

---

## 目录
1. [核心概念与术语](#1-核心概念与术语)
2. [节点类型体系](#2-节点类型体系)
3. [属性树形结构](#3-属性树形结构)
4. [属性/面板/插件管理策略](#4-属性面板插件管理策略)
5. [源码映射与实现](#5-源码映射与实现)
6. [迁移与兼容策略](#6-迁移与兼容策略)

---

## 1. 核心概念与术语

### 1.1 基础概念定义

| 概念 | 英文 | 定义 | 说明 |
|------|------|------|------|
| **流程定义** | ProcessDef | 流程的静态定义 | 包含流程基本信息、版本、活动、路由 |
| **流程版本** | ProcessDefVersion | 流程的版本化定义 | 支持版本管理、发布、冻结 |
| **活动定义** | ActivityDef | 流程中的活动节点 | 包含基础属性、扩展属性、执行配置 |
| **路由定义** | RouteDef | 活动之间的连接 | 包含条件、方向、顺序 |
| **节点类型** | NodeType | 活动的类型分类 | 控制节点、嵌套节点、执行节点、组网节点 |
| **执行类型** | ExecutionType | 活动的执行方式 | HUMAN、SKILL、LLM、AGENT、A2UI |

### 1.2 三态属性模型

每个节点具备三个属性维度：

```
┌─────────────────────────────────────────────────────────────────────┐
│                        三态属性模型                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  定义态 (flowDefinition)                                     │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 节点是什么？                                              │    │
│  │  • 包含：位置、流程控制、组网配置、嵌套配置、业务定义         │    │
│  │  • 对应：设计时属性                                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  运行态 (execution)                                          │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 节点如何执行？                                            │    │
│  │  • 包含：执行类型、Skill配置、LLM配置、Agent配置、A2UI配置    │    │
│  │  • 对应：运行时属性                                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  历史态 (history)                                            │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 执行记录如何保存？                                        │    │
│  │  • 包含：审计配置、数据保留、快照配置、追踪配置               │    │
│  │  • 对应：历史数据属性                                        │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 术语对照表

| 新术语 | 旧术语 | 说明 |
|--------|--------|------|
| `flowDefinition` | 扩展属性组 (RIGHT/FORM/SERVICE) | 统一为流程定义属性 |
| `execution` | implementation | 扩展为执行定义属性 |
| `networking` | 协调器/路由器 | 统一为组网配置 |
| `nesting` | 子流程/活动块/场景 | 统一为嵌套配置 |
| `A2UI` | 表单 (FORM) | Agent驱动的动态界面 |

---

## 2. 节点类型体系

### 2.1 节点类型树形结构

```
节点类型树
├── 流程控制节点 [CONTROL]
│   ├── 开始节点 [START]
│   │   └── defaultProps: { position: 'START', nodeType: 'CONTROL' }
│   │
│   ├── 结束节点 [END]
│   │   └── defaultProps: { position: 'END', nodeType: 'CONTROL' }
│   │
│   ├── 网关节点 [GATEWAY]
│   │   ├── 排他网关 [XOR_GATEWAY]
│   │   │   └── flowControl: { split: 'XOR', join: 'XOR' }
│   │   │
│   │   ├── 并行网关 [AND_GATEWAY]
│   │   │   └── flowControl: { split: 'AND', join: 'AND' }
│   │   │
│   │   └── 包容网关 [OR_GATEWAY]
│   │       └── flowControl: { split: 'OR', join: 'OR' }
│   │
│   └── 路由器 [ROUTER]
│       └── networking: { router: { type: 'CONDITION' } }
│
├── 流程嵌套节点 [NESTING]
│   ├── 子流程 [SUBFLOW]
│   │   └── nesting: { type: 'SUBFLOW' }
│   │
│   ├── 活动块 [BLOCK]
│   │   └── nesting: { type: 'BLOCK' }
│   │
│   ├── 场景 [SCENE]
│   │   └── nesting: { type: 'SCENE' }
│   │
│   └── 外部流程 [EXTERNAL]
│       └── nesting: { type: 'EXTERNAL' }
│
├── 执行节点 [EXECUTOR]
│   ├── 人工任务 [HUMAN]
│   │   └── execution: { type: 'HUMAN' }
│   │
│   ├── Skill任务 [SKILL]
│   │   └── execution: { type: 'SKILL' }
│   │
│   ├── LLM任务 [LLM]
│   │   └── execution: { type: 'LLM' }
│   │
│   ├── Agent任务 [AGENT]
│   │   └── execution: { type: 'AGENT' }
│   │
│   └── A2UI任务 [A2UI]
│       └── execution: { type: 'A2UI' }
│
└── 组网节点 [NETWORK]
    ├── 协调器 [COORDINATOR]
    │   └── networking: { mode: 'HIERARCHICAL', coordinator: {...} }
    │
    ├── 端点 [ENDPOINT]
    │   └── networking: { endpoints: [...] }
    │
    └── 桥接器 [BRIDGE]
        └── networking: { bridge: {...} }
```

### 2.2 节点类型枚举定义

```javascript
// 文件: e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\dto\enums\NodeType.java

public enum NodeType {
    // 流程控制节点
    CONTROL("CONTROL", "流程控制节点"),
    
    // 流程嵌套节点
    NESTING("NESTING", "流程嵌套节点"),
    
    // 执行节点
    EXECUTOR("EXECUTOR", "执行节点"),
    
    // 组网节点
    NETWORK("NETWORK", "组网节点");
    
    private final String code;
    private final String description;
    
    // @JsonCreator, @JsonValue 方法...
}
```

### 2.3 执行类型枚举定义

```javascript
// 文件: e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\dto\enums\ExecutionType.java

public enum ExecutionType {
    // 人工执行
    HUMAN("HUMAN", "人工任务"),
    
    // Skill技能执行
    SKILL("SKILL", "Skill任务"),
    
    // LLM大模型执行
    LLM("LLM", "LLM任务"),
    
    // Agent智能体执行
    AGENT("AGENT", "Agent任务"),
    
    // Agent驱动UI
    A2UI("A2UI", "A2UI任务");
    
    private final String code;
    private final String description;
}
```

---

## 3. 属性树形结构

### 3.1 完整属性树定义

```
节点属性树
├── 基础属性 [basic]
│   ├── nodeId: String (节点ID，只读)
│   ├── name: String (节点名称，必填)
│   ├── description: String (节点描述)
│   └── nodeType: Enum (节点类型，只读)
│
├── 流程定义属性 [flowDefinition]
│   │
│   ├── 位置属性 [position]
│   │   └── position: Enum [START|NORMAL|END]
│   │
│   ├── 流程控制 [flowControl]
│   │   ├── join: Enum [XOR|AND|OR] (汇聚类型)
│   │   ├── split: Enum [XOR|AND|OR] (分支类型)
│   │   ├── condition: String (条件表达式)
│   │   └── loop: Object (循环配置)
│   │
│   ├── 组网配置 [networking]
│   │   ├── mode: Enum [SOLO|SEQUENTIAL|PARALLEL|HIERARCHICAL|MESH]
│   │   │
│   │   ├── 协调器配置 [coordinator]
│   │   │   ├── strategy: Enum [HIERARCHICAL|FLAT|MESH]
│   │   │   ├── agents: Array (Agent列表)
│   │   │   └── resultAggregation: Object (结果聚合配置)
│   │   │
│   │   ├── 路由器配置 [router]
│   │   │   ├── type: Enum [CONDITION|RULE|ROUND_ROBIN|WEIGHTED]
│   │   │   └── rules: Array (路由规则)
│   │   │
│   │   └── 端点配置 [endpoints]
│   │       └── Array<{id, type, protocol, address}>
│   │
│   ├── 嵌套配置 [nesting]
│   │   ├── type: Enum [SUBFLOW|BLOCK|SCENE|EXTERNAL]
│   │   ├── refId: String (引用ID)
│   │   ├── version: String (版本)
│   │   └── params: Object (参数映射)
│   │
│   └── 业务定义 [business]
│       ├── category: String (业务分类)
│       ├── scene: Object (场景配置)
│       └── block: Object (活动块配置)
│
├── 执行定义属性 [execution]
│   │
│   ├── 执行类型 [type]
│   │   └── type: Enum [HUMAN|SKILL|LLM|AGENT|A2UI]
│   │
│   ├── 人工配置 [human] (showWhen: type=HUMAN)
│   │   ├── assignee: String (执行人)
│   │   ├── performType: Enum [SINGLE|JOINTSIGN|COUNTERSIGN]
│   │   └── dueTime: Number (时限)
│   │
│   ├── Skill配置 [skill] (showWhen: type=SKILL)
│   │   ├── skillId: String (Skill ID)
│   │   ├── capability: String (能力)
│   │   ├── input: Object (输入映射)
│   │   └── output: Object (输出映射)
│   │
│   ├── LLM配置 [llm] (showWhen: type=LLM)
│   │   ├── provider: Enum [deepseek|openai|qianwen|volcengine]
│   │   ├── model: String (模型名称)
│   │   ├── temperature: Number (温度 0-2)
│   │   ├── maxTokens: Number (最大Token)
│   │   └── tools: Array (工具配置)
│   │
│   ├── Agent配置 [agent] (showWhen: type=AGENT)
│   │   ├── agentId: String (Agent ID)
│   │   ├── agentType: Enum [LLM|TOOL|HYBRID]
│   │   ├── capabilities: Array (能力列表)
│   │   ├── northbound: Object (北向接口)
│   │   └── southbound: Object (南向接口)
│   │
│   ├── A2UI配置 [a2ui] (showWhen: type=A2UI)
│   │   ├── uiType: Enum [FORM|LIST|DASHBOARD|CHAT]
│   │   ├── agentId: String (驱动Agent)
│   │   ├── schema: Object (界面Schema)
│   │   └── interactions: Object (交互配置)
│   │
│   └── 执行控制 [control]
│       ├── timeout: Number (超时时间ms)
│       ├── retry: Object (重试配置)
│       ├── async: Boolean (是否异步)
│       └── onError: Enum [CONTINUE|STOP|RETRY|COMPENSATE]
│
├── 历史定义属性 [history]
│   ├── enableAudit: Boolean (启用审计)
│   ├── retention: Object (数据保留配置)
│   ├── snapshot: Object (快照配置)
│   └── tracking: Object (追踪配置)
│
└── 兼容属性 [compatibility]
    ├── RIGHT: Object (权限配置 - P2P兼容)
    ├── FORM: Object (表单配置 - P2P兼容)
    └── SERVICE: Object (服务配置 - P2P兼容)
```

### 3.2 属性显示条件 (showWhen)

```javascript
// 属性显示条件定义
const showWhenRules = {
    // 执行类型为 HUMAN 时显示人工配置
    'execution.human': { 'execution.type': 'HUMAN' },
    
    // 执行类型为 SKILL 时显示Skill配置
    'execution.skill': { 'execution.type': 'SKILL' },
    
    // 执行类型为 LLM 时显示LLM配置
    'execution.llm': { 'execution.type': 'LLM' },
    
    // 执行类型为 AGENT 时显示Agent配置
    'execution.agent': { 'execution.type': 'AGENT' },
    
    // 执行类型为 A2UI 时显示A2UI配置
    'execution.a2ui': { 'execution.type': 'A2UI' },
    
    // 节点类型为 CONTROL 时隐藏执行配置
    'execution': { 'nodeType': { not: 'CONTROL' } },
    
    // 位置为 START 或 END 时隐藏嵌套配置
    'flowDefinition.nesting': { 'flowDefinition.position': { not: ['START', 'END'] } }
};
```

---

## 4. 属性/面板/插件管理策略

### 4.1 属性管理架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                      属性管理架构                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Elements.js (节点类型定义)                                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • nodeTree: 节点类型树形结构                                │    │
│  │  • propertyTree: 属性树形结构                                │    │
│  │  • getAvailableProperties(nodeType): 获取可用属性            │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  PanelManager.js (面板管理器)                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 根据节点类型动态加载面板插件                              │    │
│  │  • 管理面板生命周期                                          │    │
│  │  • 处理属性变更事件                                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  PluginPanel (插件面板基类)                                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • AbstractPluginPanel: 抽象基类                             │    │
│  │  • ActivityPanelPlugin: 活动属性面板                         │    │
│  │  • RoutePanelPlugin: 路由属性面板                            │    │
│  │  • LLMConfigPlugin: LLM配置面板                              │    │
│  │  • AgentConfigPlugin: Agent配置面板                          │    │
│  │  • A2UIConfigPlugin: A2UI配置面板                            │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 面板插件注册机制

```javascript
// 文件: e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\PanelInitializer.js

class PanelInitializer {
    constructor() {
        // 插件注册表
        this.plugins = new Map();
        
        // 注册内置插件
        this.registerBuiltInPlugins();
    }
    
    registerBuiltInPlugins() {
        // 基础属性插件
        this.register('basic', BasicPropertyPlugin);
        
        // 流程定义插件
        this.register('flowDefinition', FlowDefinitionPlugin);
        this.register('flowControl', FlowControlPlugin);
        this.register('networking', NetworkingPlugin);
        this.register('nesting', NestingPlugin);
        
        // 执行定义插件
        this.register('execution', ExecutionPlugin);
        this.register('human', HumanConfigPlugin);
        this.register('skill', SkillConfigPlugin);
        this.register('llm', LLMConfigPlugin);
        this.register('agent', AgentConfigPlugin);
        this.register('a2ui', A2UIConfigPlugin);
        
        // 历史定义插件
        this.register('history', HistoryPlugin);
    }
    
    // 根据节点类型获取可用插件
    getPluginsForNodeType(nodeType, executionType) {
        const plugins = [];
        
        // 所有节点都有基础属性
        plugins.push(this.plugins.get('basic'));
        
        // 根据节点类型添加插件
        switch (nodeType) {
            case 'CONTROL':
                plugins.push(this.plugins.get('flowControl'));
                break;
            case 'NESTING':
                plugins.push(this.plugins.get('nesting'));
                break;
            case 'EXECUTOR':
                plugins.push(this.plugins.get('execution'));
                plugins.push(this.plugins.get(executionType.toLowerCase()));
                break;
            case 'NETWORK':
                plugins.push(this.plugins.get('networking'));
                break;
        }
        
        return plugins.filter(p => p);
    }
}
```

### 4.3 属性变更联动机制

```javascript
// 属性变更联动规则
const linkageRules = {
    // 执行类型变更 -> 联动更新执行配置面板
    'execution.type': {
        target: 'execution',
        action: 'switchPanel',
        params: (value) => ({ panelType: value.toLowerCase() })
    },
    
    // 节点类型变更 -> 联动更新可用属性
    'nodeType': {
        target: 'flowDefinition',
        action: 'updateAvailableProps',
        params: (value) => ({ nodeType: value })
    },
    
    // 嵌套类型变更 -> 联动更新嵌套配置
    'flowDefinition.nesting.type': {
        target: 'flowDefinition.nesting',
        action: 'updateConfig',
        params: (value) => ({ nestingType: value })
    },
    
    // 组网模式变更 -> 联动更新协调器配置
    'flowDefinition.networking.mode': {
        target: 'flowDefinition.networking.coordinator',
        action: 'updateStrategy',
        params: (value) => {
            if (value === 'HIERARCHICAL') {
                return { strategy: 'HIERARCHICAL' };
            } else if (value === 'MESH') {
                return { strategy: 'MESH' };
            }
            return { strategy: null };
        }
    }
};
```

---

## 5. 源码映射与实现

### 5.1 前端源码文件映射

| 文件路径 | 职责 | 关键类/方法 |
|---------|------|------------|
| `js/Elements.js` | 节点类型定义 | `nodeTree`, `propertyTree`, `getAvailableProperties()` |
| `js/model/ActivityDef.js` | 活动定义模型 | `toJSON()`, `fromJSON()`, 属性存取方法 |
| `js/panel/PanelManager.js` | 面板管理器 | `loadPanel()`, `switchPanel()`, `updateData()` |
| `js/panel/plugins/*.js` | 插件面板 | 各类插件实现 |
| `js/sdk/Store.js` | 数据存储 | `updateActivity()`, `emit()` |
| `js/Canvas.js` | 画布渲染 | `_updateNodeDisplay()`, `_createNode()` |
| `js/sdk/DataAdapter.js` | 数据适配 | `_convertActivityToBackend()`, `_convertActivityToFrontend()` |

### 5.2 后端源码文件映射

| 文件路径 | 职责 | 关键类/方法 |
|---------|------|------------|
| `service/ProcessDefManagerService.java` | 流程定义服务 | `saveProcessDef()`, `getActivityDefsByVersion()` |
| `controller/ProcessDefDbController.java` | REST控制器 | `saveProcessDef()`, `formatProcessDef()` |
| `dto/ActivityDTO.java` | 活动数据传输对象 | 属性定义 |
| `dto/enums/*.java` | 枚举定义 | `NodeType`, `ExecutionType`, `ActivityPosition` |
| `engine/database/DbActivityDef.java` | 活动数据库实体 | 属性存取方法 |
| `engine/database/DbAttributeDef.java` | 扩展属性实体 | `getChildIgnoreCase()` |

### 5.3 数据库表结构映射

| 表名 | 对应实体 | 存储内容 |
|------|---------|---------|
| `BPM_PROCESSDEF` | DbProcessDef | 流程定义基本信息 |
| `BPM_PROCESSDEF_VERSION` | DbProcessDefVersion | 流程版本信息 |
| `BPM_ACTIVITYDEF` | DbActivityDef | 活动定义基本信息 |
| `BPM_ACTIVITYDEF_PROPERTY` | DbAttributeDef | 活动扩展属性 |
| `BPM_ROUTEDEF` | DbRouteDef | 路由定义 |

### 5.4 属性存储路径映射

| 属性路径 | 数据库存储位置 | 存储格式 |
|---------|---------------|---------|
| `basic.nodeId` | BPM_ACTIVITYDEF.ACTIVITYDEF_ID | String |
| `basic.name` | BPM_ACTIVITYDEF.DEFNAME | String |
| `flowDefinition.position` | BPM_ACTIVITYDEF.POSITION | String (POSITION_START/END/NORMAL) |
| `flowDefinition.flowControl` | BPM_ACTIVITYDEF_PROPERTY | JSON String |
| `flowDefinition.networking` | BPM_ACTIVITYDEF_PROPERTY | JSON String |
| `flowDefinition.nesting` | BPM_ACTIVITYDEF_PROPERTY | JSON String |
| `execution` | BPM_ACTIVITYDEF_PROPERTY | JSON String |
| `history` | BPM_ACTIVITYDEF_PROPERTY | JSON String |

---

## 6. 迁移与兼容策略

### 6.1 旧属性到新属性映射

| 旧属性路径 | 新属性路径 | 转换逻辑 |
|-----------|-----------|---------|
| `activityType` | `execution.type` | 类型映射 |
| `activityCategory` | `flowDefinition.business.category` | 直接映射 |
| `position` | `flowDefinition.position` | 直接映射 |
| `positionCoord` | `flowDefinition.positionCoord` | 直接映射 |
| `RIGHT.*` | `compatibility.RIGHT.*` | 保留兼容 |
| `FORM.*` | `compatibility.FORM.*` | 保留兼容，建议迁移到 A2UI |
| `SERVICE.*` | `execution.skill.*` | 迁移到 Skill 配置 |

### 6.2 活动类型迁移映射

| 旧活动类型 | 新节点类型 | 新执行类型 | 说明 |
|-----------|-----------|-----------|------|
| `START` | CONTROL | - | 开始节点 |
| `END` | CONTROL | - | 结束节点 |
| `TASK` | EXECUTOR | HUMAN | 人工任务 |
| `SERVICE` | EXECUTOR | SKILL | 服务→Skill |
| `SCRIPT` | EXECUTOR | SKILL | 脚本→Skill |
| `SUBFLOW` | NESTING | - | 子流程 |
| `LLM_TASK` | EXECUTOR | LLM | LLM任务 |
| `AGENT_TASK` | EXECUTOR | AGENT | Agent任务 |
| `COORDINATOR` | NETWORK | - | 协调器 |
| `SCENE` | NESTING | - | 场景 |
| `XOR_GATEWAY` | CONTROL | - | 排他网关 |
| `AND_GATEWAY` | CONTROL | - | 并行网关 |
| `OR_GATEWAY` | CONTROL | - | 包容网关 |

### 6.3 数据迁移脚本

```javascript
// 迁移函数
function migrateActivityDef(oldDef) {
    const newDef = {
        nodeId: oldDef.activityDefId,
        name: oldDef.name,
        description: oldDef.description,
        nodeType: determineNodeType(oldDef.activityType),
        flowDefinition: {
            position: oldDef.position || 'NORMAL'
        },
        execution: {},
        history: {
            enableAudit: true
        },
        compatibility: {}
    };
    
    // 迁移执行类型
    const executionType = mapExecutionType(oldDef.activityType);
    if (executionType) {
        newDef.execution.type = executionType;
    }
    
    // 迁移坐标
    if (oldDef.positionCoord) {
        newDef.flowDefinition.positionCoord = oldDef.positionCoord;
    }
    
    // 迁移兼容属性
    if (oldDef.RIGHT) {
        newDef.compatibility.RIGHT = oldDef.RIGHT;
    }
    if (oldDef.FORM) {
        newDef.compatibility.FORM = oldDef.FORM;
    }
    if (oldDef.SERVICE) {
        // 服务配置迁移到 Skill 配置
        newDef.execution.skill = convertServiceToSkill(oldDef.SERVICE);
    }
    
    return newDef;
}

function determineNodeType(activityType) {
    const typeMap = {
        'START': 'CONTROL',
        'END': 'CONTROL',
        'XOR_GATEWAY': 'CONTROL',
        'AND_GATEWAY': 'CONTROL',
        'OR_GATEWAY': 'CONTROL',
        'SUBFLOW': 'NESTING',
        'SCENE': 'NESTING',
        'TASK': 'EXECUTOR',
        'SERVICE': 'EXECUTOR',
        'SCRIPT': 'EXECUTOR',
        'LLM_TASK': 'EXECUTOR',
        'AGENT_TASK': 'EXECUTOR',
        'COORDINATOR': 'NETWORK'
    };
    return typeMap[activityType] || 'EXECUTOR';
}

function mapExecutionType(activityType) {
    const execMap = {
        'TASK': 'HUMAN',
        'SERVICE': 'SKILL',
        'SCRIPT': 'SKILL',
        'LLM_TASK': 'LLM',
        'AGENT_TASK': 'AGENT'
    };
    return execMap[activityType] || null;
}
```

---

## 附录

### A. 相关设计文档索引

| 文档名称 | 路径 | 说明 |
|---------|------|------|
| BPM高级动态插件架构文档 | `BPM高级动态插件架构文档.md` | 插件架构设计 |
| BPM扩展属性知识图谱 | `BPM_ExtensionAttributes_KnowledgeGraph.md` | 属性存取矩阵 |
| 从零开始的SPAC编程构建BPM设计器实战 | `docs/从零开始的SPAC编程构建BPM设计器实战-最终版.md` | 实战记录 |

### B. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| 1.0 | 2026-04-20 | 初始版本，定义节点类型体系和属性结构 |

---

## 附录 C. 网关与流程控制兼容性设计

### C.1 原有引擎 Join/Split 实现

#### 数据库存储

| 字段名 | 存储格式 | 说明 |
|--------|---------|------|
| `INJOIN` | `JOIN_XOR` / `JOIN_AND` / `JOIN_OR` | 入口汇聚类型 |
| `SPLIT` | `SPLIT_XOR` / `SPLIT_AND` / `SPLIT_OR` | 出口分支类型 |

**源码位置**：
- 数据库表结构：[schema.sql:45-46](file:///e:/github/ooder-skills/skills/_drivers/bpm/bpmserver/src/main/resources/db/schema.sql#L45-L46)
- 实体类定义：[DbActivityDef.java:143-153](file:///e:/github/ooder-skills/skills/_drivers/bpm/bpmserver/src/main/java/net/ooder/bpm/engine/database/DbActivityDef.java#L143-L153)
- XPDL解析：[ActivityDefBean.java:323-367](file:///e:/github/ooder-skills/skills/_drivers/bpm/bpmserver/src/main/java/net/ooder/bpm/webservice/XPDLBean/ActivityDefBean.java#L323-L367)

#### 前端定义

**源码位置**：[ActivityDef.js:34-35](file:///e:/github/ooder-skills/skills/_drivers/bpm/bpm-designer/src/main/resources/static/designer/js/model/ActivityDef.js#L34-L35)

```javascript
this.join = data?.join || 'XOR';   // 默认排他汇聚
this.split = data?.split || 'XOR'; // 默认排他分支
```

### C.2 网关节点 vs Activity 属性

#### 概念对比

| 方式 | 说明 | 标准来源 |
|------|------|---------|
| **Activity 内置 join/split** | 每个活动都有入口/出口路由类型 | XPDL 1.0 |
| **独立 Gateway 节点** | 网关是独立的图形元素 | BPMN 2.0 |
| **原有引擎** | 采用 XPDL 方式，join/split 作为活动属性 | - |

#### 冲突分析

1. **语义歧义**：如果一个 Activity 的 `split='AND'`，它是否等同于一个 AND_GATEWAY？
2. **双重定义**：GATEWAY 节点是否也需要 `join` 和 `split` 属性？
3. **数据迁移**：如何处理已有的 join/split 数据？

### C.3 混合模式设计方案（推荐）

#### 设计原则

1. **统一属性结构**：所有节点都有 `flowDefinition.flowControl` 属性
2. **按需使用**：
   - `CONTROL` 节点（GATEWAY）：`flowControl` 必填
   - `EXECUTOR` 节点：`flowControl` 可选（兼容旧数据）
   - 其他节点：`flowControl` 不使用

#### 属性映射

```
原属性路径                    新属性路径
─────────────────────────────────────────────────────
Activity.join           →    flowDefinition.flowControl.join
Activity.split          →    flowDefinition.flowControl.split
```

#### 值映射

| 前端值 | 数据库值 | 含义 |
|--------|---------|------|
| `XOR` | `JOIN_XOR` / `SPLIT_XOR` | 排他（只走一条路径） |
| `AND` | `JOIN_AND` / `SPLIT_AND` | 并行（所有路径同时执行） |
| `OR` | `JOIN_OR` / `SPLIT_OR` | 包容（满足条件的路径执行） |

### C.4 执行引擎兼容层

```java
// 执行引擎中的兼容处理
public FlowBehavior getFlowBehavior(ActivityDef activity) {
    // 1. GATEWAY 节点：直接使用 flowControl
    if (activity.getNodeType() == NodeType.CONTROL) {
        return new FlowBehavior(
            activity.getFlowControl().getJoin(),
            activity.getFlowControl().getSplit()
        );
    }
    
    // 2. EXECUTOR 节点：检查是否有显式 flowControl（兼容旧数据）
    FlowControl fc = activity.getFlowControl();
    if (fc != null && (fc.getJoin() != null || fc.getSplit() != null)) {
        return new FlowBehavior(fc.getJoin(), fc.getSplit());
    }
    
    // 3. 默认行为：XOR
    return new FlowBehavior("XOR", "XOR");
}
```

### C.5 数据迁移策略

#### 迁移规则

```
原 Activity.join/split → 新 flowDefinition.flowControl.join/split

特殊情况处理：
1. 如果 Activity.split != 'XOR' 且没有后继 GATEWAY：
   → 可选：自动插入对应的 GATEWAY 节点（根据配置决定）
   
2. 如果 Activity.join != 'XOR' 且没有前驱 GATEWAY：
   → 可选：自动插入对应的 GATEWAY 节点（根据配置决定）
```

#### 迁移脚本

```javascript
function migrateFlowControl(oldActivity) {
    const newActivity = { ...oldActivity };
    
    // 迁移 join/split 到 flowDefinition.flowControl
    if (oldActivity.join || oldActivity.split) {
        newActivity.flowDefinition = newActivity.flowDefinition || {};
        newActivity.flowDefinition.flowControl = {
            join: oldActivity.join || 'XOR',
            split: oldActivity.split || 'XOR'
        };
        
        // 删除旧属性（可选，保留兼容）
        // delete newActivity.join;
        // delete newActivity.split;
    }
    
    return newActivity;
}
```

### C.6 方案对比总结

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **完全兼容** | 无需迁移，引擎不变 | 概念歧义，不符合 BPMN | ⭐⭐ |
| **分离网关** | 概念清晰，符合 BPMN | 需要数据迁移 | ⭐⭐⭐ |
| **混合模式** | 兼容旧数据，支持新标准 | 实现复杂度稍高 | ⭐⭐⭐⭐⭐ |

---

**文档结束**
