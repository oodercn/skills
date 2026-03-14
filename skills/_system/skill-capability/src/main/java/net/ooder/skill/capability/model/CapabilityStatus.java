package net.ooder.skill.capability.model;

public enum CapabilityStatus {
    REGISTERED("已注册"),
    INSTALLED("已安装"),
    ACTIVE("已激活"),
    INACTIVE("未激活"),
    ERROR("错误");

    private final String displayName;

    CapabilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
