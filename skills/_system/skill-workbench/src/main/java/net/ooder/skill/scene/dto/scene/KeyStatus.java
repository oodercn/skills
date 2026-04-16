package net.ooder.skill.scene.dto.scene;

public enum KeyStatus {
    ACTIVE("ACTIVE", "激活"),
    INACTIVE("INACTIVE", "未激活"),
    EXPIRED("EXPIRED", "已过期"),
    REVOKED("REVOKED", "已撤销"),
    PENDING("PENDING", "待激活");

    private final String code;
    private final String name;

    KeyStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static KeyStatus fromCode(String code) {
        for (KeyStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        return PENDING;
    }
}
