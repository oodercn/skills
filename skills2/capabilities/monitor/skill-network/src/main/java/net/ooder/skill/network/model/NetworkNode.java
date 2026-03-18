package net.ooder.skill.network.model;

import java.io.Serializable;

public class NetworkNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nodeId;
    private String name;
    private NodeType type;
    private String ip;
    private String address;
    private int port;
    private NodeStatus status;
    private long lastSeen;
    private long lastHeartbeat;
    private long createdAt;

    public enum NodeType {
        PERSONAL("personal", "个人设备"),
        HOME_SERVER("home-server", "家庭服务器"),
        IOT_DEVICE("iot-device", "物联网设备"),
        CLOUD_NODE("cloud-node", "云端节点"),
        EDGE_NODE("edge-node", "边缘节点"),
        ROUTER("router", "路由器"),
        SWITCH("switch", "交换机");

        private final String code;
        private final String description;

        NodeType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static NodeType fromCode(String code) {
            for (NodeType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return PERSONAL;
        }
    }

    public enum NodeStatus {
        ONLINE("online", "在线"),
        OFFLINE("offline", "离线"),
        BUSY("busy", "忙碌"),
        MAINTENANCE("maintenance", "维护中"),
        ERROR("error", "错误");

        private final String code;
        private final String description;

        NodeStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static NodeStatus fromCode(String code) {
            for (NodeStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return OFFLINE;
        }
    }

    public NetworkNode() {
        this.status = NodeStatus.OFFLINE;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id != null ? id : nodeId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId != null ? nodeId : id;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isOnline() {
        return NodeStatus.ONLINE.equals(status);
    }
}
