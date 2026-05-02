package net.ooder.bpm.event.test.distributed;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BpmDistributedEventTest {

    private static final int BROADCAST_PORT = 48888;
    private static final int NODE_A_CONTROL = 18881;
    private static final int NODE_B_CONTROL = 18882;
    private static final int NODE_C_CONTROL = 18883;
    private static final int NODE_A_TCP_EVENT = 28881;
    private static final int NODE_B_TCP_EVENT = 28882;
    private static final int NODE_C_TCP_EVENT = 28883;

    private DistributedTestCoordinator coordinator;

    @BeforeAll
    void setup() throws Exception {
        coordinator = new DistributedTestCoordinator();
        coordinator.startNode("ServerA", BROADCAST_PORT, NODE_A_CONTROL, NODE_A_TCP_EVENT);
        coordinator.startNode("ServerB", BROADCAST_PORT, NODE_B_CONTROL, NODE_B_TCP_EVENT);
        coordinator.startNode("ClientC", BROADCAST_PORT, NODE_C_CONTROL, NODE_C_TCP_EVENT);

        boolean allPeered = coordinator.waitForPeers("ServerA", 2, 20000);
        System.out.println("[Test] All nodes peered: " + allPeered);
    }

    @AfterAll
    void teardown() {
        if (coordinator != null) {
            coordinator.stopAllNodes();
        }
    }

    @Test
    @Order(1)
    @DisplayName("DT1: 三节点UDP发现组网 - 共享广播端口，每个节点发现至少2个对等节点")
    void testUdpDiscoveryNetworking() throws Exception {
        Map<String, String> statusA = coordinator.queryStatus("ServerA");
        Map<String, String> statusB = coordinator.queryStatus("ServerB");
        Map<String, String> statusC = coordinator.queryStatus("ClientC");

        int peersA = Integer.parseInt(statusA.getOrDefault("peers", "0"));
        int peersB = Integer.parseInt(statusB.getOrDefault("peers", "0"));
        int peersC = Integer.parseInt(statusC.getOrDefault("peers", "0"));

        System.out.println("[Test] Peers: A=" + peersA + " B=" + peersB + " C=" + peersC);

        assertTrue(peersA >= 2, "ServerA should discover at least 2 peers, found: " + peersA);
        assertTrue(peersB >= 2, "ServerB should discover at least 2 peers, found: " + peersB);
        assertTrue(peersC >= 2, "ClientC should discover at least 2 peers, found: " + peersC);
    }

    @Test
    @Order(2)
    @DisplayName("DT2: 节点存活验证 - 所有节点PING响应PONG")
    void testNodeLiveness() throws Exception {
        String pongA = coordinator.queryControl("ServerA", "PING");
        String pongB = coordinator.queryControl("ServerB", "PING");
        String pongC = coordinator.queryControl("ClientC", "PING");

        assertEquals("PONG:ServerA", pongA);
        assertEquals("PONG:ServerB", pongB);
        assertEquals("PONG:ClientC", pongC);
    }

    @Test
    @Order(3)
    @DisplayName("DT3: AgentInfo验证 - 节点注册为ooderAgent AgentInfo")
    void testAgentInfoRegistration() throws Exception {
        String infoA = coordinator.queryAgentInfo("ServerA");
        String infoB = coordinator.queryAgentInfo("ServerB");
        String infoC = coordinator.queryAgentInfo("ClientC");

        System.out.println("[Test] AgentInfo A: " + infoA);
        System.out.println("[Test] AgentInfo B: " + infoB);
        System.out.println("[Test] AgentInfo C: " + infoC);

        assertNotNull(infoA);
        assertTrue(infoA.contains("agentId=ServerA"), "ServerA should have agentId");
        assertTrue(infoA.contains("agentType=SCENE"), "ServerA should be SCENE type");
        assertTrue(infoA.contains("status=ONLINE"), "ServerA should be ONLINE");

        assertNotNull(infoB);
        assertTrue(infoB.contains("agentId=ServerB"), "ServerB should have agentId");

        assertNotNull(infoC);
        assertTrue(infoC.contains("agentId=ClientC"), "ClientC should have agentId");
    }

    @Test
    @Order(4)
    @DisplayName("DT4: 对等节点详情验证 - PEERS命令返回节点连接信息")
    void testPeersDetail() throws Exception {
        String peersA = coordinator.queryPeers("ServerA");
        System.out.println("[Test] ServerA peers: " + peersA);

        assertNotNull(peersA);
        assertTrue(peersA.contains("ServerB"), "ServerA should know ServerB");
        assertTrue(peersA.contains("ClientC"), "ServerA should know ClientC");
    }

    @Test
    @Order(10)
    @DisplayName("DT10: 单事件广播分发 - ServerA发送，B和C接收")
    void testSingleEventBroadcast() throws Exception {
        String result = coordinator.sendEvent("ServerA", "ProcessEvent", "STARTED", "processInstId=proc-dist-1,state=STARTED");
        assertTrue(result.startsWith("OK:"), "Send should return OK with eventId");

        boolean received = coordinator.waitForReceived("ServerB", 1, 8000);
        assertTrue(received, "ServerB should receive the event within timeout");

        Map<String, String> countsB = coordinator.queryCounts("ServerB");
        long receivedB = Long.parseLong(countsB.getOrDefault("received", "0"));
        assertTrue(receivedB >= 1, "ServerB should have received at least 1 event");

        Map<String, String> countsC = coordinator.queryCounts("ClientC");
        long receivedC = Long.parseLong(countsC.getOrDefault("received", "0"));
        assertTrue(receivedC >= 1, "ClientC should have received at least 1 event");
    }

    @Test
    @Order(20)
    @DisplayName("DT20: 多事件串行广播 - 5事件逐个发送验证")
    void testMultipleEventBroadcast() throws Exception {
        Map<String, String> countsBefore = coordinator.queryCounts("ServerB");
        long consumedBefore = Long.parseLong(countsBefore.getOrDefault("consumed", "0"));

        for (int i = 0; i < 5; i++) {
            coordinator.sendEvent("ServerA", "ProcessEvent", "STARTED",
                "processInstId=proc-multi-" + i + ",state=STARTED");
            Thread.sleep(300);
        }

        boolean received = coordinator.waitForReceived("ServerB", consumedBefore + 5, 15000);
        assertTrue(received, "ServerB should receive 5 events");

        Map<String, String> countsB = coordinator.queryCounts("ServerB");
        long consumedB = Long.parseLong(countsB.getOrDefault("consumed", "0"));
        assertTrue(consumedB >= consumedBefore + 5, "ServerB should have consumed at least 5 more events, got: " + consumedB);
    }

    @Test
    @Order(30)
    @DisplayName("DT30: 双向事件传播 - A发B收，B发A收")
    void testBidirectionalEventPropagation() throws Exception {
        long initialReceivedA = Long.parseLong(coordinator.queryCounts("ServerA").getOrDefault("received", "0"));

        coordinator.sendEvent("ServerB", "ActivityEvent", "INITED",
            "activityInstId=act-bidi-1,agentId=agent-1,action=INITED");

        boolean received = coordinator.waitForReceived("ServerA", initialReceivedA + 1, 8000);
        assertTrue(received, "ServerA should receive event from ServerB");
    }

    @Test
    @Order(40)
    @DisplayName("DT40: 传输字节数验证 - 确认真实网络数据传输")
    void testNetworkBytesTransmitted() throws Exception {
        Map<String, String> statusA = coordinator.queryStatus("ServerA");
        long bytesSent = Long.parseLong(statusA.getOrDefault("bytesSent", "0"));
        long bytesReceived = Long.parseLong(statusA.getOrDefault("bytesReceived", "0"));

        assertTrue(bytesSent > 0, "ServerA should have sent bytes over network: " + bytesSent);
        assertTrue(bytesReceived > 0, "ServerA should have received bytes over network: " + bytesReceived);

        System.out.println("[Test] ServerA bytesSent=" + bytesSent + ", bytesReceived=" + bytesReceived);
    }

    @Test
    @Order(50)
    @DisplayName("DT50: 三节点全闭环 - A/B/C各发事件，验证互收")
    void testFullClosedLoop() throws Exception {
        Map<String, String> countsBeforeA = coordinator.queryCounts("ServerA");
        Map<String, String> countsBeforeB = coordinator.queryCounts("ServerB");
        Map<String, String> countsBeforeC = coordinator.queryCounts("ClientC");

        long sentBeforeA = Long.parseLong(countsBeforeA.getOrDefault("sent", "0"));
        long sentBeforeB = Long.parseLong(countsBeforeB.getOrDefault("sent", "0"));
        long sentBeforeC = Long.parseLong(countsBeforeC.getOrDefault("sent", "0"));

        for (int i = 0; i < 3; i++) {
            coordinator.sendEvent("ServerA", "ProcessEvent", "STARTED", "processInstId=loop-a-" + i);
            coordinator.sendEvent("ServerB", "ActivityEvent", "INITED", "activityInstId=loop-b-" + i);
            coordinator.sendEvent("ClientC", "SkillFlowEvent", "DEPLOYED", "skillFlowId=loop-c-" + i);
        }

        Thread.sleep(5000);

        Map<String, String> countsAfterA = coordinator.queryCounts("ServerA");
        Map<String, String> countsAfterB = coordinator.queryCounts("ServerB");
        Map<String, String> countsAfterC = coordinator.queryCounts("ClientC");

        long sentA = Long.parseLong(countsAfterA.getOrDefault("sent", "0")) - sentBeforeA;
        long sentB = Long.parseLong(countsAfterB.getOrDefault("sent", "0")) - sentBeforeB;
        long sentC = Long.parseLong(countsAfterC.getOrDefault("sent", "0")) - sentBeforeC;

        assertEquals(3, sentA, "ServerA should have sent 3 events");
        assertEquals(3, sentB, "ServerB should have sent 3 events");
        assertEquals(3, sentC, "ClientC should have sent 3 events");

        long receivedA = Long.parseLong(countsAfterA.getOrDefault("received", "0")) - Long.parseLong(countsBeforeA.getOrDefault("received", "0"));
        long receivedB = Long.parseLong(countsAfterB.getOrDefault("received", "0")) - Long.parseLong(countsBeforeB.getOrDefault("received", "0"));
        long receivedC = Long.parseLong(countsAfterC.getOrDefault("received", "0")) - Long.parseLong(countsBeforeC.getOrDefault("received", "0"));

        assertTrue(receivedA >= 4, "ServerA should receive from B+C (>=4 with possible UDP self): " + receivedA);
        assertTrue(receivedB >= 4, "ServerB should receive from A+C: " + receivedB);
        assertTrue(receivedC >= 4, "ClientC should receive from A+B: " + receivedC);

        System.out.println("[Test] Closed loop: A sent=" + sentA + " received=" + receivedA);
        System.out.println("[Test] Closed loop: B sent=" + sentB + " received=" + receivedB);
        System.out.println("[Test] Closed loop: C sent=" + sentC + " received=" + receivedC);
    }
}
