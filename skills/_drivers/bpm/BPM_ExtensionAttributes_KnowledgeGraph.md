# BPM流程引擎扩展属性知识图谱

## 1. 核心架构概览

### 1.1 三层定义结构

```
┌─────────────────────────────────────────────────────────────────┐
│                    流程定义 (ProcessDef)                         │
│  文件: EIProcessDef.java / DbProcessDef.java                     │
│  基本属性: processDefId, name, description, classification       │
│           systemCode, accessLevel                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 流程定义版本 (ProcessDefVersion)                  │
│  文件: EIProcessDefVersion.java / DbProcessDefVersion.java       │
│  基本属性: processDefVersionId, version, publicationStatus       │
│           activeTime, freezeTime, creatorId, modifierId          │
│  扩展属性: attributeTopMap, attributeIdMap (BPM_PROCESSDEF_PROPERTY)
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐    ┌─────────────────────────┐
│      活动定义            │    │      路由定义            │
│   (ActivityDef)         │    │    (RouteDef)           │
│                         │    │                         │
│ 基本属性:                │    │ 基本属性:                │
│ - activityDefId         │    │ - routeDefId            │
│ - name, description     │    │ - name, description     │
│ - position (START/END)  │    │ - fromActivityDefId     │
│ - implementation        │    │ - toActivityDefId       │
│ - limit, alertTime      │    │ - routeOrder            │
│ - canRouteBack          │    │ - routeDirection        │
│ - join, split           │    │ - routeCondition        │
│                         │    │                         │
│ 扩展属性表:              │    │ 扩展属性表:              │
│ BPM_PROCESSDEF_PROPERTY │    │ BPM_PROCESSDEF_PROPERTY │
└─────────────────────────┘    └─────────────────────────┘
```

### 1.2 扩展属性核心类

```
EIAttributeDef (接口)
    │
    ├── DbAttributeDef (数据库实现)
    │       ├── id: String (UUID)
    │       ├── name: String (属性名)
    │       ├── value: String (属性值)
    │       ├── type: String (属性类型, 默认"BPD")
    │       ├── interpretClass: String (解释器类)
    │       ├── parentId: String (父属性ID)
    │       ├── isExtension: Integer (是否扩展)
    │       └── canInstantiate: String
    │
    └── AttributeDefProxy (代理实现)
```

## 2. 扩展属性分类树

### 2.1 流程级别扩展属性

```
ProcessDefVersion (流程版本)
├── startPosition: JSON (START活动坐标)
│   └── 写入: ProcessDefManagerService.saveProcessDef()
│   └── 读取: ProcessDefManagerService.convertActivityDef()
│   └── 格式: {"x": number, "y": number}
│
├── endPosition: JSON (END活动坐标)
│   └── 写入: ProcessDefManagerService.saveProcessDef()
│   └── 读取: ProcessDefManagerService.convertActivityDef()
│   └── 格式: {"x": number, "y": number}
│
└── [其他流程级扩展属性]
    └── 通过 getAttributeValue() / setAttribute() 存取
```

### 2.2 活动级别扩展属性

