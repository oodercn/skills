package net.ooder.skill.health.dto;

public class HealthReport {
    private String reportId;
    private String reportType;
    private HealthCheckResult summary;
    private String format;
    private String content;
    private long generatedAt;

    public HealthReport() {
        this.reportId = "report-" + System.currentTimeMillis();
        this.generatedAt = System.currentTimeMillis();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public HealthCheckResult getSummary() {
        return summary;
    }

    public void setSummary(HealthCheckResult summary) {
        this.summary = summary;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }
}
