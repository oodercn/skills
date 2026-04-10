# BPM流程引擎前端JSON与后端属性完整映射关系

## 重要说明

本文档基于对原有XPDL存储实现的深入分析，遵循原有设计思想，不做任何方案修改，仅做映射关系梳理。

原有设计是一个成熟稳定的体系，核心设计原则：
1. **属性类型(Attributetype)** 是扩展属性的分类标识
2. **XPDL ExtendedAttributes** 是外部交换格式
3. **BPM_PROCESSDEF_PROPERTY表** 是持久化存储
4. **层级属性名** 使用"."分隔（如：RIGHT.performType）

---

## 1. 属性类型(Attributetype)体系

根据代码分析，Attributetype是一个枚举类，定义了以下属性分组：

| 属性类型 | 用途 | 存储位置 | 管理器 |
|----------|------|----------|--------|
| **BPD** | 基础流程定义属性 | BPM_PROCESSDEF_PROPERTY | 通用 |
| **WORKFLOW** | 工作流引擎属性 | BPM_PROCESSDEF_PROPERTY | 通用 |
| **RIGHT** | 权限相关属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefRightManager |
| **FORM** | 表单相关属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefFormManager |
| **SERVICE** | 服务调用属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefServiceManager |
| **EVENT** | 事件相关属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefEventManager |
| **DEVICE** | 设备/IoT属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefDeviceManager |
| **DEVICEEVENT** | 设备事件属性 | BPM_PROCESSDEF_PROPERTY | DbActivityDefEventManager |
| **CUSTOMIZE** | 自定义属性 | BPM_PROCESSDEF_PROPERTY | 通用 |

---

## 2. 流程定义版本(ProcessDefVersion)属性映射

### 2.1 基本属性（数据库字段）

| JSON字段 | 后端字段 | 数据库表字段 | 说明 |
|----------|----------|--------------|------|
| processDefVersionId | processDefVersionId | BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID | 版本UUID |
| processDefId | processDefId | BPM_PROCESSDEF_VERSION.PROCESSDEF_ID | 流程ID |
| version | version | BPM_PROCESSDEF_VERSION.VERSION | 版本号 |
| state | publicationStatus | BPM_PROCESSDEF_VERSION.PUBLICATIONSTATE | 版本状态 |
| createTime | created | BPM_PROCESSDEF_VERSION.CREATED | 创建时间 |
| activeTime | activeTime | BPM_PROCESSDEF_VERSION.ACTIVETIME | 激活时间 |
| freezeTime | freezeTime | BPM_PROCESSDEF_VERSION.FREEZETIME | 冻结时间 |
| description | description | BPM_PROCESSDEF_VERSION.DESCRIPTION | 版本描述 |

### 2.2 扩展属性（BPM_PROCESSDEF_PROPERTY表）

| JSON字段 | 属性路径 | 属性类型 | 说明 |
|----------|----------|----------|------|
| classification | Classification | BPD | 流程分类 |
| systemCode | SystemCode | BPD | 系统代码 |
| versionId | VersionId | BPD | 版本ID（冗余） |
| description | Description | BPD | 描述（扩展属性） |
| activeTime | ActiveTime | BPD | 激活时间（字符串） |
| freezeTime | FreezeTime | BPD | 冻结时间（字符串） |
| creatorName | CreatorName | BPD | 创建人姓名 |
| modifierId | ModifierId | BPD | 修改人ID |
| modifierName | ModifierName | BPD | 修改人姓名 |
| modifyTime | ModifyTime | BPD | 修改时间 |
| startPosition | startPosition | BPD | START活动坐标(JSON) |
| endPosition | endPosition | BPD | END活动坐标(JSON) |

### 2.3 流程级监听器(Listeners)

存储方式：扩展属性中Name="Listeners"的特殊结构

```xml
<ExtendedAttribute Name="Listeners">
  <itjds:Listeners>
    <itjds:Listener Id="..." Name="..." ListenerEvent="..." RealizeClass="..."/>
  </itjds:Listeners>
</ExtendedAttribute>
```

### 2.4 自定义权限组(RightGroups)

