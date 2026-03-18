package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class StaticLease {
    private String id;
    private String ip;
    private String mac;
    private String hostname;
}
