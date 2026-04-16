# BPM流程设计器 (bpm-designer) - 完整技术规格文档

> **文档版本**: 3.1.0  
> **生成日期**: 2026-04-13  
> **项目仓库**: gitee.com/ooderCN/ade  
> **测试目标**: Trae Solo Web 长任务一文到底能力极限测试  
> **文档定位**: 面向 AI 编程平台的完整可执行规格，支持从零重建整个系统
> **补充说明**: 基于 ape-spec-supplement.md 建议书完善所有P0/P1级缺失项

---

## 第一章 项目概述

### 1.1 项目信息

| 属性 | 值 |
|------|------|
| 项目名称 | BPM流程设计器 (bpm-designer) |
| 项目标识 | net.ooder.bpm.designer |
| 版本号 | 3.1.0 |
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
│  │  │  DesignerController │ DesignerNlpController │ DerivationController│
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │                    Service 层                                │  │    │
│  │  │  DesignerService │ DesignerNlpService │ PerformerDerivationSvc│  │    │
│  │  │  CapabilityMatch │ FormMatchingService │ PanelRenderService   │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │                  LLM集成层                                  │  │    │
│  │  │  LLMServiceImpl │ PromptTemplateManager │ DesignerFunctionRegistry│
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │                  基础设施层                                 │  │    │
│  │  │  CacheService │ DerivationWebSocketHandler │ DataSourceAdapter│
│  │  └──────────────────────────────────────────────────────────────┘  │    │
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
├── dto/derivation/ (PerformerDerivationResultDTO, CapabilityMatchingResultDTO, FormMatchingResultDTO, PanelRenderDataDTO, FormSchema, PerformerCandidate)
├── dto/nlp/       (NlpResponse, NlpSuggestion, NlpIntent)
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
│   ├── model/     (ActivityDef.js, ProcessDef.js, RouteDef.js, AgentDef.js, SceneDef.js)
│   ├── panel/     (PanelInitializer.js, PanelManager.js, PanelPlugin.js, PluginEnvironment.js)
│   ├── sdk/       (Api.js, DataAdapter.js, EnumMapping.js, Store.js, Theme.js)
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
| 流程结构 | startNode | Map<String,Object> | 开始节点配置 |
| | endNodes | List<Map<String,Object>> | 结束节点列表(支持多个) |
| | activities | List<ActivityDef> | 活动列表 |
| | routes | List<RouteDef> | 路由列表 |
| 监听器和权限 | listeners | List<Map<String,Object>> | 监听器列表 |
| | rightGroups | List<Map<String,Object>> | 权限组列表 |
| 扩展属性 | mark | String | 标记 |
| | lock | String | 锁定状态 |
| | autoSave | Boolean | 自动保存 |
| | noSqlType | String | NoSQL类型 |
| | tableNames | List<String> | 关联表名 |
| | moduleNames | List<String> | 关联模块名 |
| | formulas | List<Map<String,Object>> | 公式列表 |
| | parameters | List<Map<String,Object>> | 参数列表 |
| | extendedAttributes | Map<String,Object> | 扩展属性 |
| | agentConfig | Map<String,Object> | Agent配置 |
| | sceneConfig | Map<String,Object> | 场景配置 |

#### 4.1.2 开始节点 (StartNode) 结构

```json
{
    "participantId": "Participant_Start",
    "firstActivityId": "act_submit",
    "positionCoord": { "x": 50, "y": 200 },
    "routing": "NO_ROUTING"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| participantId | String | 是 | 参与者ID，XPDL格式 |
| firstActivityId | String | 是 | 第一个活动的ID |
| positionCoord | Object | 是 | 坐标位置 {x, y} |
| routing | String | 否 | 路由方式，默认NO_ROUTING |

#### 4.1.3 结束节点 (EndNode) 结构

```json
{
    "participantId": "Participant_End",
    "lastActivityId": "act_end",
    "positionCoord": { "x": 800, "y": 200 },
    "routing": "NO_ROUTING"
}
```

支持多个结束节点，以数组形式存储。

---

### 4.2 活动定义 (ActivityDef) 【P0-01 已补充】

#### 4.2.1 ActivityDef 完整字段定义表

| 字段组 | 字段 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| **基本属性** | activityDefId | String | 是 | UUID | 活动定义ID，唯一标识 |
| | name | String | 是 | "新活动" | 活动名称 |
| | description | String | 否 | "" | 活动描述 |
| **类型属性** | position | ActivityPosition | 是 | NORMAL | 位置枚举(START/END/NORMAL) |
| | activityType | ActivityType | 是 | TASK | 类型枚举(TASK/SERVICE/SCRIPT等) |
| | activityCategory | ActivityCategory | 是 | HUMAN | 分类枚举(HUMAN/AGENT/SCENE) |
| **坐标和参与者** | positionCoord | PositionCoordDTO | 是 | {x:0,y:0} | 坐标对象{x(Double), y(Double)} |
| | participantId | String | 否 | "" | 参与者泳道ID |
| **实现方式** | implementation | String | 否 | "No" | 实现方式(No/Tool/Subflow等) |
| | execClass | String | 否 | "" | 执行类名 |
| **子配置** | timing | TimingDTO | 否 | null | 时限配置对象 |
| | routing | RoutingDTO | 否 | null | 路由配置对象 |
| | right | RightDTO | 否 | null | 权限配置对象 |
| | subFlow | SubFlowDTO | 否 | null | 子流程配置对象 |
| | device | DeviceDTO | 否 | null | 设备配置对象 |
| | service | ServiceDTO | 否 | null | 服务配置对象 |
| | event | EventDTO | 否 | null | 事件配置对象 |
| | agentConfig | AgentConfigDTO | 否 | null | Agent配置对象 |
| | sceneConfig | SceneConfigDTO | 否 | null | 场景配置对象 |
| **表单属性** | formId | String | 否 | "" | 表单ID |
| | formName | String | 否 | "" | 表单名称 |
| | formType | String | 否 | "CUSTOM" | 表单类型(CUSTOM/SYSTEM/EXTERNAL) |
| | formUrl | String | 否 | "" | 表单URL |
| **块活动属性** | startOfBlock | String | 否 | null | 块活动起始ID |
| | endOfBlock | String | 否 | null | 块活动结束ID |
| | participantVisualOrder | String | 否 | "" | 参与者视觉顺序 |
| **扩展属性** | extendedAttributes | Map<String,Object> | 否 | {} | 扩展属性 |

#### 4.2.2 活动类型枚举 (ActivityType) 【P1-01 已补充】

```java
public enum ActivityType {
    TASK("TASK", "用户任务"),
    SERVICE("SERVICE", "服务任务"),
    SCRIPT("SCRIPT", "脚本任务"),
    START("START", "开始节点"),
    END("END", "结束节点"),
    XOR_GATEWAY("XOR_GATEWAY", "排他网关"),
    AND_GATEWAY("AND_GATEWAY", "并行网关"),
    OR_GATEWAY("OR_GATEWAY", "包容网关"),
    SUBPROCESS("SUBPROCESS", "子流程"),
    LLM_TASK("LLM_TASK", "LLM任务");

    private final String code;
    private final String label;

