package net.ooder.bpm.test;

import net.ooder.bpm.test.client.BPMRestClient;
import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.server.JDSServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BPMAuditTest {

    @Autowired
    private BPMRestClient bpmRestClient;

    @BeforeAll
    static void setupJDSConfig() {
        Properties props = new Properties();
        props.setProperty("jds.home", System.getProperty("java.io.tmpdir"));
        props.setProperty("server.port", "8081");
        props.setProperty("configName", "test");
        
        JDSConfig.initForTest(props);
        CommonConfig.initForTest(props);
        
        try {
            JDSServer.setMockMode(true);
        } catch (Exception e) {
            System.err.println("JDSServer.setMockMode failed: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("审计1: 流程定义必须返回有效数据")
    void audit_ProcessDef_MustReturnValidData() {
        System.out.println("\n=== 审计1: 流程定义数据验证 ===");
        
        var result = bpmRestClient.getProcessDef("test-process-def");
        
        assertNotNull(result, "ResultModel 不能为 null");
        assertNotNull(result.getData(), "data 不能为 null");
        
        Map<String, Object> data = (Map<String, Object>) result.getData();
        
        assertEquals("test-process-def", data.get("processDefId"), "流程定义ID必须匹配");
        assertNotNull(data.get("name"), "流程定义名称不能为 null");
        assertNotNull(data.get("systemCode"), "系统代码不能为 null");
        
        System.out.println("流程定义审计通过: " + data);
    }

    @Test
    @Order(2)
    @DisplayName("审计2: 创建流程实例必须返回活动实例")
    void audit_NewProcess_MustReturnActivityInst() {
        System.out.println("\n=== 审计2: 创建流程实例验证 ===");
        
        var result = bpmRestClient.newProcess("leave-approval", "测试请假");
        
        assertNotNull(result, "ResultModel 不能为 null");
        assertNotNull(result.getData(), "创建流程实例必须返回数据 - 当前返回 null 表示 Mock 实现有问题!");
        
        Map<String, Object> data = (Map<String, Object>) result.getData();
        
        assertNotNull(data.get("activityInstId"), "活动实例ID不能为 null - 这是严重问题!");
        assertNotNull(data.get("processInstId"), "流程实例ID不能为 null");
        assertNotNull(data.get("activityDefId"), "活动定义ID不能为 null");
        
        System.out.println("创建流程实例审计通过: " + data);
    }

    @Test
    @Order(3)
    @DisplayName("审计3: 活动实例必须有正确的状态")
    void audit_ActivityInst_MustHaveValidState() {
        System.out.println("\n=== 审计3: 活动实例状态验证 ===");
        
        var result = bpmRestClient.getActivityInst("test-activity-001");
        
        assertNotNull(result, "ResultModel 不能为 null");
        assertNotNull(result.getData(), "data 不能为 null");
        
        Map<String, Object> data = (Map<String, Object>) result.getData();
        
        assertEquals("test-activity-001", data.get("activityInstId"), "活动实例ID必须匹配");
        assertNotNull(data.get("state"), "活动状态不能为 null");
        assertEquals("running", data.get("state"), "新创建的活动状态应为 running");
        
        System.out.println("活动实例状态: " + data.get("state"));
        System.out.println("canPerform: " + data.get("canPerform"));
        System.out.println("canSignReceive: " + data.get("canSignReceive"));
        System.out.println("canRouteBack: " + data.get("canRouteBack"));
    }

    @Test
    @Order(4)
    @DisplayName("审计4: 签收操作必须检查前置条件")
    void audit_SignReceive_MustCheckPrecondition() {
        System.out.println("\n=== 审计4: 签收前置条件验证 ===");
        
        String activityInstId = "audit-activity-001";
        
        var activityResult = bpmRestClient.getActivityInst(activityInstId);
        Map<String, Object> activityData = (Map<String, Object>) activityResult.getData();
        
        Boolean canSignReceive = (Boolean) activityData.get("canSignReceive");
        System.out.println("canSignReceive = " + canSignReceive);
        
        var signResult = bpmRestClient.signReceive(activityInstId);
        Map<String, Object> signData = (Map<String, Object>) signResult.getData();
        
        String mainCode = (String) signData.get("mainCode");
        System.out.println("signReceive mainCode = " + mainCode);
        
        if (Boolean.FALSE.equals(canSignReceive)) {
            System.out.println("警告: canSignReceive=false 但 signReceive 操作仍然成功! 这是业务逻辑漏洞!");
            assertEquals("MAINCODE_ERROR", mainCode, "当 canSignReceive=false 时，签收应该失败");
        }
    }

    @Test
    @Order(5)
    @DisplayName("审计5: 退回操作必须检查前置条件")
    void audit_RouteBack_MustCheckPrecondition() {
        System.out.println("\n=== 审计5: 退回前置条件验证 ===");
        
        String activityInstId = "audit-activity-002";
        
        var activityResult = bpmRestClient.getActivityInst(activityInstId);
        Map<String, Object> activityData = (Map<String, Object>) activityResult.getData();
        
        Boolean canRouteBack = (Boolean) activityData.get("canRouteBack");
        System.out.println("canRouteBack = " + canRouteBack);
        
        var routeBackResult = bpmRestClient.routeBack(activityInstId, "history-001");
        Map<String, Object> routeBackData = (Map<String, Object>) routeBackResult.getData();
        
        String mainCode = (String) routeBackData.get("mainCode");
        System.out.println("routeBack mainCode = " + mainCode);
        
        if (Boolean.FALSE.equals(canRouteBack)) {
            System.out.println("警告: canRouteBack=false 但 routeBack 操作仍然成功! 这是业务逻辑漏洞!");
            assertEquals("MAINCODE_ERROR", mainCode, "当 canRouteBack=false 时，退回应该失败");
        }
    }

    @Test
    @Order(6)
    @DisplayName("审计6: 完成任务后活动状态应改变")
    void audit_EndTask_ShouldChangeState() {
        System.out.println("\n=== 审计6: 任务完成状态变化验证 ===");
        
        String activityInstId = "audit-activity-003";
        
        var beforeResult = bpmRestClient.getActivityInst(activityInstId);
        Map<String, Object> beforeData = (Map<String, Object>) beforeResult.getData();
        String beforeState = (String) beforeData.get("state");
        System.out.println("完成任务前状态: " + beforeState);
        
        var endTaskResult = bpmRestClient.endTask(activityInstId);
        assertNotNull(endTaskResult.getData(), "endTask 必须返回结果");
        
        var afterResult = bpmRestClient.getActivityInst(activityInstId);
        Map<String, Object> afterData = (Map<String, Object>) afterResult.getData();
        String afterState = (String) afterData.get("state");
        System.out.println("完成任务后状态: " + afterState);
        
        assertNotEquals(beforeState, afterState, "完成任务后状态应该改变!");
        System.out.println("状态变化验证: " + beforeState + " -> " + afterState);
    }

    @Test
    @Order(7)
    @DisplayName("审计7: 完成流程后流程状态应改变")
    void audit_CompleteProcess_ShouldChangeState() {
        System.out.println("\n=== 审计7: 流程完成状态变化验证 ===");
        
        String processInstId = "audit-process-001";
        
        var beforeResult = bpmRestClient.getProcessInst(processInstId);
        Map<String, Object> beforeData = (Map<String, Object>) beforeResult.getData();
        String beforeState = (String) beforeData.get("state");
        System.out.println("完成流程前状态: " + beforeState);
        
        var completeResult = bpmRestClient.completeProcessInst(processInstId);
        assertNotNull(completeResult.getData(), "completeProcessInst 必须返回结果");
        
        var afterResult = bpmRestClient.getProcessInst(processInstId);
        Map<String, Object> afterData = (Map<String, Object>) afterResult.getData();
        String afterState = (String) afterData.get("state");
        System.out.println("完成流程后状态: " + afterState);
        
        assertNotEquals(beforeState, afterState, "完成流程后状态应该改变!");
        System.out.println("状态变化验证: " + beforeState + " -> " + afterState);
    }

    @Test
    @Order(8)
    @DisplayName("审计8: 完整业务闭环验证")
    void audit_FullBusinessCycle() {
        System.out.println("\n=== 审计8: 完整业务闭环验证 ===");
        
        String processDefId = "leave-approval-audit";
        String processInstName = "审计测试请假申请";
        
        System.out.println("步骤1: 获取流程定义");
        var processDefResult = bpmRestClient.getProcessDef(processDefId);
        assertTrue(processDefResult.getData() != null, "流程定义必须存在");
        
        System.out.println("步骤2: 创建流程实例");
        var newProcessResult = bpmRestClient.newProcess(processDefId, processInstName);
        assertTrue(newProcessResult.getData() != null, "创建流程实例必须返回数据");
        
        Map<String, Object> processData = (Map<String, Object>) newProcessResult.getData();
        String activityInstId = (String) processData.get("activityInstId");
        String processInstId = (String) processData.get("processInstId");
        
        System.out.println("活动实例ID: " + activityInstId);
        System.out.println("流程实例ID: " + processInstId);
        
        assertNotNull(activityInstId, "活动实例ID不能为 null - Mock 实现有问题!");
        assertNotNull(processInstId, "流程实例ID不能为 null");
        
        if (activityInstId != null) {
            System.out.println("步骤3: 获取活动实例并验证状态");
            var activityResult = bpmRestClient.getActivityInst(activityInstId);
            Map<String, Object> activityData = (Map<String, Object>) activityResult.getData();
            assertEquals("running", activityData.get("state"), "新活动状态应为 running");
            
            System.out.println("步骤4: 完成任务");
            var endTaskResult = bpmRestClient.endTask(activityInstId);
            assertEquals("MAINCODE_SUCCESS", ((Map)endTaskResult.getData()).get("mainCode"), "完成任务应成功");
            
            System.out.println("步骤5: 验证活动状态已改变");
            var afterActivityResult = bpmRestClient.getActivityInst(activityInstId);
            Map<String, Object> afterActivityData = (Map<String, Object>) afterActivityResult.getData();
            assertNotEquals("running", afterActivityData.get("state"), "完成任务后状态应改变");
        }
        
        System.out.println("步骤6: 完成流程实例");
        var completeResult = bpmRestClient.completeProcessInst(processInstId != null ? processInstId : "fallback-process");
        assertEquals("MAINCODE_SUCCESS", ((Map)completeResult.getData()).get("mainCode"), "完成流程应成功");
        
        System.out.println("\n审计结论: 需要修复 Mock 实现以支持真实业务逻辑");
    }
}
