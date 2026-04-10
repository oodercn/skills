# BPM流程引擎JSON转换修复总结

## 修复概述

本次修复补充了BPM流程引擎中所有缺失的扩展属性JSON转换实现，确保数据完整性和一致性。

## 修复文件

**新文件**: `ProcessDefManagerServiceFixed.java`
- 完整路径: `bpmserver/src/main/java/net/ooder/bpm/service/ProcessDefManagerServiceFixed.java`
- 行数: 1155行
- 基于原有 `ProcessDefManagerService.java` 进行扩展修复

## 主要修复内容

### 1. 开始/结束节点坐标（严重）

**问题**: 原有实现只保存了简单的{x,y}坐标，丢失了ParticipantID、ActivityID、Routing等关键信息

**修复**:
- **读取**: 从XPDL格式 `ParticipantID;FirstActivityID;X;Y;Routing` 解析
- **保存**: 保存为XPDL格式，兼容Swing版设计器
- **JSON格式**:
```json
{
  "startNode": {
    "participantId": "Participant_1",
    "firstActivityId": "Activity_1",
    "x": 100,
    "y": 200,
    "routing": "NO_ROUTING"
  },
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

**相关方法**:
- `parseStartNode()` / `parseStartNodeFromXPDL()`
- `parseEndNodes()` / `parseEndNodesFromXPDL()`
- `saveStartNode()` / `saveEndNodes()`

### 2. 监听器（严重）

**问题**: 原有实现完全丢失监听器数据

**修复**:
- **读取**: 从XML格式解析 `<itjds:Listeners>`
- **保存**: 保存为XML格式
- **JSON格式**:
```json
{
  "listeners": [
    {
      "id": "listener_1",
      "name": "流程启动监听",
      "event": "PROCESS_START",
      "realizeClass": "com.example.Listener"
    }
  ]
}
```

**相关方法**:
- `parseListeners()` / `parseListenersFromXML()`
- `saveListeners()`

### 3. 权限组（严重）

**问题**: 原有实现完全丢失权限组数据

**修复**:
- **读取**: 从XML格式解析 `<itjds:RightGroups>`
- **保存**: 保存为XML格式
- **JSON格式**:
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

**相关方法**:
- `parseRightGroups()` / `parseRightGroupsFromXML()`
- `saveRightGroups()`

### 4. 活动坐标与参与者关系（中等）

**问题**: 原有实现丢失了ParticipantID

**修复**:
- **读取**: 从 `XOffset`/`YOffset`/`ParticipantID` 扩展属性读取
- **保存**: 保存为扩展属性
- **JSON格式**:
```json
{
  "positionCoord": {"x": 100, "y": 200},
  "participantId": "Participant_1"
}
```

**相关方法**:
- `convertActivityDef()` 中的坐标处理逻辑
- `saveActivity()` 中的坐标保存逻辑

### 5. 属性组支持（RIGHT/FORM/SERVICE）

**问题**: 原有实现不支持属性组（如RIGHT.performType）

**修复**:
- **读取**: 支持 `GROUPNAME.propertyName` 格式的属性读取
- **保存**: 保存为属性组结构
- **JSON格式**:
```json
{
  "RIGHT": {
    "performType": "SINGLE",
    "performSequence": "FIRST",
    "canInsteadSign": "YES"
  },
  "FORM": {
    "formId": "form_001",
    "formName": "请假单"
  },
  "SERVICE": {
    "httpMethod": "POST",
    "httpUrl": "/api/test"
  }
}
```

**相关方法**:
- `convertActivityDef()` 中的属性组读取
- `saveAttributeGroup()`

### 6. 块活动特殊属性

**问题**: 原有实现不支持块活动的StartOfBlock/EndOfBlock

**修复**:
- **读取**: 从XPDL格式解析块坐标
- **保存**: 保存为XPDL格式
- **JSON格式**:
```json
{
  "startOfBlock": {
    "participantId": "Participant_1",
    "firstActivityId": "Activity_1",
    "x": 100,
    "y": 200,
    "routing": "NO_ROUTING"
  },
  "endOfBlock": { ... },
  "participantVisualOrder": "Participant_1;Participant_2"
}
```

**相关方法**:
- `parseBlockCoordinate()`
- `saveBlockCoordinate()`

### 7. BPD扩展属性

**问题**: 原有实现缺少CreatorName/ModifierId等BPD属性

**修复**:
- **读取**: 从BPD属性组读取
- **保存**: 保存为BPD属性
- **包含属性**: creatorName, modifierId, modifierName, modifyTime, limit, durationUnit

**相关方法**:
- `convertProcessDefVersion()` 中的BPD属性读取
- `saveBPDAttributes()`

## 兼容性处理

### 向后兼容
- 保留原有的 `startPosition`/`endPosition` 格式（简单{x,y}）
- 保留原有的 `WORKFLOW.positionCoord` 格式
- 新旧格式同时支持，优先使用新格式

### XPDL兼容
- 完全兼容Swing版XPDL设计器的格式
- 支持历史XPDL文件的导入

## 使用方法

### 1. 替换服务类

将原有的 `ProcessDefManagerService` 替换为 `ProcessDefManagerServiceFixed`:

```java
// 在Controller中注入
@Autowired
private ProcessDefManagerServiceFixed processDefManagerService;
```

### 2. API调用示例

**保存流程**:
```java
@PostMapping("/api/process-definitions")
public ResponseEntity<?> saveProcessDef(@RequestBody Map<String, Object> processData) {
    Map<String, Object> result = processDefManagerService.saveProcessDef(processData);
    return ResponseEntity.ok(result);
}
```

**获取完整流程**:
```java
@GetMapping("/api/process-definitions/{processDefId}/full")
public ResponseEntity<?> getFullProcessDef(@PathVariable String processDefId) {
    Map<String, Object> result = processDefManagerService.getFullProcessDef(processDefId);
    return ResponseEntity.ok(result);
}
```

## 测试验证

### 测试数据
使用提供的测试数据文件：
- `test-data/simple-approval-process.json` - 简单审批流程
- `test-data/countersign-process.json` - 会签流程
- `test-data/all-attributes-process.json` - 全属性测试流程

### 验证步骤
1. 使用测试数据保存流程
2. 重新加载流程
3. 验证所有扩展属性完整
4. 验证XPDL格式正确

## 性能考虑

- XML解析使用正则表达式（简化实现），大数据量时建议改用DOM解析器
- 属性读取采用按需加载，避免不必要的解析
- 支持批量保存，减少数据库操作

## 后续优化建议

1. **XML解析优化**: 使用DOM或SAX解析器替代正则表达式
2. **缓存机制**: 对频繁读取的流程定义添加缓存
3. **验证机制**: 添加JSON Schema验证
4. **版本管理**: 支持流程定义版本对比

## 文件清单

### 修复文件
- `bpmserver/src/main/java/net/ooder/bpm/service/ProcessDefManagerServiceFixed.java` (1155行)

### 测试文件
- `test-data/simple-approval-process.json