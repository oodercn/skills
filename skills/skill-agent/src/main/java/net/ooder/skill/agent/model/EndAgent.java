package net.ooder.skill.agent.model;

import lombok.Data;

@Data
public class EndAgent {
    private String agentId;
    private String name;
    private String type;
    private String status;
    private String ipAddress;
    private String routeAgentId;
    private String version;
    private String description;
    private long createdAt;
    private long lastUpdated;
}
