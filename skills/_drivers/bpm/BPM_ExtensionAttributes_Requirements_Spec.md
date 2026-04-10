# BPM流程引擎扩展属性需求规格说明书

## 版本信息
- **版本**: v1.0
- **日期**: 2026-04-09
- **状态**: 草案

---

## 1. 引言

### 1.1 目的
本文档定义BPM流程引擎扩展属性的完整规范，确保前端设计器、后端服务和客户端读取的数据一致性。

### 1.2 范围
涵盖流程级、活动级、路由级的所有扩展属性，包括BPD、WORKFLOW、RIGHT、FORM、SERVICE、EVENT、DEVICE等属性组。

### 1.3 术语定义

| 术语 | 定义 |
|------|------|
| **BPD** | Business Process Definition，基础流程定义属性组 |
| **WORKFLOW** | 工作流引擎属性组 |
| **RIGHT** | 权限引擎属性组 |
| **XPDL** | XML Process Definition Language，流程定义交换格式 |
| **ExtendedAttribute** | XPDL中的扩展属性元素 |

---

## 2. 属性规范总览

### 2.1 属性分类

```
扩展属性
├── 流程级属性 (WorkflowProcess)
│   ├── BPD标准属性
│   ├── 开始/结束节点坐标
│   ├── 监听器 (Listeners)
│   └── 权限组 (RightGroups)
│
├── 活动级属性 (Activity)
│   ├── 视觉位置 (XOffset, YOffset, ParticipantID)
│   ├── WORKFLOW属性组
│   ├── RIGHT属性组
│   ├── FORM属性组
│   ├── SERVICE属性组
│   ├── DEVICE属性组
│   └── 块活动特殊属性
│
└── 路由级属性 (Transition)
    └── Routing类型
```

---

## 3. 流程级属性规范

### 3.1 BPD标准属性

#### 3.1.1 基本属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **processDefId** | String | 是 | UUID | 流程定义ID | WorkflowProcess.Id |
| **name** | String | 是 | - | 流程名称 | WorkflowProcess.Name |
| **description** | String | 否 | "" | 流程描述 | WorkflowProcess.Description |
| **version** | Integer | 是 | 1 | 版本号 | BPM_PROCESSDEF_VERSION.VERSION |
| **state** | String | 是 | "DRAFT" | 版本状态 | BPM_PROCESSDEF_VERSION.PUBLICATIONSTATE |

#### 3.1.2 分类属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **classification** | String | 否 | "办公流程" | 流程分类 | ExtendedAttribute(Name="Classification") |
| **systemCode** | String | 否 | "bpm" | 系统代码 | ExtendedAttribute(Name="SystemCode") |
| **accessLevel** | String | 否 | "PUBLIC" | 访问级别 | WorkflowProcess.AccessLevel |

#### 3.1.3 时间属性

| 属性名 | 类型 | 必填 | 格式 | 说明 | XPDL映射 |
|--------|------|------|------|------|----------|
| **createTime** | DateTime | 自动 | ISO8601 | 创建时间 | BPM_PROCESSDEF_VERSION.CREATED |
| **activeTime** | DateTime | 否 | ISO8601 | 激活时间 | ExtendedAttribute(Name="ActiveTime") |
| **freezeTime** | DateTime | 否 | ISO8601 | 冻结时间 | ExtendedAttribute(Name="FreezeTime") |
| **modifyTime** | DateTime | 自动 | ISO8601 | 修改时间 | ExtendedAttribute(Name="ModifyTime") |

#### 3.1.4 人员属性

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **creatorId** | String | 自动 | 创建人ID | BPM_PROCESSDEF_VERSION.CREATORID |
| **creatorName** | String | 否 | 创建人姓名 | ExtendedAttribute(Name="CreatorName") |
| **modifierId** | String | 自动 | 修改人ID | ExtendedAttribute(Name="ModifierId") |
| **modifierName** | String | 否 | 修改人姓名 | ExtendedAttribute(Name="ModifierName") |

### 3.2 开始/结束节点坐标 (关键属性)

