package net.ooder.bpm.enums;

public enum IsolationLevel {
    SHARED("SHARED", "共享父上下文"),
    PARTIAL("PARTIAL", "部分隔离"),
    ISOLATED("ISOLATED", "完全隔离");

    private final String code;
    private final String description;

    IsolationLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static IsolationLevel fromCode(String code) {
        for (IsolationLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return SHARED;
    }
}
