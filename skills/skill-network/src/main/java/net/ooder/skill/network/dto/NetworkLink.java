package net.ooder.skill.network.dto;

public class NetworkLink {
    private String linkId;
    private String sourceNode;
    private String targetNode;
    private String linkType;
    private String status;
    private int latency;
    private int bandwidth;
    private long establishedAt;
    private long lastActive;

    public NetworkLink() {
        this.status = "active";
        this.linkType = "p2p";
        this.establishedAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(String targetNode) {
        this.targetNode = targetNode;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public long getEstablishedAt() {
        return establishedAt;
    }

    public void setEstablishedAt(long establishedAt) {
        this.establishedAt = establishedAt;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }
}
