package net.ooder.skill.network.model;

import lombok.Data;

@Data
public class IPBlacklist {
    private String id;
    private String ipAddress;
    private String reason;
    private String source;
    private boolean enabled;
    private long created;
}
