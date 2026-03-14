package net.ooder.skill.capability.model;

public enum CapabilityType {
    SCENE("场景能力", "开箱即用的业务功能"),
    SKILL("技能包", "底层技能包"),
    TOOL("工具能力", "独立工具能力"),
    DRIVER("驱动能力", "外部系统驱动"),
    LLM("LLM能力", "大语言模型能力");

    private final String displayName;
    private final String description;

    CapabilityType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
