package net.ooder.skill.network.model;

import lombok.Data;

@Data
public class IPAddress {
    private String id;
    private String ipAddress;
    private String type;
    private String status;
    private String deviceName;
    private String macAddress;
    private String deviceType;
    private String leaseTime;
    private long lastActive;
}
