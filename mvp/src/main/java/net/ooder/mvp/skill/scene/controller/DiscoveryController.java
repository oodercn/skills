package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.integration.SceneEngineIntegration;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.InstallRequest.InstallMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/discovery")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryController.class);

    @Autowired(required = false)
    private SceneEngineIntegration sceneEngineIntegration;

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @PostMapping("/local")
    public ResultModel<Map<String, Object>> discoverLocal(@RequestBody(required = false) Map<String, Object> request) {
        log.info("[discoverLocal] Starting local discovery");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> capabilities = new ArrayList<>();
        List<Map<String, Object>> skills = new ArrayList<>();
        
        if (sceneEngineIntegration != null) {
            try {
                skills = sceneEngineIntegration.discoverSkills();
                capabilities = sceneEngineIntegration.discoverCapabilities();
                log.info("[discoverLocal] Discovered {} skills and {} capabilities", skills.size(), capabilities.size());
            } catch (Exception e) {
                log.error("[discoverLocal] Error discovering: {}", e.getMessage());
            }
        }
        
        if (capabilities.isEmpty() && mockEnabled) {
            log.info("[discoverLocal] Using mock capabilities");
            capabilities = getMockCapabilities();
            skills = getMockSkills();
        }
        
        result.put("capabilities", capabilities);
        result.put("skills", skills);
        result.put("source", "local");
        result.put("timestamp", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/github")
    public ResultModel<Map<String, Object>> discoverFromGitHub(@RequestBody Map<String, Object> request) {
        log.info("[discoverFromGitHub] Starting GitHub discovery");
        return discoverFromGit("github", request);
    }

    @PostMapping("/gitee")
    public ResultModel<Map<String, Object>> discoverFromGitee(@RequestBody Map<String, Object> request) {
        log.info("[discoverFromGitee] Starting Gitee discovery");
        return discoverFromGit("gitee", request);
    }

    private ResultModel<Map<String, Object>> discoverFromGit(String source, Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> capabilities = new ArrayList<>();
        List<Map<String, Object>> skills = new ArrayList<>();
        
        String repoUrl = (String) request.getOrDefault("repoUrl", 
            "github".equals(source) ? "https://github.com/ooderCN/skills" : "https://gitee.com/ooderCN/skills");
        String branch = (String) request.getOrDefault("branch", "main");
        
        log.info("[discoverFromGit] Source: {}, Repo: {}, Branch: {}", source, repoUrl, branch);
        
        if (mockEnabled) {
            capabilities = getMockCapabilities();
            skills = getMockSkills();
        }
        
        result.put("capabilities", capabilities);
        result.put("skills", skills);
        result.put("source", source);
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/install")
    public ResultModel<Map<String, Object>> installSkill(@RequestBody Map<String, Object> request) {
        String skillId = (String) request.get("skillId");
        String source = (String) request.getOrDefault("source", "local");
        
        log.info("[installSkill] Installing skill: {} from {}", skillId, source);
        
        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("source", source);
        
        if (skillPackageManager == null) {
            log.warn("[installSkill] SkillPackageManager not available");
            result.put("status", mockEnabled ? "installed" : "failed");
            result.put("message", mockEnabled ? "模拟安装成功" : "SkillPackageManager 不可用");
            return ResultModel.success(result);
        }
        
        try {
            CompletableFuture<Boolean> isInstalledFuture = skillPackageManager.isInstalled(skillId);
            Boolean isInstalled = isInstalledFuture.get(10, java.util.concurrent.TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(isInstalled)) {
                log.info("[installSkill] Skill already installed: {}", skillId);
                result.put("status", "installed");
                result.put("message", "已安装，跳过");
                return ResultModel.success(result);
            }
            
            CompletableFuture<InstallResultWithDependencies> installFuture = 
                skillPackageManager.installWithDependencies(skillId, InstallMode.FULL_INSTALL);
            
            InstallResultWithDependencies installResult = installFuture.get(60, java.util.concurrent.TimeUnit.SECONDS);
            
            if (installResult != null && installResult.isSuccess()) {
                log.info("[installSkill] Successfully installed: {}", skillId);
                result.put("status", "installed");
                result.put("message", "安装成功");
                if (installResult.getInstalledDependencies() != null) {
                    result.put("installedDependencies", installResult.getInstalledDependencies());
                }
            } else {
                String error = installResult != null ? installResult.getError() : "未知错误";
                log.error("[installSkill] Failed to install {}: {}", skillId, error);
                result.put("status", "failed");
                result.put("message", "安装失败: " + error);
            }
        } catch (Exception e) {
            log.error("[installSkill] Error installing {}: {}", skillId, e.getMessage());
            result.put("status", "failed");
            result.put("message", "安装异常: " + e.getMessage());
        }
        
        return ResultModel.success(result);
    }

    private List<Map<String, Object>> getMockCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        String[][] caps = {
            {"report-remind", "日志提醒", "notification", "提醒用户提交日志"},
            {"report-submit", "日志提交", "data-input", "提交工作日志"},
            {"report-aggregate", "日志汇总", "data-processing", "汇总团队日志"},
            {"report-analyze", "日志分析", "intelligence", "分析日志数据"},
            {"notification-email", "邮件通知", "notification", "发送邮件通知"},
            {"notification-sms", "短信通知", "notification", "发送短信通知"}
        };
        
        for (String[] cap : caps) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cap[0]);
            map.put("name", cap[1]);
            map.put("category", cap[2]);
            map.put("description", cap[3]);
            map.put("skillId", "skill-daily-report");
            map.put("status", "available");
            map.put("installed", false);
            capabilities.add(map);
        }
        
        return capabilities;
    }

    private List<Map<String, Object>> getMockSkills() {
        List<Map<String, Object>> skills = new ArrayList<>();
        
        Map<String, Object> dailyReportSkill = new HashMap<>();
        dailyReportSkill.put("skillId", "skill-daily-report");
        dailyReportSkill.put("name", "日志汇报技能");
        dailyReportSkill.put("version", "1.0.0");
        dailyReportSkill.put("description", "工作日志管理技能包");
        dailyReportSkill.put("capabilities", Arrays.asList("report-remind", "report-submit", "report-aggregate", "report-analyze"));
        dailyReportSkill.put("status", "available");
        dailyReportSkill.put("installed", false);
        skills.add(dailyReportSkill);
        
        Map<String, Object> notificationSkill = new HashMap<>();
        notificationSkill.put("skillId", "skill-notification");
        notificationSkill.put("name", "通知技能");
        notificationSkill.put("version", "1.0.0");
        notificationSkill.put("description", "消息通知技能包");
        notificationSkill.put("capabilities", Arrays.asList("notification-email", "notification-sms"));
        notificationSkill.put("status", "available");
        notificationSkill.put("installed", false);
        skills.add(notificationSkill);
        
        return skills;
    }
}
