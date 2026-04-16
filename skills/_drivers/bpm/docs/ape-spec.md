# BPM流程设计器 (bpm-designer) - 完整技术规格文档

> **文档版本**: 3.0.2  
> **生成日期**: 2026-04-13  
> **项目仓库**: gitee.com/ooderCN/ade  
> **测试目标**: Trae Solo Web 长任务一文到底能力极限测试  
> **文档定位**: 面向 AI 编程平台的完整可执行规格，支持从零重建整个系统

---

## 第一章 项目概述

### 1.1 项目信息

| 属性 | 值 |
|------|------|
| 项目名称 | BPM流程设计器 (bpm-designer) |
| 项目标识 | net.ooder.bpm.designer |
| 版本号 | 3.0.2 |
| GroupId | net.ooder |
| ArtifactId | bpm-designer |
| 打包方式 | jar |
| Java版本 | 21 |
| Spring Boot版本 | 3.4.4 |
| 作者 | Ooder Team |
| 协议 | Ooder License |

### 1.2 项目定位

BPM流程设计器是一个基于Ooder框架的AI原生BPM流程可视化设计器，定位为BPM工作流系统的前端设计工具。它不是传统意义上的流程画图工具，而是一个融合了自然语言交互、AI辅助推导、场景驱动设计的智能流程设计平台。

核心差异化特征：
1. **SPAC编程模型** - 场景(Scene)驱动的流程设计，而非传统的表单驱动
2. **AI原生** - 内置LLM对话、Function Calling、智能推导能力
3. **Agent融合** - 活动节点支持Agent配置，实现AI代理执行
4. **插件化面板** - 动态Schema驱动的属性面板，支持运行时插件注册
5. **双模式交互** - 传统拖拽设计 + 自然语言对话设计

### 1.3 系统边界

