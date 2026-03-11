package net.ooder.skill.network.dto;

public class NetworkStats {
    private long totalBytesSent;
    private long totalBytesReceived;
    private long totalPacketsSent;
    private long totalPacketsReceived;
    private long totalErrors;
    private double averageLatency;
    private double bandwidth;

    public NetworkStats() {
        this.totalBytesSent = 0;
        this.totalBytesReceived = 0;
        this.totalPacketsSent = 0;
        this.totalPacketsReceived = 0;
        this.totalErrors = 0;
        this.averageLatency = 0.0;
        this.bandwidth = 0.0;
    }

    public long getTotalBytesSent() {
        return totalBytesSent;
    }

    public void setTotalBytesSent(long totalBytesSent) {
        this.totalBytesSent = totalBytesSent;
    }

    public long getTotalBytesReceived() {
        return totalBytesReceived;
    }

    public void setTotalBytesReceived(long totalBytesReceived) {
        this.totalBytesReceived = totalBytesReceived;
    }

    public long getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public void setTotalPacketsSent(long totalPacketsSent) {
        this.totalPacketsSent = totalPacketsSent;
    }

    public long getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public void setTotalPacketsReceived(long totalPacketsReceived) {
        this.totalPacketsReceived = totalPacketsReceived;
    }

    public long getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(long totalErrors) {
        this.totalErrors = totalErrors;
    }

    public double getAverageLatency() {
        return averageLatency;
    }

    public void setAverageLatency(double averageLatency) {
        this.averageLatency = averageLatency;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }
}
