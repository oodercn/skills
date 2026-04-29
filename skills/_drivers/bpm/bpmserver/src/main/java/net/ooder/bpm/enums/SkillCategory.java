package net.ooder.bpm.enums;

public enum SkillCategory {
    LLM("LLM", "大模型推理"),
    FORM("FORM", "表单交互"),
    SERVICE("SERVICE", "服务调用"),
    WORKFLOW("WORKFLOW", "流程编排"),
    KNOWLEDGE("KNOWLEDGE", "知识管理"),
    DATA("DATA", "数据处理"),
    COMM("COMM", "通讯服务"),
    TOOL("TOOL", "工具服务");

    private final String code;
    private final String description;

    SkillCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SkillCategory fromCode(String code) {
        for (SkillCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return SERVICE;
    }
}
