package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class LogEntry {
    private long timestamp;
    private String level;
    private String message;
    private String source;
}