    ActivityType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ActivityType fromCode(String code) {
        for (ActivityType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return TASK;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
```

#### 4.2.3 活动分类枚举 (ActivityCategory) 【P1-01 已补充】

```java
public enum ActivityCategory {
    HUMAN("HUMAN", "人工活动"),
    AGENT("AGENT", "Agent活动"),
    SCENE("SCENE", "场景活动");

    private final String code;
    private final String label;

    ActivityCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ActivityCategory fromCode(String code) {
        for (ActivityCategory cat : values()) {
            if (cat.code.equals(code)) return cat;
        }
        return HUMAN;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
```

#### 4.2.4 活动位置枚举 (ActivityPosition) 【P1-01 已补充】

```java
public enum ActivityPosition {
    START("START", "开始位置"),
    END("END", "结束位置"),
    NORMAL("NORMAL", "普通位置");

    private final String code;
    private final String label;

    ActivityPosition(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ActivityPosition fromCode(String code) {
        for (ActivityPosition pos : values()) {
            if (pos.code.equals(code)) return pos;
        }
        return NORMAL;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
```

#### 4.2.5 实现方式枚举 (Implementation)

| 代码 | 说明 |
|------|------|
| No | 无实现 - 手动活动 |
| Tool | 工具实现 - 调用工具 |
| Subflow | 子流程实现 - 调用子流程 |
| Outflow | 外部流程实现 - 调用外部流程 |
| Device | 设备实现 - IoT设备交互 |
| Event | 事件实现 - 事件驱动 |
| Service | 服务实现 - 服务调用 |

#### 4.2.6 RIGHT属性组

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

#### 4.2.7 FORM属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| formId | String | 表单ID |
| formName | String | 表单名称 |
| formType | String | 表单类型(CUSTOM/SYSTEM/EXTERNAL) |
| formUrl | String | 表单URL |

#### 4.2.8 SERVICE属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| httpMethod | String | HTTP方法(GET/POST/PUT/DELETE) |
| httpUrl | String | 服务URL |
| httpRequestType | String | 请求类型 |
| httpResponseType | String | 响应类型 |
| httpServiceParams | String | 服务参数(JSON) |
| serviceSelectedId | String | 服务选择ID |

#### 4.2.9 WORKFLOW属性组

| 字段 | 类型 | 说明 |
|------|------|------|
| deadLineOperation | String | 超时操作(ALERT/AUTO_COMPLETE/ESCALATE) |
| specialScope | String | 特送范围(ALL/DEPT/ROLE) |

---

### 4.3 路由定义 (RouteDef)

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| routeDefId | String | 是 | 路由定义ID |
| name | String | 否 | 路由名称 |
| description | String | 否 | 路由描述 |
| from | String | 是 | 起始活动ID |
| to | String | 是 | 目标活动ID |
| routeOrder | Integer | 否 | 路由顺序，默认0 |
| routeDirection | String | 否 | 路由方向(FORWARD/BACK)，默认FORWARD |
| routeConditionType | String | 否 | 条件类型(DEFAULT/CONDITION/OTHERWISE)，默认DEFAULT |
| condition | String | 否 | 条件表达式 |
| extendedAttributes | Map<String,Object> | 否 | 扩展属性 |

---

### 4.4 子配置DTO规格

#### 4.4.1 TimingDTO (时限配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| limitTime | Integer | 否 | 0 | 时限值 |
| alertTime | Integer | 否 | 0 | 预警时间 |
| durationUnit | String | 否 | "D" | 时限单位(D/H/M) |
| startTime | String | 否 | null | 开始时间 |
| endTime | String | 否 | null | 结束时间 |
| remindType | String | 否 | "NONE" | 提醒类型 |
| remindInterval | Integer | 否 | 0 | 提醒间隔(分钟) |

#### 4.4.2 RoutingDTO (路由配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| join | String | 否 | "XOR" | 聚合类型(XOR/AND/OR) |
| split | String | 否 | "XOR" | 分裂类型(XOR/AND/OR) |
| canRouteBack | String | 否 | "NO" | 可否退回(YES/NO) |
| routeBackMethod | String | 否 | "PREV" | 退回方式(PREV/FIRST/ANY) |
| canSpecialSend | String | 否 | "NO" | 可否特送(YES/NO) |
| specialScope | String | 否 | "ALL" | 特送范围(ALL/DEPT/ROLE) |
| defaultRoute | String | 否 | null | 默认路由ID |
| parallelMode | String | 否 | "SEQUENTIAL" | 并行模式 |
| mergeCondition | String | 否 | null | 合并条件 |

#### 4.4.3 RightDTO (权限配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| moveSponsorTo | String | 否 | "CURRENT" | 发起人移动方向 |
| performer | String | 否 | null | 办理人 |
| performerType | String | 否 | "USER" | 办理人类型(USER/ROLE/DEPT/EXPRESSION) |
| participationType | String | 否 | "SINGLE" | 参与类型 |
| participationScope | String | 否 | null | 参与范围 |
| participationScopeValue | String | 否 | null | 参与范围值 |
| candidateUsers | List<String> | 否 | [] | 候选用户列表 |
| candidateGroups | List<String> | 否 | [] | 候选组列表 |
| candidateRoles | List<String> | 否 | [] | 候选角色列表 |
| assignee | String | 否 | null | 指派人 |
| owner | String | 否 | null | 拥有者 |
| reassignable | Boolean | 否 | true | 可否转办 |
| delegatable | Boolean | 否 | true | 可否委托 |
| transferable | Boolean | 否 | true | 可否转交 |

#### 4.4.4 SubFlowDTO (子流程配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| subProcessDefId | String | 是 | - | 子流程定义ID |
| subProcessName | String | 否 | null | 子流程名称 |
| subProcessVersion | String | 否 | "latest" | 子流程版本 |
| executionMode | String | 否 | "SYNC" | 执行模式(SYNC/ASYNC) |
| waitForCompletion | Boolean | 否 | true | 是否等待完成 |
| dataMapping | Map<String,Object> | 否 | {} | 数据映射 |
| parameterMapping | Map<String,Object> | 否 | {} | 参数映射 |

#### 4.4.5 DeviceDTO (设备配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| deviceId | String | 是 | - | 设备ID |
| deviceName | String | 否 | null | 设备名称 |
| deviceType | String | 否 | null | 设备类型 |
| deviceModel | String | 否 | null | 设备型号 |
| connectionString | String | 否 | null | 连接字符串 |
| protocol | String | 否 | "HTTP" | 通信协议 |
| parameters | Map<String,Object> | 否 | {} | 设备参数 |

#### 4.4.6 ServiceDTO (服务配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| serviceId | String | 是 | - | 服务ID |
| serviceName | String | 否 | null | 服务名称 |
| serviceType | String | 否 | "REST" | 服务类型 |
| serviceUrl | String | 是 | - | 服务URL |
| serviceMethod | String | 否 | "POST" | 服务方法 |
| serviceProtocol | String | 否 | "HTTP" | 服务协议 |
| inputParameters | Map<String,Object> | 否 | {} | 输入参数 |
| outputParameters | Map<String,Object> | 否 | {} | 输出参数 |
| headers | Map<String,String> | 否 | {} | 请求头 |
| timeout | Integer | 否 | 30000 | 超时时间(ms) |
| retryCount | Integer | 否 | 0 | 重试次数 |

#### 4.4.7 EventDTO (事件配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| eventId | String | 是 | - | 事件ID |
| eventName | String | 否 | null | 事件名称 |
| eventType | String | 否 | "MESSAGE" | 事件类型 |
| eventTrigger | String | 否 | null | 事件触发器 |
| triggerCondition | String | 否 | null | 触发条件 |
| eventAction | String | 否 | null | 事件动作 |
| actionParameters | Map<String,Object> | 否 | {} | 动作参数 |
| listenerClass | String | 否 | null | 监听器类 |
| listenerExpression | String | 否 | null | 监听器表达式 |

#### 4.4.8 AgentConfigDTO (Agent配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| agentId | String | 是 | - | Agent ID |
| agentName | String | 否 | null | Agent名称 |
| agentType | String | 否 | "LLM" | Agent类型 |
| modelName | String | 是 | - | LLM模型名称 |
| systemPrompt | String | 否 | null | 系统提示词 |
| temperature | Double | 否 | 0.7 | 温度参数 |
| maxTokens | Integer | 否 | 4096 | 最大Token数 |
| tools | List<String> | 否 | [] | 工具列表 |
| capabilities | List<String> | 否 | [] | 能力列表 |
| inputSchema | Map<String,Object> | 否 | {} | 输入Schema |
| outputSchema | Map<String,Object> | 否 | {} | 输出Schema |
| memoryEnabled | Boolean | 否 | false | 是否启用记忆 |
| contextWindow | Integer | 否 | 10 | 上下文窗口大小 |

#### 4.4.9 SceneConfigDTO (场景配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| sceneId | String | 是 | - | 场景ID |
| sceneName | String | 否 | null | 场景名称 |
| sceneType | String | 否 | "BUSINESS" | 场景类型 |
| sceneCategory | String | 否 | null | 场景分类 |
| triggerEvents | List<String> | 否 | [] | 触发事件列表 |
| entryConditions | List<String> | 否 | [] | 入口条件列表 |
| exitConditions | List<String> | 否 | [] | 退出条件列表 |
| sceneData | Map<String,Object> | 否 | {} | 场景数据 |
| sceneRules | List<Map<String,Object>> | 否 | [] | 场景规则列表 |
| sceneState | String | 否 | "ACTIVE" | 场景状态 |
| priority | Integer | 否 | 0 | 优先级 |
| validityPeriod | Map<String,String> | 否 | {} | 有效期 |

---

### 4.5 推导服务响应DTO 【P0-02 已补充】

#### 4.5.1 PerformerDerivationResultDTO (执行者推导结果)

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 推导是否成功 |
| status | String | 推导状态(SUCCESS/PARTIAL/FAILED) |
| recommendedPerformers | List<PerformerCandidate> | 推荐执行者列表 |
| confidence | Double | 置信度(0.0-1.0) |
| derivationReason | String | 推导依据/推理过程 |
| derivationMethod | String | 推导方法(KEYWORD/RULE/LLM/HYBRID) |
| candidates | List<PerformerCandidate> | 所有候选人列表 |
| fallbackSuggestion | String | 备选建议 |
| errorMessage | String | 错误信息(失败时) |

#### 4.5.2 PerformerCandidate (执行者候选人)

| 字段 | 类型 | 说明 |
|------|------|------|
| candidateId | String | 候选人ID |
| candidateName | String | 候选人名称 |
| candidateType | String | 候选人类型(USER/ROLE/DEPT/GROUP) |
| department | String | 所属部门 |
| role | String | 所属角色 |
| matchScore | Double | 匹配分数(0.0-1.0) |
| matchReason | String | 匹配原因 |
| metadata | Map<String,Object> | 扩展元数据 |

#### 4.5.3 CapabilityMatchingResultDTO (能力匹配结果)

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 匹配是否成功 |
| status | String | 匹配状态(SUCCESS/PARTIAL/FAILED) |
| recommendedCapabilities | List<CapabilityItem> | 推荐能力列表 |
| matchScores | Map<String,Double> | 各能力的匹配度评分 |
| bindingConfig | Map<String,Object> | 绑定配置建议 |
| derivationReason | String | 推导依据 |
| errorMessage | String | 错误信息 |

#### 4.5.4 CapabilityItem (能力项)

| 字段 | 类型 | 说明 |
|------|------|------|
| capabilityId | String | 能力ID |
| capabilityName | String | 能力名称 |
| capabilityType | String | 能力类型 |
| description | String | 能力描述 |
| matchScore | Double | 匹配分数 |
| isRecommended | Boolean | 是否推荐 |

#### 4.5.5 FormMatchingResultDTO (表单匹配结果)

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 匹配是否成功 |
| status | String | 匹配状态(SUCCESS/PARTIAL/FAILED/GENERATED) |
| recommendedForms | List<FormInfo> | 推荐表单列表 |
| generatedSchema | FormSchema | 生成的表单Schema(如果需要生成) |
| fieldMappings | Map<String,String> | 字段映射关系 |
| matchScores | Map<String,Double> | 各表单的匹配度评分 |
| derivationReason | String | 推导依据 |
| errorMessage | String | 错误信息 |

#### 4.5.6 FormInfo (表单信息)

| 字段 | 类型 | 说明 |
|------|------|------|
| formId | String | 表单ID |
| formName | String | 表单名称 |
| formType | String | 表单类型 |
| formUrl | String | 表单URL |
| matchScore | Double | 匹配分数 |
| isRecommended | Boolean | 是否推荐 |

#### 4.5.7 FormSchema (表单Schema)

| 字段 | 类型 | 说明 |
|------|------|------|
| schemaId | String | Schema ID |
| schemaName | String | Schema名称 |
| fields | List<FormField> | 表单字段定义列表 |
| layout | FormLayout | 布局信息 |
| validationRules | Map<String,Object> | 验证规则 |

#### 4.5.8 FormField (表单字段)

| 字段 | 类型 | 说明 |
|------|------|------|
| fieldId | String | 字段ID |
| fieldName | String | 字段名称 |
| fieldType | String | 字段类型(TEXT/NUMBER/DATE/SELECT/MULTI_SELECT/TEXTAREA/FILE/USER/DEPT) |
| label | String | 显示标签 |
| placeholder | String | 占位文本 |
| required | Boolean | 是否必填 |
| defaultValue | Object | 默认值 |
| options | List<Map<String,Object>> | 选项列表(用于SELECT等) |
| validation | Map<String,Object> | 验证规则 |
| visible | Boolean | 是否可见 |
| editable | Boolean | 是否可编辑 |

#### 4.5.9 FormLayout (表单布局)

| 字段 | 类型 | 说明 |
|------|------|------|
| columns | Integer | 列数 |
| labelWidth | Integer | 标签宽度(px) |
| labelPosition | String | 标签位置(LEFT/TOP/RIGHT) |
| groups | List<FormGroup> | 字段分组 |

#### 4.5.10 PanelRenderDataDTO (面板渲染数据)

| 字段 | 类型 | 说明 |
|------|------|------|
| panelType | String | 面板类型(PERFORMER/CAPABILITY/FORM/ACTIVITY) |
| title | String | 面板标题 |
| schema | Map<String,Object> | Schema定义(JSON Schema格式) |
| defaultValue | Map<String,Object> | 默认值 |
| options | Map<String,List<OptionItem>> | 可选项列表 |
| validationRules | Map<String,Object> | 验证规则 |
| sections | List<PanelSection> | 面板分区 |
| actions | List<PanelAction> | 可用操作按钮 |

#### 4.5.11 PanelSection (面板分区)

| 字段 | 类型 | 说明 |
|------|------|------|
| sectionId | String | 分区ID |
| sectionName | String | 分区名称 |
| collapsed | Boolean | 是否折叠 |
| fields | List<String> | 包含的字段ID列表 |

#### 4.5.12 PanelAction (面板操作)

| 字段 | 类型 | 说明 |
|------|------|------|
| actionId | String | 操作ID |
| actionName | String | 操作名称 |
| actionType | String | 操作类型(BUTTON/LINK/MENU) |
| icon | String | 图标类名 |
| handler | String | 处理函数名 |

---

### 4.6 NLP服务内部类 【P0-03 已补充】

#### 4.6.1 NlpResponse (NLP统一响应)

| 字段 | 类型 | 说明 |
|------|------|------|
| intent | String | 识别的意图(create_process/add_activity/update_attribute等) |
| confidence | Double | 意图识别置信度(0.0-1.0) |
| entities | Map<String,Object> | 提取的实体(如流程名称、活动类型等) |
| action | String | 建议执行的动作 |
| actionParams | Map<String,Object> | 动作参数 |
| message | String | 响应消息 |
| suggestions | List<NlpSuggestion> | 建议列表 |
| success | Boolean | 处理是否成功 |
| errorMessage | String | 错误信息 |

#### 4.6.2 NlpSuggestion (NLP建议项)

| 字段 | 类型 | 说明 |
|------|------|------|
| type | String | 建议类型(ACTION/INFO/QUESTION) |
| title | String | 建议标题 |
| description | String | 建议描述 |
| action | String | 可执行动作 |
| params | Map<String,Object> | 动作参数 |

#### 4.6.3 NlpIntent (NLP意图)

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 意图名称(create_process/add_activity/update_attribute/delete_activity等) |
| confidence | Double | 置信度(0.0-1.0) |
| category | String | 意图分类(process_management/activity_management/attribute_management/query) |
| slots | Map<String,Object> | 提取的槽位值 |

---

### 4.7 统一API响应格式

```java
public class ApiResponse<T> {
    private int code;          // 状态码(200=成功, 404=未找到, 500=错误)
    private String message;    // 消息
    private T data;            // 数据
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
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

### 5.4 DesignerNlpService 接口定义 【P1-02 已补充】

```java
public interface DesignerNlpService {
    
    /**
     * 处理用户对话消息
     * @param message 用户消息
     * @param context 上下文信息(包含processId, activityId, mode等)
     * @return NLP响应
     */
    NlpResponse chat(String message, Map<String, Object> context);
    
    /**
     * 从自然语言描述创建流程
     * @param description 流程描述
     * @param context 上下文信息
     * @return 创建的流程DTO
     */
    ProcessDTO createProcessFromNlp(String description, Map<String, Object> context);
    
    /**
     * 从自然语言描述创建活动
     * @param description 活动描述
     * @param context 上下文信息
     * @return 创建的活动DTO
     */
    ActivityDTO createActivityFromNlp(String description, Map<String, Object> context);
    
    /**
     * 从自然语言更新属性
     * @param attributeName 属性名
     * @param value 属性值
     * @param context 上下文信息
     * @return 更新后的属性Map
     */
    Map<String, Object> updateAttributeFromNlp(String attributeName, Object value, Map<String, Object> context);
    
    /**
     * 获取智能建议
     * @param context 上下文信息
     * @return 建议列表
     */
    List<NlpSuggestion> getSuggestions(Map<String, Object> context);
    
    /**
     * 验证流程定义
     * @param processDef 流程定义
     * @param context 上下文信息
     * @return 验证结果描述
     */
    String validateProcess(ProcessDef processDef, Map<String, Object> context);
    
    /**
     * 生成流程的自然语言描述
     * @param processDef 流程定义
     * @return 描述文本
     */
    String describeProcess(ProcessDef processDef);
    
    /**
     * 生成活动的自然语言描述
     * @param activityDef 活动定义
     * @return 描述文本
     */
    String describeActivity(ActivityDef activityDef);
    
    /**
     * 分析用户输入的意图
     * @param userInput 用户输入
     * @return 意图列表(按置信度排序)
     */
    List<NlpIntent> analyzeIntent(String userInput);
    
    /**
     * 从用户输入提取实体
     * @param userInput 用户输入
     * @param intentType 意图类型
     * @return 实体Map
     */
    Map<String, Object> extractEntities(String userInput, String intentType);
}
```

---

## 第六章 前端架构规格

### 6.1 应用入口 (App.js)

**初始化流程**:
1. `_initIcons()` - 初始化RemixIcon图标映射
2. `ThemeFactory.get()` - 获取主题实例
3. `new Store()` - 创建状态管理器
4. `ApiFactory.create()` - 创建API客户端
5. `_initSidebar()` - 初始化侧边栏(Tree + Elements)
6. `_initCanvas()` - 初始化SVG画布
7. `_initPanel()` - 初始化属性面板
8. `_initChat()` - 初始化AI对话
9. `_initToolbar()` - 初始化工具栏
10. `_initTabManager()` - 初始化标签页管理
11. `_bindGlobalEvents()` - 绑定全局事件

**全局快捷键**:
| 快捷键 | 功能 |
|--------|------|
| Ctrl+S | 保存流程 |
| Ctrl+Z | 撤销 |
| Ctrl+Y | 重做 |
| Delete | 删除选中元素 |
| Ctrl+A | 全选 |
| Ctrl+C | 复制(开发中) |
| Ctrl+V | 粘贴(开发中) |

---

### 6.2 画布引擎 (Canvas.js) 【P1-05 已补充】

#### 6.2.1 核心属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| container | Element | - | SVG容器DOM |
| store | Store | - | 状态管理器 |
| nodes | Map | new Map() | 节点映射(activityDefId -> nodeData) |
| edges | Map | new Map() | 边映射(routeDefId -> edgeData) |
| scale | Number | 1 | 缩放比例 |
| offsetX | Number | 0 | X轴平移偏移 |
| offsetY | Number | 0 | Y轴平移偏移 |
| isDragging | Boolean | false | 是否正在拖拽节点 |
| isDrawingEdge | Boolean | false | 是否正在绘制连线 |
| selectedNodes | Set | new Set() | 选中的节点集合 |
| nodeWidth | Number | 120 | 节点宽度(px) |
| nodeHeight | Number | 60 | 节点高度(px) |
| smallNodeSize | Number | 50 | 小节点尺寸(px) |
| gridSize | Number | 20 | 网格大小(px) |
| minScale | Number | 0.25 | 最小缩放比例 |
| maxScale | Number | 2.0 | 最大缩放比例 |

#### 6.2.2 节点渲染规则

| 节点类型 | 形状 | 颜色 | 尺寸 |
|----------|------|------|------|
| START | 圆形 | #4CAF50(绿) | 50px直径 |
| END | 圆形 | #F44336(红) | 50px直径 |
| TASK | 圆角矩形 | #2196F3(蓝) | 120x60px |
| SERVICE | 圆角矩形 | #FF9800(橙) | 120x60px |
| SCRIPT | 圆角矩形 | #9C27B0(紫) | 120x60px |
| XOR_GATEWAY | 菱形 | #FFC107(黄) | 50x50px |
| AND_GATEWAY | 菱形 | #FFC107(黄) | 50x50px |
| OR_GATEWAY | 菱形 | #FFC107(黄) | 50x50px |
| SUBPROCESS | 圆角矩形(双线框) | #673AB7(深紫) | 140x70px |
| LLM_TASK | 圆角矩形(渐变) | #E91E63→#9C27B0 | 120x60px |

#### 6.2.3 交互规格

**拖拽移动节点**:
- 触发条件: 鼠标左键按下并移动
- 拖拽过程: 节点跟随鼠标移动，显示虚线预览位置
- 释放行为: 节点吸附到最近网格点，更新positionCoord
- 边界约束: 不允许拖出画布可视区域(可配置)

**选择/多选**:
- 单击选择: 点击节点，高亮显示，触发activity:select事件
- Ctrl+点击多选: 按住Ctrl点击多个节点，加入selectedNodes
- 框选: 从空白区域拖拽矩形，选中矩形内所有节点
- 全选: Ctrl+A选中所有节点

**连线绘制**:
- 起始锚点: 节点边缘中点，鼠标悬停显示连接点
- 路径计算: 使用贝塞尔曲线，自动避开节点
- 终点吸附: 拖拽到目标节点边缘时自动吸附
- 条件设置: 连线完成后弹出条件编辑对话框

**缩放**:
- 缩放方式: 鼠标滚轮向上放大，向下缩小
- 缩放中心: 鼠标位置
- 缩放范围: 0.25x ~ 2.0x
- 快捷键: Ctrl++ 放大, Ctrl+- 缩小, Ctrl+0 适应屏幕

**平移**:
- 触发方式: 按住Space+拖拽 或 中键拖拽 或 拖拽空白区域
- 边界约束: 允许平移到画布边界外一定距离

**右键菜单**:
- 空白区域: 粘贴、全选、适应屏幕
- 节点上: 编辑、复制、删除、查看属性
- 连线上: 编辑条件、删除

**删除**:
- Delete键删除选中元素
- 连带删除: 删除节点时同时删除相关连线
- 确认机制: 可配置是否需要确认对话框

---

### 6.3 状态管理器 (Store.js)

#### 6.3.1 核心属性

| 属性 | 类型 | 说明 |
|------|------|------|
| process | ProcessDef | 当前流程定义 |
| currentActivity | ActivityDef | 当前选中的活动 |
| currentRoute | RouteDef | 当前选中的路由 |
| selectedNodes | Array | 多选节点列表 |
| clipboard | Object | 剪贴板内容 |
| dirty | Boolean | 是否有未保存修改 |
| history | Array | 撤销历史栈 |
| historyIndex | Number | 当前历史位置 |
| maxHistory | Number | 最大历史记录数(50) |
| listeners | Map | 事件监听器映射 |
| api | Api | API客户端 |
| _autoSaveTimer | Number | 自动保存定时器ID |
| _autoSaveDelay | Number | 自动保存延迟(1000ms) |

#### 6.3.2 核心方法

| 方法 | 参数 | 返回值 | 说明 |
|------|------|--------|------|
| setProcess | ProcessDef | void | 设置当前流程 |
| getProcess | - | ProcessDef | 获取当前流程 |
| selectActivity | activityId | void | 选中活动 |
| updateActivity | ActivityDef | void | 更新活动(触发自动保存) |
| addActivity | ActivityDef | void | 添加活动 |
| removeActivity | activityId | void | 移除活动 |
| selectRoute | routeId | void | 选中路由 |
| updateRoute | RouteDef | void | 更新路由 |
| addRoute | RouteDef | void | 添加路由 |
| removeRoute | routeId | void | 移除路由 |
| setDirty | Boolean | void | 设置脏标记 |
| undo | - | void | 撤销 |
| redo | - | void | 重做 |
| on | event, callback | void | 注册事件监听 |
| off | event, callback | void | 移除事件监听 |

#### 6.3.3 事件列表

| 事件名 | 数据 | 说明 |
|--------|------|------|
| process:change | ProcessDef | 流程变更 |
| activity:select | ActivityDef | 活动选中 |
| activity:update | ActivityDef | 活动更新 |
| activity:add | ActivityDef | 活动添加 |
| activity:remove | activityId | 活动移除 |
| route:update | RouteDef | 路由更新 |
| route:add | RouteDef | 路由添加 |
| route:remove | routeId | 路由移除 |
| dirty:change | Boolean | 脏标记变更 |
| save:success | ProcessDef | 保存成功 |
| save:error | Error | 保存失败 |

---

### 6.4 前端数据模型 【P0-04 已补充】

#### 6.4.1 ProcessDef.js (前端流程定义模型)

```javascript
class ProcessDef {
    constructor(data) {
        // 基本属性
        this.processDefId = data?.processDefId || this._generateId();
        this.name = data?.name || '新流程';
        this.description = data?.description || '';
        this.category = data?.category || data?.classification || '办公流程';
        this.classification = this.category;
        this.systemCode = data?.systemCode || 'bpm';
        this.accessLevel = data?.accessLevel || 'PUBLIC';
        
        // 版本信息
        this.version = data?.version || 1;
        this.status = data?.status || data?.state || 'DRAFT';
        this.state = this.status;
        this.creatorName = data?.creatorName || '';
        this.modifierName = data?.modifierName || '';
        this.modifyTime = data?.modifyTime || data?.updatedTime || null;
        this.createTime = data?.createTime || data?.createdTime || null;
        
        // 时限配置
        this.limit = data?.limit || 0;
        this.durationUnit = data?.durationUnit || 'D';
        
        // 开始/结束节点
        this.startNode = this._normalizeStartNode(data?.startNode, data?.activities);
        this.endNodes = this._normalizeEndNodes(data?.endNodes, data?.activities);
        
        // 监听器和权限组
        this.listeners = data?.listeners || [];
        this.rightGroups = data?.rightGroups || [];
        
        // 活动和路由
        this.activities = (data?.activities || []).map(a => 
            a instanceof ActivityDef ? a : new ActivityDef(a)
        );
        this.routes = data?.routes || [];
        
        // 扩展属性
        this.extendedAttributes = data?.extendedAttributes || {};
    }
    
    // === 核心方法 ===
    
    /**
     * 添加活动
     * @param {ActivityDef} activity 活动定义
     */
    addActivity(activity) {
        if (!(activity instanceof ActivityDef)) {
            activity = new ActivityDef(activity);
        }
        this.activities.push(activity);
        return activity;
    }
    
    /**
     * 移除活动
     * @param {String} activityDefId 活动ID
     */
    removeActivity(activityDefId) {
        const index = this.activities.findIndex(a => a.activityDefId === activityDefId);
        if (index > -1) {
            this.activities.splice(index, 1);
            // 同时移除相关路由
            this.routes = this.routes.filter(r => 
                r.from !== activityDefId && r.to !== activityDefId
            );
            return true;
        }
        return false;
    }
    
    /**
     * 获取活动
     * @param {String} activityDefId 活动ID
     * @returns {ActivityDef|null}
     */
    getActivity(activityDefId) {
        return this.activities.find(a => a.activityDefId === activityDefId) || null;
    }
    
    /**
     * 获取开始活动
     * @returns {ActivityDef|null}
     */
    getStartActivity() {
        return this.activities.find(a => a.position === 'START' || a.activityType === 'START') || null;
    }
    
    /**
     * 获取结束活动列表
     * @returns {ActivityDef[]}
     */
    getEndActivities() {
        return this.activities.filter(a => a.position === 'END' || a.activityType === 'END');
    }
    
    /**
     * 添加路由
     * @param {Object} route 路由定义
     */
    addRoute(route) {
        route.routeDefId = route.routeDefId || route.id || this._generateId();
        this.routes.push(route);
        return route;
    }
    
    /**
     * 移除路由
     * @param {String} routeDefId 路由ID
     */
    removeRoute(routeDefId) {
        const index = this.routes.findIndex(r => r.routeDefId === routeDefId || r.id === routeDefId);
        if (index > -1) {
            this.routes.splice(index, 1);
            return true;
        }
        return false;
    }
    
    /**
     * 验证流程定义
     * @returns {Object} {valid: Boolean, errors: String[]}
     */
    validate() {
        const errors = [];
        
        if (!this.name || this.name.trim() === '') {
            errors.push('流程名称不能为空');
        }
        
        if (this.activities.length === 0) {
            errors.push('流程必须包含至少一个活动');
        }
        
        const startActivity = this.getStartActivity();
        if (!startActivity) {
            errors.push('流程必须包含一个开始节点');
        }
        
        const endActivities = this.getEndActivities();
        if (endActivities.length === 0) {
            errors.push('流程必须包含至少一个结束节点');
        }
        
        // 检查路由连通性
        // ...
        
        return { valid: errors.length === 0, errors };
    }
    
    /**
     * 转换为JSON
     * @returns {Object}
     */
    toJSON() {
        return {
            processDefId: this.processDefId,
            name: this.name,
            description: this.description,
            classification: this.classification,
            systemCode: this.systemCode,
            accessLevel: this.accessLevel,
            version: this.version,
            status: this.status,
            limit: this.limit,
            durationUnit: this.durationUnit,
            startNode: this.startNode,
            endNodes: this.endNodes,
            activities: this.activities.map(a => a.toJSON()),
            routes: this.routes,
            listeners: this.listeners,
            rightGroups: this.rightGroups,
            extendedAttributes: this.extendedAttributes
        };
    }
    
    /**
     * 从JSON创建
     * @param {Object} json
     * @returns {ProcessDef}
     */
    static fromJSON(json) {
        return new ProcessDef(json);
    }
    
    /**
     * 从后端数据创建
     * @param {Object} backendData
     * @returns {ProcessDef}
     */
    static fromBackend(backendData) {
        return DataAdapter.fromBackendProcess(backendData);
    }
    
    /**
     * 转换为后端格式
     * @returns {Object}
     */
    toBackend() {
        return DataAdapter.toBackendProcess(this);
    }
    
    /**
     * 克隆
     * @returns {ProcessDef}
     */
    clone() {
        return new ProcessDef(JSON.parse(JSON.stringify(this.toJSON())));
    }
    
    /**
     * 获取统计信息
     * @returns {Object}
     */
    getStatistics() {
        return {
            activityCount: this.activities.length,
            routeCount: this.routes.length,
            taskCount: this.activities.filter(a => a.activityType === 'TASK').length,
            gatewayCount: this.activities.filter(a => 
                ['XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY'].includes(a.activityType)
            ).length
        };
    }
    
    // === 私有方法 ===
    
    _generateId() {
        return 'proc_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }
    
    _normalizeStartNode(startNode, activities) {
        if (startNode) return startNode;
        const startActivity = activities?.find(a => a.position === 'START' || a.activityType === 'START');
        if (startActivity) {
            return {
                participantId: 'Participant_Start',
                firstActivityId: startActivity.activityDefId,
                positionCoord: startActivity.positionCoord || { x: 50, y: 200 },
                routing: 'NO_ROUTING'
            };
        }
        return null;
    }
    
    _normalizeEndNodes(endNodes, activities) {
        if (endNodes && endNodes.length > 0) return endNodes;
        const endActivities = activities?.filter(a => a.position === 'END' || a.activityType === 'END') || [];
        return endActivities.map(a => ({
            participantId: 'Participant_End',
            lastActivityId: a.activityDefId,
            positionCoord: a.positionCoord || { x: 800, y: 200 },
            routing: 'NO_ROUTING'
        }));
    }
}
```

#### 6.4.2 ActivityDef.js (前端活动定义模型)

```javascript
class ActivityDef {
    constructor(data) {
        // 基本属性
        this.activityDefId = data?.activityDefId || this._generateId();
        this.name = data?.name || '新活动';
        this.description = data?.description || '';
        
        // 类型属性
        this.activityType = data?.activityType || 'TASK';
        this.position = this._normalizePosition(data?.position, this.activityType);
        this.activityCategory = data?.activityCategory || 'HUMAN';
        
        // 坐标和参与者
        this.positionCoord = this._normalizeCoord(data?.positionCoord);
        this.participantId = data?.participantId || '';
        
        // 实现方式
        this.implementation = data?.implementation || 'No';
        
        // 时限配置
        this.limitTime = data?.limitTime || data?.limit || 0;
        this.alertTime = data?.alertTime || 0;
        this.durationUnit = data?.durationUnit || 'D';
        
        // 流程控制
        this.join = data?.join || 'XOR';
        this.split = data?.split || 'XOR';
        this.canRouteBack = this._normalizeYesNo(data?.canRouteBack);
        this.routeBackMethod = data?.routeBackMethod || 'PREV';
        this.canSpecialSend = this._normalizeYesNo(data?.canSpecialSend);
        this.specialScope = data?.specialScope || 'ALL';
        
        // 属性组
        this.RIGHT = this._buildRightGroup(data);
        this.FORM = this._buildFormGroup(data);
        this.SERVICE = this._buildServiceGroup(data);
        this.WORKFLOW = this._buildWorkflowGroup(data);
        
        // 块活动属性
        this.startOfBlock = data?.startOfBlock || null;
        this.endOfBlock = data?.endOfBlock || null;
        this.participantVisualOrder = data?.participantVisualOrder || '';
        
        // Agent/Scene配置
        this.agentConfig = data?.agentConfig || null;
        this.sceneConfig = data?.sceneConfig || null;
        
        // 扩展属性
        this.extendedAttributes = data?.extendedAttributes || {};
    }
    
    // === 属性组便捷访问方法 ===
    
    getRight() { return this.RIGHT; }
    getForm() { return this.FORM; }
    getService() { return this.SERVICE; }
    getWorkflow() { return this.WORKFLOW; }
    
    // === 核心方法 ===
    
    /**
     * 验证活动定义
     * @returns {Object} {valid: Boolean, errors: String[]}
     */
    validate() {
        const errors = [];
        
        if (!this.name || this.name.trim() === '') {
            errors.push('活动名称不能为空');
        }
        
        if (!this.activityType) {
            errors.push('活动类型不能为空');
        }
        
        if (!this.positionCoord || typeof this.positionCoord.x !== 'number') {
            errors.push('活动坐标无效');
        }
        
        return { valid: errors.length === 0, errors };
    }
    
    /**
     * 转换为JSON
     * @returns {Object}
     */
    toJSON() {
        return {
            activityDefId: this.activityDefId,
            name: this.name,
            description: this.description,
            activityType: this.activityType,
            position: this.position,
            activityCategory: this.activityCategory,
            positionCoord: this.positionCoord,
            participantId: this.participantId,
            implementation: this.implementation,
            limitTime: this.limitTime,
            alertTime: this.alertTime,
            durationUnit: this.durationUnit,
            join: this.join,
            split: this.split,
            canRouteBack: this.canRouteBack,
            routeBackMethod: this.routeBackMethod,
            canSpecialSend: this.canSpecialSend,
            specialScope: this.specialScope,
            RIGHT: this.RIGHT,
            FORM: this.FORM,
            SERVICE: this.SERVICE,
            WORKFLOW: this.WORKFLOW,
            agentConfig: this.agentConfig,
            sceneConfig: this.sceneConfig,
            extendedAttributes: this.extendedAttributes
        };
    }
    
    /**
     * 从JSON创建
     * @param {Object} json
     * @returns {ActivityDef}
     */
    static fromJSON(json) {
        return new ActivityDef(json);
    }
    
    /**
     * 克隆
     * @returns {ActivityDef}
     */
    clone() {
        return new ActivityDef(JSON.parse(JSON.stringify(this.toJSON())));
    }
    
    // === 私有方法 ===
    
    _generateId() {
        return 'act_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }
    
    _normalizePosition(position, activityType) {
        if (position) return position;
        if (activityType === 'START') return 'START';
        if (activityType === 'END') return 'END';
        return 'NORMAL';
    }
    
    _normalizeCoord(coord) {
        if (coord && typeof coord.x === 'number' && typeof coord.y === 'number') {
            return coord;
        }
        return { x: 0, y: 0 };
    }
    
    _normalizeYesNo(value) {
        if (value === true || value === 'YES' || value === 'yes') return 'YES';
        return 'NO';
    }
    
    _buildRightGroup(data) {
        return {
            performType: data?.performType || data?.RIGHT?.performType || 'SINGLE',
            performSequence: data?.performSequence || data?.RIGHT?.performSequence || '1',
            performerSelectedId: data?.performerSelectedId || data?.RIGHT?.performerSelectedId || '',
            readerSelectedId: data?.readerSelectedId || data?.RIGHT?.readerSelectedId || '',
            specialSendScope: data?.specialSendScope || data?.RIGHT?.specialSendScope || 'ALL',
            canInsteadSign: this._normalizeYesNo(data?.canInsteadSign || data?.RIGHT?.canInsteadSign),
            canTakeBack: this._normalizeYesNo(data?.canTakeBack || data?.RIGHT?.canTakeBack),
            canReSend: this._normalizeYesNo(data?.canReSend || data?.RIGHT?.canReSend),
            movePerformerTo: data?.movePerformerTo || data?.RIGHT?.movePerformerTo || 'NEXT',
            moveSponsorTo: data?.moveSponsorTo || data?.RIGHT?.moveSponsorTo || 'CURRENT',
            moveReaderTo: data?.moveReaderTo || data?.RIGHT?.moveReaderTo || 'NONE'
        };
    }
    
    _buildFormGroup(data) {
        return {
            formId: data?.formId || data?.FORM?.formId || '',
            formName: data?.formName || data?.FORM?.formName || '',
            formType: data?.formType || data?.FORM?.formType || 'CUSTOM',
            formUrl: data?.formUrl || data?.FORM?.formUrl || ''
        };
    }
    
    _buildServiceGroup(data) {
        return {
            httpMethod: data?.httpMethod || data?.SERVICE?.httpMethod || 'POST',
            httpUrl: data?.httpUrl || data?.SERVICE?.httpUrl || '',
            httpRequestType: data?.httpRequestType || data?.SERVICE?.httpRequestType || 'JSON',
            httpResponseType: data?.httpResponseType || data?.SERVICE?.httpResponseType || 'JSON',
            httpServiceParams: data?.httpServiceParams || data?.SERVICE?.httpServiceParams || '{}',
            serviceSelectedId: data?.serviceSelectedId || data?.SERVICE?.serviceSelectedId || ''
        };
    }
    
    _buildWorkflowGroup(data) {
        return {
            deadLineOperation: data?.deadLineOperation || data?.WORKFLOW?.deadLineOperation || 'ALERT',
            specialScope: data?.specialScope || data?.WORKFLOW?.specialScope || 'ALL'
        };
    }
}
```

#### 6.4.3 RouteDef.js (前端路由定义模型)

```javascript
class RouteDef {
    constructor(data) {
        // 双命名兼容
        this.routeDefId = data?.routeDefId || data?.id || this._generateId();
        this.id = this.routeDefId;  // 兼容字段
        
        this.name = data?.name || '';
        this.description = data?.description || '';
        
        // 双命名兼容
        this.from = data?.from || data?.fromActivityDefId || '';
        this.fromActivityDefId = this.from;  // 兼容字段
        
        this.to = data?.to || data?.toActivityDefId || '';
        this.toActivityDefId = this.to;  // 兼容字段
        
        // 双命名兼容
        this.order = data?.order || data?.routeOrder || 0;
        this.routeOrder = this.order;  // 兼容字段
        
        this.direction = data?.direction || data?.routeDirection || 'FORWARD';
        this.routeDirection = this.direction;  // 兼容字段
        
        this.condition = data?.condition || data?.routeCondition || '';
        this.routeCondition = this.condition;  // 兼容字段
        
        this.conditionType = data?.conditionType || data?.routeConditionType || 'DEFAULT';
        this.routeConditionType = this.conditionType;  // 兼容字段
        
        this.routing = data?.routing || '';
        this.extendedAttributes = data?.extendedAttributes || {};
    }
    
    toJSON() {
        return {
            routeDefId: this.routeDefId,
            id: this.id,
            name: this.name,
            description: this.description,
            from: this.from,
            fromActivityDefId: this.fromActivityDefId,
            to: this.to,
            toActivityDefId: this.toActivityDefId,
            order: this.order,
            routeOrder: this.routeOrder,
            direction: this.direction,
            routeDirection: this.routeDirection,
            condition: this.condition,
            routeCondition: this.routeCondition,
            conditionType: this.conditionType,
            routeConditionType: this.routeConditionType,
            routing: this.routing,
            extendedAttributes: this.extendedAttributes
        };
    }
    
    static fromJSON(json) {
        return new RouteDef(json);
    }
    
    static fromBackend(backendData) {
        return new RouteDef({
            routeDefId: backendData.routeDefId,
            name: backendData.name,
            description: backendData.description,
            from: backendData.from,
            to: backendData.to,
            routeOrder: backendData.routeOrder,
            routeDirection: backendData.routeDirection,
            routeConditionType: backendData.routeConditionType,
            condition: backendData.condition,
            extendedAttributes: backendData.extendedAttributes
        });
    }
    
    toBackend() {
        return {
            routeDefId: this.routeDefId,
            name: this.name,
            description: this.description,
            from: this.from,
            to: this.to,
            routeOrder: this.routeOrder,
            routeDirection: this.routeDirection,
            routeConditionType: this.routeConditionType,
            condition: this.condition,
            extendedAttributes: this.extendedAttributes
        };
    }
    
    _generateId() {
        return 'route_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }
}
```

---

### 6.5 DataAdapter.js 前后端数据适配规则 【P0-05 已补充】

```javascript
const DataAdapter = {
    
    /**
     * 后端ProcessDef → 前端ProcessDef
     * @param {Object} backendData 后端返回的流程数据
     * @returns {Object} 前端格式的流程数据
     */
    fromBackendProcess(backendData) {
        if (!backendData) return null;
        
        return {
            // 基本属性 - 字段命名映射
            processDefId: backendData.processDefId,
            name: backendData.name,
            description: backendData.description,
            category: backendData.classification || '办公流程',
            classification: backendData.classification,
            systemCode: backendData.systemCode || 'bpm',
            accessLevel: backendData.accessLevel || 'PUBLIC',
            
            // 版本信息
            version: backendData.version || 1,
            status: backendData.status || backendData.publicationStatus || 'DRAFT',
            state: backendData.status || backendData.publicationStatus || 'DRAFT',
            creatorId: backendData.creatorId,
            creatorName: backendData.creatorName,
            createTime: backendData.createdTime || backendData.createTime,
            createdTime: backendData.createdTime,
            modifierId: backendData.modifierId,
            modifierName: backendData.modifierName,
            modifyTime: backendData.modifyTime || backendData.updatedTime,
            updatedTime: backendData.updatedTime,
            activeTime: backendData.activeTime,
            freezeTime: backendData.freezeTime,
            
            // 时限配置
            limit: backendData.limit || 0,
            durationUnit: backendData.durationUnit || 'D',
            
            // 流程结构
            startNode: backendData.startNode,
            endNodes: backendData.endNodes || [],
            activities: (backendData.activities || []).map(a => this.fromBackendActivity(a)),
            routes: (backendData.routes || []).map(r => this.fromBackendRoute(r)),
            
            // 监听器和权限
            listeners: backendData.listeners || [],
            rightGroups: backendData.rightGroups || [],
            
            // 扩展属性
            extendedAttributes: backendData.extendedAttributes || {},
            agentConfig: backendData.agentConfig,
            sceneConfig: backendData.sceneConfig
        };
    },
    
    /**
     * 前端ProcessDef → 后端ProcessDef
     * @param {Object|ProcessDef} frontendData 前端流程数据
     * @returns {Object} 后端格式的流程数据
     */
    toBackendProcess(frontendData) {
        if (!frontendData) return null;
        
        const data = frontendData instanceof ProcessDef ? frontendData.toJSON() : frontendData;
        
        return {
            processDefId: data.processDefId,
            name: data.name,
            description: data.description,
            classification: data.classification || data.category,
            systemCode: data.systemCode,
            accessLevel: data.accessLevel,
            version: data.version,
            publicationStatus: data.status || data.state,
            status: data.status || data.state,
            creatorId: data.creatorId,
            creatorName: data.creatorName,
            createdTime: data.createdTime || data.createTime,
            modifierId: data.modifierId,
            modifierName: data.modifierName,
            modifyTime: data.modifyTime || data.updatedTime,
            updatedTime: data.updatedTime,
            activeTime: data.activeTime,
            freezeTime: data.freezeTime,
            limit: data.limit,
            durationUnit: data.durationUnit,
            startNode: data.startNode,
            endNodes: data.endNodes,
            activities: (data.activities || []).map(a => 
                a instanceof ActivityDef ? this.toBackendActivity(a.toJSON()) : this.toBackendActivity(a)
            ),
            routes: (data.routes || []).map(r => 
                r instanceof RouteDef ? r.toBackend() : this.toBackendRoute(r)
            ),
            listeners: data.listeners,
            rightGroups: data.rightGroups,
            extendedAttributes: data.extendedAttributes,
            agentConfig: data.agentConfig,
            sceneConfig: data.sceneConfig
        };
    },
    
    /**
     * 后端ActivityDef → 前端ActivityDef
     */
    fromBackendActivity(backendData) {
        if (!backendData) return null;
        
        return {
            activityDefId: backendData.activityDefId,
            name: backendData.name,
            description: backendData.description,
            
            // 枚举值转换 - 后端枚举code → 前端字符串
            position: backendData.position?.code || backendData.position || 'NORMAL',
            activityType: backendData.activityType?.code || backendData.activityType || 'TASK',
            activityCategory: backendData.activityCategory?.code || backendData.activityCategory || 'HUMAN',
            
            positionCoord: backendData.positionCoord || { x: 0, y: 0 },
            participantId: backendData.participantId,
            implementation: backendData.implementation,
            execClass: backendData.execClass,
            
            // 子配置转换
            timing: this._convertTimingFromBackend(backendData.timing),
            routing: this._convertRoutingFromBackend(backendData.routing),
            right: this._convertRightFromBackend(backendData.right),
            subFlow: backendData.subFlow,
            device: backendData.device,
            service: backendData.service,
            event: backendData.event,
            agentConfig: backendData.agentConfig,
            sceneConfig: backendData.sceneConfig,
            
            // 表单属性
            formId: backendData.formId,
            formName: backendData.formName,
            formType: backendData.formType,
            formUrl: backendData.formUrl,
            
            // 块活动属性
            startOfBlock: backendData.startOfBlock,
            endOfBlock: backendData.endOfBlock,
            participantVisualOrder: backendData.participantVisualOrder,
            
            extendedAttributes: backendData.extendedAttributes
        };
    },
    
    /**
     * 前端ActivityDef → 后端ActivityDef
     */
    toBackendActivity(frontendData) {
        if (!frontendData) return null;
        
        return {
            activityDefId: frontendData.activityDefId,
            name: frontendData.name,
            description: frontendData.description,
            position: frontendData.position,
            activityType: frontendData.activityType,
            activityCategory: frontendData.activityCategory,
            positionCoord: frontendData.positionCoord,
            participantId: frontendData.participantId,
            implementation: frontendData.implementation,
            execClass: frontendData.execClass,
            timing: frontendData.timing,
            routing: frontendData.routing,
            right: frontendData.right,
            subFlow: frontendData.subFlow,
            device: frontendData.device,
            service: frontendData.service,
            event: frontendData.event,
            agentConfig: frontendData.agentConfig,
            sceneConfig: frontendData.sceneConfig,
            formId: frontendData.formId,
            formName: frontendData.formName,
            formType: frontendData.formType,
            formUrl: frontendData.formUrl,
            startOfBlock: frontendData.startOfBlock,
            endOfBlock: frontendData.endOfBlock,
            participantVisualOrder: frontendData.participantVisualOrder,
            extendedAttributes: frontendData.extendedAttributes
        };
    },
    
    /**
     * 后端RouteDef → 前端RouteDef
     */
    fromBackendRoute(backendData) {
        if (!backendData) return null;
        
        return {
            routeDefId: backendData.routeDefId,
            id: backendData.routeDefId,
            name: backendData.name,
            description: backendData.description,
            from: backendData.from,
            fromActivityDefId: backendData.from,
            to: backendData.to,
            toActivityDefId: backendData.to,
            order: backendData.routeOrder || 0,
            routeOrder: backendData.routeOrder || 0,
            direction: backendData.routeDirection || 'FORWARD',
            routeDirection: backendData.routeDirection || 'FORWARD',
            condition: backendData.condition || '',
            routeCondition: backendData.condition || '',
            conditionType: backendData.routeConditionType || 'DEFAULT',
            routeConditionType: backendData.routeConditionType || 'DEFAULT',
            routing: backendData.routing || '',
            extendedAttributes: backendData.extendedAttributes
        };
    },
    
    /**
     * 前端RouteDef → 后端RouteDef
     */
    toBackendRoute(frontendData) {
        if (!frontendData) return null;
        
        return {
            routeDefId: frontendData.routeDefId || frontendData.id,
            name: frontendData.name,
            description: frontendData.description,
            from: frontendData.from || frontendData.fromActivityDefId,
            to: frontendData.to || frontendData.toActivityDefId,
            routeOrder: frontendData.routeOrder || frontendData.order || 0,
            routeDirection: frontendData.routeDirection || frontendData.direction || 'FORWARD',
            routeConditionType: frontendData.routeConditionType || frontendData.conditionType || 'DEFAULT',
            condition: frontendData.condition || frontendData.routeCondition || '',
            routing: frontendData.routing || '',
            extendedAttributes: frontendData.extendedAttributes
        };
    },
    
    // === 私有转换方法 ===
    
    _convertTimingFromBackend(timing) {
        if (!timing) return null;
        return {
            limitTime: timing.limitTime || 0,
            alertTime: timing.alertTime || 0,
            durationUnit: timing.durationUnit || 'D',
            startTime: timing.startTime,
            endTime: timing.endTime,
            remindType: timing.remindType,
            remindInterval: timing.remindInterval
        };
    },
    
    _convertRoutingFromBackend(routing) {
        if (!routing) return null;
        return {
            join: routing.join || 'XOR',
            split: routing.split || 'XOR',
            canRouteBack: routing.canRouteBack || 'NO',
            routeBackMethod: routing.routeBackMethod || 'PREV',
            canSpecialSend: routing.canSpecialSend || 'NO',
            specialScope: routing.specialScope || 'ALL',
            defaultRoute: routing.defaultRoute,
            parallelMode: routing.parallelMode,
            mergeCondition: routing.mergeCondition
        };
    },
    
    _convertRightFromBackend(right) {
        if (!right) return null;
        return {
            moveSponsorTo: right.moveSponsorTo || 'CURRENT',
            performer: right.performer,
            performerType: right.performerType || 'USER',
            participationType: right.participationType || 'SINGLE',
            participationScope: right.participationScope,
            participationScopeValue: right.participationScopeValue,
            candidateUsers: right.candidateUsers || [],
            candidateGroups: right.candidateGroups || [],
            candidateRoles: right.candidateRoles || [],
            assignee: right.assignee,
            owner: right.owner,
            reassignable: right.reassignable !== false,
            delegatable: right.delegatable !== false,
            transferable: right.transferable !== false
        };
    },
    
    /**
     * 日期格式转换
     * @param {String} dateStr 日期字符串
     * @param {String} format 目标格式 ('iso'|'display'|'timestamp')
     * @returns {String|Number}
     */
    convertDate(dateStr, format = 'iso') {
        if (!dateStr) return null;
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return null;
        
        switch (format) {
            case 'iso':
                return date.toISOString();
            case 'display':
                return date.toLocaleString('zh-CN');
            case 'timestamp':
                return date.getTime();
            default:
                return dateStr;
        }
    },
    
    /**
     * null/undefined 处理策略
     * @param {*} value 值
     * @param {*} defaultValue 默认值
     * @returns {*}
     */
    normalizeValue(value, defaultValue) {
        if (value === null || value === undefined) {
            return defaultValue;
        }
        return value;
    }
};
```

---

### 6.6 index.html 页面布局结构 【P0-06 已补充】

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BPM流程设计器</title>
    <link rel="stylesheet" href="css/var.css">
    <link rel="stylesheet" href="css/base.css">
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/node.css">
    <link rel="stylesheet" href="css/panel.css">
    <link rel="stylesheet" href="css/chat.css">
    <link rel="stylesheet" href="css/tree.css">
    <link rel="stylesheet" href="css/palette.css">
    <link rel="stylesheet" href="lib/icon/icon.css">
</head>
<body>
    <div id="app" class="app-container">
        <!-- 左侧边栏 -->
        <aside id="sidebar" class="sidebar">
            <!-- 流程树导航 -->
            <div id="tree-container" class="tree-container">
                <div class="tree-header">
                    <span class="tree-title">流程列表</span>
                    <button id="tree-refresh-btn" class="tree-btn" title="刷新">
                        <i class="ri-refresh-line"></i>
                    </button>
                </div>
                <div id="tree-content" class="tree-content"></div>
            </div>
            
            <!-- 拖拽元素面板 -->
            <div id="elements-container" class="elements-container">
                <div class="elements-header">
                    <span class="elements-title">流程元素</span>
                </div>
                <div id="elements-content" class="elements-content">
                    <!-- 拖拽元素将由JS动态生成 -->
                </div>
            </div>
        </aside>
        
        <!-- 中间主区域 -->
        <main id="main-area" class="main-area">
            <!-- 工具栏 -->
            <header id="toolbar" class="toolbar">
                <div class="toolbar-left">
                    <button id="btn-save" class="toolbar-btn" title="保存 (Ctrl+S)">
                        <i class="ri-save-line"></i>
                    </button>
                    <button id="btn-undo" class="toolbar-btn" title="撤销 (Ctrl+Z)" disabled>
                        <i class="ri-arrow-go-back-line"></i>
                    </button>
                    <button id="btn-redo" class="toolbar-btn" title="重做 (Ctrl+Y)" disabled>
                        <i class="ri-arrow-go-forward-line"></i>
                    </button>
                    <span class="toolbar-divider"></span>
                    <button id="btn-delete" class="toolbar-btn" title="删除 (Delete)" disabled>
                        <i class="ri-delete-bin-line"></i>
                    </button>
                </div>
                <div class="toolbar-center">
                    <!-- 标签页管理 -->
                    <div id="tab-manager" class="tab-manager">
                        <div id="tabs-container" class="tabs-container"></div>
                        <button id="btn-new-tab" class="tab-btn" title="新建流程">
                            <i class="ri-add-line"></i>
                        </button>
                    </div>
                </div>
                <div class="toolbar-right">
                    <button id="btn-zoom-out" class="toolbar-btn" title="缩小">
                        <i class="ri-zoom-out-line"></i>
                    </button>
                    <span id="zoom-level" class="zoom-level">100%</span>
                    <button id="btn-zoom-in" class="toolbar-btn" title="放大">
                        <i class="ri-zoom-in-line"></i>
                    </button>
                    <button id="btn-fit-screen" class="toolbar-btn" title="适应屏幕">
                        <i class="ri-fullscreen-line"></i>
                    </button>
                    <span class="toolbar-divider"></span>
                    <button id="btn-theme" class="toolbar-btn" title="切换主题">
                        <i class="ri-moon-line"></i>
                    </button>
                </div>
            </header>
            
            <!-- SVG画布 -->
            <div id="canvas-container" class="canvas-container">
                <svg id="canvas" class="canvas">
                    <defs>
                        <!-- 箭头标记 -->
                        <marker id="arrowhead" markerWidth="10" markerHeight="7" 
                                refX="9" refY="3.5" orient="auto">
                            <polygon points="0 0, 10 3.5, 0 7" fill="var(--edge-color)"/>
                        </marker>
                    </defs>
                    <!-- 网格层 -->
                    <g id="grid-layer" class="grid-layer"></g>
                    <!-- 边层 -->
                    <g id="edge-layer" class="edge-layer"></g>
                    <!-- 节点层 -->
                    <g id="node-layer" class="node-layer"></g>
                </svg>
            </div>
        </main>
        
        <!-- 右侧属性面板 -->
        <aside id="panel" class="panel">
            <div class="panel-header">
                <span id="panel-title" class="panel-title">属性</span>
                <button id="panel-close" class="panel-btn" title="关闭">
                    <i class="ri-close-line"></i>
                </button>
            </div>
            <div id="panel-content" class="panel-content">
                <!-- 抽屉式面板容器 -->
                <div id="panel-drawers" class="panel-drawers">
                    <!-- 通用属性 -->
                    <div class="panel-drawer" data-category="common">
                        <div class="drawer-header">
                            <i class="ri-settings-3-line"></i>
                            <span>通用属性</span>
                            <i class="ri-arrow-down-s-line drawer-arrow"></i>
                        </div>
                        <div class="drawer-content"></div>
                    </div>
                    
                    <!-- 业务属性 -->
                    <div class="panel-drawer" data-category="business">
                        <div class="drawer-header">
                            <i class="ri-briefcase-line"></i>
                            <span>业务属性</span>
                            <i class="ri-arrow-down-s-line drawer-arrow"></i>
                        </div>
                        <div class="drawer-content"></div>
                    </div>
                    
                    <!-- 插件属性 -->
                    <div class="panel-drawer" data-category="plugin">
                        <div class="drawer-header">
                            <i class="ri-plug-line"></i>
                            <span>插件属性</span>
                            <i class="ri-arrow-down-s-line drawer-arrow"></i>
                        </div>
                        <div class="drawer-content"></div>
                    </div>
                </div>
            </div>
        </aside>
        
        <!-- AI对话面板 -->
        <aside id="chat" class="chat">
            <div class="chat-header">
                <i class="ri-robot-line"></i>
                <span>AI助手</span>
                <button id="chat-toggle" class="chat-btn" title="折叠/展开">
                    <i class="ri-arrow-right-s-line"></i>
                </button>
            </div>
            <div id="chat-content" class="chat-content">
                <!-- 消息列表 -->
                <div id="chat-messages" class="chat-messages">
                    <div class="chat-message system">
                        <div class="message-content">
                            您好！我是BPM流程设计助手。您可以：
                            <ul>
                                <li>说"创建一个请假审批流程"</li>
                                <li>说"添加一个经理审批节点"</li>
                                <li>说"设置办理人为部门经理"</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="chat-input-area">
                <textarea id="chat-input" class="chat-input" 
                          placeholder="输入消息... (Enter发送, Shift+Enter换行)"
                          rows="1"></textarea>
                <button id="chat-send" class="chat-send-btn" title="发送">
                    <i class="ri-send-plane-fill"></i>
                </button>
            </div>
        </aside>
    </div>
    
    <!-- 右键菜单 -->
    <div id="context-menu" class="context-menu hidden">
        <ul class="context-menu-list"></ul>
    </div>
    
    <!-- 脚本 -->
    <script src="lib/icon/icons.js"></script>
    <script src="js/sdk/Theme.js"></script>
    <script src="js/sdk/EnumMapping.js"></script>
    <script src="js/sdk/DataAdapter.js"></script>
    <script src="js/sdk/Store.js"></script>
    <script src="js/sdk/Api.js"></script>
    <script src="js/model/ProcessDef.js"></script>
    <script src="js/model/ActivityDef.js"></script>
    <script src="js/model/RouteDef.js"></script>
    <script src="js/panel/PanelManager.js"></script>
    <script src="js/Canvas.js"></script>
    <script src="js/Chat.js"></script>
    <script src="js/Tree.js"></script>
    <script src="js/Elements.js"></script>
    <script src="js/Toolbar.js"></script>
    <script src="js/TabManager.js"></script>
    <script src="js/ContextMenu.js"></script>
    <script src="js/App.js"></script>
</body>
</html>
```

**布局说明**:

| 区域 | ID | CSS类 | 布局方式 | 说明 |
|------|-----|-------|----------|------|
| 整体容器 | app | app-container | flex (row) | 水平排列所有区域 |
| 左侧边栏 | sidebar | sidebar | flex (column) | 垂直排列树和元素面板 |
| 中间主区域 | main-area | main-area | flex (column) | 垂直排列工具栏和画布 |
| 右侧属性面板 | panel | panel | flex (column) | 垂直排列标题和内容 |
| AI对话面板 | chat | chat | flex (column) | 可折叠 |

**响应式策略**:
- 最小宽度: 1280px
- 侧边栏: 固定宽度 240px
- 属性面板: 固定宽度 320px
- AI对话面板: 可折叠，展开时宽度 300px
- 画布区域: flex: 1 自适应

---

### 6.7 面板插件系统 (PanelManager.js)

插件分类:
- **通用属性(common)**: BasicPanelPlugin, AgentBasicPanelPlugin, SceneBasicPanelPlugin, ProcessBasicPanelPlugin, StartNodePanelPlugin, EndNodesPanelPlugin
- **业务属性(business)**: TimingPanelPlugin, FlowControlPanelPlugin, RightPanelPlugin, FormPanelPlugin, ServicePanelPlugin, LLMPanelPlugin, AgentToolsPanelPlugin, SceneEnginePanelPlugin, SceneParamsPanelPlugin, SceneCapabilityPanelPlugin, ListenersPanelPlugin, RightGroupsPanelPlugin, ProcessVariablesPanelPlugin, ProcessTimingPanelPlugin
- **插件属性(plugin)**: ActivityListenerPanelPlugin, ExecutionListenerPanelPlugin, ExpressionPanelPlugin, MultiInstancePanelPlugin, ExtensionPropertiesPanelPlugin

插件组映射: HUMAN→activity, AGENT→activity-agent, SCENE→activity-scene

---

### 6.8 AI对话管理器 (Chat.js) 【P1-06 已补充】

#### 6.8.1 UI规格

**消息类型样式**:
| 消息类型 | CSS类 | 样式 |
|----------|-------|------|
| 用户消息 | user | 右对齐，蓝色背景 |
| AI消息 | assistant | 左对齐，灰色背景 |
| 系统消息 | system | 居中，浅灰背景 |
| 错误消息 | error | 左对齐，红色边框 |

**输入区域**:
- 输入框: textarea，自动高度调整
- 发送按钮: 右侧固定位置
- 折叠/展开按钮: 标题栏右侧

**消息操作按钮**:
- "应用建议"按钮: 当AI返回建议时显示
- "复制"按钮: 复制消息内容

#### 6.8.2 LLM对话流程

```
用户输入 → 构建上下文 → 调用NLP API → 意图识别 → 执行操作 → 返回结果
    │            │              │            │           │
    │            │              │            │           └─ 显示结果消息
    │            │              │            └─ 根据intent调用对应方法
    │            │              └─ POST /api/bpm/nlp/chat
    │            └─ {processId, activityId, mode, processData}
    └─ "创建一个请假审批流程"
```

**上下文构建规则**:
```javascript
const context = {
    processId: store.process?.processDefId,
    processName: store.process?.name,
    activityId: store.currentActivity?.activityDefId,
    activityName: store.currentActivity?.name,
    mode: 'DESIGN',  // DESIGN | SIMULATE | DEBUG
    processData: store.process?.toJSON(),
    recentActions: []  // 最近操作历史
};
```

**错误处理**:
- 网络错误: 显示重试按钮
- LLM超时: 显示等待提示，支持取消
- 解析错误: 显示原始响应，提示用户重述

---

### 6.9 API客户端 (Api.js)

baseUrl: /api/bpm, timeout: 30000ms

核心方法:
| 方法 | HTTP | 端点 | 说明 |
|------|------|------|------|
| getProcess(id, ver) | GET | /process/{id}/version/{ver} | 获取流程 |
| saveProcess(def) | POST | /process | 创建流程 |
| updateProcess(id, def) | PUT | /process/{id} | 更新流程 |
| deleteProcess(id) | DELETE | /process/{id} | 删除流程 |
| getProcessList(params) | GET | /process | 获取流程列表 |
| exportYaml(id) | GET | /process/{id}/export/yaml | 导出YAML |
| importYaml(yaml) | POST | /process/import/yaml | 导入YAML |
| saveActivity(pid, def) | POST | /process/{pid}/activity | 添加活动 |
| updateActivity(pid, aid, def) | PUT | /process/{pid}/activity/{aid} | 更新活动 |
| deleteActivity(pid, aid) | DELETE | /process/{pid}/activity/{aid} | 删除活动 |

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

### 7.3 Function Calling 已注册函数 【P1-04 已补充】

#### 7.3.1 执行者推导函数

**get_organization_tree** - 获取组织架构树
```json
{
    "name": "get_organization_tree",
    "description": "获取组织架构树，返回部门层级结构",
    "parameters": {
        "type": "object",
        "properties": {
            "rootDeptId": {
                "type": "string",
                "description": "根部门ID，不传则返回完整树"
            },
            "depth": {
                "type": "integer",
                "description": "返回层级深度，默认-1表示全部"
            }
        },
        "required": []
    }
}
```

**get_users_by_role** - 按角色获取用户
```json
{
    "name": "get_users_by_role",
    "description": "根据角色ID获取该角色下的所有用户",
    "parameters": {
        "type": "object",
        "properties": {
            "roleId": {
                "type": "string",
                "description": "角色ID"
            },
            "departmentId": {
                "type": "string",
                "description": "部门ID，可选，用于过滤特定部门的用户"
            }
        },
        "required": ["roleId"]
    }
}
```

**search_users** - 搜索用户
```json
{
    "name": "search_users",
    "description": "根据关键词搜索用户",
    "parameters": {
        "type": "object",
        "properties": {
            "keyword": {
                "type": "string",
                "description": "搜索关键词(姓名/工号/部门)"
            },
            "limit": {
                "type": "integer",
                "description": "返回数量限制，默认10"
            }
        },
        "required": ["keyword"]
    }
}
```

**get_department_leader** - 获取部门负责人
```json
{
    "name": "get_department_leader",
    "description": "获取指定部门的负责人",
    "parameters": {
        "type": "object",
        "properties": {
            "departmentId": {
                "type": "string",
                "description": "部门ID"
            },
            "leaderType": {
                "type": "string",
                "enum": ["primary", "deputy", "all"],
                "description": "负责人类型：primary-正职, deputy-副职, all-全部"
            }
        },
        "required": ["departmentId"]
    }
}
```

**list_roles** - 列出所有角色
```json
{
    "name": "list_roles",
    "description": "获取系统中所有角色列表",
    "parameters": {
        "type": "object",
        "properties": {
            "category": {
                "type": "string",
                "description": "角色分类，可选"
            }
        },
        "required": []
    }
}
```

#### 7.3.2 能力匹配函数

**list_capabilities** - 列出所有能力
```json
{
    "name": "list_capabilities",
    "description": "获取系统中所有可用能力列表",
    "parameters": {
        "type": "object",
        "properties": {
            "category": {
                "type": "string",
                "description": "能力分类，可选"
            }
        },
        "required": []
    }
}
```

**search_capabilities** - 搜索能力
```json
{
    "name": "search_capabilities",
    "description": "根据关键词搜索能力",
    "parameters": {
        "type": "object",
        "properties": {
            "keyword": {
                "type": "string",
                "description": "搜索关键词"
            },
            "limit": {
                "type": "integer",
                "description": "返回数量限制，默认10"
            }
        },
        "required": ["keyword"]
    }
}
```

**get_capability_detail** - 获取能力详情
```json
{
    "name": "get_capability_detail",
    "description": "获取指定能力的详细信息",
    "parameters": {
        "type": "object",
        "properties": {
            "capabilityId": {
                "type": "string",
                "description": "能力ID"
            }
        },
        "required": ["capabilityId"]
    }
}
```

**match_capability_by_activity** - 按活动匹配能力
```json
{
    "name": "match_capability_by_activity",
    "description": "根据活动描述匹配合适的能力",
    "parameters": {
        "type": "object",
        "properties": {
            "activityDesc": {
                "type": "string",
                "description": "活动描述"
            },
            "activityType": {
                "type": "string",
                "description": "活动类型"
            },
            "keywords": {
                "type": "array",
                "items": { "type": "string" },
                "description": "关键词列表"
            }
        },
        "required": ["activityDesc"]
    }
}
```

#### 7.3.3 表单匹配函数

**list_forms** - 列出所有表单
```json
{
    "name": "list_forms",
    "description": "获取系统中所有可用表单列表",
    "parameters": {
        "type": "object",
        "properties": {
            "category": {
                "type": "string",
                "description": "表单分类，可选"
            }
        },
        "required": []
    }
}
```

**search_forms** - 搜索表单
```json
{
    "name": "search_forms",
    "description": "根据关键词搜索表单",
    "parameters": {
        "type": "object",
        "properties": {
            "keyword": {
                "type": "string",
                "description": "搜索关键词"
            },
            "limit": {
                "type": "integer",
                "description": "返回数量限制，默认10"
            }
        },
        "required": ["keyword"]
    }
}
```

**get_form_schema** - 获取表单Schema
```json
{
    "name": "get_form_schema",
    "description": "获取指定表单的Schema定义",
    "parameters": {
        "type": "object",
        "properties": {
            "formId": {
                "type": "string",
                "description": "表单ID"
            }
        },
        "required": ["formId"]
    }
}
```

**match_form_by_activity** - 按活动匹配表单
```json
{
    "name": "match_form_by_activity",
    "description": "根据活动描述匹配合适的表单",
    "parameters": {
        "type": "object",
        "properties": {
            "activityDesc": {
                "type": "string",
                "description": "活动描述"
            },
            "activityType": {
                "type": "string",
                "description": "活动类型"
            },
            "keywords": {
                "type": "array",
                "items": { "type": "string" },
                "description": "关键词列表"
            }
        },
        "required": ["activityDesc"]
    }
}
```

**generate_form_schema** - 生成表单Schema
```json
{
    "name": "generate_form_schema",
    "description": "根据活动描述自动生成表单Schema",
    "parameters": {
        "type": "object",
        "properties": {
            "activityDesc": {
                "type": "string",
                "description": "活动描述"
            },
            "fieldRequirements": {
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "fieldName": { "type": "string" },
                        "fieldType": { "type": "string" },
                        "required": { "type": "boolean" }
                    }
                },
                "description": "字段需求列表，可选"
            }
        },
        "required": ["activityDesc"]
    }
}
```

---

### 7.4 Prompt模板文件 【P1-03 已补充】

#### 7.4.1 performer-derivation.yaml (执行者推导)

```yaml
system_prompt: |
  你是一个BPM流程设计专家，专门负责根据活动描述推导合适的执行者。
  
  你的任务是：
  1. 分析活动描述中的关键业务语义
  2. 识别可能涉及的部门、角色、职位信息
  3. 调用可用工具获取组织架构信息
  4. 返回推荐的执行者列表及推导依据
  
  可用工具：
  - get_organization_tree: 获取组织架构树
  - get_users_by_role: 按角色获取用户
  - search_users: 搜索用户
  - get_department_leader: 获取部门负责人
  - list_roles: 列出所有角色

variables:
  - name: context
    description: 当前上下文信息
    type: object
  - name: activityDesc
    description: 活动描述文本
    type: string

user_prompt_template: |
  请根据以下活动描述，推导合适的执行者：
  
  ## 上下文信息
  - 流程名称：{{context.processName}}
  - 当前活动：{{context.activityName}}
  
  ## 活动描述
  {{activityDesc}}
  
  ## 推导要求
  1. 首先分析活动描述中的关键信息
  2. 调用工具获取相关组织架构信息
  3. 返回推荐执行者及推导依据

output_format: |
  请以JSON格式返回结果：
  ```json
  {
    "success": true,
    "status": "SUCCESS",
    "recommendedPerformers": [
      {
        "candidateId": "用户ID或角色ID",
        "candidateName": "名称",
        "candidateType": "USER/ROLE/DEPT",
        "matchScore": 0.95,
        "matchReason": "匹配原因"
      }
    ],
    "confidence": 0.9,
    "derivationReason": "整体推导依据"
  }
  ```

examples:
  - input: "经理审批"
    output: |
      调用 get_department_leader 获取部门经理信息
      返回推荐执行者为部门经理
  - input: "HR备案"
    output: |
      调用 search_users 搜索HR部门用户
      返回HR部门相关人员
```

#### 7.4.2 capability-matching.yaml (能力匹配)

```yaml
system_prompt: |
  你是一个BPM流程设计专家，专门负责根据活动描述匹配合适的业务能力。
  
  你的任务是：
  1. 分析活动描述中的业务语义
  2. 识别可能需要的业务能力（如邮件、日历、文档等）
  3. 调用可用工具获取能力信息
  4. 返回推荐的能力列表及匹配依据
  
  可用工具：
  - list_capabilities: 列出所有能力
  - search_capabilities: 搜索能力
  - get_capability_detail: 获取能力详情
  - match_capability_by_activity: 按活动匹配能力

variables:
  - name: context
    description: 当前上下文信息
    type: object
  - name: activityDesc
    description: 活动描述文本
    type: string

user_prompt_template: |
  请根据以下活动描述，匹配合适的业务能力：
  
  ## 上下文信息
  - 流程名称：{{context.processName}}
  - 当前活动：{{context.activityName}}
  
  ## 活动描述
  {{activityDesc}}
  
  ## 匹配要求
  1. 分析活动需要的业务能力
  2. 调用工具搜索匹配的能力
  3. 返回推荐能力及匹配分数

output_format: |
  请以JSON格式返回结果：
  ```json
  {
    "success": true,
    "status": "SUCCESS",
    "recommendedCapabilities": [
      {
        "capabilityId": "能力ID",
        "capabilityName": "能力名称",
        "matchScore": 0.9,
        "isRecommended": true
      }
    ],
    "bindingConfig": {
      "autoBind": true,
      "parameters": {}
    }
  }
  ```
```

#### 7.4.3 form-matching.yaml (表单匹配)

```yaml
system_prompt: |
  你是一个BPM流程设计专家，专门负责根据活动描述匹配或生成合适的表单。
  
  你的任务是：
  1. 分析活动描述中的数据需求
  2. 搜索现有的匹配表单
  3. 如果没有合适表单，生成建议的表单Schema
  4. 返回推荐结果
  
  可用工具：
  - list_forms: 列出所有表单
  - search_forms: 搜索表单
  - get_form_schema: 获取表单Schema
  - match_form_by_activity: 按活动匹配表单
  - generate_form_schema: 生成表单Schema

variables:
  - name: context
    description: 当前上下文信息
    type: object
  - name: activityDesc
    description: 活动描述文本
    type: string

user_prompt_template: |
  请根据以下活动描述，匹配或生成合适的表单：
  
  ## 上下文信息
  - 流程名称：{{context.processName}}
  - 当前活动：{{context.activityName}}
  
  ## 活动描述
  {{activityDesc}}
  
  ## 匹配要求
  1. 首先搜索现有表单
  2. 如果找到匹配表单，返回推荐列表
  3. 如果没有合适表单，生成建议的表单Schema

output_format: |
  请以JSON格式返回结果：
  ```json
  {
    "success": true,
    "status": "SUCCESS",
    "recommendedForms": [...],
    "generatedSchema": {
      "schemaId": "auto_generated",
      "schemaName": "表单名称",
      "fields": [
        {
          "fieldId": "field_1",
          "fieldName": "字段名",
          "fieldType": "TEXT/NUMBER/DATE/SELECT",
          "label": "显示标签",
          "required": true
        }
      ]
    }
  }
  ```
```

#### 7.4.4 full-derivation.yaml (完整推导-三合一)

```yaml
system_prompt: |
  你是一个BPM流程设计专家，负责根据活动描述进行完整的流程推导。
  
  你需要同时完成以下三个任务：
  
  ## 任务一：办理人推导
  分析活动描述，识别关键业务语义，推导合适的办理人或角色。
  可用工具：get_organization_tree, get_users_by_role, search_users, get_department_leader, list_roles
  
  ## 任务二：能力匹配
  根据活动描述匹配合适的业务能力。
  可用工具：list_capabilities, search_capabilities, get_capability_detail, match_capability_by_activity
  
  ## 任务三：表单匹配
  根据活动描述推荐或生成合适的表单。
  可用工具：list_forms, search_forms, get_form_schema, match_form_by_activity, generate_form_schema
  
  ## 执行顺序
  1. 先执行办理人推导
  2. 再执行能力匹配
  3. 最后执行表单匹配
  4. 汇总返回完整结果

variables:
  - name: context
    description: 当前上下文信息
    type: object
  - name: activityDesc
    description: 活动描述文本
    type: string

user_prompt_template: |
  请分析以下活动描述，完成完整的流程推导：
  
  ## 上下文信息
  - 流程名称：{{context.processName}}
  - 当前活动：{{context.activityName}}
  - 活动类型：{{context.activityType}}
  
  ## 活动描述
  {{activityDesc}}
  
  ## 推导要求
  1. 办理人推导：确定最合适的办理人或角色
  2. 能力匹配：匹配相关的业务能力
  3. 表单匹配：推荐合适的表单或生成建议表单结构

output_format: |
  请以JSON格式返回完整推导结果：
  ```json
  {
    "performerDerivation": {
      "success": true,
      "recommendedPerformers": [...],
      "confidence": 0.9
    },
    "capabilityMatching": {
      "success": true,
      "recommendedCapabilities": [...]
    },
    "formMatching": {
      "success": true,
      "recommendedForms": [...],
      "generatedSchema": {...}
    }
  }
  ```

examples:
  - input: "经理审批请假申请"
    output: |
      1. 办理人推导：调用get_department_leader获取部门经理
      2. 能力匹配：匹配审批、通知能力
      3. 表单匹配：搜索请假申请表单
```

---

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
java -Dfile.encoding=UTF-8 -jar target/bpm-designer-3.1.0.jar  # 运行
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
2. **长上下文测试**: 本文档约25000+字，测试256K上下文窗口实际可用性
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
| 数据适配器 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\sdk\DataAdapter.js |

---

## 附录A: 补充项对照表

| 补充项ID | 内容 | 章节 | 状态 |
|----------|------|------|------|
| P0-01 | ActivityDef完整字段定义表 | 4.2.1 | ✅ 已补充 |
| P0-02 | 推导服务响应DTO结构 | 4.5 | ✅ 已补充 |
| P0-03 | NLP服务内部类字段定义 | 4.6 | ✅ 已补充 |
| P0-04 | 前端JS数据模型规格 | 6.4 | ✅ 已补充 |
| P0-05 | DataAdapter.js前后端数据适配规则 | 6.5 | ✅ 已补充 |
| P0-06 | index.html页面布局结构 | 6.6 | ✅ 已补充 |
| P1-01 | 枚举DTO行为方法定义 | 4.2.2-4.2.4 | ✅ 已补充 |
| P1-02 | DesignerNlpService接口方法签名 | 5.4 | ✅ 已补充 |
| P1-03 | 4个Prompt YAML模板内容 | 7.4 | ✅ 已补充 |
| P1-04 | Function Calling函数Parameters Schema | 7.3 | ✅ 已补充 |
| P1-05 | Canvas.js详细交互规格 | 6.2 | ✅ 已补充 |
| P1-06 | Chat.js UI规格和LLM对话流程 | 6.8 | ✅ 已补充 |
| P2-07 | EnumMapping.js前端枚举映射规格 | 附录B.1 | ✅ 已补充 |
| P2-08 | Theme.js主题管理规格 | 附录B.2 | ✅ 已补充 |
| P2-09 | CSS变量主题系统(var.css) | 附录B.3 | ✅ 已补充 |
| P2-10 | 推导服务Java接口方法签名 | 附录B.4 | ✅ 已补充 |
| P2-11 | DataSourceAdapter接口和Mock数据 | 附录B.5 | ✅ 已补充 |
| P2-12 | 完整推导API(/full)响应结构 | 附录B.6 | ✅ 已补充 |
| P2-13 | OptionItem类型定义 | 附录B.7 | ✅ 已补充 |
| P2-14 | 前端模块间依赖关系说明 | 附录B.8 | ✅ 已补充 |
| P2-01 | Tree/Elements/TabManager/Toolbar/ContextMenu组件规格 | 附录C.1 | ✅ 已补充 |
| P2-02 | CSS样式指导 | 附录C.2 | ✅ 已补充 |
| P2-03 | skill.yaml内容 | 附录C.3 | ✅ 已补充 |
| P2-04 | Ooder框架用法说明 | 附录C.4 | ✅ 已补充 |
| P2-05 | WebSocket端点注册方式 | 附录C.5 | ✅ 已补充 |
| P2-06 | WebMvcConfig详细配置 | 附录C.6 | ✅ 已补充 |

---

## 附录B: P2级补充内容

### B.1 EnumMapping.js 前端枚举映射规格 【P2-07】

```javascript
const EnumMapping = {
    ActivityType: {
        TASK: { code: 'TASK', label: '用户任务', icon: 'ri-user-line', color: '#2196F3', shape: 'rect' },
        SERVICE: { code: 'SERVICE', label: '服务任务', icon: 'ri-settings-line', color: '#FF9800', shape: 'rect' },
        SCRIPT: { code: 'SCRIPT', label: '脚本任务', icon: 'ri-code-line', color: '#9C27B0', shape: 'rect' },
        START: { code: 'START', label: '开始节点', icon: 'ri-play-circle-line', color: '#4CAF50', shape: 'circle' },
        END: { code: 'END', label: '结束节点', icon: 'ri-stop-circle-line', color: '#F44336', shape: 'circle' },
        XOR_GATEWAY: { code: 'XOR_GATEWAY', label: '排他网关', icon: 'ri-git-branch-line', color: '#FFC107', shape: 'diamond' },
        AND_GATEWAY: { code: 'AND_GATEWAY', label: '并行网关', icon: 'ri-git-merge-line', color: '#FFC107', shape: 'diamond' },
        OR_GATEWAY: { code: 'OR_GATEWAY', label: '包容网关', icon: 'ri-git-commit-line', color: '#FFC107', shape: 'diamond' },
        SUBPROCESS: { code: 'SUBPROCESS', label: '子流程', icon: 'ri-flow-chart', color: '#673AB7', shape: 'rect-double' },
        LLM_TASK: { code: 'LLM_TASK', label: 'LLM任务', icon: 'ri-robot-line', color: '#E91E63', shape: 'rect-gradient' }
    },
    
    ActivityCategory: {
        HUMAN: { code: 'HUMAN', label: '人工活动', icon: 'ri-user-line' },
        AGENT: { code: 'AGENT', label: 'Agent活动', icon: 'ri-robot-line' },
        SCENE: { code: 'SCENE', label: '场景活动', icon: 'ri-movie-line' }
    },
    
    ActivityPosition: {
        START: { code: 'START', label: '开始位置' },
        END: { code: 'END', label: '结束位置' },
        NORMAL: { code: 'NORMAL', label: '普通位置' }
    },
    
    Implementation: {
        No: { code: 'No', label: '无实现' },
        Tool: { code: 'Tool', label: '工具实现' },
        Subflow: { code: 'Subflow', label: '子流程实现' },
        Outflow: { code: 'Outflow', label: '外部流程实现' },
        Device: { code: 'Device', label: '设备实现' },
        Event: { code: 'Event', label: '事件实现' },
        Service: { code: 'Service', label: '服务实现' }
    },
    
    RouteDirection: {
        FORWARD: { code: 'FORWARD', label: '正向' },
        BACK: { code: 'BACK', label: '退回' }
    },
    
    RouteConditionType: {
        DEFAULT: { code: 'DEFAULT', label: '默认路由' },
        CONDITION: { code: 'CONDITION', label: '条件路由' },
        OTHERWISE: { code: 'OTHERWISE', label: '其他路由' }
    },
    
    DurationUnit: {
        D: { code: 'D', label: '天' },
        H: { code: 'H', label: '小时' },
        M: { code: 'M', label: '分钟' }
    },
    
    PerformType: {
        SINGLE: { code: 'SINGLE', label: '单人办理' },
        MULTIPLE: { code: 'MULTIPLE', label: '多人办理' },
        JOINTSIGN: { code: 'JOINTSIGN', label: '会签' },
        NEEDNOTSELECT: { code: 'NEEDNOTSELECT', label: '无需选择' },
        NOSELECT: { code: 'NOSELECT', label: '不选择' }
    },
    
    getLabel(enumType, code) {
        const enumObj = this[enumType];
        if (enumObj && enumObj[code]) {
            return enumObj[code].label;
        }
        return code;
    },
    
    getOptions(enumType) {
        const enumObj = this[enumType];
        if (!enumObj) return [];
        return Object.values(enumObj).map(item => ({
            value: item.code,
            label: item.label,
            icon: item.icon,
            color: item.color
        }));
    },
    
    fromCode(enumType, code) {
        const enumObj = this[enumType];
        if (enumObj && enumObj[code]) {
            return enumObj[code];
        }
        return null;
    },
    
    getIcon(enumType, code) {
        const item = this.fromCode(enumType, code);
        return item?.icon || 'ri-question-line';
    },
    
    getColor(enumType, code) {
        const item = this.fromCode(enumType, code);
        return item?.color || '#999999';
    },
    
    getShape(enumType, code) {
        const item = this.fromCode(enumType, code);
        return item?.shape || 'rect';
    }
};

if (typeof module !== 'undefined' && module.exports) {
    module.exports = EnumMapping;
}
```

---

### B.2 Theme.js 主题管理规格 【P2-08】

```javascript
class Theme {
    constructor(name) {
        this.name = name || 'light';
    }
    
    apply() {
        document.documentElement.setAttribute('data-theme', this.name);
        localStorage.setItem('theme', this.name);
    }
    
    getCSSVar(key) {
        return getComputedStyle(document.documentElement).getPropertyValue(key).trim();
    }
    
    setCSSVar(key, value) {
        document.documentElement.style.setProperty(key, value);
    }
    
    toggle() {
        this.name = this.name === 'light' ? 'dark' : 'light';
        this.apply();
        return this.name;
    }
    
    isDark() {
        return this.name === 'dark';
    }
}

class ThemeFactory {
    static instance = null;
    
    static get() {
        if (!ThemeFactory.instance) {
            const savedTheme = localStorage.getItem('theme') || 'light';
            ThemeFactory.instance = new Theme(savedTheme);
            ThemeFactory.instance.apply();
        }
        return ThemeFactory.instance;
    }
    
    static toggle() {
        const theme = ThemeFactory.get();
        return theme.toggle();
    }
    
    static setTheme(name) {
        const theme = ThemeFactory.get();
        theme.name = name;
        theme.apply();
        return theme;
    }
    
    static isDark() {
        return ThemeFactory.get().isDark();
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = { Theme, ThemeFactory };
}
```

---

### B.3 CSS变量主题系统 (var.css) 【P2-09】

```css
:root {
    --sidebar-width: 240px;
    --panel-width: 320px;
    --chat-width: 300px;
    --chat-collapsed-width: 40px;
    --toolbar-height: 40px;
    --tab-height: 32px;
    
    --bg-primary: #ffffff;
    --bg-secondary: #f5f5f5;
    --bg-tertiary: #eeeeee;
    --bg-hover: #e8e8e8;
    --bg-active: #e0e0e0;
    
    --text-primary: #333333;
    --text-secondary: #666666;
    --text-tertiary: #999999;
    --text-disabled: #cccccc;
    --text-inverse: #ffffff;
    
    --border-color: #e0e0e0;
    --border-color-light: #eeeeee;
    --border-color-dark: #cccccc;
    
    --primary-color: #2196F3;
    --primary-color-light: #64B5F6;
    --primary-color-dark: #1976D2;
    --danger-color: #F44336;
    --danger-color-light: #EF5350;
    --success-color: #4CAF50;
    --success-color-light: #81C784;
    --warning-color: #FFC107;
    --warning-color-light: #FFD54F;
    --info-color: #00BCD4;
    
    --node-start: #4CAF50;
    --node-start-bg: rgba(76, 175, 80, 0.1);
    --node-end: #F44336;
    --node-end-bg: rgba(244, 67, 54, 0.1);
    --node-task: #2196F3;
    --node-task-bg: rgba(33, 150, 243, 0.1);
    --node-service: #FF9800;
    --node-service-bg: rgba(255, 152, 0, 0.1);
    --node-script: #9C27B0;
    --node-script-bg: rgba(156, 39, 176, 0.1);
    --node-gateway: #FFC107;
    --node-gateway-bg: rgba(255, 193, 7, 0.1);
    --node-subprocess: #673AB7;
    --node-subprocess-bg: rgba(103, 58, 183, 0.1);
    --node-llm: #E91E63;
    --node-llm-bg: rgba(233, 30, 99, 0.1);
    
    --edge-color: #999999;
    --edge-color-hover: #666666;
    --edge-selected: #2196F3;
    --edge-highlight: #4CAF50;
    
    --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.1);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 10px 20px rgba(0, 0, 0, 0.15);
    --shadow-node: 0 2px 8px rgba(0, 0, 0, 0.12);
    --shadow-panel: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    --radius-sm: 4px;
    --radius-md: 8px;
    --radius-lg: 12px;
    --radius-full: 9999px;
    
    --transition-fast: 0.15s ease;
    --transition-normal: 0.25s ease;
    --transition-slow: 0.35s ease;
    
    --font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    --font-mono: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, monospace;
    --font-size-xs: 12px;
    --font-size-sm: 13px;
    --font-size-md: 14px;
    --font-size-lg: 16px;
    --font-size-xl: 18px;
    
    --z-sidebar: 100;
    --z-panel: 200;
    --z-toolbar: 300;
    --z-canvas: 1;
    --z-node: 10;
    --z-edge: 5;
    --z-context-menu: 500;
    --z-modal: 600;
    --z-tooltip: 700;
}

[data-theme="dark"] {
    --bg-primary: #1e1e1e;
    --bg-secondary: #252526;
    --bg-tertiary: #2d2d2d;
    --bg-hover: #3c3c3c;
    --bg-active: #404040;
    
    --text-primary: #e0e0e0;
    --text-secondary: #a0a0a0;
    --text-tertiary: #707070;
    --text-disabled: #505050;
    
    --border-color: #404040;
    --border-color-light: #333333;
    --border-color-dark: #505050;
    
    --primary-color: #64B5F6;
    --primary-color-light: #90CAF9;
    --primary-color-dark: #42A5F5;
    
    --node-start: #66BB6A;
    --node-end: #EF5350;
    --node-task: #64B5F6;
    --node-service: #FFB74D;
    --node-gateway: #FFD54F;
    --node-subprocess: #9575CD;
    --node-llm: #F48FB1;
    
    --edge-color: #666666;
    --edge-color-hover: #888888;
    
    --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.3);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.3);
    --shadow-lg: 0 10px 20px rgba(0, 0, 0, 0.4);
    --shadow-node: 0 2px 8px rgba(0, 0, 0, 0.4);
    --shadow-panel: 0 4px 12px rgba(0, 0, 0, 0.3);
}
```

---

### B.4 推导服务 Java 接口方法签名 【P2-10】

```java
public interface PerformerDerivationService {
    
    /**
     * 根据活动描述推导执行者
     * @param context 上下文信息(包含processId, activityId等)
     * @param activityDesc 活动描述
     * @return 推导结果
     */
    PerformerDerivationResultDTO derive(Map<String, Object> context, String activityDesc);
    
    /**
     * 根据活动描述和候选人列表推导执行者
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @param candidateIds 候选人ID列表
     * @return 推导结果
     */
    PerformerDerivationResultDTO deriveWithCandidates(Map<String, Object> context, String activityDesc, List<String> candidateIds);
    
    /**
     * 搜索候选人
     * @param query 搜索关键词
     * @param filters 过滤条件
     * @return 候选人列表
     */
    List<PerformerCandidate> searchCandidates(String query, Map<String, Object> filters);
}

public interface CapabilityMatchingService {
    
    /**
     * 根据活动描述匹配能力
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @return 匹配结果
     */
    CapabilityMatchingResultDTO match(Map<String, Object> context, String activityDesc);
    
    /**
     * 智能匹配能力(使用LLM)
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @return 匹配结果
     */
    CapabilityMatchingResultDTO smartMatch(Map<String, Object> context, String activityDesc);
    
    /**
     * 根据关键词匹配能力
     * @param activityDesc 活动描述
     * @param keywords 关键词列表
     * @return 匹配结果
     */
    CapabilityMatchingResultDTO matchByKeywords(String activityDesc, List<String> keywords);
    
    /**
     * 构建能力绑定配置
     * @param result 匹配结果
     * @return 绑定配置
     */
    Map<String, Object> buildBindingConfig(CapabilityMatchingResultDTO result);
}

public interface FormMatchingService {
    
    /**
     * 根据活动描述匹配表单
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @return 匹配结果
     */
    FormMatchingResultDTO match(Map<String, Object> context, String activityDesc);
    
    /**
     * 智能匹配表单(使用LLM)
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @return 匹配结果
     */
    FormMatchingResultDTO smartMatch(Map<String, Object> context, String activityDesc);
    
    /**
     * 生成表单Schema
     * @param context 上下文信息
     * @param activityDesc 活动描述
     * @return 生成的表单Schema
     */
    FormSchema generateSchema(Map<String, Object> context, String activityDesc);
    
    /**
     * 根据字段需求匹配表单
     * @param activityDesc 活动描述
     * @param requiredFields 必需字段列表
     * @return 匹配结果
     */
    FormMatchingResultDTO matchByFields(String activityDesc, List<String> requiredFields);
}

public interface PanelRenderService {
    
    /**
     * 构建执行者面板渲染数据
     * @param result 执行者推导结果
     * @return 面板渲染数据
     */
    PanelRenderDataDTO buildPerformerPanel(PerformerDerivationResultDTO result);
    
    /**
     * 构建能力面板渲染数据
     * @param result 能力匹配结果
     * @return 面板渲染数据
     */
    PanelRenderDataDTO buildCapabilityPanel(CapabilityMatchingResultDTO result);
    
    /**
     * 构建表单面板渲染数据
     * @param result 表单匹配结果
     * @return 面板渲染数据
     */
    PanelRenderDataDTO buildFormPanel(FormMatchingResultDTO result);
    
    /**
     * 构建活动面板渲染数据(综合面板)
     * @param performerResult 执行者推导结果
     * @param capabilityResult 能力匹配结果
     * @param formResult 表单匹配结果
     * @return 面板渲染数据
     */
    PanelRenderDataDTO buildActivityPanel(
        PerformerDerivationResultDTO performerResult,
        CapabilityMatchingResultDTO capabilityResult,
        FormMatchingResultDTO formResult
    );
}
```

---

### B.5 DataSourceAdapter 接口和 Mock 数据 【P2-11】

```java
public interface DataSourceAdapter {
    
    /**
     * 获取流程定义
     * @param processId 流程ID
     * @param version 版本号(null表示最新版本)
     * @return 流程定义
     */
    ProcessDef getProcess(String processId, Integer version);
    
    /**
     * 获取流程列表
     * @param category 分类过滤
     * @param status 状态过滤
     * @param page 页码
     * @param size 每页数量
     * @return 流程列表
     */
    List<ProcessDef> getProcessList(String category, String status, int page, int size);
    
    /**
     * 获取流程树
     * @return 流程树结构
     */
    List<Map<String, Object>> getProcessTree();
    
    /**
     * 保存流程
     * @param processDef 流程定义
     * @return 保存后的流程定义
     */
    ProcessDef saveProcess(ProcessDef processDef);
    
    /**
     * 删除流程
     * @param processId 流程ID
     */
    void deleteProcess(String processId);
    
    /**
     * 检查数据源是否可用
     * @return 是否可用
     */
    boolean isAvailable();
    
    /**
     * 获取数据源名称
     * @return 数据源名称
     */
    String getName();
}
```

**Mock数据示例** (use-real-data: false 时使用):

```java
@Component
@ConditionalOnProperty(name = "datasource.bpm.use-real-data", havingValue = "false")
public class MockBpmDataSourceAdapter extends AbstractDataSourceAdapter {
    
    private static final List<ProcessDef> MOCK_PROCESSES = List.of(
        ProcessDef.builder()
            .processDefId("leave_approval")
            .name("请假审批流程")
            .description("员工请假申请审批流程")
            .classification("办公流程")
            .systemCode("oa")
            .version(1)
            .status("PUBLISHED")
            .creatorName("系统管理员")
            .createdTime("2026-01-01T00:00:00Z")
            .activities(List.of(
                ActivityDef.builder()
                    .activityDefId("act_submit")
                    .name("提交申请")
                    .activityType("TASK")
                    .position("START")
                    .activityCategory("HUMAN")
                    .positionCoord(Map.of("x", 50, "y", 200))
                    .build(),
                ActivityDef.builder()
                    .activityDefId("act_manager_approve")
                    .name("经理审批")
                    .activityType("TASK")
                    .position("NORMAL")
                    .activityCategory("HUMAN")
                    .positionCoord(Map.of("x", 250, "y", 200))
                    .build(),
                ActivityDef.builder()
                    .activityDefId("act_hr_record")
                    .name("HR备案")
                    .activityType("TASK")
                    .position("END")
                    .activityCategory("HUMAN")
                    .positionCoord(Map.of("x", 450, "y", 200))
                    .build()
            ))
            .routes(List.of(
                Map.of("routeDefId", "route_1", "from", "act_submit", "to", "act_manager_approve"),
                Map.of("routeDefId", "route_2", "from", "act_manager_approve", "to", "act_hr_record")
            ))
            .build(),
        ProcessDef.builder()
            .processDefId("expense_reimbursement")
            .name("费用报销流程")
            .description("员工费用报销审批流程")
            .classification("财务流程")
            .systemCode("finance")
            .version(2)
            .status("PUBLISHED")
            .build()
    );
    
    @Override
    public ProcessDef getProcess(String processId, Integer version) {
        return MOCK_PROCESSES.stream()
            .filter(p -> p.getProcessDefId().equals(processId))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public List<ProcessDef> getProcessList(String category, String status, int page, int size) {
        return MOCK_PROCESSES;
    }
    
    @Override
    public List<Map<String, Object>> getProcessTree() {
        return List.of(
            Map.of("id", "leave_approval", "name", "请假审批流程", "type", "process", "status", "PUBLISHED"),
            Map.of("id", "expense_reimbursement", "name", "费用报销流程", "type", "process", "status", "PUBLISHED")
        );
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public String getName() {
        return "MockDataSource";
    }
}
```

---

### B.6 完整推导 API (/full) 响应结构 【P2-12】

```json
{
    "performerDerivation": {
        "success": true,
        "status": "SUCCESS",
        "recommendedPerformers": [
            {
                "candidateId": "user_001",
                "candidateName": "张经理",
                "candidateType": "USER",
                "department": "技术部",
                "role": "部门经理",
                "matchScore": 0.95,
                "matchReason": "根据活动描述'经理审批'匹配到部门经理角色"
            }
        ],
        "confidence": 0.92,
        "derivationReason": "活动描述中明确提到'经理审批'，系统自动匹配到部门经理角色",
        "derivationMethod": "KEYWORD",
        "candidates": [],
        "fallbackSuggestion": null,
        "errorMessage": null
    },
    "capabilityMatching": {
        "success": true,
        "status": "SUCCESS",
        "recommendedCapabilities": [
            {
                "capabilityId": "cap_approval",
                "capabilityName": "审批能力",
                "capabilityType": "BUSINESS",
                "description": "提供审批流程处理能力",
                "matchScore": 0.98,
                "isRecommended": true
            },
            {
                "capabilityId": "cap_notification",
                "capabilityName": "消息通知",
                "capabilityType": "COMMUNICATION",
                "description": "发送审批结果通知",
                "matchScore": 0.85,
                "isRecommended": true
            }
        ],
        "matchScores": {
            "cap_approval": 0.98,
            "cap_notification": 0.85
        },
        "bindingConfig": {
            "autoBind": true,
            "parameters": {
                "notifyOnApprove": true,
                "notifyOnReject": true
            }
        },
        "derivationReason": "审批类活动需要审批能力和消息通知能力",
        "errorMessage": null
    },
    "formMatching": {
        "success": true,
        "status": "SUCCESS",
        "recommendedForms": [
            {
                "formId": "form_leave_apply",
                "formName": "请假申请表",
                "formType": "CUSTOM",
                "formUrl": "/forms/leave-apply",
                "matchScore": 0.96,
                "isRecommended": true
            }
        ],
        "generatedSchema": null,
        "fieldMappings": {
            "请假类型": "leaveType",
            "开始时间": "startTime",
            "结束时间": "endTime",
            "请假原因": "reason"
        },
        "matchScores": {
            "form_leave_apply": 0.96
        },
        "derivationReason": "根据流程上下文匹配到请假申请表单",
        "errorMessage": null
    }
}
```

---

### B.7 OptionItem 类型定义 【P2-13】

**后端DTO定义**:

```java
@Data
public class OptionItem {
    private String value;       // 选项值
    private String label;       // 显示标签
    private Boolean disabled;   // 是否禁用(可选，默认false)
    private String group;       // 分组名称(可选)
    private String icon;        // 图标(可选)
    private String description; // 描述(可选)
    
    public static OptionItem of(String value, String label) {
        OptionItem item = new OptionItem();
        item.setValue(value);
        item.setLabel(label);
        item.setDisabled(false);
        return item;
    }
    
    public static OptionItem of(String value, String label, String group) {
        OptionItem item = of(value, label);
        item.setGroup(group);
        return item;
    }
}
```

**前端JavaScript定义**:

```javascript
class OptionItem {
    constructor(value, label, options = {}) {
        this.value = value;
        this.label = label;
        this.disabled = options.disabled || false;
        this.group = options.group || null;
        this.icon = options.icon || null;
        this.description = options.description || null;
    }
    
    static fromJSON(json) {
        return new OptionItem(json.value, json.label, json);
    }
    
    toJSON() {
        return {
            value: this.value,
            label: this.label,
            disabled: this.disabled,
            group: this.group,
            icon: this.icon,
            description: this.description
        };
    }
}
```

---

### B.8 前端模块间依赖关系说明 【P2-14】

```
模块依赖图 (按加载顺序):

┌─────────────────────────────────────────────────────────────────────┐
│                        第一层: 无依赖模块                            │
├─────────────────────────────────────────────────────────────────────┤
│  Theme.js        → 无依赖（最先加载，提供主题服务）                   │
│  EnumMapping.js  → 无依赖（提供枚举映射服务）                        │
│  DataAdapter.js  → 无依赖（纯工具函数，前后端数据转换）              │
│  Api.js          → 无依赖（HTTP客户端封装）                          │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        第二层: 基础依赖模块                          │
├─────────────────────────────────────────────────────────────────────┤
│  Store.js        → 依赖 Api.js（状态管理，需要API进行持久化）        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        第三层: 数据模型模块                          │
├─────────────────────────────────────────────────────────────────────┤
│  ActivityDef.js  → 依赖 DataAdapter.js                              │
│  RouteDef.js     → 依赖 DataAdapter.js                              │
│  ProcessDef.js   → 依赖 ActivityDef.js, RouteDef.js, DataAdapter.js │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        第四层: UI组件模块                            │
├─────────────────────────────────────────────────────────────────────┤
│  PanelManager.js → 依赖 Store.js（面板需要读写状态）                 │
│  Canvas.js       → 依赖 Store.js（画布需要渲染流程数据）             │
│  Chat.js         → 依赖 Store.js, Api.js（对话需要访问状态和API）   │
│  Tree.js         → 依赖 Store.js, Api.js（流程树需要加载流程列表）  │
│  Elements.js     → 依赖 Store.js（拖拽元素需要添加到流程）           │
│  TabManager.js   → 依赖 Store.js（标签页管理需要切换流程）           │
│  ContextMenu.js  → 依赖 Store.js, Canvas.js（右键菜单操作画布）     │
│  Toolbar.js      → 依赖 Store.js, Canvas.js（工具栏操作画布）       │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        第五层: 应用入口模块                          │
├─────────────────────────────────────────────────────────────────────┤
│  App.js          → 依赖以上所有模块（协调器，最后加载）              │
└─────────────────────────────────────────────────────────────────────┘
```

**模块初始化顺序** (在App.js中):

```javascript
// 1. 初始化主题（无依赖）
this.theme = ThemeFactory.get();

// 2. 初始化枚举映射（无依赖）
this.enumMapping = EnumMapping;

// 3. 初始化API客户端（无依赖）
this.api = new Api();

// 4. 初始化状态管理器（依赖Api）
this.store = new Store(this.api);

// 5. 初始化画布（依赖Store）
this.canvas = new Canvas(this.store);

// 6. 初始化面板管理器（依赖Store）
this.panelManager = new PanelManager(this.store);

// 7. 初始化AI对话（依赖Store, Api）
this.chat = new Chat(this.store, this.api);

// 8. 初始化其他组件...
```

---

## 附录C: 原P2级补充内容

### C.1 Tree/Elements/TabManager/Toolbar/ContextMenu 组件规格 【P2-01】

#### C.1.1 Tree.js (流程树导航)

```javascript
class Tree {
    constructor(container, store, api) {
        this.container = container;
        this.store = store;
        this.api = api;
        this.selectedId = null;
    }
    
    // 公开方法
    load() { ... }                    // 加载流程树
    refresh() { ... }                 // 刷新流程树
    select(processId) { ... }         // 选中节点
    expand(nodeId) { ... }            // 展开节点
    collapse(nodeId) { ... }          // 折叠节点
    
    // 事件
    on('select', (processId) => {})   // 节点选中事件
    on('dblclick', (processId) => {}) // 双击打开事件
}
```

#### C.1.2 Elements.js (拖拽元素面板)

```javascript
class Elements {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.elements = [];
    }
    
    // 公开方法
    loadElements() { ... }            // 加载可拖拽元素
    setFilter(type) { ... }           // 设置过滤类型
    
    // 元素类型
    elements = [
        { type: 'TASK', label: '用户任务', icon: 'ri-user-line' },
        { type: 'SERVICE', label: '服务任务', icon: 'ri-settings-line' },
        { type: 'XOR_GATEWAY', label: '排他网关', icon: 'ri-git-branch-line' },
        { type: 'AND_GATEWAY', label: '并行网关', icon: 'ri-git-merge-line' },
        { type: 'SUBPROCESS', label: '子流程', icon: 'ri-flow-chart' },
        { type: 'LLM_TASK', label: 'LLM任务', icon: 'ri-robot-line' }
    ];
}
```

#### C.1.3 TabManager.js (多标签页管理)

```javascript
class TabManager {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.tabs = [];
        this.activeTabId = null;
    }
    
    // 公开方法
    openTab(processId, processName) { ... }  // 打开标签页
    closeTab(tabId) { ... }                   // 关闭标签页
    switchTab(tabId) { ... }                  // 切换标签页
    getActiveTab() { ... }                    // 获取当前标签页
    getTab(tabId) { ... }                     // 获取指定标签页
    
    // 事件
    on('open', (tab) => {})          // 标签页打开事件
    on('close', (tabId) => {})       // 标签页关闭事件
    on('switch', (tab) => {})        // 标签页切换事件
}
```

#### C.1.4 Toolbar.js (工具栏)

```javascript
class Toolbar {
    constructor(container, store, canvas) {
        this.container = container;
        this.store = store;
        this.canvas = canvas;
    }
    
    // 公开方法
    enableAction(actionId) { ... }    // 启用操作按钮
    disableAction(actionId) { ... }   // 禁用操作按钮
    updateZoomLevel(level) { ... }    // 更新缩放显示
    
    // 操作列表
    actions = {
        save: { icon: 'ri-save-line', shortcut: 'Ctrl+S' },
        undo: { icon: 'ri-arrow-go-back-line', shortcut: 'Ctrl+Z' },
        redo: { icon: 'ri-arrow-go-forward-line', shortcut: 'Ctrl+Y' },
        delete: { icon: 'ri-delete-bin-line', shortcut: 'Delete' },
        zoomIn: { icon: 'ri-zoom-in-line' },
        zoomOut: { icon: 'ri-zoom-out-line' },
        fitScreen: { icon: 'ri-fullscreen-line' },
        theme: { icon: 'ri-moon-line' }
    };
}
```

#### C.1.5 ContextMenu.js (右键菜单)

```javascript
class ContextMenu {
    constructor(container, store, canvas) {
        this.container = container;
        this.store = store;
        this.canvas = canvas;
        this.visible = false;
        this.position = { x: 0, y: 0 };
    }
    
    // 公开方法
    show(x, y, items) { ... }         // 显示菜单
    hide() { ... }                    // 隐藏菜单
    setItems(items) { ... }           // 设置菜单项
    
    // 菜单项类型
    getMenuItems(context) {
        if (context.type === 'node') {
            return [
                { label: '编辑', action: 'edit', icon: 'ri-edit-line' },
                { label: '复制', action: 'copy', icon: 'ri-file-copy-line' },
                { label: '删除', action: 'delete', icon: 'ri-delete-bin-line' }
            ];
        } else if (context.type === 'edge') {
            return [
                { label: '编辑条件', action: 'editCondition', icon: 'ri-edit-line' },
                { label: '删除', action: 'delete', icon: 'ri-delete-bin-line' }
            ];
        } else {
            return [
                { label: '粘贴', action: 'paste', icon: 'ri-clipboard-line' },
                { label: '全选', action: 'selectAll', icon: 'ri-checkbox-line' }
            ];
        }
    }
}
```

---

### C.2 CSS样式指导 【P2-02】

#### C.2.1 关键组件样式要求

**节点样式**:
```css
.bpm-node {
    cursor: move;
    transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.bpm-node:hover {
    transform: scale(1.02);
    box-shadow: var(--shadow-node);
}

.bpm-node.selected {
    outline: 2px solid var(--primary-color);
    outline-offset: 2px;
}

.bpm-node.dragging {
    opacity: 0.8;
    cursor: grabbing;
}
```

**面板样式**:
```css
.panel-drawer {
    border-bottom: 1px solid var(--border-color-light);
}

.panel-drawer:last-child {
    border-bottom: none;
}

.drawer-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    cursor: pointer;
    background: var(--bg-secondary);
    transition: background var(--transition-fast);
}

.drawer-header:hover {
    background: var(--bg-hover);
}

.drawer-content {
    padding: 16px;
    max-height: 0;
    overflow: hidden;
    transition: max-height var(--transition-normal);
}

.drawer-content.expanded {
    max-height: 1000px;
}
```

#### C.2.2 响应式断点

```css
@media (max-width: 1600px) {
    :root {
        --sidebar-width: 200px;
        --panel-width: 280px;
    }
}

@media (max-width: 1280px) {
    .chat {
        position: absolute;
        right: 0;
        transform: translateX(calc(100% - var(--chat-collapsed-width)));
    }
    
    .chat.expanded {
        transform: translateX(0);
    }
}
```

---

### C.3 skill.yaml 内容 【P2-03】

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: bpm-designer
  name: BPM流程设计器
  version: "3.1.0"
  description: AI原生BPM流程可视化设计器，支持自然语言交互和智能推导
  author: Ooder Team
  license: Ooder License
  repository: https://gitee.com/ooderCN/ade
  keywords:
    - bpm
    - workflow
    - designer
    - ai
    - llm

spec:
  skillForm: PROVIDER
  skillCategory: SERVICE
  sceneType: AUTO
  
  purposes:
    - WORKFLOW
    - TEAM
    - PERSISTENT
  
  capability:
    category: biz
    level: advanced
  
  capabilities:
    - id: flow-design
      name: 流程设计
      description: 可视化流程设计，支持拖拽和自然语言两种模式
      category: bpm
      autoBind: true
      inputs:
        - name: description
          type: string
          description: 流程描述
      outputs:
        - name: processDef
          type: ProcessDef
          description: 流程定义
    
    - id: form-binding
      name: 表单绑定
      description: 流程节点表单绑定和生成
      category: bpm
      autoBind: false
    
    - id: performer-derivation
      name: 执行者推导
      description: AI辅助推导活动执行者
      category: ai
      autoBind: true
    
    - id: capability-matching
      name: 能力匹配
      description: AI辅助匹配业务能力
      category: ai
      autoBind: true
    
    - id: nlp-interaction
      name: 自然语言交互
      description: 通过自然语言创建和编辑流程
      category: ai
      autoBind: true
      
  dependencies:
    - id: bpmserver
      name: BPM流程引擎
      version: ">=3.0.0"
      required: true
      endpoint: http://localhost:8084/bpm
    
    - id: llm-service
      name: LLM服务
      required: true
      config:
        provider: aliyun
        model: qwen-plus
      
  configSchema:
    type: object
    properties:
      serverUrl:
        type: string
        title: BPM服务器地址
        default: "http://localhost:8084"
        description: BPM流程引擎服务地址
      
      storagePath:
        type: string
        title: 流程定义存储路径
        default: "./data/bpm"
        description: 本地流程定义存储路径
      
      llmEnabled:
        type: boolean
        title: 启用LLM功能
        default: true
        description: 是否启用AI辅助功能
      
      autoSave:
        type: boolean
        title: 自动保存
        default: true
        description: 是否自动保存流程定义
      
      autoSaveDelay:
        type: integer
        title: 自动保存延迟(ms)
        default: 1000
        description: 自动保存延迟时间
      
    required:
      - serverUrl
      
  estimatedResources:
    cpu: "100m"
    memory: "256Mi"
    
  healthCheck:
    path: /actuator/health
    interval: 30s
    timeout: 5s
```

---

### C.4 Ooder框架用法说明 【P2-04】

Ooder是一个注解驱动的UI组件框架，核心特性：

#### C.4.1 核心API

```javascript
// 组件注册
Ooder.register('my-component', {
    template: '<div class="my-component">{{content}}</div>',
    props: ['content'],
    methods: {
        onClick() { this.$emit('click'); }
    }
});

// 组件使用
<my-component content="Hello" @click="handleClick"></my-component>
```

#### C.4.2 注解驱动方式

```javascript
// @Component 注解
@Component({
    selector: 'activity-panel',
    template: './activity-panel.html',
    styles: ['./activity-panel.css']
})
class ActivityPanel {
    @Input() activity;
    @Output() change = new EventEmitter();
    
    @Watch('activity.name')
    onNameChange(newVal, oldVal) {
        console.log(`Name changed from ${oldVal} to ${newVal}`);
    }
}
```

#### C.4.3 面板插件注册机制

```javascript
// 注册面板插件
PanelPluginManager.register({
    id: 'timing-panel',
    name: '时限配置',
    category: 'business',
    applicable: (activity) => activity.activityType === 'TASK',
    schema: TimingPanelSchema,
    component: TimingPanelComponent
});
```

---

### C.5 WebSocket端点注册方式 【P2-05】

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(derivationWebSocketHandler(), "/ws/derivation")
            .setAllowedOrigins("*")
            .withSockJS();
    }
    
    @Bean
    public DerivationWebSocketHandler derivationWebSocketHandler() {
        return new DerivationWebSocketHandler();
    }
}

// CORS处理
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600);
    }
}
```

---

### C.6 WebMvcConfig详细配置 【P2-06】

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("X-Request-Id", "X-Response-Time")
            .allowCredentials(true)
            .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/designer/**")
            .addResourceLocations("classpath:/static/designer/")
            .setCachePeriod(0);
        
        // 图标资源
        registry.addResourceHandler("/lib/**")
            .addResourceLocations("classpath:/static/designer/lib/")
            .setCachePeriod(31536000); // 1年缓存
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/designer/index.html");
        registry.addViewController("/designer").setViewName("redirect:/designer/index.html");
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // FastJSON2转换器
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        JSONWriter.Feature[] features = {
            JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.WriteNullListAsEmpty,
            JSONWriter.Feature.WriteNullStringAsEmpty,
            JSONWriter.Feature.WriteDateUseDateFormat
        };
        converter.setFastJsonWriterFeatures(features);
        converters.add(0, converter);
    }
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter(corsConfigurationSource()));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

*文档生成时间: 2026-04-13*  
*文档版本: 3.2.0*  
*测试平台: Trae Solo Web (字节跳动)*  
*文档目的: AI编程平台长任务能力极限测试 - 一文到底*
