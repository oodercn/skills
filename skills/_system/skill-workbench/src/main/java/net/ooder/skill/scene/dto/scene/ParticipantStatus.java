package net.ooder.skill.scene.dto.scene;

public enum ParticipantStatus {
    INVITED("INVITED", "已邀请"),
    PENDING("PENDING", "待确认"),
    ACTIVE("ACTIVE", "已激活"),
    SUSPENDED("SUSPENDED", "已暂停"),
    REMOVED("REMOVED", "已移除");

    private final String code;
    private final String name;

    ParticipantStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static ParticipantStatus fromCode(String code) {
        for (ParticipantStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        return PENDING;
    }
}
