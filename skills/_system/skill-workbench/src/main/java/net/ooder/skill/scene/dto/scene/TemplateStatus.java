package net.ooder.skill.scene.dto.scene;

public enum TemplateStatus {
    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布"),
    DEPRECATED("DEPRECATED", "已废弃"),
    ARCHIVED("ARCHIVED", "已归档");

    private final String code;
    private final String name;

    TemplateStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static TemplateStatus fromCode(String code) {
        for (TemplateStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        return DRAFT;
    }
}
