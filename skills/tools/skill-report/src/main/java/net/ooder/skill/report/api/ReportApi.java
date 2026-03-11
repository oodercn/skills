package net.ooder.skill.report.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鎶ヨ〃鏈嶅姟API
 */
public interface ReportApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 鎶ヨ〃鐢熸垚
    Result<Map<String, Object>> generateReport(String reportType, Map<String, Object> params);
    Result<byte[]> exportReport(String reportId, String format);

    // 鎶ヨ〃绠＄悊
    Result<Map<String, Object>> getReport(String reportId);
    Result<List<Map<String, Object>>> listReports();
    Result<Boolean> deleteReport(String reportId);

    // 妯℃澘绠＄悊
    Result<Map<String, Object>> createTemplate(Map<String, Object> template);
    Result<List<Map<String, Object>>> listTemplates();
}
