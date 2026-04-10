# Y坐标丢失问题测试用例

## 问题描述

流程设计器保存活动节点坐标后，重新加载时Y坐标被重置为默认值100，X坐标被重置为400。

## 测试数据

### 上行数据（前端发送给后端）
```json
{
  "activityDefId": "act_1775777196873_6xl5vcknc",
  "name": "用户任务",
  "description": "",
  "position": "NORMAL",
  "activityType": "TASK",
  "positionCoord": {
    "x": 124,
    "y": 308
  },
  "participantId": "",
  "implementation": "No",
  "limitTime": 0,
  "alertTime": 0,
  "durationUnit": "D",
  "join": "XOR",
  "split": "XOR",
  "canRouteBack": "NO",
  "routeBackMethod": "PREV",
  "canSpecialSend": "NO",
  "specialScope": "ALL",
  "startOfBlock": null,
  "endOfBlock": null,
  "participantVisualOrder": "",
  "extendedAttributes": {
    "activityCategory": "HUMAN",
    "agentConfig": null,
    "sceneConfig": null
  }
}
```

### 下行数据（后端返回给前端）
```json
{
  "activityDefId": "act_1775777196873_6xl5vcknc",
  "name": "用户任务",
  "description": "",
  "position": "NORMAL",
  "activityType": "TASK",
  "activityCategory": "HUMAN",
  "implementation": "No",
  "execClass": null,
  "positionCoord": {
    "x": 400,
    "y": 100
  },
  "timing": {},
  "routing": {},
  "right": {
    "moveSponsorTo": "SPONSOR"
  },
  "subFlow": {},
  "device": {},
  "service": {},
  "event": {},
  "agentConfig": null,
  "sceneConfig": null,
  "extendedAttributes": {}
}
```

## 差异对比

| 字段 | 上行值 | 下行值 | 状态 |
|------|--------|--------|------|
| positionCoord.x | 124 | 400 | ❌ 被修改 |
| positionCoord.y | 308 | 100 | ❌ 被修改 |
| activityCategory | 在extendedAttributes中 | 提升到顶层 | ⚠️ 结构变化 |
| extendedAttributes | 包含数据 | 为空对象 | ❌ 数据丢失 |

## 问题根因分析

### 1. 坐标被重置为默认值

在 `DesignerService.java` 第71-79行发现默认坐标设置代码：

```java
Map<String, Object> posCoord = (Map<String, Object>) a.get("positionCoord");
if (posCoord != null) {
    activity.setPositionCoord(posCoord);
} else {
    Map<String, Object> defaultCoord = new HashMap<>();
    defaultCoord.put("x", 100 + process.getActivities().size() * 150);
    defaultCoord.put("y", 100);
    activity.setPositionCoord(defaultCoord);
}
```

**问题**: 当 `positionCoord` 为null时使用默认值，但返回的400,100与公式 `100 + size * 150` 不符（如果size=2，应该是400）。

### 2. 数据结构转换问题

后端返回的数据结构与前端发送的结构不一致：
- `activityCategory` 从 `extendedAttributes` 提升到顶层
- `extendedAttributes` 被重置为空对象

## 测试步骤

### 前置条件
1. 启动BPMServer（端口8082）
2. 启动BPM Designer（端口8081或8088）
3. 确保数据库已初始化

### 测试步骤

#### 步骤1: 创建流程
1. 打开设计器 http://localhost:8088/designer/index.html
2. 创建新流程或打开现有流程
3. 添加一个用户任务节点
4. 将节点拖拽到坐标 (124, 308)

#### 步骤2: 保存流程
1. 点击保存按钮
2. 观察浏览器开发者工具的Network面板
3. 记录请求体中的 `positionCoord` 值（预期: {"x":124,"y":308}）

#### 步骤3: 重新加载流程
1. 刷新页面
2. 重新打开刚才保存的流程
3. 观察浏览器开发者工具的Network面板
4. 记录响应体中的 `positionCoord` 值

#### 步骤4: 验证结果
- **预期结果**: `positionCoord` 应该保持为 `{"x":124,"y":308}`
- **实际结果**: `positionCoord` 被重置为 `{"x":400,"y":100}`

### 数据库验证

#### 验证数据是否正确保存
```sql
SELECT * FROM BPM_ATTRIBUTEDEF 
WHERE ATTRIBUTENAME = 'positionCoord' 
AND ACTIVITYDEF_ID = 'act_1775777196873_6xl5vcknc';
```

**预期结果**: `ATTRIBUTEVALUE` 字段应该包含 `{"x":124,"y":308}`

#### 验证属性是否正确关联
```sql
SELECT * FROM BPM_ACTIVITYDEF_PROPERTY 
WHERE PROPNAME = 'positionCoord' 
AND ACTIVITYDEF_ID = 'act_1775777196873_6xl5vcknc';
```

**预期结果**: `PROPVALUE` 字段应该包含 `{"x":124,"y":308}`

## 修复建议

### 方案1: 修复DesignerService.java

修改 `DesignerService.java` 第71-79行，确保正确读取坐标：

```java
Map<String, Object> posCoord = (Map<String, Object>) a.get("positionCoord");
if (posCoord != null && posCoord.get("x") != null && posCoord.get("y") != null) {
    activity.setPositionCoord(posCoord);
} else {
    // 只有在坐标真正为空时才使用默认值
    Map<String, Object> defaultCoord = new HashMap<>();
    defaultCoord.put("x", 100 + process.getActivities().size() * 150);
    defaultCoord.put("y", 100);
    activity.setPositionCoord(defaultCoord);
}
```

### 方案2: 修复数据转换逻辑

检查 `formatProcessDef` 方法，确保坐标数据正确传递：

```java
// 在ProcessDefDbController.java中
formattedActivity.put("positionCoord", activity.get("positionCoord"));
```

### 方案3: 添加日志追踪

在关键位置添加日志，追踪坐标数据的变化：

```java
// 在ProcessDefManagerService.java中
log.debug("Saving positionCoord: {}", positionCoordJson);

// 在ProcessDefDbController.java中
log.debug("Returning positionCoord: {}", activity.get("positionCoord"));
```

## 优先级

**高** - 影响流程设计器的正常使用，用户体验差。

## 相关文件

- `bpm-designer/src/main/java/net/ooder/bpm/designer/service/DesignerService.java`
- `bpmserver/src/main/java/net/ooder/bpm/controller/ProcessDefDbController.java`
- `bpmserver/src/main/java/net/ooder/bpm/service/ProcessDefManagerService.java`
- `bpm-designer/src/main/resources/static/designer/js/sdk/DataAdapter.js`

## 备注

- 问题可能不仅限于Y坐标，X坐标也被修改了
- 需要检查是否其他属性也有类似问题
- 建议添加端到端测试，确保数据一致性