```
ActivityDef (活动定义)
├── WORKFLOW (工作流属性组)
│   ├── positionCoord: JSON (普通活动坐标)
│   │   └── 写入: ProcessDefManagerService.saveProcessDef()
│   │   └── 读取: ProcessDefManagerService.convertActivityDef()
│   │   └── 路径: WORKFLOW.positionCoord
│   │
│   └── [其他工作流属性]
│
├── RIGHT (权限属性组)
│   ├── performType: String (执行类型)
│   ├── performSequence: String (执行顺序)
│   ├── specialSendScope: Enum (特送范围)
│   ├── canInsteadSign: String (是否代签)
│   ├── canTakeBack: String (是否收回)
│   ├── canReSend: String (是否重发)
│   ├── insteadSignSelected: String (代签选择)
│   ├── performerSelectedId: String (执行人选择ID)
│   ├── readerSelectedId: String (读者选择ID)
│   ├── movePerformerTo: Enum (执行人移动目标)
│   ├── moveSponsorTo: Enum (发起人移动目标)
│   ├── moveReaderTo: Enum (读者移动目标)
│   ├── surrogateId: String (代理人ID)
│   └── surrogateName: String (代理人名称)
│   └── 读取: DbActivityDefRightManager.loadByKey()
│   └── 枚举: ActivityDefRightAtt, RightGroupEnums, ActivityDefSpecialSendScope
│
├── FORM (表单属性组)
│   ├── formId: String (表单ID)
│   ├── formName: String (表单名称)
│   ├── formType: String (表单类型)
│   └── [表单字段权限配置]
│   └── 读取: DbActivityDefFormManager
│
├── SERVICE (服务属性组)
│   ├── httpMethod: Enum (HTTP方法)
│   ├── httpRequestType: Enum (请求类型)
│   ├── httpResponseType: Enum (响应类型)
│   ├── httpServiceParams: String (服务参数)
│   ├── httpUrl: String (服务URL)
│   └── serviceSelectedId: String (服务选择ID)
│   └── 读取: DbActivityDefServiceManager
│   └── 枚举: HttpMethod, RequestType, ResponseType
│
├── EVENT (事件属性组)
│   ├── eventType: String (事件类型)
│   ├── eventTrigger: String (事件触发器)
│   └── [事件监听配置]
│   └── 读取: DbActivityDefEventManager
│
├── DEVICE (设备属性组)
│   └── [IoT设备相关配置]
│   └── 读取: DbActivityDefDeviceManager
│
└── [自定义扩展属性]
    └── 通过 getAttributeValue() / setAttribute() 存取
```

### 2.3 路由级别扩展属性

```
RouteDef (路由定义)
├── RouteDirection: String (路由方向)
│   └── 枚举: FORWARD, BACKWARD
│   └── 写入: ProcessDefManagerService.saveProcessDef()
│   └── 读取: RouteDefBean.getRouteDirection()
│
├── RouteOrder: int (路由顺序)
│   └── 写入: ProcessDefManagerService.saveProcessDef()
│   └── 读取: RouteDefBean.getRouteOrder()
│
├── RouteConditionType: String (条件类型)
│   └── 如: CONDITION, OTHERWISE
│
├── [监听器配置]
│   └── 通过 Listeners 扩展属性存储
│
└── [自定义扩展属性]
    └── 通过 getAttributeValue() / setAttribute() 存取
```

## 3. 属性存取矩阵 (谁写/谁读)

### 3.1 流程定义版本属性

| 属性名 | 写入位置 | 读取位置 | 数据格式 | 转换损失 |
|--------|----------|----------|----------|----------|
| startPosition | ProcessDefManagerService.saveProcessDef() | ProcessDefManagerService.convertActivityDef() | JSON String | 无 |
| endPosition | ProcessDefManagerService.saveProcessDef() | ProcessDefManagerService.convertActivityDef() | JSON String | 无 |
| [其他属性] | setAttribute() | getAttributeValue() | String | 无 |

### 3.2 活动定义属性

| 属性组 | 属性名 | 写入位置 | 读取位置 | 数据格式 | 转换损失 |
|--------|--------|----------|----------|----------|----------|
| 基本属性 | position | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getPosition() | String (POSITION_START/END/NORMAL) | 无 |
| 基本属性 | implementation | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getImplementation() | String | 无 |
| 基本属性 | limit | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getLimit() | int | 无 |
| 基本属性 | alertTime | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getAlertTime() | int | 无 |
| 基本属性 | durationUnit | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getDurationUnit() | String | 无 |
| 基本属性 | canRouteBack | ProcessDefManagerService.saveProcessDef() | EIActivityDef.getCanRouteBack() | String (Y/N) | 无 |
| WORKFLOW | positionCoord | ProcessDefManagerService.saveProcessDef() | activityDef.getAttributeValue("WORKFLOW.positionCoord") | JSON String | 无 |
| RIGHT | performType | [权限管理器] | DbActivityDefRightManager.loadByKey() | String | 无 |
| RIGHT | specialSendScope | [权限管理器] | DbActivityDefRightManager.loadByKey() | Enum String | 需转换 |
| FORM | formId | [表单管理器] | DbActivityDefFormManager | String | 无 |
| SERVICE | httpMethod | [服务管理器] | DbActivityDefServiceManager | Enum String | 需转换 |

