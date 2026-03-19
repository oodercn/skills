package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class ParticipantJoinRequest implements Serializable {
    private String participantId;
    private String name;
    private String participantType;
    private String role;
    
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParticipantType() { return participantType; }
    public void setParticipantType(String participantType) { this.participantType = participantType; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
