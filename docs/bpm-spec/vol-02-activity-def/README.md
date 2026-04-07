# 第二册：活动定义规格

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**源码路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\client\ActivityDef.java`

---

## 目录

1. [活动定义基础](#1-活动定义基础)
2. [活动位置类型](#2-活动位置类型)
3. [活动实现方式](#3-活动实现方式)
4. [活动时限属性](#4-活动时限属性)
5. [活动路由属性](#5-活动路由属性)
6. [活动权限属性](#6-活动权限属性)
7. [活动事件](#7-活动事件)
8. [设备活动定义](#8-设备活动定义)
9. [服务活动定义](#9-服务活动定义)
10. [事件活动定义](#10-事件活动定义)

---

## 1. 活动定义基础

### 1.1 接口定义

**源码位置**: `net.ooder.bpm.client.ActivityDef`

```java
@ESDEntity
public interface ActivityDef extends java.io.Serializable {
    public String getProcessDefId();
    public String getProcessDefVersionId();
    public String getActivityDefId();
    public String getName();
    public String getDescription();
    public ActivityDefPosition getPosition();
    public ActivityDefImpl getImplementation();
    public String getExecClass();
    public CommonYesNoEnum getIswaitreturn() throws BPMException;
    public ProcessDefVersion getSubFlow() throws BPMException;
    public String getSubFlowId();
    public int getLimit();
    public int getAlertTime();
    public DurationUnit getDurationUnit();
    public ActivityDefDeadLineOperation getDeadlineOperation();
    public CommonYesNoEnum getCanRouteBack();
    public ActivityDefRouteBackMethod getRouteBackMethod();
    public CommonYesNoEnum getCanSpecialSend();
    public CommonYesNoEnum getCanReSend();
    public ActivityDefJoin getJoin();
    public ActivityDefSplit getSplit();
    public List<String> getOutRouteIds() throws BPMException;
    public List<String> getInRouteIds() throws BPMException;
    public List<AttributeDef> getAllAttribute();
    public String getWorkflowAttribute(String name);
    public ActivityDefRight getRightAttribute();
    public Object getAppAttribute(String name);
    public Object getAttribute(Attributetype attributetype, String name);
    public List<Listener> getListeners();
    public ProcessDefVersion getProcessDefVersion() throws BPMException;
    public ProcessDef getProcessDef() throws BPMException;
    public String getAttribute(String name);
}
```

### 1.2 基础属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| activityDefId | String | 活动UUID | 活动唯一标识 |
| processDefId | String | 所属流程UUID | 活动所属流程UUID |
| processDefVersionId | String | 所属版本UUID | 活动所属流程版本UUID |
| name | String | 活动名称 | 活动名称 |
| description | String | 活动描述 | 活动描述 |
| position | ActivityDefPosition | 活动位置 | 起始/普通/结束 |
| implementation | ActivityDefImpl | 实现方式 | 手动/自动/子流程等 |

---

## 2. 活动位置类型

### 2.1 枚举定义

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefPosition`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| POSITION_NORMAL | NORMAL | 普通 | 一般活动节点 |
| POSITION_START | START | 起始活动 | 流程起始节点 |
| POSITION_END | END | 结束活动 | 流程结束节点 |
| VIRTUAL_LAST_DEF | LAST | LAST | 虚拟最后节点 |

### 2.2 位置类型说明

