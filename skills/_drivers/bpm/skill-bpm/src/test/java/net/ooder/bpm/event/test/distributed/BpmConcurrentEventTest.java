package net.ooder.bpm.event.test.distributed;

import net.ooder.bpm.event.test.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BpmConcurrentEventTest {

    private static final int BROADCAST_PORT = 48890;
    private static final int NODE_A_CONTROL = 18991;
    private static final int NODE_B_CONTROL = 18992;
    private static final int NODE_C_CONTROL = 18993;
    private static final int NODE_A_TCP_EVENT = 28991;
    private static final int NODE_B_TCP_EVENT = 28992;
    private static final int NODE_C_TCP_EVENT = 28993;

    private DistributedTestCoordinator coordinator;

    @BeforeAll
    void setup() throws Exception {
        coordinator = new DistributedTestCoordinator();
        coordinator.startNode("ConcA", BROADCAST_PORT, NODE_A_CONTROL, NODE_A_TCP_EVENT);
        coordinator.startNode("ConcB", BROADCAST_PORT, NODE_B_CONTROL, NODE_B_TCP_EVENT);
        coordinator.startNode("ConcC", BROADCAST_PORT, NODE_C_CONTROL, NODE_C_TCP_EVENT);

        boolean allPeered = coordinator.waitForPeers("ConcA", 2, 20000);
        System.out.println("[ConcurrentTest] All nodes peered: " + allPeered);
        assertTrue(allPeered, "All nodes should be peered before concurrent tests");
    }

    @AfterAll
    void teardown() {
        if (coordinator != null) {
            coordinator.stopAllNodes();
        }
    }

    @Test
    @Order(1)
    @DisplayName("CC1: 批量串行发送50事件 - 验证完整接收无丢失")
    void testBatchSerial50Events() throws Exception {
        Map<String, String> countsBeforeB = coordinator.queryCounts("ConcB");
        Map<String, String> countsBeforeC = coordinator.queryCounts("ConcC");
        long receivedBeforeB = Long.parseLong(countsBeforeB.getOrDefault("received", "0"));
        long receivedBeforeC = Long.parseLong(countsBeforeC.getOrDefault("received", "0"));

        String result = coordinator.batchSend("ConcA", 50, "ProcessEvent", "batch-serial");
        assertTrue(result.startsWith("OK:"), "Batch send should return OK");

        boolean receivedB = coordinator.waitForReceived("ConcB", receivedBeforeB + 50, 15000);
        boolean receivedC = coordinator.waitForReceived("ConcC", receivedBeforeC + 50, 15000);

        Map<String, String> countsAfterB = coordinator.queryCounts("ConcB");
        Map<String, String> countsAfterC = coordinator.queryCounts("ConcC");
        long receivedAfterB = Long.parseLong(countsAfterB.getOrDefault("received", "0"));
        long receivedAfterC = Long.parseLong(countsAfterC.getOrDefault("received", "0"));

        long deltaB = receivedAfterB - receivedBeforeB;
        long deltaC = receivedAfterC - receivedBeforeC;

        System.out.println("[CC1] Batch 50: ConcB received=" + deltaB + ", ConcC received=" + deltaC);

        assertTrue(receivedB, "ConcB should receive all 50 events within timeout");
        assertTrue(receivedC, "ConcC should receive all 50 events within timeout");
        assertTrue(deltaB >= 50, "ConcB should have received >= 50 events, got: " + deltaB);
        assertTrue(deltaC >= 50, "ConcC should have received >= 50 events, got: " + deltaC);
    }

    @Test
    @Order(10)
    @DisplayName("CC10: 并发4线程发送100事件 - 验证并发传输无丢失")
    void testConcurrent4Thread100Events() throws Exception {
        Map<String, String> countsBeforeB = coordinator.queryCounts("ConcB");
        Map<String, String> countsBeforeC = coordinator.queryCounts("ConcC");
        long receivedBeforeB = Long.parseLong(countsBeforeB.getOrDefault("received", "0"));
        long receivedBeforeC = Long.parseLong(countsBeforeC.getOrDefault("received", "0"));

        String result = coordinator.concurrentSend("ConcA", 100, 4, "ProcessEvent", "concurrent-4t");
        assertTrue(result.startsWith("OK:"), "Concurrent send should return OK");
        assertTrue(result.contains("concurrentSent=100"), "Should have sent 100 events");

        boolean receivedB = coordinator.waitForReceived("ConcB", receivedBeforeB + 100, 20000);
        boolean receivedC = coordinator.waitForReceived("ConcC", receivedBeforeC + 100, 20000);

        Map<String, String> countsAfterB = coordinator.queryCounts("ConcB");
        Map<String, String> countsAfterC = coordinator.queryCounts("ConcC");
        long deltaB = Long.parseLong(countsAfterB.getOrDefault("received", "0")) - receivedBeforeB;
        long deltaC = Long.parseLong(countsAfterC.getOrDefault("received", "0")) - receivedBeforeC;

        System.out.println("[CC10] Concurrent 4T/100: ConcB received=" + deltaB + ", ConcC received=" + deltaC);

        assertTrue(receivedB, "ConcB should receive all 100 concurrent events");
        assertTrue(receivedC, "ConcC should receive all 100 concurrent events");
        assertTrue(deltaB >= 100, "ConcB should have received >= 100 events, got: " + deltaB);
        assertTrue(deltaC >= 100, "ConcC should have received >= 100 events, got: " + deltaC);
    }

    @Test
    @Order(20)
    @DisplayName("CC20: 并发8线程发送200事件 - 高并发压力测试")
    void testConcurrent8Thread200Events() throws Exception {
        Map<String, String> countsBeforeB = coordinator.queryCounts("ConcB");
        Map<String, String> countsBeforeC = coordinator.queryCounts("ConcC");
        long receivedBeforeB = Long.parseLong(countsBeforeB.getOrDefault("received", "0"));
        long receivedBeforeC = Long.parseLong(countsBeforeC.getOrDefault("received", "0"));

        String result = coordinator.concurrentSend("ConcA", 200, 8, "ActivityEvent", "high-concurrency-8t");
        assertTrue(result.startsWith("OK:"), "Concurrent send should return OK");

        boolean receivedB = coordinator.waitForReceived("ConcB", receivedBeforeB + 200, 30000);
        boolean receivedC = coordinator.waitForReceived("ConcC", receivedBeforeC + 200, 30000);

        Map<String, String> countsAfterB = coordinator.queryCounts("ConcB");
        Map<String, String> countsAfterC = coordinator.queryCounts("ConcC");
        long deltaB = Long.parseLong(countsAfterB.getOrDefault("received", "0")) - receivedBeforeB;
        long deltaC = Long.parseLong(countsAfterC.getOrDefault("received", "0")) - receivedBeforeC;

        System.out.println("[CC20] Concurrent 8T/200: ConcB received=" + deltaB + ", ConcC received=" + deltaC);

        assertTrue(receivedB, "ConcB should receive all 200 high-concurrency events");
        assertTrue(receivedC, "ConcC should receive all 200 high-concurrency events");
        assertTrue(deltaB >= 200, "ConcB should have received >= 200 events, got: " + deltaB);
        assertTrue(deltaC >= 200, "ConcC should have received >= 200 events, got: " + deltaC);
    }

    @Test
    @Order(30)
    @DisplayName("CC30: 多节点并发同时发送 - A/B/C各发50事件，验证互收完整性")
    void testMultiNodeConcurrentSend() throws Exception {
        Map<String, String> countsBeforeA = coordinator.queryCounts("ConcA");
        Map<String, String> countsBeforeB = coordinator.queryCounts("ConcB");
        Map<String, String> countsBeforeC = coordinator.queryCounts("ConcC");

        long receivedBeforeA = Long.parseLong(countsBeforeA.getOrDefault("received", "0"));
        long receivedBeforeB = Long.parseLong(countsBeforeB.getOrDefault("received", "0"));
        long receivedBeforeC = Long.parseLong(countsBeforeC.getOrDefault("received", "0"));

        long sentBeforeA = Long.parseLong(countsBeforeA.getOrDefault("sent", "0"));
        long sentBeforeB = Long.parseLong(countsBeforeB.getOrDefault("sent", "0"));
        long sentBeforeC = Long.parseLong(countsBeforeC.getOrDefault("sent", "0"));

        ExecutorService testExecutor = Executors.newFixedThreadPool(3);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(3);

        testExecutor.submit(() -> {
            try { startLatch.await(); coordinator.batchSend("ConcA", 50, "ProcessEvent", "multi-node-a"); } catch (Exception e) {} finally { doneLatch.countDown(); }
        });
        testExecutor.submit(() -> {
            try { startLatch.await(); coordinator.batchSend("ConcB", 50, "ActivityEvent", "multi-node-b"); } catch (Exception e) {} finally { doneLatch.countDown(); }
        });
        testExecutor.submit(() -> {
            try { startLatch.await(); coordinator.batchSend("ConcC", 50, "SkillFlowEvent", "multi-node-c"); } catch (Exception e) {} finally { doneLatch.countDown(); }
        });

        startLatch.countDown();
        doneLatch.await(30, TimeUnit.SECONDS);
        testExecutor.shutdown();

        Thread.sleep(5000);

        Map<String, String> countsAfterA = coordinator.queryCounts("ConcA");
        Map<String, String> countsAfterB = coordinator.queryCounts("ConcB");
        Map<String, String> countsAfterC = coordinator.queryCounts("ConcC");

        long sentA = Long.parseLong(countsAfterA.getOrDefault("sent", "0")) - sentBeforeA;
        long sentB = Long.parseLong(countsAfterB.getOrDefault("sent", "0")) - sentBeforeB;
        long sentC = Long.parseLong(countsAfterC.getOrDefault("sent", "0")) - sentBeforeC;

        long receivedA = Long.parseLong(countsAfterA.getOrDefault("received", "0")) - receivedBeforeA;
        long receivedB = Long.parseLong(countsAfterB.getOrDefault("received", "0")) - receivedBeforeB;
        long receivedC = Long.parseLong(countsAfterC.getOrDefault("received", "0")) - receivedBeforeC;

        System.out.println("[CC30] Multi-node concurrent: A sent=" + sentA + " received=" + receivedA);
        System.out.println("[CC30] Multi-node concurrent: B sent=" + sentB + " received=" + receivedB);
        System.out.println("[CC30] Multi-node concurrent: C sent=" + sentC + " received=" + receivedC);

        assertEquals(50, sentA, "ConcA should have sent 50 events");
        assertEquals(50, sentB, "ConcB should have sent 50 events");
        assertEquals(50, sentC, "ConcC should have sent 50 events");

        assertTrue(receivedA >= 80, "ConcA should receive from B+C (>=80 with UDP): " + receivedA);
        assertTrue(receivedB >= 80, "ConcB should receive from A+C (>=80): " + receivedB);
        assertTrue(receivedC >= 80, "ConcC should receive from A+B (>=80): " + receivedC);
    }

    @Test
    @Order(40)
    @DisplayName("CC40: 传输一致性审计 - 对比三节点发送/接收/消费计数")
    void testTransmissionConsistencyAudit() throws Exception {
        Map<String, String> statusA = coordinator.queryStatus("ConcA");
        Map<String, String> statusB = coordinator.queryStatus("ConcB");
        Map<String, String> statusC = coordinator.queryStatus("ConcC");

        long sentA = Long.parseLong(statusA.getOrDefault("sent", "0"));
        long sentB = Long.parseLong(statusB.getOrDefault("sent", "0"));
        long sentC = Long.parseLong(statusC.getOrDefault("sent", "0"));

        long receivedA = Long.parseLong(statusA.getOrDefault("received", "0"));
        long receivedB = Long.parseLong(statusB.getOrDefault("received", "0"));
        long receivedC = Long.parseLong(statusC.getOrDefault("received", "0"));

        long consumedA = Long.parseLong(statusA.getOrDefault("consumed", "0"));
        long consumedB = Long.parseLong(statusB.getOrDefault("consumed", "0"));
        long consumedC = Long.parseLong(statusC.getOrDefault("consumed", "0"));

        long bytesSentA = Long.parseLong(statusA.getOrDefault("bytesSent", "0"));
        long bytesReceivedA = Long.parseLong(statusA.getOrDefault("bytesReceived", "0"));
        long bytesSentB = Long.parseLong(statusB.getOrDefault("bytesSent", "0"));
        long bytesReceivedB = Long.parseLong(statusB.getOrDefault("bytesReceived", "0"));
        long bytesSentC = Long.parseLong(statusC.getOrDefault("bytesSent", "0"));
        long bytesReceivedC = Long.parseLong(statusC.getOrDefault("bytesReceived", "0"));

        System.out.println("===== CC40 传输一致性审计报告 =====");
        System.out.println("ConcA: sent=" + sentA + ", received=" + receivedA + ", consumed=" + consumedA + ", bytesSent=" + bytesSentA + ", bytesReceived=" + bytesReceivedA);
        System.out.println("ConcB: sent=" + sentB + ", received=" + receivedB + ", consumed=" + consumedB + ", bytesSent=" + bytesSentB + ", bytesReceived=" + bytesReceivedB);
        System.out.println("ConcC: sent=" + sentC + ", received=" + receivedC + ", consumed=" + consumedC + ", bytesSent=" + bytesSentC + ", bytesReceived=" + bytesReceivedC);

        long totalSent = sentA + sentB + sentC;
        long totalReceived = receivedA + receivedB + receivedC;
        long totalConsumed = consumedA + consumedB + consumedC;
        long totalBytesSent = bytesSentA + bytesSentB + bytesSentC;
        long totalBytesReceived = bytesReceivedA + bytesReceivedB + bytesReceivedC;

        System.out.println("Total: sent=" + totalSent + ", received=" + totalReceived + ", consumed=" + totalConsumed);
        System.out.println("Total: bytesSent=" + totalBytesSent + ", bytesReceived=" + totalBytesReceived);

        assertTrue(totalSent > 0, "Total sent should be > 0");
        assertTrue(totalReceived > 0, "Total received should be > 0");
        assertTrue(totalConsumed > 0, "Total consumed should be > 0");
        assertTrue(totalBytesSent > 0, "Total bytesSent should be > 0");
        assertTrue(totalBytesReceived > 0, "Total bytesReceived should be > 0");

        assertTrue(receivedA >= consumedA, "ConcA: received >= consumed");
        assertTrue(receivedB >= consumedB, "ConcB: received >= consumed");
        assertTrue(receivedC >= consumedC, "ConcC: received >= consumed");

        assertTrue(totalReceived >= totalSent, "Total received should >= total sent (each broadcast reaches 2 peers)");
        System.out.println("===== CC40 审计通过 =====");
    }

    @Test
    @Order(50)
    @DisplayName("CC50: 网络拓扑验证 - 三节点全连接拓扑确认")
    void testNetworkTopologyVerification() throws Exception {
        String peersA = coordinator.queryPeers("ConcA");
        String peersB = coordinator.queryPeers("ConcB");
        String peersC = coordinator.queryPeers("ConcC");

        System.out.println("[CC50] ConcA peers: " + peersA);
        System.out.println("[CC50] ConcB peers: " + peersB);
        System.out.println("[CC50] ConcC peers: " + peersC);

        assertTrue(peersA.contains("ConcB"), "ConcA should know ConcB");
        assertTrue(peersA.contains("ConcC"), "ConcA should know ConcC");
        assertTrue(peersB.contains("ConcA"), "ConcB should know ConcA");
        assertTrue(peersB.contains("ConcC"), "ConcB should know ConcC");
        assertTrue(peersC.contains("ConcA"), "ConcC should know ConcA");
        assertTrue(peersC.contains("ConcB"), "ConcC should know ConcB");

        Map<String, String> statusA = coordinator.queryStatus("ConcA");
        int peersCountA = Integer.parseInt(statusA.getOrDefault("peers", "0"));
        assertEquals(2, peersCountA, "ConcA should have exactly 2 peers");

        String agentInfoA = coordinator.queryAgentInfo("ConcA");
        assertTrue(agentInfoA.contains("agentId=ConcA"), "AgentInfo should contain agentId");
        assertTrue(agentInfoA.contains("status=ONLINE"), "Agent should be ONLINE");
        assertTrue(agentInfoA.contains("bpm-event-broadcast"), "Agent should have broadcast capability");

        System.out.println("[CC50] AgentInfo: " + agentInfoA);
        System.out.println("[CC50] Full mesh topology verified: A<->B, A<->C, B<->C");
    }

    @Test
    @Order(60)
    @DisplayName("CC60: 混合域并发事件 - PROCESS/ACTIVITY/SKILLFLOW/AGENT/SCENE_BRIDGE交替发送")
    void testMixedDomainConcurrentEvents() throws Exception {
        Map<String, String> countsBeforeB = coordinator.queryCounts("ConcB");
        long receivedBeforeB = Long.parseLong(countsBeforeB.getOrDefault("received", "0"));

        int eventsPerDomain = 10;
        for (BpmEventDomain domain : BpmEventDomain.values()) {
            for (int i = 0; i < eventsPerDomain; i++) {
                BpmTestEvent event = null;
                if (domain == BpmEventDomain.PROCESS) {
                    event = BpmTestEvent.processEvent("ConcA", "proc-mix-" + i, "STARTED");
                } else if (domain == BpmEventDomain.ACTIVITY) {
                    event = BpmTestEvent.activityEvent("ConcA", "act-mix-" + i, "agent-mix", "INITED");
                } else if (domain == BpmEventDomain.SKILLFLOW) {
                    event = BpmTestEvent.skillFlowEvent("ConcA", "sf-mix-" + i, BpmSkillFlowState.DEPLOYED, BpmSkillFlowState.ACTIVATED);
                } else if (domain == BpmEventDomain.AGENT) {
                    event = BpmTestEvent.agentEvent("ConcA", "agent-mix", "DELEGATE", "task-mix-" + i);
                } else if (domain == BpmEventDomain.SCENE_BRIDGE) {
                    event = BpmTestEvent.sceneBridgeEvent("ConcA", "workflow.registered", Map.of("sceneId", "scene-mix-" + i));
                }
                if (event == null) continue;
                coordinator.sendEvent("ConcA", event.getEventType(), event.getPayload(),
                    "domain=" + domain.name() + ",index=" + i);
            }
        }

        int totalExpected = BpmEventDomain.values().length * eventsPerDomain;
        boolean received = coordinator.waitForReceived("ConcB", receivedBeforeB + totalExpected, 20000);

        Map<String, String> countsAfterB = coordinator.queryCounts("ConcB");
        long deltaB = Long.parseLong(countsAfterB.getOrDefault("received", "0")) - receivedBeforeB;

        System.out.println("[CC60] Mixed domain: expected=" + totalExpected + ", ConcB received=" + deltaB);

        assertTrue(received, "ConcB should receive all mixed domain events");
        assertTrue(deltaB >= totalExpected, "ConcB should have received >= " + totalExpected + " events, got: " + deltaB);
    }
}
