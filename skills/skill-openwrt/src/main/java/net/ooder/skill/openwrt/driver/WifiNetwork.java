package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class WifiNetwork {
    private String networkId;
    private String ssid;
    private String encryption;
    private int channel;
    private boolean enabled;
    private boolean hidden;
    private String macaddr;
}
