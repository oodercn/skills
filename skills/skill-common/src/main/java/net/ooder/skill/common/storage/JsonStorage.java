package net.ooder.skill.common.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonStorage {
    
    private static final Logger log = LoggerFactory.getLogger(JsonStorage.class);
    
    private final ObjectMapper objectMapper;
    private final String basePath;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    public JsonStorage(String basePath) {
        this.basePath = basePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public <T> void save(String key, T data) {
        if (data == null) {
            return;
        }
        cache.put(key, data);
        
        File file = getFilePath(key);
        try {
            objectMapper.writeValue(file, data);
            log.debug("Saved data to {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save data to {}: {}", key, e.getMessage());
        }
    }
    
    public <T> T load(String key, Class<T> clazz) {
        Object cached = cache.get(key);
        if (cached != null && clazz.isInstance(cached)) {
            return clazz.cast(cached);
        }
        
        File file = getFilePath(key);
        if (!file.exists()) {
            return null;
        }
        
        try {
            T data = objectMapper.readValue(file, clazz);
            cache.put(key, data);
            return data;
        } catch (IOException e) {
            log.error("Failed to load data from {}: {}", key, e.getMessage());
            return null;
        }
    }
    
    public <T> List<T> loadList(String key, Class<T> elementClass) {
        Object cached = cache.get(key);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) cached;
            return list;
        }
        
        File file = getFilePath(key);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try {
            CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementClass);
            List<T> data = objectMapper.readValue(file, type);
            cache.put(key, data);
            return data;
        } catch (IOException e) {
            log.error("Failed to load list from {}: {}", key, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void delete(String key) {
        cache.remove(key);
        
        File file = getFilePath(key);
        if (file.exists()) {
            file.delete();
        }
    }
    
    public boolean exists(String key) {
        if (cache.containsKey(key)) {
            return true;
        }
        return getFilePath(key).exists();
    }
    
    public void clear() {
        cache.clear();
        File dir = new File(basePath);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    file.delete();
                }
            }
        }
    }
    
    private File getFilePath(String key) {
        return new File(basePath, key + ".json");
    }
    
    public String getBasePath() {
        return basePath;
    }
}
