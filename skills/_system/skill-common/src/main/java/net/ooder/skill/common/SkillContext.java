package net.ooder.skill.common;

import lombok.Data;

import java.util.Map;

/**
 * Skill涓婁笅鏂? * 涓存椂鏇夸唬agent-sdk鐨凷killContext绫伙紝鐩村埌SDK鍙戝竷
 */
@Data
public class SkillContext {

    private String skillId;
    private String skillName;
    private String skillVersion;
    private Map<String, Object> config;
    private Map<String, Object> attributes;

    public Object getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }

    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new java.util.HashMap<>();
        }
        attributes.put(key, value);
    }

    public Object getConfig(String key) {
        return config != null ? config.get(key) : null;
    }
}
