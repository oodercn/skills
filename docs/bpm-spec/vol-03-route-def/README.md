# 第三册：路由定义规格

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**源码路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\client\RouteDef.java`

---

## 目录

1. [路由定义基础](#1-路由定义基础)
2. [路由方向](#2-路由方向)
3. [路由条件](#3-路由条件)
4. [路由扩展属性](#4-路由扩展属性)
5. [路由监听器](#5-路由监听器)

---

## 1. 路由定义基础

### 1.1 接口定义

**源码位置**: `net.ooder.bpm.client.RouteDef`

```java
@ESDEntity
public interface RouteDef extends java.io.Serializable {
    public String getRouteDefId();
    public String getProcessDefId();
    public String getProcessDefVersionId();
    public String getName();
    public String getDescription();
    public String getFromActivityDefId();
    public String getToActivityDefId();
    public int getRouteOrder();
    public RouteDirction getRouteDirection();
    public String getRouteCondition();
    public RouteCondition getRouteConditionType();
    public Object getWorkflowAttribute(String name);
    public Object getRightAttribute(String name);
    public String getAttribute(String name);
    public Object getAttribute(Attributetype attributetype, String name);
    public List<AttributeDef> getAllAttribute();
    public Object getAppAttribute(String name);
    public List<Listener> getListeners();
    public ActivityDef getFromActivityDef() throws BPMException;
    public ActivityDef getToActivityDef() throws BPMException;
    public ProcessDefVersion getProcessDefVersion() throws BPMException;
    public ProcessDef getProcessDef() throws BPMException;
    public boolean isToEnd();
}
```

### 1.2 基础属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| routeDefId | String | 路由UUID | 路由唯一标识 |
| processDefId | String | 所属流程UUID | 路由所属流程UUID |
| processDefVersionId | String | 所属版本UUID | 路由所属流程版本UUID |
| name | String | 路由名称 | 路由名称 |
| description | String | 路由描述 | 路由描述 |
| fromActivityDefId | String | 起始活动节点 | 路由起始活动节点ID |
| toActivityDefId | String | 到达活动节点 | 路由到达活动节点ID |
| routeOrder | int | 路由顺序 | 路由的顺序 |
| routeDirection | RouteDirction | 路由方向 | 路由的方向 |
| routeCondition | String | 路由条件 | 路由的条件表达式 |
| routeConditionType | RouteCondition | 路由条件类型 | 路由条件类型 |

### 1.3 路由结构图

```
┌─────────────────────────────────────────────────────────────┐
│                      路由结构示意图                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌───────────┐                    ┌───────────┐           │
│   │ Activity A│                    │ Activity B│           │
│   │  (起始)   │                    │  (目标)   │           │
│   └───────────┘                    └───────────┘           │
│         │                                ▲                 │
│         │    ┌─────────────────────┐     │                 │
│         └───▶│       Route         │─────┘                 │
│              │                     │                        │
│              │  fromActivityDefId  │                        │
│              │  toActivityDefId    │                        │
│              │  routeCondition     │                        │
│              │  routeDirection     │                        │
│              └─────────────────────┘                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 路由方向

### 2.1 枚举定义

**源码位置**: `net.ooder.bpm.enums.route.RouteDirction`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| FORWARD | FORWARD | 前进路由 | 正常流转方向 |
| BACK | BACKWARD | 退回路由 | 退回方向 |
| SPECIAL | SPECIAL | 特送 | 特送路由 |

### 2.2 路由方向说明

