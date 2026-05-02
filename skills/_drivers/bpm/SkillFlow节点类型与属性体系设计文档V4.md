# SkillFlow 节点类型与属性体系设计文档 V4

## 文档信息
- **版本**: 4.0
- **创建日期**: 2026-04-20
- **目的**: 重新定义执行体=Skill概念，设计嵌套节点与上下文环境属性体系

---

## 目录
1. [核心概念重新定义](#1-核心概念重新定义)
2. [Skill执行体体系](#2-skill执行体体系)
3. [嵌套节点与上下文环境](#3-嵌套节点与上下文环境)
4. [节点类型体系 V4](#4-节点类型体系-v4)
5. [属性树形结构 V4](#5-属性树形结构-v4)
6. [迁移与兼容策略](#6-迁移与兼容策略)

---

## 1. 核心概念重新定义

### 1.1 活动的本质定义

```
┌─────────────────────────────────────────────────────────────────────┐
│                        活动的本质 V4                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│      活动 = 谁（执行者） + 调用什么（Skill）                          │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  执行者 [Performer]                                          │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • HUMAN  - 真人（需要UI交互，人工触发Skill）                │    │
│  │  • AGENT  - 拟人智能体（龙虾，可自主执行Skill）              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              +                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  执行体 [Skill] - 统一的执行单元                              │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  Skill 是唯一核心实体，所有执行内容都是Skill的不同形态        │    │
│  │                                                              │    │
│  │  ├── LLM_SKILL    - 大模型推理Skill（对话、生成、分析）      │    │
│  │  ├── FORM_SKILL   - 表单交互Skill（数据采集、审批）          │    │
│  │  ├── SERVICE_SKILL - 服务调用Skill（API、工具、能力）        │    │
│  │  ├── WORKFLOW_SKILL - 流程编排Skill（子流程、活动块）        │    │
│  │  └── CUSTOM_SKILL - 自定义Skill（用户扩展）                  │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  关键认知：                                                          │
│  • LLM 是一种 Skill（大模型推理能力）                                │
│  • FORM 是一种 Skill（表单交互能力）                                 │
│  • SERVICE 是一种 Skill（服务调用能力）                              │
│  • 执行体 = Skill，不再区分执行类型                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 执行者与Skill的组合矩阵

| 执行者 | Skill类型 | 组合结果 | UI技术 | 说明 |
|--------|----------|---------|--------|------|
| HUMAN | FORM_SKILL | 人工表单任务 | A2UI/传统表单 | 人工填写、审批 |
| HUMAN | SERVICE_SKILL | 人工触发服务 | A2UI按钮触发 | 用户手动调用服务 |
| HUMAN | LLM_SKILL | 人工对话任务 | A2UI对话界面 | 用户与LLM交互 |
| AGENT | SERVICE_SKILL | Agent执行服务 | 无UI | 自动化服务调用 |
| AGENT | LLM_SKILL | Agent推理任务 | 无UI | 自动化智能处理 |
| AGENT | FORM_SKILL | Agent辅助表单 | A2UI预填充 | Agent生成/填充表单 |
| AGENT | WORKFLOW_SKILL | Agent编排流程 | 无UI | Agent调度子流程 |

### 1.3 Skill的分类体系

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Skill分类体系                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  按SkillForm（形态）分类                                      │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • SCENE     - 场景Skill（容器型，需要激活）                  │    │
│  │  • STANDALONE - 独立Skill（原子型，可直接调用）               │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  按SkillCategory（功能）分类                                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • LLM        - 大模型推理（对话、生成、分析）                │    │
│  │  • FORM       - 表单交互（数据采集、审批）                    │    │
│  │  • SERVICE    - 服务调用（API、工具）                         │    │
│  │  • WORKFLOW   - 流程编排（子流程、活动块）                    │    │
│  │  • KNOWLEDGE  - 知识管理（RAG、问答）                         │    │
│  │  • DATA       - 数据处理（存储、查询）                        │    │
│  │  • COMM       - 通讯服务（消息、通知）                        │    │
│  │  • TOOL       - 工具服务（文档、报表）                        │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  按SkillProvider（提供者）分类                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • SYSTEM    - 系统内置Skill                                 │    │
│  │  • DRIVER    - 驱动适配Skill（第三方平台集成）                │    │
│  │  • BUSINESS  - 业务定制Skill                                 │    │
│  │  • USER      - 用户自定义Skill                               │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Skill执行体体系

### 2.1 Skill的定义结构

```javascript
const SkillDefinition = {
    skillId: 'skill-llm-deepseek',
    name: 'DeepSeek LLM Skill',
    description: 'DeepSeek大模型推理能力',
    
    form: 'STANDALONE',
    category: 'LLM',
    provider: 'DRIVER',
    
    input: {
        type: 'object',
        properties: {
            prompt: { type: 'string', description: '提示词' },
            context: { type: 'object', description: '上下文数据' },
            tools: { type: 'array', description: '可用工具' }
        }
    },
    
    output: {
        type: 'object',
        properties: {
            content: { type: 'string', description: '生成内容' },
            tokens: { type: 'object', description: 'Token统计' },
            toolCalls: { type: 'array', description: '工具调用' }
        }
    },
    
    config: {
        provider: 'deepseek',
        model: 'deepseek-chat',
        temperature: 0.7,
        maxTokens: 4096
    },
    
    capabilities: ['text-generation', 'code-generation', 'reasoning'],
    
    dependencies: []
};
```

### 2.2 活动节点中的Skill配置

```javascript
const ActivityNodeConfig = {
    nodeId: 'act_001',
    name: '智能审批',
    nodeType: 'ACTIVITY',
    
    performer: {
        type: 'AGENT',
        agentId: 'agent_approval'
    },
    
    skill: {
        skillId: 'skill-llm-deepseek',
        
        inputMapping: {
            prompt: '${executionContext.taskDescription}',
            context: '${processContext.formData}',
            tools: ['approval-tool', 'notification-tool']
        },
        
        outputMapping: {
            approved: '${result.approved}',
            comment: '${result.comment}'
        },
        
        config: {
            temperature: 0.3,
            systemPrompt: '你是一个审批助手...'
        },
        
        fallback: {
            skillId: 'skill-form-approval',
            condition: 'llm_unavailable'
        }
    },
    
    execution: {
        timeout: 30000,
        retry: { maxAttempts: 3, interval: 1000 },
        async: false,
        onError: 'FALLBACK'
    }
};
```

### 2.3 Skill与执行者的关系

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Skill与执行者关系                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  活动节点 [ACTIVITY]                                         │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  performer: { type: 'HUMAN' | 'AGENT', ... }                │    │
│  │                                                              │    │
│  │  skill: {                                                    │    │
│  │      skillId: string,        // 调用的Skill ID              │    │
│  │      inputMapping: {...},    // 输入映射                    │    │
│  │      outputMapping: {...},   // 输出映射                    │    │
│  │      config: {...},          // 运行时配置                  │    │
│  │      fallback: {...}         // 降级策略                    │    │
│  │  }                                                           │    │
│  │                                                              │    │
│  │  执行流程：                                                  │    │
│  │  1. 执行者（人/Agent）触发                                   │    │
│  │  2. 加载Skill定义                                           │    │
│  │  3. 执行输入映射                                            │    │
│  │  4. 执行Skill                                               │    │
│  │  5. 执行输出映射                                            │    │
│  │  6. 返回结果                                                │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  执行者决定：                                                        │
│  • HUMAN：需要UI交互，人工触发Skill执行                              │
│  • AGENT：自动执行Skill，无需人工干预                                │
│                                                                      │
│  Skill决定：                                                         │
│  • 执行什么能力（LLM推理、表单交互、服务调用...）                     │
│  • 如何执行（输入输出、配置参数）                                    │
│  • 执行结果（返回数据结构）                                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. 嵌套节点与上下文环境

### 3.1 嵌套节点的核心目的

```
┌─────────────────────────────────────────────────────────────────────┐
│                    嵌套节点的核心目的                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  嵌套的目的 = 设定独立的上下文环境                                   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  为什么需要嵌套？                                            │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  1. 隔离性 - 避免变量冲突、状态污染                          │    │
│  │  2. 复用性 - 可复用的活动组合                                │    │
│  │  3. 生命周期 - 独立的创建、运行、销毁                        │    │
│  │  4. 权限控制 - 独立的角色、权限定义                          │    │
│  │  5. 资源管理 - 独立的资源配额、限制                          │    │
│  │  6. 监控追踪 - 独立的日志、审计                              │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  嵌套节点的类型                                              │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  ├── 子流程 [SUBFLOW]                                        │    │
│  │  │   └── 引用另一个流程定义，完全独立的上下文                │    │
│  │  │                                                          │    │
│  │  ├── 活动块 [BLOCK]                                          │    │
│  │  │   ├── 普通活动块 - 共享父上下文，部分隔离                 │    │
│  │  │   └── 场景 [SCENE] - 完全独立的上下文环境                 │    │
│  │  │                                                          │    │
│  │  └── 外部流程 [EXTERNAL]                                     │    │
│  │      └── 通过组网协议对接的外部流程                          │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 上下文环境设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                    上下文环境层级结构                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  全局上下文 [GlobalContext]                                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 系统级配置、全局变量、系统服务                            │    │
│  │  • 所有流程共享                                              │    │
│  │  • 生命周期：系统启动到关闭                                  │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  流程上下文 [ProcessContext]                                 │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 流程实例数据、流程变量、流程配置                          │    │
│  │  • 流程内所有活动共享                                       │    │
│  │  • 生命周期：流程启动到结束                                  │    │
│  │                                                              │    │
│  │  ├── variables: { ... }        // 流程变量                   │    │
│  │  ├── formData: { ... }         // 表单数据                   │    │
│  │  ├── processConfig: { ... }    // 流程配置                   │    │
│  │  └── metadata: { ... }         // 元数据                     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  活动上下文 [ActivityContext]                                │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 活动实例数据、活动变量、执行状态                          │    │
│  │  • 活动内独享                                               │    │
│  │  • 生命周期：活动开始到完成                                  │    │
│  │                                                              │    │
│  │  ├── taskData: { ... }         // 任务数据                   │    │
│  │  ├── executionState: { ... }   // 执行状态                   │    │
│  │  ├── performer: { ... }        // 执行者信息                 │    │
│  │  └── skillContext: { ... }     // Skill执行上下文            │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  嵌套上下文 [NestedContext]                                  │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │  • 嵌套节点（子流程/活动块/场景）的独立上下文                │    │
│  │  • 根据嵌套类型决定隔离程度                                  │    │
│  │  • 生命周期：嵌套节点开始到结束                              │    │
│  │                                                              │    │
│  │  隔离级别：                                                  │    │
│  │  ├── SHARED    - 共享父上下文（普通活动块）                  │    │
│  │  ├── PARTIAL   - 部分隔离（子流程）                          │    │
│  │  └── ISOLATED  - 完全隔离（场景）                            │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3 场景的上下文环境

```
┌─────────────────────────────────────────────────────────────────────┐
│                    场景的独立上下文环境                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  场景 [SCENE] 是一种特殊的嵌套节点，具有完全独立的上下文环境         │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  场景上下文 [SceneContext]                                   │    │
│  │  ─────────────────────────────────────────────────────────── │    │
│  │                                                              │    │
│  │  隔离级别: ISOLATED（完全隔离）                              │    │
│  │                                                              │    │
│  │  ├── 场景标识                                                │    │
│  │  │   ├── sceneId: string           // 场景实例ID             │    │
│  │  │   ├── sceneType: AUTO|TRIGGER|HYBRID  // 场景类型         │    │
│  │  │   └── sceneStatus: ACTIVE|PAUSED|TERMINATED  // 状态      │    │
│  │  │                                                          │    │
│  │  ├── 独立生命周期                                            │    │
│  │  │   ├── lifecycle: AUTO|TRIGGER|MANUAL  // 生命周期类型     │    │
│  │  │   ├── activateTime: timestamp     // 激活时间             │    │
│  │  │   ├── expireTime: timestamp       // 过期时间             │    │
│  │  │   └── autoDestroy: boolean        // 自动销毁             │    │
│  │  │                                                          │    │
│  │  ├── 独立角色系统                                            │    │
│  │  │   ├── roles: [                        // 角色定义         │    │
│  │  │   │   { name: 'MANAGER', permissions: [...] },           │    │
│  │  │   │   { name: 'MEMBER', permissions: [...] }             │    │
│  │  │   ]                                                      │    │
│  │  │   ├── participants: [...]           // 参与者列表         │    │
│  │  │   └── roleBindings: [...]           // 角色绑定           │    │
│  │  │                                                          │    │
│  │  ├── 独立菜单系统                                            │    │
│  │  │   ├── menus: [                        // 菜单配置         │    │
│  │  │   │   { id, name, icon, url, order, roles: [...] }       │    │
│  │  │   ]                                                      │    │
│  │  │   └── menuVisibility: {...}        // 菜单可见性          │    │
│  │  │                                                          │    │
│  │  ├── 独立能力清单                                            │    │
│  │  │   ├── capabilities: [...]           // 能力列表           │    │
│  │  │   ├── skills: [...]                // 依赖Skill          │    │
│  │  │   └── resources: {...}             // 资源配额            │    │
│  │  │                                                          │    │
│  │  ├── 独立数据空间                                            │    │
│  │  │   ├── variables: {...}             // 场景变量            │    │
│  │  │   ├── formData: {...}              // 表单数据            │    │
│  │  │   └── knowledgeBase: {...}         // 知识库              │    │
│  │  │                                                          │    │
│  │  └── 独立监控追踪                                            │    │
│  │      ├── auditLog: [...]             // 审计日志             │    │
│  │      ├── metrics: {...}              // 监控指标             │    │
│  │      └── alerts: [...]               // 告警配置             │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  与父上下文的关系：                                                  │
│  • 默认不共享任何数据                                                │
│  • 通过显式的输入/输出映射进行数据传递                               │
│  • 通过事件机制进行通信                                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.4 嵌套节点的属性树

```
嵌套节点属性树
├── 基础属性 [basic]
│   ├── nodeId: String
│   ├── name: String
│   ├── description: String
│   └── nodeType: SUBFLOW | BLOCK | EXTERNAL
│
├── 嵌套配置 [nesting]
│   │
│   ├── 嵌套类型 [type]
│   │   └── type: SUBFLOW | BLOCK_NORMAL | SCENE | EXTERNAL
│   │
│   ├── 引用配置 [reference]
│   │   ├── refId: String              // 引用的流程/Skill ID
│   │   ├── refType: PROCESS | SKILL | EXTERNAL
│   │   └── version: String            // 版本
│   │
│   ├── 上下文隔离 [contextIsolation]
│   │   ├── level: SHARED | PARTIAL | ISOLATED
│   │   │
│   │   ├── 变量隔离 [variableIsolation]
│   │   │   ├── inheritVariables: Boolean    // 是否继承父变量
│   │   │   ├── variableScope: PARENT | LOCAL | NONE
│   │   │   └── exposedVariables: [...]      // 暴露给父的变量
│   │   │
│   │   ├── 数据隔离 [dataIsolation]
│   │   │   ├── inheritFormData: Boolean     // 是否继承表单数据
│   │   │   ├── dataScope: PARENT | LOCAL | NONE
│   │   │   └── sharedData: [...]            // 共享的数据
│   │   │
│   │   └── 权限隔离 [permissionIsolation]
│   │       ├── inheritPermissions: Boolean  // 是否继承权限
│   │       ├── permissionScope: PARENT | LOCAL | NONE
│   │       └── roleMapping: {...}           // 角色映射
│   │
│   ├── 输入输出映射 [ioMapping]
│   │   ├── inputMapping: {              // 输入映射
│   │   │   targetVar: '${sourceVar}',
│   │   │   ...
│   │   }
│   │   │
│   │   └── outputMapping: {             // 输出映射
│   │       targetVar: '${resultVar}',
│   │       ...
│   │   }
│   │
│   └── 生命周期配置 [lifecycle]
│       ├── autoStart: Boolean           // 自动启动
│       ├── autoDestroy: Boolean         // 自动销毁
│       ├── destroyCondition: String     // 销毁条件
│       └── maxLifetime: Number          // 最大生命周期(ms)
│
├── 场景专属配置 [sceneConfig] (type=SCENE)
│   │
│   ├── 场景类型 [sceneType]
│   │   └── sceneType: AUTO | TRIGGER | HYBRID
│   │
│   ├── 角色配置 [roles]
│   │   └── roles: [
│   │       {
│   │         name: String,
│   │         description: String,
│   │         permissions: [...],
│   │         minCount: Number,
│   │         maxCount: Number
│   │       }
│   │   ]
│   │
│   ├── 菜单配置 [menus]
│   │   └── menus: [
│   │       {
│   │         id: String,
│   │         name: String,
│   │         icon: String,
│   │         url: String,
│   │         order: Number,
│   │         roles: [...],
│   │         visible: Boolean
│   │       }
│   │   ]
│   │
│   ├── 能力配置 [capabilities]
│   │   ├── skills: [...]               // 依赖的Skill
│   │   ├── capabilities: [...]         // 能力清单
│   │   └── resources: {...}            // 资源配额
│   │
│   ├── 激活流程 [activationSteps]
│   │   └── activationSteps: [
│   │       {
│   │         stepId: String,
│   │         name: String,
│   │         required: Boolean,
│   │         autoExecute: Boolean,
│   │         actions: [...]
│   │       }
│   │   ]
│   │
│   └── 驱动条件 [driverConditions] (sceneType=AUTO)
│       └── driverConditions: [
│           {
│             type: CRON | EVENT | CONDITION,
│             config: {...}
│           }
│         ]
│
├── 子流程专属配置 [subflowConfig] (type=SUBFLOW)
│   ├── refProcessId: String            // 引用的流程ID
│   ├── version: String                 // 版本
│   ├── params: {...}                   // 参数
│   └── callback: {...}                 // 回调配置
│
├── 外部流程专属配置 [externalConfig] (type=EXTERNAL)
│   ├── endpoint: {...}                 // 端点配置
│   ├── protocol: {...}                 // 协议配置
│   └── security: {...}                 // 安全配置
│
└── 执行控制 [execution]
    ├── timeout: Number
    ├── retry: {...}
    ├── async: Boolean
    └── onError: CONTINUE | STOP | RETRY | FALLBACK
```

---

## 4. 节点类型体系 V4

### 4.1 简化后的节点类型树

```
节点类型树 V4
│
├── 流程控制节点 [CONTROL]
│   ├── 开始节点 [START]
│   │   └── position: START
│   │
│   ├── 结束节点 [END]
│   │   └── position: END
│   │
│   └── 网关节点 [GATEWAY]
│       ├── 排他网关 [XOR_GATEWAY]
│       ├── 并行网关 [AND_GATEWAY]
│       └── 包容网关 [OR_GATEWAY]
│
├── 活动节点 [ACTIVITY]
│   │   // 核心：执行者 + Skill
│   │
│   ├── 人工任务 [HUMAN_TASK]
│   │   ├── performer: { type: 'HUMAN', ... }
│   │   └── skill: { skillId: '...', ... }
│   │
│   └── Agent任务 [AGENT_TASK]
│       ├── performer: { type: 'AGENT', ... }
│       └── skill: { skillId: '...', ... }
│
├── 嵌套节点 [NESTING]
│   │   // 核心：独立上下文环境
│   │
│   ├── 子流程 [SUBFLOW]
│   │   ├── nesting.type: SUBFLOW
│   │   ├── nesting.contextIsolation.level: PARTIAL
│   │   └── nesting.reference.refId: processId
│   │
│   ├── 活动块 [BLOCK]
│   │   ├── nesting.type: BLOCK_NORMAL
│   │   ├── nesting.contextIsolation.level: SHARED
│   │   └── nesting.reference.activities: [...]
│   │
│   ├── 场景 [SCENE]
│   │   ├── nesting.type: SCENE
│   │   ├── nesting.contextIsolation.level: ISOLATED
│   │   └── sceneConfig: { roles, menus, capabilities, ... }
│   │
│   └── 外部流程 [EXTERNAL]
│       ├── nesting.type: EXTERNAL
│       ├── nesting.contextIsolation.level: ISOLATED
│       └── externalConfig: { endpoint, protocol, security }
│
└── 组网节点 [NETWORK]
    ├── 协调器 [COORDINATOR]
    ├── 端点 [ENDPOINT]
    └── 桥接器 [BRIDGE]
```

### 4.2 节点类型枚举定义 V4

```javascript
const NodeType = {
    CONTROL: 'CONTROL',
    ACTIVITY: 'ACTIVITY',
    NESTING: 'NESTING',
    NETWORK: 'NETWORK'
};

const NestingType = {
    SUBFLOW: 'SUBFLOW',
    BLOCK_NORMAL: 'BLOCK_NORMAL',
    SCENE: 'SCENE',
    EXTERNAL: 'EXTERNAL'
};

const ContextIsolationLevel = {
    SHARED: 'SHARED',      // 共享父上下文
    PARTIAL: 'PARTIAL',    // 部分隔离
    ISOLATED: 'ISOLATED'   // 完全隔离
};

const PerformerType = {
    HUMAN: 'HUMAN',
    AGENT: 'AGENT'
};

const SceneType = {
    AUTO: 'AUTO',          // 自主场景
    TRIGGER: 'TRIGGER',    // 触发场景
    HYBRID: 'HYBRID'       // 混合场景
};
```

---

## 5. 属性树形结构 V4

### 5.1 统一属性结构

```
节点属性树 V4
├── 基础属性 [basic]
│   ├── nodeId: String
│   ├── name: String
│   ├── description: String
│   └── nodeType: Enum [CONTROL|ACTIVITY|NESTING|NETWORK]
│
├── 流程定义属性 [flowDefinition]
│   ├── position: START | NORMAL | END
│   ├── positionCoord: { x: Number, y: Number }
│   │
│   └── 流程控制 [flowControl]
│       ├── join: XOR | AND | OR
│       ├── split: XOR | AND | OR
│       └── condition: String
│
├── 执行定义属性 [execution]
│   │
│   ├── 执行者 [performer]
│   │   ├── type: HUMAN | AGENT
│   │   ├── assignee: String (type=HUMAN)
│   │   ├── performType: SINGLE | JOINTSIGN | COUNTERSIGN
│   │   └── agentId: String (type=AGENT)
│   │
│   ├── Skill配置 [skill]
│   │   ├── skillId: String              // 调用的Skill ID
│   │   ├── skillCategory: LLM | FORM | SERVICE | WORKFLOW | ...
│   │   │
│   │   ├── 输入映射 [inputMapping]
│   │   │   └── { targetParam: '${sourceVar}', ... }
│   │   │
│   │   ├── 输出映射 [outputMapping]
│   │   │   └── { targetVar: '${resultParam}', ... }
│   │   │
│   │   ├── 运行时配置 [config]
│   │   │   └── { ... }  // Skill特定的配置
│   │   │
│   │   └── 降级策略 [fallback]
│   │       ├── skillId: String          // 降级Skill
│   │       └── condition: String        // 降级条件
│   │
│   └── 执行控制 [control]
│       ├── timeout: Number
│       ├── retry: Object
│       ├── async: Boolean
│       └── onError: CONTINUE | STOP | RETRY | FALLBACK
│
├── 嵌套配置 [nesting]
│   │   // 详见 3.4 嵌套节点的属性树
│   │
│   ├── type: SUBFLOW | BLOCK_NORMAL | SCENE | EXTERNAL
│   ├── contextIsolation: { level, variableIsolation, dataIsolation, permissionIsolation }
│   ├── ioMapping: { inputMapping, outputMapping }
│   ├── lifecycle: { autoStart, autoDestroy, ... }
│   │
│   ├── sceneConfig: { sceneType, roles, menus, capabilities, ... } (type=SCENE)
│   ├── subflowConfig: { refProcessId, version, params, ... } (type=SUBFLOW)
│   └── externalConfig: { endpoint, protocol, security } (type=EXTERNAL)
│
├── 组网配置 [networking]
│   ├── enabled: Boolean
│   ├── protocol: A2A | HTTP | MQTT | KAFKA
│   ├── coordinator: {...}
│   ├── endpoint: {...}
│   └── bridge: {...}
│
├── 历史定义属性 [history]
│   ├── enableAudit: Boolean
│   ├── retention: Object
│   └── tracking: Object
│
└── 兼容属性 [compatibility]
    ├── RIGHT: Object (P2P兼容)
    ├── FORM: Object (P2P兼容)
    └── SERVICE: Object (P2P兼容)
```

### 5.2 上下文环境属性

```
上下文环境属性
│
├── 全局上下文 [globalContext]
│   ├── systemConfig: {...}
│   ├── globalVariables: {...}
│   └── systemServices: [...]
│
├── 流程上下文 [processContext]
│   ├── processId: String
│   ├── processVariables: {...}
│   ├── formData: {...}
│   ├── processConfig: {...}
│   └── metadata: {...}
│
├── 活动上下文 [activityContext]
│   ├── activityId: String
│   ├── taskData: {...}
│   ├── executionState: {...}
│   ├── performer: {...}
│   └── skillContext: {...}
│
└── 嵌套上下文 [nestedContext]
    ├── contextId: String
    ├── isolationLevel: SHARED | PARTIAL | ISOLATED
    │
    ├── 变量空间 [variableSpace]
    │   ├── inherited: {...}           // 继承的变量
    │   ├── local: {...}               // 本地变量
    │   └── exposed: {...}             // 暴露的变量
    │
    ├── 数据空间 [dataSpace]
    │   ├── inherited: {...}           // 继承的数据
    │   ├── local: {...}               // 本地数据
    │   └── shared: {...}              // 共享的数据
    │
    ├── 权限空间 [permissionSpace]
    │   ├── inherited: {...}           // 继承的权限
    │   ├── local: {...}               // 本地权限
    │   └── roleMapping: {...}         // 角色映射
    │
    └── 生命周期 [lifecycle]
        ├── createTime: Timestamp
        ├── activateTime: Timestamp
        ├── expireTime: Timestamp
        └── destroyTime: Timestamp
```

---

## 6. 迁移与兼容策略

### 6.1 旧执行类型到Skill映射

| 旧执行类型 | 新Skill类型 | Skill ID示例 | 说明 |
|-----------|------------|-------------|------|
| FORM | FORM_SKILL | skill-form-default | 表单交互 |
| SKILL | SERVICE_SKILL | skill-service-* | 服务调用 |
| LLM | LLM_SKILL | skill-llm-* | 大模型推理 |
| A2UI | FORM_SKILL | skill-form-a2ui | 动态表单 |

### 6.2 属性迁移映射

```
旧属性路径                         新属性路径
─────────────────────────────────────────────────────
execution.type               →    skill.skillCategory
execution.form               →    skill.config (skillCategory=FORM)
execution.skill              →    skill.config (skillCategory=SERVICE)
execution.llm                →    skill.config (skillCategory=LLM)
flowDefinition.nesting       →    nesting.*
flowDefinition.blockConfig   →    nesting.sceneConfig (type=SCENE)
```

### 6.3 数据迁移脚本

```javascript
function migrateActivityDefV3ToV4(oldDef) {
    const newDef = { ...oldDef };
    
    // 迁移执行类型到Skill
    const skillMapping = {
        'FORM': { category: 'FORM', skillId: 'skill-form-default' },
        'SKILL': { category: 'SERVICE', skillId: oldDef.skill?.skillId },
        'LLM': { category: 'LLM', skillId: oldDef.llm?.provider }
    };
    
    const oldType = oldDef.execution?.type;
    if (oldType && skillMapping[oldType]) {
        newDef.skill = {
            skillId: skillMapping[oldType].skillId,
            skillCategory: skillMapping[oldType].category,
            config: oldDef.execution[oldType.toLowerCase()] || {},
            inputMapping: oldDef.execution.inputMapping || {},
            outputMapping: oldDef.execution.outputMapping || {}
        };
        delete newDef.execution.type;
        delete newDef.execution.form;
        delete newDef.execution.skill;
        delete newDef.execution.llm;
    }
    
    // 迁移嵌套配置
    if (oldDef.flowDefinition?.nesting) {
        newDef.nesting = {
            type: oldDef.flowDefinition.nesting.type,
            contextIsolation: {
                level: getContextIsolationLevel(oldDef.flowDefinition.nesting.type)
            },
            ...oldDef.flowDefinition.nesting
        };
    }
    
    return newDef;
}

function getContextIsolationLevel(nestingType) {
    const levelMap = {
        'SUBFLOW': 'PARTIAL',
        'BLOCK_NORMAL': 'SHARED',
        'SCENE': 'ISOLATED',
        'EXTERNAL': 'ISOLATED'
    };
    return levelMap[nestingType] || 'SHARED';
}
```

---

## 附录

### A. 相关设计文档索引

| 文档名称 | 路径 | 说明 |
|---------|------|------|
| SkillFlow节点类型与属性体系设计文档 V1 | `SkillFlow节点类型与属性体系设计文档.md` | V1版本 |
| SkillFlow节点类型与属性体系设计文档 V2 | `SkillFlow节点类型与属性体系设计文档V2.md` | V2版本 |
| SkillFlow节点类型与属性体系设计文档 V3 | `SkillFlow节点类型与属性体系设计文档V3.md` | V3版本 |
| Skills分类统计报告 | `e:\github\ooder-skills\archive\skills_classification_report.md` | Skills分类 |
| 技能分类框架 | `e:\github\ooder-skills\archive\v2.3.1-docs\archive\scene\skill-classification-framework-v2.md` | 分类框架 |

### B. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| 1.0 | 2026-04-20 | 初始版本 |
| 2.0 | 2026-04-20 | 重新定义核心概念：执行者×执行内容、场景⊂活动块 |
| 3.0 | 2026-04-20 | 新增组网协议层约定设计、定义期概念与流程、详细UI/UE方案 |
| 4.0 | 2026-04-20 | **重大重构**：执行体=Skill、嵌套节点与上下文环境设计 |

---

**文档结束**
