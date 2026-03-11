package net.ooder.skill.network.model;

import java.io.Serializable;

public class NetworkDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type;
    private String ipAddress;
    private String macAddress;
    private String status;
    private String vendor;
    private String model;
    private String firmwareVersion;
    private long lastSeen;
    private long uptime;
    private long bytesIn;
    private long bytesOut;

    public enum DeviceType {
        ROUTER("router", "路由器"),
        SWITCH("switch", "交换机"),
        ACCESS_POINT("access-point", "接入点"),
        FIREWALL("firewall", "防火墙"),
        LOAD_BALANCER("load-balancer", "负载均衡器"),
        GATEWAY("gateway", "网关"),
        SERVER("server", "服务器"),
        CLIENT("client", "客户端"),
        IOT("iot", "物联网设备");

        private final String code;
        private final String description;

        DeviceType(String code, String description) {
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

    public NetworkDevice() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(long bytesIn) {
        this.bytesIn = bytesIn;
    }

    public long getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(long bytesOut) {
        this.bytesOut = bytesOut;
    }

    public boolean isOnline() {
        return "online".equalsIgnoreCase(status) || "active".equalsIgnoreCase(status);
    }
}
