package net.ooder.bpm.test;

import net.ooder.bpm.test.client.BPMRestClient;
import net.ooder.config.JDSConfig;
import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.server.JDSServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BPMIntegrationTest {

    @Value("${bpm.server.url}")
    private String bpmServerUrl;

    @Autowired
    private BPMRestClient bpmRestClient;

    private RestTemplate restTemplate;

    @BeforeAll
    static void setupJDSConfig() {
        Properties props = new Properties();
        props.setProperty("JDSHome", System.getProperty("java.io.tmpdir"));
        props.setProperty("jds.home", System.getProperty("java.io.tmpdir"));
        props.setProperty("server.home", System.getProperty("java.io.tmpdir"));
        props.setProperty("configName", "bpm-test");

        JDSConfig.initForTest(props);
        CommonConfig.initForTest(props);
        JDSServer.setMockMode(true);
    }
    
    @AfterAll
    static void cleanup() {
        JDSConfig.reset();
        CommonConfig.reset();
        JDSServer.reset();
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("测试 BPM 服务器健康检查")
    void testServerHealth() {
        ResponseEntity<String> response = assertDoesNotThrow(() ->
            restTemplate.getForEntity(bpmServerUrl + "/api/health", String.class),
            "BPM服务器健康检查应成功");
        assertNotNull(response, "健康检查响应不应为null");
    }

    @Test
    @DisplayName("测试获取流程定义")
    void testGetProcessDef() {
        String processDefId = "test-process-def";
        var result = assertDoesNotThrow(() -> bpmRestClient.getProcessDef(processDefId),
            "获取流程定义应成功");
        assertNotNull(result, "流程定义结果不应为null");
    }

    @Test
    @DisplayName("测试获取流程实例")
    void testGetProcessInst() {
        String processInstId = "test-process-inst";
        var result = assertDoesNotThrow(() -> bpmRestClient.getProcessInst(processInstId),
            "获取流程实例应成功");
        assertNotNull(result, "流程实例结果不应为null");
    }

    @Test
    @DisplayName("测试获取活动实例")
    void testGetActivityInst() {
        String activityInstId = "test-activity-inst";
        var result = assertDoesNotThrow(() -> bpmRestClient.getActivityInst(activityInstId),
            "获取活动实例应成功");
        assertNotNull(result, "活动实例结果不应为null");
    }

    @Test
    @DisplayName("测试新建流程")
    void testNewProcess() {
        String processDefId = "test-process-def";
        String processInstName = "Test Process Instance";
        var result = assertDoesNotThrow(() -> bpmRestClient.newProcess(processDefId, processInstName),
            "新建流程应成功");
        assertNotNull(result, "新建流程结果不应为null");
    }
}
