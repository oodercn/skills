package net.ooder.mvp.skill.scene.spi.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.mvp.skill.scene.spi.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MvpStorageService implements StorageService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpStorageService.class);
    
    private final Map<String, Map<String, Object>> sceneDataStore = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void save(String sceneId, String key, Object value) {
        if (sceneId == null || key == null) {
            log.warn("Cannot save: sceneId or key is null");
            return;
        }
        
        Map<String, Object> sceneData = sceneDataStore.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
        sceneData.put(key, value);
        log.debug("Saved data for scene {}, key: {}", sceneId, key);
    }
    
    @Override
    public Object get(String sceneId, String key) {
        if (sceneId == null || key == null) {
            return null;
        }
        
        Map<String, Object> sceneData = sceneDataStore.get(sceneId);
        if (sceneData == null) {
            return null;
        }
        
        return sceneData.get(key);
    }
    
    @Override
    public <T> T get(String sceneId, String key, Class<T> type) {
        Object value = get(sceneId, key);
        if (value == null) {
            return null;
        }
        
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        
        if (value instanceof String) {
            try {
                return objectMapper.readValue((String) value, type);
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize value for scene {}, key {}: {}", sceneId, key, e.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public void delete(String sceneId, String key) {
        if (sceneId == null || key == null) {
            return;
        }
        
        Map<String, Object> sceneData = sceneDataStore.get(sceneId);
        if (sceneData != null) {
            sceneData.remove(key);
            log.debug("Deleted data for scene {}, key: {}", sceneId, key);
        }
    }
    
    @Override
    public Map<String, Object> query(String sceneId, String prefix) {
        if (sceneId == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> sceneData = sceneDataStore.get(sceneId);
        
        if (sceneData == null) {
            return result;
        }
        
        if (prefix == null || prefix.isEmpty()) {
            return new HashMap<>(sceneData);
        }
        
        for (Map.Entry<String, Object> entry : sceneData.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    @Override
    public void batchSave(String sceneId, Map<String, Object> data) {
        if (sceneId == null || data == null || data.isEmpty()) {
            return;
        }
        
        Map<String, Object> sceneData = sceneDataStore.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
        sceneData.putAll(data);
        log.debug("Batch saved {} items for scene {}", data.size(), sceneId);
    }
    
    public void clearSceneData(String sceneId) {
        if (sceneId != null) {
            sceneDataStore.remove(sceneId);
            log.info("Cleared all data for scene {}", sceneId);
        }
    }
    
    public Map<String, Object> getAllSceneData(String sceneId) {
        return sceneDataStore.getOrDefault(sceneId, Collections.emptyMap());
    }
}
