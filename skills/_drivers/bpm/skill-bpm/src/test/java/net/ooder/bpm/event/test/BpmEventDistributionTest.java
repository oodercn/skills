package net.ooder.bpm.event.test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BpmEventDistributionTest {

    private static final int NUM_RECEIVERS = 2;
    private static BpmEventNetwork javaSerNetwork;
    private static BpmEventNetwork jsonSerNetwork;
    private static BpmEventNetwork directMemNetwork;
    private static BpmEventContainer serverA;
    private static BpmEventContainer serverB;
    private static BpmEventContainer clientC;
    private static BpmEventContainer agentOnlyNode;

    @BeforeAll
    static void setup() {
        javaSerNetwork = new BpmEventNetwork(BpmEventNetwork.TransportMode.JAVA_SERIALIZATION, 0);
        serverA = new BpmEventContainer("ServerA", javaSerNetwork);
        serverB = new BpmEventContainer("ServerB", javaSerNetwork);
        clientC = new BpmEventContainer("ClientC", javaSerNetwork);
        agentOnlyNode = new BpmEventContainer("AgentNode", javaSerNetwork,
            EnumSet.of(BpmEventDomain.AGENT, BpmEventDomain.SCENE_BRIDGE));
        serverA.start();
        serverB.start();
        clientC.start();
        agentOnlyNode.start();
    }

    @AfterAll
    static void teardown() {
        serverA.stop();
        serverB.stop();
        clientC.stop();
        agentOnlyNode.stop();
        javaSerNetwork.shutdown();
    }

    @BeforeEach
    void clearRecords() {
        for (BpmEventContainer c : Arrays.asList(serverA, serverB, clientC, agentOnlyNode)) {
            c.getRecord().clear();
            c.goOnline();
        }
    }

    private void waitForDelivery(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void assertSerializationIsolation(BpmEventContainer sender, BpmEventContainer receiver, String eventId) {
        Integer sentIdentity = sender.getRecord().getSentObjectIdentity(eventId);
        Integer receivedIdentity = receiver.getRecord().getReceivedObjectIdentity(eventId);
        assertNotNull(sentIdentity, "Sender must have sent identity for " + eventId);
        assertNotNull(receivedIdentity, "Receiver must have received identity for " + eventId);
        assertNotEquals(sentIdentity, receivedIdentity,
            "After serialization transport, objects MUST be different instances: sender@" + sentIdentity + " vs receiver@" + receivedIdentity);
    }

    private void assertFieldByFieldMatch(BpmEventContainer sender, BpmEventContainer receiver, String eventId,
                                          String[] fieldNames, String[] expectedValues) {
        assertEquals(fieldNames.length, expectedValues.length, "fieldNames and expectedValues must match");
        for (int i = 0; i < fieldNames.length; i++) {
            String actual = receiver.getRecord().getConsumedFieldValue(eventId, fieldNames[i]);
            assertNotNull(actual, "Field '" + fieldNames[i] + "' must exist in consumed record for event " + eventId);
            assertEquals(expectedValues[i], actual,
                "Field '" + fieldNames[i] + "' mismatch for event " + eventId);
        }
    }

    private void assertReceivedEventMatchesSent(BpmEventContainer sender, BpmEventContainer receiver, String eventId) {
        Object sentObj = sender.getRecord().getSentEntry(eventId).event;
        Object receivedObj = receiver.getRecord().getReceivedEntry(eventId).event;
        assertNotNull(receivedObj, "Receiver must have received event object for " + eventId);
        assertTrue(receivedObj instanceof BpmTestEvent, "Received event must be BpmTestEvent");
        assertTrue(sentObj instanceof BpmTestEvent, "Sent event must be BpmTestEvent");

        BpmTestEvent sent = (BpmTestEvent) sentObj;
        BpmTestEvent received = (BpmTestEvent) receivedObj;

        assertEquals(sent.getEventId(), received.getEventId(), "eventId must match after serialization");
        assertEquals(sent.getEventType(), received.getEventType(), "eventType must match after serialization");
        assertEquals(sent.getDomain(), received.getDomain(), "domain must match after serialization");
        assertEquals(sent.getSource(), received.getSource(), "source must match after serialization");
        assertEquals(sent.getPayload(), received.getPayload(), "payload must match after serialization");
        if (sent.getSceneEventCode() != null) {
            assertEquals(sent.getSceneEventCode(), received.getSceneEventCode(), "sceneEventCode must match");
        }

        if (sent.getMetadata() != null && received.getMetadata() != null) {
            for (Map.Entry<String, String> entry : sent.getMetadata().entrySet()) {
                assertEquals(entry.getValue(), received.getMetadata().get(entry.getKey()),
                    "metadata[" + entry.getKey() + "] must match after serialization");
            }
        }
    }

    // ===================== 1. 容器隔离与基础设施 =====================

    @Test
    @Order(1)
    @DisplayName("TC1: 容器隔离 - 独立Record/线程池/ID + 传输模式验证")
    void testContainerIsolation() {
        assertNotEquals(serverA.getContainerId(), serverB.getContainerId());
        assertTrue(serverA.isOnline());
        assertEquals(BpmEventNetwork.TransportMode.JAVA_SERIALIZATION, javaSerNetwork.getTransportMode());
        assertEquals(0, javaSerNetwork.getSerializationErrors(), "No serialization errors at start");
    }

    // ===================== 2. 序列化传输验证（修复P0-1） =====================

    @Test
    @Order(2)
    @DisplayName("TC2: Java序列化传输 - 对象身份不同+逐字段独立验证（修复内存直传问题）")
    void testJavaSerializationTransport() {
        BpmTestEvent event = BpmTestEvent.processEvent("ServerA", "proc-ser-1", "STARTED");
        serverA.publishEventSync(event);
        waitForDelivery(500);

        String sentId = serverA.getRecord().getSentEventIds().iterator().next();
        assertSerializationIsolation(serverA, serverB, sentId);
        assertSerializationIsolation(serverA, clientC, sentId);

        assertReceivedEventMatchesSent(serverA, serverB, sentId);
        assertReceivedEventMatchesSent(serverA, clientC, sentId);

        Integer bRef = serverB.getRecord().getReceivedObjectIdentity(sentId);
        Integer cRef = clientC.getRecord().getReceivedObjectIdentity(sentId);
        assertNotEquals(bRef, cRef, "B and C must receive DIFFERENT deserialized instances");

        assertEquals(0, javaSerNetwork.getSerializationErrors(), "No serialization errors");
        assertTrue(javaSerNetwork.getTotalBytesTransmitted() > 0, "Bytes must have been transmitted");
    }

    // ===================== 3. 独立字段验证（修复P0-3/P0-4） =====================

    @Test
    @Order(3)
    @DisplayName("TC3: ProcessEvent - 从反序列化对象独立提取字段验证（修复循环自证）")
    void testProcessEventIndependentFieldVerification() {
        BpmTestEvent event = BpmTestEvent.processEvent("ServerA", "proc-ind-1", "COMPLETED");
        serverA.publishEventSync(event);
        waitForDelivery(500);

        String sentId = serverA.getRecord().getSentEventIds().iterator().next();
        for (BpmEventContainer c : Arrays.asList(serverB, clientC)) {
            assertFieldByFieldMatch(serverA, c, sentId,
                new String[]{"eventType", "payload", "processInstId", "state", "domain", "source"},
                new String[]{"ProcessEvent", "COMPLETED", "proc-ind-1", "COMPLETED", "PROCESS", "ServerA"});
        }
    }

    @Test
    @Order(4)
    @DisplayName("TC4: ActivityEvent - 从反序列化对象独立提取agentId+action")
    void testActivityEventIndependentFieldVerification() {
        BpmTestEvent event = BpmTestEvent.activityEvent("ServerA", "act-ind-1", "agent-llm-1", "INITED");
        serverA.publishEventSync(event);
        waitForDelivery(500);

        String sentId = serverA.getRecord().getSentEventIds().iterator().next();
        for (BpmEventContainer c : Arrays.asList(serverB, clientC)) {
            assertFieldByFieldMatch(serverA, c, sentId,
                new String[]{"activityInstId", "agentId", "action", "domain"},
                new String[]{"act-ind-1", "agent-llm-1", "INITED", "ACTIVITY"});
        }
    }

    // ===================== 5. 事件域过滤 =====================

    @Test
    @Order(5)
    @DisplayName("TC5: 事件域过滤 - AgentNode只接收AGENT和SCENE_BRIDGE（经过序列化）")
    void testDomainFiltering() {
        serverA.publishProcessEvent("proc-filter", "STARTED");
        serverA.publishAgentEvent("agent-filter", "TASK_DELEGATE", "review");
        serverA.publishSkillFlowEvent("sf-filter", BpmSkillFlowState.DEPLOYED, BpmSkillFlowState.ACTIVATED);
        serverA.publishSceneBridgeEvent("workflow.completed", Map.of("workflowId", "wf-1"));
        waitForDelivery(500);

        assertEquals(4, serverB.getRecord().getTotalReceived());
        assertEquals(4, clientC.getRecord().getTotalReceived());
        assertEquals(2, agentOnlyNode.getRecord().getTotalReceived());
        assertEquals(1, agentOnlyNode.getRecord().getReceivedCountByType("AgentEvent"));
        assertEquals(1, agentOnlyNode.getRecord().getReceivedCountByType("SceneBridgeEvent"));
        assertEquals(0, agentOnlyNode.getRecord().getReceivedCountByType("ProcessEvent"));
    }

    // ===================== 6. 定向投递 =====================

    @Test
    @Order(6)
    @DisplayName("TC6: 定向投递 - sendTo经过序列化只投递到目标容器")
    void testTargetedDelivery() {
        BpmTestEvent event = BpmTestEvent.agentEvent("ServerA", "agent-target", "TASK_DELEGATE", "approve");
        serverA.sendTo("ServerB", event);
        waitForDelivery(500);

        assertEquals(1, serverB.getRecord().getTotalReceived());
        assertEquals(0, clientC.getRecord().getTotalReceived());
        assertEquals(0, agentOnlyNode.getRecord().getTotalReceived());
    }

    // ===================== 7. JSON序列化传输 =====================

    @Test
    @Order(7)
    @DisplayName("TC7: JSON序列化传输 - 验证JSON编解码后字段完整性")
    void testJsonSerializationTransport() {
        BpmEventNetwork jsonNet = new BpmEventNetwork(BpmEventNetwork.TransportMode.JSON_SERIALIZATION, 0);
        BpmEventContainer jsonSender = new BpmEventContainer("JsonSender", jsonNet);
        BpmEventContainer jsonReceiver = new BpmEventContainer("JsonReceiver", jsonNet);
        jsonSender.start();
        jsonReceiver.start();

        try {
            BpmTestEvent event = BpmTestEvent.skillFlowEvent("JsonSender", "sf-json-1",
                BpmSkillFlowState.DEPLOYED, BpmSkillFlowState.ACTIVATED);
            jsonSender.publishEventSync(event);
            waitForDelivery(500);

            assertEquals(1, jsonReceiver.getRecord().getTotalReceived());
            assertEquals(1, jsonReceiver.getRecord().getTotalConsumed());

            String sentId = jsonSender.getRecord().getSentEventIds().iterator().next();
            assertFieldByFieldMatch(jsonSender, jsonReceiver, sentId,
                new String[]{"eventType", "payload", "skillFlowId", "fromState", "toState", "domain"},
                new String[]{"SkillFlowEvent", "deployed->activated", "sf-json-1", "deployed", "activated", "SKILLFLOW"});

            assertTrue(jsonNet.getTotalBytesTransmitted() > 0);
            assertEquals(0, jsonNet.getSerializationErrors());
        } finally {
            jsonSender.stop();
            jsonReceiver.stop();
            jsonNet.shutdown();
        }
    }

    // ===================== 10. 串行分发 =====================

    @Test
    @Order(10)
    @DisplayName("TC10: 串行分发 - 10事件经过序列化传输+逐事件字段验证")
    void testSerialDistribution() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            serverA.publishProcessEvent("proc-serial-" + i, "STARTED");
        }
        waitForDelivery(1000);

        assertEquals(count, serverA.getRecord().getTotalSent());
        assertEquals(count, serverB.getRecord().getTotalReceived());
        assertEquals(count, serverB.getRecord().getTotalConsumed());
        assertEquals(0, serverA.getRecord().getTotalReceived());

        for (String id : serverA.getRecord().getSentEventIds()) {
            assertSerializationIsolation(serverA, serverB, id);
            assertReceivedEventMatchesSent(serverA, serverB, id);
        }
    }

    // ===================== 20. 并行分发 =====================

    @Test
    @Order(20)
    @DisplayName("TC20: 并行分发 - 序列化传输下无丢失无重复")
    void testParallelDistribution() throws Exception {
        int eventsPerNode = 30;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        new Thread(() -> {
            try { startLatch.await(); } catch (InterruptedException e) { return; }
            for (int i = 0; i < eventsPerNode; i++) {
                serverA.publishProcessEvent("par-a-" + i, "STARTED");
            }
            doneLatch.countDown();
        }).start();

        new Thread(() -> {
            try { startLatch.await(); } catch (InterruptedException e) { return; }
            for (int i = 0; i < eventsPerNode; i++) {
                serverB.publishActivityEvent("act-b-" + i, "agent-par-" + i, "INITED");
            }
            doneLatch.countDown();
        }).start();

        startLatch.countDown();
        assertTrue(doneLatch.await(15, TimeUnit.SECONDS));
        waitForDelivery(2000);

        Set<String> allSentIds = new HashSet<>();
        allSentIds.addAll(serverA.getRecord().getSentEventIds());
        allSentIds.addAll(serverB.getRecord().getSentEventIds());

        assertTrue(clientC.getRecord().findMissingReceivedIds(allSentIds).isEmpty());
        assertTrue(clientC.getRecord().findDuplicateReceivedIds().isEmpty());
    }

    // ===================== 30. 离线/上线 =====================

    @Test
    @Order(30)
    @DisplayName("TC30: 离线 - B离线不接收不消费，C正常接收序列化事件")
    void testOfflineNodeDoesNotReceive() {
        serverB.goOffline();
        serverA.publishProcessEvent("proc-off-1", "SUSPENDED");
        waitForDelivery(500);

        assertEquals(0, serverB.getRecord().getTotalReceived());
        assertEquals(1, clientC.getRecord().getTotalReceived());
        assertEquals(1, clientC.getRecord().getTotalConsumed());
    }

    @Test
    @Order(31)
    @DisplayName("TC31: 离线恢复 - 上线后只接收新事件")
    void testOfflineRecovery() {
        serverB.goOffline();
        serverA.publishProcessEvent("proc-offline-pre", "SUSPENDED");
        waitForDelivery(300);
        assertEquals(0, serverB.getRecord().getTotalReceived());

        serverB.goOnline();
        serverA.publishProcessEvent("proc-offline-post", "RESUMED");
        waitForDelivery(500);

        assertEquals(1, serverB.getRecord().getTotalReceived());
        String receivedId = serverB.getRecord().getReceivedEventIds().iterator().next();
        assertEquals("RESUMED", serverB.getRecord().getConsumedFieldValue(receivedId, "state"));
    }

    // ===================== 40. 并发 =====================

    @Test
    @Order(40)
    @DisplayName("TC40: 并发 - 8线程x25事件序列化传输，无丢失无重复")
    void testConcurrentPublish() throws Exception {
        int threadCount = 8;
        int eventsPerThread = 25;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger errors = new AtomicInteger(0);

        for (int t = 0; t < threadCount; t++) {
            final int threadIdx = t;
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < eventsPerThread; i++) {
                        serverA.publishActivityEvent("act-conc-" + threadIdx + "-" + i, "agent-" + threadIdx, "ACTIVING");
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        assertEquals(0, errors.get());
        waitForDelivery(3000);

        int totalExpected = threadCount * eventsPerThread;
        assertEquals(totalExpected, serverA.getRecord().getTotalSent());
        assertEquals(totalExpected, serverB.getRecord().getTotalReceived());
        assertEquals(totalExpected, serverB.getRecord().getTotalConsumed());
        assertTrue(serverB.getRecord().findDuplicateReceivedIds().isEmpty());
        assertEquals(0, javaSerNetwork.getSerializationErrors());
    }

    // ===================== 50. 全闭环 =====================

    @Test
    @Order(50)
    @DisplayName("TC50: 全闭环 - 序列化传输下sent*receivers=received=consumed")
    void testFullClosedLoop() {
        int eventsPerContainer = 25;
        int totalExpectedSent = eventsPerContainer * 3;
        int totalExpectedReceived = totalExpectedSent * NUM_RECEIVERS;

        IntStream.range(0, eventsPerContainer).parallel().forEach(i -> {
            serverA.publishProcessEvent("proc-loop-A-" + i, "STARTED");
            serverB.publishActivityEvent("act-loop-B-" + i, "agent-loop-" + i, "INITED");
            clientC.publishSkillFlowEvent("sf-loop-C-" + i, BpmSkillFlowState.DEPLOYED, BpmSkillFlowState.ACTIVATED);
        });
        waitForDelivery(3000);

        long totalSent = serverA.getRecord().getTotalSent() + serverB.getRecord().getTotalSent() + clientC.getRecord().getTotalSent();
        long totalReceived = serverA.getRecord().getTotalReceived() + serverB.getRecord().getTotalReceived() + clientC.getRecord().getTotalReceived();
        long totalConsumed = serverA.getRecord().getTotalConsumed() + serverB.getRecord().getTotalConsumed() + clientC.getRecord().getTotalConsumed();

        assertEquals(totalExpectedSent, totalSent);
        assertEquals(totalExpectedReceived, totalReceived);
        assertEquals(totalExpectedReceived, totalConsumed);

        for (String id : serverA.getRecord().getSentEventIds()) {
            assertSerializationIsolation(serverA, serverB, id);
        }
    }

    // ===================== 60. SkillFlow生命周期 =====================

    @Test
    @Order(60)
    @DisplayName("TC60: SkillFlow正常生命周期 - 序列化传输+逐状态字段验证")
    void testSkillFlowNormalLifecycle() {
        String sfId = "sf-normal-1";
        List<BpmSkillFlowState> path = BpmSkillFlowState.validLifecyclePath();

        for (int i = 0; i < path.size() - 1; i++) {
            assertTrue(path.get(i).canTransitionTo(path.get(i + 1)));
            serverA.publishSkillFlowEvent(sfId, path.get(i), path.get(i + 1));
        }
        waitForDelivery(1000);

        assertEquals(path.size() - 1, serverB.getRecord().getTotalConsumed());

        for (BpmEventRecord.RecordEntry sent : serverA.getRecord().getSentEvents()) {
            assertTrue(serverB.getRecord().verifyConsumedField(sent.eventId, "skillFlowId", sfId));
            assertReceivedEventMatchesSent(serverA, serverB, sent.eventId);
        }
    }

    @Test
    @Order(62)
    @DisplayName("TC62: SkillFlow非法状态转换拒绝")
    void testSkillFlowInvalidTransition() {
        assertFalse(BpmSkillFlowState.DISCOVERED.canTransitionTo(BpmSkillFlowState.RUNNING));
        assertFalse(BpmSkillFlowState.COMPLETED.canTransitionTo(BpmSkillFlowState.RUNNING));
        assertFalse(BpmSkillFlowState.PAUSED.canTransitionTo(BpmSkillFlowState.COMPLETED));
        assertFalse(BpmSkillFlowState.ERROR.canTransitionTo(BpmSkillFlowState.RUNNING));
    }

    // ===================== 70. Agent委派闭环 =====================

    @Test
    @Order(70)
    @DisplayName("TC70: Agent委派闭环 - 序列化传输下TASK_DELEGATE→TASK_RESULT")
    void testAgentDelegationClosedLoop() {
        BpmTestEvent delegate = BpmTestEvent.agentEvent("ServerA", "agent-1", "TASK_DELEGATE", "Review document");
        serverA.publishEventSync(delegate);
        waitForDelivery(500);

        String delegateId = serverA.getRecord().getSentEventIds().iterator().next();
        assertReceivedEventMatchesSent(serverA, serverB, delegateId);
        assertEquals("agent-1", serverB.getRecord().getConsumedFieldValue(delegateId, "agentId"));
        assertEquals("TASK_DELEGATE", serverB.getRecord().getConsumedFieldValue(delegateId, "messageType"));

        BpmTestEvent result = BpmTestEvent.agentEvent("ServerB", "agent-1", "TASK_RESULT", "Approved");
        serverB.publishEventSync(result);
        waitForDelivery(500);

        String resultId = serverB.getRecord().getSentEventIds().stream()
            .filter(id -> "AgentEvent".equals(serverB.getRecord().getSentEntry(id).eventType))
            .findFirst().orElse(null);
        assertNotNull(resultId);
        assertReceivedEventMatchesSent(serverB, serverA, resultId);
        assertEquals("TASK_RESULT", serverA.getRecord().getConsumedFieldValue(resultId, "messageType"));
    }

    // ===================== 80. Scene桥接 =====================

    @Test
    @Order(80)
    @DisplayName("TC80: Scene桥接事件 - 序列化传输后mappedBpmState字段验证")
    void testSceneBridgeEventPropagation() {
        BpmTestEvent event = SceneBpmEventMapper.bridgeSceneEvent("ClientC", "workflow.completed",
            Map.of("workflowId", "wf-bridge-1"));
        clientC.publishEventSync(event);
        waitForDelivery(500);

        String sentId = clientC.getRecord().getSentEventIds().iterator().next();
        assertSerializationIsolation(clientC, serverA, sentId);
        assertReceivedEventMatchesSent(clientC, serverA, sentId);

        assertEquals("workflow.completed", serverA.getRecord().getConsumedFieldValue(sentId, "sceneEventCode"));
        assertEquals("COMPLETED", serverA.getRecord().getConsumedFieldValue(sentId, "mappedBpmState"));
    }

    @Test
    @Order(83)
    @DisplayName("TC83: Scene桥接双向闭环 - 序列化传输下Scene→BPM→Scene")
    void testSceneBridgeBidirectionalLoop() {
        BpmTestEvent sceneToBpm = SceneBpmEventMapper.bridgeSceneEvent("ClientC", "skill.installed",
            Map.of("skillId", "skill-bridge-1"));
        clientC.publishEventSync(sceneToBpm);
        waitForDelivery(500);

        String sceneSentId = clientC.getRecord().getSentEventIds().iterator().next();
        assertEquals("DEPLOYED", serverA.getRecord().getConsumedFieldValue(sceneSentId, "mappedBpmState"));

        BpmTestEvent bpmToScene = SceneBpmEventMapper.bridgeBpmEvent("ServerA", "ACTIVATED",
            Map.of("skillId", "skill-bridge-1"));
        serverA.publishEventSync(bpmToScene);
        waitForDelivery(500);

        String bpmSentId = serverA.getRecord().getSentEventIds().stream()
            .filter(id -> "SceneBridgeEvent".equals(serverA.getRecord().getSentEntry(id).eventType))
            .findFirst().orElse(null);
        assertNotNull(bpmSentId);
        assertReceivedEventMatchesSent(serverA, clientC, bpmSentId);
        assertEquals("skill.started", clientC.getRecord().getConsumedFieldValue(bpmSentId, "sceneEventCode"));
    }

    // ===================== 90. 自接收排除 =====================

    @Test
    @Order(90)
    @DisplayName("TC90: 自接收排除 - 发送者不接收自身事件")
    void testNoSelfReceive() {
        serverA.publishProcessEvent("proc-self", "STARTED");
        waitForDelivery(500);

        assertEquals(1, serverA.getRecord().getTotalSent());
        assertEquals(0, serverA.getRecord().getTotalReceived());
        assertEquals(0, serverA.getRecord().getTotalConsumed());
        assertEquals(1, serverB.getRecord().getTotalReceived());
    }

    // ===================== 91. 序列化完整性深度验证 =====================

    @Test
    @Order(91)
    @DisplayName("TC91: 序列化完整性 - metadata所有字段经过序列化/反序列化后逐字段匹配")
    void testSerializationIntegrityAllFields() {
        BpmTestEvent event = BpmTestEvent.activityEvent("ServerA", "act-integrity", "agent-int", "COMPLETED");
        event.getMetadata().put("zField", "last");
        event.getMetadata().put("aField", "first");
        event.getMetadata().put("specialChars", "val=with=equals");
        serverA.publishEventSync(event);
        waitForDelivery(500);

        for (String id : serverA.getRecord().getSentEventIds()) {
            assertReceivedEventMatchesSent(serverA, serverB, id);
            assertReceivedEventMatchesSent(serverA, clientC, id);
        }
    }

    // ===================== 92. 无序列化错误 =====================

    @Test
    @Order(92)
    @DisplayName("TC92: 全量Scene事件映射 - 序列化传输无错误")
    void testAllSceneEventMappingsViaSerialization() {
        Set<String> sceneCodes = SceneBpmEventMapper.getAllSceneEventCodes();
        assertFalse(sceneCodes.isEmpty());

        for (String code : sceneCodes) {
            assertNotEquals("UNKNOWN", SceneBpmEventMapper.sceneToBpmState(code));
            serverA.publishSceneBridgeEvent(code, Map.of("testKey", code));
        }
        waitForDelivery(2000);

        assertEquals(sceneCodes.size(), serverA.getRecord().getTotalSent());
        assertEquals(sceneCodes.size(), serverB.getRecord().getTotalReceived());
        assertEquals(sceneCodes.size(), serverB.getRecord().getTotalConsumed());
        assertEquals(0, javaSerNetwork.getSerializationErrors(), "Zero serialization errors across all Scene events");
    }

    // ===================== 93. 传输字节数验证 =====================

    @Test
    @Order(93)
    @DisplayName("TC93: 传输字节数验证 - 序列化确实产生了字节数据")
    void testTransmissionBytesVerification() {
        long bytesBefore = javaSerNetwork.getTotalBytesTransmitted();
        for (int i = 0; i < 5; i++) {
            serverA.publishProcessEvent("proc-bytes-" + i, "STARTED");
        }
        waitForDelivery(500);

        long bytesAfter = javaSerNetwork.getTotalBytesTransmitted();
        assertTrue(bytesAfter > bytesBefore,
            "Bytes transmitted must increase after events. Before=" + bytesBefore + " After=" + bytesAfter);
        assertEquals(0, javaSerNetwork.getSerializationErrors());
    }
}