### 3.3 路由定义属性

| 属性名 | 写入位置 | 读取位置 | 数据格式 | 转换损失 |
|--------|----------|----------|----------|----------|
| routeOrder | ProcessDefManagerService.saveProcessDef() | EIRouteDef.getRouteOrder() | int | 无 |
| routeDirection | ProcessDefManagerService.saveProcessDef() | EIRouteDef.getRouteDirection() | String | 无 |
| routeCondition | ProcessDefManagerService.saveProcessDef() | EIRouteDef.getRouteCondition() | String | 无 |
| routeConditionType | ProcessDefManagerService.saveProcessDef() | EIRouteDef.getRouteConditionType() | String | 无 |

## 4. JSON存取转换分析

### 4.1 当前JSON转换逻辑

```java
// 写入示例 (ProcessDefManagerService.saveProcessDef())
Map<String, Object> positionCoord = ...;
String positionCoordJson = mapper.writeValueAsString(positionCoord);

// START/END活动坐标 -> 流程版本扩展属性
DbAttributeDef startPosAttr = new DbAttributeDef();
startPosAttr.setName("startPosition");
startPosAttr.setValue(positionCoordJson);
startPosAttr.setType("BPD");
activeVersion.setAttribute("startPosition", startPosAttr);

// 普通活动坐标 -> 活动扩展属性
DbAttributeDef workflowAttr = new DbAttributeDef();
workflowAttr.setName("WORKFLOW");
workflowAttr.setType("WORKFLOW");
activity.setAttribute(null, workflowAttr);

DbAttributeDef posCoordAttr = new DbAttributeDef();
posCoordAttr.setName("positionCoord");
posCoordAttr.setValue(positionCoordJson);
posCoordAttr.setType("WORKFLOW");
activity.setAttribute("WORKFLOW", posCoordAttr);

// 读取示例 (ProcessDefManagerService.convertActivityDef())
String positionCoord = activityDef.getAttributeValue("WORKFLOW.positionCoord");
Map<String, Object> positionCoordMap = mapper.readValue(positionCoord, Map.class);
```

### 4.2 转换一致性检查

| 检查项 | 状态 | 说明 |
|--------|------|------|
| START/END坐标保存位置 | ⚠️ 不一致 | 保存在流程版本而非活动自身 |
| 属性名大小写 | ⚠️ 不一致 | 代码中有时大写有时小写 |
| JSON序列化 | ✅ 一致 | 使用ObjectMapper统一处理 |
| 属性路径分隔符 | ✅ 一致 | 使用"."分隔 |
| 属性类型标记 | ⚠️ 不一致 | 有时用"BPD"有时用"WORKFLOW" |

## 5. 需要建立枚举约束的属性

### 5.1 已有枚举定义的属性

| 属性 | 枚举类 | 枚举值示例 | 位置 |
|------|--------|------------|------|
| position | ActivityDefPosition | POSITION_START, POSITION_END, POSITION_NORMAL | bpm.enums.activitydef |
| routeDirection | RouteDirction | FORWARD, BACKWARD | bpm.enums.route |
| specialSendScope | ActivityDefSpecialSendScope | [待查] | bpm.enums.activitydef |
| movePerformerTo | RightGroupEnums | [待查] | bpm.enums.right |
| httpMethod | HttpMethod | GET, POST, PUT, DELETE | [待查] |
| requestType | RequestType | [待查] | [待查] |
| responseType | ResponseType | [待查] | [待查] |

### 5.2 建议建立枚举的属性

