package net.ooder.skill.network.model;

import lombok.Data;

@Data
public class NetworkDevice {
    private String id;
    private String name;
    private String type;
    private String ipAddress;
    private String macAddress;
    private String status;
    private String manufacturer;
    private String model;
    private String firmwareVersion;
    private long lastSeen;
}
