# WorkflowClient逐项读取测试文档

## 1. 测试概述

### 1.1 测试目标
使用WorkflowClient API逐项读取流程定义的所有属性，验证数据完整性和一致性。

### 1.2 测试范围
- 流程级属性（BPD、开始/结束节点坐标、监听器、权限组）
- 活动级属性（WORKFLOW、RIGHT、FORM、SERVICE属性组）
- 路由级属性

### 1.3 测试数据
使用以下测试数据文件：
- `simple-approval-process.json` - 简单审批流程
- `countersign-process.json` - 会签流程
- `all-attributes-process.json` - 全属性测试流程

---

## 2. WorkflowClient API读取方法

### 2.1 流程定义读取

```java
// 获取流程定义
EIProcessDef processDef = workflowClient.getProcessDef(processDefId);

// 获取流程定义版本
EIProcessDefVersion version = workflowClient.getProcessDefVersion(processDefVersionId);

// 获取所有版本
List<EIProcessDefVersion> versions = workflowClient.getProcessDefVersions(processDefId);
```

### 2.2 活动定义读取

```java
// 获取活动定义
EIActivityDef activityDef = workflowClient.getActivityDef(activityDefId);

// 获取流程的所有活动
List<EIActivityDef> activities = workflowClient.getActivityDefs(processDefVersionId);

// 获取属性值
String value = activityDef.getAttributeValue("RIGHT.performType");
```

### 2.3 路由定义读取

```java
// 获取路由定义
EIRouteDef routeDef = workflowClient.getRouteDef(routeDefId);

// 获取流程的所有路由
List<EIRouteDef> routes = workflowClient.getRouteDefs(processDefVersionId);
```

---

## 3. 流程级属性读取测试

### 3.1 BPD标准属性读取

```java
@Test
public void testReadProcessDefBasicAttributes() {
    // 读取流程定义
    EIProcessDef processDef = workflowClient.getProcessDef("proc_leave_001");
    
    // 验证基本属性
    assertEquals("proc_leave_001", processDef.getProcessDefId());
    assertEquals("请假审批流程", processDef.getName());
    assertEquals("员工请假审批流程，包含填写申请、部门经理审批、HR审批", processDef.getDescription());
    assertEquals("人事流程", processDef.getClassification());
    assertEquals("HR", processDef.getSystemCode());
    assertEquals("PUBLIC", processDef.getAccessLevel());
    
    System.out.println("✓ 流程基本属性读取成功");
}

@Test
public void testReadProcessDefVersionAttributes() {
    // 读取流程定义版本
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    // 验证版本属性
    assertEquals("ver_leave_001_v1", version.getProcessDefVersionId());
    assertEquals("proc_leave_001", version.getProcessDefId());
    assertEquals(1, version.getVersion());
    assertEquals("RELEASED", version.getPublicationStatus());
    
    // 验证时间属性
    assertNotNull(version.getCreated());
    assertNotNull(version.getActiveTime());
    
    // 验证人员属性
    assertEquals("系统管理员", version.getAttributeValue("CreatorName"));
    assertEquals("admin", version.getAttributeValue("ModifierId"));
    assertEquals("系统管理员", version.getAttributeValue("ModifierName"));
    
    System.out.println("✓ 流程版本属性读取成功");
}
```

### 3.2 开始/结束节点坐标读取（关键）

