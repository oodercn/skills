package net.ooder.skill.capability.model;

public enum SkillForm {
    PROVIDER("provider", "提供者"),
    CONSUMER("consumer", "消费者"),
    BOTH("both", "双向");

    private final String code;
    private final String displayName;

    SkillForm(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static SkillForm fromCode(String code) {
        if (code == null) return null;
        for (SkillForm form : values()) {
            if (form.code.equalsIgnoreCase(code)) {
                return form;
            }
        }
        return PROVIDER;
    }
}
