package net.ooder.vfs.jdbc.concurrent;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LockManager测试")
class LockManagerTest {

    @BeforeEach
    void setUp() {
        LockManager.clearAll();
    }

    @AfterEach
    void tearDown() {
        LockManager.clearAll();
    }

    @Test
    @DisplayName("基本锁获取和释放测试")
    void testBasicLockAndUnlock() {
        String key = "test-key-1";
        
        LockManager.lock(key);
        assertTrue(LockManager.isLocked(key));
        assertTrue(LockManager.isHeldByCurrentThread(key));
        
        LockManager.unlock(key);
        assertFalse(LockManager.isHeldByCurrentThread(key));
    }

    @Test
    @DisplayName("executeWithLock测试")
    void testExecuteWithLock() {
        String key = "test-key-2";
        AtomicInteger counter = new AtomicInteger(0);
        
        String result = LockManager.executeWithLock(key, () -> {
            counter.incrementAndGet();
            assertTrue(LockManager.isLocked(key));
            return "success";
        });
        
        assertEquals("success", result);
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("tryLock测试")
    void testTryLock() {
        String key = "test-key-3";
        
        boolean acquired = LockManager.tryLock(key);
        assertTrue(acquired);
        assertTrue(LockManager.isLocked(key));
        
        LockManager.unlock(key);
    }

    @Test
    @DisplayName("tryLock超时测试")
    void testTryLockWithTimeout() throws InterruptedException {
        String key = "test-key-4";
        
        boolean acquired = LockManager.tryLock(key, 1, TimeUnit.SECONDS);
        assertTrue(acquired);
        
        LockManager.unlock(key);
    }

    @Test
    @DisplayName("并发锁竞争测试")
    void testConcurrentLockContention() throws InterruptedException {
        String key = "test-key-5";
        int threadCount = 10;
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    LockManager.executeWithLock(key, () -> {
                        int value = counter.incrementAndGet();
                        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        return null;
                    });
                } catch (Exception e) {
                    fail("Exception during execution: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertEquals(threadCount, counter.get());
        
        executor.shutdown();
    }

    @Test
    @DisplayName("不同key的锁不互相影响测试")
    void testDifferentKeysDoNotInterfere() throws InterruptedException {
        String key1 = "test-key-6";
        String key2 = "test-key-7";
        AtomicInteger counter = new AtomicInteger(0);
        
        Thread thread1 = new Thread(() -> {
            LockManager.executeWithLock(key1, () -> {
                counter.incrementAndGet();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
        });
        
        Thread thread2 = new Thread(() -> {
            LockManager.executeWithLock(key2, () -> {
                counter.incrementAndGet();
                return null;
            });
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join(1000);
        thread2.join(1000);
        
        assertEquals(2, counter.get());
    }

    @Test
    @DisplayName("锁计数测试")
    void testLockCount() {
        LockManager.clearAll();
        
        assertEquals(0, LockManager.getLockCount());
        
        LockManager.lock("key1");
        LockManager.lock("key2");
        LockManager.lock("key3");
        
        assertEquals(3, LockManager.getLockCount());
        
        LockManager.unlock("key1");
        LockManager.unlock("key2");
        
        assertEquals(3, LockManager.getLockCount());
    }

    @Test
    @DisplayName("活动锁计数测试")
    void testActiveLockCount() {
        LockManager.clearAll();
        
        assertEquals(0, LockManager.getActiveLockCount());
        
        LockManager.lock("key1");
        LockManager.lock("key2");
        
        assertEquals(2, LockManager.getActiveLockCount());
        
        LockManager.unlock("key1");
        
        assertEquals(1, LockManager.getActiveLockCount());
    }

    @Test
    @DisplayName("可重入锁测试")
    void testReentrantLock() {
        String key = "test-key-reentrant";
        
        LockManager.lock(key);
        assertTrue(LockManager.isHeldByCurrentThread(key));
        
        LockManager.lock(key);
        assertTrue(LockManager.isHeldByCurrentThread(key));
        
        LockManager.unlock(key);
        assertTrue(LockManager.isHeldByCurrentThread(key));
        
        LockManager.unlock(key);
        assertFalse(LockManager.isHeldByCurrentThread(key));
    }

    @Test
    @DisplayName("executeWithTryLock测试")
    void testExecuteWithTryLock() {
        String key = "test-key-trylock";
        
        Integer result = LockManager.executeWithTryLock(key, () -> 42);
        
        assertEquals(42, result);
    }

    @Test
    @DisplayName("清理所有锁测试")
    void testClearAll() {
        LockManager.lock("key1");
        LockManager.lock("key2");
        LockManager.lock("key3");
        
        assertTrue(LockManager.getLockCount() > 0);
        
        LockManager.clearAll();
        
        assertEquals(0, LockManager.getLockCount());
    }

    @Test
    @DisplayName("多线程并发性能测试")
    void testMultiThreadPerformance() throws InterruptedException {
        int threadCount = 100;
        int iterations = 1000;
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * iterations);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < iterations; j++) {
                final String key = "key-" + (j % 10);
                executor.submit(() -> {
                    try {
                        LockManager.executeWithLock(key, () -> {
                            counter.incrementAndGet();
                            return null;
                        });
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertEquals(threadCount * iterations, counter.get());
        
        System.out.println("Executed " + (threadCount * iterations) + " operations in " + duration + "ms");
        
        executor.shutdown();
    }
}