#### 3.2.1 开始节点坐标

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **startNode** | Object | 是 | 开始节点信息 | ExtendedAttribute(Name="StartOfWorkflow") |
| startNode.participantId | String | 是 | 所属参与者ID | 格式第1部分 |
| startNode.firstActivityId | String | 是 | 第一个活动ID | 格式第2部分 |
| startNode.x | Integer | 是 | X坐标 | 格式第3部分 |
| startNode.y | Integer | 是 | Y坐标 | 格式第4部分 |
| startNode.routing | String | 是 | 路由类型 | 格式第5部分 |

**XPDL格式**: `ParticipantID;FirstActivityID;X;Y;Routing`

**示例**:
```xml
<ExtendedAttribute Name="StartOfWorkflow" 
                   Value="Participant_1;Activity_1;100;200;NO_ROUTING" 
                   Type="BPD"/>
```

**JSON格式**:
```json
{
  "startNode": {
    "participantId": "Participant_1",
    "firstActivityId": "Activity_1",
    "x": 100,
    "y": 200,
    "routing": "NO_ROUTING"
  }
}
```

#### 3.2.2 结束节点坐标

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **endNodes** | Array | 是 | 结束节点列表 | ExtendedAttribute(Name="EndOfWorkflow") |
| endNodes[].participantId | String | 是 | 所属参与者ID | 格式第1部分 |
| endNodes[].lastActivityId | String | 是 | 最后一个活动ID | 格式第2部分 |
| endNodes[].x | Integer | 是 | X坐标 | 格式第3部分 |
| endNodes[].y | Integer | 是 | Y坐标 | 格式第4部分 |
| endNodes[].routing | String | 是 | 路由类型 | 格式第5部分 |

**XPDL格式**: `ParticipantID;LastActivityID;X;Y;Routing`

**示例**:
```xml
<ExtendedAttribute Name="EndOfWorkflow" 
                   Value="Participant_1;Activity_5;500;200;NO_ROUTING" 
                   Type="BPD"/>
```

**JSON格式**:
```json
{
  "endNodes": [
    {
      "participantId": "Participant_1",
      "lastActivityId": "Activity_5",
      "x": 500,
      "y": 200,
      "routing": "NO_ROUTING"
    }
  ]
}
```

#### 3.2.3 路由类型枚举

| 枚举值 | 说明 |
|--------|------|
| **NO_ROUTING** | 无路由 |
| **SIMPLE_ROUTING** | 简单路由 |
| **ORTHOGONAL_ROUTING** | 正交路由 |

### 3.3 监听器 (Listeners)

#### 3.3.1 监听器结构

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **listeners** | Array | 否 | 监听器列表 | ExtendedAttribute(Name="Listeners") |
| listeners[].id | String | 是 | 监听器ID | itjds:Listener.Id |
| listeners[].name | String | 是 | 监听器名称 | itjds:Listener.Name |
| listeners[].event | String | 是 | 监听事件 | itjds:Listener.ListenerEvent |
| listeners[].realizeClass | String | 是 | 实现类 | itjds:Listener.RealizeClass |

**XPDL格式**:
```xml
<ExtendedAttribute Name="Listeners">
  <itjds:Listeners>
    <itjds:Listener Id="listener_1" 
                    Name="流程启动监听" 
                    ListenerEvent="PROCESS_START" 
                    RealizeClass="com.example.ProcessStartListener"/>
  </itjds:Listeners>
</ExtendedAttribute>
```

**JSON格式**:
```json
{
  "listeners": [
    {
      "id": "listener_1",
      "name": "流程启动监听",
      "event": "PROCESS_START",
      "realizeClass": "com.example.ProcessStartListener"
    }
  ]
}
```

#### 3.3.2 监听事件枚举

| 枚举值 | 说明 |
|--------|------|
| **PROCESS_START** | 流程启动 |
| **PROCESS_END** | 流程结束 |
| **ACTIVITY_START** | 活动启动 |
| **ACTIVITY_END** | 活动结束 |
| **ROUTE_TAKE** | 路由执行 |
| **ASSIGNMENT** | 任务分配 |

