# BPM 扩展属性 JSON 转换修复总结

## 修复完成时间
2026-04-09

## 修复范围
本次修复针对 `ProcessDefManagerService.java` 中的 JSON 转换逻辑，补充了所有缺失的扩展属性处理，确保与原有 Swing XPDL 编辑器的数据格式完全兼容。

---

## 修复内容清单

### 1. 流程版本级扩展属性 (EIProcessDefVersion)

#### 1.1 开始节点 (StartOfWorkflow)
- **XPDL格式**: `ParticipantID;FirstActivityID;X;Y;Routing`
- **修复前**: 丢失 ParticipantID、FirstActivityID、Routing
- **修复后**: 完整解析和保存所有字段
- **JSON格式**:
```json
{
  "startNode": {
    "participantId": "Participant_Start",
    "firstActivityId": "act_start",
    "positionCoord": {"x": 50, "y": 200},
    "routing": "NO_ROUTING"
  }
}
```

#### 1.2 结束节点 (EndOfWorkflow)
- **XPDL格式**: `ParticipantID;LastActivityID;X;Y;Routing`（多个用 `|` 分隔）
- **修复前**: 丢失 ParticipantID、LastActivityID、Routing
- **修复后**: 支持多结束节点，完整解析所有字段
- **JSON格式**:
```json
{
  "endNodes": [
    {
      "participantId": "Participant_End",
      "lastActivityId": "act_end",
      "positionCoord": {"x": 800, "y": 200},
      "routing": "NO_ROUTING"
    }
  ]
}
```

#### 1.3 监听器 (Listeners)
- **XPDL格式**: XML格式 `<itjds:Listeners><itjds:Listener .../></itjds:Listeners>`
- **修复前**: 完全丢失
- **修复后**: 完整解析和保存
- **JSON格式**:
```json
{
  "listeners": [
    {
      "id": "listener_001",
      "name": "流程启动监听",
      "event": "PROCESS_START",
      "realizeClass": "com.example.Listener"
    }
  ]
}
```

#### 1.4 权限组 (RightGroups)
- **XPDL格式**: XML格式 `<itjds:RightGroups><itjds:RightGroup .../></itjds:RightGroups>`
- **修复前**: 完全丢失
- **修复后**: 完整解析和保存
- **JSON格式**:
```json
{
  "rightGroups": [
    {
      "id": "rg_001",
      "name": "默认组",
      "code": "DEFAULT",
      "order": 1,
      "defaultGroup": true
    }
  ]
}
```

#### 1.5 BPD扩展属性
- **修复前**: 部分丢失
- **修复后**: 完整支持
- **属性列表**:
  - `CreatorName` - 创建者名称
  - `ModifierId` - 修改者ID
  - `ModifierName` - 修改者名称
  - `ModifyTime` - 修改时间
  - `Limit` - 时限
  - `DurationUnit` - 时间单位 (M/H/D/W)

---

### 2. 活动级扩展属性 (EIActivityDef)

#### 2.1 坐标属性
- **XPDL属性**: `XOffset`, `YOffset`
- **修复前**: 仅支持简单的 `{x, y}` JSON格式
- **修复后**: 同时支持 XPDL 属性和 JSON 格式
- **JSON格式**:
```json
{
  "positionCoord": {"x": 300, "y": 200}
}
```

#### 2.2 参与者ID (ParticipantID)
- **XPDL属性**: `ParticipantID`
- **修复前**: 丢失
- **修复后**: 完整保存和读取
- **JSON格式**:
```json
{
  "participantId": "Participant_Main"
}
```

#### 2.3 WORKFLOW属性组
- **属性列表**:
  - `DeadLineOperation` - 逾期操作
  - `SpecialScope` - 特殊范围
- **JSON格式**:
```json
{
  "WORKFLOW": {
    "deadLineOperation": "NOTIFY",
    "specialScope": "ALL"
  }
}
```

#### 2.4 RIGHT属性组
- **属性列表**:
  - `performType` - 执行类型 (SINGLE/JOINTSIGN/COUNTERSIGN)
  - `performSequence` - 执行顺序 (FIRST/MEANWHILE/SEQUENCE)
  - `specialSendScope` - 特殊发送范围
  - `canInsteadSign` - 允许代签
  - `canTakeBack` - 允许收回
  - `canReSend` - 允许重发
  - `insteadSignSelected` - 代签选择
  - `performerSelectedId` - 执行人选择
  - `readerSelectedId` - 阅读人选择
  - `movePerformerTo` - 移动执行人到
  - `moveSponsorTo` - 移动发起人到
  - `moveReaderTo` - 移动阅读人到
  - `surrogateId` - 代理人ID
  - `surrogateName` - 代理人名称
