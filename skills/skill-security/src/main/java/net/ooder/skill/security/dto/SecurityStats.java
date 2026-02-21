package net.ooder.skill.security.dto;

public class SecurityStats {
    private long totalScans;
    private long threatsDetected;
    private long threatsResolved;
    private long blockedConnections;
    private long auditLogs;
    private double averageScanTime;

    public SecurityStats() {
        this.totalScans = 0;
        this.threatsDetected = 0;
        this.threatsResolved = 0;
        this.blockedConnections = 0;
        this.auditLogs = 0;
        this.averageScanTime = 0.0;
    }

    public long getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(long totalScans) {
        this.totalScans = totalScans;
    }

    public long getThreatsDetected() {
        return threatsDetected;
    }

    public void setThreatsDetected(long threatsDetected) {
        this.threatsDetected = threatsDetected;
    }

    public long getThreatsResolved() {
        return threatsResolved;
    }

    public void setThreatsResolved(long threatsResolved) {
        this.threatsResolved = threatsResolved;
    }

    public long getBlockedConnections() {
        return blockedConnections;
    }

    public void setBlockedConnections(long blockedConnections) {
        this.blockedConnections = blockedConnections;
    }

    public long getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(long auditLogs) {
        this.auditLogs = auditLogs;
    }

    public double getAverageScanTime() {
        return averageScanTime;
    }

    public void setAverageScanTime(double averageScanTime) {
        this.averageScanTime = averageScanTime;
    }
}
