package net.ooder.skill.network.p2p;

public enum P2PEventType {
    SERVICE_STARTED("service-started", "服务启动"),
    SERVICE_STOPPED("service-stopped", "服务停止"),
    NODE_DISCOVERED("node-discovered", "节点发现"),
    NODE_LOST("node-lost", "节点丢失"),
    NODE_CONNECTED("node-connected", "节点连接"),
    NODE_DISCONNECTED("node-disconnected", "节点断开"),
    SKILL_SHARED("skill-shared", "技能共享"),
    SKILL_UNSHARED("skill-unshared", "取消共享"),
    SKILLS_UPDATED("skills-updated", "技能更新"),
    HEARTBEAT_RECEIVED("heartbeat-received", "心跳接收"),
    ERROR("error", "错误");

    private final String code;
    private final String description;

    P2PEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
