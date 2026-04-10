package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.engine.database.*;
import net.ooder.bpm.engine.inter.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProcessDefManagerService 集成测试
 * 验证所有扩展属性的JSON转换和存储
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProcessDefManagerServiceIntegrationTest {

    @Autowired
    private ProcessDefManagerService processDefManagerService;

    @Autowired
    private DbProcessDefManager processDefManager;

    @Autowired
    private DbProcessDefVersionManager processDefVersionManager;

    @Autowired
    private DbActivityDefManager activityDefManager;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // 清理测试数据
        cleanupTestData("proc_all_attrs_001");
        cleanupTestData("proc_simple_001");
        cleanupTestData("proc_countersign_001");
    }

    private void cleanupTestData(String processDefId) {
        try {
            EIProcessDef def = processDefManager.loadByKey(processDefId);
            if (def != null) {
                processDefManager.delete(def);
            }
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    @Test
    @DisplayName("测试全属性流程的保存和读取")
    void testAllAttributesProcess() throws Exception {
        // 加载测试数据
        File testFile = new File("e:/github/ooder-skills/skills/_drivers/bpm/test-data/all-attributes-process.json");
        Map<String, Object> processData = mapper.readValue(testFile, Map.class);

        // 保存流程
        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);
        assertNotNull(savedResult, "保存流程应返回结果");

        // 验证流程定义
        Map<String, Object> processDef = (Map<String, Object>) savedResult.get("processDef");
        assertNotNull(processDef);
        assertEquals("proc_all_attrs_001", processDef.get("processDefId"));
        assertEquals("全属性测试流程", processDef.get("name"));

        // 验证版本信息
        Map<String, Object> activeVersion = (Map<String, Object>) savedResult.get("activeVersion");
        assertNotNull(activeVersion);
        assertEquals("测试人员", activeVersion.get("creatorName"));
        assertEquals("test_user", activeVersion.get("modifierId"));

        // 验证开始节点
        Map<String, Object> startNode = (Map<String, Object>) savedResult.get("startNode");
        assertNotNull(startNode, "应包含开始节点");
        assertEquals("Participant_Start", startNode.get("participantId"));
        assertEquals("act_start", startNode.get("firstActivityId"));
        Map<String, Object> startCoord = (Map<String, Object>) startNode.get("positionCoord");
        assertNotNull(startCoord);
        assertEquals(50, startCoord.get("x"));
        assertEquals(200, startCoord.get("y"));
        assertEquals("NO_ROUTING", startNode.get("routing"));

        // 验证结束节点
        List<Map<String, Object>> endNodes = (List<Map<String, Object>>) savedResult.get("endNodes");
        assertNotNull(endNodes, "应包含结束节点列表");
        assertEquals(1, endNodes.size());
        Map<String, Object> endNode = endNodes.get(0);
        assertEquals("Participant_End", endNode.get("participantId"));
        assertEquals("act_end", endNode.get("lastActivityId"));

        // 验证监听器
        List<Map<String, Object>> listeners = (List<Map<String, Object>>) savedResult.get("listeners");
        assertNotNull(listeners, "应包含监听器列表");
        assertEquals(6, listeners.size(), "应有6个监听器");

        // 验证监听器属性
        Map<String, Object> firstListener = listeners.get(0);
        assertEquals("listener_001", firstListener.get("id"));
        assertEquals("流程启动监听", firstListener.get("name"));
        assertEquals("PROCESS_START", firstListener.get("event"));
        assertEquals("com.ooder.bpm.listener.TestProcessStartListener", firstListener.get("realizeClass"));

        // 验证权限组
        List<Map<String, Object>> rightGroups = (List<Map<String, Object>>) savedResult.get("rightGroups");
        assertNotNull(rightGroups, "应包含权限组列表");
        assertEquals(4, rightGroups.size(), "应有4个权限组");

        // 验证权限组属性
        Map<String, Object> defaultGroup = rightGroups.get(0);
        assertEquals("rg_001", defaultGroup.get("id"));
        assertEquals("默认组", defaultGroup.get("name"));
        assertEquals("DEFAULT", defaultGroup.get("code"));
        assertEquals(1, defaultGroup.get("order"));
        assertEquals(true, defaultGroup.get("defaultGroup"));

        // 验证活动
        List<Map<String, Object>> activities = (List<Map<String, Object>>) savedResult.get("activities");
        assertNotNull(activities);
        assertEquals(6, activities.size(), "应有6个活动");

        // 验证全属性活动
        Map<String, Object> allAttrsActivity = findActivity(activities, "act_all_attrs");
        assertNotNull(allAttrsActivity);

        // 验证坐标
        Map<String, Object> positionCoord = (Map<String, Object>) allAttrsActivity.get("positionCoord");
        assertNotNull(positionCoord);
        assertEquals(300, positionCoord.get("x"));
        assertEquals(200, positionCoord.get("y"));

        // 验证参与者ID
        assertEquals("Participant_Main", allAttrsActivity.get("participantId"));

        // 验证RIGHT属性组
        Map<String, Object> rightAttrs = (Map<String, Object>) allAttrsActivity.get("RIGHT");
        assertNotNull(rightAttrs, "应包含RIGHT属性组");
        assertEquals("JOINTSIGN", rightAttrs.get("performType"));
        assertEquals("MEANWHILE", rightAttrs.get("performSequence"));
        assertEquals("YES", rightAttrs.get("canInsteadSign"));
        assertEquals("user_substitute_001", rightAttrs.get("insteadSignSelected"));
        assertEquals("user_performer_001", rightAttrs.get("performerSelectedId"));
        assertEquals("rg_002", rightAttrs.get("movePerformerTo"));
        assertEquals("user_surrogate_001", rightAttrs.get("surrogateId"));
        assertEquals("代理人姓名", rightAttrs.get("surrogateName"));

        // 验证FORM属性组
        Map<String, Object> formAttrs = (Map<String, Object>) allAttrsActivity.get("FORM");
        assertNotNull(formAttrs, "应包含FORM属性组");
        assertEquals("form_test_001", formAttrs.get("formId"));
        assertEquals("测试表单", formAttrs.get("formName"));
        assertEquals("CUSTOM", formAttrs.get("formType"));
        assertEquals("/forms/test/form001.html", formAttrs.get("formUrl"));

        // 验证服务活动
        Map<String, Object> serviceActivity = findActivity(activities, "act_service");
        assertNotNull(serviceActivity);
        Map<String, Object> serviceAttrs = (Map<String, Object>) serviceActivity.get("SERVICE");
        assertNotNull(serviceAttrs, "应包含SERVICE属性组");
        assertEquals("POST", serviceAttrs.get("httpMethod"));
        assertEquals("https://api.example.com/test", serviceAttrs.get("httpUrl"));
        assertEquals("JSON", serviceAttrs.get("httpRequestType"));
        assertEquals("svc_test_001", serviceAttrs.get("serviceSelectedId"));

        // 验证路由
        List<Map<String, Object>> routes = (List<Map<String, Object>>) savedResult.get("routes");
        assertNotNull(routes);
        assertEquals(6, routes.size(), "应有6条路由");

        // 验证条件路由
        Map<String, Object> conditionRoute = findRoute(routes, "route_main_to_service");
        assertNotNull(conditionRoute);
        assertEquals("${condition1 == true}", conditionRoute.get("routeCondition"));
        assertEquals("CONDITION", conditionRoute.get("routeConditionType"));
    }

    @Test
    @DisplayName("测试简单审批流程")
    void testSimpleApprovalProcess() throws Exception {
        File testFile = new File("e:/github/ooder-skills/skills/_drivers/bpm/test-data/simple-approval-process.json");
        Map<String, Object> processData = mapper.readValue(testFile, Map.class);

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);
        assertNotNull(savedResult);

        // 验证基本结构
        assertNotNull(savedResult.get("processDef"));
        assertNotNull(savedResult.get("activities"));
        assertNotNull(savedResult.get("routes"));

        List<Map<String, Object>> activities = (List<Map<String, Object>>) savedResult.get("activities");
        assertEquals(5, activities.size(), "简单流程应有5个活动（开始、提交、经理审批、HR审批、结束）");

        // 验证审批活动
        Map<String, Object> managerActivity = findActivity(activities, "act_manager_approval");
        assertNotNull(managerActivity);
        Map<String, Object> rightAttrs = (Map<String, Object>) managerActivity.get("RIGHT");
        assertNotNull(rightAttrs);
        assertEquals("SINGLE", rightAttrs.get("performType"));
        assertEquals("YES", rightAttrs.get("canInsteadSign"));
    }

    @Test
    @DisplayName("测试会签流程")
    void testCountersignProcess() throws Exception {
        File testFile = new File("e:/github/ooder-skills/skills/_drivers/bpm/test-data/countersign-process.json");
        Map<String, Object> processData = mapper.readValue(testFile, Map.class);

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);
        assertNotNull(savedResult);

        List<Map<String, Object>> activities = (List<Map<String, Object>>) savedResult.get("activities");

        // 验证并行会签活动
        Map<String, Object> parallelActivity = findActivity(activities, "act_parallel_countersign");
        assertNotNull(parallelActivity);
        assertEquals("AND", parallelActivity.get("split"), "并行会签应使用AND分支");

        Map<String, Object> rightAttrs = (Map<String, Object>) parallelActivity.get("RIGHT");
        assertNotNull(rightAttrs);
        assertEquals("JOINTSIGN", rightAttrs.get("performType"));
        assertEquals("MEANWHILE", rightAttrs.get("performSequence"));
    }

    @Test
    @DisplayName("测试开始和结束节点坐标保存")
    void testStartEndNodeCoordinates() throws Exception {
        // 创建包含特定坐标的测试数据
        Map<String, Object> processData = createTestProcessWithCoordinates();

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        // 验证开始节点坐标
        Map<String, Object> startNode = (Map<String, Object>) savedResult.get("startNode");
        assertNotNull(startNode);
        Map<String, Object> startCoord = (Map<String, Object>) startNode.get("positionCoord");
        assertEquals(100, startCoord.get("x"));
        assertEquals(150, startCoord.get("y"));

        // 验证结束节点坐标
        List<Map<String, Object>> endNodes = (List<Map<String, Object>>) savedResult.get("endNodes");
        assertNotNull(endNodes);
        Map<String, Object> endCoord = (Map<String, Object>) endNodes.get(0).get("positionCoord");
        assertEquals(800, endCoord.get("x"));
        assertEquals(150, endCoord.get("y"));
    }

    @Test
    @DisplayName("测试监听器XML格式转换")
    void testListenerXmlConversion() throws Exception {
        Map<String, Object> processData = createTestProcessWithListeners();

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        List<Map<String, Object>> listeners = (List<Map<String, Object>>) savedResult.get("listeners");
        assertNotNull(listeners);
        assertEquals(2, listeners.size());

        // 验证所有监听器属性都被正确保存和读取
        for (Map<String, Object> listener : listeners) {
            assertNotNull(listener.get("id"));
            assertNotNull(listener.get("name"));
            assertNotNull(listener.get("event"));
            assertNotNull(listener.get("realizeClass"));
        }
    }

    @Test
    @DisplayName("测试权限组XML格式转换")
    void testRightGroupXmlConversion() throws Exception {
        Map<String, Object> processData = createTestProcessWithRightGroups();

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        List<Map<String, Object>> rightGroups = (List<Map<String, Object>>) savedResult.get("rightGroups");
        assertNotNull(rightGroups);
        assertEquals(3, rightGroups.size());

        // 验证权限组排序
        assertEquals(1, rightGroups.get(0).get("order"));
        assertEquals(2, rightGroups.get(1).get("order"));
        assertEquals(3, rightGroups.get(2).get("order"));

        // 验证默认组
        assertEquals(true, rightGroups.get(0).get("defaultGroup"));
    }

    @Test
    @DisplayName("测试活动属性组保存")
    void testActivityAttributeGroups() throws Exception {
        Map<String, Object> processData = createTestProcessWithAttributeGroups();

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        List<Map<String, Object>> activities = (List<Map<String, Object>>) savedResult.get("activities");
        Map<String, Object> testActivity = findActivity(activities, "act_test");

        // 验证所有属性组
        assertNotNull(testActivity.get("RIGHT"), "应包含RIGHT属性组");
        assertNotNull(testActivity.get("FORM"), "应包含FORM属性组");
        assertNotNull(testActivity.get("SERVICE"), "应包含SERVICE属性组");
        assertNotNull(testActivity.get("WORKFLOW"), "应包含WORKFLOW属性组");
    }

    @Test
    @DisplayName("测试XPDL格式兼容性")
    void testXpdlFormatCompatibility() throws Exception {
        // 测试XPDL格式的开始/结束节点字符串解析
        String startOfWorkflow = "Participant_001;act_start;50;200;NO_ROUTING";
        String endOfWorkflow = "Participant_End;act_end;800;200;NO_ROUTING";

        // 创建包含XPDL格式的测试数据
        Map<String, Object> processData = createTestProcessWithXpdlFormat(startOfWorkflow, endOfWorkflow);

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        // 验证解析正确
        Map<String, Object> startNode = (Map<String, Object>) savedResult.get("startNode");
        assertEquals("Participant_001", startNode.get("participantId"));
        assertEquals("act_start", startNode.get("firstActivityId"));

        List<Map<String, Object>> endNodes = (List<Map<String, Object>>) savedResult.get("endNodes");
        assertEquals("Participant_End", endNodes.get(0).get("participantId"));
    }

    @Test
    @DisplayName("测试多结束节点场景")
    void testMultipleEndNodes() throws Exception {
        // 创建包含多个结束节点的测试数据
        Map<String, Object> processData = createTestProcessWithMultipleEndNodes();

        Map<String, Object> savedResult = processDefManagerService.saveProcessDef(processData);

        List<Map<String, Object>> endNodes = (List<Map<String, Object>>) savedResult.get("endNodes");
        assertNotNull(endNodes);
        assertEquals(3, endNodes.size(), "应有3个结束节点");

        // 验证每个结束节点的坐标
        Map<String, Object> end1 = endNodes.get(0);
        Map<String, Object> coord1 = (Map<String, Object>) end1.get("positionCoord");
        assertEquals(800, coord1.get("x"));

        Map<String, Object> end2 = endNodes.get(1);
        Map<String, Object> coord2 = (Map<String, Object>) end2.get("positionCoord");
        assertEquals(800, coord2.get("x"));
        assertEquals(300, coord2.get("y"));
    }

    @Test
    @DisplayName("测试getFullProcessDef方法")
    void testGetFullProcessDef() throws Exception {
        // 先保存一个流程
        File testFile = new File("e:/github/ooder-skills/skills/_drivers/bpm/test-data/simple-approval-process.json");
        Map<String, Object> processData = mapper.readValue(testFile, Map.class);
        processDefManagerService.saveProcessDef(processData);

        // 使用getFullProcessDef读取
        Map<String, Object> fullProcess = processDefManagerService.getFullProcessDef("proc_simple_001");

        assertNotNull(fullProcess);
        assertNotNull(fullProcess.get("processDef"));
        assertNotNull(fullProcess.get("activeVersion"));
        assertNotNull(fullProcess.get("activities"));
        assertNotNull(fullProcess.get("routes"));
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> findActivity(List<Map<String, Object>> activities, String activityDefId) {
        return activities.stream()
                .filter(a -> activityDefId.equals(a.get("activityDefId")))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> findRoute(List<Map<String, Object>> routes, String routeDefId) {
        return routes.stream()
                .filter(r -> routeDefId.equals(r.get("routeDefId")))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> createTestProcessWithCoordinates() {
        Map<String, Object> process = new HashMap<>();
        process.put("processDefId", "proc_test_coord");
        process.put("name", "坐标测试流程");

        Map<String, Object> startNode = new HashMap<>();
        startNode.put("participantId", "Participant_Start");
        startNode.put("firstActivityId", "act_start");
        Map<String, Object> startCoord = new HashMap<>();
        startCoord.put("x", 100);
        startCoord.put("y", 150);
        startNode.put("positionCoord", startCoord);
        startNode.put("routing", "NO_ROUTING");
        process.put("startNode", startNode);

        List<Map<String, Object>> endNodes = new ArrayList<>();
        Map<String, Object> endNode = new HashMap<>();
        endNode.put("participantId", "Participant_End");
        endNode.put("lastActivityId", "act_end");
        Map<String, Object> endCoord = new HashMap<>();
        endCoord.put("x", 800);
        endCoord.put("y", 150);
        endNode.put("positionCoord", endCoord);
        endNode.put("routing", "NO_ROUTING");
        endNodes.add(endNode);
        process.put("endNodes", endNodes);

        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(createSimpleActivity("act_start", "开始", "START", 100, 150));
        activities.add(createSimpleActivity("act_end", "结束", "END", 800, 150));
        process.put("activities", activities);

        process.put("routes", new ArrayList<>());

        return process;
    }

    private Map<String, Object> createTestProcessWithListeners() {
        Map<String, Object> process = createBasicProcess("proc_test_listeners", "监听器测试");

        List<Map<String, Object>> listeners = new ArrayList<>();
        Map<String, Object> listener1 = new HashMap<>();
        listener1.put("id", "listener_001");
        listener1.put("name", "测试监听1");
        listener1.put("event", "PROCESS_START");
        listener1.put("realizeClass", "com.test.Listener1");
        listeners.add(listener1);

        Map<String, Object> listener2 = new HashMap<>();
        listener2.put("id", "listener_002");
        listener2.put("name", "测试监听2");
        listener2.put("event", "PROCESS_END");
        listener2.put("realizeClass", "com.test.Listener2");
        listeners.add(listener2);

        process.put("listeners", listeners);
        return process;
    }

    private Map<String, Object> createTestProcessWithRightGroups() {
        Map<String, Object> process = createBasicProcess("proc_test_rights", "权限组测试");

        List<Map<String, Object>> rightGroups = new ArrayList<>();
        rightGroups.add(createRightGroup("rg_001", "默认组", "DEFAULT", 1, true));
        rightGroups.add(createRightGroup("rg_002", "审批组", "APPROVAL", 2, false));
        rightGroups.add(createRightGroup("rg_003", "知会组", "NOTIFY", 3, false));

        process.put("rightGroups", rightGroups);
        return process;
    }

    private Map<String, Object> createTestProcessWithAttributeGroups() {
        Map<String, Object> process = createBasicProcess("proc_test_attrs", "属性组测试");

        List<Map<String, Object>> activities = new ArrayList<>();
        Map<String, Object> activity = createSimpleActivity("act_test", "测试活动", "NORMAL", 300, 200);

        Map<String, Object> rightAttrs = new HashMap<>();
        rightAttrs.put("performType", "SINGLE");
        activity.put("RIGHT", rightAttrs);

        Map<String, Object> formAttrs = new HashMap<>();
        formAttrs.put("formId", "form_001");
        activity.put("FORM", formAttrs);

        Map<String, Object> serviceAttrs = new HashMap<>();
        serviceAttrs.put("httpMethod", "GET");
        activity.put("SERVICE", serviceAttrs);

        Map<String, Object> workflowAttrs = new HashMap<>();
        workflowAttrs.put("deadLineOperation", "NOTIFY");
        activity.put("WORKFLOW", workflowAttrs);

        activities.add(activity);
        process.put("activities", activities);

        return process;
    }

    private Map<String, Object> createTestProcessWithXpdlFormat(String startOfWorkflow, String endOfWorkflow) {
        Map<String, Object> process = createBasicProcess("proc_test_xpdl", "XPDL测试");

        // 解析XPDL格式创建startNode和endNodes
        String[] startParts = startOfWorkflow.split(";");
        Map<String, Object> startNode = new HashMap<>();
        startNode.put("participantId", startParts[0]);
        startNode.put("firstActivityId", startParts[1]);
        Map<String, Object> startCoord = new HashMap<>();
        startCoord.put("x", Integer.parseInt(startParts[2]));
        startCoord.put("y", Integer.parseInt(startParts[3]));
        startNode.put("positionCoord", startCoord);
        startNode.put("routing", startParts[4]);
        process.put("startNode", startNode);

        List<Map<String, Object>> endNodes = new ArrayList<>();
        String[] endParts = endOfWorkflow.split(";");
        Map<String, Object> endNode = new HashMap<>();
        endNode.put("participantId", endParts[0]);
        endNode.put("lastActivityId", endParts[1]);
        Map<String, Object> endCoord = new HashMap<>();
        endCoord.put("x", Integer.parseInt(endParts[2]));
        endCoord.put("y", Integer.parseInt(endParts[3]));
        endNode.put("positionCoord", endCoord);
        endNode.put("routing", endParts[4]);
        endNodes.add(endNode);
        process.put("endNodes", endNodes);

        return process;
    }

    private Map<String, Object> createTestProcessWithMultipleEndNodes() {
        Map<String, Object> process = createBasicProcess("proc_test_multi_end", "多结束节点测试");

        List<Map<String, Object>> endNodes = new ArrayList<>();
        endNodes.add(createEndNode("Participant_End1", "act_end1", 800, 100));
        endNodes.add(createEndNode("Participant_End2", "act_end2", 800, 200));
        endNodes.add(createEndNode("Participant_End3", "act_end3", 800, 300));
        process.put("endNodes", endNodes);

        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(createSimpleActivity("act_start", "开始", "START", 100, 200));
        activities.add(createSimpleActivity("act_end1", "结束1", "END", 800, 100));
        activities.add(createSimpleActivity("act_end2", "结束2", "END", 800, 200));
        activities.add(createSimpleActivity("act_end3", "结束3", "END", 800, 300));
        process.put("activities", activities);

        return process;
    }

    private Map<String, Object> createBasicProcess(String processDefId, String name) {
        Map<String, Object> process = new HashMap<>();
        process.put("processDefId", processDefId);
        process.put("name", name);

        Map<String, Object> startNode = new HashMap<>();
        startNode.put("participantId", "Participant_Start");
        startNode.put("firstActivityId", "act_start");
        Map<String, Object> startCoord = new HashMap<>();
        startCoord.put("x", 50);
        startCoord.put("y", 200);
        startNode.put("positionCoord", startCoord);
        startNode.put("routing", "NO_ROUTING");
        process.put("startNode", startNode);

        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(createSimpleActivity("act_start", "开始", "START", 50, 200));
        process.put("activities", activities);
        process.put("routes", new ArrayList<>());

        return process;
    }

    private Map<String, Object> createSimpleActivity(String id, String name, String position, int x, int y) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("activityDefId", id);
        activity.put("name", name);
        activity.put("position", position);
        Map<String, Object> coord = new HashMap<>();
        coord.put("x", x);
        coord.put("y", y);
        activity.put("positionCoord", coord);
        activity.put("participantId", "Participant_" + id);
        activity.put("implementation", "No");
        activity.put("join", "XOR");
        activity.put("split", "XOR");
        return activity;
    }

    private Map<String, Object> createRightGroup(String id, String name, String code, int order, boolean defaultGroup) {
        Map<String, Object> group = new HashMap<>();
        group.put("id", id);
        group.put("name", name);
        group.put("code", code);
        group.put("order", order);
        group.put("defaultGroup", defaultGroup);
        return group;
    }

    private Map<String, Object> createEndNode(String participantId, String lastActivityId, int x, int y) {
        Map<String, Object> endNode = new HashMap<>();
        endNode.put("participantId", participantId);
        endNode.put("lastActivityId", lastActivityId);
        Map<String, Object> coord = new HashMap<>();
        coord.put("x", x);
        coord.put("y", y);
        endNode.put("positionCoord", coord);
        endNode.put("routing", "NO_ROUTING");
        return endNode;
    }
}
