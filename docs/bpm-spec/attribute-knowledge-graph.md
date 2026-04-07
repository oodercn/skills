# BPM 属性知识图谱

本文档整理了XPDL规范中流程、活动、路由三个核心实体的完整属性定义。

---

## 一、流程属性 (ProcessDef)

### 1.1 基本属性

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| processDefId | String | 是 | 流程定义唯一标识 |
| name | String | 是 | 流程名称 |
| description | String | 否 | 流程描述 |
| category | String | 否 | 流程分类（HR/FINANCE/IT/OPERATION） |
| accessLevel | Enum | 是 | 访问级别（Public/Private/Block） |
| version | Integer | 否 | 版本号 |
| status | Enum | 否 | 发布状态（UNDER_REVISION/RELEASED/UNDER_TEST） |
| createdTime | DateTime | 否 | 创建时间 |
| updatedTime | DateTime | 否 | 更新时间 |
| creator | String | 否 | 创建者 |

### 1.2 版本信息

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| version | Integer | 否 | 版本号 |
| status | Enum | 否 | 发布状态 |
| deadline | Integer | 否 | 完成期限（天） |

### 1.3 表单配置

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| idType | Enum | 否 | 标识类型（GLOBAL/ACTIVITY） |
| lockStrategy | Enum | 否 | 锁定策略（LOCK/NO_LOCK） |
| autoSave | Boolean | 否 | 自动保存 |

### 1.4 监听器配置

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| listeners | List | 否 | 监听器列表 |
| listeners[].name | String | 是 | 监听器名称 |
| listeners[].event | Enum | 是 | 事件类型（STARTED/COMPLETED/TERMINATED） |
| listeners[].className | String | 是 | 实现类 |

### 1.5 扩展属性

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| extendedAttributes | Map | 否 | 扩展属性键值对 |

---

## 二、活动属性 (ActivityDef)

### 2.1 基本属性

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| activityDefId | String | 是 | 活动定义唯一标识 |
| name | String | 是 | 活动名称 |
| description | String | 否 | 活动描述 |
| position | Enum | 是 | 活动位置（START/NORMAL/END） |
| activityType | Enum | 是 | 活动类型 |
| activityCategory | Enum | 是 | 活动分类（HUMAN/AGENT/SCENE） |
| implementation | Enum | 是 | 实现方式 |
| execClass | String | 否 | 执行类 |

### 2.2 活动类型 (activityType)

| 值 | 说明 |
|----|------|
| START | 开始活动 |
| END | 结束活动 |
| TASK | 用户任务 |
| SERVICE | 服务任务 |
| SCRIPT | 脚本任务 |
| DEVICE | 设备任务 |
| EVENT | 事件任务 |
| LLM_TASK | LLM任务 |
| AGENT_TASK | Agent任务 |
| COORDINATOR | 协调器 |
| SCENE | 场景 |
| SUBPROCESS | 子流程 |
| CALL_ACTIVITY | 调用活动 |
| ACTIVITY_BLOCK | 活动块 |

### 2.3 实现方式 (implementation)

| 值 | 说明 | 对应扩展面板 |
|----|------|--------------|
| IMPL_NO | 手动活动 | ActivityDefRight（权限配置） |
| IMPL_TOOL | 自动活动 | ActivityDefTool（工具配置） |
| IMPL_SUBFLOW | 子流程活动 | ActivityDefSubflow（子流程配置） |
| IMPL_OUTFLOW | 跳转流程活动 | ActivityDefOutflow（跳转配置） |
| IMPL_DEVICE | 设备活动 | ActivityDefDevice（设备配置） |
| IMPL_EVENT | 事件活动 | ActivityDefEvent（事件配置） |
| IMPL_SERVICE | 服务活动 | ActivityDefService（服务配置） |

### 2.4 位置坐标

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| positionCoord.x | Integer | 是 | X坐标 |
| positionCoord.y | Integer | 是 | Y坐标 |

### 2.5 时限配置 (Timing)

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 时间限制 |
| alertTime | Integer | 否 | 报警时间 |
| durationUnit | Enum | 否 | 时间单位（Y/M/D/H/m/s/W） |
| deadlineOperation | Enum | 否 | 到期处理（DEFAULT/DELAY/TAKEBACK/SURROGATE） |

### 2.6 路由配置 (Routing)

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| join | Enum | 否 | 等待合并（DEFAULT/AND/XOR） |
| split | Enum | 否 | 并行处理（DEFAULT/AND/XOR） |
| canRouteBack | Boolean | 否 | 允许退回 |
| routeBackMethod | Enum | 否 | 退回路径（DEFAULT/LAST/ANY/SPECIFY） |
| canSpecialSend | Boolean | 否 | 允许特送 |
| specialSendScope | Enum | 否 | 特送范围（DEFAULT/ALL/PERFORMERS） |
| canReSend | Boolean | 否 | 允许补发 |

