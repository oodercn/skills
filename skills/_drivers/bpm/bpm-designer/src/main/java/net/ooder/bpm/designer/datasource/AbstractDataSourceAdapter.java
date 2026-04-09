package net.ooder.bpm.designer.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataSourceAdapter implements DataSourceAdapter {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    protected final long cacheTtlMs;
    
    protected AbstractDataSourceAdapter(long cacheTtlMs) {
        this.cacheTtlMs = cacheTtlMs;
    }
    
    protected AbstractDataSourceAdapter() {
        this(300000);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T getFromCache(String key) {
        Object cached = cache.get(key);
        if (cached instanceof CacheEntry) {
            CacheEntry entry = (CacheEntry) cached;
            if (System.currentTimeMillis() < entry.expireTime) {
                return (T) entry.value;
            } else {
                cache.remove(key);
            }
        }
        return null;
    }
    
    protected void putToCache(String key, Object value) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + cacheTtlMs));
    }
    
    protected String buildCacheKey(String tenantId, String method, String... params) {
        StringBuilder sb = new StringBuilder(tenantId).append(":").append(method);
        for (String param : params) {
            sb.append(":").append(param != null ? param : "null");
        }
        return sb.toString();
    }
    
    protected List<Map<String, Object>> filterByKeyword(List<Map<String, Object>> list, String keyword, String... fields) {
        if (keyword == null || keyword.isEmpty()) {
            return list;
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : list) {
            for (String field : fields) {
                Object value = item.get(field);
                if (value != null && value.toString().toLowerCase().contains(lowerKeyword)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }
    
    protected Map<String, Object> findById(List<Map<String, Object>> list, String idField, String id) {
        for (Map<String, Object> item : list) {
            if (id.equals(item.get(idField))) {
                return item;
            }
        }
        return null;
    }
    
    protected List<Map<String, Object>> findByField(List<Map<String, Object>> list, String field, Object value) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : list) {
            if (Objects.equals(item.get(field), value)) {
                result.add(item);
            }
        }
        return result;
    }
    
    private static class CacheEntry {
        final Object value;
        final long expireTime;
        
        CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
    }
}
