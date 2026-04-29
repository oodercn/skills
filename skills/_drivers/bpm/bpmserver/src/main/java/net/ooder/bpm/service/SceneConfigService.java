package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SceneConfigService {

    private static final Logger log = LoggerFactory.getLogger(SceneConfigService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getSceneConfig(String processDefId) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM BPM_SCENE_CONFIG WHERE PROCESSDEF_ID = ?", processDefId);
        if (results.isEmpty()) {
            return null;
        }
        return convertSceneConfigRow(results.get(0));
    }

    public Map<String, Object> createSceneConfig(Map<String, Object> configData) {
        String sceneConfigId = (String) configData.getOrDefault("sceneConfigId",
            "scene-cfg-" + UUID.randomUUID().toString().substring(0, 8));
        String processDefId = (String) configData.get("processDefId");
        String sceneType = (String) configData.getOrDefault("sceneType", "TRIGGER");

        String lifecycleConfig = toJson(configData.get("lifecycleConfig"));
        String roles = toJson(configData.get("roles"));
        String menus = toJson(configData.get("menus"));
        String capabilities = toJson(configData.get("capabilities"));
        String activationSteps = toJson(configData.get("activationSteps"));
        String driverConditions = toJson(configData.get("driverConditions"));
        String knowledgeConfig = toJson(configData.get("knowledgeConfig"));

        long now = System.currentTimeMillis();
        jdbcTemplate.update(
            "INSERT INTO BPM_SCENE_CONFIG (SCENE_CONFIG_ID, PROCESSDEF_ID, SCENE_TYPE, " +
            "LIFECYCLE_CONFIG, ROLES, MENUS, CAPABILITIES, ACTIVATION_STEPS, DRIVER_CONDITIONS, " +
            "KNOWLEDGE_CONFIG, CREATED_TIME, MODIFIED_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            sceneConfigId, processDefId, sceneType,
            lifecycleConfig, roles, menus, capabilities, activationSteps, driverConditions,
            knowledgeConfig, now, now);

        return getSceneConfig(processDefId);
    }

    public Map<String, Object> updateSceneConfig(Map<String, Object> configData) {
        String sceneConfigId = (String) configData.get("sceneConfigId");

        Map<String, Object> existing = getSceneConfigById(sceneConfigId);
        if (existing == null) {
            throw new RuntimeException("场景配置不存在: " + sceneConfigId);
        }

        String sceneType = (String) configData.getOrDefault("sceneType", existing.get("SCENE_TYPE"));
        String lifecycleConfig = configData.containsKey("lifecycleConfig")
            ? toJson(configData.get("lifecycleConfig")) : (String) existing.get("LIFECYCLE_CONFIG");
        String roles = configData.containsKey("roles")
            ? toJson(configData.get("roles")) : (String) existing.get("ROLES");
        String menus = configData.containsKey("menus")
            ? toJson(configData.get("menus")) : (String) existing.get("MENUS");
        String capabilities = configData.containsKey("capabilities")
            ? toJson(configData.get("capabilities")) : (String) existing.get("CAPABILITIES");
        String activationSteps = configData.containsKey("activationSteps")
            ? toJson(configData.get("activationSteps")) : (String) existing.get("ACTIVATION_STEPS");
        String driverConditions = configData.containsKey("driverConditions")
            ? toJson(configData.get("driverConditions")) : (String) existing.get("DRIVER_CONDITIONS");
        String knowledgeConfig = configData.containsKey("knowledgeConfig")
            ? toJson(configData.get("knowledgeConfig")) : (String) existing.get("KNOWLEDGE_CONFIG");

        long now = System.currentTimeMillis();
        jdbcTemplate.update(
            "UPDATE BPM_SCENE_CONFIG SET SCENE_TYPE=?, LIFECYCLE_CONFIG=?, ROLES=?, MENUS=?, " +
            "CAPABILITIES=?, ACTIVATION_STEPS=?, DRIVER_CONDITIONS=?, KNOWLEDGE_CONFIG=?, MODIFIED_TIME=? " +
            "WHERE SCENE_CONFIG_ID=?",
            sceneType, lifecycleConfig, roles, menus, capabilities, activationSteps,
            driverConditions, knowledgeConfig, now, sceneConfigId);

        return getSceneConfig((String) existing.get("PROCESSDEF_ID"));
    }

    public void deleteSceneConfig(String sceneConfigId) {
        jdbcTemplate.update("DELETE FROM BPM_SCENE_CONFIG WHERE SCENE_CONFIG_ID = ?", sceneConfigId);
        log.info("Deleted scene config: {}", sceneConfigId);
    }

    private Map<String, Object> getSceneConfigById(String sceneConfigId) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM BPM_SCENE_CONFIG WHERE SCENE_CONFIG_ID = ?", sceneConfigId);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    private Map<String, Object> convertSceneConfigRow(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sceneConfigId", row.get("SCENE_CONFIG_ID"));
        result.put("processDefId", row.get("PROCESSDEF_ID"));
        result.put("sceneType", row.get("SCENE_TYPE"));
        result.put("lifecycleConfig", fromJson((String) row.get("LIFECYCLE_CONFIG")));
        result.put("roles", fromJson((String) row.get("ROLES")));
        result.put("menus", fromJson((String) row.get("MENUS")));
        result.put("capabilities", fromJson((String) row.get("CAPABILITIES")));
        result.put("activationSteps", fromJson((String) row.get("ACTIVATION_STEPS")));
        result.put("driverConditions", fromJson((String) row.get("DRIVER_CONDITIONS")));
        result.put("knowledgeConfig", fromJson((String) row.get("KNOWLEDGE_CONFIG")));
        result.put("createdTime", row.get("CREATED_TIME"));
        result.put("modifiedTime", row.get("MODIFIED_TIME"));
        return result;
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private Object fromJson(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return mapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }
}
