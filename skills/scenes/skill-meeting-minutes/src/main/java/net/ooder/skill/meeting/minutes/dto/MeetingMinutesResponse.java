package net.ooder.skill.meeting.minutes.dto;

import java.util.List;

public class MeetingMinutesResponse {
    private String minutesId;
    private String title;
    private String meetingDate;
    private String location;
    private String summary;
    private List<String> participants;
    private String projectId;
    private String kbId;
    private String nextMeetingDate;
    private String nextMeetingAgenda;
    private String status;
    private String statusName;
    private String meetingType;
    private String meetingTypeName;
    private String createdBy;
    private Long createdAt;
    private Long updatedAt;
    private List<DecisionResponse> decisions;
    private List<ActionItemResponse> actionItems;

    public String getMinutesId() {
        return minutesId;
    }

    public void setMinutesId(String minutesId) {
        this.minutesId = minutesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getKbId() {
        return kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getNextMeetingDate() {
        return nextMeetingDate;
    }

    public void setNextMeetingDate(String nextMeetingDate) {
        this.nextMeetingDate = nextMeetingDate;
    }

    public String getNextMeetingAgenda() {
        return nextMeetingAgenda;
    }

    public void setNextMeetingAgenda(String nextMeetingAgenda) {
        this.nextMeetingAgenda = nextMeetingAgenda;
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

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingTypeName() {
        return meetingTypeName;
    }

    public void setMeetingTypeName(String meetingTypeName) {
        this.meetingTypeName = meetingTypeName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DecisionResponse> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<DecisionResponse> decisions) {
        this.decisions = decisions;
    }

    public List<ActionItemResponse> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItemResponse> actionItems) {
        this.actionItems = actionItems;
    }
}