```
┌──────────────────────────────────────────────────────────────────────┐
│                        系统边界定义                                    │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐  │
│  │   bpm-designer   │    │   bpmserver      │    │   外部服务       │  │
│  │   (本系统)        │───▶│   (流程引擎)      │───▶│   LLM API       │  │
│  │                  │    │                  │    │   组织架构API    │  │
│  │  - 可视化设计     │    │  - 流程存储       │    │   能力服务API    │  │
│  │  - NLP交互       │    │  - 流程执行       │    │   表单服务API    │  │
│  │  - AI推导        │    │  - 实例管理       │    │   场景服务API    │  │
│  │  - 属性编辑       │    │  - 权限控制       │    │                  │  │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘  │
│                                                                      │
│  bpm-designer 不负责:                                                │
│  - 流程实例的运行时执行                                               │
│  - 用户权限的认证和授权                                               │
│  - 数据库的持久化存储                                                 │
│  - 消息队列和异步任务                                                 │
│                                                                      │
│  bpm-designer 负责:                                                  │
│  - 流程定义的可视化创建和编辑                                         │
│  - 自然语言到流程定义的转换                                           │
│  - AI辅助的执行者/能力/表单推导                                       │
│  - 流程定义的导入导出                                                 │
│  - 设计过程的实时协作(WebSocket)                                      │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 1.4 术语表

| 术语 | 英文 | 定义 |
|------|------|------|
| 流程定义 | ProcessDef | 一个完整的BPM流程的静态定义，包含活动、路由、权限等 |
| 活动定义 | ActivityDef | 流程中的一个节点，代表一个工作步骤 |
| 路由定义 | RouteDef | 连接两个活动的有向边，定义流转条件 |
| 执行者 | Performer | 活动的办理人，可以是用户、角色、部门或表达式 |
| 能力 | Capability | 活动关联的业务能力，如邮件、日历、文档等 |
| 场景 | Scene | 驱动流程设计的业务场景，包含触发条件、入口条件、退出条件 |
| Agent | Agent | AI代理配置，定义LLM模型、工具、提示词等 |
| 推导 | Derivation | AI根据活动描述自动推导执行者、能力、表单的过程 |
| SPAC | Scene-Process-Activity-Capability | 场景驱动的流程设计方法论 |
| XPDL | XML Process Definition Language | 流程定义的XML交换格式 |
| BPD | Business Process Diagram | 流程图的可视化表示 |
| DTO | Data Transfer Object | 前后端数据传输对象 |
| NLP | Natural Language Processing | 自然语言处理 |
| LLM | Large Language Model | 大语言模型 |
| Function Calling | - | LLM的函数调用能力 |
| MCP | Model Context Protocol | 模型上下文协议 |

---

## 第二章 技术选型

### 2.1 后端技术栈

| 技术 | 版本 | 用途 | Maven坐标 |
|------|------|------|-----------|
| Java | 21 | 主要开发语言 | - |
| Spring Boot | 3.4.4 | 应用框架 | org.springframework.boot:spring-boot-starter-parent |
| Spring Web | 3.4.4 | REST API | spring-boot-starter-web |
| Spring WebSocket | 3.4.4 | 实时通信 | spring-boot-starter-websocket |
| Spring Thymeleaf | 3.4.4 | 模板引擎 | spring-boot-starter-thymeleaf |
| Spring Validation | 3.4.4 | 参数校验 | spring-boot-starter-validation |
| FastJSON2 | 2.0.43 | JSON序列化 | com.alibaba.fastjson2:fastjson2 |
| Jackson YAML | (Spring Boot内置) | YAML解析 | jackson-dataformat-yaml |
| Lombok | 1.18.30 | 代码简化 | org.projectlombok:lombok |
| Spring Boot Test | 3.4.4 | 测试框架 | spring-boot-starter-test |
| Maven Compiler | 3.11.0 | 编译插件 | maven-compiler-plugin |
| Maven Surefire | 3.2.5 | 测试插件 | maven-surefire-plugin |

### 2.2 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| 原生JavaScript | ES6+ | 前端开发，无框架依赖 |
| Ooder框架 | 3.x | UI组件框架(注解驱动) |
| CSS3 | - | 样式设计，CSS变量主题系统 |
| RemixIcon | - | 图标库(ri-*前缀) |
| SVG | - | 画布渲染引擎 |
| DOM API | - | 面板渲染和交互 |

### 2.3 依赖服务

| 服务 | 端口 | 用途 | 连接方式 |
|------|------|------|----------|
| bpmserver | 8084 | BPM流程引擎后端 | REST API (http://127.0.0.1:8084/bpm) |
| 能力服务 | 8085 | 业务能力匹配 | REST API (http://127.0.0.1:8085/capability) |
| 表单服务 | 8086 | 表单匹配和生成 | REST API (http://127.0.0.1:8086/form) |
| 场景服务 | 8087 | 场景引擎 | REST API (http://127.0.0.1:8087/scene) |
| LLM服务 | - | AI辅助功能 | OpenAI兼容API (阿里云Qwen) |
| 管理端口 | 8089 | Actuator监控 | HTTP |

### 2.4 Maven配置

本地Maven仓库路径: `D:\maven\.m2`

---

## 第三章 系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          BPM流程设计器 (bpm-designer)                        │
│                          端口: 8088                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                        前端层 (Frontend)                             │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │    │
│  │  │  App.js  │ │Canvas.js │ │ Chat.js  │ │Panel.js  │ │ Tree.js  │ │    │
│  │  │  应用入口 │ │ 画布引擎  │ │ AI对话   │ │ 属性面板  │ │ 流程树   │ │    │
│  │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ │    │
│  │       │            │            │            │            │        │    │
│  │  ┌────┴────────────┴────────────┴────────────┴────────────┴────┐   │    │
│  │  │                    Store.js (状态管理)                        │   │    │
│  │  │  - 流程数据管理  - 撤销/重做  - 事件总线  - 自动保存          │   │    │
│  │  └────────────────────────┬────────────────────────────────────┘   │    │
│  │                           │                                        │    │
│  │  ┌────────────────────────┴────────────────────────────────────┐   │    │
│  │  │                    Api.js (HTTP客户端)                       │   │    │
│  │  │  - REST API调用  - 请求封装  - 错误处理                      │   │    │
│  │  └────────────────────────┬────────────────────────────────────┘   │    │
│  └───────────────────────────┼─────────────────────────────────────────┘    │
│                              │ HTTP/JSON                                    │
│  ┌───────────────────────────┴─────────────────────────────────────────┐    │
│  │                      后端层 (Backend)                                │    │
│  │                                                                     │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │                   Controller 层                               │  │    │
│  │  │  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────┐ │  │    │
│  │  │  │DesignerController│ │DesignerNlpCtrl   │ │DerivationCtrl│ │  │    │
│  │  │  │ /api/bpm/*       │ │ /api/bpm/nlp/*   │ │ /api/bpm/    │ │  │    │
│  │  │  │                  │ │                  │ │  derivation/*│ │  │    │
│  │  │  └────────┬─────────┘ └────────┬─────────┘ └──────┬───────┘ │  │    │
│  │  └───────────┼────────────────────┼──────────────────┼─────────┘  │    │
│  │              │                    │                   │            │    │
│  │  ┌───────────┴────────────────────┴──────────────────┴─────────┐  │    │
│  │  │                    Service 层                                │  │    │
│  │  │  DesignerService │ DesignerNlpService │ PerformerDerivationSvc│  │    │
│  │  │  CapabilityMatch │ FormMatchingService │ PanelRenderService   │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │                  LLM集成层                                  │  │    │
│  │  │  LLMServiceImpl │ PromptTemplateManager │ DesignerFunctionRegistry│
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │                  基础设施层                                 │  │    │
│  │  │  CacheService │ DerivationWebSocketHandler │ DataSourceAdapter│
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  └──────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
  │  bpmserver   │   │  LLM API     │   │  外部服务     │
  │  :8084/bpm   │   │  (阿里云Qwen) │   │  能力/表单/   │
  │              │   │              │   │  场景服务     │
  └──────────────┘   └──────────────┘   └──────────────┘
```

