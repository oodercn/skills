package net.ooder.skill.scene.dto.scene;

public enum CapabilityProviderType {
    INTERNAL("INTERNAL", "内部能力"),
    EXTERNAL("EXTERNAL", "外部能力"),
    HYBRID("HYBRID", "混合能力"),
    REMOTE("REMOTE", "远程能力");

    private final String code;
    private final String name;

    CapabilityProviderType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static CapabilityProviderType fromCode(String code) {
        for (CapabilityProviderType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return INTERNAL;
    }
}