- **JSON格式**:
```json
{
  "RIGHT": {
    "performType": "JOINTSIGN",
    "performSequence": "MEANWHILE",
    "canInsteadSign": "YES",
    "canTakeBack": "YES",
    "canReSend": "YES",
    "insteadSignSelected": "user_001",
    "performerSelectedId": "user_002",
    "readerSelectedId": "user_003",
    "movePerformerTo": "rg_002",
    "moveSponsorTo": "rg_001",
    "moveReaderTo": "rg_004",
    "surrogateId": "user_004",
    "surrogateName": "代理人姓名"
  }
}
```

#### 2.5 FORM属性组
- **属性列表**:
  - `formId` - 表单ID
  - `formName` - 表单名称
  - `formType` - 表单类型 (CUSTOM/SYSTEM/EXTERNAL)
  - `formUrl` - 表单URL
- **JSON格式**:
```json
{
  "FORM": {
    "formId": "form_001",
    "formName": "请假申请表",
    "formType": "CUSTOM",
    "formUrl": "/forms/leave/form.html"
  }
}
```

#### 2.6 SERVICE属性组
- **属性列表**:
  - `httpMethod` - HTTP方法 (GET/POST/PUT/DELETE/PATCH)
  - `httpUrl` - HTTP URL
  - `httpRequestType` - 请求类型 (JSON/XML/FORM/TEXT)
  - `httpResponseType` - 响应类型 (JSON/XML/FORM/TEXT)
  - `httpServiceParams` - 服务参数
  - `serviceSelectedId` - 服务选择ID
- **JSON格式**:
```json
{
  "SERVICE": {
    "httpMethod": "POST",
    "httpUrl": "https://api.example.com/test",
    "httpRequestType": "JSON",
    "httpResponseType": "JSON",
    "httpServiceParams": "{\"param1\":\"value1\"}",
    "serviceSelectedId": "svc_001"
  }
}
```

#### 2.7 块活动特殊属性
- **属性列表**:
  - `StartOfBlock` - 块开始坐标 (XPDL格式)
  - `EndOfBlock` - 块结束坐标 (XPDL格式)
  - `ParticipantVisualOrder` - 参与者视觉顺序
- **JSON格式**:
```json
{
  "startOfBlock": {
    "participantId": "Participant_Block",
    "firstActivityId": "act_block_start",
    "positionCoord": {"x": 200, "y": 300},
    "routing": "NO_ROUTING"
  },
  "endOfBlock": {
    "participantId": "Participant_Block",
    "lastActivityId": "act_block_end",
    "positionCoord": {"x": 600, "y": 300},
    "routing": "NO_ROUTING"
  },
  "participantVisualOrder": "1"
}
```

---

### 3. 路由级扩展属性 (EIRouteDef)

#### 3.1 Routing属性
- **XPDL属性**: `Routing`
- **修复前**: 丢失
- **修复后**: 完整支持
- **JSON格式**:
```json
{
  "routing": "NO_ROUTING"
}
```

---

## 关键修复代码

### 开始节点解析 (parseStartNodeFromXPDL)
```java
private Map<String, Object> parseStartNodeFromXPDL(String startOfWorkflow) {
    String[] parts = startOfWorkflow.split(";");
    if (parts.length < 5) {
        log.warn("Invalid StartOfWorkflow format: {}", startOfWorkflow);
        return null;
    }
    
    Map<String, Object> startNode = new LinkedHashMap<>();
    startNode.put("participantId", parts[0]);
    startNode.put("firstActivityId", parts[1]);
    
    Map<String, Object> positionCoord = new LinkedHashMap<>();
    positionCoord.put("x", Integer.parseInt(parts[2]));
    positionCoord.put("y", Integer.parseInt(parts[3]));
    startNode.put("positionCoord", positionCoord);
    startNode.put("routing", parts[4]);
    
    return startNode;
}
```

