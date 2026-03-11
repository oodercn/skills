package net.ooder.skill.knowledge.share.dto;

import java.util.List;

public class CollaborationResponse {
    private String collaborationId;
    private String kbId;
    private String initiatorId;
    private String status;
    private String statusName;
    private List<String> participants;
    private Long startedAt;
    private Long stoppedAt;

    public String getCollaborationId() {
        return collaborationId;
    }

    public void setCollaborationId(String collaborationId) {
        this.collaborationId = collaborationId;
    }

    public String getKbId() {
        return kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(Long stoppedAt) {
        this.stoppedAt = stoppedAt;
    }
}
