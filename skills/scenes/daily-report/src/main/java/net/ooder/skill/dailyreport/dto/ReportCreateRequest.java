package net.ooder.skill.dailyreport.dto;

import java.time.LocalDate;
import java.util.List;

public class ReportCreateRequest {
    private LocalDate reportDate;
    private String content;
    private List<WorkItemRequest> workItems;
    private List<String> problems;
    private List<String> plans;

    public static class WorkItemRequest {
        private String content;
        private String progress;
        private int percentage;
        private String status;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getProgress() { return progress; }
        public void setProgress(String progress) { this.progress = progress; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<WorkItemRequest> getWorkItems() { return workItems; }
    public void setWorkItems(List<WorkItemRequest> workItems) { this.workItems = workItems; }
    public List<String> getProblems() { return problems; }
    public void setProblems(List<String> problems) { this.problems = problems; }
    public List<String> getPlans() { return plans; }
    public void setPlans(List<String> plans) { this.plans = plans; }
}