```
┌─────────────────────────────────────────────────────────────┐
│                      流程结构示意图                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────┐                                              │
│   │  START  │ ◀── 起始活动（POSITION_START）               │
│   └────┬────┘                                              │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────┐                                              │
│   │ NORMAL  │ ◀── 普通活动（POSITION_NORMAL）               │
│   └────┬────┘                                              │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────┐                                              │
│   │ NORMAL  │ ◀── 普通活动（POSITION_NORMAL）               │
│   └────┬────┘                                              │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────┐                                              │
│   │   END   │ ◀── 结束活动（POSITION_END）                  │
│   └─────────┘                                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 活动实现方式

### 3.1 枚举定义

**源码位置**: `net.ooder.config.ActivityDefImpl`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| IMPL_NO | IMPL_NO | 手动活动 | 人工办理 |
| IMPL_TOOL | IMPL_TOOL | 自动活动 | 自动执行 |
| IMPL_SUBFLOW | IMPL_SUBFLOW | 子流程活动 | 调用子流程 |
| IMPL_OUTFLOW | IMPL_OUTFLOW | 跳转流程活动 | 跳转到其他流程 |
| IMPL_DEVICE | IMPL_DEVICE | 设备活动 | 设备执行 |
| IMPL_EVENT | IMPL_EVENT | 事件活动 | 事件触发 |
| IMPL_SERVICE | IMPL_SERVICE | 服务活动 | 服务调用 |

### 3.2 实现方式属性映射

| 实现方式 | 扩展属性接口 | 说明 |
|----------|--------------|------|
| IMPL_NO | ActivityDefRight | 手动活动需要配置办理人权限 |
| IMPL_TOOL | execClass | 自动活动需要配置执行类 |
| IMPL_SUBFLOW | subFlow, iswaitreturn | 子流程活动需要配置子流程和等待标志 |
| IMPL_OUTFLOW | subFlow | 跳转流程活动需要配置目标流程 |
| IMPL_DEVICE | ActivityDefDevice | 设备活动需要配置设备属性 |
| IMPL_EVENT | ActivityDefEvent | 事件活动需要配置事件属性 |
| IMPL_SERVICE | ActivityDefService | 服务活动需要配置服务属性 |

---

## 4. 活动时限属性

### 4.1 时限属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| limit | int | 活动时间限制 | 时间限制值 |
| alertTime | int | 活动报警时间 | 报警时间值 |
| durationUnit | DurationUnit | 时间单位 | Y/M/D/H/m/s/W |
| deadlineOperation | ActivityDefDeadLineOperation | 到期处理办法 | 到达时间限制后的操作 |

### 4.2 时间单位 (DurationUnit)

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| Y | Y | 年 | 年 |
| M | M | 月 | 月 |
| D | D | 日 | 日 |
| H | H | 时 | 时 |
| m | m | 分 | 分 |
| s | s | 秒 | 秒 |
| W | W | 工作日 | 工作日 |

### 4.3 到期处理办法 (ActivityDefDeadLineOperation)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| DEFAULT | DEFAULT | 默认值 | 默认处理 |
| DELAY | DELAY | 延期办理 | 延期办理 |
| TAKEBACK | TAKEBACK | 自动收回 | 自动收回 |
| SURROGATE | SURROGATE | 代办人自动接收 | 代办人自动接收 |

---

## 5. 活动路由属性

### 5.1 路由属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| join | ActivityDefJoin | 等待合并 | 到达此活动节点路由的处理方法 |
| split | ActivityDefSplit | 并行处理 | 从此活动节点出发的路由 |
| canRouteBack | CommonYesNoEnum | 是否允许退回 | 是否可以退回 |
| routeBackMethod | ActivityDefRouteBackMethod | 退回路径 | 退回的方法 |
| canSpecialSend | CommonYesNoEnum | 是否允许特送 | 是否可以特送 |
| specialSendScope | ActivityDefSpecialSendScope | 特送范围 | 特送范围 |
| canReSend | CommonYesNoEnum | 是否可以补发 | 是否可以补发 |

### 5.2 等待合并类型 (ActivityDefJoin)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefJoin`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| DEFAULT | DEFAULT | 默认值 | 默认处理 |
| JOIN_AND | AND | 等待合并 | 等待所有路由到达 |
| JOIN_XOR | XOR | 不等待异步推进 | 不等待，异步推进 |

### 5.3 并行处理类型 (ActivityDefSplit)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefSplit`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| DEFAULT | DEFAULT | 默认值 | 默认处理 |
| SPLIT_AND | AND | 并行分裂 | 并行执行所有路由 |
| SPLIT_XOR | XOR | 选择性执行 | 选择性执行路由 |

### 5.4 退回路径类型 (ActivityDefRouteBackMethod)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefRouteBackMethod`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| DEFAULT | DEFAULT | 默认值 | 默认处理 |
| LAST | LAST | 上一活动 | 退回上一步 |
| ANY | ANY | 退回经过的任意活动 | 退回前面经过得任意一步 |
| SPECIFY | SPECIFY | 条件退回 | 退回到指定的活动节点上 |

