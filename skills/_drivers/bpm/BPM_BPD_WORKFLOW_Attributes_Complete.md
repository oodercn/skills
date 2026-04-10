# BPM流程引擎 BPD & WORKFLOW 扩展属性完整列表

## 说明

本文档基于对原有Swing版本XPDL编辑器源码（`E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio`）的深入分析，补全BPD和WORKFLOW属性组的完整定义。

原有设计器中，扩展属性通过`ExtendedAttribute`类表示，包含以下字段：
- **Name**: 属性名
- **Value**: 属性值
- **Type**: 属性类型（BPD/WORKFLOW/RIGHT/FORM/SERVICE等）
- **Id**: 属性ID
- **Description**: 属性描述

---

## 1. BPD属性组（流程定义级）

BPD（Business Process Definition）属性组存储在流程定义版本级别，对应`ProcessDefVersionBean`中的扩展属性处理。

### 1.1 流程版本基本属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **VersionId** | BPD | 流程版本UUID | WorkflowProcess.attrVersionId |
| **Classification** | BPD | 流程分类 | WorkflowProcess.attrClassification |
| **SystemCode** | BPD | 所属系统代码 | WorkflowProcess.attrSystemCode |
| **Description** | BPD | 版本描述 | WorkflowProcess.versionDesc |
| **ActiveTime** | BPD | 激活时间 | WorkflowProcess.activeTime |
| **FreezeTime** | BPD | 冻结时间 | WorkflowProcess.freezeTime |
| **CreatorName** | BPD | 创建人姓名 | WorkflowProcess.creatorName |
| **ModifierId** | BPD | 修改人ID | WorkflowProcess.modifierId |
| **ModifierName** | BPD | 修改人姓名 | WorkflowProcess.modifierName |
| **ModifyTime** | BPD | 修改时间 | WorkflowProcess.modifyTime |

### 1.2 流程时限属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **Limit** | BPD | 时间限制 | ProcessHeader.limit |
| **DurationUnit** | BPD | 时间单位(Y/M/D/H/m/s/W) | ProcessHeader.durationUnit |

### 1.3 特殊结构属性

| 属性名 | 属性类型 | 说明 | 存储方式 |
|--------|----------|------|----------|
| **Listeners** | BPD | 流程监听器集合 | 嵌套XML结构(itjds:Listeners) |
| **RightGroups** | BPD | 自定义权限组集合 | 嵌套XML结构(itjds:RightGroups) |

---

## 2. WORKFLOW属性组（活动级）

WORKFLOW属性组存储在活动定义级别，对应`ActivityDefBean`中的扩展属性处理。

### 2.1 活动位置属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **Position** | WORKFLOW | 活动位置类型 | Activity.getPosition() |
| **WORKFLOW.positionCoord** | WORKFLOW | 活动坐标(JSON格式) | ProcessDefManagerService |

**Position枚举值**：
- `START` - 开始活动
- `END` - 结束活动
- `NORMAL` - 普通活动

### 2.2 活动时限属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **DurationUnit** | WORKFLOW | 时间单位 | ActivityEventNo.durationUnit |
| **AlertTime** | WORKFLOW | 预警时间 | ActivityEventNo.alertTime |
| **DeadLineOperation** | WORKFLOW | 到期处理办法 | ActivityEventNo.deadLineOperation |

**DurationUnit枚举值**：
- `Y` - 年
- `M` - 月
- `D` - 日
- `H` - 小时
- `m` - 分钟
- `s` - 秒
- `W` - 周

**DeadLineOperation枚举值**：
- `DEFAULT` - 默认
- `NOTIFY` - 通知
- `ESCALATE` - 升级
- `TERMINATE` - 终止

### 2.3 活动路由属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **CanRouteBack** | WORKFLOW | 是否允许退回 | ActivityEventNo.canRouteBack |
| **RouteBackMethod** | WORKFLOW | 退回方法 | ActivityEventNo.routeBackMethod |
| **CanSpecialSend** | WORKFLOW | 是否允许特送 | ActivityEventNo.canSpecialSend |
| **SpecialScope** | WORKFLOW | 特送范围 | ActivityEventNo.specialScope |

**CanRouteBack枚举值**：
- `YES` - 允许
- `NO` - 不允许

**RouteBackMethod枚举值**：
- `PREV` - 退回上一步
- `START` - 退回开始
- `SPECIFIED` - 指定步骤

**CanSpecialSend枚举值**：
- `YES` - 允许
- `NO` - 不允许

**SpecialScope枚举值**：
- `ALL` - 全部
- `SAME_GROUP` - 同组
- `SPECIFIED` - 指定

### 2.4 活动实现属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **Implementation** | WORKFLOW | 实现类型 | Activity.getImplementation() |
| **ExecClass** | WORKFLOW | 执行类名 | Activity.getExecClass() |

**Implementation枚举值**：
- `No` - 无实现
- `Tool` - 工具应用
- `SubFlow` - 子流程
- `Block` - 块活动

---

## 3. 路由级属性

路由属性存储在路由定义级别，对应`RouteDefBean`中的扩展属性处理。

### 3.1 路由基本属性

| 属性名 | 属性类型 | 说明 | 所在类 |
|--------|----------|------|--------|
| **RouteOrder** | BPD | 路由顺序 | RouteDefBean.getRouteOrder() |
| **RouteDirection** | BPD | 路由方向 | RouteDefBean.getRouteDirection() |

**RouteDirection枚举值**：
- `FORWARD` - 前进
- `BACKWARD` - 后退

---

## 4. XPDL文件中的属性表示

### 4.1 流程级BPD属性示例

```xml
<ExtendedAttributes>
  <!-- 流程版本信息 -->
  <ExtendedAttribute Name="VersionId" Value="uuid-xxx" Type="BPD"/>
  <ExtendedAttribute Name="Classification" Value="办公流程" Type="BPD"/>
  <ExtendedAttribute Name="SystemCode" Value="OA" Type="BPD"/>
  <ExtendedAttribute Name="Description" Value="流程描述" Type="BPD"/>
  <ExtendedAttribute Name="ActiveTime" Value="2024-01-01 00:00:00" Type="BPD"/>
  <ExtendedAttribute Name="FreezeTime" Value="" Type="BPD"/>
  <ExtendedAttribute Name="CreatorName" Value="张三" Type="BPD"/>
  <ExtendedAttribute Name="ModifierId" Value="user001" Type="BPD"/>
  <ExtendedAttribute Name="ModifierName" Value="李四" Type="BPD"/>
  <ExtendedAttribute Name="ModifyTime" Value="2024-01-02 12:00:00" Type="BPD"/>
  
  <!-- 时限信息 -->
  <ExtendedAttribute Name="Limit" Value="0" Type="BPD"/>
  <ExtendedAttribute Name="DurationUnit" Value="D" Type="BPD"/>
  
  <!-- 监听器（特殊结构） -->
  <ExtendedAttribute Name="Listeners">
    <itjds:Listeners>
      <itjds:Listener Id="..." Name="..." ListenerEvent="..." RealizeClass="..."/>
    </itjds:Listeners>
  </ExtendedAttribute>
  
  <!-- 权限组（特殊结构） -->
  <ExtendedAttribute Name="RightGroups">
    <itjds:RightGroups>
      <itjds:RightGroup Id="..." Name="..." Code="..." Order="1" DefaultGroup="YES"/>
    </itjds:RightGroups>
  </ExtendedAttribute>
</ExtendedAttributes>
```

### 4.2 活动级WORKFLOW属性示例
