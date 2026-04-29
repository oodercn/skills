package net.ooder.bpm.enums;

public enum PerformerType {
    HUMAN("HUMAN", "真人"),
    AGENT("AGENT", "拟人智能体"),
    SYSTEM("SYSTEM", "系统");

    private final String code;
    private final String description;

    PerformerType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PerformerType fromCode(String code) {
        for (PerformerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return HUMAN;
    }
}