### 5.5 特送范围类型 (ActivityDefSpecialSendScope)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| DEFAULT | DEFAULT | 默认值 | 默认处理 |
| ALL | ALL | 所有人 | 所有人都可以特送 |
| PERFORMERS | PERFORMERS | 曾经的办理人 | 曾经的办理人可以特送 |

### 5.6 执行类型 (ActivityDefExecution)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefExecution`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| ASYNCHR | ASYNCHR | 异步 | 异步执行 |
| DEFAULT | SYNCHR | 同步 | 同步执行 |

---

## 6. 活动权限属性

### 6.1 接口定义

**源码位置**: `net.ooder.bpm.client.ActivityDefRight`

```java
public interface ActivityDefRight extends java.io.Serializable {
    public List<ParticipantSelect> getPerformerSelectedAtt();
    public List<ParticipantSelect> getReaderSelectedAtt();
    public List<ParticipantSelect> getInsteadSignAtt();
    public ActivityDefPerformtype getPerformType();
    public ActivityDefPerformSequence getPerformSequence();
    public Boolean isCanInsteadSign();
    public Boolean isCanTakeBack();
    public ActivityDefSpecialSendScope getSpecialSendScope();
    public Boolean isCanReSend();
    public List<Person> getPerFormPersons() throws BPMException;
    public List<Person> getReaderPersons() throws BPMException;
    public List<Person> getInsteadSignPersons() throws BPMException;
    public RightGroupEnums getMovePerformerTo();
    public RightGroupEnums getMoveReaderTo();
}
```

### 6.2 权限属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| performType | ActivityDefPerformtype | 办理类型 | 办理方式 |
| performSequence | ActivityDefPerformSequence | 办理顺序 | 办理顺序 |
| performerSelectedAtt | List\<ParticipantSelect\> | 办理人公式 | 办理人选择公式 |
| readerSelectedAtt | List\<ParticipantSelect\> | 阅办人公式 | 阅办人选择公式 |
| insteadSignAtt | List\<ParticipantSelect\> | 代签人公式 | 代签人选择公式 |
| canInsteadSign | Boolean | 是否能够代签 | 是否能够代签 |
| canTakeBack | Boolean | 是否可以收回 | 是否可以收回 |
| canReSend | Boolean | 是否可以补发 | 是否可以补发 |
| specialSendScope | ActivityDefSpecialSendScope | 特送范围 | 特送范围 |
| movePerformerTo | RightGroupEnums | 办理后权限 | 办理后权限转移 |
| moveReaderTo | RightGroupEnums | 阅办后权限 | 阅办后权限转移 |

### 6.3 办理类型 (ActivityDefPerformtype)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefPerformtype`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| SINGLE | SINGLE | 单人办理 | 仅一人办理 |
| MULTIPLE | MULTIPLE | 多人办理 | 多人可办理 |
| JOINTSIGN | JOINTSIGN | 合会签办理 | 需多人会签 |
| NEEDNOTSELECT | NEEDNOTSELECT | 无需选择 | 直接送达 |
| NOSELECT | NOSELECT | 不需要选择 | 所有候选人成为办理人 |
| DEFAULT | DEFAULT | 默认值 | 默认处理 |

### 6.4 办理顺序 (ActivityDefPerformSequence)

