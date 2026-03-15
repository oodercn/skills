package net.ooder.skill.capability.model;

public enum ConnectorType {
    HTTP("http", "HTTP连接器"),
    WEBSOCKET("websocket", "WebSocket连接器"),
    GRPC("grpc", "gRPC连接器"),
    TCP("tcp", "TCP连接器");

    private final String code;
    private final String displayName;

    ConnectorType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static ConnectorType fromCode(String code) {
        if (code == null) return null;
        for (ConnectorType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
