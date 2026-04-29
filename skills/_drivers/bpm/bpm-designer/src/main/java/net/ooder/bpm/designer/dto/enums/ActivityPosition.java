package net.ooder.bpm.designer.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityPosition {
    START("START", "起始活动"),
    NORMAL("NORMAL", "普通活动"),
    END("END", "结束活动");

    private final String code;
    private final String label;

    ActivityPosition(String code, String label) {
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
    public static ActivityPosition fromCode(String code) {
        if (code == null) return NORMAL;
        for (ActivityPosition position : values()) {
            if (position.code.equalsIgnoreCase(code)) {
                return position;
            }
        }
        return NORMAL;
    }
}
