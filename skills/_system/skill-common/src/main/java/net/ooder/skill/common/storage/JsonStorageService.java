package net.ooder.skill.common.storage;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonStorageService {

    private static final Logger log = LoggerFactory.getLogger(JsonStorageService.class);

    private final String storagePath;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public JsonStorageService(String storagePath) {
        this.storagePath = storagePath;
    }

    public JsonStorageService() {
        this.storagePath = "./data";
    }

    @PostConstruct
    public void init() {
        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
            log.info("Created storage directory: {}", storagePath);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String collection, String id) {
        Map<String, Object> data = (Map<String, Object>) cache.get(collection);
        if (data != null) {
            return (T) data.get(id);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> void put(String collection, String id, T entity) {
        Map<String, Object> data = (Map<String, Object>) cache.computeIfAbsent(collection, k -> new HashMap<>());
        data.put(id, entity);
        saveToFile(collection, data);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean remove(String collection, String id) {
        Map<String, Object> data = (Map<String, Object>) cache.get(collection);
        if (data != null && data.remove(id) != null) {
            saveToFile(collection, data);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(String collection) {
        Map<String, Object> data = (Map<String, Object>) cache.get(collection);
        if (data != null) {
            return new ArrayList<>((Collection<T>) data.values());
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String collection) {
        return (Map<String, T>) cache.getOrDefault(collection, new HashMap<>());
    }

    private void saveToFile(String collection, Map<String, Object> data) {
        try {
            File file = new File(storagePath, collection + ".json");
            String json = JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
            Files.write(file.toPath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            log.debug("Saved {} to {}", collection, file.getName());
        } catch (IOException e) {
            log.error("Failed to save collection: {}", collection, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String collection) {
        File file = new File(storagePath, collection + ".json");
        if (file.exists()) {
            try {
                String json = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                Map<String, Object> data = JSON.parseObject(json, Map.class, JSONReader.Feature.FieldBased);
                cache.put(collection, data);
                log.info("Loaded {} records from {}", data.size(), collection);
            } catch (IOException e) {
                log.warn("Failed to load collection: {}", collection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadList(String collection, Class<T> clazz) {
        File file = new File(storagePath, collection + ".json");
        if (file.exists()) {
            try {
                String json = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                return JSON.parseArray(json, clazz, JSONReader.Feature.FieldBased);
            } catch (IOException e) {
                log.warn("Failed to load list: {}", collection);
            }
        }
        return new ArrayList<>();
    }

    public <T> void saveList(String collection, List<T> list) {
        try {
            File file = new File(storagePath, collection + ".json");
            String json = JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
            Files.write(file.toPath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            log.debug("Saved {} items to {}", list.size(), collection);
        } catch (IOException e) {
            log.error("Failed to save list: {}", collection, e);
        }
    }
}
