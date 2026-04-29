package net.ooder.bpm.enums;

public enum SkillForm {
    SCENE("SCENE", "场景Skill"),
    STANDALONE("STANDALONE", "独立Skill");

    private final String code;
    private final String description;

    SkillForm(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SkillForm fromCode(String code) {
        for (SkillForm form : values()) {
            if (form.code.equals(code)) {
                return form;
            }
        }
        return STANDALONE;
    }
}
