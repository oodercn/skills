package net.ooder.skill.dashboard.dto;

public class SystemStatsDTO {
    
    private int cpuUsage;
    private int memoryUsage;
    private long totalMemory;
    private long usedMemory;
    private long maxMemory;
    private int threadCount;
    private long uptime;

    public int getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(int cpuUsage) { this.cpuUsage = cpuUsage; }
    public int getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(int memoryUsage) { this.memoryUsage = memoryUsage; }
    public long getTotalMemory() { return totalMemory; }
    public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
    public long getUsedMemory() { return usedMemory; }
    public void setUsedMemory(long usedMemory) { this.usedMemory = usedMemory; }
    public long getMaxMemory() { return maxMemory; }
    public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }
    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
}
