package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class SystemStatus {
    private String status;
    private double cpuUsage;
    private double memoryUsage;
    private long totalMemory;
    private long freeMemory;
    private long uptime;
    private int load1;
    private int load5;
    private int load15;
}
