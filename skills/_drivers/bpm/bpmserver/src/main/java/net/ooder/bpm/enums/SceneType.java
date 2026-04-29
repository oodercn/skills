package net.ooder.bpm.enums;

public enum SceneType {
    AUTO("AUTO", "自主场景"),
    TRIGGER("TRIGGER", "触发场景"),
    HYBRID("HYBRID", "混合场景");

    private final String code;
    private final String description;

    SceneType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SceneType fromCode(String code) {
        for (SceneType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TRIGGER;
    }
}
