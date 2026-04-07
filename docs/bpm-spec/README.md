# BPM 流程定义需求规格说明书

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**项目路径**: E:\github\ooder-skills  
**源码路径**: E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm

---

## 文档结构

本需求规格说明书采用分册结构，共分为七册：

| 分册 | 名称 | 内容 |
|------|------|------|
| [第一册](./vol-01-process-def/README.md) | 流程定义规格 | ProcessDef、ProcessDefVersion、ProcessDefForm、Listener |
| [第二册](./vol-02-activity-def/README.md) | 活动定义规格 | ActivityDef、ActivityDefRight、ActivityDefForm、各类活动扩展 |
| [第三册](./vol-03-route-def/README.md) | 路由定义规格 | RouteDef、路由条件、路由方向 |
| [第四册](./vol-04-agent-extension/README.md) | Agent扩展规格 | Agent活动类型、调度策略、协作模式 |
| [第五册](./vol-05-a2ui-extension/README.md) | A2UI扩展规格 | SceneDef、PageAgent、Function Calling |
| [第六册](./vol-06-yaml-standard/README.md) | YAML格式标准 | 流程定义YAML格式规范 |
| [第七册](./vol-07-designer-interaction/README.md) | 设计器交互规格 | 工具栏、菜单栏、右键菜单、快捷键 |

---

## 源码参考

### 客户端接口

```
E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\client\
├── ProcessDef.java           # 流程定义接口
├── ProcessDefVersion.java    # 流程版本接口
├── ProcessDefForm.java       # 流程表单接口
├── ActivityDef.java          # 活动定义接口
├── ActivityDefRight.java     # 活动权限接口
├── ActivityDefForm.java      # 活动表单接口
├── ActivityDefEvent.java     # 事件活动接口
├── ActivityDefService.java   # 服务活动接口
├── ActivityDefDevice.java    # 设备活动接口
├── RouteDef.java             # 路由定义接口
├── Listener.java             # 监听器接口
├── AttributeDef.java         # 扩展属性接口
└── ...
```

### 枚举定义

```
E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\enums\
├── process\                  # 流程相关枚举
│   ├── ProcessDefAccess.java
│   ├── ProcessDefVersionStatus.java
│   ├── ProcessDefEnums.java
│   └── ...
├── activitydef\              # 活动相关枚举
│   ├── ActivityDefPosition.java
│   ├── ActivityDefPerformtype.java
│   ├── ActivityDefPerformSequence.java
│   ├── ActivityDefJoin.java
│   ├── ActivityDefSplit.java
│   ├── ActivityDefDeadLineOperation.java
│   ├── ActivityDefRouteBackMethod.java
│   ├── ActivityDefSpecialSendScope.java
│   ├── ActivityDefExecution.java
│   ├── ActivityDefEnums.java
│   ├── service\              # 服务活动枚举
│   ├── event\                # 事件活动枚举
│   ├── deivce\               # 设备活动枚举
│   └── task\                 # 任务枚举
├── route\                    # 路由相关枚举
│   ├── RouteCondition.java
│   ├── RouteDirction.java
│   └── RouteEnums.java
├── right\                    # 权限相关枚举
│   ├── RightGroupEnums.java
│   ├── CommissionEnums.java
│   └── ...
├── form\                     # 表单相关枚举
│   ├── MarkEnum.java
│   ├── LockEnum.java
│   ├── EditorEnum.java
│   └── ...
├── event\                    # 事件相关枚举
│   ├── ProcessEventEnums.java
│   ├── ActivityEventEnums.java
│   ├── ListenerEnums.java
│   └── ...
└── ...
```

---

## 枚举统计

| 分类 | 枚举数量 | 说明 |
|------|----------|------|
| 流程定义枚举 | 6 | ProcessDefAccess、ProcessDefVersionStatus、MarkEnum、LockEnum、EditorEnum、ProcessEventEnums |
| 活动定义枚举 | 20+ | ActivityDefPosition、ActivityDefPerformtype、ActivityDefPerformSequence、ActivityDefJoin、ActivityDefSplit 等 |
| 路由定义枚举 | 3 | RouteCondition、RouteDirction、ListenerEnums |
| 权限定义枚举 | 8 | RightGroupEnums、CommissionEnums、RightConditionEnums 等 |
| 表单定义枚举 | 4 | MarkEnum、LockEnum、EditorEnum、HistoryRightEnum |
| 事件定义枚举 | 10+ | ProcessEventEnums、ActivityEventEnums、ListenerEnums 等 |
| Agent扩展枚举 | 6 | ActivityCategory、ActivityDefAgentType、AgentScheduleStrategy、AgentCollaborationMode、PerformerType、ConversationType |

---

## 重复定义处理

经检查发现以下重复定义，建议统一处理：

| 问题 | 处理方案 |
|------|----------|
| 办理类型重复 | 统一使用 `ActivityDefPerformtype`，通过 `performerType` 区分 Human/Agent/Device |
| 执行顺序重复 | 统一使用 `ActivityDefPerformSequence` |
| 到期处理重复 | 统一使用 `ActivityDefDeadLineOperation` |

---

## 修订历史

| 版本 | 日期 | 修订内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-04-06 | 初始版本 | - |
| v2.0 | 2026-04-06 | 添加Agent和A2UI扩展 | - |
| v3.0 | 2026-04-06 | 分册结构重组 | - |
