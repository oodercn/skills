package net.ooder.skill.report.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.report.ReportProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultReportProvider implements ReportProvider {
    
    private final Map<String, ReportResult> reports = new ConcurrentHashMap<>();
    private final Map<String, TemplateResult> templates = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return "default";
    }
    
    @Override
    public List<String> getSupportedFormats() {
        return Arrays.asList("pdf", "excel", "word", "html", "csv", "json");
    }
    
    @Override
    public ReportResult createReport(ReportRequest request) {
        log.info("Create report: name={}, type={}", request.getName(), request.getType());
        
        String reportId = request.getReportId() != null ? request.getReportId() : UUID.randomUUID().toString();
        String format = request.getFormat() != null ? request.getFormat() : "pdf";
        
        ReportResult result = new ReportResult();
        result.setSuccess(true);
        result.setReportId(reportId);
        result.setName(request.getName());
        result.setType(request.getType());
        result.setStatus("generated");
        result.setFormat(format);
        result.setFilePath("/reports/" + reportId + "." + format);
        result.setDownloadUrl("/api/reports/" + reportId + "/download");
        result.setFileSize(1024 * 100);
        result.setGeneratedAt(System.currentTimeMillis());
        
        reports.put(reportId, result);
        
        return result;
    }
    
    @Override
    public ReportResult getReport(String reportId) {
        return reports.get(reportId);
    }
    
    @Override
    public List<ReportResult> listReports(String type, int page, int pageSize) {
        List<ReportResult> filteredReports = new ArrayList<>();
        
        for (ReportResult report : reports.values()) {
            if (type == null || type.isEmpty() || type.equals(report.getType())) {
                filteredReports.add(report);
            }
        }
        
        int from = page * pageSize;
        int to = Math.min(from + pageSize, filteredReports.size());
        
        return from < filteredReports.size() 
                ? filteredReports.subList(from, to) 
                : new ArrayList<>();
    }
    
    @Override
    public boolean deleteReport(String reportId) {
        return reports.remove(reportId) != null;
    }
    
    @Override
    public ExportResult exportReport(String reportId, String format, String outputPath) {
        log.info("Export report: reportId={}, format={}", reportId, format);
        
        ReportResult report = reports.get(reportId);
        if (report == null) {
            ExportResult result = new ExportResult();
            result.setSuccess(false);
            result.setErrorCode("REPORT_NOT_FOUND");
            result.setErrorMessage("Report not found: " + reportId);
            return result;
        }
        
        ExportResult result = new ExportResult();
        result.setSuccess(true);
        result.setFilePath(outputPath + "/" + reportId + "." + format);
        result.setDownloadUrl("/api/reports/" + reportId + "/download?format=" + format);
        result.setFileSize(1024 * 100);
        
        return result;
    }
    
    @Override
    public TemplateResult createTemplate(TemplateRequest request) {
        log.info("Create report template: templateId={}", request.getTemplateId());
        
        TemplateResult result = new TemplateResult();
        result.setTemplateId(request.getTemplateId());
        result.setName(request.getName());
        result.setType(request.getType());
        result.setFormat(request.getFormat() != null ? request.getFormat() : "html");
        result.setCreatedAt(System.currentTimeMillis());
        
        templates.put(request.getTemplateId(), result);
        
        return result;
    }
    
    @Override
    public TemplateResult getTemplate(String templateId) {
        return templates.get(templateId);
    }
    
    @Override
    public List<TemplateResult> listTemplates() {
        return new ArrayList<>(templates.values());
    }
    
    @Override
    public boolean deleteTemplate(String templateId) {
        return templates.remove(templateId) != null;
    }
}