存储方式：扩展属性中Name="RightGroups"的特殊结构

```xml
<ExtendedAttribute Name="RightGroups">
  <itjds:RightGroups>
    <itjds:RightGroup Id="..." Name="..." Code="..." Order="..." DefaultGroup="..."/>
  </itjds:RightGroups>
</ExtendedAttribute>
```

---

## 3. 活动定义(ActivityDef)属性映射

### 3.1 基本属性（数据库字段）

| JSON字段 | 后端字段 | 数据库表字段 | 说明 |
|----------|----------|--------------|------|
| activityDefId | activityDefId | BPM_ACTIVITYDEF.ACTIVITYDEF_ID | 活动UUID |
| processDefId | processDefId | BPM_ACTIVITYDEF.PROCESSDEF_ID | 流程ID |
| processDefVersionId | processDefVersionId | BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID | 版本ID |
| name | name | BPM_ACTIVITYDEF.DEFNAME | 活动名称 |
| description | description | BPM_ACTIVITYDEF.DESCRIPTION | 活动描述 |
| position | position | BPM_ACTIVITYDEF.POSITION | 位置(START/END/NORMAL) |
| implementation | implementation | BPM_ACTIVITYDEF.IMPLEMENTATION | 实现方式 |
| limitTime | limit | BPM_ACTIVITYDEF.LIMIT | 时限 |
| alertTime | alertTime | BPM_ACTIVITYDEF.ALERTTIME | 预警时间 |
| durationUnit | durationUnit | BPM_ACTIVITYDEF.DURATIONUNIT | 时间单位 |
| deadlineOperation | deadlineOperation | BPM_ACTIVITYDEF.DEADLINEOPERATION | 到期操作 |
| canRouteBack | canRouteBack | BPM_ACTIVITYDEF.CANROUTEBACK | 是否可退回 |
| routeBackMethod | routeBackMethod | BPM_ACTIVITYDEF.TAKEBACKMETHOD | 退回方法 |
| canSpecialSend | canSpecialSend | BPM_ACTIVITYDEF.CANSPECIALSEND | 是否可特送 |
| join | join | BPM_ACTIVITYDEF.JOIN | 汇聚类型 |
| split | split | BPM_ACTIVITYDEF.SPLIT | 分支类型 |
| execClass | execClass | BPM_ACTIVITYDEF.EXECCLASS | 执行类 |

### 3.2 WORKFLOW属性组

| JSON字段 | 属性路径 | 属性类型 | 说明 |
|----------|----------|----------|------|
| positionCoord | WORKFLOW.positionCoord | WORKFLOW | 活动坐标(JSON) |

### 3.3 RIGHT属性组（权限引擎）

| JSON字段 | 属性路径 | 属性类型 | 枚举/格式 | 说明 |
|----------|----------|----------|-----------|------|
| performType | RIGHT.performType | RIGHT | String | 执行类型 |
| performSequence | RIGHT.performSequence | RIGHT | String | 执行顺序 |
| specialSendScope | RIGHT.specialSendScope | RIGHT | ActivityDefSpecialSendScope | 特送范围 |
| canInsteadSign | RIGHT.canInsteadSign | RIGHT | Y/N | 是否可代签 |
| canTakeBack | RIGHT.canTakeBack | RIGHT | Y/N | 是否可收回 |
| canReSend | RIGHT.canReSend | RIGHT | Y/N | 是否可重发 |
| insteadSignSelected | RIGHT.insteadSignSelected | RIGHT | String | 代签选择ID |
| performerSelectedId | RIGHT.performerSelectedId | RIGHT | String | 执行人选择ID |
| readerSelectedId | RIGHT.readerSelectedId | RIGHT | String | 读者选择ID |
| movePerformerTo | RIGHT.movePerformerTo | RIGHT | RightGroupEnums | 执行人移动目标 |
| moveSponsorTo | RIGHT.moveSponsorTo | RIGHT | RightGroupEnums | 发起人移动目标 |
| moveReaderTo | RIGHT.moveReaderTo | RIGHT | RightGroupEnums | 读者移动目标 |
| surrogateId | RIGHT.surrogateId | RIGHT | String | 代理人ID |
| surrogateName | RIGHT.surrogateName | RIGHT | String | 代理人名称 |