### 3.4 权限组 (RightGroups)

#### 3.4.1 权限组结构

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **rightGroups** | Array | 否 | 权限组列表 | ExtendedAttribute(Name="RightGroups") |
| rightGroups[].id | String | 是 | 权限组ID | itjds:RightGroup.Id |
| rightGroups[].name | String | 是 | 权限组名称 | itjds:RightGroup.Name |
| rightGroups[].code | String | 是 | 权限组代码 | itjds:RightGroup.Code |
| rightGroups[].order | Integer | 是 | 显示顺序 | itjds:RightGroup.Order |
| rightGroups[].defaultGroup | Boolean | 否 | 是否默认 | itjds:RightGroup.DefaultGroup |

**XPDL格式**:
```xml
<ExtendedAttribute Name="RightGroups">
  <itjds:RightGroups>
    <itjds:RightGroup Id="rg_1" 
                      Name="审批组" 
                      Code="APPROVAL" 
                      Order="1" 
                      DefaultGroup="YES"/>
  </itjds:RightGroups>
</ExtendedAttribute>
```

**JSON格式**:
```json
{
  "rightGroups": [
    {
      "id": "rg_1",
      "name": "审批组",
      "code": "APPROVAL",
      "order": 1,
      "defaultGroup": true
    }
  ]
}
```

---

## 4. 活动级属性规范

### 4.1 基本属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | 数据库映射 |
|--------|------|------|--------|------|------------|
| **activityDefId** | String | 是 | UUID | 活动定义ID | BPM_ACTIVITYDEF.ACTIVITYDEF_ID |
| **processDefId** | String | 是 | - | 流程定义ID | BPM_ACTIVITYDEF.PROCESSDEF_ID |
| **processDefVersionId** | String | 是 | - | 版本ID | BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID |
| **name** | String | 是 | - | 活动名称 | BPM_ACTIVITYDEF.DEFNAME |
| **description** | String | 否 | "" | 活动描述 | BPM_ACTIVITYDEF.DESCRIPTION |

### 4.2 视觉位置属性 (关键属性)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **position** | String | 是 | "NORMAL" | 位置类型 | BPM_ACTIVITYDEF.POSITION |
| **positionCoord** | Object | 是 | - | 坐标对象 | - |
| positionCoord.x | Integer | 是 | 0 | X坐标 | ExtendedAttribute(Name="XOffset") |
| positionCoord.y | Integer | 是 | 0 | Y坐标 | ExtendedAttribute(Name="YOffset") |
| **participantId** | String | 是 | - | 所属参与者ID | ExtendedAttribute(Name="ParticipantID") |

**位置类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **START** | 开始节点 |
| **END** | 结束节点 |
| **NORMAL** | 普通活动 |

**XPDL格式**:
```xml
<ExtendedAttribute Name="XOffset" Value="100"/>
<ExtendedAttribute Name="YOffset" Value="200"/>
<ExtendedAttribute Name="ParticipantID" Value="Participant_1"/>
```

**JSON格式**:
```json
{
  "position": "NORMAL",
  "positionCoord": {
    "x": 100,
    "y": 200
  },
  "participantId": "Participant_1"
}
```

### 4.3 活动类型属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | 数据库映射 |
|--------|------|------|--------|------|------------|
| **activityType** | String | 是 | "TASK" | 活动类型 | BPM_ACTIVITYDEF.ACTIVITYTYPE |
| **implementation** | String | 是 | "No" | 实现类型 | BPM_ACTIVITYDEF.IMPLEMENTATION |
| **execClass** | String | 否 | "" | 执行类 | BPM_ACTIVITYDEF.EXECCLASS |

**活动类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **TASK** | 人工任务 |
| **SERVICE** | 服务任务 |
| **SCRIPT** | 脚本任务 |
| **USER** | 用户任务 |
| **AGENT** | Agent任务 |
| **SCENE** | 场景任务 |