```
┌─────────────────────────────────────────────────────────────┐
│                      路由方向示意图                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────┐   FORWARD    ┌─────────┐   FORWARD    ┌─────┐│
│   │ Activity│─────────────▶│ Activity│─────────────▶│ END ││
│   │    A    │              │    B    │              │     ││
│   └─────────┘              └─────────┘              └─────┘│
│        ▲                        │                            │
│        │        BACK            │                            │
│        └────────────────────────┘                            │
│                                                             │
│   ┌─────────┐              ┌─────────┐                     │
│   │ Activity│──SPECIAL───▶│ Activity│                     │
│   │    C    │              │    D    │                     │
│   └─────────┘              └─────────┘                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 路由条件

### 3.1 枚举定义

**源码位置**: `net.ooder.bpm.enums.route.RouteCondition`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| CONDITION | CONDITION | 执行条件 | 满足条件时流转 |
| OTHERWISE | OTHERWISE | 例外处理 | 例外处理路由 |
| EXCEPTION | EXCEPTION | 异常处理 | 异常处理路由 |
| DEFAULTEXCEPTION | DEFAULTEXCEPTION | 流程默认值 | 流程默认路由 |

### 3.2 条件类型说明

| 条件类型 | 使用场景 | 示例 |
|----------|----------|------|
| CONDITION | 正常条件流转 | `${approved == true}` |
| OTHERWISE | 默认分支 | 当所有条件都不满足时 |
| EXCEPTION | 异常捕获 | 捕获特定异常 |
| DEFAULTEXCEPTION | 默认异常处理 | 捕获未处理的异常 |

### 3.3 条件表达式示例

```yaml
routes:
  - id: r1
    name: 审批通过
    from: approval
    to: end
    routeConditionType: CONDITION
    routeCondition: "${approved == true}"
    
  - id: r2
    name: 审批拒绝
    from: approval
    to: revision
    routeConditionType: CONDITION
    routeCondition: "${approved == false}"
    
  - id: r3
    name: 默认路由
    from: approval
    to: end
    routeConditionType: OTHERWISE
    routeCondition: "true"
```

---

## 4. 路由扩展属性

### 4.1 扩展属性类型

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| workflowAttribute | Object | 工作流扩展属性 | 工作流相关扩展属性 |
| rightAttribute | Object | 权限扩展属性 | 权限相关扩展属性 |
| appAttribute | Object | 应用扩展属性 | 应用相关扩展属性 |
| attribute | String | 自定义扩展属性 | 自定义扩展属性 |
| allAttribute | List\<AttributeDef\> | 所有扩展属性 | 所有扩展属性列表 |

### 4.2 扩展属性定义

**源码位置**: `net.ooder.bpm.client.AttributeDef`

```java
public interface AttributeDef extends Attribute {
    public AttributeInterpretClass getInterpretClass();
    public Attributetype getType();
    public Integer getIsExtension();
    public CommonYesNoEnum getCanInstantiate();
}
```

---

## 5. 路由监听器

### 5.1 监听器属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| listenerId | String | 监听器ID | 监听器唯一标识 |
| listenerName | String | 监听器名称 | 监听器名称 |
| listenerEvent | ListenerEnums | 监听器事件 | 监听器注册的事件类型 |
| realizeClass | String | 执行类 | 监听器实现类 |
| expressionEventType | EventEnums | 监听事件类型 | 表达式事件类型 |
| expressionListenerType | ListenerTypeEnums | 监听器类型 | 监听器类型 |
| expressionStr | String | 执行表达式 | 执行表达式 |

---

## 附录

### A. 面板清单

| 面板名称 | 所属对象 | 说明 |
|----------|----------|------|
| 基本信息面板 | RouteDef | 路由基本信息配置 |
| 连接设置面板 | RouteDef | 起始/目标活动配置 |
| 条件设置面板 | RouteDef | 路由条件和类型配置 |
| 监听器配置面板 | Listener | 路由监听器配置 |
| 扩展属性面板 | AttributeDef | 自定义扩展属性配置 |

### B. 枚举清单

| 枚举名称 | 中文名 | 枚举值数量 |
|----------|--------|------------|
| RouteDirction | 路由方向 | 3 |
| RouteCondition | 路由条件类型 | 4 |
| ListenerEnums | 监听器类型 | 5 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-03-route-def\README.md`