### 3.4 FORM属性组（表单引擎）

| JSON字段 | 属性路径 | 属性类型 | 说明 |
|----------|----------|----------|------|
| formId | FORM.formId | FORM | 表单ID |
| formName | FORM.formName | FORM | 表单名称 |
| formType | FORM.formType | FORM | 表单类型 |
| [字段权限] | FORM.[fieldId].[right] | FORM | 字段权限配置 |

### 3.5 SERVICE属性组（服务引擎）

| JSON字段 | 属性路径 | 属性类型 | 枚举/格式 | 说明 |
|----------|----------|----------|-----------|------|
| httpMethod | SERVICE.httpMethod | SERVICE | HttpMethod | HTTP方法 |
| requestType | SERVICE.httpRequestType | SERVICE | RequestType | 请求类型 |
| responseType | SERVICE.httpResponseType | SERVICE | ResponseType | 响应类型 |
| serviceParams | SERVICE.httpServiceParams | SERVICE | String | 服务参数 |
| url | SERVICE.httpUrl | SERVICE | String | 服务URL |
| serviceSelectedId | SERVICE.serviceSelectedId | SERVICE | String | 服务选择ID |

### 3.6 DEVICE属性组（设备引擎）

| JSON字段 | 属性路径 | 属性类型 | 枚举/格式 | 说明 |
|----------|----------|----------|-----------|------|
| performType | DEVICE.performType | DEVICE | ActivityDefDevicePerformtype | 执行类型 |
| performSequence | DEVICE.performSequence | DEVICE | ActivityDefDevicePerformSequence | 执行顺序 |
| specialSendScope | DEVICE.specialSendScope | DEVICE | ActivityDefDeviceSpecial | 特送范围 |
| canTakeBack | DEVICE.canTakeBack | DEVICE | CommonYesNoEnum | 是否可收回 |
| canReSend | DEVICE.canReSend | DEVICE | CommonYesNoEnum | 是否可重发 |
| deviceSelectedId | DEVICE.deviceSelectedId | DEVICE | String | 设备选择ID |

### 3.7 DEVICEEVENT属性组（设备事件）

| JSON字段 | 属性路径 | 属性类型 | 枚举/格式 | 说明 |
|----------|----------|----------|-----------|------|
| alertTime | DEVICEEVENT.alertTime | DEVICEEVENT | String | 预警时间 |
| attributeName | DEVICEEVENT.attributeName | DEVICEEVENT | DeviceDataTypeKey | 属性名 |
| deadLineOperation | DEVICEEVENT.deadLineOperation | DEVICEEVENT | ActivityDefDeadLineOperation | 到期操作 |
| deviceApi | DEVICEEVENT.deviceApi | DEVICEEVENT | DeviceAPIEventEnums | 设备API事件 |
| deviceSelectedId | DEVICEEVENT.deviceSelectedId | DEVICEEVENT | String | 设备选择ID |

### 3.8 活动级监听器(Listeners)

存储方式：扩展属性中Name="Listeners"的特殊结构（同流程级）

---

## 4. 路由定义(RouteDef)属性映射

### 4.1 基本属性（数据库字段）

| JSON字段 | 后端字段 | 数据库表字段 | 说明 |
|----------|----------|--------------|------|
| routeDefId | routeDefId | BPM_ROUTEDEF.ROUTEDEF_ID | 路由UUID |
| processDefId | processDefId | BPM_ROUTEDEF.PROCESSDEF_ID | 流程ID |
| processDefVersionId | processDefVersionId | BPM_ROUTEDEF.PROCESSDEF_VERSION_ID | 版本ID |
| name | name | BPM_ROUTEDEF.DEFNAME | 路由名称 |
| description | description | BPM_ROUTEDEF.DESCRIPTION | 路由描述 |
| fromActivityDefId | fromActivityDefId | BPM_ROUTEDEF.FROMACTIVITYDEF_ID | 起点活动ID |
| toActivityDefId | toActivityDefId | BPM_ROUTEDEF.TOACTIVITYDEF_ID | 终点活动ID |
| routeOrder | routeOrder | BPM_ROUTEDEF.ROUTEORDER | 路由顺序 |
| routeDirection | routeDirection | BPM_ROUTEDEF.ROUTEDIRECTION | 路由方向 |
| routeCondition | routeCondition | BPM_ROUTEDEF.ROUTECONDITION | 路由条件 |
| routeConditionType | routeConditionType | BPM_ROUTEDEF.ROUTECONDITIONTYPE | 条件类型 |