**实现类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **No** | 无实现 |
| **Tool** | 工具应用 |
| **SubFlow** | 子流程 |
| **Block** | 块活动 |
| **Device** | 设备活动 |
| **Service** | 服务调用 |
| **Event** | 事件活动 |
| **OutFlow** | 外部流程 |

### 4.4 时限属性 (WORKFLOW属性组)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **limitTime** | Integer | 否 | 0 | 时限 | BPM_ACTIVITYDEF.LIMIT |
| **alertTime** | Integer | 否 | 0 | 预警时间 | ExtendedAttribute(Name="AlertTime") |
| **durationUnit** | String | 否 | "D" | 时间单位 | BPM_ACTIVITYDEF.DURATIONUNIT |
| **deadLineOperation** | String | 否 | "DEFAULT" | 到期操作 | ExtendedAttribute(Name="DeadLineOperation") |

**时间单位枚举**:

| 枚举值 | 说明 |
|--------|------|
| **Y** | 年 |
| **M** | 月 |
| **D** | 日 |
| **H** | 小时 |
| **m** | 分钟 |
| **s** | 秒 |
| **W** | 周 |

**到期操作枚举**:

| 枚举值 | 说明 |
|--------|------|
| **DEFAULT** | 默认 |
| **NOTIFY** | 通知 |
| **ESCALATE** | 升级 |
| **TERMINATE** | 终止 |

### 4.5 路由控制属性 (WORKFLOW属性组)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **canRouteBack** | String | 否 | "NO" | 允许退回 | BPM_ACTIVITYDEF.CANROUTEBACK |
| **routeBackMethod** | String | 否 | "PREV" | 退回方法 | BPM_ACTIVITYDEF.TAKEBACKMETHOD |
| **canSpecialSend** | String | 否 | "NO" | 允许特送 | BPM_ACTIVITYDEF.CANSPECIALSEND |
| **specialScope** | String | 否 | "ALL" | 特送范围 | ExtendedAttribute(Name="SpecialScope") |
| **join** | String | 否 | "XOR" | 汇聚类型 | BPM_ACTIVITYDEF.JOIN |
| **split** | String | 否 | "XOR" | 分支类型 | BPM_ACTIVITYDEF.SPLIT |

**布尔枚举** (YES/NO):

| 枚举值 | 说明 |
|--------|------|
| **YES** | 是 |
| **NO** | 否 |

**退回方法枚举**:

| 枚举值 | 说明 |
|--------|------|
| **PREV** | 退回上一步 |
| **START** | 退回开始 |
| **SPECIFIED** | 指定步骤 |

**特送范围枚举**:

| 枚举值 | 说明 |
|--------|------|
| **ALL** | 全部 |
| **SAME_GROUP** | 同组 |
| **SPECIFIED** | 指定 |

**汇聚/分支类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **AND** | 与汇聚/分支 |
| **XOR** | 异或汇聚/分支 |
| **OR** | 或汇聚/分支 |

### 4.6 权限属性 (RIGHT属性组)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **performType** | String | 否 | "SINGLE" | 执行类型 | RIGHT.performType |
| **performSequence** | String | 否 | "FIRST" | 执行顺序 | RIGHT.performSequence |
| **canInsteadSign** | String | 否 | "NO" | 允许代签 | RIGHT.canInsteadSign |
| **canTakeBack** | String | 否 | "NO" | 允许收回 | RIGHT.canTakeBack |
| **canReSend** | String | 否 | "NO" | 允许重发 | RIGHT.canReSend |
| **performerSelectedId** | String | 否 | "" | 执行人选择ID | RIGHT.performerSelectedId |
| **readerSelectedId** | String | 否 | "" | 读者选择ID | RIGHT.readerSelectedId |
| **movePerformerTo** | String | 否 | "" | 执行人移动目标 | RIGHT.movePerformerTo |
| **moveSponsorTo** | String | 否 | "" | 发起人移动目标 | RIGHT.moveSponsorTo |
| **moveReaderTo** | String | 否 | "" | 读者移动目标 | RIGHT.moveReaderTo |
| **surrogateId** | String | 否 | "" | 代理人ID | RIGHT.surrogateId |
| **surrogateName** | String | 否 | "" | 代理人名称 | RIGHT.surrogateName |

