package net.ooder.mvp.skill.scene.network.dto;

public class LinkDTO {
    private String linkId;
    private String linkType;
    private String sourceNode;
    private String targetNode;
    private String status;
    private long latency;
    private long bandwidth;
    private long establishedAt;
    private long lastActive;
    private int bindingCount;

    public String getLinkId() { return linkId; }
    public void setLinkId(String linkId) { this.linkId = linkId; }
    public String getLinkType() { return linkType; }
    public void setLinkType(String linkType) { this.linkType = linkType; }
    public String getSourceNode() { return sourceNode; }
    public void setSourceNode(String sourceNode) { this.sourceNode = sourceNode; }
    public String getTargetNode() { return targetNode; }
    public void setTargetNode(String targetNode) { this.targetNode = targetNode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLatency() { return latency; }
    public void setLatency(long latency) { this.latency = latency; }
    public long getBandwidth() { return bandwidth; }
    public void setBandwidth(long bandwidth) { this.bandwidth = bandwidth; }
    public long getEstablishedAt() { return establishedAt; }
    public void setEstablishedAt(long establishedAt) { this.establishedAt = establishedAt; }
    public long getLastActive() { return lastActive; }
    public void setLastActive(long lastActive) { this.lastActive = lastActive; }
    public int getBindingCount() { return bindingCount; }
    public void setBindingCount(int bindingCount) { this.bindingCount = bindingCount; }
}
