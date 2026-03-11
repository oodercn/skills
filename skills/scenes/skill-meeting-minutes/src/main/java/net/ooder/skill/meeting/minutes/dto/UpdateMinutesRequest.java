package net.ooder.skill.meeting.minutes.dto;

import java.util.List;

public class UpdateMinutesRequest {
    private String title;
    private String location;
    private String summary;
    private List<String> participants;
    private String nextMeetingDate;
    private String nextMeetingAgenda;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
