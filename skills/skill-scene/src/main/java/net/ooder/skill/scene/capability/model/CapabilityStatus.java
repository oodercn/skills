package net.ooder.skill.scene.capability.model;

public enum CapabilityStatus {
    DEFINED("已定义", "能力已定义但未注册"),
    REGISTERED("已注册", "已注册到能力注册表"),
    PUBLISHED("已发布", "已对外发布"),
    ENABLED("已启用", "允许能力被调用"),
    DISABLED("已禁用", "暂停能力调用"),
    DEPRECATED("已废弃", "标记为废弃"),
    ARCHIVED("已归档", "归档历史记录");

    private final String name;
    private final String description;

    CapabilityStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
