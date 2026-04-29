package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContextIsolationService {

    private static final Logger log = LoggerFactory.getLogger(ContextIsolationService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getContextIsolation(String activityDefId) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM BPM_CONTEXT_ISOLATION WHERE ACTIVITYDEF_ID = ?", activityDefId);
        if (results.isEmpty()) {
            return null;
        }
        return convertContextRow(results.get(0));
    }

    public Map<String, Object> createContextIsolation(Map<String, Object> configData) {
        String contextId = (String) configData.getOrDefault("contextId",
            "ctx-" + UUID.randomUUID().toString().substring(0, 8));
        String activityDefId = (String) configData.get("activityDefId");
        String nestingType = (String) configData.getOrDefault("nestingType", "SUBFLOW");
        String isolationLevel = (String) configData.getOrDefault("isolationLevel",
            getDefaultIsolationLevel(nestingType));

        String variableIsolation = toJson(configData.get("variableIsolation"));
        String dataIsolation = toJson(configData.get("dataIsolation"));
        String permissionIsolation = toJson(configData.get("permissionIsolation"));
        String ioMapping = toJson(configData.get("ioMapping"));
        String lifecycle = toJson(configData.get("lifecycle"));

        long now = System.currentTimeMillis();
        jdbcTemplate.update(
            "INSERT INTO BPM_CONTEXT_ISOLATION (CONTEXT_ID, ACTIVITYDEF_ID, NESTING_TYPE, ISOLATION_LEVEL, " +
            "VARIABLE_ISOLATION, DATA_ISOLATION, PERMISSION_ISOLATION, IO_MAPPING, LIFECYCLE, CREATED_TIME) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            contextId, activityDefId, nestingType, isolationLevel,
            variableIsolation, dataIsolation, permissionIsolation, ioMapping, lifecycle, now);

        return getContextIsolation(activityDefId);
    }

    public Map<String, Object> updateContextIsolation(Map<String, Object> configData) {
        String contextId = (String) configData.get("contextId");

        Map<String, Object> existing = getContextIsolationById(contextId);
        if (existing == null) {
            throw new RuntimeException("上下文隔离配置不存在: " + contextId);
        }

        String nestingType = (String) configData.getOrDefault("nestingType", existing.get("NESTING_TYPE"));
        String isolationLevel = (String) configData.getOrDefault("isolationLevel", existing.get("ISOLATION_LEVEL"));
        String variableIsolation = configData.containsKey("variableIsolation")
            ? toJson(configData.get("variableIsolation")) : (String) existing.get("VARIABLE_ISOLATION");
        String dataIsolation = configData.containsKey("dataIsolation")
            ? toJson(configData.get("dataIsolation")) : (String) existing.get("DATA_ISOLATION");
        String permissionIsolation = configData.containsKey("permissionIsolation")
            ? toJson(configData.get("permissionIsolation")) : (String) existing.get("PERMISSION_ISOLATION");
        String ioMapping = configData.containsKey("ioMapping")
            ? toJson(configData.get("ioMapping")) : (String) existing.get("IO_MAPPING");
        String lifecycle = configData.containsKey("lifecycle")
            ? toJson(configData.get("lifecycle")) : (String) existing.get("LIFECYCLE");

        jdbcTemplate.update(
            "UPDATE BPM_CONTEXT_ISOLATION SET NESTING_TYPE=?, ISOLATION_LEVEL=?, VARIABLE_ISOLATION=?, " +
            "DATA_ISOLATION=?, PERMISSION_ISOLATION=?, IO_MAPPING=?, LIFECYCLE=? WHERE CONTEXT_ID=?",
            nestingType, isolationLevel, variableIsolation, dataIsolation,
            permissionIsolation, ioMapping, lifecycle, contextId);

        return getContextIsolation((String) existing.get("ACTIVITYDEF_ID"));
    }

    public void deleteContextIsolation(String contextId) {
        jdbcTemplate.update("DELETE FROM BPM_CONTEXT_ISOLATION WHERE CONTEXT_ID = ?", contextId);
        log.info("Deleted context isolation: {}", contextId);
    }

    private String getDefaultIsolationLevel(String nestingType) {
        switch (nestingType) {
            case "SUBFLOW": return "PARTIAL";
            case "BLOCK_NORMAL": return "SHARED";
            case "SCENE": return "ISOLATED";
            case "EXTERNAL": return "ISOLATED";
            default: return "SHARED";
        }
    }

    private Map<String, Object> getContextIsolationById(String contextId) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM BPM_CONTEXT_ISOLATION WHERE CONTEXT_ID = ?", contextId);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    private Map<String, Object> convertContextRow(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contextId", row.get("CONTEXT_ID"));
        result.put("activityDefId", row.get("ACTIVITYDEF_ID"));
        result.put("nestingType", row.get("NESTING_TYPE"));
        result.put("isolationLevel", row.get("ISOLATION_LEVEL"));
        result.put("variableIsolation", fromJson((String) row.get("VARIABLE_ISOLATION")));
        result.put("dataIsolation", fromJson((String) row.get("DATA_ISOLATION")));
        result.put("permissionIsolation", fromJson((String) row.get("PERMISSION_ISOLATION")));
        result.put("ioMapping", fromJson((String) row.get("IO_MAPPING")));
        result.put("lifecycle", fromJson((String) row.get("LIFECYCLE")));
        result.put("createdTime", row.get("CREATED_TIME"));
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