```java
@Test
public void testReadStartNodeCoordinates() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    // 读取开始节点坐标
    String startOfWorkflow = version.getAttributeValue("StartOfWorkflow");
    assertNotNull("开始节点坐标不能为空", startOfWorkflow);
    
    // 解析格式: ParticipantID;FirstActivityID;X;Y;Routing
    String[] parts = startOfWorkflow.split(";");
    assertEquals(5, parts.length);
    
    assertEquals("Participant_Employee", parts[0]);
    assertEquals("act_fill_form", parts[1]);
    assertEquals("100", parts[2]);
    assertEquals("300", parts[3]);
    assertEquals("NO_ROUTING", parts[4]);
    
    System.out.println("✓ 开始节点坐标读取成功: " + startOfWorkflow);
}

@Test
public void testReadEndNodeCoordinates() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    // 读取结束节点坐标
    String endOfWorkflow = version.getAttributeValue("EndOfWorkflow");
    assertNotNull("结束节点坐标不能为空", endOfWorkflow);
    
    // 解析格式: ParticipantID;LastActivityID;X;Y;Routing
    String[] parts = endOfWorkflow.split(";");
    assertEquals(5, parts.length);
    
    assertEquals("Participant_Employee", parts[0]);
    assertEquals("act_notify", parts[1]);
    assertEquals("1000", parts[2]);
    assertEquals("300", parts[3]);
    assertEquals("NO_ROUTING", parts[4]);
    
    System.out.println("✓ 结束节点坐标读取成功: " + endOfWorkflow);
}

@Test
public void testReadMultipleEndNodes() {
    // 测试多个结束节点的情况
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_multi_end_v1");
    
    // 读取结束节点坐标（多个）
    String endOfWorkflow = version.getAttributeValue("EndOfWorkflow");
    assertNotNull("结束节点坐标不能为空", endOfWorkflow);
    
    // 多个结束节点用分隔符分隔
    String[] endNodes = endOfWorkflow.split("\\|");
    assertTrue("应该有多个结束节点", endNodes.length > 1);
    
    for (String endNode : endNodes) {
        String[] parts = endNode.split(";");
        assertEquals(5, parts.length);
        System.out.println("✓ 结束节点: " + endNode);
    }
}
```

### 3.3 监听器读取（关键）

```java
@Test
public void testReadListeners() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    // 获取监听器属性
    EIAttributeDef listenersAttr = version.getAttribute("Listeners");
    assertNotNull("监听器属性不能为空", listenersAttr);
    
    // 获取监听器列表
    List<Listener> listeners = parseListeners(listenersAttr.getValue());
    assertFalse("监听器列表不能为空", listeners.isEmpty());
    
    // 验证监听器属性
    Listener listener1 = listeners.get(0);
    assertEquals("listener_process_start", listener1.getId());
    assertEquals("流程启动监听", listener1.getName());
    assertEquals("PROCESS_START", listener1.getEvent());
    assertEquals("com.ooder.bpm.listener.ProcessStartLogger", listener1.getRealizeClass());
    
    System.out.println("✓ 监听器读取成功，共 " + listeners.size() + " 个监听器");
}

@Test
public void testReadAllListenerEvents() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_all_attrs_001_v1");
    
    List<Listener> listeners = getListeners(version);
    
    // 验证所有监听事件类型
    Set<String> events = listeners.stream()
        .map(Listener::getEvent)
        .collect(Collectors.toSet());
    
    assertTrue("应包含PROCESS_START", events.contains("PROCESS_START"));
    assertTrue("应包含PROCESS_END", events.contains("PROCESS_END"));
    assertTrue("应包含ACTIVITY_START", events.contains("ACTIVITY_START"));
    assertTrue("应包含ACTIVITY_END", events.contains("ACTIVITY_END"));
    assertTrue("应包含ROUTE_TAKE", events.contains("ROUTE_TAKE"));
    assertTrue("应包含ASSIGNMENT", events.contains("ASSIGNMENT"));
    
    System.out.println("✓ 所有监听事件类型读取成功");
}
```

### 3.4 权限组读取（关键）

```java
@Test
public void testReadRightGroups() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    // 获取权限组属性
    EIAttributeDef rightGroupsAttr = version.getAttribute("RightGroups");
    assertNotNull("权限组属性不能为空", rightGroupsAttr);
    
    // 获取权限组列表
    List<RightGroup> rightGroups = parseRightGroups(rightGroupsAttr.getValue());
    assertFalse("权限组列表不能为空", rightGroups.isEmpty());
    
    // 验证权限组属性
    RightGroup rg1 = rightGroups.get(0);
    assertEquals("rg_employee", rg1.getId());
    assertEquals("员工组", rg1.getName());
    assertEquals("EMPLOYEE", rg1.getCode());
    assertEquals(1, rg1.getOrder());
    assertTrue(rg1.isDefaultGroup());
    
    System.out.println("✓ 权限组读取成功，共 " + rightGroups.size() + " 个权限组");
}

@Test
public void testReadRightGroupOrder() {
    EIProcessDefVersion version = workflowClient.getProcessDefVersion("ver_leave_001_v1");
    
    List<RightGroup> rightGroups = getRightGroups(version);
    
    // 验证权限组顺序
    int previousOrder = 0;
    for (RightGroup rg : rightGroups) {
        assertTrue("权限组顺序应递增", rg.getOrder() > previousOrder);
        previousOrder = rg.getOrder();
    }
    
    System.out.println("✓ 权限组顺序验证成功");
}
```

