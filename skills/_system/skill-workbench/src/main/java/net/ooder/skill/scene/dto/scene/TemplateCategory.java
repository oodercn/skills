package net.ooder.skill.scene.dto.scene;

public enum TemplateCategory {
    BUSINESS("business", "业务场景"),
    COLLABORATION("collaboration", "协作场景"),
    AUTOMATION("automation", "自动化场景"),
    INTEGRATION("integration", "集成场景"),
    CUSTOM("custom", "自定义场景");

    private final String code;
    private final String name;

    TemplateCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static TemplateCategory fromCode(String code) {
        for (TemplateCategory cat : values()) {
            if (cat.code.equals(code)) return cat;
        }
        return CUSTOM;
    }
}
