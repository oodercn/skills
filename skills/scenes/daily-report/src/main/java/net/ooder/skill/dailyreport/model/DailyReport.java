package net.ooder.skill.dailyreport.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DailyReport {
    private String id;
    private String userId;
    private String userName;
    private String department;
    private LocalDate reportDate;
    private String content;
    private List<WorkItem> workItems;
    private List<String> problems;
    private List<String> plans;
    private String status;
    private String comment;
    private String reviewer;
    private String reviewerId;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class WorkItem {
        private String id;
        private String content;
        private String progress;
        private int percentage;
        private String status;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getProgress() { return progress; }
        public void setProgress(String progress) { this.progress = progress; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<WorkItem> getWorkItems() { return workItems; }
    public void setWorkItems(List<WorkItem> workItems) { this.workItems = workItems; }
    public List<String> getProblems() { return problems; }
    public void setProblems(List<String> problems) { this.problems = problems; }
    public List<String> getPlans() { return plans; }
    public void setPlans(List<String> plans) { this.plans = plans; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
