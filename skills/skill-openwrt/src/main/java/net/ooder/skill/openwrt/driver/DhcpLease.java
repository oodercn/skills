package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class DhcpLease {
    private String ip;
    private String mac;
    private String hostname;
    private String leaseTime;
    private long expires;
}