### 监听器XML解析 (parseListenersFromXML)
```java
private List<Map<String, Object>> parseListenersFromXML(String xml) {
    List<Map<String, Object>> listeners = new ArrayList<>();
    
    Pattern pattern = Pattern.compile(
        "<itjds:Listener\\s+Id=\"([^\"]+)\"\\s+Name=\"([^\"]+)\"\\s+ListenerEvent=\"([^\"]+)\"\\s+RealizeClass=\"([^\"]+)\""
    );
    Matcher matcher = pattern.matcher(xml);
    
    while (matcher.find()) {
        Map<String, Object> listener = new LinkedHashMap<>();
        listener.put("id", matcher.group(1));
        listener.put("name", matcher.group(2));
        listener.put("event", matcher.group(3));
        listener.put("realizeClass", matcher.group(4));
        listeners.add(listener);
    }
    
    return listeners;
}
```

### 属性组保存 (saveAttributeGroup)
```java
private void saveAttributeGroup(EIActivityDef activity, String groupName, Map<String, Object> attrs) 
        throws BPMException {
    // 创建属性组根节点
    DbAttributeDef groupAttr = new DbAttributeDef();
    groupAttr.setId(UUID.randomUUID().toString());
    groupAttr.setName(groupName);
    groupAttr.setType(groupName);
    activity.setAttribute(null, groupAttr);
    
    // 保存组内属性
    for (Map.Entry<String, Object> entry : attrs.entrySet()) {
        if (entry.getValue() != null) {
            DbAttributeDef attr = new DbAttributeDef();
            attr.setId(UUID.randomUUID().toString());
            attr.setName(entry.getKey());
            attr.setValue(entry.getValue().toString());
            attr.setType(groupName);
            activity.setAttribute(groupName, attr);
        }
    }
}
```

---

## 测试覆盖

### 测试文件位置
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\test\java\net\ooder\bpm\service\ProcessDefManagerServiceIntegrationTest.java`
- `e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\test\java\net\ooder\bpm\service\XpdlCompatibilityTest.java`

### 测试数据文件
- `e:\github\ooder-skills\skills\_drivers\bpm\test-data\all-attributes-process.json` - 全属性测试
- `e:\github\ooder-skills\skills\_drivers\bpm\test-data\simple-approval-process.json` - 简单审批流程
- `e:\github\ooder-skills\skills\_drivers\bpm\test-data\countersign-process.json` - 会签流程

### 测试用例统计
- 全属性流程测试: 1个
- 简单审批流程测试: 1个
- 会签流程测试: 1个
- 坐标保存测试: 1个
- 监听器XML转换测试: 1个
- 权限组XML转换测试: 1个
- 属性组保存测试: 1个
- XPDL格式兼容性测试: 1个
- 多结束节点测试: 1个
- getFullProcessDef测试: 1个
- XPDL格式定义测试: 20+个

**总计: 30+ 个测试用例**

---

## 运行测试

### 使用测试脚本
```batch
e:\github\ooder-skills\skills\_drivers\bpm\run-tests.bat
```

### 使用Maven
```bash
cd e:\github\ooder-skills\skills\_drivers\bpm\bpmserver
mvn test -Dtest=XpdlCompatibilityTest
mvn test -Dtest=ProcessDefManagerServiceIntegrationTest
```

---

## 枚举约束建议

基于修复过程中识别的属性值，建议建立以下枚举约束：

### Attributetype 枚举
```java
public enum Attributetype {
    BPD,        // 基础流程定义属性
    WORKFLOW,   // 工作流属性
    RIGHT,      // 权限属性
    FORM,       // 表单属性
    SERVICE,    // 服务属性
    EVENT,      // 事件属性
    DEVICE      // 设备属性
}
```

### 活动位置枚举
```java
public enum ActivityPosition {
    POSITION_START,   // 开始节点
    POSITION_END,     // 结束节点
    POSITION_NORMAL   // 普通节点
}
```

### Join/Split 类型枚举
```java
public enum JoinSplitType {
    XOR,  // 异或
    OR,   // 或
    AND   // 与
}
```

### 执行类型枚举
```java
public enum PerformType {
    SINGLE,      // 单人
    JOINTSIGN,   // 会签
    COUNTERSIGN  // 核签
}
```

### 执行顺序枚举
```java
public enum PerformSequence {
    FIRST,      // 先到先得
    MEANWHILE,  //