### 2.7 权限配置 (ActivityDefRight) - IMPL_NO

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| performType | Enum | 是 | 办理类型（SINGLE/MULTIPLE/JOINTSIGN/NEEDNOTSELECT/NOSELECT） |
| performSequence | Enum | 是 | 办理顺序（FIRST/SEQUENCE/MEANWHILE/AUTOSIGN） |
| performerSelectedAtt | List | 否 | 办理人配置列表 |
| performerSelectedAtt[].type | Enum | 是 | 类型（ROLE/USER/FORMULA） |
| performerSelectedAtt[].value | String | 是 | 值 |
| readerSelectedAtt | List | 否 | 阅办人配置列表 |
| canInsteadSign | Boolean | 否 | 允许代签 |
| canTakeBack | Boolean | 否 | 允许收回 |
| movePerformerTo | Enum | 否 | 办理后权限转移（PERFORMER/SPONSOR/READER/HISTORYPERFORMER/NORIGHT） |
| moveReaderTo | Enum | 否 | 阅办后权限转移（READER/HISTORYREADER/NORIGHT） |

### 2.8 设备配置 (ActivityDefDevice) - IMPL_DEVICE

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| commandExecType | Enum | 是 | 命令执行方式（SYNC/ASYNC） |
| commandRetry | Enum | 否 | 命令重试方式（NONE/AUTO/MANUAL） |
| commandExecRetryTimes | Integer | 否 | 命令重试次数 |
| commandDelayTime | Integer | 否 | 命令等待时间（秒） |
| commandSendTimeout | Integer | 否 | 命令超时等待时间（秒） |
| performSequence | Enum | 否 | 设备执行顺序（FIRST/SEQUENCE/MEANWHILE） |
| performType | Enum | 否 | 设备执行方式（SINGLE/MULTIPLE） |
| endpointSelectedId | String | 否 | 设备端点ID |
| commandSelectedId | String | 否 | 命令ID |
| canOffLineSend | Boolean | 否 | 允许离线发送 |
| canTakeBack | Boolean | 否 | 允许收回命令 |
| canReSend | Boolean | 否 | 允许重新发送 |

### 2.9 服务配置 (ActivityDefService) - IMPL_SERVICE

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| url | String | 是 | 服务URL |
| method | Enum | 是 | HTTP方法（GET/POST/PUT/DELETE） |
| requestType | Enum | 否 | 请求类型（JSON/XML/FORM） |
| responseType | Enum | 否 | 响应类型（JSON/XML/TEXT） |
| serviceParams | String | 否 | 服务参数（JSON格式） |
| serviceSelectedID | String | 否 | 服务选择ID |

### 2.10 事件配置 (ActivityDefEvent) - IMPL_EVENT

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| deviceEvent | Enum | 是 | 设备事件类型（ON_DATA_CHANGE/ON_STATUS_CHANGE/ON_ALARM/ON_TIMER） |
| endpointSelectedId | String | 否 | 端点选择ID |
| durationUnit | Enum | 否 | 时间单位（D/H/m/s） |
| alertTime | Integer | 否 | 报警时间 |
| deadLineOperation | Enum | 否 | 到期处理（DEFAULT/DELAY/TAKEBACK） |
| attributeName | String | 否 | 属性名称 |

### 2.11 子流程配置 (ActivityDefSubflow) - IMPL_SUBFLOW/IMPL_OUTFLOW

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| subFlowId | String | 是 | 子流程ID |
| iswaitreturn | Boolean | 否 | 等待返回 |
| paramMapping | List | 否 | 参数映射 |
| paramMapping[].source | String | 是 | 源参数 |
| paramMapping[].target | String | 是 | 目标参数 |

### 2.12 Agent配置 (AgentDef) - AGENT分类

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| agentType | Enum | 是 | Agent类型（LLM/TASK/EVENT/HYBRID/COORDINATOR/TOOL） |
| scheduleStrategy | Enum | 否 | 调度策略（SEQUENTIAL/PARALLEL/CONDITIONAL/ROUND_ROBIN/PRIORITY） |
| collaborationMode | Enum | 否 | 协作模式（SOLO/HIERARCHICAL/PEER/DEBATE/VOTING） |
| llmConfig.model | Enum | 否 | 模型（gpt-4/gpt-4-turbo/gpt-3.5-turbo/claude-3） |
| llmConfig.temperature | Float | 否 | 温度（0-2） |
| llmConfig.maxTokens | Integer | 否 | 最大Token |
| llmConfig.enableFunctionCalling | Boolean | 否 | 启用函数调用 |
| llmConfig.enableStreaming | Boolean | 否 | 启用流式输出 |

### 2.13 场景配置 (SceneDef) - SCENE分类

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sceneId | String | 是 | 场景ID |
| name | String | 是 | 场景名称 |
| sceneType | Enum | 否 | 场景类型（FORM/LIST/DASHBOARD/CUSTOM） |
| pageAgent.agentId | String | 否 | Agent ID |
| pageAgent.pageId | String | 否 | 页面ID |
| pageAgent.templatePath | String | 否 | 模板路径 |
| storage.type | Enum | 否 | 存储类型（VFS/SQL/HYBRID） |
| storage.vfsPath | String | 否 | VFS路径 |