### 3.2 后端包结构

```
net.ooder.bpm.designer
├── BpmDesignerApplication.java
├── cache/         (CacheConfig, CacheKeyGenerator, CacheService, CacheStats)
├── config/        (WebMvcConfig)
├── controller/    (DesignerController, DesignerDerivationController, DesignerNlpController, IndexController)
├── datasource/    (AbstractDataSourceAdapter, BpmDataSourceAdapter, DataSourceAdapter)
├── dto/           (ActivityDTO, PositionCoordDTO, ProcessDTO, RouteDTO)
├── dto/sub/       (AgentConfigDTO, DeviceDTO, EventDTO, RightDTO, RoutingDTO, SceneConfigDTO, ServiceDTO, SubFlowDTO, TimingDTO)
├── function/      (DesignerFunctionDefinition, DesignerFunctionRegistry, FunctionCallRequest)
├── llm/           (FunctionCall, LLMResponse, LLMService, LLMServiceImpl)
├── model/         (ActivityDef, ApiResponse, ProcessDef, RouteDef)
├── prompt/        (DesignerPromptBuilder, PromptTemplate, PromptTemplateManager)
├── service/       (CapabilityMatchingService, DesignerNlpService, DesignerService, FormMatchingService, PanelRenderService, PerformerDerivationService)
└── websocket/     (DerivationWebSocketHandler)
```

### 3.3 前端模块结构

```
static/designer/
├── index.html
├── css/           (base.css, chat.css, main.css, node.css, palette.css, panel.css, tree.css, var.css)
├── js/
│   ├── App.js, Canvas.js, Chat.js, ContextMenu.js, Elements.js, Palette.js, Panel.js, TabManager.js, Toolbar.js, Tree.js
│   ├── model/     (ActivityDef.js, ActivityDefNew.js, AgentDef.js, ProcessDef.js, ProcessDefNew.js, RouteDef.js, SceneDef.js)
│   ├── panel/     (PanelInitializer.js, PanelManager.js, PanelPlugin.js, PluginEnvironment.js)
│   ├── panel/components/ (ExternalDictionaryPanel.js)
│   ├── panel/core/       (AbstractPluginPanel.js, PanelPluginManager.js, PluginDataAdapter.js, PluginDataSource.js)
│   ├── panel/schemas/    (ActivityPanelSchema.js, ProcessPanelSchema.js, RoutePanelSchema.js)
│   ├── sdk/       (Api.js, BpmJsonSchema.js, DataAdapter.js, EnumMapping.js, PanelSchema.js, PropertyChecker.js, Store.js, Theme.js)
│   └── test/      (BpmModelTest.js)
└── lib/icon/      (icon.css, icons.js, remixicon.woff2)
```

### 3.4 架构设计原则

1. **前后端分离**: 前端纯静态资源(HTML/CSS/JS)，后端提供REST API
2. **DTO隔离**: 前后端通过DTO传输数据，Model层独立
3. **事件驱动**: 前端使用事件总线(Store)解耦组件
4. **插件化**: 面板系统采用插件架构，支持运行时注册
5. **适配器模式**: 数据源通过适配器模式支持多种后端
6. **模板方法**: Prompt模板通过YAML配置，支持变量替换
7. **缓存策略**: 内存缓存+TTL过期，减轻后端压力

---

## 第四章 数据模型规格

### 4.1 流程定义 (ProcessDef)

#### 4.1.1 后端模型字段