### 4.2 路由扩展属性

| JSON字段 | 属性路径 | 属性类型 | 说明 |
|----------|----------|----------|------|
| [自定义属性] | [属性名] | BPD/WORKFLOW | 路由级自定义属性 |

### 4.3 路由级监听器(Listeners)

存储方式：扩展属性中Name="Listeners"的特殊结构

---

## 5. XPDL与JSON的转换规则

### 5.1 XPDL ExtendedAttribute结构

```xml
<ExtendedAttributes>
  <!-- 基础属性 -->
  <ExtendedAttribute Type="BPD" Name="Classification" Value="办公流程"/>
  <ExtendedAttribute Type="BPD" Name="SystemCode" Value="OA"/>
  
  <!-- 分组属性（使用.分隔） -->
  <ExtendedAttribute Type="RIGHT" Name="RIGHT.performType" Value="Sequential"/>
  <ExtendedAttribute Type="RIGHT" Name="RIGHT.specialSendScope" Value="ALL"/>
  <ExtendedAttribute Type="WORKFLOW" Name="WORKFLOW.positionCoord" Value="{&quot;x&quot;:100,&quot;y&quot;:200}"/>
  
  <!-- 监听器（特殊结构） -->
  <ExtendedAttribute Name="Listeners">
    <itjds:Listeners>
      <itjds:Listener Id="..." Name="..." ListenerEvent="..." RealizeClass="..."/>
    </itjds:Listeners>
  </ExtendedAttribute>
</ExtendedAttributes>
```

### 5.2 属性类型(Type)的确定规则

根据AbstractBean.getExtendedAttributesList()方法：

1. **从XPDL读取时**：
   - 优先使用ExtendedAttribute的Type属性
   - 如果Type为空，默认为"BPD"
   - 属性名使用"."分隔层级（如：RIGHT.performType）

2. **保存到数据库时**：
   - 属性类型存储在DbAttributeDef.type字段
   - 属性名存储在DbAttributeDef.name字段（不含前缀）
   - 完整路径通过parentId关联构建

3. **从数据库读取时**：
   - 通过attributeTopMap和attributeIdMap重建层级
   - 使用getAttribute("Type.Name")方式访问

### 5.3 特殊属性处理

以下属性在XPDL中作为ExtendedAttribute存储，但在数据库中是基本字段：

**活动级**：
- DurationUnit → BPM_ACTIVITYDEF.DURATIONUNIT
- AlertTime → BPM_ACTIVITYDEF.ALERTTIME
- DeadLineOperation → BPM_ACTIVITYDEF.DEADLINEOPERATION
- CanRouteBack → BPM_ACTIVITYDEF.CANROUTEBACK
- RouteBackMethod → BPM_ACTIVITYDEF.TAKEBACKMETHOD
- CanSpecialSend → BPM_ACTIVITYDEF.CANSPECIALSEND
- Position → BPM_ACTIVITYDEF.POSITION

**流程版本级**：
- Classification → 从BPM_PROCESSDEF读取
- SystemCode → 从BPM_PROCESSDEF读取
- VersionId → BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID

---

## 6. 前端JSON结构建议

### 6.1 流程定义JSON结构

```json
{
  "processDefId": "uuid",
  "name": "流程名称",
  "description": "流程描述",
  "classification": "办公流程",
  "systemCode": "OA",
  "accessLevel": "PUBLIC",
  "version": 1,
  "state": "RELEASED",
  "extendedAttributes": {
    "startPosition": {"x": 100, "y": 100},
    "endPosition": {"x": 500, "y": 100}
  },
  "listeners": [
    {"id": "...", "name": "...", "event": "...", "class": "..."}
  ],
  "rightGroups": [
    {"id": "...", "name": "...", "code": "...", "order": 1, "default": true}
  ],
  "activities": [...],
  "routes": [...]
}
```

