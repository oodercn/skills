package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class IPBlacklist {
    private String id;
    private String ipAddress;
    private String reason;
    private boolean enabled;
    private long created;
}