| 字段组 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| 基本属性 | processDefId | String | 流程定义ID，唯一标识 |
| | name | String | 流程名称 |
| | description | String | 流程描述 |
| | classification | String | 分类(办公流程/生产流程等) |
| | systemCode | String | 系统代码 |
| | accessLevel | String | 访问级别(PUBLIC/PRIVATE/RESTRICTED) |
| 版本信息 | version | Integer | 版本号 |
| | status | String | 状态(DRAFT/PUBLISHED/ARCHIVED) |
| | creatorId | String | 创建者ID |
| | creatorName | String | 创建者名称 |
| | createdTime | String | 创建时间 |
| | modifierId | String | 修改者ID |
| | modifierName | String | 修改者名称 |
| | modifyTime | String | 修改时间 |
| | updatedTime | String | 更新时间 |
| | activeTime | String | 激活时间 |
| | freezeTime | String | 冻结时间 |
| 时限配置 | limit | Integer | 时限值 |
| | durationUnit | String | 时限单位(D/H/M) |
| 流程结构 | startNode | Map | 开始节点配置 |
| | endNodes | List<Map> | 结束节点列表(支持多个) |
| | activities | List<ActivityDef> | 活动列表 |
| | routes | List<RouteDef> | 路由列表 |
| 监听器和权限 | listeners | List<Map> | 监听器列表 |
| | rightGroups | List<Map> | 权限组列表 |
| 扩展属性 | mark | String | 标记 |
| | lock | String | 锁定状态 |
| | autoSave | Boolean | 自动保存 |
| | noSqlType | String | NoSQL类型 |
| | tableNames | List<String> | 关联表名 |
| | moduleNames | List<String> | 关联模块名 |
| | formulas | List<Map> | 公式列表 |
| | parameters | List<Map> | 参数列表 |
| | extendedAttributes | Map | 扩展属性 |
| | agentConfig | Map | Agent配置 |
| | sceneConfig | Map | 场景配置 |

#### 4.1.2 开始节点 (StartNode) 结构

```json
{
    "participantId": "Participant_Start",
    "firstActivityId": "act_submit",
    "positionCoord": { "x": 50, "y": 200 },
    "routing": "NO_ROUTING"
}
```

#### 4.1.3 结束节点 (EndNode) 结构

```json
{
    "participantId": "Participant_End",
    "lastActivityId": "act_end",
    "positionCoord": { "x": 800, "y": 200 },
    "routing": "NO_ROUTING"
}
```

### 4.2 活动定义 (ActivityDef)

#### 4.2.1 活动类型枚举 (ActivityType)

| 代码 | 标签 | 说明 |
|------|------|------|
| TASK | 用户任务 | 需要人工处理的任务节点 |
| SERVICE | 服务任务 | 自动执行的服务调用 |
| SCRIPT | 脚本任务 | 执行脚本代码 |
| START | 开始节点 | 流程起始点 |
| END | 结束节点 | 流程结束点 |
| XOR_GATEWAY | 排他网关 | 条件分支(只走一条) |
| AND_GATEWAY | 并行网关 | 并行分支(所有分支都走) |
| OR_GATEWAY | 包容网关 | 条件并行分支 |
| SUBPROCESS | 子流程 | 嵌套流程 |
| LLM_TASK | LLM任务 | AI智能处理任务 |

#### 4.2.2 活动分类枚举 (ActivityCategory)

| 代码 | 标签 | 说明 |
|------|------|------|
| HUMAN | 人工活动 | 需要人工参与的活动 |
| AGENT | Agent活动 | AI代理执行的活动 |
| SCENE | 场景活动 | 场景驱动执行的活动 |

#### 4.2.3 活动位置枚举 (ActivityPosition)

| 代码 | 标签 | 说明 |
|------|------|------|
| START | 开始位置 | 流程起始节点 |
| END | 结束位置 | 流程结束节点 |
| NORMAL | 普通位置 | 中间处理节点 |

#### 4.2.4 实现方式枚举 (Implementation)

| 代码 | 说明 |
|------|------|
| IMPL_NO | 无实现 - 手动活动 |
| IMPL_TOOL | 工具实现 - 调用工具 |
| IMPL_SUBFLOW | 子流程实现 - 调用子流程 |
| IMPL_OUTFLOW | 外部流程实现 - 调用外部流程 |
| IMPL_DEVICE | 设备实现 - IoT设备交互 |
| IMPL_EVENT | 事件实现 - 事件驱动 |
| IMPL_SERVICE | 服务实现 - 服务调用 |

#### 4.2.5 RIGHT属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| performType | String | 办理类型(SINGLE/MULTIPLE/JOINTSIGN/NEEDNOTSELECT/NOSELECT) |
| performSequence | String | 办理顺序 |
| performerSelectedId | String | 办理人选择ID(公式或表达式) |
| readerSelectedId | String | 阅办人选择ID |
| specialSendScope | String | 特送范围(ALL/DEPT/ROLE) |
| canInsteadSign | String | 可否代签(YES/NO) |
| canTakeBack | String | 可否收回(YES/NO) |
| canReSend | String | 可否重发(YES/NO) |
| movePerformerTo | String | 办理人移动方向 |
| moveSponsorTo | String | 发起人移动方向 |
| moveReaderTo | String | 阅办人移动方向 |

