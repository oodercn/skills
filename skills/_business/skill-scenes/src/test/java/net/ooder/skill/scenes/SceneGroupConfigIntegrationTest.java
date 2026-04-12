package net.ooder.skill.scenes;

import net.ooder.scene.group.SqlSceneGroupConfigManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class SceneGroupConfigIntegrationTest {

    @Autowired
    private SqlSceneGroupConfigManager configManager;

    @Test
    public void testConfigManagerInjection() {
        assertNotNull(configManager, "SqlSceneGroupConfigManager should be injected");
    }

    @Test
    public void testSetConfig() {
        String sceneGroupId = "test-scene-group-config-1";
        String key = "test.config.key";
        String value = "test-value";
        
        configManager.setConfig(sceneGroupId, key, value);
        
        Object retrieved = configManager.getConfig(sceneGroupId, key);
        assertNotNull(retrieved, "Config should be set");
        assertEquals(value, retrieved.toString(), "Config value should match");
    }

    @Test
    public void testGetConfig() {
        String sceneGroupId = "test-scene-group-config-2";
        String key = "test.config.get";
        String value = "get-test-value";
        
        configManager.setConfig(sceneGroupId, key, value);
        
        Object retrieved = configManager.getConfig(sceneGroupId, key);
        assertNotNull(retrieved, "Config should be retrieved");
        assertEquals(value, retrieved.toString(), "Config value should match");
    }

    @Test
    public void testGetAllConfig() {
        String sceneGroupId = "test-scene-group-config-3";
        
        configManager.setConfig(sceneGroupId, "key1", "value1");
        configManager.setConfig(sceneGroupId, "key2", "value2");
        configManager.setConfig(sceneGroupId, "key3", "value3");
        
        Map<String, Object> allConfig = configManager.getAllConfig(sceneGroupId);
        
        assertNotNull(allConfig, "All config should not be null");
        assertTrue(allConfig.size() >= 3, "Should have at least 3 configs");
    }

    @Test
    public void testSetConfigBatch() {
        String sceneGroupId = "test-scene-group-config-4";
        
        Map<String, Object> config = new HashMap<>();
        config.put("batch.key1", "batch-value1");
        config.put("batch.key2", "batch-value2");
        config.put("batch.key3", "batch-value3");
        
        configManager.setConfigBatch(sceneGroupId, config);
        
        Map<String, Object> retrieved = configManager.getAllConfig(sceneGroupId);
        assertTrue(retrieved.containsKey("batch.key1"), "Should contain batch.key1");
        assertTrue(retrieved.containsKey("batch.key2"), "Should contain batch.key2");
        assertTrue(retrieved.containsKey("batch.key3"), "Should contain batch.key3");
    }

    @Test
    public void testDeleteConfig() {
        String sceneGroupId = "test-scene-group-config-5";
        String key = "test.config.delete";
        String value = "delete-test-value";
        
        configManager.setConfig(sceneGroupId, key, value);
        
        Object retrieved = configManager.getConfig(sceneGroupId, key);
        assertNotNull(retrieved, "Config should be set");
        
        configManager.deleteConfig(sceneGroupId, key);
        
        Object deleted = configManager.getConfig(sceneGroupId, key);
        assertNull(deleted, "Config should be deleted");
    }

    @Test
    public void testConfigTypes() {
        String sceneGroupId = "test-scene-group-config-6";
        
        configManager.setConfig(sceneGroupId, "string.config", "string-value");
        configManager.setConfig(sceneGroupId, "int.config", 12345);
        configManager.setConfig(sceneGroupId, "boolean.config", true);
        
        Object stringVal = configManager.getConfig(sceneGroupId, "string.config");
        Object intVal = configManager.getConfig(sceneGroupId, "int.config");
        Object boolVal = configManager.getConfig(sceneGroupId, "boolean.config");
        
        assertNotNull(stringVal, "String config should not be null");
        assertNotNull(intVal, "Int config should not be null");
        assertNotNull(boolVal, "Boolean config should not be null");
    }
}
