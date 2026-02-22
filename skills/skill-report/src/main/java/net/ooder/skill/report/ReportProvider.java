package net.ooder.skill.report;

import java.util.List;
import java.util.Map;

public interface ReportProvider {
    
    String getProviderType();
    
    List<String> getSupportedFormats();
    
    ReportResult createReport(ReportRequest request);
    
    ReportResult getReport(String reportId);
    
    List<ReportResult> listReports(String type, int page, int pageSize);
    
    boolean deleteReport(String reportId);
    
    ExportResult exportReport(String reportId, String format, String outputPath);
    
    TemplateResult createTemplate(TemplateRequest request);
    
    TemplateResult getTemplate(String templateId);
    
    List<TemplateResult> listTemplates();
    
    boolean deleteTemplate(String templateId);
    
    public static class ReportRequest {
        private String reportId;
        private String name;
        private String type;
        private String templateId;
        private Map<String, Object> parameters;
        private Map<String, Object> data;
        private String format;
        private String schedule;
        
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
    }
    
    public static class ReportResult {
        private boolean success;
        private String reportId;
        private String name;
        private String type;
        private String status;
        private String format;
        private String filePath;
        private String downloadUrl;
        private long fileSize;
        private long generatedAt;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public long getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(long generatedAt) { this.generatedAt = generatedAt; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class ExportResult {
        private boolean success;
        private String filePath;
        private String downloadUrl;
        private long fileSize;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class TemplateRequest {
        private String templateId;
        private String name;
        private String type;
        private String content;
        private String format;
        private Map<String, Object> schema;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public Map<String, Object> getSchema() { return schema; }
        public void setSchema(Map<String, Object> schema) { this.schema = schema; }
    }
    
    public static class TemplateResult {
        private String templateId;
        private String name;
        private String type;
        private String format;
        private long createdAt;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
}