---

## 三、路由属性 (RouteDef)

### 3.1 基本属性

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| routeDefId | String | 是 | 路由定义唯一标识 |
| name | String | 否 | 路由名称 |
| description | String | 否 | 路由描述 |
| from | String | 是 | 起始活动ID |
| to | String | 是 | 目标活动ID |
| routeOrder | Integer | 否 | 路由顺序 |

### 3.2 路由方向

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| routeDirection | Enum | 否 | 路由方向（FORWARD/BACK） |

### 3.3 路由条件

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| routeConditionType | Enum | 否 | 条件类型（CONDITION/OTHERWISE） |
| condition | String | 否 | 条件表达式 |
| priority | Integer | 否 | 优先级 |
| isDefault | Boolean | 否 | 是否默认路由 |

### 3.4 显示配置

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| showLabel | Boolean | 否 | 显示标签 |
| labelPosition | Enum | 否 | 标签位置（middle/start/end） |
| splitType | Enum | 否 | 分裂类型（AND/XOR） |
| joinType | Enum | 否 | 合并类型（AND/XOR） |

---

## 四、属性与面板映射关系

### 4.1 流程面板映射

| Tab ID | 属性组 | 说明 |
|--------|--------|------|
| basic | 基本属性 | name, description, category, accessLevel |
| version | 版本信息 | version, status, deadline |
| form | 表单配置 | idType, lockStrategy, autoSave |
| listener | 监听器配置 | listeners[] |

### 4.2 活动面板映射

| Tab ID | 属性组 | 说明 |
|--------|--------|------|
| basic | 基本属性 | name, description, activityType, activityCategory, implementation, execClass |
| timing | 时限配置 | limit, alertTime, durationUnit, deadlineOperation |
| route | 路由配置 | join, split, canRouteBack, routeBackMethod, canSpecialSend, specialSendScope, canReSend |
| right | 权限配置 | performType, performSequence, performerSelectedAtt, readerSelectedAtt, canInsteadSign, canTakeBack |
| device | 设备配置 | commandExecType, commandRetry, endpointSelectedId, commandSelectedId |
| service | 服务配置 | url, method, requestType, responseType, serviceParams |
| event | 事件配置 | deviceEvent, endpointSelectedId, durationUnit, alertTime |
| subflow | 子流程配置 | subFlowId, iswaitreturn, paramMapping |
| agent | Agent配置 | agentType, scheduleStrategy, collaborationMode, llmConfig |
| scene | 场景配置 | sceneId, sceneType, pageAgent, storage |

### 4.3 路由面板映射

| Tab ID | 属性组 | 说明 |
|--------|--------|------|
| basic | 基本属性 | name, from, to, routeDirection, routeOrder |
| condition | 条件配置 | condition, routeConditionType, priority, isDefault |
| display | 显示配置 | showLabel, labelPosition, splitType, joinType |

---

## 五、YAML映射关系

### 5.1 流程YAML结构

```yaml
apiVersion: bpm.ooder.net/v1
kind: ProcessDef
metadata:
  id: {processDefId}
  name: {name}
  description: {description}
  classification: {category}
spec:
  accessLevel: {accessLevel}
  version:
    version: {version}
    publicationStatus: {status}
  form:
    idType: {idType}
    lockStrategy: {lockStrategy}
  activities:
    - id: {activityDefId}
      name: {name}
      position: {position}
      category: {activityCategory}
      implementation: {implementation}
      positionCoord:
        x: {x}
        y: {y}
      timing:
        limit: {limit}
        durationUnit: {durationUnit}
      routing:
        join: {join}
        split: {split}
      right:
        performType: {performType}
      device:
        commandExecType: {commandExecType}
      service:
        url: {url}
      agentConfig:
        agentType: {agentType}
  routes:
    - id: {routeDefId}
      name: {name}
      connection:
        from: {from}
        to: {to}
      condition:
        routeCondition: {condition}
```

---

## 六、后端Bean校验规则

### 6.1 流程定义校验

- processDefId: 必填，格式校验
- name: 必填，长度1-100
- accessLevel: 必填，枚举值校验
- version: 非负整数
- status: 枚举值校验

### 6.2 活动定义校验

- activityDefId: 必填，格式校验
- name: 必填，长度1-100
- position: 必填，枚举值校验
- activityType: 必填，枚举值校验
- activityCategory: 必填，枚举值校验
- implementation: 必填，枚举值校验
- positionCoord.x/y: 非负整数

### 6.3 路由定义校验

- routeDefId: 必填，格式校验
- from: 必填，引用存在的活动ID
- to: 必填，引用存在的活动ID
- condition: 非空时校验表达式语法
