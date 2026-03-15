package net.ooder.skill.capability.model;

public enum DriverType {
    DATABASE("database", "数据库驱动"),
    FILE("file", "文件驱动"),
    API("api", "API驱动"),
    MESSAGE("message", "消息驱动");

    private final String code;
    private final String displayName;

    DriverType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static DriverType fromCode(String code) {
        if (code == null) return null;
        for (DriverType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
