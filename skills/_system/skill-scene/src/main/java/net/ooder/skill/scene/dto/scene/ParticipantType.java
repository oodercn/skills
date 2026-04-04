package net.ooder.skill.scene.dto.scene;

public enum ParticipantType {
    USER("USER", "用户参与者"),
    AGENT("AGENT", "智能体参与者"),
    SYSTEM("SYSTEM", "系统参与者"),
    DEVICE("DEVICE", "设备参与者"),
    EXTERNAL("EXTERNAL", "外部参与者");

    private final String code;
    private final String name;

    ParticipantType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static ParticipantType fromCode(String code) {
        for (ParticipantType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return USER;
    }
}
