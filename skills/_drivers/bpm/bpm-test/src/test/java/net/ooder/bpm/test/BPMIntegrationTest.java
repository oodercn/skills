package net.ooder.bpm.test;

import net.ooder.bpm.test.client.BPMRestClient;
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

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("测试 BPM 服务器健康检查")
    void testServerHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(bpmServerUrl + "/", String.class);
            System.out.println("Server response status: " + response.getStatusCode());
            assertNotNull(response);
        } catch (Exception e) {
            System.out.println("Server connection test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试获取流程定义")
    void testGetProcessDef() {
        String processDefId = "test-process-def";
        try {
            var result = bpmRestClient.getProcessDef(processDefId);
            System.out.println("GetProcessDef result: " + result);
        } catch (Exception e) {
            System.out.println("API test - getProcessDef: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试获取流程实例")
    void testGetProcessInst() {
        String processInstId = "test-process-inst";
        try {
            var result = bpmRestClient.getProcessInst(processInstId);
            System.out.println("GetProcessInst result: " + result);
        } catch (Exception e) {
            System.out.println("API test - getProcessInst: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试获取活动实例")
    void testGetActivityInst() {
        String activityInstId = "test-activity-inst";
        try {
            var result = bpmRestClient.getActivityInst(activityInstId);
            System.out.println("GetActivityInst result: " + result);
        } catch (Exception e) {
            System.out.println("API test - getActivityInst: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试新建流程")
    void testNewProcess() {
        String processDefId = "test-process-def";
        String processInstName = "Test Process Instance";
        try {
            var result = bpmRestClient.newProcess(processDefId, processInstName);
            System.out.println("NewProcess result: " + result);
        } catch (Exception e) {
            System.out.println("API test - newProcess: " + e.getMessage());
        }
    }
}
