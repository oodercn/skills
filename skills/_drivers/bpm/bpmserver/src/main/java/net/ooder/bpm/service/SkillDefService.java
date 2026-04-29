package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillDefService {

    private static final Logger log = LoggerFactory.getLogger(SkillDefService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> listSkills(String form, String category, String provider) {
        StringBuilder sql = new StringBuilder("SELECT * FROM BPM_SKILL_DEF WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (form != null && !form.isEmpty()) {
            sql.append(" AND FORM = ?");
            params.add(form);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND CATEGORY = ?");
            params.add(category);
        }
        if (provider != null && !provider.isEmpty()) {
            sql.append(" AND PROVIDER = ?");
            params.add(provider);
        }

        sql.append(" ORDER BY CREATED_TIME DESC");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public Map<String, Object> getSkill(String skillId) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM BPM_SKILL_DEF WHERE SKILL_ID = ?", skillId);
        if (results.isEmpty()) {
            return null;
        }
        return convertSkillRow(results.get(0));
    }

    public Map<String, Object> createSkill(Map<String, Object> skillData) {
        String skillId = (String) skillData.getOrDefault("skillId",
            "skill-" + UUID.randomUUID().toString().substring(0, 8));
        String name = (String) skillData.get("name");
        String description = (String) skillData.getOrDefault("description", "");
        String icon = (String) skillData.getOrDefault("icon", "");
        String form = (String) skillData.getOrDefault("form", "STANDALONE");
        String category = (String) skillData.getOrDefault("category", "SERVICE");
        String provider = (String) skillData.getOrDefault("provider", "SYSTEM");

        validateSkillClassification(form, category, provider);

        String categoryConfig = toJson(skillData.get("categoryConfig"));
        String providerConfig = toJson(skillData.get("providerConfig"));
        String executionConfig = toJson(skillData.get("executionConfig"));
        String inputSchema = toJson(skillData.get("inputSchema"));
        String outputSchema = toJson(skillData.get("outputSchema"));
        String dependencies = toJson(skillData.get("dependencies"));

        long now = System.currentTimeMillis();
        jdbcTemplate.update(
            "INSERT INTO BPM_SKILL_DEF (SKILL_ID, NAME, DESCRIPTION, ICON, VERSION, STATUS, FORM, CATEGORY, PROVIDER, " +
            "CATEGORY_CONFIG, PROVIDER_CONFIG, EXECUTION_CONFIG, INPUT_SCHEMA, OUTPUT_SCHEMA, DEPENDENCIES, " +
            "CREATED_BY, CREATED_TIME, MODIFIED_BY, MODIFIED_TIME) " +
            "VALUES (?, ?, ?, ?, '1.0', 'PUBLISHED', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            skillId, name, description, icon, form, category, provider,
            categoryConfig, providerConfig, executionConfig, inputSchema, outputSchema, dependencies,
            "system", now, "system", now);

        return getSkill(skillId);
    }

    public Map<String, Object> updateSkill(Map<String, Object> skillData) {
        String skillId = (String) skillData.get("skillId");

        Map<String, Object> existing = getSkill(skillId);
        if (existing == null) {
            throw new RuntimeException("Skill不存在: " + skillId);
        }

        String name = (String) skillData.getOrDefault("name", existing.get("NAME"));
        String description = (String) skillData.getOrDefault("description", existing.get("DESCRIPTION"));
        String icon = (String) skillData.getOrDefault("icon", existing.get("ICON"));
        String form = (String) skillData.getOrDefault("form", existing.get("FORM"));
        String category = (String) skillData.getOrDefault("category", existing.get("CATEGORY"));
        String provider = (String) skillData.getOrDefault("provider", existing.get("PROVIDER"));

        validateSkillClassification(form, category, provider);

        String categoryConfig = skillData.containsKey("categoryConfig")
            ? toJson(skillData.get("categoryConfig")) : (String) existing.get("CATEGORY_CONFIG");
        String providerConfig = skillData.containsKey("providerConfig")
            ? toJson(skillData.get("providerConfig")) : (String) existing.get("PROVIDER_CONFIG");
        String executionConfig = skillData.containsKey("executionConfig")
            ? toJson(skillData.get("executionConfig")) : (String) existing.get("EXECUTION_CONFIG");
        String inputSchema = skillData.containsKey("inputSchema")
            ? toJson(skillData.get("inputSchema")) : (String) existing.get("INPUT_SCHEMA");
        String outputSchema = skillData.containsKey("outputSchema")
            ? toJson(skillData.get("outputSchema")) : (String) existing.get("OUTPUT_SCHEMA");
        String dependencies = skillData.containsKey("dependencies")
            ? toJson(skillData.get("dependencies")) : (String) existing.get("DEPENDENCIES");

        long now = System.currentTimeMillis();
        jdbcTemplate.update(
            "UPDATE BPM_SKILL_DEF SET NAME=?, DESCRIPTION=?, ICON=?, FORM=?, CATEGORY=?, PROVIDER=?, " +
            "CATEGORY_CONFIG=?, PROVIDER_CONFIG=?, EXECUTION_CONFIG=?, INPUT_SCHEMA=?, OUTPUT_SCHEMA=?, " +
            "DEPENDENCIES=?, MODIFIED_BY=?, MODIFIED_TIME=? WHERE SKILL_ID=?",
            name, description, icon, form, category, provider,
            categoryConfig, providerConfig, executionConfig, inputSchema, outputSchema,
            dependencies, "system", now, skillId);

        return getSkill(skillId);
    }

    public void deleteSkill(String skillId) {
        Map<String, Object> existing = getSkill(skillId);
        if (existing == null) {
            throw new RuntimeException("Skill不存在: " + skillId);
        }

        String provider = (String) existing.get("PROVIDER");
        if ("SYSTEM".equals(provider)) {
            throw new RuntimeException("系统内置Skill不可删除: " + skillId);
        }

        jdbcTemplate.update("DELETE FROM BPM_SKILL_DEF WHERE SKILL_ID = ?", skillId);
        log.info("Deleted skill: {}", skillId);
    }

    public Map<String, Object> getSkillEnums() {
        Map<String, Object> enums = new LinkedHashMap<>();

        List<Map<String, String>> formList = new ArrayList<>();
        for (SkillForm f : SkillForm.values()) {
            formList.add(Map.of("code", f.getCode(), "description", f.getDescription()));
        }
        enums.put("form", formList);

        List<Map<String, String>> categoryList = new ArrayList<>();
        for (SkillCategory c : SkillCategory.values()) {
            categoryList.add(Map.of("code", c.getCode(), "description", c.getDescription()));
        }
        enums.put("category", categoryList);

        List<Map<String, String>> providerList = new ArrayList<>();
        for (SkillProvider p : SkillProvider.values()) {
            providerList.add(Map.of("code", p.getCode(), "description", p.getDescription()));
        }
        enums.put("provider", providerList);

        List<Map<String, String>> performerList = new ArrayList<>();
        for (PerformerType p : PerformerType.values()) {
            performerList.add(Map.of("code", p.getCode(), "description", p.getDescription()));
        }
        enums.put("performerType", performerList);

        List<Map<String, String>> isolationList = new ArrayList<>();
        for (IsolationLevel l : IsolationLevel.values()) {
            isolationList.add(Map.of("code", l.getCode(), "description", l.getDescription()));
        }
        enums.put("isolationLevel", isolationList);

        List<Map<String, String>> sceneTypeList = new ArrayList<>();
        for (SceneType s : SceneType.values()) {
            sceneTypeList.add(Map.of("code", s.getCode(), "description", s.getDescription()));
        }
        enums.put("sceneType", sceneTypeList);

        return enums;
    }

    private void validateSkillClassification(String form, String category, String provider) {
        if ("SCENE".equals(form) && !"WORKFLOW".equals(category)) {
            throw new RuntimeException("SCENE形态必须绑定WORKFLOW类别");
        }
    }

    private Map<String, Object> convertSkillRow(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("skillId", row.get("SKILL_ID"));
        result.put("name", row.get("NAME"));
        result.put("description", row.get("DESCRIPTION"));
        result.put("icon", row.get("ICON"));
        result.put("version", row.get("VERSION"));
        result.put("status", row.get("STATUS"));
        result.put("form", row.get("FORM"));
        result.put("category", row.get("CATEGORY"));
        result.put("provider", row.get("PROVIDER"));
        result.put("categoryConfig", fromJson((String) row.get("CATEGORY_CONFIG")));
        result.put("providerConfig", fromJson((String) row.get("PROVIDER_CONFIG")));
        result.put("executionConfig", fromJson((String) row.get("EXECUTION_CONFIG")));
        result.put("inputSchema", fromJson((String) row.get("INPUT_SCHEMA")));
        result.put("outputSchema", fromJson((String) row.get("OUTPUT_SCHEMA")));
        result.put("dependencies", fromJson((String) row.get("DEPENDENCIES")));
        result.put("createdBy", row.get("CREATED_BY"));
        result.put("createdTime", row.get("CREATED_TIME"));
        result.put("modifiedBy", row.get("MODIFIED_BY"));
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