**执行类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **SINGLE** | 单人执行 |
| **MULTIPLE** | 多人执行 |
| **JOINTSIGN** | 会签 |

**执行顺序枚举**:

| 枚举值 | 说明 |
|--------|------|
| **FIRST** | 抢先 |
| **SEQUENCE** | 顺序 |
| **MEANWHILE** | 同时 |

### 4.7 表单属性 (FORM属性组)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **formId** | String | 否 | "" | 表单ID | FORM.formId |
| **formName** | String | 否 | "" | 表单名称 | FORM.formName |
| **formType** | String | 否 | "" | 表单类型 | FORM.formType |
| **formUrl** | String | 否 | "" | 表单URL | FORM.formUrl |

### 4.8 服务属性 (SERVICE属性组)

| 属性名 | 类型 | 必填 | 默认值 | 说明 | XPDL映射 |
|--------|------|------|--------|------|----------|
| **httpMethod** | String | 否 | "GET" | HTTP方法 | SERVICE.httpMethod |
| **httpUrl** | String | 否 | "" | 服务URL | SERVICE.httpUrl |
| **httpRequestType** | String | 否 | "JSON" | 请求类型 | SERVICE.httpRequestType |
| **httpResponseType** | String | 否 | "JSON" | 响应类型 | SERVICE.httpResponseType |
| **httpServiceParams** | String | 否 | "" | 服务参数 | SERVICE.httpServiceParams |
| **serviceSelectedId** | String | 否 | "" | 服务选择ID | SERVICE.serviceSelectedId |

**HTTP方法枚举**:

| 枚举值 | 说明 |
|--------|------|
| **GET** | GET请求 |
| **POST** | POST请求 |
| **PUT** | PUT请求 |
| **DELETE** | DELETE请求 |

### 4.9 块活动特殊属性

#### 4.9.1 块开始/结束坐标

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **startOfBlock** | Object | 否 | 块开始信息 | ExtendedAttribute(Name="StartOfBlock") |
| startOfBlock.participantId | String | 是 | 参与者ID | 格式第1部分 |
| startOfBlock.firstActivityId | String | 是 | 第一个活动ID | 格式第2部分 |
| startOfBlock.x | Integer | 是 | X坐标 | 格式第3部分 |
| startOfBlock.y | Integer | 是 | Y坐标 | 格式第4部分 |
| startOfBlock.routing | String | 是 | 路由类型 | 格式第5部分 |
| **endOfBlock** | Object | 否 | 块结束信息 | ExtendedAttribute(Name="EndOfBlock") |
| endOfBlock.participantId | String | 是 | 参与者ID | 格式第1部分 |
| endOfBlock.lastActivityId | String | 是 | 最后一个活动ID | 格式第2部分 |
| endOfBlock.x | Integer | 是 | X坐标 | 格式第3部分 |
| endOfBlock.y | Integer | 是 | Y坐标 | 格式第4部分 |
| endOfBlock.routing | String | 是 | 路由类型 | 格式第5部分 |

**XPDL格式**:
```xml
<ExtendedAttribute Name="StartOfBlock" 
                   Value="Participant_1;Activity_1;100;200;NO_ROUTING"/>
<ExtendedAttribute Name="EndOfBlock" 
                   Value="Participant_1;Activity_5;500;200;NO_ROUTING"/>
```

**JSON格式**:
```json
{
  "startOfBlock": {
    "participantId": "Participant_1",
    "firstActivityId": "Activity_1",
    "x": 100,
    "y": 200,
    "routing": "NO_ROUTING"
  },
  "endOfBlock": {
    "participantId": "Participant_1",
    "lastActivityId": "Activity_5",
    "x": 500,
    "y": 200,
    "routing": "NO_ROUTING"
  }
}
```

#### 4.9.2 块内参与者顺序

