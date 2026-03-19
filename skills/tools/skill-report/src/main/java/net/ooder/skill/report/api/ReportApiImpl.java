package net.ooder.skill.report.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ReportApiImpl implements ReportApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, Map<String, Object>> reports = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> templates = new ConcurrentHashMap<>();

    @Override
    public String getApiName() { return "skill-report"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("ReportApi initialized");
    }

    @Override
    public void start() { this.running = true; }

    @Override
    public void stop() { this.running = false; }

    @Override
    public boolean isInitialized() { return initialized; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public Result<Map<String, Object>> generateReport(String reportType, Map<String, Object> params) {
        String reportId = UUID.randomUUID().toString();
        Map<String, Object> report = new HashMap<>();
        report.put("reportId", reportId);
        report.put("type", reportType);
        report.put("params", params);
        report.put("createdAt", System.currentTimeMillis());
        report.put("status", "completed");
        reports.put(reportId, report);
        return Result.success(report);
    }

    @Override
    public Result<byte[]> exportReport(String reportId, String format) {
        return Result.success("Report content".getBytes());
    }

    @Override
    public Result<Map<String, Object>> getReport(String reportId) {
        Map<String, Object> report = reports.get(reportId);
        return report != null ? Result.success(report) : Result.error("Report not found");
    }

    @Override
    public Result<List<Map<String, Object>>> listReports() {
        return Result.success(new ArrayList<>(reports.values()));
    }

    @Override
    public Result<Boolean> deleteReport(String reportId) {
        reports.remove(reportId);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> createTemplate(Map<String, Object> template) {
        String templateId = UUID.randomUUID().toString();
        template.put("templateId", templateId);
        templates.put(templateId, template);
        return Result.success(template);
    }

    @Override
    public Result<List<Map<String, Object>>> listTemplates() {
        return Result.success(new ArrayList<>(templates.values()));
    }
}