---

## 4. 活动级属性读取测试

### 4.1 视觉位置属性读取（关键）

```java
@Test
public void testReadActivityPosition() {
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    
    // 验证位置类型
    assertEquals("NORMAL", activity.getPosition());
    
    // 验证坐标
    String xOffset = activity.getAttributeValue("XOffset");
    String yOffset = activity.getAttributeValue("YOffset");
    
    assertNotNull("XOffset不能为空", xOffset);
    assertNotNull("YOffset不能为空", yOffset);
    assertEquals("250", xOffset);
    assertEquals("300", yOffset);
    
    System.out.println("✓ 活动坐标读取成功: (" + xOffset + ", " + yOffset + ")");
}

@Test
public void testReadActivityParticipantId() {
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    
    // 验证参与者ID
    String participantId = activity.getAttributeValue("ParticipantID");
    assertNotNull("ParticipantID不能为空", participantId);
    assertEquals("Participant_Employee", participantId);
    
    System.out.println("✓ 活动参与者ID读取成功: " + participantId);
}

@Test
public void testReadStartEndActivityPosition() {
    // 测试开始活动
    EIActivityDef startActivity = workflowClient.getActivityDef("act_start");
    assertEquals("START", startActivity.getPosition());
    
    // 测试结束活动
    EIActivityDef endActivity = workflowClient.getActivityDef("act_end");
    assertEquals("END", endActivity.getPosition());
    
    System.out.println("✓ 开始/结束活动位置读取成功");
}
```

### 4.2 WORKFLOW属性组读取

```java
@Test
public void testReadActivityWorkflowAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_dept_approval");
    
    // 验证时限属性
    assertEquals(48, activity.getLimit());
    assertEquals(24, activity.getAlertTime());
    assertEquals("H", activity.getDurationUnit());
    
    // 验证扩展属性
    String deadLineOperation = activity.getAttributeValue("DeadLineOperation");
    assertEquals("NOTIFY", deadLineOperation);
    
    System.out.println("✓ 活动WORKFLOW属性读取成功");
}

@Test
public void testReadActivityRouteControlAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_dept_approval");
    
    // 验证路由控制属性
    assertEquals("YES", activity.getCanRouteBack());
    assertEquals("PREV", activity.getRouteBackMethod());
    assertEquals("YES", activity.getCanSpecialSend());
    
    String specialScope = activity.getAttributeValue("SpecialScope");
    assertEquals("ALL", specialScope);
    
    System.out.println("✓ 活动路由控制属性读取成功");
}

@Test
public void testReadActivityJoinSplitAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_countersign_merge");
    
    // 验证汇聚分支属性
    assertEquals("AND", activity.getJoin());
    assertEquals("XOR", activity.getSplit());
    
    System.out.println("✓ 活动汇聚分支属性读取成功");
}
```

### 4.3 RIGHT属性组读取

