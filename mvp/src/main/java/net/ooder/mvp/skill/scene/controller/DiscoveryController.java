package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.capability.service.LocalDiscoveryService;
import net.ooder.mvp.skill.scene.capability.service.LocalDiscoveryService.DiscoveryResult;
import net.ooder.mvp.skill.scene.capability.service.LocalDiscoveryService.SyncResult;
import net.ooder.mvp.skill.scene.discovery.SkillIndexLoader;
import net.ooder.mvp.skill.scene.dto.discovery.CapabilityDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/discovery")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryController.class);

    @Autowired
    private LocalDiscoveryService localDiscoveryService;

    @Autowired
    private SkillIndexLoader skillIndexLoader;

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Value("${ooder.discovery.use-index-first:true}")
    private boolean useIndexFirst;

    @PostMapping("/local")
    public ResultModel<Map<String, Object>> discoverLocal(@RequestBody(required = false) Map<String, Object> request) {
        log.info("[discoverLocal] Starting local discovery");
        
        DiscoveryResult discovery = localDiscoveryService.discover();
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("capabilities", discovery.getCapabilities());
        result.put("total", discovery.getTotal());
        result.put("stats", discovery.getStats());
        result.put("source", discovery.getSource());
        result.put("timestamp", discovery.getTimestamp());
        
        return ResultModel.success(result);
    }

    @PostMapping("/sync")
    public ResultModel<Map<String, Object>> syncFromSkills() {
        log.info("[syncFromSkills] Starting manual sync");
        
        SyncResult syncResult = localDiscoveryService.sync();
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("synced", syncResult.getSynced());
        result.put("skipped", syncResult.getSkipped());
        result.put("errors", syncResult.getErrors());
        result.put("timestamp", syncResult.getTimestamp());
        result.put("message", String.format("同步完成: 成功 %d, 跳过 %d, 错误 %d", 
                syncResult.getSynced(), syncResult.getSkipped(), syncResult.getErrors()));
        
        return ResultModel.success(result);
    }

    @GetMapping("/capability/{capabilityId}")
    public ResultModel<Map<String, Object>> getCapabilityDetail(@PathVariable String capabilityId) {
        log.info("[getCapabilityDetail] Getting detail for: {}", capabilityId);
        
        Map<String, Object> detail = localDiscoveryService.getCapabilityDetail(capabilityId);
        if (detail == null) {
            return ResultModel.error("能力不存在: " + capabilityId);
        }
        
        return ResultModel.success(detail);
    }

    @PostMapping("/github")
    public ResultModel<Map<String, Object>> discoverFromGitHub(@RequestBody(required = false) Map<String, Object> request) {
        log.info("[discoverFromGitHub] Starting GitHub discovery, useIndexFirst: {}", useIndexFirst);
        
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        String repoUrl = request != null ? (String) request.getOrDefault("repoUrl", "https://github.com/ooderCN/skills") : "https://github.com/ooderCN/skills";
        String branch = request != null ? (String) request.getOrDefault("branch", "main") : "main";
        
        if (useIndexFirst) {
            List<CapabilityDTO> caps = skillIndexLoader.getSkillsFromIndex("GITHUB");
            capabilities = convertCapabilitiesToMaps(caps);
            log.info("[discoverFromGitHub] Found {} capabilities from skill-index", capabilities.size());
        }
        
        if (capabilities.isEmpty() && mockEnabled) {
            log.info("[discoverFromGitHub] Using mock data");
            capabilities = getMockGitHubCapabilities();
        }
        
        result.put("capabilities", capabilities);
        result.put("total", capabilities.size());
        result.put("source", "github");
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/gitee")
    public ResultModel<Map<String, Object>> discoverFromGitee(@RequestBody(required = false) Map<String, Object> request) {
        log.info("[discoverFromGitee] Starting Gitee discovery, useIndexFirst: {}", useIndexFirst);
        
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        String repoUrl = request != null ? (String) request.getOrDefault("repoUrl", "https://gitee.com/ooderCN/skills") : "https://gitee.com/ooderCN/skills";
        String branch = request != null ? (String) request.getOrDefault("branch", "main") : "main";
        
        if (useIndexFirst) {
            List<CapabilityDTO> caps = skillIndexLoader.getAllCapabilities("GITEE");
            capabilities = convertCapabilitiesToMaps(caps);
            log.info("[discoverFromGitee] Found {} capabilities from skill-index", capabilities.size());
        }
        
        if (capabilities.isEmpty() && mockEnabled) {
            log.info("[discoverFromGitee] Using mock data");
            capabilities = getMockGiteeCapabilities();
        }
        
        result.put("capabilities", capabilities);
        result.put("total", capabilities.size());
        result.put("source", "gitee");
        result.put("repoUrl", repoUrl);
        result.put("branch", branch);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    private List<Map<String, Object>> convertCapabilitiesToMaps(List<CapabilityDTO> caps) {
        return caps.stream().map(cap -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", cap.getId());
            map.put("name", cap.getName());
            map.put("description", cap.getDescription());
            map.put("version", cap.getVersion());
            map.put("source", cap.getSource());
            map.put("status", cap.getStatus());
            map.put("type", cap.getType());
            map.put("sceneCapability", cap.isSceneCapability());
            map.put("skillForm", cap.getSkillForm());
            map.put("sceneType", cap.getSceneType());
            map.put("category", cap.getCategory());
            map.put("capabilityCategory", cap.getCapabilityCategory());
            map.put("businessCategory", cap.getBusinessCategory());
            map.put("visibility", cap.getVisibility());
            map.put("installed", cap.isInstalled());
            map.put("capabilities", cap.getCapabilities());
            map.put("dependencies", cap.getDependencies());
            map.put("tags", cap.getTags());
            map.put("driverConditions", cap.getDriverConditions());
            map.put("participants", cap.getParticipants());
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/install")
    public ResultModel<Map<String, Object>> installSkill(@RequestBody Map<String, Object> request) {
        String skillId = (String) request.get("skillId");
        String source = (String) request.getOrDefault("source", "local");
        
        log.info("[installSkill] Installing skill: {} from {}", skillId, source);
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("skillId", skillId);
        result.put("source", source);
        
        if (skillPackageManager == null) {
            log.warn("[installSkill] SkillPackageManager not available");
            result.put("status", mockEnabled ? "installed" : "failed");
            result.put("message", mockEnabled ? "模拟安装成功" : "SkillPackageManager 不可用");
            if (mockEnabled) {
                skillIndexLoader.markAsInstalled(skillId);
            }
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
            
            String downloadUrl = skillIndexLoader.getDownloadUrl(skillId);
            
            if (downloadUrl != null) {
                log.info("[installSkill] Installing from URL: {}", downloadUrl);
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

    private List<Map<String, Object>> getMockGitHubCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(createMockCapability("daily-log-scene", "日志汇报场景", "SCENE",
            "完整的日志汇报场景能力，包含提醒、提交、汇总、分析等闭环流程", "2.3", "GITHUB"));
        capabilities.add(createMockCapability("report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "2.3", "GITHUB"));
        capabilities.add(createMockCapability("report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "2.3", "GITHUB"));
        
        return capabilities;
    }

    private List<Map<String, Object>> getMockGiteeCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(createMockCapability("daily-log-scene", "日志汇报场景", "SCENE",
            "完整的日志汇报场景能力，包含提醒、提交、汇总、分析等闭环流程", "2.3", "GITEE"));
        capabilities.add(createMockCapability("report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "2.3", "GITEE"));
        capabilities.add(createMockCapability("report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "2.3", "GITEE"));
        capabilities.add(createMockCapability("notification-email", "邮件通知", "COMMUNICATION",
            "发送邮件通知", "2.3", "GITEE"));
        
        return capabilities;
    }

    private Map<String, Object> createMockCapability(String id, String name, String type, 
            String description, String version, String source) {
        Map<String, Object> cap = new LinkedHashMap<>();
        cap.put("id", id);
        cap.put("name", name);
        cap.put("type", type);
        cap.put("description", description);
        cap.put("version", version);
        cap.put("source", source);
        cap.put("status", "available");
        cap.put("sceneCapability", "SCENE".equals(type));
        cap.put("installed", false);
        return cap;
    }
    
    @GetMapping("/categories/stats")
    public ResultModel<Map<String, Object>> getCategoryStats() {
        log.info("[getCategoryStats] Getting category statistics");
        Map<String, Object> stats = skillIndexLoader.getCategoryStats();
        return ResultModel.success(stats);
    }
    
    @GetMapping("/categories/user-facing")
    public ResultModel<List<Map<String, Object>>> getUserFacingCategories() {
        log.info("[getUserFacingCategories] Getting user facing categories");
        List<Map<String, Object>> categories = skillIndexLoader.getUserFacingCategories();
        return ResultModel.success(categories);
    }
    
    @GetMapping("/categories/all")
    public ResultModel<List<Map<String, Object>>> getAllCategories() {
        log.info("[getAllCategories] Getting all categories with stats");
        List<Map<String, Object>> categories = skillIndexLoader.getAllCategoriesWithStats();
        return ResultModel.success(categories);
    }
    
    @GetMapping("/categories/{categoryId}/subcategories")
    public ResultModel<List<Map<String, Object>>> getSubCategories(@PathVariable String categoryId) {
        log.info("[getSubCategories] Getting subcategories for: {}", categoryId);
        List<Map<String, Object>> subCategories = skillIndexLoader.getSubCategories(categoryId);
        return ResultModel.success(subCategories);
    }
}
