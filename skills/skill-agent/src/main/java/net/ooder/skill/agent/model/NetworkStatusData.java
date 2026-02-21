package net.ooder.skill.agent.model;

import lombok.Data;

@Data
public class NetworkStatusData {
    private String status;
    private String message;
    private long timestamp;
    private int endAgentCount;
    private int routeAgentCount;
    private int totalConnections;
    private int activeConnections;
    private double packetLossRate;
    private double avgResponseTime;
}