| 属性名 | 类型 | 必填 | 说明 | XPDL映射 |
|--------|------|------|------|----------|
| **participantVisualOrder** | String | 否 | 参与者顺序 | ExtendedAttribute(Name="ParticipantVisualOrder") |

**XPDL格式**:
```xml
<ExtendedAttribute Name="ParticipantVisualOrder" 
                   Value="Participant_1;Participant_2;Participant_3"/>
```

**JSON格式**:
```json
{
  "participantVisualOrder": "Participant_1;Participant_2;Participant_3"
}
```

---

## 5. 路由级属性规范

### 5.1 基本属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | 数据库映射 |
|--------|------|------|--------|------|------------|
| **routeDefId** | String | 是 | UUID | 路由ID | BPM_ROUTEDEF.ROUTEDEF_ID |
| **processDefId** | String | 是 | - | 流程定义ID | BPM_ROUTEDEF.PROCESSDEF_ID |
| **processDefVersionId** | String | 是 | - | 版本ID | BPM_ROUTEDEF.PROCESSDEF_VERSION_ID |
| **name** | String | 是 | - | 路由名称 | BPM_ROUTEDEF.DEFNAME |
| **description** | String | 否 | "" | 路由描述 | BPM_ROUTEDEF.DESCRIPTION |
| **fromActivityDefId** | String | 是 | - | 源活动ID | BPM_ROUTEDEF.FROMACTIVITYDEF_ID |
| **toActivityDefId** | String | 是 | - | 目标活动ID | BPM_ROUTEDEF.TOACTIVITYDEF_ID |

### 5.2 路由控制属性

| 属性名 | 类型 | 必填 | 默认值 | 说明 | 数据库映射 |
|--------|------|------|--------|------|------------|
| **routeOrder** | Integer | 否 | 0 | 路由顺序 | BPM_ROUTEDEF.ROUTEORDER |
| **routeDirection** | String | 否 | "FORWARD" | 路由方向 | BPM_ROUTEDEF.ROUTEDIRECTION |
| **routeCondition** | String | 否 | "" | 路由条件 | BPM_ROUTEDEF.ROUTECONDITION |
| **routeConditionType** | String | 否 | "CONDITION" | 条件类型 | BPM_ROUTEDEF.ROUTECONDITIONTYPE |
| **routing** | String | 否 | "NO_ROUTING" | 路由类型 | ExtendedAttribute(Name="Routing") |

**路由方向枚举**:

| 枚举值 | 说明 |
|--------|------|
| **FORWARD** | 前进 |
| **BACKWARD** | 后退 |

**条件类型枚举**:

| 枚举值 | 说明 |
|--------|------|
| **CONDITION** | 条件 |
| **OTHERWISE** | 否则 |
| **EXCEPTION** | 异常 |

---

## 6. 完整JSON示例

### 6.1 简单审批流程

