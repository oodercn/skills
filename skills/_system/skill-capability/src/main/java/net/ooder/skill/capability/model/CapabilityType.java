package net.ooder.skill.capability.model;

public enum CapabilityType {
    SCENE("scene", "场景能力", "开箱即用的业务功能", "ri-apps-line"),
    SKILL("skill", "技能包", "底层技能包", "ri-puzzle-line"),
    TOOL("tool", "工具能力", "独立工具能力", "ri-tools-line"),
    DRIVER("driver", "驱动能力", "外部系统驱动", "ri-database-2-line"),
    LLM("llm", "LLM能力", "大语言模型能力", "ri-brain-line"),
    KNOWLEDGE("knowledge", "知识能力", "知识库能力", "ri-book-2-line"),
    VFS("vfs", "虚拟文件系统", "虚拟文件系统能力", "ri-folder-line");

    private final String code;
    private final String displayName;
    private final String description;
    private final String icon;

    CapabilityType(String code, String displayName, String description, String icon) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }

    public static CapabilityType fromCode(String code) {
        if (code == null) return null;
        for (CapabilityType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