#### 4.2.6 FORM属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| formId | String | 表单ID |
| formName | String | 表单名称 |
| formType | String | 表单类型(CUSTOM/SYSTEM/EXTERNAL) |
| formUrl | String | 表单URL |

#### 4.2.7 SERVICE属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| httpMethod | String | HTTP方法(GET/POST/PUT/DELETE) |
| httpUrl | String | 服务URL |
| httpRequestType | String | 请求类型 |
| httpResponseType | String | 响应类型 |
| httpServiceParams | String | 服务参数(JSON) |
| serviceSelectedId | String | 服务选择ID |

#### 4.2.8 WORKFLOW属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| deadLineOperation | String | 超时操作(ALERT/AUTO_COMPLETE/ESCALATE) |
| specialScope | String | 特送范围(ALL/DEPT/ROLE) |

### 4.3 路由定义 (RouteDef)

| 字段 | 类型 | 说明 |
|------|------|------|
| routeDefId | String | 路由定义ID |
| name | String | 路由名称 |
| description | String | 路由描述 |
| from | String | 起始活动ID |
| to | String | 目标活动ID |
| routeOrder | Integer | 路由顺序 |
| routeDirection | String | 路由方向(FORWARD/BACK) |
| routeConditionType | String | 条件类型(DEFAULT/CONDITION/OTHERWISE) |
| condition | String | 条件表达式 |
| extendedAttributes | Map | 扩展属性 |

### 4.4 子配置DTO规格

**TimingDTO**: limitTime(Integer), alertTime(Integer), durationUnit(String), startTime(String), endTime(String), remindType(String), remindInterval(Integer)

**RoutingDTO**: join(String), split(String), canRouteBack(String), routeBackMethod(String), canSpecialSend(String), specialScope(String), defaultRoute(String), parallelMode(String), mergeCondition(String)

**RightDTO**: moveSponsorTo(String), performer(String), performerType(String), participationType(String), participationScope(String), participationScopeValue(String), candidateUsers(List<String>), candidateGroups(List<String>), candidateRoles(List<String>), assignee(String), owner(String), reassignable(Boolean), delegatable(Boolean), transferable(Boolean)

**SubFlowDTO**: subProcessDefId(String), subProcessName(String), subProcessVersion(String), executionMode(String), waitForCompletion(Boolean), dataMapping(Map), parameterMapping(Map)

**DeviceDTO**: deviceId(String), deviceName(String), deviceType(String), deviceModel(String), connectionString(String), protocol(String), parameters(Map)

**ServiceDTO**: serviceId(String), serviceName(String), serviceType(String), serviceUrl(String), serviceMethod(String), serviceProtocol(String), inputParameters(Map), outputParameters(Map), headers(Map<String,String>), timeout(Integer), retryCount(Integer)

**EventDTO**: eventId(String), eventName(String), eventType(String), eventTrigger(String), triggerCondition(String), eventAction(String), actionParameters(Map), listenerClass(String), listenerExpression(String)

**AgentConfigDTO**: agentId(String), agentName(String), agentType(String), modelName(String), systemPrompt(String), temperature(Double), maxTokens(Integer), tools(List<String>), capabilities(List<String>), inputSchema(Map), outputSchema(Map), memoryEnabled(Boolean), contextWindow(Integer)

**SceneConfigDTO**: sceneId(String), sceneName(String), sceneType(String), sceneCategory(String), triggerEvents(List<String>), entryConditions(List<String>), exitConditions(List<String>), sceneData(Map), sceneRules(List<Map>), sceneState(String), priority(Integer), validityPeriod(Map<String,String>)

### 4.5 统一API响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

---

## 第五章 API规格

### 5.1 流程设计API (`/api/bpm`)

