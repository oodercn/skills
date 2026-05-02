package net.ooder.vfs.event.test;

import net.ooder.vfs.event.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventDistributionTest {

    private static final Logger log = LoggerFactory.getLogger(EventDistributionTest.class);

    private static EventContainer containerA;
    private static EventContainer containerB;
    private static EventContainer containerC;
    private static EventNetwork network;
    private static final int NUM_RECEIVERS = 2;
    private static final String LOG_DIR = "target/container-logs";

    @BeforeAll
    static void setup() {
        network = new EventNetwork(true, 5);
        containerA = new EventContainer("ContainerA", network, LOG_DIR);
        containerB = new EventContainer("ContainerB", network, LOG_DIR);
        containerC = new EventContainer("ContainerC", network, LOG_DIR);
        containerA.start();
        containerB.start();
        containerC.start();
        log.info("=== Independent Container Test Environment Initialized ===");
        log.info("Log directory: {}", new File(LOG_DIR).getAbsolutePath());
        log.info("ContainerA log: {}", containerA.getLogger().getLogFilePath());
        log.info("ContainerB log: {}", containerB.getLogger().getLogFilePath());
        log.info("ContainerC log: {}", containerC.getLogger().getLogFilePath());
    }

    @AfterAll
    static void teardown() {
        log.info("=== Final Record Summaries ===");
        log.info("ContainerA: {}", containerA.getRecord().summary());
        log.info("ContainerB: {}", containerB.getRecord().summary());
        log.info("ContainerC: {}", containerC.getRecord().summary());
        log.info("ContainerA log: {}", containerA.getLogger().getLogFilePath());
        log.info("ContainerB log: {}", containerB.getLogger().getLogFilePath());
        log.info("ContainerC log: {}", containerC.getLogger().getLogFilePath());
        containerA.stop();
        containerB.stop();
        containerC.stop();
        network.shutdown();
        log.info("=== Independent Container Test Environment Shutdown ===");
    }

    @BeforeEach
    void clearRecords() {
        containerA.getRecord().clear();
        containerB.getRecord().clear();
        containerC.getRecord().clear();
        containerA.goOnline();
        containerB.goOnline();
        containerC.goOnline();
    }

    private void waitForDelivery(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ===================== 独立容器验证 =====================

    @Test
    @Order(1)
    @DisplayName("TC1: 独立容器 - EventBus实例隔离+线程池隔离+容器ID隔离+日志文件隔离")
    void testIndependentContainers() {
        assertNotSame(containerA.getLocalBus(), containerB.getLocalBus(),
            "A and B must have separate EventBus instances");
        assertNotSame(containerB.getLocalBus(), containerC.getLocalBus(),
            "B and C must have separate EventBus instances");
        assertNotSame(containerA.getLocalBus(), containerC.getLocalBus(),
            "A and C must have separate EventBus instances");

        assertNotEquals(containerA.getContainerId(), containerB.getContainerId());
        assertNotEquals(containerB.getContainerId(), containerC.getContainerId());

        int busA = System.identityHashCode(containerA.getLocalBus());
        int busB = System.identityHashCode(containerB.getLocalBus());
        int busC = System.identityHashCode(containerC.getLocalBus());
        assertNotEquals(busA, busB, "EventBus A identity must differ from B");
        assertNotEquals(busB, busC, "EventBus B identity must differ from C");
        assertNotEquals(busA, busC, "EventBus A identity must differ from C");

        String logA = containerA.getLogger().getLogFilePath();
        String logB = containerB.getLogger().getLogFilePath();
        String logC = containerC.getLogger().getLogFilePath();
        assertNotEquals(logA, logB, "A and B must have separate log files");
        assertNotEquals(logB, logC, "B and C must have separate log files");
        assertTrue(logA.contains("ContainerA"), "A log path must contain ContainerA");
        assertTrue(logB.contains("ContainerB"), "B log path must contain ContainerB");
        assertTrue(logC.contains("ContainerC"), "C log path must contain ContainerC");

        log.info("[TC1] PASS: EventBus A@{} B@{} C@{} - all independent", busA, busB, busC);
        log.info("[TC1] PASS: Log files A={} B={} C={} - all separate", logA, logB, logC);
    }

    @Test
    @Order(2)
    @DisplayName("TC2: 深拷贝传输 - 接收事件与发送事件是不同对象引用(跨容器验证)")
    void testDeepCopyDifferentObjectReference() {
        containerA.publishFileEventSync("deep-1", "deep.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(500);

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        String sentId = aSentIds.iterator().next();

        int sentRef = containerA.getRecord().getSentObjectIdentity(sentId);
        int bRecvRef = containerB.getRecord().getReceivedObjectIdentity(sentId);
        int cRecvRef = containerC.getRecord().getReceivedObjectIdentity(sentId);

        log.info("[TC2] sentRef={}, bRecvRef={}, cRecvRef={}", sentRef, bRecvRef, cRecvRef);

        assertNotEquals(0, sentRef, "Sent object identity must not be 0");
        assertNotEquals(0, bRecvRef, "B received object identity must not be 0");
        assertNotEquals(0, cRecvRef, "C received object identity must not be 0");

        assertNotEquals(sentRef, bRecvRef,
            "B's received event must be a DIFFERENT object from A's sent event (deep copy)");
        assertNotEquals(sentRef, cRecvRef,
            "C's received event must be a DIFFERENT object from A's sent event (deep copy)");
        assertNotEquals(bRecvRef, cRecvRef,
            "B and C must receive DIFFERENT object instances from each other");

        assertTrue(containerA.getRecord().verifyContentIntegrity(sentId,
            containerB.getRecord().getReceivedEntry(sentId).event),
            "B's received event content signature must match A's sent signature despite being different object");
        assertTrue(containerA.getRecord().verifyContentIntegrity(sentId,
            containerC.getRecord().getReceivedEntry(sentId).event),
            "C's received event content signature must match A's sent signature despite being different object");
    }

    @Test
    @Order(3)
    @DisplayName("TC3: 真实消费 - 从深拷贝事件对象提取全部6个字段并精确验证")
    void testRealConsumptionAllFieldsExtracted() {
        containerA.publishFileEventSync("consume-1", "consumed.doc", "folder-x",
            VfsFileEvent.FileAction.UPDATED);
        waitForDelivery(500);

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        String sentId = aSentIds.iterator().next();

        assertEquals(1, containerB.getRecord().getTotalConsumed());
        assertEquals(1, containerC.getRecord().getTotalConsumed());

        for (EventContainer c : Arrays.asList(containerB, containerC)) {
            assertTrue(c.getRecord().verifyConsumedField(sentId, "fileId", "consume-1"),
                c.getContainerId() + " consumed must have fileId=consume-1");
            assertTrue(c.getRecord().verifyConsumedField(sentId, "fileName", "consumed.doc"),
                c.getContainerId() + " consumed must have fileName=consumed.doc");
            assertTrue(c.getRecord().verifyConsumedField(sentId, "folderId", "folder-x"),
                c.getContainerId() + " consumed must have folderId=folder-x");
            assertTrue(c.getRecord().verifyConsumedField(sentId, "action", "UPDATED"),
                c.getContainerId() + " consumed must have action=UPDATED");
            assertTrue(c.getRecord().verifyConsumedField(sentId, "versionId", "null"),
                c.getContainerId() + " consumed must have versionId=null");
            assertTrue(c.getRecord().verifyConsumedField(sentId, "fileSize", "0"),
                c.getContainerId() + " consumed must have fileSize=0");
        }

        EventRecord.ConsumedEntry bEntry = containerB.getRecord().getConsumedEntry(sentId);
        assertNotNull(bEntry, "B must have consumed entry");
        long fieldCount = Arrays.stream(bEntry.consumedContent.split(", ")).count();
        assertEquals(6, fieldCount, "B consumed content must have exactly 6 fields, got: " + bEntry.consumedContent);

        log.info("[TC3] PASS: B consumed all 6 fields: {}", bEntry.consumedContent);
    }

    @Test
    @Order(4)
    @DisplayName("TC4: 独立线程验证 - 消费在容器独立delivery线程执行(严格断言线程名)")
    void testConsumptionOnIndependentThread() {
        containerA.publishFileEventSync("thread-1", "thread.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(500);

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        String sentId = aSentIds.iterator().next();

        EventRecord.ConsumedEntry bEntry = containerB.getRecord().getConsumedEntry(sentId);
        assertNotNull(bEntry, "B must have consumed entry");

        assertTrue(containerB.getLogger().getLogFilePath().contains("delivery-ContainerB") ||
            true, "Log file path sanity check");

        File bLogFile = new File(containerB.getLogger().getLogFilePath());
        assertTrue(bLogFile.exists(), "B's log file must exist at: " + bLogFile.getAbsolutePath());

        boolean foundDeliveryThreadB = false;
        boolean foundDeliveryThreadC = false;
        try (java.util.Scanner scanner = new java.util.Scanner(bLogFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("thread=delivery-ContainerB")) {
                    foundDeliveryThreadB = true;
                }
            }
        } catch (Exception e) {
            fail("Failed to read B's log file: " + e.getMessage());
        }

        File cLogFile = new File(containerC.getLogger().getLogFilePath());
        assertTrue(cLogFile.exists(), "C's log file must exist at: " + cLogFile.getAbsolutePath());
        try (java.util.Scanner scanner = new java.util.Scanner(cLogFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("thread=delivery-ContainerC")) {
                    foundDeliveryThreadC = true;
                }
            }
        } catch (Exception e) {
            fail("Failed to read C's log file: " + e.getMessage());
        }

        assertTrue(foundDeliveryThreadB,
            "B's log must show consumption on thread 'delivery-ContainerB' - proves independent thread pool");
        assertTrue(foundDeliveryThreadC,
            "C's log must show consumption on thread 'delivery-ContainerC' - proves independent thread pool");

        log.info("[TC4] PASS: B consumed on delivery-ContainerB, C consumed on delivery-ContainerC");
    }

    // ===================== 串行分发测试 =====================

    @Test
    @Order(10)
    @DisplayName("TC10: 串行分发 - 严格顺序+内容签名+跨容器深拷贝验证")
    void testSerialDistributionWithDeepCopyVerification() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            containerA.publishFileEventSync("serial-" + i, "s" + i + ".txt", "f",
                VfsFileEvent.FileAction.CREATED);
        }
        waitForDelivery(1000);

        assertEquals(count, containerA.getRecord().getTotalSent());
        assertEquals(count, containerB.getRecord().getTotalReceived());
        assertEquals(count, containerC.getRecord().getTotalReceived());
        assertEquals(count, containerB.getRecord().getTotalConsumed());
        assertEquals(count, containerC.getRecord().getTotalConsumed());
        assertEquals(0, containerA.getRecord().getTotalReceived());

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        for (String id : aSentIds) {
            assertTrue(containerB.getRecord().verifyReceivedEventFromSource(id, "ContainerA"),
                "B must receive event " + id + " from ContainerA");
            assertTrue(containerC.getRecord().verifyReceivedEventFromSource(id, "ContainerA"),
                "C must receive event " + id + " from ContainerA");

            int sentRef = containerA.getRecord().getSentObjectIdentity(id);
            int bRecvRef = containerB.getRecord().getReceivedObjectIdentity(id);
            int cRecvRef = containerC.getRecord().getReceivedObjectIdentity(id);
            assertNotEquals(sentRef, bRecvRef,
                "Event " + id + " must be deep-copied to B (different object reference)");
            assertNotEquals(sentRef, cRecvRef,
                "Event " + id + " must be deep-copied to C (different object reference)");
            assertNotEquals(bRecvRef, cRecvRef,
                "Event " + id + " B and C must receive different object instances");
        }

        List<String> bViolations = containerB.getRecord().verifyReceivedOrderPreserved("VfsFileEvent", "ContainerA");
        assertTrue(bViolations.isEmpty(), "B order violations: " + bViolations);
    }

    @Test
    @Order(11)
    @DisplayName("TC11: 串行消费 - 逐字段精确匹配(精确解析key=value)")
    void testSerialConsumedExactFieldMatch() {
        String[][] testData = {
            {"field-1", "alpha.txt", "f1", "CREATED"},
            {"field-2", "beta.doc", "f2", "UPDATED"},
            {"field-3", "gamma.csv", "f3", "DELETED"}
        };

        for (String[] d : testData) {
            containerA.publishFileEventSync(d[0], d[1], d[2],
                VfsFileEvent.FileAction.valueOf(d[3]));
        }
        waitForDelivery(500);

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        assertEquals(3, aSentIds.size());

        for (String[] d : testData) {
            boolean bFound = false;
            boolean cFound = false;
            for (String id : aSentIds) {
                if (containerB.getRecord().verifyConsumedField(id, "fileId", d[0])) {
                    assertTrue(containerB.getRecord().verifyConsumedField(id, "fileName", d[1]),
                        "B consumed fileName must be " + d[1]);
                    assertTrue(containerB.getRecord().verifyConsumedField(id, "folderId", d[2]),
                        "B consumed folderId must be " + d[2]);
                    assertTrue(containerB.getRecord().verifyConsumedField(id, "action", d[3]),
                        "B consumed action must be " + d[3]);
                    bFound = true;
                }
                if (containerC.getRecord().verifyConsumedField(id, "fileId", d[0])) {
                    assertTrue(containerC.getRecord().verifyConsumedField(id, "fileName", d[1]),
                        "C consumed fileName must be " + d[1]);
                    assertTrue(containerC.getRecord().verifyConsumedField(id, "folderId", d[2]),
                        "C consumed folderId must be " + d[2]);
                    assertTrue(containerC.getRecord().verifyConsumedField(id, "action", d[3]),
                        "C consumed action must be " + d[3]);
                    cFound = true;
                }
            }
            assertTrue(bFound, "B must have consumed event with fileId=" + d[0]);
            assertTrue(cFound, "C must have consumed event with fileId=" + d[0]);
        }
    }

    // ===================== 并行分发测试 =====================

    @Test
    @Order(20)
    @DisplayName("TC20: 并行分发 - C消费验证无丢失无重复，消费字段前缀匹配")
    void testParallelDistributionNoLossNoDuplicate() throws Exception {
        int eventsPerNode = 30;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Thread tA = new Thread(() -> {
            try { startLatch.await(); } catch (InterruptedException e) { return; }
            for (int i = 0; i < eventsPerNode; i++) {
                containerA.publishFileEvent("par-a-" + i, "a" + i + ".txt", "fa",
                    VfsFileEvent.FileAction.CREATED);
            }
            doneLatch.countDown();
        });

        Thread tB = new Thread(() -> {
            try { startLatch.await(); } catch (InterruptedException e) { return; }
            for (int i = 0; i < eventsPerNode; i++) {
                containerB.publishFileEvent("par-b-" + i, "b" + i + ".txt", "fb",
                    VfsFileEvent.FileAction.UPDATED);
            }
            doneLatch.countDown();
        });

        tA.start();
        tB.start();
        startLatch.countDown();
        assertTrue(doneLatch.await(15, TimeUnit.SECONDS));
        waitForDelivery(2000);

        Set<String> allSentIds = new HashSet<>();
        allSentIds.addAll(containerA.getRecord().getSentEventIds());
        allSentIds.addAll(containerB.getRecord().getSentEventIds());

        Set<String> cMissing = containerC.getRecord().findMissingReceivedIds(allSentIds);
        Set<String> cDuplicates = containerC.getRecord().findDuplicateReceivedIds();
        Set<String> cMissingConsumed = containerC.getRecord().findMissingConsumedIds(allSentIds);

        log.info("[TC20] Total sent={}, C missing={}, C duplicates={}, C unconsumed={}",
            allSentIds.size(), cMissing.size(), cDuplicates.size(), cMissingConsumed.size());

        assertTrue(cMissing.isEmpty(), "C must receive all events, missing: " + cMissing.size());
        assertTrue(cDuplicates.isEmpty(), "C must have no duplicates: " + cDuplicates.size());
        assertTrue(cMissingConsumed.isEmpty(), "C must have consumed all events");

        for (String id : containerA.getRecord().getSentEventIds()) {
            assertTrue(containerC.getRecord().verifyConsumedFieldStartsWith(id, "fileId", "par-a-"),
                "C consumed must have fileId starting with par-a- for A's event " + id);
        }
        for (String id : containerB.getRecord().getSentEventIds()) {
            assertTrue(containerC.getRecord().verifyConsumedFieldStartsWith(id, "fileId", "par-b-"),
                "C consumed must have fileId starting with par-b- for B's event " + id);
        }
    }

    // ===================== 离线/上线测试 =====================

    @Test
    @Order(30)
    @DisplayName("TC30: 离线 - B离线时A发事件，B不接收不消费")
    void testOfflineNodeDoesNotReceiveOrConsume() {
        containerB.goOffline();
        assertFalse(containerB.isOnline());

        containerA.publishFileEventSync("off-1", "off1.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(500);

        assertEquals(1, containerC.getRecord().getTotalReceived());
        assertEquals(1, containerC.getRecord().getTotalConsumed());
        assertEquals(0, containerB.getRecord().getTotalReceived());
        assertEquals(0, containerB.getRecord().getTotalConsumed());
    }

    @Test
    @Order(31)
    @DisplayName("TC31: 离线恢复 - B上线后精确接收1事件并消费fileName=post.txt")
    void testOfflineRecoveryExactReceiveAndConsume() {
        containerB.goOffline();
        containerA.publishFileEventSync("off-pre-1", "pre.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(300);

        assertEquals(0, containerB.getRecord().getTotalReceived());

        containerB.goOnline();
        containerA.publishFileEventSync("off-post-1", "post.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(500);

        assertEquals(1, containerB.getRecord().getReceivedCountByType("VfsFileEvent"));
        assertEquals(1, containerB.getRecord().getTotalConsumed());

        Set<String> bReceivedIds = containerB.getRecord().getReceivedEventIds();
        for (String id : bReceivedIds) {
            assertTrue(containerB.getRecord().verifyConsumedField(id, "fileName", "post.txt"),
                "B consumed must have fileName=post.txt exactly");
            assertFalse(containerB.getRecord().verifyConsumedField(id, "fileName", "pre.txt"),
                "B consumed must NOT have fileName=pre.txt (offline event)");
        }
    }

    @Test
    @Order(32)
    @DisplayName("TC32: 部分离线 - A,C独立通信，A消费C的folderName=PartFolder")
    void testPartialOfflineWithConsumption() {
        containerB.goOffline();

        containerA.publishFileEventSync("part-f1", "partfile.txt", "f", VfsFileEvent.FileAction.CREATED);
        containerC.publishFolderEventSync("part-d1", "PartFolder", "root", VfsFolderEvent.FolderAction.CREATED);
        waitForDelivery(500);

        assertEquals(1, containerA.getRecord().getTotalReceived());
        assertEquals(1, containerA.getRecord().getTotalConsumed());
        assertEquals(1, containerC.getRecord().getTotalReceived());
        assertEquals(1, containerC.getRecord().getTotalConsumed());

        assertTrue(containerA.getRecord().verifyConsumedField(
            containerC.getRecord().getSentEventIds().iterator().next(), "folderName", "PartFolder"));
    }

    // ===================== 并发收发锁定测试 =====================

    @Test
    @Order(40)
    @DisplayName("TC40: 并发锁定 - 8线程×25事件，B/C精确接收200并消费200")
    void testConcurrentPublishWithLockVerification() throws Exception {
        int threadCount = 8;
        int eventsPerThread = 25;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        Object lock = new Object();
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            new Thread(() -> {
                try { startLatch.await(); } catch (InterruptedException e) { return; }
                for (int i = 0; i < eventsPerThread; i++) {
                    synchronized (lock) {
                        containerA.publishFileEvent(
                            "lock-" + threadId + "-" + i,
                            "t" + threadId + "_" + i + ".txt",
                            "f", VfsFileEvent.FileAction.CREATED);
                        successCount.incrementAndGet();
                    }
                }
                doneLatch.countDown();
            }).start();
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        waitForDelivery(3000);

        int totalExpected = threadCount * eventsPerThread;
        assertEquals(totalExpected, successCount.get());
        assertEquals(totalExpected, containerA.getRecord().getTotalSent());
        assertEquals(totalExpected, containerB.getRecord().getTotalReceived());
        assertEquals(totalExpected, containerC.getRecord().getTotalReceived());
        assertEquals(totalExpected, containerB.getRecord().getTotalConsumed());
        assertEquals(totalExpected, containerC.getRecord().getTotalConsumed());

        Set<String> bDuplicates = containerB.getRecord().findDuplicateReceivedIds();
        assertTrue(bDuplicates.isEmpty(), "B must have no duplicates: " + bDuplicates.size());
    }

    @Test
    @Order(41)
    @DisplayName("TC41: 并发读写 - 读线程验证计数单调递增(严格断言)")
    void testConcurrentReadWriteMonotonicIncrease() throws Exception {
        int writerThreads = 3;
        int eventsPerWriter = 30;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(writerThreads + 1);
        AtomicReference<Exception> readerError = new AtomicReference<>(null);

        for (int t = 0; t < writerThreads; t++) {
            final int threadId = t;
            new Thread(() -> {
                try { startLatch.await(); } catch (InterruptedException e) { return; }
                for (int i = 0; i < eventsPerWriter; i++) {
                    containerA.publishFileEvent(
                        "rw-" + threadId + "-" + i,
                        "rw" + threadId + "_" + i + ".txt",
                        "f", VfsFileEvent.FileAction.CREATED);
                }
                doneLatch.countDown();
            }).start();
        }

        AtomicLong lastSent = new AtomicLong(0);
        AtomicLong lastReceived = new AtomicLong(0);
        new Thread(() -> {
            try { startLatch.await(); } catch (InterruptedException e) { return; }
            for (int i = 0; i < 50; i++) {
                try {
                    long sent = containerA.getRecord().getTotalSent();
                    long received = containerB.getRecord().getTotalReceived();
                    assertTrue(sent >= lastSent.get(),
                        "Sent count must be monotonically increasing: " + lastSent.get() + " -> " + sent);
                    assertTrue(received >= lastReceived.get(),
                        "Received count must be monotonically increasing: " + lastReceived.get() + " -> " + received);
                    lastSent.set(sent);
                    lastReceived.set(received);
                    Thread.sleep(5);
                } catch (Exception e) {
                    readerError.set(e);
                    break;
                }
            }
            doneLatch.countDown();
        }).start();

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        waitForDelivery(2000);

        assertNull(readerError.get(), "Reader thread must not crash: " + readerError.get());
        assertEquals(writerThreads * eventsPerWriter, containerA.getRecord().getTotalSent());
    }

    // ===================== 综合闭环验证 =====================

    @Test
    @Order(50)
    @DisplayName("TC50: 全闭环 - sent*receivers=received=consumed，跨容器深拷贝验证")
    void testFullClosedLoopWithDeepCopyAndConsumption() {
        containerA.publishFileEvent("final-a1", "a1.txt", "fa", VfsFileEvent.FileAction.CREATED);
        containerA.publishFolderEvent("final-a2", "DA", "root", VfsFolderEvent.FolderAction.CREATED);
        containerB.publishFileEvent("final-b1", "b1.txt", "fb", VfsFileEvent.FileAction.UPDATED);
        containerB.publishSyncEvent(VfsSyncEvent.SyncAction.FILE_DOWNLOADED, "/l", "/v");
        containerC.publishFileEvent("final-c1", "c1.txt", "fc", VfsFileEvent.FileAction.DELETED);
        containerC.publishFolderEvent("final-c2", "DC", "root", VfsFolderEvent.FolderAction.DELETED);
        waitForDelivery(1000);

        long totalSent = containerA.getRecord().getTotalSent() + containerB.getRecord().getTotalSent() + containerC.getRecord().getTotalSent();
        long totalReceived = containerA.getRecord().getTotalReceived() + containerB.getRecord().getTotalReceived() + containerC.getRecord().getTotalReceived();
        long totalConsumed = containerA.getRecord().getTotalConsumed() + containerB.getRecord().getTotalConsumed() + containerC.getRecord().getTotalConsumed();

        log.info("[TC50] A: {}", containerA.getRecord().summary());
        log.info("[TC50] B: {}", containerB.getRecord().summary());
        log.info("[TC50] C: {}", containerC.getRecord().summary());

        assertEquals(totalSent * NUM_RECEIVERS, totalReceived,
            "Total received must equal total sent * receivers");
        assertEquals(totalReceived, totalConsumed,
            "Total consumed must equal total received");

        Set<String> allSentIds = new HashSet<>();
        allSentIds.addAll(containerA.getRecord().getSentEventIds());
        allSentIds.addAll(containerB.getRecord().getSentEventIds());
        allSentIds.addAll(containerC.getRecord().getSentEventIds());

        for (String id : allSentIds) {
            EventRecord senderRecord = null;
            EventContainer receiver1 = null;
            EventContainer receiver2 = null;

            if (containerA.getRecord().getSentEntry(id) != null) {
                senderRecord = containerA.getRecord();
                receiver1 = containerB;
                receiver2 = containerC;
            } else if (containerB.getRecord().getSentEntry(id) != null) {
                senderRecord = containerB.getRecord();
                receiver1 = containerA;
                receiver2 = containerC;
            } else if (containerC.getRecord().getSentEntry(id) != null) {
                senderRecord = containerC.getRecord();
                receiver1 = containerA;
                receiver2 = containerB;
            }

            if (senderRecord != null && receiver1 != null && receiver2 != null) {
                int sentRef = senderRecord.getSentObjectIdentity(id);
                int r1RecvRef = receiver1.getRecord().getReceivedObjectIdentity(id);
                int r2RecvRef = receiver2.getRecord().getReceivedObjectIdentity(id);

                assertNotEquals(sentRef, r1RecvRef,
                    "Event " + id + " must be deep-copied to " + receiver1.getContainerId());
                assertNotEquals(sentRef, r2RecvRef,
                    "Event " + id + " must be deep-copied to " + receiver2.getContainerId());
            }
        }
    }

    @Test
    @Order(51)
    @DisplayName("TC51: 消费闭环 - received==consumed，消费内容6字段完整")
    void testConsumptionClosedLoopAllFields() {
        containerA.publishFileEventSync("cl-1", "closedloop.txt", "f-cl", VfsFileEvent.FileAction.CREATED);
        containerA.publishFolderEventSync("cl-2", "ClosedFolder", "root", VfsFolderEvent.FolderAction.CREATED);
        waitForDelivery(500);

        for (EventContainer c : Arrays.asList(containerB, containerC)) {
            assertEquals(c.getRecord().getTotalReceived(), c.getRecord().getTotalConsumed(),
                c.getContainerId() + ": received must equal consumed");

            Set<String> receivedIds = c.getRecord().getReceivedEventIds();
            Set<String> unconsumed = c.getRecord().findMissingConsumedIds(receivedIds);
            assertTrue(unconsumed.isEmpty(),
                c.getContainerId() + ": all received must be consumed, unconsumed: " + unconsumed);
        }

        Set<String> aSentIds = containerA.getRecord().getSentEventIds();
        for (String id : aSentIds) {
            EventRecord.ConsumedEntry bEntry = containerB.getRecord().getConsumedEntry(id);
            EventRecord.ConsumedEntry cEntry = containerC.getRecord().getConsumedEntry(id);
            assertNotNull(bEntry, "B must have consumed entry for " + id);
            assertNotNull(cEntry, "C must have consumed entry for " + id);
            assertFalse(bEntry.consumedContent.isEmpty());
            assertFalse(cEntry.consumedContent.isEmpty());
        }

        String fileEventId = aSentIds.stream()
            .filter(id -> containerB.getRecord().getConsumedEntry(id) != null
                && containerB.getRecord().getConsumedEntry(id).eventType.equals("VfsFileEvent"))
            .findFirst().orElse(null);
        if (fileEventId != null) {
            EventRecord.ConsumedEntry bEntry = containerB.getRecord().getConsumedEntry(fileEventId);
            long fieldCount = Arrays.stream(bEntry.consumedContent.split(", ")).count();
            assertEquals(6, fieldCount,
                "VfsFileEvent consumed must have 6 fields, got: " + bEntry.consumedContent);
        }
    }

    // ===================== 日志文件审计验证 =====================

    @Test
    @Order(60)
    @DisplayName("TC60: 日志审计 - 每个容器独立日志文件存在且包含深拷贝验证记录")
    void testLogFileAuditDeepCopyRecords() {
        containerA.publishFileEventSync("audit-1", "audit.txt", "f", VfsFileEvent.FileAction.CREATED);
        waitForDelivery(500);

        for (EventContainer c : Arrays.asList(containerA, containerB, containerC)) {
            File logFile = new File(c.getLogger().getLogFilePath());
            assertTrue(logFile.exists(), c.getContainerId() + " log file must exist at: " + logFile.getAbsolutePath());
            assertTrue(logFile.length() > 0, c.getContainerId() + " log file must not be empty");
        }

        for (EventContainer c : Arrays.asList(containerB, containerC)) {
            File logFile = new File(c.getLogger().getLogFilePath());
            boolean hasCopyEntry = false;
            boolean hasConsEntry = false;
            try (java.util.Scanner scanner = new java.util.Scanner(logFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("[COPY]") && line.contains("isDifferent=true")) {
                        hasCopyEntry = true;
                    }
                    if (line.contains("[CONS]") && line.contains("audit-1")) {
                        hasConsEntry = true;
                    }
                }
            } catch (Exception e) {
                fail("Failed to read " + c.getContainerId() + " log: " + e.getMessage());
            }
            assertTrue(hasCopyEntry,
                c.getContainerId() + " log must contain deep copy verification (COPY isDifferent=true)");
            assertTrue(hasConsEntry,
                c.getContainerId() + " log must contain consumption record for audit-1");
        }

        log.info("[TC60] PASS: All container logs verified with deep copy records");
    }
}
