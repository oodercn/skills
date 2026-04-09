package net.ooder.bpm.test;

import net.ooder.bpm.test.client.BPMRestClient;
import net.ooder.common.CommonConfig;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.config.core.ConfigRegistry;
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
class BPMFullCycleTest {

    @Autowired
    private BPMRestClient bpmRestClient;

    private static final String TEST_PROCESS_DEF_ID = "leave-approval-process";
    private static final String TEST_PROCESS_INST_NAME = "请假申请-测试流程";
    private static String createdProcessInstId;
    private static String createdActivityInstId;

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
    
    @AfterAll
    static void cleanup() {
        try {
            JDSConfig.reset();
            CommonConfig.reset();
            JDSServer.reset();
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. 获取流程定义 - 流程定义是流程的模板")
    void testGetProcessDef() {
        System.out.println("=== 步骤1: 获取流程定义 ===");
        
        var result = bpmRestClient.getProcessDef(TEST_PROCESS_DEF_ID);
        
        assertNotNull(result, "流程定义结果不应为空");
        System.out.println("流程定义获取结果: " + result);
        
        assertNotNull(result.getData(), "流程定义数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("流程定义ID: " + data.get("processDefId"));
        System.out.println("流程定义名称: " + data.get("name"));
        System.out.println("系统代码: " + data.get("systemCode"));
        
        assertEquals(TEST_PROCESS_DEF_ID, data.get("processDefId"), "流程定义ID应匹配");
        
        System.out.println("流程定义获取测试通过\n");
    }

    @Test
    @Order(2)
    @DisplayName("2. 获取活动定义 - 活动定义是流程中的节点定义")
    void testGetActivityDef() {
        System.out.println("=== 步骤2: 获取活动定义 ===");
        
        String activityDefId = "submit-activity";
        var result = bpmRestClient.getActivityDef(activityDefId);
        
        assertNotNull(result, "活动定义结果不应为空");
        System.out.println("活动定义获取结果: " + result);
        
        assertNotNull(result.getData(), "活动定义数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("活动定义ID: " + data.get("activityDefId"));
        System.out.println("活动定义名称: " + data.get("name"));
        
        System.out.println("活动定义获取测试通过\n");
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取路由定义 - 路由定义是活动之间的连接")
    void testGetRouteDef() {
        System.out.println("=== 步骤3: 获取路由定义 ===");
        
        String routeDefId = "submit-to-approve";
        var result = bpmRestClient.getRouteDef(routeDefId);
        
        assertNotNull(result, "路由定义结果不应为空");
        System.out.println("路由定义获取结果: " + result);
        
        assertNotNull(result.getData(), "路由定义数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("路由定义ID: " + data.get("routeDefId"));
        System.out.println("路由定义名称: " + data.get("name"));
        System.out.println("起始活动: " + data.get("fromActivityDefId"));
        System.out.println("目标活动: " + data.get("toActivityDefId"));
        
        System.out.println("路由定义获取测试通过\n");
    }

    @Test
    @Order(4)
    @DisplayName("4. 创建流程实例 - 基于流程定义启动一个具体的流程")
    void testNewProcess() {
        System.out.println("=== 步骤4: 创建流程实例 ===");
        
        var result = bpmRestClient.newProcess(TEST_PROCESS_DEF_ID, TEST_PROCESS_INST_NAME);
        
        assertNotNull(result, "创建流程实例结果不应为空");
        System.out.println("创建流程实例结果: " + result);
        
        assertNotNull(result.getData(), "创建流程实例数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        createdActivityInstId = (String) data.get("activityInstId");
        System.out.println("创建的活动实例ID: " + createdActivityInstId);
        System.out.println("活动定义ID: " + data.get("activityDefId"));
        System.out.println("流程实例ID: " + data.get("processInstId"));
        
        assertNotNull(createdActivityInstId, "活动实例ID不应为空");
        
        System.out.println("创建流程实例测试通过\n");
    }

    @Test
    @Order(5)
    @DisplayName("5. 获取流程实例 - 查看流程实例的详细信息")
    void testGetProcessInst() {
        System.out.println("=== 步骤5: 获取流程实例 ===");
        
        String processInstId = "test-process-inst-" + System.currentTimeMillis();
        var result = bpmRestClient.getProcessInst(processInstId);
        
        assertNotNull(result, "流程实例结果不应为空");
        System.out.println("流程实例获取结果: " + result);
        
        assertNotNull(result.getData(), "流程实例数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("流程实例ID: " + data.get("processInstId"));
        System.out.println("流程实例名称: " + data.get("name"));
        System.out.println("流程定义ID: " + data.get("processDefId"));
        System.out.println("流程状态: " + data.get("state"));
        
        System.out.println("获取流程实例测试通过\n");
    }

    @Test
    @Order(6)
    @DisplayName("6. 获取活动实例 - 查看当前待办活动的详细信息")
    void testGetActivityInst() {
        System.out.println("=== 步骤6: 获取活动实例 ===");
        
        String activityInstId = createdActivityInstId != null ? createdActivityInstId : "test-activity-inst";
        var result = bpmRestClient.getActivityInst(activityInstId);
        
        assertNotNull(result, "活动实例结果不应为空");
        System.out.println("活动实例获取结果: " + result);
        
        assertNotNull(result.getData(), "活动实例数据不应为null");
        data = (Map<String, Object>) result.getData();
        System.out.println("活动实例ID: " + data.get("activityInstId"));
        System.out.println("活动实例名称: " + data.get("name"));
        System.out.println("活动状态: " + data.get("state"));
        System.out.println("是否可以办理: " + data.get("canPerform"));
        System.out.println("是否可以签收: " + data.get("canSignReceive"));
        System.out.println("是否可以退回: " + data.get("canRouteBack"));
        
        assertEquals(activityInstId, data.get("activityInstId"), "活动实例ID应匹配");
        
        System.out.println("获取活动实例测试通过\n");
    }

    @Test
    @Order(7)
    @DisplayName("7. 签收活动 - 认领待办任务")
    void testSignReceive() {
        System.out.println("=== 步骤7: 签收活动 ===");
        
        String activityInstId = createdActivityInstId != null ? createdActivityInstId : "test-activity-inst";
        var result = bpmRestClient.signReceive(activityInstId);
        
        assertNotNull(result, "签收结果不应为空");
        System.out.println("签收结果: " + result);
        
        assertNotNull(result.getData(), "签收数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("签收状态: " + data.get("mainCode"));
        System.out.println("签收消息: " + data.get("message"));
        
        System.out.println("签收活动测试通过\n");
    }

    @Test
    @Order(8)
    @DisplayName("8. 完成任务 - 结束当前活动")
    void testEndTask() {
        System.out.println("=== 步骤8: 完成任务 ===");
        
        String activityInstId = createdActivityInstId != null ? createdActivityInstId : "test-activity-inst";
        var result = bpmRestClient.endTask(activityInstId);
        
        assertNotNull(result, "完成任务结果不应为空");
        System.out.println("完成任务结果: " + result);
        
        assertNotNull(result.getData(), "完成任务数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("完成状态: " + data.get("mainCode"));
        System.out.println("完成消息: " + data.get("message"));
        
        System.out.println("完成任务测试通过\n");
    }

    @Test
    @Order(9)
    @DisplayName("9. 完成流程实例 - 结束整个流程")
    void testCompleteProcessInst() {
        System.out.println("=== 步骤9: 完成流程实例 ===");
        
        String processInstId = "test-process-inst-" + System.currentTimeMillis();
        var result = bpmRestClient.completeProcessInst(processInstId);
        
        assertNotNull(result, "完成流程实例结果不应为空");
        System.out.println("完成流程实例结果: " + result);
        
        assertNotNull(result.getData(), "完成流程实例数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("完成状态: " + data.get("mainCode"));
        System.out.println("完成消息: " + data.get("message"));
        
        System.out.println("完成流程实例测试通过\n");
    }

    @Test
    @Order(10)
    @DisplayName("10. 中止流程实例 - 异常终止流程")
    void testAbortProcessInst() {
        System.out.println("=== 步骤10: 中止流程实例 ===");
        
        String processInstId = "test-process-inst-abort-" + System.currentTimeMillis();
        var result = bpmRestClient.abortProcessInst(processInstId);
        
        assertNotNull(result, "中止流程实例结果不应为空");
        System.out.println("中止流程实例结果: " + result);
        
        assertNotNull(result.getData(), "中止流程实例数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("中止状态: " + data.get("mainCode"));
        System.out.println("中止消息: " + data.get("message"));
        
        System.out.println("中止流程实例测试通过\n");
    }

    @Test
    @Order(11)
    @DisplayName("11. 退回操作 - 将活动退回到上一个节点")
    void testRouteBack() {
        System.out.println("=== 步骤11: 退回操作 ===");
        
        String activityInstId = createdActivityInstId != null ? createdActivityInstId : "test-activity-inst";
        String activityInstHistoryId = "history-" + System.currentTimeMillis();
        
        var result = bpmRestClient.routeBack(activityInstId, activityInstHistoryId);
        
        assertNotNull(result, "退回结果不应为空");
        System.out.println("退回结果: " + result);
        
        assertNotNull(result.getData(), "退回数据不应为null");
        Map<String, Object> data = (Map<String, Object>) result.getData();
        System.out.println("退回状态: " + data.get("mainCode"));
        System.out.println("退回消息: " + data.get("message"));
        
        System.out.println("退回操作测试通过\n");
    }

    @Test
    @Order(12)
    @DisplayName("12. 完整闭环测试 - 从创建到完成的完整流程")
    void testFullCycle() {
        System.out.println("\n========================================");
        System.out.println("=== 完整闭环测试：模拟请假审批流程 ===");
        System.out.println("========================================\n");
        
        String processDefId = "leave-approval-v1";
        String processInstName = "张三的请假申请";
        
        System.out.println("【步骤1】获取流程定义: " + processDefId);
        var processDefResult = bpmRestClient.getProcessDef(processDefId);
        assertNotNull(processDefResult, "流程定义获取成功");
        System.out.println("流程定义获取成功\n");
        
        System.out.println("【步骤2】创建流程实例: " + processInstName);
        var newProcessResult = bpmRestClient.newProcess(processDefId, processInstName);
        assertNotNull(newProcessResult, "流程实例创建成功");
        String activityInstId = null;
        if (newProcessResult.getData() != null) {
            Map<String, Object> data = (Map<String, Object>) newProcessResult.getData();
            activityInstId = (String) data.get("activityInstId");
            System.out.println("创建的活动实例ID: " + activityInstId);
        }
        System.out.println();
        
        if (activityInstId != null) {
            System.out.println("【步骤3】获取活动实例: " + activityInstId);
            var activityInstResult = bpmRestClient.getActivityInst(activityInstId);
            assertNotNull(activityInstResult, "活动实例获取成功");
            System.out.println("活动实例获取成功\n");
            
            System.out.println("【步骤4】签收活动");
            var signResult = bpmRestClient.signReceive(activityInstId);
            assertNotNull(signResult, "签收成功");
            System.out.println("签收成功\n");
            
            System.out.println("【步骤5】完成任务");
            var endTaskResult = bpmRestClient.endTask(activityInstId);
            assertNotNull(endTaskResult, "任务完成成功");
            System.out.println("任务完成成功\n");
        }
        
        System.out.println("【步骤6】完成流程实例");
        String processInstId = "process-" + System.currentTimeMillis();
        var completeResult = bpmRestClient.completeProcessInst(processInstId);
        assertNotNull(completeResult, "流程实例完成成功");
        System.out.println("流程实例完成成功\n");
        
        System.out.println("========================================");
        System.out.println("=== 完整闭环测试通过 ===");
        System.out.println("========================================\n");
    }
}