```json
{
  "processDef": {
    "processDefId": "proc_001",
    "name": "请假审批流程",
    "description": "员工请假审批流程",
    "classification": "人事流程",
    "systemCode": "HR",
    "accessLevel": "PUBLIC"
  },
  "activeVersion": {
    "processDefVersionId": "ver_001",
    "processDefId": "proc_001",
    "version": 1,
    "state": "RELEASED",
    "createTime": "2026-04-09T10:00:00Z",
    "creatorName": "张三"
  },
  "startNode": {
    "participantId": "Participant_1",
    "firstActivityId": "act_001",
    "x": 100,
    "y": 200,
    "routing": "NO_ROUTING"
  },
  "endNodes": [
    {
      "participantId": "Participant_1",
      "lastActivityId": "act_004",
      "x": 900,
      "y": 200,
      "routing": "NO_ROUTING"
    }
  ],
  "listeners": [
    {
      "id": "listener_1",
      "name": "流程启动监听",
      "event": "PROCESS_START",
      "realizeClass": "com.example.ProcessStartListener"
    }
  ],
  "rightGroups": [
    {
      "id": "rg_1",
      "name": "审批组",
      "code": "APPROVAL",
      "order": 1,
      "defaultGroup": true
    }
  ],
  "activities": [
    {
      "activityDefId": "act_001",
      "name": "填写请假单",
      "description": "员工填写请假申请",
      "position": "NORMAL",
      "positionCoord": {
        "x": 200,
        "y": 200
      },
      "participantId": "Participant_1",
      "activityType": "TASK",
      "implementation": "No",
      "limitTime": 24,
      "durationUnit": "H",
      "canRouteBack": "NO",
      "canSpecialSend": "NO",
      "join": "XOR",
      "split": "XOR",
      "RIGHT": {
        "performType": "SINGLE",
        "performSequence": "FIRST",
        "canInsteadSign": "NO",
        "canTakeBack": "YES",
        "canReSend": "NO"
      },
      "FORM": {
        "formId": "form_leave",
        "formName": "请假单",
        "formType": "CUSTOM"
      }
    },
    {
      "activityDefId": "act_002",
      "name": "部门经理审批",
      "description": "部门经理审批请假申请",
      "position": "NORMAL",
      "positionCoord": {
        "x": 400,
        "y": 200
      },
      "participantId": "Participant_1",
      "activityType": "TASK",
      "implementation": "No",
      "limitTime": 48,
      "durationUnit": "H",
      "alertTime": 24,
      "deadLineOperation": "NOTIFY",
      "canRouteBack": "YES",
      "routeBackMethod": "PREV",
      "canSpecialSend": "YES",
      "specialScope": "ALL",
      "join": "XOR",
      "split": "XOR",
      "RIGHT": {
        "performType": "SINGLE",
        "performSequence": "FIRST",
        "canInsteadSign": "YES",
        "canTakeBack": "YES",
        "canReSend": "YES",
        "performerSelectedId": "dept_manager"
      }
    },
    {
      "activityDefId": "act_003",
      "name": "HR审批",
      "description": "HR部门审批",
      "position": "NORMAL",
      "positionCoord": {
        "x": 600,
        "y": 200
      },
      "participantId": "Participant_1",
      "activityType": "TASK",
      "implementation": "No",
      "limitTime": 24,
      "durationUnit": "H",
      "canRouteBack": "YES",
      "routeBackMethod": "SPECIFIED",
      "join": "XOR",
      "split": "XOR",
      "RIGHT": {
        "performType": "SINGLE",
        "performSequence": "FIRST",
        "performerSelectedId": "hr_manager"
      }
    },
    {
      "activityDefId": "act_004",
      "name": "通知结果",
      "description": "通知员工审批结果",
      "position": "NORMAL",
      "positionCoord": {
        "x": 800,
        "y": 200
      },
      "participantId": "Participant_1",
      "activityType": "SERVICE",
      "implementation": "Service",
      "SERVICE": {
        "httpMethod": "POST",
        "httpUrl": "/api/notify",
        "httpRequestType": "JSON",
        "httpResponseType": "JSON"
      }
    }
  ],
  "routes": [
    {
      "routeDefId": "route_001",
      "name": "提交申请",
      "fromActivityDefId": "act_001",
      "toActivityDefId": "act_002",
      "routeOrder": 1,
      "routeDirection": "FORWARD",
      "routeCondition": "",
      "routeConditionType": "OTHERWISE",
      "routing": "NO_ROUTING"
    },
    {
      "routeDefId": "route_002",
      "name": "经理批准",
      "fromActivityDefId": "act_002",
      "toActivityDefId": "act_003",
      "routeOrder": 1,
      "routeDirection": "FORWARD",
      "routeCondition": "${approved == true}",
      "routeConditionType": "CONDITION",
      "routing": "NO_ROUTING"
    },
    {
      "routeDefId": "route_003",
      "name": "经理驳回",
      "fromActivityDefId": "act_002",
      "toActivityDefId": "act_001",
      "routeOrder": 2,
      "routeDirection": "BACKWARD",
      "routeCondition": "${approved == false}",
      "routeConditionType": "CONDITION",
      "routing": "NO_ROUTING"
    },
    {
      "routeDefId": "route_004",
      "name": "HR批准",
      "fromActivityDefId": "act_003",
      "toActivityDefId": "act_004",
      "routeOrder": 1,
      "routeDirection": "FORWARD",
      "routeCondition": "${approved == true}",
      "routeConditionType": "CONDITION",
      "routing": "NO_ROUTING"
    },
    {
      "routeDefId": "route_005",
      "name": "HR驳回",
      "fromActivityDefId": "act_003",
      "toActivityDefId": "act_001",
      "routeOrder": 2,
      "routeDirection": "BACKWARD",
      "routeCondition": "${approved == false}",
      "routeConditionType": "CONDITION",
      "routing": "NO_ROUTING"
    }
  ]
}
```

