package net.ooder.skill.capability.model;

public enum CapabilityCategory {
    COMMUNICATION("communication", "通信交互"),
    KNOWLEDGE("knowledge", "知识管理"),
    STORAGE("storage", "存储服务"),
    INTEGRATION("integration", "集成服务"),
    BUSINESS("business", "业务能力"),
    ANALYSIS("analysis", "分析能力");

    private final String code;
    private final String displayName;

    CapabilityCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static CapabilityCategory fromCode(String code) {
        if (code == null) return null;
        for (CapabilityCategory cat : values()) {
            if (cat.code.equalsIgnoreCase(code)) {
                return cat;
            }
        }
        return null;
    }
}
