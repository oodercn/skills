package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class DeviceInfo {
    private String model;
    private String firmware;
    private String kernel;
    private String hostname;
    private long uptime;
    private String macAddress;
}
