package net.ooder.skill.capability.model;

public enum CapabilityBindingStatus {
    ACTIVE("激活"),
    INACTIVE("未激活"),
    PENDING("待激活"),
    ERROR("错误");

    private final String displayName;

    CapabilityBindingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
