package net.ooder.skill.network.dto;

import java.util.List;

public class NetworkRoute {
    private String routeId;
    private String sourceNode;
    private String targetNode;
    private List<String> hops;
    private int totalLatency;
    private int hopCount;
    private String status;
    private String routeType;
    private long createdAt;

    public NetworkRoute() {
        this.status = "active";
        this.routeType = "dynamic";
        this.createdAt = System.currentTimeMillis();
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
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

    public List<String> getHops() {
        return hops;
    }

    public void setHops(List<String> hops) {
        this.hops = hops;
    }

    public int getTotalLatency() {
        return totalLatency;
    }

    public void setTotalLatency(int totalLatency) {
        this.totalLatency = totalLatency;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
