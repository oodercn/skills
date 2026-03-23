package net.ooder.skill.recruitment.dto;

public class InterviewScheduleRequest {
    private String type;
    private String interviewer;
    private String time;
    private String location;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getInterviewer() { return interviewer; }
    public void setInterviewer(String interviewer) { this.interviewer = interviewer; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
