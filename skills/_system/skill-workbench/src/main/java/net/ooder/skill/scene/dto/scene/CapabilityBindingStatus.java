package net.ooder.skill.scene.dto.scene;

public enum CapabilityBindingStatus {
    BINDED("BINDED", "已绑定"),
    UNBINDED("UNBINDED", "未绑定"),
    PENDING("PENDING", "待绑定"),
    ERROR("ERROR", "绑定错误"),
    DISABLED("DISABLED", "已禁用");

    private final String code;
    private final String name;

    CapabilityBindingStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static CapabilityBindingStatus fromCode(String code) {
        for (CapabilityBindingStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        return UNBINDED;
    }
}
