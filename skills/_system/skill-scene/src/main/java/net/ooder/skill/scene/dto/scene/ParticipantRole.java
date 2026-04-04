package net.ooder.skill.scene.dto.scene;

public enum ParticipantRole {
    INITIATOR("INITIATOR", "发起人"),
    OWNER("OWNER", "负责人"),
    MEMBER("MEMBER", "成员"),
    OBSERVER("OBSERVER", "观察者"),
    APPROVER("APPROVER", "审批人"),
    EXECUTOR("EXECUTOR", "执行人");

    private final String code;
    private final String name;

    ParticipantRole(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static ParticipantRole fromCode(String code) {
        for (ParticipantRole role : values()) {
            if (role.code.equals(code)) return role;
        }
        return MEMBER;
    }
}
