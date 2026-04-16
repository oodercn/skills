package net.ooder.skill.scene.dto.scene;

public enum ConnectorType {
    REST_API("REST_API", "REST API连接器"),
    GRPC("GRPC", "gRPC连接器"),
    WEBSOCKET("WEBSOCKET", "WebSocket连接器"),
    MESSAGE_QUEUE("MESSAGE_QUEUE", "消息队列连接器"),
    DATABASE("DATABASE", "数据库连接器"),
    FILE_SYSTEM("FILE_SYSTEM", "文件系统连接器");

    private final String code;
    private final String name;

    ConnectorType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static ConnectorType fromCode(String code) {
        for (ConnectorType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return REST_API;
    }
}
