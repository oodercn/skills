package net.ooder.skill.scene.dto.scene;

public enum KeyType {
    API_KEY("API_KEY", "API密钥"),
    ACCESS_TOKEN("ACCESS_TOKEN", "访问令牌"),
    CERTIFICATE("CERTIFICATE", "证书"),
    PASSWORD("PASSWORD", "密码"),
    SSH_KEY("SSH_KEY", "SSH密钥");

    private final String code;
    private final String name;

    KeyType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static KeyType fromCode(String code) {
        for (KeyType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return API_KEY;
    }
}
