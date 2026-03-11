package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class IPAddress {
    private String id;
    private String ipAddress;
    private String type;
    private String status;
    private String deviceName;
    private String macAddress;
    private long lastActive;
}
