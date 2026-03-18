package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class NetworkDevice {
    private String id;
    private String name;
    private String type;
    private String ipAddress;
    private String macAddress;
    private String status;
    private long lastSeen;
}