```java
@Test
public void testReadActivityRightAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    
    // 读取RIGHT属性组
    String performType = activity.getAttributeValue("RIGHT.performType");
    String performSequence = activity.getAttributeValue("RIGHT.performSequence");
    String canInsteadSign = activity.getAttributeValue("RIGHT.canInsteadSign");
    String canTakeBack = activity.getAttributeValue("RIGHT.canTakeBack");
    String canReSend = activity.getAttributeValue("RIGHT.canReSend");
    
    // 验证属性值
    assertEquals("SINGLE", performType);
    assertEquals("FIRST", performSequence);
    assertEquals("NO", canInsteadSign);
    assertEquals("YES", canTakeBack);
    assertEquals("NO", canReSend);
    
    System.out.println("✓ 活动RIGHT属性读取成功");
}

@Test
public void testReadActivityRightPerformerAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    
    // 读取执行人相关属性
    String performerSelectedId = activity.getAttributeValue("RIGHT.performerSelectedId");
    String readerSelectedId = activity.getAttributeValue("RIGHT.readerSelectedId");
    
    assertEquals("current_user", performerSelectedId);
    
    System.out.println("✓ 活动执行人属性读取成功");
}

@Test
public void testReadActivityRightMoveAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_dept_approval");
    
    // 读取移动目标属性
    String movePerformerTo = activity.getAttributeValue("RIGHT.movePerformerTo");
    String moveSponsorTo = activity.getAttributeValue("RIGHT.moveSponsorTo");
    String moveReaderTo = activity.getAttributeValue("RIGHT.moveReaderTo");
    
    assertEquals("rg_manager", movePerformerTo);
    
    System.out.println("✓ 活动移动目标属性读取成功");
}

@Test
public void testReadActivityRightSurrogateAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_all_attrs");
    
    // 读取代理人属性
    String surrogateId = activity.getAttributeValue("RIGHT.surrogateId");
    String surrogateName = activity.getAttributeValue("RIGHT.surrogateName");
    
    assertEquals("user_surrogate_001", surrogateId);
    assertEquals("代理人姓名", surrogateName);
    
    System.out.println("✓ 活动代理人属性读取成功");
}
```

### 4.4 FORM属性组读取

```java
@Test
public void testReadActivityFormAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    
    // 读取FORM属性组
    String formId = activity.getAttributeValue("FORM.formId");
    String formName = activity.getAttributeValue("FORM.formName");
    String formType = activity.getAttributeValue("FORM.formType");
    String formUrl = activity.getAttributeValue("FORM.formUrl");
    
    // 验证属性值
    assertEquals("form_leave_application", formId);
    assertEquals("请假申请表", formName);
    assertEquals("CUSTOM", formType);
    assertEquals("/forms/leave/apply.html", formUrl);
    
    System.out.println("✓ 活动FORM属性读取成功");
}
```

### 4.5 SERVICE属性组读取

```java
@Test
public void testReadActivityServiceAttributes() {
    EIActivityDef activity = workflowClient.getActivityDef("act_archive");
    
    // 读取SERVICE属性组
    String httpMethod = activity.getAttributeValue("SERVICE.httpMethod");
    String httpUrl = activity.getAttributeValue("SERVICE.httpUrl");
    String httpRequestType = activity.getAttributeValue("SERVICE.httpRequestType");
    String httpResponseType = activity.getAttributeValue("SERVICE.httpResponseType");
    String httpServiceParams = activity.getAttributeValue("SERVICE.httpServiceParams");
    String serviceSelectedId = activity.getAttributeValue("SERVICE.serviceSelectedId");
    
    // 验证属性值
    assertEquals("POST", httpMethod);
    assertEquals("/api/leave/archive", httpUrl);
    assertEquals("JSON", httpRequestType);
    assertEquals("JSON", httpResponseType);
    assertEquals("{}", httpServiceParams);
    assertEquals("svc_archive", serviceSelectedId);
    
    System.out.println("✓ 活动SERVICE属性读取成功");
}
```

### 4.6 块活动特殊属性读取

