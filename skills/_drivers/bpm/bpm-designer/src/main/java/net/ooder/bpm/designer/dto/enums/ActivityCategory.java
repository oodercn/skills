package net.ooder.bpm.designer.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityCategory {
    HUMAN("HUMAN", "人工活动"),
    AGENT("AGENT", "Agent活动"),
    SCENE("SCENE", "场景活动"),
    SYSTEM("SYSTEM", "系统活动");

    private final String code;
    private final String label;

    ActivityCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
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
