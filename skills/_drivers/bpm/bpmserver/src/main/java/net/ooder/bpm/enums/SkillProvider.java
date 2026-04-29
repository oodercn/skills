package net.ooder.bpm.enums;

public enum SkillProvider {
    SYSTEM("SYSTEM", "系统内置"),
    DRIVER("DRIVER", "驱动适配"),
    BUSINESS("BUSINESS", "业务定制"),
    USER("USER", "用户自定义");

    private final String code;
    private final String description;

    SkillProvider(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SkillProvider fromCode(String code) {
        for (SkillProvider provider : values()) {
            if (provider.code.equals(code)) {
                return provider;
            }
        }
        return SYSTEM;
    }
}
