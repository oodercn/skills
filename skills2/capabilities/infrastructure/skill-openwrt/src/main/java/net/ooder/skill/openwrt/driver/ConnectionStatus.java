package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class ConnectionStatus {
    private boolean connected;
    private String host;
    private int port;
    private String username;
    private long connectedAt;
    private String error;
}
