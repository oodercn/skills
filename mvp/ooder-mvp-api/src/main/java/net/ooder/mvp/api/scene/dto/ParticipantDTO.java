package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private String participantId;
    private String name;
    private String participantType;
    private String role;
    private String status;
    private long joinTime;
    private long lastHeartbeat;
    
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParticipantType() { return participantType; }
    public void setParticipantType(String participantType) { this.participantType = participantType; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getJoinTime() { return joinTime; }
    public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
}