| 方法 | 端点 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | /process/{processId}/version/{version} | 获取流程定义 | - | ApiResponse<ProcessDef> |
| GET | /process/{processId}/version/latest | 获取最新版本 | - | ApiResponse<ProcessDef> |
| GET | /process | 获取流程列表 | Query: category,status,page,size | ApiResponse<List<ProcessDef>> |
| POST | /process | 创建流程 | ProcessDef JSON | ApiResponse<ProcessDef> |
| PUT | /process/{processId} | 更新流程 | ProcessDef JSON | ApiResponse<ProcessDef> |
| DELETE | /process/{processId} | 删除流程 | - | ApiResponse<Void> |
| GET | /process/tree | 获取流程树 | - | ApiResponse<List<Map>> |
| GET | /process/{processId}/export/yaml | 导出YAML | - | ApiResponse<String> |
| POST | /process/import/yaml | 导入YAML | {yaml: string} | ApiResponse<ProcessDef> |
| POST | /process/{processId}/activity | 添加活动 | Map JSON | ApiResponse<ProcessDef> |
| PUT | /process/{processId}/activity/{activityId} | 更新活动 | Map JSON | ApiResponse<ProcessDef> |
| DELETE | /process/{processId}/activity/{activityId} | 删除活动 | - | ApiResponse<ProcessDef> |
| POST | /process/{processId}/route | 添加路由 | Map JSON | ApiResponse<ProcessDef> |
| PUT | /process/{processId}/route/{routeId} | 更新路由 | Map JSON | ApiResponse<ProcessDef> |
| DELETE | /process/{processId}/route/{routeId} | 删除路由 | - | ApiResponse<ProcessDef> |
| GET | /capabilities | 获取能力列表 | - | ApiResponse<List<String>> |
| GET | /enums/{enumType} | 获取枚举选项 | - | ApiResponse<List<Map>> |

### 5.2 NLP API (`/api/bpm/nlp`)

| 方法 | 端点 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | /chat | 对话处理 | {message, context} | NlpResponse |
| POST | /process/create | 从NLP创建流程 | {description, context} | ProcessDefDTO |
| POST | /activity/create | 从NLP创建活动 | {description, context} | ActivityDefDTO |
| POST | /attribute/update | 从NLP更新属性 | {attributeName, value, context} | Map |
| POST | /suggestions | 获取建议 | {context} | List<NlpSuggestion> |
| POST | /validate | 验证流程 | {processDef, context} | String |
| POST | /describe/process | 描述流程 | {processDef} | String |
| POST | /describe/activity | 描述活动 | {activityDef} | String |
| POST | /intent/analyze | 意图分析 | {userInput} | List<NlpIntent> |
| POST | /entities/extract | 实体提取 | {userInput, intentType} | Map |

### 5.3 智能推导API (`/api/bpm/designer/derivation`)

| 方法 | 端点 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | /performer | 执行者推导 | {context, activityDesc} | PerformerDerivationResultDTO |
| POST | /performer/search | 候选人搜索 | {query, filters} | List<PerformerCandidate> |
| POST | /capability | 能力匹配 | {context, activityDesc} | CapabilityMatchingResultDTO |
| POST | /capability/smart | 智能能力匹配 | {context, activityDesc} | CapabilityMatchingResultDTO |
| POST | /form | 表单匹配 | {context, activityDesc} | FormMatchingResultDTO |
| POST | /form/smart | 智能表单匹配 | {context, activityDesc} | FormMatchingResultDTO |
| POST | /form/generate | 生成表单Schema | {context, activityDesc} | FormSchema |
| POST | /panel/performer | 构建执行者面板 | {result} | PanelRenderDataDTO |
| POST | /panel/capability | 构建能力面板 | {result} | PanelRenderDataDTO |
| POST | /panel/form | 构建表单面板 | {result} | PanelRenderDataDTO |
| POST | /panel/activity | 构建活动面板 | {performerResult, capabilityResult, formResult} | PanelRenderDataDTO |
| POST | /full | 完整推导 | {context, activityDesc} | 完整推导结果 |
| GET | /functions | 获取可用函数 | - | List<DesignerFunctionDefinition> |
| GET | /functions/schemas | 获取函数Schema | - | List<Map> |
| GET | /functions/category/{category} | 按类别获取函数 | - | List<DesignerFunctionDefinition> |

---

## 第六章 前端架构规格

### 6.1 应用入口 (App.js)

初始化流程: _initIcons → ThemeFactory.get → new Store → ApiFactory.create → _initSidebar → _initCanvas → _initPanel → _initChat → _initToolbar → _initTabManager → _bindGlobalEvents

全局快捷键: Ctrl+S(保存), Ctrl+Z(撤销), Ctrl+Y(重做), Delete(删除)

### 6.2 画布引擎 (Canvas.js)

SVG渲染引擎，核心属性: nodes(Map), edges(Map), scale(1), offsetX/offsetY, nodeWidth(120), nodeHeight(60), smallNodeSize(50), gridSize(20)

节点渲染规则:
- START: 圆形，绿色
- END: 圆形，红色
- TASK: 圆角矩形，蓝色
- SERVICE: 圆角矩形，橙色
- GATEWAY: 菱形，黄色
- SUBPROCESS: 圆角矩形(双线框)，紫色
- LLM_TASK: 圆角矩形，渐变色

### 6.3 状态管理器 (Store.js)

事件总线状态管理器。核心属性: process, currentActivity, currentRoute, dirty, history(50), _autoSaveDelay(1000ms)

