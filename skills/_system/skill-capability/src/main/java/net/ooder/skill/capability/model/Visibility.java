package net.ooder.skill.capability.model;

public enum Visibility {
    PUBLIC("public", "公开"),
    PRIVATE("private", "私有"),
    PROTECTED("protected", "受保护");

    private final String code;
    private final String displayName;

    Visibility(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static Visibility fromCode(String code) {
        if (code == null) return PUBLIC;
        for (Visibility v : values()) {
            if (v.code.equalsIgnoreCase(code)) {
                return v;
            }
        }
        return PUBLIC;
    }
}