```java
@Test
public void testReadBlockStartEndCoordinates() {
    EIActivityDef blockActivity = workflowClient.getActivityDef("act_block");
    
    // 读取块开始坐标
    String startOfBlock = blockActivity.getAttributeValue("StartOfBlock");
    assertNotNull("块开始坐标不能为空", startOfBlock);
    
    String[] startParts = startOfBlock.split(";");
    assertEquals(5, startParts.length);
    
    // 读取块结束坐标
    String endOfBlock = blockActivity.getAttributeValue("EndOfBlock");
    assertNotNull("块结束坐标不能为空", endOfBlock);
    
    String[] endParts = endOfBlock.split(";");
    assertEquals(5, endParts.length);
    
    System.out.println("✓ 块活动坐标读取成功");
}

@Test
public void testReadBlockParticipantVisualOrder() {
    EIActivityDef blockActivity = workflowClient.getActivityDef("act_block");
    
    // 读取块内参与者顺序
    String participantVisualOrder = blockActivity.getAttributeValue("ParticipantVisualOrder");
    assertNotNull("参与者顺序不能为空", participantVisualOrder);
    
    String[] participants = participantVisualOrder.split(";");
    assertTrue("应有多个参与者", participants.length > 0);
    
    System.out.println("✓ 块活动参与者顺序读取成功");
}
```

---

## 5. 路由级属性读取测试

### 5.1 路由基本属性读取

```java
@Test
public void testReadRouteBasicAttributes() {
    EIRouteDef route = workflowClient.getRouteDef("route_submit");
    
    // 验证基本属性
    assertEquals("route_submit", route.getRouteDefId());
    assertEquals("提交申请", route.getName());
    assertEquals("act_fill_form", route.getFromActivityDefId());
    assertEquals("act_dept_approval", route.getToActivityDefId());
    
    System.out.println("✓ 路由基本属性读取成功");
}

@Test
public void testReadRouteControlAttributes() {
    EIRouteDef route = workflowClient.getRouteDef("route_dept_approve");
    
    // 验证控制属性
    assertEquals(1, route.getRouteOrder());
    assertEquals("FORWARD", route.getRouteDirection());
    assertEquals("${approved == true}", route.getRouteCondition());
    assertEquals("CONDITION", route.getRouteConditionType());
    
    System.out.println("✓ 路由控制属性读取成功");
}

@Test
public void testReadRouteDirection() {
    // 测试前进路由
    EIRouteDef forwardRoute = workflowClient.getRouteDef("route_dept_approve");
    assertEquals("FORWARD", forwardRoute.getRouteDirection());
    
    // 测试后退路由
    EIRouteDef backwardRoute = workflowClient.getRouteDef("route_dept_reject");
    assertEquals("BACKWARD", backwardRoute.getRouteDirection());
    
    System.out.println("✓ 路由方向读取成功");
}
```

---

## 6. 完整流程读取测试

### 6.1 完整流程属性读取

```java
@Test
public void testReadCompleteProcess() {
    // 读取完整流程
    String processDefId = "proc_leave_001";
    String versionId = "ver_leave_001_v1";
    
    // 1. 读取流程定义
    EIProcessDef processDef = workflowClient.getProcessDef(processDefId);
    assertNotNull(processDef);
    
    // 2. 读取流程版本
    EIProcessDefVersion version = workflowClient.getProcessDefVersion(versionId);
    assertNotNull(version);
    
    // 3. 读取所有活动
    List<EIActivityDef> activities = workflowClient.getActivityDefs(versionId);
    assertFalse(activities.isEmpty());
    
    // 4. 读取所有路由
    List<EIRouteDef> routes = workflowClient.getRouteDefs(versionId);
    assertFalse(routes.isEmpty());
    
    // 5. 验证开始/结束节点坐标
    assertNotNull(version.getAttributeValue("StartOfWorkflow"));
    assertNotNull(version.getAttributeValue("EndOfWorkflow"));
    
    // 6. 验证监听器
    assertNotNull(version.getAttribute("Listeners"));
    
    // 7. 验证权限组
    assertNotNull(version.getAttribute("RightGroups"));
    
    System.out.println("✓ 完整流程读取成功");
    System.out.println("  - 活动数量: " + activities.size());
    System.out.println("  - 路由数量: " + routes.size());
}
```

---

## 7. 数据一致性验证测试

### 7.1 JSON到XPDL一致性验证

