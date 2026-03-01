package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/discovery")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GitDiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(GitDiscoveryController.class);

    @PostMapping("/github")
    public ResultModel<Map<String, Object>> discoverFromGitHub(@RequestBody Map<String, Object> config) {
        String repoUrl = (String) config.get("repoUrl");
        String branch = (String) config.getOrDefault("branch", "main");
        String token = (String) config.get("token");
        
        log.info("[discoverFromGitHub] repoUrl: {}, branch: {}", repoUrl, branch);
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "GITHUB");
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("capabilities", getMockGitHubCapabilities(repoUrl));
        result.put("scanTime", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/gitee")
    public ResultModel<Map<String, Object>> discoverFromGitee(@RequestBody Map<String, Object> config) {
        String repoUrl = (String) config.get("repoUrl");
        String branch = (String) config.getOrDefault("branch", "master");
        String token = (String) config.get("token");
        
        log.info("[discoverFromGitee] repoUrl: {}, branch: {}", repoUrl, branch);
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "GITEE");
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("capabilities", getMockGiteeCapabilities(repoUrl));
        result.put("scanTime", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/git")
    public ResultModel<Map<String, Object>> discoverFromGit(@RequestBody Map<String, Object> config) {
        String repoUrl = (String) config.get("repoUrl");
        String branch = (String) config.getOrDefault("branch", "main");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        
        log.info("[discoverFromGit] repoUrl: {}, branch: {}", repoUrl, branch);
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "GIT_REPOSITORY");
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("capabilities", getMockGitCapabilities(repoUrl));
        result.put("scanTime", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/github/search")
    public ResultModel<List<Map<String, Object>>> searchGitHub(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String topic) {
        
        log.info("[searchGitHub] keyword: {}, topic: {}", keyword, topic);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        results.add(createSkillRepo(
            "ooderCN/skill-daily-report",
            "日志汇报技能",
            "提供日志提醒、提交、汇总、分析能力，支持定时提醒、表单提交、数据汇总、AI分析等功能",
            "https://github.com/ooderCN/skill-daily-report",
            128, 45, "v1.0.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-notification",
            "通知技能",
            "邮件、短信、站内信通知能力，支持模板消息、批量发送、定时发送",
            "https://github.com/ooderCN/skill-notification",
            256, 89, "v1.2.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "会议管理技能",
            "会议预约、提醒、纪要能力，支持会议室预定、参会人员管理、会议记录生成",
            "https://github.com/ooderCN/skill-meeting",
            96, 32, "v1.1.0"
        ));
        
        return ResultModel.success(results);
    }

    @GetMapping("/gitee/search")
    public ResultModel<List<Map<String, Object>>> searchGitee(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String topic) {
        
        log.info("[searchGitee] keyword: {}, topic: {}", keyword, topic);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        results.add(createSkillRepo(
            "ooderCN/skill-daily-report",
            "日志汇报技能",
            "提供日志提醒、提交、汇总、分析能力，支持定时提醒、表单提交、数据汇总、AI分析等功能",
            "https://gitee.com/ooderCN/skill-daily-report",
            128, 45, "v1.0.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "会议管理技能",
            "会议预约、提醒、纪要能力，支持会议室预定、参会人员管理、会议记录生成",
            "https://gitee.com/ooderCN/skill-meeting",
            96, 32, "v1.1.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-network",
            "网络管理技能",
            "网络管理服务，支持网络配置、状态监控、故障诊断",
            "https://gitee.com/ooderCN/skill-network",
            64, 23, "v0.9.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-security",
            "安全管理技能",
            "安全管理服务，支持权限控制、审计日志、安全扫描",
            "https://gitee.com/ooderCN/skill-security",
            85, 28, "v1.0.0"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-im",
            "即时通讯技能",
            "即时通讯服务，支持消息发送、群组管理、消息推送",
            "https://gitee.com/ooderCN/skill-im",
            156, 67, "v1.3.0"
        ));
        
        return ResultModel.success(results);
    }

    @PostMapping("/install")
    public ResultModel<Map<String, Object>> installSkill(@RequestBody Map<String, Object> request) {
        String skillId = (String) request.get("skillId");
        String source = (String) request.get("source");
        String repoUrl = (String) request.get("repoUrl");
        
        log.info("[installSkill] skillId: {}, source: {}, repoUrl: {}", skillId, source, repoUrl);
        
        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("status", "installed");
        result.put("installTime", System.currentTimeMillis());
        result.put("capabilities", getCapabilitiesForSkill(skillId));
        
        return ResultModel.success(result);
    }

    private List<Map<String, Object>> getMockGitHubCapabilities(String repoUrl) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "1.0.0", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "1.0.0", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "日志汇总", "SERVICE",
            "汇总所有员工提交的日志", "1.0.0", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-analyze", "日志分析", "AI",
            "使用AI分析日志内容，提取关键信息", "1.0.0", "GITHUB"
        ));
        
        return capabilities;
    }

    private List<Map<String, Object>> getMockGiteeCapabilities(String repoUrl) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "1.0.0", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "1.0.0", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "日志汇总", "SERVICE",
            "汇总所有员工提交的日志", "1.0.0", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "notification-email", "邮件通知", "COMMUNICATION",
            "发送邮件通知", "2.1.0", "GITEE"
        ));
        
        return capabilities;
    }

    private List<Map<String, Object>> getMockGitCapabilities(String repoUrl) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "data-backup", "数据备份", "STORAGE",
            "自动备份场景数据到云端或本地存储", "1.0.0", "GIT_REPOSITORY"
        ));
        
        capabilities.add(createCapability(
            "system-monitor", "系统监控", "MONITORING",
            "监控系统运行状态，包括CPU、内存、网络等", "1.0.0", "GIT_REPOSITORY"
        ));
        
        return capabilities;
    }

    private Map<String, Object> createCapability(String id, String name, String type, 
            String description, String version, String source) {
        Map<String, Object> cap = new HashMap<>();
        cap.put("id", id);
        cap.put("name", name);
        cap.put("type", type);
        cap.put("description", description);
        cap.put("version", version);
        cap.put("source", source);
        cap.put("status", "available");
        return cap;
    }

    private Map<String, Object> createSkillRepo(String fullName, String name, String description,
            String htmlUrl, int stars, int forks, String latestVersion) {
        Map<String, Object> repo = new HashMap<>();
        repo.put("fullName", fullName);
        repo.put("name", name);
        repo.put("description", description);
        repo.put("htmlUrl", htmlUrl);
        repo.put("stars", stars);
        repo.put("forks", forks);
        repo.put("latestVersion", latestVersion);
        repo.put("installCount", stars * 10);
        repo.put("rating", 4.5 + Math.random() * 0.5);
        repo.put("updatedAt", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 30));
        return repo;
    }

    private List<Map<String, Object>> getCapabilitiesForSkill(String skillId) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        if ("skill-daily-report".equals(skillId)) {
            capabilities.add(createCapability("report-remind", "日志提醒", "COMMUNICATION", "定时提醒", "1.0.0", "INSTALLED"));
            capabilities.add(createCapability("report-submit", "日志提交", "SERVICE", "提交日志", "1.0.0", "INSTALLED"));
            capabilities.add(createCapability("report-aggregate", "日志汇总", "SERVICE", "汇总日志", "1.0.0", "INSTALLED"));
            capabilities.add(createCapability("report-analyze", "日志分析", "AI", "分析日志", "1.0.0", "INSTALLED"));
        } else if ("skill-notification".equals(skillId)) {
            capabilities.add(createCapability("notification-email", "邮件通知", "COMMUNICATION", "发送邮件", "2.1.0", "INSTALLED"));
            capabilities.add(createCapability("notification-sms", "短信通知", "COMMUNICATION", "发送短信", "1.5.0", "INSTALLED"));
        }
        
        return capabilities;
    }
}
