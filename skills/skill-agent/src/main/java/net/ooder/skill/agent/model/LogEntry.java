package net.ooder.skill.agent.model;

import lombok.Data;

@Data
public class LogEntry {
    private String id;
    private String level;
    private String message;
    private String source;
    private long timestamp;
}