事件列表: process:change, activity:select, activity:update, route:update, dirty:change

### 6.4 面板插件系统 (PanelManager.js)

插件分类:
- **通用属性(common)**: BasicPanelPlugin, AgentBasicPanelPlugin, SceneBasicPanelPlugin, ProcessBasicPanelPlugin, StartNodePanelPlugin, EndNodesPanelPlugin
- **业务属性(business)**: TimingPanelPlugin, FlowControlPanelPlugin, RightPanelPlugin, FormPanelPlugin, ServicePanelPlugin, LLMPanelPlugin, AgentToolsPanelPlugin, SceneEnginePanelPlugin, SceneParamsPanelPlugin, SceneCapabilityPanelPlugin, ListenersPanelPlugin, RightGroupsPanelPlugin, ProcessVariablesPanelPlugin, ProcessTimingPanelPlugin
- **插件属性(plugin)**: ActivityListenerPanelPlugin, ExecutionListenerPanelPlugin, ExpressionPanelPlugin, MultiInstancePanelPlugin, ExtensionPropertiesPanelPlugin

插件组映射: HUMAN→activity, AGENT→activity-agent, SCENE→activity-scene

### 6.5 AI对话管理器 (Chat.js)

本地命令: 创建流程, 删除流程, 导出YAML, 帮助

### 6.6 API客户端 (Api.js)

baseUrl: /api/bpm, timeout: 30000ms

---

## 第七章 LLM集成规格

### 7.1 LLM服务配置

```yaml
llm:
  enabled: true
  provider: aliyun
  model: qwen-plus
  prompt-file: prompts/full-derivation.yaml
  api-endpoint: https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
  temperature: 0.7
  max-tokens: 4096
  timeout: 60000
  max-retries: 3
```

### 7.2 LLM服务接口

```java
public interface LLMService {
    LLMResponse chat(String prompt);
    LLMResponse chat(String systemPrompt, String userPrompt);
    LLMResponse chatWithFunctions(String prompt, List<Map<String, Object>> functions);
    LLMResponse chatWithFunctions(String systemPrompt, String userPrompt, List<Map<String, Object>> functions);
    LLMResponse chatWithFunctionResult(String originalPrompt, String functionName, Object result);
    boolean isAvailable();
    String getModelName();
}
```

### 7.3 Function Calling已注册函数

- **执行者推导**: get_organization_tree, get_users_by_role, search_users, get_department_leader, list_roles
- **能力匹配**: list_capabilities, search_capabilities, get_capability_detail, match_capability_by_activity
- **表单匹配**: list_forms, search_forms, get_form_schema, match_form_by_activity, generate_form_schema

### 7.4 Prompt模板文件

| 文件 | 用途 |
|------|------|
| performer-derivation.yaml | 执行者推导提示词 |
| capability-matching.yaml | 能力匹配提示词 |
| form-matching.yaml | 表单匹配提示词 |
| full-derivation.yaml | 完整推导提示词(三合一) |

### 7.5 DesignerPromptBuilder构建流程

1. buildRoleSection() - 角色定义(BPM流程设计助手)
2. buildContextSection(context) - 当前上下文(用户/流程/活动/知识参考)
3. buildSchemaSection() - 可用Schema类型
4. buildRulesSection() - 交互规则
5. buildExamplesSection() - 示例交互

---

## 第八章 WebSocket规格

WebSocket路径: /ws/derivation

消息类型: derivation_start, derivation_progress, derivation_complete, derivation_error

消息格式:
```json
{
    "type": "derivation_progress",
    "taskId": "task_123",
    "status": "IN_PROGRESS",
    "step": "performer_derivation",
    "progress": 33,
    "message": "正在推导执行者...",
    "timestamp": "2026-04-13T10:30:00Z"
}
```

---

## 第九章 缓存规格

配置: enabled=true, default-ttl=30

CacheService方法: put(key, value, ttl), get(key), remove(key), clear(), getStats()

CacheStats: hitCount, missCount, size, evictionCount

---

## 第十章 配置规格

### 10.1 应用配置 (application.yml)

```yaml
server:
  port: 8088
spring:
  application:
    name: bpm-designer
bpm:
  server:
    url: http://127.0.0.1:8084/bpm
llm:
  enabled: true
  provider: aliyun
  model: qwen-plus
  temperature: 0.7
  max-tokens: 4096
  timeout: 60000
  max-retries: 3
datasource:
  bpm:
    use-real-data: false
    bpm-server-url: http://127.0.0.1:8084
    capability-service-url: http://127.0.0.1:8085/capability
    form-service-url: http://127.0.0.1:8086/form
    scene-service-url: http://127.0.0.1:8087/scene
cache:
  enabled: true
  default-ttl: 30
management:
  server:
    port: 8089
```

