package net.ooder.skill.capability;

public enum CapabilityCategory {
    DATA_ACCESS("data-access", "数据访问"),
    AUTHENTICATION("authentication", "认证授权"),
    COMMUNICATION("communication", "通信交互"),
    INTEGRATION("integration", "系统集成"),
    PROCESSING("processing", "数据处理"),
    STORAGE("storage", "存储管理"),
    ORCHESTRATION("orchestration", "编排调度"),
    MONITORING("monitoring", "监控运维");

    private final String value;
    private final String label;

    CapabilityCategory(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }
    
    public static CapabilityCategory fromValue(String value) {
        for (CapabilityCategory cat : values()) {
            if (cat.value.equals(value)) {
                return cat;
            }
        }
        return null;
    }
}
