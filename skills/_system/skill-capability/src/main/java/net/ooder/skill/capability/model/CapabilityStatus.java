package net.ooder.skill.capability.model;

public enum CapabilityStatus {
    REGISTERED("registered", "已注册"),
    INSTALLED("installed", "已安装"),
    ENABLED("enabled", "已启用"),
    DISABLED("disabled", "已禁用"),
    ACTIVE("active", "已激活"),
    INACTIVE("inactive", "未激活"),
    ERROR("error", "错误");

    private final String code;
    private final String displayName;

    CapabilityStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static CapabilityStatus fromCode(String code) {
        if (code == null) return null;
        for (CapabilityStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
