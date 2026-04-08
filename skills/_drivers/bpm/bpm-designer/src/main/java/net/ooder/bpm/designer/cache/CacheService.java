package net.ooder.bpm.designer.cache;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheService {
    
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    private final CacheConfig config;
    
    public CacheService(CacheConfig config) {
        this.config = config;
        startCleanupThread();
    }
    
    public <T> Optional<T> get(String key, Class<T> type) {
        if (!config.isEnabled()) {
            return Optional.empty();
        }
        
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            log.debug("Cache miss for key: {}", key);
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            log.debug("Cache expired for key: {}", key);
            return Optional.empty();
        }
        
        try {
            T value = JSON.parseObject(entry.getValue(), type);
            log.debug("Cache hit for key: {}", key);
            return Optional.of(value);
        } catch (Exception e) {
            log.warn("Failed to deserialize cache value for key: {}", key, e);
            cache.remove(key);
            return Optional.empty();
        }
    }
    
    public void put(String key, Object value) {
        put(key, value, config.getDefaultTtl());
    }
    
    public void put(String key, Object value, Duration ttl) {
        if (!config.isEnabled()) {
            return;
        }
        
        try {
            String jsonValue = JSON.toJSONString(value);
            Instant expiresAt = Instant.now().plus(ttl);
            cache.put(key, new CacheEntry(jsonValue, expiresAt));
            log.debug("Cached value for key: {}, ttl: {}s", key, ttl.getSeconds());
        } catch (Exception e) {
            log.warn("Failed to cache value for key: {}", key, e);
        }
    }
    
    public void evict(String key) {
        cache.remove(key);
        log.debug("Evicted cache for key: {}", key);
    }
    
    public void evictByPrefix(String prefix) {
        cache.keySet().removeIf(key -> key.startsWith(prefix));
        log.debug("Evicted cache entries with prefix: {}", prefix);
    }
    
    public void clear() {
        cache.clear();
        log.info("Cache cleared");
    }
    
    public int size() {
        return cache.size();
    }
    
    public CacheStats getStats() {
        return new CacheStats(cache.size(), config.getMaxSize());
    }
    
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(config.getCleanupInterval().toMillis());
                    cleanup();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "cache-cleanup");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    private void cleanup() {
        int removed = 0;
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                cache.remove(entry.getKey());
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("Cleaned up {} expired cache entries", removed);
        }
    }
    
    private static class CacheEntry {
        private final String value;
        private final Instant expiresAt;
        
        CacheEntry(String value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
        
        String getValue() {
            return value;
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
