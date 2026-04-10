package net.ooder.bpm.designer.dto.enums;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 活动类别枚举
 */
public enum ActivityCategory {
    @JSONField(name = "HUMAN")
    HUMAN("HUMAN", "人工活动"),

    @JSONField(name = "AGENT")
    AGENT("AGENT", "Agent活动"),

    @JSONField(name = "SCENE")
    SCENE("SCENE", "场景活动"),

    @JSONField(name = "SYSTEM")
    SYSTEM("SYSTEM", "系统活动");

    private final String code;
    private final String label;

    ActivityCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ActivityCategory fromCode(String code) {
        if (code == null) return HUMAN;
        for (ActivityCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        return HUMAN;
    }
}
