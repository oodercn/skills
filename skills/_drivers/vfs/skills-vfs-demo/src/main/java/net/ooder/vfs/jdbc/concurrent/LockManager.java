package net.ooder.vfs.jdbc.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class LockManager {
    
    private static final Logger log = LoggerFactory.getLogger(LockManager.class);
    
    private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    
    private static final ScheduledExecutorService cleanupExecutor = 
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "LockManager-Cleanup");
            thread.setDaemon(true);
            return thread;
        });
    
    private static volatile boolean initialized = false;
    
    static {
        init();
    }
    
    private LockManager() {
    }
    
    public static synchronized void init() {
        if (!initialized) {
            cleanupExecutor.scheduleAtFixedRate(LockManager::cleanup, 5, 5, TimeUnit.MINUTES);
            initialized = true;
            log.info("LockManager initialized");
        }
    }
    
    public static ReentrantLock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock(true));
    }
    
    public static void lock(String key) {
        ReentrantLock lock = getLock(key);
        lock.lock();
    }
    
    public static void unlock(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
    
    public static boolean tryLock(String key) {
        ReentrantLock lock = getLock(key);
        return lock.tryLock();
    }
    
    public static boolean tryLock(String key, long timeout, TimeUnit unit) {
        try {
            ReentrantLock lock = getLock(key);
            return lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public static <T> T executeWithLock(String key, Supplier<T> action) {
        ReentrantLock lock = getLock(key);
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
    
    public static void executeWithLock(String key, Runnable action) {
        ReentrantLock lock = getLock(key);
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
    
    public static <T> T executeWithTryLock(String key, Supplier<T> action) {
        ReentrantLock lock = getLock(key);
        if (lock.tryLock()) {
            try {
                return action.get();
            } finally {
                lock.unlock();
            }
        }
        return null;
    }
    
    public static <T> T executeWithTryLock(String key, long timeout, TimeUnit unit, 
                                           Supplier<T> action) throws InterruptedException {
        ReentrantLock lock = getLock(key);
        if (lock.tryLock(timeout, unit)) {
            try {
                return action.get();
            } finally {
                lock.unlock();
            }
        }
        return null;
    }
    
    public static int getLockCount() {
        return lockMap.size();
    }
    
    public static int getActiveLockCount() {
        return (int) lockMap.values().stream()
            .filter(ReentrantLock::isLocked)
            .count();
    }
    
    public static boolean isLocked(String key) {
        ReentrantLock lock = lockMap.get(key);
        return lock != null && lock.isLocked();
    }
    
    public static boolean isHeldByCurrentThread(String key) {
        ReentrantLock lock = lockMap.get(key);
        return lock != null && lock.isHeldByCurrentThread();
    }
    
    private static void cleanup() {
        try {
            int beforeSize = lockMap.size();
            int removed = 0;
            
            Iterator<Map.Entry<String, ReentrantLock>> it = lockMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ReentrantLock> entry = it.next();
                ReentrantLock lock = entry.getValue();
                
                if (!lock.isLocked() && lock.getQueueLength() == 0) {
                    if (lock.tryLock()) {
                        try {
                            it.remove();
                            removed++;
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }
            
            int afterSize = lockMap.size();
            
            if (removed > 0) {
                log.debug("LockManager cleanup: removed {} unused locks, {} locks remaining", removed, afterSize);
            }
        } catch (Exception e) {
            log.error("LockManager cleanup failed", e);
        }
    }
    
    public static void shutdown() {
        try {
            cleanupExecutor.shutdown();
            if (!cleanupExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
            lockMap.clear();
            initialized = false;
            log.info("LockManager shutdown completed");
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("LockManager shutdown interrupted", e);
        }
    }
    
    public static void clearAll() {
        lockMap.clear();
        log.info("All locks cleared");
    }
}
