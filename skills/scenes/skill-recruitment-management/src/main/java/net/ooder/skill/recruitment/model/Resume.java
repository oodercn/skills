package net.ooder.skill.recruitment.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Resume {
    private String id;
    private String jobId;
    private String jobTitle;
    private String name;
    private String phone;
    private String email;
    private String education;
    private String experience;
    private String currentCompany;
    private String expectedSalary;
    private String source;
    private String status;
    private String stage;
    private Map<String, Object> parsedInfo;
    private List<InterviewRecord> interviews;
    private List<String> attachments;
    private String rating;
    private String comment;
    private LocalDateTime appliedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class InterviewRecord {
        private String id;
        private String type;
        private String interviewer;
        private LocalDateTime time;
        private String location;
        private String result;
        private String feedback;
        private int score;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getInterviewer() { return interviewer; }
        public void setInterviewer(String interviewer) { this.interviewer = interviewer; }
        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }
    public String getExpectedSalary() { return expectedSalary; }
    public void setExpectedSalary(String expectedSalary) { this.expectedSalary = expectedSalary; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public Map<String, Object> getParsedInfo() { return parsedInfo; }
    public void setParsedInfo(Map<String, Object> parsedInfo) { this.parsedInfo = parsedInfo; }
    public List<InterviewRecord> getInterviews() { return interviews; }
    public void setInterviews(List<InterviewRecord> interviews) { this.interviews = interviews; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