```java
@Test
public void testJSONToXPDLConsistency() {
    // 读取原始JSON
    JSONObject json = loadJSON("simple-approval-process.json");
    
    // 保存流程
    String processDefId = saveProcessFromJSON(json);
    
    // 读取保存后的流程
    EIProcessDefVersion version = workflowClient.getProcessDefVersion(processDefId + "_v1");
    
    // 验证关键属性一致性
    // 1. 开始节点坐标
    String startOfWorkflow = version.getAttributeValue("StartOfWorkflow");
    assertTrue(startOfWorkflow.contains("Participant_Employee"));
    assertTrue(startOfWorkflow.contains("act_fill_form"));
    
    // 2. 活动坐标
    EIActivityDef activity = workflowClient.getActivityDef("act_fill_form");
    assertEquals("250", activity.getAttributeValue("XOffset"));
    assertEquals("300", activity.getAttributeValue("YOffset"));
    
    // 3. 监听器
    assertNotNull(version.getAttribute("Listeners"));
    
    // 4. 权限组
    assertNotNull(version.getAttribute("RightGroups"));
    
    System.out.println("✓ JSON到XPDL一致性验证成功");
}
```

### 7.2 XPDL到JSON一致性验证

```java
@Test
public void testXPDLToJSONConsistency() {
    // 读取XPDL文件
    WorkflowProcess xpdl = loadXPDL("historical.xpdl");
    
    // 转换为JSON
    JSONObject json = convertXPDLToJSON(xpdl);
    
    // 验证关键属性
    // 1. 开始节点
    assertNotNull(json.getJSONObject("startNode"));
    assertEquals("Participant_1", json.getJSONObject("startNode").getString("participantId"));
    
    // 2. 结束节点
    assertNotNull(json.getJSONArray("endNodes"));
    
    // 3. 监听器
    assertNotNull(json.getJSONArray("listeners"));
    
    // 4. 权限组
    assertNotNull(json.getJSONArray("rightGroups"));
    
    System.out.println("✓ XPDL到JSON一致性验证成功");
}
```

---

## 8. 性能测试

### 8.1 大流程读取性能测试

```java
@Test
public void testLargeProcessReadPerformance() {
    String processDefId = "proc_large_001";
    
    long startTime = System.currentTimeMillis();
    
    // 读取大流程
    EIProcessDefVersion version = workflowClient.getProcessDefVersion(processDefId + "_v1");
    List<EIActivityDef> activities = workflowClient.getActivityDefs(version.getProcessDefVersionId());
    List<EIRouteDef> routes = workflowClient.getRouteDefs(version.getProcessDefVersionId());
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    System.out.println("✓ 大流程读取完成");
    System.out.println("  - 活动数量: " + activities.size());
    System.out.println("  - 路由数量: " + routes.size());
    System.out.println("  - 读取时间: " + duration + "ms");
    
    assertTrue("读取时间应小于3秒", duration < 3000);
}
```

---

## 9. 测试执行命令

### 9.1 运行所有测试

```bash
mvn test -Dtest=BPMExtensionAttributesTest
```

### 9.2 运行特定测试

```bash
# 只运行流程级属性测试
mvn test -Dtest=BPMExtensionAttributesTest#testReadProcessDefBasicAttributes

# 只运行活动级属性测试
mvn test -Dtest=BPMExtensionAttributesTest#testReadActivity*

# 只运行关键属性测试
mvn test -Dtest=BPMExtensionAttributesTest#testReadStartNodeCoordinates,testReadEndNodeCoordinates,testReadListeners,testReadRightGroups
```

### 9.3 生成测试报告

```bash
mvn test -Dtest=BPMExtensionAttributesTest -DgenerateReport=true
```

---

## 10. 测试数据准备

### 10.1 初始化测试数据

```java
@BeforeClass
public static void setupTestData() {
    // 加载测试数据
    loadTestProcess("simple-approval-process.json");
    loadTestProcess("countersign-process.json");
    loadTestProcess("all-attributes-process.json");
}
```

### 10.2 清理测试数据

```java
@AfterClass
public static void cleanupTestData() {
    // 清理测试数据
    deleteTestProcess("proc_leave_001");
    deleteTestProcess("proc_contract_001");
    deleteTestProcess("proc_all_attrs_001");
}
```

---

*文档结束*