| 属性 | 当前类型 | 建议枚举值 | 理由 |
|------|----------|------------|------|
| implementation | String | NO, TOOL, SUBFLOW, OUTFLOW, BLOCK | 活动实现方式固定 |
| canRouteBack | String | YES, NO | 布尔语义 |
| canSpecialSend | String | YES, NO | 布尔语义 |
| durationUnit | String | Y, M, D, H, m, s, W | 时间单位固定 |
| publicationStatus | String | UNDER_REVISION, RELEASED, UNDER_TEST | 版本状态固定 |
| accessLevel | String | PUBLIC, PRIVATE, BLOCK | 访问级别固定 |
| join | String | AND, XOR, OR | 汇聚类型固定 |
| split | String | AND, XOR, OR | 分支类型固定 |
| routeConditionType | String | CONDITION, OTHERWISE, EXCEPTION | 条件类型固定 |
| deadlineOperation | String | DEFAULT, [其他] | 到期操作固定 |

## 6. 现有保存设定与引擎读取的兼容性分析

### 6.1 兼容的部分 ✅

1. **基本属性映射**
   - processDefId, name, description 等基础字段映射正确
   - 活动position转换为POSITION_START/END/NORMAL格式

2. **坐标保存机制**
   - START/END坐标保存在流程版本级别
   - 普通活动坐标保存在活动WORKFLOW属性组

3. **路由基本属性**
   - routeOrder, routeDirection, routeCondition等保存正确

### 6.2 不兼容或潜在问题 ⚠️

1. **扩展属性丢失**
   - 当前saveProcessDef()仅保存positionCoord，其他扩展属性未保存
   - RIGHT, FORM, SERVICE等属性组在保存时丢失

2. **属性类型不一致**
   - 坐标属性有时标记为"BPD"，有时标记为"WORKFLOW"
   - 属性名大小写不统一

3. **监听器未处理**
   - 活动和路由的监听器在JSON转换中丢失

4. **子流程关联**
   - SubFlow/OutFlow/Block类型的实现类关联未处理

5. **权限属性未保存**
   - performType, specialSendScope等权限相关属性未在JSON保存中处理

### 6.3 建议改进

1. **统一属性类型标记**
   ```java
   // 建议统一使用大写属性组名
   public static final String ATTR_GROUP_WORKFLOW = "WORKFLOW";
   public static final String ATTR_GROUP_RIGHT = "RIGHT";
   public static final String ATTR_GROUP_FORM = "FORM";
   public static final String ATTR_GROUP_SERVICE = "SERVICE";
   ```

2. **完整扩展属性序列化**
   - 在保存时遍历所有扩展属性并序列化到JSON
   - 在读取时完整还原所有扩展属性

3. **枚举值校验**
   - 在保存时对枚举类型属性进行值校验
   - 提供默认值处理机制

4. **监听器支持**
   - 添加Listeners的JSON序列化/反序列化支持

## 7. 文件位置索引

### 7.1 核心接口
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\inter\EIProcessDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\inter\EIProcessDefVersion.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\inter\EIActivityDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\inter\EIRouteDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\inter\EIAttributeDef.java`

### 7.2 数据库实现
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\DbProcessDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\DbProcessDefVersion.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\DbActivityDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\DbRouteDef.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\DbAttributeDef.java`

### 7.3 XPDL Bean (XML/JSON转换)
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\AbstractBean.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\ProcessDefBean.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\ActivityDefBean.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\webservice\XPDLBean\RouteDefBean.java`

### 7.4 服务层 (JSON API)
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\service\ProcessDefManagerService.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\controller\ProcessDefDbController.java`

### 7.5 属性解释器
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\attribute\InterpreterManager.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\attribute\DefaultInterpreter.java`

### 7.6 权限/表单/服务/事件管理器
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\right\DbActivityDefRightManager.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\form\DbActivityDefFormManager.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\service\DbActivityDefServiceManager.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\database\event\DbActivityDefEventManager.java`

---

*文档生成时间: 2026-04-09*
*分析范围: BPM流程引擎定义相关核心代码*