### 6.2 活动定义JSON结构

```json
{
  "activityDefId": "uuid",
  "name": "活动名称",
  "description": "活动描述",
  "position": "NORMAL",
  "activityType": "TASK",
  "implementation": "No",
  "limitTime": 0,
  "alertTime": 0,
  "durationUnit": "D",
  "canRouteBack": "N",
  "positionCoord": {"x": 200, "y": 200},
  "join": "XOR",
  "split": "XOR",
  "extendedAttributes": {
    "WORKFLOW": {
      "positionCoord": "{\"x\":200,\"y\":200}"
    },
    "RIGHT": {
      "performType": "Sequential",
      "specialSendScope": "ALL",
      "canInsteadSign": "Y",
      "performerSelectedId": "..."
    },
    "FORM": {
      "formId": "...",
      "formName": "..."
    },
    "SERVICE": {
      "httpMethod": "POST",
      "httpUrl": "..."
    }
  },
  "listeners": [...]
}
```

### 6.3 路由定义JSON结构

```json
{
  "routeDefId": "uuid",
  "name": "路由名称",
  "from": "activityId1",
  "to": "activityId2",
  "routeOrder": 1,
  "routeDirection": "FORWARD",
  "routeConditionType": "CONDITION",
  "condition": "${approve == true}",
  "listeners": [...]
}
```

---

## 7. 关键设计要点总结

### 7.1 属性存储位置决策树

```
属性应该存在哪里？
├── 是否跨版本共享？
│   ├── 是 → BPM_PROCESSDEF表（流程定义级）
│   └── 否 → 继续判断
├── 是否是标准XPDL属性？
│   ├── 是 → BPM_ACTIVITYDEF/BPM_ROUTEDEF表（基本字段）
│   └── 否 → 继续判断
├── 属于哪个业务域？
│   ├── 权限 → RIGHT属性组
│   ├── 表单 → FORM属性组
│   ├── 服务 → SERVICE属性组
│   ├── 设备 → DEVICE/DEVICEEVENT属性组
│   └── 其他 → BPD/WORKFLOW/CUSTOMIZE属性组
└── 存储到BPM_PROCESSDEF_PROPERTY表
```

### 7.2 属性命名规范

1. **属性类型(Attributetype)**：全大写（RIGHT, FORM, SERVICE等）
2. **属性名**：驼峰命名（performType, specialSendScope等）
3. **完整路径**：Type.Name（如：RIGHT.performType）
4. **层级分隔**：使用"."（如：FORM.field1.readRight）

### 7.3 与原有XPDL设计的兼容性

原有XPDL设计通过以下机制保证扩展性：

1. **ExtendedAttributes容器**：所有非标准XPDL属性都存储于此
2. **Type属性**：标识属性分类（对应Attributetype）
3. **Name属性**：使用.分隔的层级路径
4. **特殊结构**：Listeners/RightGroups使用嵌套XML结构

前端JSON设计应遵循以上原则，确保与原有XPDL体系的兼容性。

---

## 8. 文件索引

### 8.1 XPDL转换核心
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\AbstractBean.java` - 扩展属性解析基类
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\ProcessDefVersionBean.java` - 流程版本XPDL转换
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\ActivityDefBean.java` - 活动XPDL转换
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\RouteDefBean.java` - 路由XPDL转换

### 8.2 属性管理器
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\right\DbActivityDefRightManager.java` - 权限属性
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\form\DbActivityDefFormManager.java` - 表单属性
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\service\DbActivityDefServiceManager.java` - 服务属性
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\event\DbActivityDefEventManager.java` - 事件属性
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\device\DbActivityDefDeviceManager.java` - 设备属性

---

*文档生成时间: 2026-04-09*
*基于原有XPDL存储实现分析，遵循成熟稳定的设计体系*
