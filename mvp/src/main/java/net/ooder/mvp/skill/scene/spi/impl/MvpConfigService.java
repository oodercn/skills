package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MvpConfigService implements ConfigService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpConfigService.class);
    
    private final Map<String, Object> configStore = new ConcurrentHashMap<>();
    
    private static final Map<String, Object> DEFAULT_CONFIGS;
    
    static {
        DEFAULT_CONFIGS = new HashMap<>();
        DEFAULT_CONFIGS.put("scene.maxParticipants", 100);
        DEFAULT_CONFIGS.put("scene.timeout", 300000);
        DEFAULT_CONFIGS.put("scene.retryCount", 3);
        DEFAULT_CONFIGS.put("llm.defaultProvider", "system");
        DEFAULT_CONFIGS.put("llm.defaultModel", "gpt-4");
        DEFAULT_CONFIGS.put("llm.timeout", 60000);
        DEFAULT_CONFIGS.put("notification.enabled", true);
    }
    
    public MvpConfigService() {
        configStore.putAll(DEFAULT_CONFIGS);
    }
    
    @Override
    public Object getConfig(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        
        return configStore.getOrDefault(key, DEFAULT_CONFIGS.get(key));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key, Class<T> type) {
        Object value = getConfig(key);
        
        if (value == null) {
            return null;
        }
        
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        
        if (value instanceof Map && type == Map.class) {
            return (T) value;
        }
        
        try {
            String strValue = String.valueOf(value);
            if (type == String.class) {
                return type.cast(strValue);
            } else if (type == Integer.class || type == int.class) {
                return type.cast(Integer.parseInt(strValue));
            } else if (type == Long.class || type == long.class) {
                return type.cast(Long.parseLong(strValue));
            } else if (type == Boolean.class || type == boolean.class) {
                return type.cast(Boolean.parseBoolean(strValue));
            }
        } catch (NumberFormatException e) {
            log.warn("Failed to convert config value for key {}: {}", key, e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Map<String, Object> getConfigs(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new HashMap<>(configStore);
        }
        
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : configStore.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    @Override
    public void setConfig(String key, Object value) {
        if (key == null || key.isEmpty()) {
            log.warn("Cannot set config: key is null or empty");
            return;
        }
        
        configStore.put(key, value);
        log.info("Set config: {} = {}", key, value);
    }
    
    @Override
    public void deleteConfig(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        
        configStore.remove(key);
        log.info("Deleted config: {}", key);
    }
    
    @Override
    public boolean hasConfig(String key) {
        return key != null && !key.isEmpty() && configStore.containsKey(key);
    }
    
    public void resetToDefaults() {
        configStore.clear();
        configStore.putAll(DEFAULT_CONFIGS);
        log.info("Reset configs to defaults");
    }
}
