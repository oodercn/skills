package net.ooder.skill.network.model;

import java.io.Serializable;

public class NetworkStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private long timestamp;
    private long totalBytesIn;
    private long totalBytesOut;
    private long totalPacketsIn;
    private long totalPacketsOut;
    private int activeConnections;
    private int totalNodes;
    private int onlineNodes;
    private double avgLatency;
    private double bandwidthUsage;
    private double cpuUsage;
    private double memoryUsage;

    public NetworkStats() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTotalBytesIn() {
        return totalBytesIn;
    }

    public void setTotalBytesIn(long totalBytesIn) {
        this.totalBytesIn = totalBytesIn;
    }

    public long getTotalBytesOut() {
        return totalBytesOut;
    }

    public void setTotalBytesOut(long totalBytesOut) {
        this.totalBytesOut = totalBytesOut;
    }

    public long getTotalPacketsIn() {
        return totalPacketsIn;
    }

    public void setTotalPacketsIn(long totalPacketsIn) {
        this.totalPacketsIn = totalPacketsIn;
    }

    public long getTotalPacketsOut() {
        return totalPacketsOut;
    }

    public void setTotalPacketsOut(long totalPacketsOut) {
        this.totalPacketsOut = totalPacketsOut;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public int getOnlineNodes() {
        return onlineNodes;
    }

    public void setOnlineNodes(int onlineNodes) {
        this.onlineNodes = onlineNodes;
    }

    public double getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    public double getBandwidthUsage() {
        return bandwidthUsage;
    }

    public void setBandwidthUsage(double bandwidthUsage) {
        this.bandwidthUsage = bandwidthUsage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