---

## 7. 数据验证规则

### 7.1 流程级验证

1. **processDefId**: 必填，UUID格式
2. **name**: 必填，长度1-100字符
3. **version**: 必填，正整数
4. **state**: 必填，枚举值(DRAFT/RELEASED/ARCHIVED)
5. **startNode**: 必填，且只有一个
6. **endNodes**: 必填，至少一个

### 7.2 活动级验证

1. **activityDefId**: 必填，UUID格式
2. **name**: 必填，长度1-100字符
3. **position**: 必填，枚举值(START/END/NORMAL)
4. **positionCoord**: 必填，x和y为整数
5. **participantId**: 必填，关联有效参与者
6. **activityType**: 必填，枚举值
7. **implementation**: 必填，枚举值

### 7.3 路由级验证

1. **routeDefId**: 必填，UUID格式
2. **fromActivityDefId**: 必填，关联有效活动
3. **toActivityDefId**: 必填，关联有效活动
4. **routeDirection**: 必填，枚举值(FORWARD/BACKWARD)
5. **routeConditionType**: 必填，枚举值

---

## 8. 附录

### 8.1 枚举值汇总

| 枚举名称 | 枚举值 |
|----------|--------|
| Position | START, END, NORMAL |
| ActivityType | TASK, SERVICE, SCRIPT, USER, AGENT, SCENE |
| Implementation | No, Tool, SubFlow, Block, Device, Service, Event, OutFlow |
| DurationUnit | Y, M, D, H, m, s, W |
| DeadLineOperation | DEFAULT, NOTIFY, ESCALATE, TERMINATE |
| YesNo | YES, NO |
| RouteBackMethod | PREV, START, SPECIFIED |
| SpecialScope | ALL, SAME_GROUP, SPECIFIED |
| JoinSplitType | AND, XOR, OR |
| PerformType | SINGLE, MULTIPLE, JOINTSIGN |
| PerformSequence | FIRST, SEQUENCE, MEANWHILE |
| RouteDirection | FORWARD, BACKWARD |
| RouteConditionType | CONDITION, OTHERWISE, EXCEPTION |
| HttpMethod | GET, POST, PUT, DELETE |
| ListenerEvent | PROCESS_START, PROCESS_END, ACTIVITY_START, ACTIVITY_END, ROUTE_TAKE, ASSIGNMENT |

### 8.2 XPDL到JSON映射表

| XPDL属性 | JSON属性 | 说明 |
|----------|----------|------|
| WorkflowProcess.Id | processDefId | 流程ID |
| WorkflowProcess.Name | name | 流程名称 |
| ExtendedAttribute(Name="StartOfWorkflow") | startNode | 开始节点坐标 |
| ExtendedAttribute(Name="EndOfWorkflow") | endNodes | 结束节点坐标 |
| ExtendedAttribute(Name="Listeners") | listeners | 监听器 |
| ExtendedAttribute(Name="RightGroups") | rightGroups | 权限组 |
| ExtendedAttribute(Name="XOffset") | positionCoord.x | X坐标 |
| ExtendedAttribute(Name="YOffset") | positionCoord.y | Y坐标 |
| ExtendedAttribute(Name="ParticipantID") | participantId | 参与者ID |

---

*文档结束*