### 10.2 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| LLM_API_KEY | LLM API密钥 | - |
| BPM_SERVER_URL | BPM服务器地址 | http://localhost:8080 |

---

## 第十一章 部署规格

### 11.1 构建命令

```bash
mvn clean compile                    # 编译
mvn clean package -DskipTests        # 打包
java -Dfile.encoding=UTF-8 -jar target/bpm-designer-3.0.2.jar  # 运行
```

### 11.2 依赖服务启动顺序

1. bpmserver (8084) → 2. 能力服务 (8085) → 3. 表单服务 (8086) → 4. 场景服务 (8087) → 5. bpm-designer (8088)

### 11.3 健康检查

```
GET http://localhost:8089/actuator/health
```

---

## 第十二章 Trae Solo Web 兼容性说明

### 12.1 Trae Solo Web 技术特性 (2026年3月31日发布)

| 特性 | 说明 | 本项目兼容性 |
|------|------|-------------|
| SOLO Coder | 专注代码实现的Agent | 适合后端Java代码生成 |
| SOLO Builder | 负责规划、调用MCP工具 | 适合前端代码和配置生成 |
| 上下文压缩 | 长对话不爆Token | 支持大型Spec文档输入 |
| 多任务并行 | 多个任务同时运行 | 可并行生成前后端代码 |
| MCP生态集成 | Figma、飞书等工具 | 可通过MCP调用设计资源 |
| 256K上下文 | 超长上下文支持 | 支持完整Spec一次性输入 |
| Builder模式 | 自然语言生成完整项目 | 核心使用场景 |
| 图像转代码 | Figma/UI截图转代码 | 可用于前端面板生成 |
| Code模式 | Agent主导开发 | 代码生成和调试 |
| More Than Coding | 非代码任务(PRD/报告) | Spec文档生成 |
| 双智能体协作 | SOLO Builder规划 + SOLO Coder执行 | 完整开发闭环 |
| 桌面端+Web端 | 跨平台同步 | 灵活使用 |

### 12.2 本Spec文档的Trae Solo Web测试目标

1. **一文到底测试**: 验证能否一次性读取完整Spec并生成可运行代码
2. **长上下文测试**: 本文档约12000+字，测试256K上下文窗口实际可用性
3. **多文件生成测试**: 验证能否根据Spec同时生成Java后端+JS前端+YAML配置
4. **架构理解测试**: 验证AI能否理解插件化面板、事件驱动等复杂架构模式
5. **DTO转换测试**: 验证AI能否正确实现前后端DTO双向转换逻辑

### 12.3 推荐使用方式

将本spec.md完整内容粘贴到SOLO Builder，指令：
"根据以下完整技术规格文档，从零生成BPM流程设计器项目。要求：1.严格按照Spec中的包结构和类名生成代码 2.前端使用原生JavaScript 3.后端使用Spring Boot 3.4.4 + Java 21 4.所有DTO转换逻辑必须完整实现 5.面板插件系统必须可运行 6.LLM集成必须支持OpenAI兼容API"

---

## 第十三章 文件路径索引

### 13.1 后端关键文件

| 文件 | 绝对路径 |
|------|----------|
| 主应用入口 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\BpmDesignerApplication.java |
| 设计器服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerService.java |
| NLP服务接口 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerNlpService.java |
| 执行者推导服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\PerformerDerivationService.java |
| 能力匹配服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\CapabilityMatchingService.java |
| 表单匹配服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\FormMatchingService.java |
| 面板渲染服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\PanelRenderService.java |
| LLM服务实现 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\llm\LLMServiceImpl.java |
| 设计器控制器 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\controller\DesignerController.java |
| 缓存服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\cache\CacheService.java |
| 提示词构建器 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\prompt\DesignerPromptBuilder.java |
| 应用配置 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\application.yml |
| POM配置 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\pom.xml |

### 13.2 前端关键文件

| 文件 | 绝对路径 |
|------|----------|
| 前端入口HTML | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\index.html |
| 应用入口JS | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\App.js |
| 画布引擎 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\Canvas.js |
| AI对话 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\Chat.js |
| 面板管理器 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\panel\PanelManager.js |
| 流程模型 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\model\ProcessDef.js |
| 活动模型 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\model\ActivityDef.js |
| 状态管理 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\sdk\Store.js |
| API客户端 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\sdk\Api.js |

---

*文档生成时间: 2026-04-13*  
*文档版本: 3.0.2*  
*测试平台: Trae Solo Web (字节跳动)*  
*文档目的: AI编程平台长任务能力极限测试 - 一文到底*
