package net.ooder.skill.capability.model;

public enum AccessLevel {
    PLATFORM("platform", "平台级"),
    SCENE("scene", "场景级"),
    AGENT("agent", "智能体级");

    private final String code;
    private final String displayName;

    AccessLevel(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static AccessLevel fromCode(String code) {
        if (code == null) return null;
        for (AccessLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        return null;
    }
}