**源码位置**: `net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| FIRST | FIRST | 抢占 | 抢占办理 |
| SEQUENCE | SEQUENCE | 顺序办理 | 按顺序办理 |
| MEANWHILE | MEANWHILE | 同时办理 | 同时办理 |
| AUTOSIGN | AUTOSIGN | 自动签收 | 自动签收 |
| DEFAULT | DEFAULT | 默认值 | 默认处理 |

### 6.5 权限组枚举 (RightGroupEnums)

**源码位置**: `net.ooder.bpm.enums.right.RightGroupEnums`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| PERFORMER | PERFORMER | 当前办理人 | 当前办理人组 |
| SPONSOR | SPONSOR | 发起人 | 发起人组 |
| READER | READER | 读者组 | 读者组 |
| HISTORYPERFORMER | HISTORYPERFORMER | 曾经办理人 | 曾经办理人组 |
| HISSPONSOR | HISSPONSOR | 发送人 | 发送人组 |
| HISTORYREADER | HISTORYREADER | 历史读者 | 历史读者组 |
| NORIGHT | NORIGHT | 无权限组 | 无权限组 |
| NULL | NULL | 访客组 | 访客组 |

---

## 7. 活动事件

### 7.1 枚举定义

**源码位置**: `net.ooder.bpm.enums.event.ActivityEventEnums`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| INITED | activityInited | 初始化 | 活动初始化 |
| ROUTING | activityActiving | 新活动开始被激活 | 活动开始被激活 |
| ACTIVING | activityRouting | 开始执行路由操作 | 开始执行路由操作 |
| SAVEING | activityFormSaveing | 开始执行任务 | 开始执行任务 |
| SAVEED | activityFormSaveed | 执行完毕 | 执行完毕 |
| ROUTED | activityRouted | 路由操作完成 | 路由操作完成 |
| ACTIVED | activityInited | 活动完成激活 | 活动完成激活 |
| SPLITING | activitySpliting | 活动开始执行路由分裂 | 活动开始执行路由分裂 |
| SPLITED | activitySplited | 活动已经分裂 | 活动已经分裂为多个活动实例 |
| JOINING | activityJoining | 活动开始执行合并操作 | 活动开始执行合并操作 |
| JOINED | activityJoined | 活动已经完成合并操作 | 活动已经完成合并操作 |
| OUTFLOWING | activityOutFlowing | 活动开始跳转到其他流程上 | 活动开始跳转到其他流程上 |
| OUTFLOWED | activityOutFlowed | 活动已经跳转到其他流程上 | 活动已经跳转到其他流程上 |
| OUTFLOWRETURNING | activityOutFlowReturning | 外流活动开始返回 | 外流活动开始返回 |
| OUTFLOWRETURNED | activityOutFlowReturned | 外流活动完成返回 | 外流活动完成返回 |
| SUSPENDING | activitySuspending | 活动开始挂起 | 活动开始挂起 |
| SUSPENDED | activitySuspended | 活动已经挂起 | 活动已经挂起 |
| RESUMING | activityResuming | 活动开始恢复 | 活动开始恢复 |
| RESUMED | activityResumed | 活动已经恢复 | 活动已经恢复 |
| COMPLETING | activityCompleting | 活动开始完成 | 活动开始完成 |
| COMPLETED | activityCompleted | 活动已经完成 | 活动已经完成 |
| TAKEBACKING | activityTakebacking | 活动开始收回 | 活动开始收回 |
| TAKEBACKED | activityTakebacked | 活动已经收回 | 活动已经收回 |
| DISP | activityDisplay | 活动开始展示 | 活动开始展示 |

---

## 8. 设备活动定义

### 8.1 接口定义

**源码位置**: `net.ooder.bpm.client.ActivityDefDevice`

```java
public interface ActivityDefDevice {
    public CommandExecType getCommandExecType();
    public CommandRetry getCommandRetry();
    public Integer getCommandExecRetryTimes();
    public Integer getCommandDelayTime();
    public Integer commandSendTimeout();
    public Boolean isCanOffLineSend();
    public ActivityDefDevicePerformSequence getPerformSequence();
    public ActivityDefDevicePerformtype getPerformType();
    public Boolean isCanTakeBack();
    public Boolean isCanReSend();
    public String getEndpointSelectedId();
    public String getCommandSelectedId();
    public ActivityDefDeviceSpecial getSpecialSendScope();
    public List<DeviceEndPoint> getEndpoints() throws BPMException;
    public List<Command> getCommand() throws BPMException;
}
```

### 8.2 设备活动属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| commandExecType | CommandExecType | 命令执行方式 | 命令执行方式 |
| commandRetry | CommandRetry | 命令重试方式 | 命令重试方式 |
| commandExecRetryTimes | Integer | 命令重试次数 | 命令重试次数 |
| commandDelayTime | Integer | 命令等待时间 | 命令等待时间 |
| commandSendTimeout | Integer | 命令超时等待时间 | 命令超时等待时间 |
| canOffLineSend | Boolean | 是否可以离线发送 | 是否可以离线发送 |
| performSequence | ActivityDefDevicePerformSequence | 设备执行顺序 | 设备执行顺序 |
| performType | ActivityDefDevicePerformtype | 设备执行方式 | 设备执行方式 |
| canTakeBack | Boolean | 是否收回命令 | 是否收回命令 |
| canReSend | Boolean | 是否能重新发送 | 是否能重新发送 |
| endpointSelectedId | String | 设备应用列表 | 设备应用列表 |
| commandSelectedId | String | 设备名列表 | 设备名列表 |
| specialSendScope | ActivityDefDeviceSpecial | 特送范围 | 特送范围 |
| endpoints | List\<DeviceEndPoint\> | 设备端点列表 | 设备端点列表 |
| command | List\<Command\> | 命令列表 | 命令列表 |

---

## 9. 服务活动定义

### 9.1 接口定义

**源码位置**: `net.ooder.bpm.client.ActivityDefService`

```java
public interface ActivityDefService {
    public String getUrl();
    public RequestType getRequestType();
    public ResponseType getResponseType();
    public HttpMethod getMethod();
    public String getServiceParams();
    public String getServiceSelectedID();
}
```

### 9.2 服务活动属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| url | String | 服务URL | 服务URL地址 |
| requestType | RequestType | 请求类型 | 请求类型 |
| responseType | ResponseType | 响应类型 | 响应类型 |
| method | HttpMethod | HTTP方法 | HTTP方法 |
| serviceParams | String | 服务参数 | 服务参数 |
| serviceSelectedID | String | 服务选择ID | 服务选择ID |

---

## 10. 事件活动定义

### 10.1 接口定义

**源码位置**: `net.ooder.bpm.client.ActivityDefEvent`

```java
public interface ActivityDefEvent {
    public DeviceAPIEventEnums getDeviceEvent();
    public String getEndpointSelectedId();
    public List<DeviceEndPoint> getEndpoints();
    public DurationUnit getDurationUnit();
    public String getAlertTime();
    public ActivityDefDeadLineOperation getDeadLineOperation();
    public DeviceDataTypeKey getAttributeName();
}
```

### 10.2 事件活动属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| deviceEvent | DeviceAPIEventEnums | 设备事件 | 设备事件类型 |
| endpointSelectedId | String | 端点选择ID | 端点选择ID |
| endpoints | List\<DeviceEndPoint\> | 端点列表 | 端点列表 |
| durationUnit | DurationUnit | 时间单位 | 时间单位 |
| alertTime | String | 报警时间 | 报警时间 |
| deadLineOperation | ActivityDefDeadLineOperation | 到期处理 | 到期处理办法 |
| attributeName | DeviceDataTypeKey | 属性名称 | 属性名称 |

---

## 附录

### A. 面板清单

| 面板名称 | 所属对象 | 说明 |
|----------|----------|------|
| 基本信息面板 | ActivityDef | 活动基本信息配置 |
| 实现方式面板 | ActivityDef | 活动实现方式配置 |
| 时限设置面板 | ActivityDef | 时间限制和报警配置 |
| 路由设置面板 | ActivityDef | 合并/分裂/退回/特送配置 |
| 权限设置面板 | ActivityDefRight | 办理人/阅办人/代签人配置 |
| 设备配置面板 | ActivityDefDevice | 设备活动配置 |
| 服务配置面板 | ActivityDefService | 服务活动配置 |
| 事件配置面板 | ActivityDefEvent | 事件活动配置 |

### B. 枚举清单

| 枚举名称 | 中文名 | 枚举值数量 |
|----------|--------|------------|
| ActivityDefPosition | 活动位置类型 | 4 |
| ActivityDefJoin | 等待合并类型 | 3 |
| ActivityDefSplit | 并行处理类型 | 3 |
| ActivityDefPerformtype | 办理类型 | 6 |
| ActivityDefPerformSequence | 办理顺序 | 5 |
| ActivityDefDeadLineOperation | 到期处理办法 | 4 |
| ActivityDefRouteBackMethod | 退回路径类型 | 4 |
| ActivityDefSpecialSendScope | 特送范围类型 | 3 |
| ActivityDefExecution | 执行类型 | 2 |
| RightGroupEnums | 权限组枚举 | 8 |
| ActivityEventEnums | 活动事件 | 24 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-02-activity-def\README.md`
