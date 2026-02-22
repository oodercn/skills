package net.ooder.skill.agent.dto;

public class AgentNetworkStatus {
    private String agentId;
    private String status;
    private String ipAddress;
    private String macAddress;
    private long bytesSent;
    private long bytesReceived;
    private long packetsSent;
    private long packetsReceived;
    private double latency;
    private long checkTime;

    public AgentNetworkStatus() {
        this.checkTime = System.currentTimeMillis();
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public long getPacketsSent() {
        return packetsSent;
    }

    public void setPacketsSent(long packetsSent) {
        this.packetsSent = packetsSent;
    }

    public long getPacketsReceived() {
        return packetsReceived;
    }

    public void setPacketsReceived(long packetsReceived) {
        this.packetsReceived = packetsReceived;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }
}
