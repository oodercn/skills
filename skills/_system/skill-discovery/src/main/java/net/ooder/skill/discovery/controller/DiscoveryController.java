package net.ooder.skill.discovery.controller;

import net.ooder.skill.discovery.model.ResultModel;
import net.ooder.skill.discovery.dto.discovery.*;
import net.ooder.skill.discovery.dto.discovery.PageResult;
import net.ooder.skill.discovery.controller.converter.DiscoveryConverter;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.common.enums.DiscoveryMethod;
import net.ooder.skill.common.discovery.DiscoveryOrchestrator;
import net.ooder.skill.common.discovery.DiscoveryResult;
import net.ooder.skill.common.discovery.CapabilityDTO;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/discovery")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class DiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryController.class);

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;
    
    @Autowired(required = false)
    private SkillRegistry skillRegistry;
    
    @Autowired(required = false)
    private SkillDiscoverer skillDiscoverer;
    
    @Autowired(required = false)
    private DiscoveryOrchestrator discoveryOrchestrator;
    
    @Autowired(required = false)
    private DiscoveryCoordinator discoveryCoordinator;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Value("${ooder.discovery.use-se-sdk:true}")
    private boolean useSeSdk;

    @PostMapping("/local")
    public ResultModel<LocalDiscoveryResultDTO> discoverLocal(@RequestBody(required = false) Map<String, Object> request) {
        log.info("[discoverLocal] Starting local discovery");
        
        LocalDiscoveryResultDTO result = new LocalDiscoveryResultDTO();
        List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> capabilities = new ArrayList<>();
        
        if (discoveryCoordinator != null) {
            log.info("[discoverLocal] Using DiscoveryCoordinator from scene-engine");
            try {
                CompletableFuture<List<net.ooder.scene.skill.model.RichSkill>> future = 
                    discoveryCoordinator.discover("LOCAL");
                List<net.ooder.scene.skill.model.RichSkill> skills = future.get(30, TimeUnit.SECONDS);
                log.info("[discoverLocal] DiscoveryCoordinator discovered {} skills", skills.size());
                
                if (skills != null && !skills.isEmpty()) {
                    for (net.ooder.scene.skill.model.RichSkill skill : skills) {
                        capabilities.add(convertRichSkillToCapabilityDTO(skill));
                    }
                    
                    capabilities = filterInternalCapabilities(capabilities);
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setSource("discovery-coordinator");
                    result.setTimestamp(System.currentTimeMillis());
                    
                    return ResultModel.success(result);
                }
                log.warn("[discoverLocal] DiscoveryCoordinator returned empty result, trying fallback");
            } catch (Throwable e) {
                log.error("[discoverLocal] DiscoveryCoordinator failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (discoveryOrchestrator != null) {
            log.info("[discoverLocal] Using DiscoveryOrchestrator");
            try {
                DiscoveryResult discoveryResult = discoveryOrchestrator.discoverLocal();
                log.info("[discoverLocal] DiscoveryOrchestrator result: success={}, total={}", 
                    discoveryResult.isSuccess(), discoveryResult.getTotalCount());
                
                if (discoveryResult.isSuccess() && discoveryResult.getCapabilities() != null) {
                    for (CapabilityDTO cap : discoveryResult.getCapabilities()) {
                        capabilities.add(convertToDiscoveryCapabilityDTO(cap));
                    }
                    
                    capabilities = filterInternalCapabilities(capabilities);
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setSource("discovery-orchestrator");
                    result.setTimestamp(System.currentTimeMillis());
                    
                    return ResultModel.success(result);
                }
                log.warn("[discoverLocal] DiscoveryOrchestrator returned empty or failed result: {}", 
                    discoveryResult.getErrorMessage());
            } catch (Throwable e) {
                log.error("[discoverLocal] DiscoveryOrchestrator failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (useSeSdk && skillDiscoverer != null) {
            log.info("[discoverLocal] Using SE SDK SkillDiscoverer");
            try {
                CompletableFuture<List<SkillPackage>> future = skillDiscoverer.discover();
                List<SkillPackage> packages = future.get(30, TimeUnit.SECONDS);
                log.info("[discoverLocal] Discovered {} skill packages from SE SDK", packages.size());
                
                if (packages != null && !packages.isEmpty()) {
                    for (SkillPackage pkg : packages) {
                        capabilities.add(convertSkillPackageToCapabilityDTO(pkg));
                    }
                    
                    capabilities = filterInternalCapabilities(capabilities);
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setSource("se-sdk");
                    result.setTimestamp(System.currentTimeMillis());
                    
                    return ResultModel.success(result);
                }
                log.warn("[discoverLocal] SE SDK returned empty result, trying next source");
            } catch (Throwable e) {
                log.error("[discoverLocal] SE SDK discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (useSeSdk && skillPackageManager != null) {
            log.info("[discoverLocal] Using SE SDK SkillPackageManager");
            try {
                CompletableFuture<List<SkillPackage>> future = skillPackageManager.discoverAll(DiscoveryMethod.LOCAL_FS);
                List<SkillPackage> packages = future.get(30, TimeUnit.SECONDS);
                log.info("[discoverLocal] Discovered {} skill packages from SkillPackageManager", packages.size());
                
                if (packages != null && !packages.isEmpty()) {
                    for (SkillPackage pkg : packages) {
                        capabilities.add(convertSkillPackageToCapabilityDTO(pkg));
                    }
                    
                    capabilities = filterInternalCapabilities(capabilities);
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setSource("se-package-manager");
                    result.setTimestamp(System.currentTimeMillis());
                    
                    return ResultModel.success(result);
                }
                log.warn("[discoverLocal] SkillPackageManager returned empty result, trying next source");
            } catch (Throwable e) {
                log.error("[discoverLocal] SE SDK package manager discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (skillRegistry != null) {
            log.info("[discoverLocal] Using SE SDK SkillRegistry");
            try {
                List<InstalledSkill> installedSkills = skillRegistry.getInstalledSkills();
                log.info("[discoverLocal] Found {} installed skills from SE SDK", installedSkills.size());
                
                if (installedSkills != null && !installedSkills.isEmpty()) {
                    for (InstalledSkill skill : installedSkills) {
                        capabilities.add(convertInstalledSkillToCapabilityDTO(skill));
                    }
                    
                    capabilities = filterInternalCapabilities(capabilities);
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setSource("se-registry");
                    result.setTimestamp(System.currentTimeMillis());
                    
                    return ResultModel.success(result);
                }
                log.warn("[discoverLocal] SkillRegistry returned empty result, trying next source");
            } catch (Throwable e) {
                log.error("[discoverLocal] SE SDK registry query failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        log.error("[discoverLocal] No discovery service available");
        result.setCapabilities(capabilities);
        result.setTotal(0);
        result.setSource("none");
        result.setErrorMessage("无法获取技能数据：SE SDK 服务不可用，请检查配置");
        result.setTimestamp(System.currentTimeMillis());
        
        return ResultModel.error("无法获取技能数据: " + result.getErrorMessage());
    }

    @PostMapping("/github")
    public ResultModel<GitDiscoveryResultDTO> discoverFromGitHub(@RequestBody(required = false) GitDiscoveryRequestDTO request) {
        log.info("[discoverFromGitHub] Starting GitHub discovery");
        
        GitDiscoveryResultDTO result = new GitDiscoveryResultDTO();
        List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> capabilities = new ArrayList<>();
        
        String repoUrl = request != null && request.getRepoUrl() != null ? request.getRepoUrl() : "https://github.com/ooderCN/skills";
        String branch = request != null && request.getBranch() != null ? request.getBranch() : "main";
        
        result.setRepoUrl(repoUrl);
        result.setBranch(branch);
        result.setSource("github");
        
        if (discoveryCoordinator != null) {
            log.info("[discoverFromGitHub] Using DiscoveryCoordinator from scene-engine");
            try {
                CompletableFuture<List<net.ooder.scene.skill.model.RichSkill>> future = 
                    discoveryCoordinator.discover("GITHUB");
                List<net.ooder.scene.skill.model.RichSkill> skills = future.get(60, TimeUnit.SECONDS);
                log.info("[discoverFromGitHub] DiscoveryCoordinator discovered {} skills from GitHub", skills.size());
                
                if (skills != null && !skills.isEmpty()) {
                    for (net.ooder.scene.skill.model.RichSkill skill : skills) {
                        capabilities.add(convertRichSkillToCapabilityDTO(skill));
                    }
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setTimestamp(System.currentTimeMillis());
                    return ResultModel.success(result);
                }
                log.warn("[discoverFromGitHub] DiscoveryCoordinator returned empty result, trying fallback");
            } catch (Exception e) {
                log.error("[discoverFromGitHub] DiscoveryCoordinator GitHub discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (discoveryOrchestrator != null) {
            log.info("[discoverFromGitHub] Using DiscoveryOrchestrator for GitHub");
            try {
                DiscoveryResult discoveryResult = discoveryOrchestrator.discoverFromGitHub(repoUrl, branch);
                log.info("[discoverFromGitHub] DiscoveryOrchestrator result: success={}, total={}", 
                    discoveryResult.isSuccess(), discoveryResult.getTotalCount());
                
                if (discoveryResult.isSuccess() && discoveryResult.getCapabilities() != null) {
                    for (CapabilityDTO cap : discoveryResult.getCapabilities()) {
                        capabilities.add(convertToDiscoveryCapabilityDTO(cap));
                    }
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setTimestamp(System.currentTimeMillis());
                    return ResultModel.success(result);
                }
                log.warn("[discoverFromGitHub] DiscoveryOrchestrator returned empty or failed result: {}", 
                    discoveryResult.getErrorMessage());
            } catch (Exception e) {
                log.error("[discoverFromGitHub] DiscoveryOrchestrator GitHub discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (useSeSdk && skillPackageManager != null) {
            log.info("[discoverFromGitHub] Using SE SDK SkillPackageManager for GitHub");
            try {
                CompletableFuture<List<SkillPackage>> future = skillPackageManager.discoverAll(DiscoveryMethod.GITHUB);
                List<SkillPackage> packages = future.get(60, TimeUnit.SECONDS);
                log.info("[discoverFromGitHub] Discovered {} skills from GitHub via SE SDK", packages.size());
                
                for (SkillPackage pkg : packages) {
                    capabilities.add(convertSkillPackageToCapabilityDTO(pkg));
                }
                
                result.setCapabilities(capabilities);
                result.setTotal(capabilities.size());
                result.setTimestamp(System.currentTimeMillis());
                
                return ResultModel.success(result);
            } catch (Exception e) {
                log.error("[discoverFromGitHub] SE SDK GitHub discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (capabilities.isEmpty()) {
            log.error("[discoverFromGitHub] No capabilities found from any source");
            result.setErrorMessage("无法从 GitHub 获取技能数据，请检查网络连接和配置");
            return ResultModel.error("无法从 GitHub 获取技能数据: " + result.getErrorMessage());
        }
        
        result.setCapabilities(capabilities);
        result.setTotal(capabilities.size());
        result.setTimestamp(System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/gitee")
    public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(@RequestBody(required = false) GitDiscoveryRequestDTO request) {
        log.info("[discoverFromGitee] Starting Gitee discovery");
        
        GitDiscoveryResultDTO result = new GitDiscoveryResultDTO();
        List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> capabilities = new ArrayList<>();
        
        String repoUrl = request != null && request.getRepoUrl() != null ? request.getRepoUrl() : "https://gitee.com/ooderCN/skills";
        String branch = request != null && request.getBranch() != null ? request.getBranch() : "master";
        
        result.setRepoUrl(repoUrl);
        result.setBranch(branch);
        result.setSource("gitee");
        
        if (discoveryCoordinator != null) {
            log.info("[discoverFromGitee] Using DiscoveryCoordinator from scene-engine");
            try {
                CompletableFuture<List<net.ooder.scene.skill.model.RichSkill>> future = 
                    discoveryCoordinator.discover("GITEE");
                List<net.ooder.scene.skill.model.RichSkill> skills = future.get(60, TimeUnit.SECONDS);
                log.info("[discoverFromGitee] DiscoveryCoordinator discovered {} skills from Gitee", skills.size());
                
                if (skills != null && !skills.isEmpty()) {
                    for (net.ooder.scene.skill.model.RichSkill skill : skills) {
                        capabilities.add(convertRichSkillToCapabilityDTO(skill));
                    }
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setTimestamp(System.currentTimeMillis());
                    return ResultModel.success(result);
                }
                log.warn("[discoverFromGitee] DiscoveryCoordinator returned empty result, trying fallback");
            } catch (Exception e) {
                log.error("[discoverFromGitee] DiscoveryCoordinator Gitee discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (discoveryOrchestrator != null) {
            log.info("[discoverFromGitee] Using DiscoveryOrchestrator for Gitee");
            try {
                DiscoveryResult discoveryResult = discoveryOrchestrator.discoverFromGitee(repoUrl, branch);
                log.info("[discoverFromGitee] DiscoveryOrchestrator result: success={}, total={}", 
                    discoveryResult.isSuccess(), discoveryResult.getTotalCount());
                
                if (discoveryResult.isSuccess() && discoveryResult.getCapabilities() != null) {
                    for (CapabilityDTO cap : discoveryResult.getCapabilities()) {
                        capabilities.add(convertToDiscoveryCapabilityDTO(cap));
                    }
                    
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setTimestamp(System.currentTimeMillis());
                    return ResultModel.success(result);
                }
                log.warn("[discoverFromGitee] DiscoveryOrchestrator returned empty or failed result: {}", 
                    discoveryResult.getErrorMessage());
            } catch (Exception e) {
                log.error("[discoverFromGitee] DiscoveryOrchestrator Gitee discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (useSeSdk && skillPackageManager != null) {
            log.info("[discoverFromGitee] Using SE SDK SkillPackageManager for Gitee");
            try {
                CompletableFuture<List<SkillPackage>> future = skillPackageManager.discoverAll(DiscoveryMethod.GITEE);
                List<SkillPackage> packages = future.get(60, TimeUnit.SECONDS);
                log.info("[discoverFromGitee] Discovered {} skills from Gitee via SE SDK", packages.size());
                
                // 验证 packages 列表
                if (packages == null || packages.isEmpty()) {
                    log.warn("[discoverFromGitee] Packages list is null or empty");
                } else {
                    log.info("[discoverFromGitee] First package: {}", packages.get(0).getSkillId());
                    log.info("[discoverFromGitee] Starting conversion of {} packages", packages.size());
                }
                
                int successCount = 0;
                int failCount = 0;
                int duplicateCount = 0;
                int filteredCount = 0;
                java.util.Map<String, net.ooder.skill.discovery.dto.discovery.CapabilityDTO> dedupMap = new java.util.LinkedHashMap<>();
                for (int i = 0; i < packages.size(); i++) {
                    SkillPackage pkg = packages.get(i);
                    try {
                        log.info("[discoverFromGitee] Processing package {}/{}: {}", i + 1, packages.size(), pkg.getSkillId());
                        net.ooder.skill.discovery.dto.discovery.CapabilityDTO dto = convertSkillPackageToCapabilityDTO(pkg);
                        String skillId = dto.getId();
                        if (dedupMap.containsKey(skillId)) {
                            net.ooder.skill.discovery.dto.discovery.CapabilityDTO existing = dedupMap.get(skillId);
                            if ("SCENE".equals(existing.getSkillForm()) && !"SCENE".equals(dto.getSkillForm())) {
                                log.info("[discoverFromGitee] Skipping duplicate {} (keeping SCENE version, ignoring PROVIDER)", skillId);
                                duplicateCount++;
                                continue;
                            } else if (!"SCENE".equals(existing.getSkillForm()) && "SCENE".equals(dto.getSkillForm())) {
                                log.info("[discoverFromGitee] Replacing {} with SCENE version", skillId);
                                dedupMap.put(skillId, dto);
                                duplicateCount++;
                                continue;
                            } else {
                                log.info("[discoverFromGitee] Keeping first occurrence of {}", skillId);
                                duplicateCount++;
                                continue;
                            }
                        }
                        dedupMap.put(skillId, dto);
                        successCount++;
                        log.info("[discoverFromGitee] Successfully converted package {}: category={}, skillForm={}",
                            pkg.getSkillId(), dto.getCategory(), dto.getSkillForm());
                    } catch (Exception e) {
                        failCount++;
                        log.error("[discoverFromGitee] Failed to convert package {}: {}", pkg.getSkillId(), e.getMessage(), e);
                    }
                }
                capabilities.addAll(dedupMap.values());

                java.util.Set<String> userFacingCategories = java.util.Set.of("llm", "knowledge", "biz", "util");
                capabilities.removeIf(cap -> {
                    String skillForm = cap.getSkillForm();
                    String category = cap.getCategory();
                    if ("SCENE".equals(skillForm) || "DRIVER".equals(skillForm)) {
                        return false;
                    }
                    if ("PROVIDER".equals(skillForm) || "SKILL".equals(skillForm)) {
                        // 如果 category 为 null，则过滤掉
                        if (category == null) {
                            return true;
                        }
                        return !userFacingCategories.contains(category);
                    }
                    return true;
                });
                filteredCount = dedupMap.size() - capabilities.size();

                log.info("[discoverFromGitee] Conversion complete: success={}, duplicate={}, filtered={}, failed={}, total={}",
                    successCount, duplicateCount, filteredCount, failCount, capabilities.size());
                
                if (!capabilities.isEmpty()) {
                    result.setCapabilities(capabilities);
                    result.setTotal(capabilities.size());
                    result.setTimestamp(System.currentTimeMillis());
                    return ResultModel.success(result);
                }
                log.warn("[discoverFromGitee] SE SDK Gitee discovery returned empty result, trying next source");
            } catch (Exception e) {
                log.error("[discoverFromGitee] SE SDK Gitee discovery failed: {}", e.getMessage(), e);
                result.setErrorMessage(e.getMessage());
            }
        }
        
        if (capabilities.isEmpty()) {
            log.error("[discoverFromGitee] No capabilities found from any source");
            result.setErrorMessage("无法从 Gitee 获取技能数据，请检查网络连接和配置");
            return ResultModel.error("无法从 Gitee 获取技能数据: " + result.getErrorMessage());
        }
        
        result.setCapabilities(capabilities);
        result.setTotal(capabilities.size());
        result.setTimestamp(System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/categories/stats")
    public ResultModel<CategoryStatsDTO> getCategoryStats() {
        log.info("[getCategoryStats] Getting category statistics");
        
        if (skillRegistry != null) {
            try {
                List<InstalledSkill> skills = skillRegistry.getInstalledSkills();
                Map<String, Long> categoryCount = new HashMap<>();
                long installed = 0;
                long sceneCount = 0;
                long providerCount = 0;
                long driverCount = 0;
                
                for (InstalledSkill skill : skills) {
                    String category = skill.getSceneId() != null ? "scene" : "provider";
                    categoryCount.merge(category, 1L, Long::sum);
                    installed++;
                    
                    String skillForm = skill.getSkillForm();
                    if (skillForm == null || skillForm.isEmpty()) {
                        skillForm = "PROVIDER";
                    }
                    if ("SCENE".equals(skillForm)) {
                        sceneCount++;
                    } else if ("DRIVER".equals(skillForm)) {
                        driverCount++;
                    } else {
                        providerCount++;
                    }
                }
                
                CategoryStatsDTO stats = new CategoryStatsDTO();
                stats.setTotal(skills.size());
                stats.setCategories(categoryCount);
                stats.setInstalled(installed);
                stats.setNotInstalled(0L);
                stats.setSceneCount(sceneCount);
                stats.setProviderCount(providerCount);
                stats.setDriverCount(driverCount);
                
                return ResultModel.success(stats);
            } catch (Throwable e) {
                log.error("[getCategoryStats] Failed to get stats: {}", e.getMessage());
            }
        }
        
        CategoryStatsDTO emptyStats = new CategoryStatsDTO();
        emptyStats.setTotal(0);
        emptyStats.setMessage("No statistics available");
        return ResultModel.success(emptyStats);
    }
    
    @GetMapping("/categories/user-facing")
    public ResultModel<List<Map<String, Object>>> getUserFacingCategories() {
        log.info("[getUserFacingCategories] Getting user facing categories");
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[][] userCategories = {
            {"llm", "大模型", "ri-robot-line", "#6366f1"},
            {"knowledge", "知识库", "ri-book-2-line", "#10b981"},
            {"biz", "业务场景", "ri-briefcase-line", "#f59e0b"},
            {"org", "组织管理", "ri-team-line", "#3b82f6"},
            {"msg", "消息通知", "ri-message-3-line", "#ef4444"},
            {"vfs", "文件存储", "ri-folder-line", "#8b5cf6"},
            {"ui", "界面组件", "ri-layout-line", "#06b6d4"},
            {"sys", "系统管理", "ri-settings-3-line", "#64748b"}
        };
        
        for (String[] cat : userCategories) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", cat[0]);
            category.put("name", cat[1]);
            category.put("icon", cat[2]);
            category.put("color", cat[3]);
            category.put("userFacing", true);
            categories.add(category);
        }
        
        return ResultModel.success(categories);
    }
    
    @GetMapping("/categories/all")
    public ResultModel<List<Map<String, Object>>> getAllCategories() {
        log.info("[getAllCategories] Getting all categories with stats");
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[][] allCategories = {
            {"llm", "大模型", "ri-robot-line", "#6366f1"},
            {"knowledge", "知识库", "ri-book-2-line", "#10b981"},
            {"biz", "业务场景", "ri-briefcase-line", "#f59e0b"},
            {"org", "组织管理", "ri-team-line", "#3b82f6"},
            {"msg", "消息通知", "ri-message-3-line", "#ef4444"},
            {"vfs", "文件存储", "ri-folder-line", "#8b5cf6"},
            {"ui", "界面组件", "ri-layout-line", "#06b6d4"},
            {"sys", "系统管理", "ri-settings-3-line", "#64748b"},
            {"payment", "支付", "ri-bank-card-line", "#22c55e"},
            {"media", "媒体", "ri-video-line", "#ec4899"},
            {"util", "工具", "ri-tools-line", "#78716c"}
        };
        
        for (String[] cat : allCategories) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", cat[0]);
            category.put("name", cat[1]);
            category.put("icon", cat[2]);
            category.put("color", cat[3]);
            category.put("count", 0);
            categories.add(category);
        }
        
        return ResultModel.success(categories);
    }
    
    @GetMapping("/categories/{categoryId}/subcategories")
    public ResultModel<List<Map<String, Object>>> getSubCategories(@PathVariable String categoryId) {
        log.info("[getSubCategories] Getting subcategories for: {}", categoryId);
        List<Map<String, Object>> subCategories = new ArrayList<>();
        
        return ResultModel.success(subCategories);
    }
    
    @GetMapping("/capability/{capabilityId}")
    public ResultModel<CapabilityDetailDTO> getCapabilityDetail(@PathVariable String capabilityId) {
        log.info("[getCapabilityDetail] Getting detail for: {}", capabilityId);
        
        CapabilityDetailDTO detail = new CapabilityDetailDTO();
        detail.setId(capabilityId);
        detail.setSkillId(capabilityId);
        detail.setName(capabilityId);
        detail.setInstalled(false);
        
        if (skillRegistry != null) {
            try {
                List<InstalledSkill> skills = skillRegistry.getInstalledSkills();
                for (InstalledSkill skill : skills) {
                    if (capabilityId.equals(skill.getSkillId())) {
                        detail.setName(skill.getName());
                        detail.setInstalled(true);
                        detail.setSkillForm(skill.getSkillForm());
                        detail.setSceneType(skill.getSceneId());
                        break;
                    }
                }
            } catch (Throwable e) {
                log.error("[getCapabilityDetail] Failed to get detail: {}", e.getMessage());
            }
        }
        
        return ResultModel.success(detail);
    }
    
    @PostMapping("/sync")
    public ResultModel<SyncResultDTO> syncFromSkills() {
        log.info("[syncFromSkills] Starting manual sync");
        
        SyncResultDTO result = new SyncResultDTO();
        
        if (discoveryCoordinator != null) {
            try {
                CompletableFuture<List<net.ooder.scene.skill.model.RichSkill>> future = 
                    discoveryCoordinator.discover("LOCAL");
                List<net.ooder.scene.skill.model.RichSkill> skills = future.get(30, TimeUnit.SECONDS);
                log.info("[syncFromSkills] DiscoveryCoordinator discovered {} skills", skills.size());
                result.setSynced(skills.size());
                result.setSkipped(0);
                result.setErrors(0);
                result.setTimestamp(System.currentTimeMillis());
                result.setMessage(String.format("同步完成: 成功 %d", skills.size()));
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[syncFromSkills] DiscoveryCoordinator sync failed: {}", e.getMessage());
            }
        }
        
        if (discoveryOrchestrator != null) {
            try {
                discoveryOrchestrator.reloadIndex();
                DiscoveryResult discoveryResult = discoveryOrchestrator.discoverLocal();
                result.setSynced(discoveryResult.getTotalCount());
                result.setSkipped(0);
                result.setErrors(0);
                result.setTimestamp(System.currentTimeMillis());
                result.setMessage(String.format("同步完成: 成功 %d", discoveryResult.getTotalCount()));
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[syncFromSkills] Failed to sync: {}", e.getMessage());
                result.setErrors(1);
                result.setMessage("同步失败: " + e.getMessage());
            }
        }
        
        if (skillDiscoverer != null) {
            try {
                CompletableFuture<List<SkillPackage>> future = skillDiscoverer.discover();
                List<SkillPackage> packages = future.get(30, TimeUnit.SECONDS);
                result.setSynced(packages.size());
                result.setSkipped(0);
                result.setErrors(0);
                result.setTimestamp(System.currentTimeMillis());
                result.setMessage(String.format("同步完成: 成功 %d", packages.size()));
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[syncFromSkills] Failed to sync from SE SDK: {}", e.getMessage());
                result.setErrors(1);
                result.setMessage("同步失败: " + e.getMessage());
            }
        }
        
        result.setTimestamp(System.currentTimeMillis());
        return ResultModel.success(result);
    }
    
    @GetMapping("/methods")
    public ResultModel<List<DiscoveryMethodDTO>> getDiscoveryMethods() {
        log.info("[getDiscoveryMethods] Getting available discovery methods");
        
        List<DiscoveryMethodDTO> methods = new ArrayList<>();
        
        methods.add(new DiscoveryMethodDTO("GITEE", "Gitee仓库", "ri-git-repository-line",
            "从Gitee仓库发现能力", "#ef4444", true));
        methods.add(new DiscoveryMethodDTO("GITHUB", "GitHub仓库", "ri-github-fill",
            "从GitHub仓库发现能力", "#6366f1", true));
        methods.add(new DiscoveryMethodDTO("LOCAL_FS", "本地文件系统", "ri-folder-line", 
            "扫描本地已安装的能力包", "#3b82f6", false));
        methods.add(new DiscoveryMethodDTO("SKILL_CENTER", "能力中心", "ri-cloud-line",
            "从能力中心发现可用能力", "#10b981", true));
        methods.add(new DiscoveryMethodDTO("GIT_REPOSITORY", "Git仓库", "ri-git-branch-line",
            "从任意Git仓库发现能力", "#f59e0b", true));
        methods.add(new DiscoveryMethodDTO("AUTO", "自动检测", "ri-magic-line",
            "自动选择最佳发现方式", "#64748b", false));
        
        for (DiscoveryMethodDTO method : methods) {
            method.setConfigFields(getConfigFields(method.getId()));
        }
        
        return ResultModel.success(methods);
    }

    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getConfig() {
        log.info("[getConfig] Getting discovery config");
        Map<String, Object> config = new HashMap<>();
        config.put("autoScan", false);
        config.put("scanInterval", 3600);
        config.put("sources", Arrays.asList("local", "github", "gitee"));
        return ResultModel.success(config);
    }

    @PutMapping("/config")
    public ResultModel<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> config) {
        log.info("[updateConfig] Updating discovery config");
        return ResultModel.success(config);
    }
    
    @PostMapping("/install")
    public ResultModel<InstallResultDTO> installCapability(@RequestBody InstallSkillRequestDTO request) {
        String skillId = request.getSkillId();
        String source = request.getSource() != null ? request.getSource() : "local";
        
        log.info("[installCapability] Installing skill: {} from {}, type: {}", skillId, source, request.getType());
        
        InstallResultDTO result = new InstallResultDTO();
        result.setSkillId(skillId);
        result.setCapabilityId(skillId);
        result.setInstallSource(source);

        if (skillPackageManager == null) {
            log.error("[installCapability] SkillPackageManager not available");
            result.setStatus("failed");
            result.setMessage("SkillPackageManager 不可用，请检查 skill-hotplug-starter 配置");
            return ResultModel.error("SkillPackageManager 不可用，无法安装 skill");
        }

        try {
            CompletableFuture<Boolean> isInstalledFuture = skillPackageManager.isInstalled(skillId);
            Boolean isInstalled = isInstalledFuture.get(10, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(isInstalled)) {
                log.info("[installCapability] Skill already installed: {}", skillId);
                result.setStatus("installed");
                result.setMessage("已安装，跳过");
                result.setInstallTime(System.currentTimeMillis());
                return ResultModel.success(result);
            }

            CompletableFuture<InstallResultWithDependencies> installFuture =
                skillPackageManager.installWithDependencies(skillId, InstallRequest.InstallMode.FULL_INSTALL);

            InstallResultWithDependencies installResult = installFuture.get(120, TimeUnit.SECONDS);

            if (installResult != null && installResult.isSuccess()) {
                log.info("[installCapability] Successfully installed: {}", skillId);
                result.setStatus("installed");
                result.setMessage("安装成功");
                result.setInstallTime(System.currentTimeMillis());
                if (installResult.getInstalledDependencies() != null) {
                    result.setInstalledDependencies(installResult.getInstalledDependencies());
                }
                return ResultModel.success(result);
            } else {
                String error = installResult != null ? installResult.getError() : "未知错误";
                log.error("[installCapability] Failed to install {}: {}", skillId, error);
                result.setStatus("failed");
                result.setMessage("安装失败: " + error);
                return ResultModel.error("安装失败: " + error);
            }
        } catch (Exception e) {
            log.error("[installCapability] Error installing {}: {}", skillId, e.getMessage(), e);
            result.setStatus("failed");
            result.setMessage("安装异常: " + e.getMessage());
            return ResultModel.error("安装异常: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResultModel<RefreshResultDTO> refreshDiscovery() {
        log.info("[refreshDiscovery] Refreshing discovery");
        
        RefreshResultDTO result = new RefreshResultDTO();
        
        if (discoveryCoordinator != null) {
            try {
                CompletableFuture<List<net.ooder.scene.skill.model.RichSkill>> future = 
                    discoveryCoordinator.discover("LOCAL");
                List<net.ooder.scene.skill.model.RichSkill> skills = future.get(30, TimeUnit.SECONDS);
                log.info("[refreshDiscovery] DiscoveryCoordinator discovered {} skills", skills.size());
                result.setDiscovered(skills.size());
                result.setSource("discovery-coordinator");
                result.setTimestamp(System.currentTimeMillis());
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[refreshDiscovery] DiscoveryCoordinator failed: {}", e.getMessage());
                result.setError(e.getMessage());
            }
        }
        
        if (discoveryOrchestrator != null) {
            try {
                discoveryOrchestrator.reloadIndex();
                DiscoveryResult discoveryResult = discoveryOrchestrator.discoverLocal();
                log.info("[refreshDiscovery] Discovered {} skills", discoveryResult.getTotalCount());
                result.setDiscovered(discoveryResult.getTotalCount());
                result.setSource("discovery-orchestrator");
                result.setTimestamp(System.currentTimeMillis());
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[refreshDiscovery] Failed to refresh: {}", e.getMessage());
                result.setError(e.getMessage());
            }
        }
        
        if (skillDiscoverer != null) {
            try {
                CompletableFuture<List<SkillPackage>> future = skillDiscoverer.discover();
                List<SkillPackage> packages = future.get(30, TimeUnit.SECONDS);
                log.info("[refreshDiscovery] Discovered {} skills from SE SDK", packages.size());
                result.setDiscovered(packages.size());
                result.setSource("se-sdk");
                result.setTimestamp(System.currentTimeMillis());
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[refreshDiscovery] Failed to refresh from SE SDK: {}", e.getMessage());
                result.setError(e.getMessage());
            }
        }
        
        if (skillRegistry != null) {
            try {
                List<InstalledSkill> skills = skillRegistry.getInstalledSkills();
                log.info("[refreshDiscovery] Found {} installed skills", skills.size());
                result.setRegistered(skills.size());
                result.setSource("se-registry");
                result.setTimestamp(System.currentTimeMillis());
                return ResultModel.success(result);
            } catch (Throwable e) {
                log.error("[refreshDiscovery] Failed to get from registry: {}", e.getMessage());
                result.setError(e.getMessage());
            }
        }
        
        result.setTimestamp(System.currentTimeMillis());
        return ResultModel.success(result);
    }
    
    private List<ConfigFieldDTO> getConfigFields(String methodId) {
        List<ConfigFieldDTO> fields = new ArrayList<>();
        
        switch (methodId) {
            case "GITHUB":
                fields.add(new ConfigFieldDTO("token", "GitHub Token", "password", "GitHub Personal Access Token"));
                fields.add(new ConfigFieldDTO("owner", "仓库所有者", "text", "ooderCN"));
                fields.add(new ConfigFieldDTO("repo", "仓库名称", "text", "skills"));
                break;
            case "GITEE":
                fields.add(new ConfigFieldDTO("token", "Gitee Token", "password", "Gitee 私人令牌"));
                fields.add(new ConfigFieldDTO("owner", "仓库所有者", "text", "ooderCN"));
                fields.add(new ConfigFieldDTO("repo", "仓库名称", "text", "skills"));
                fields.add(new ConfigFieldDTO("branch", "分支", "text", "master"));
                break;
            case "SKILL_CENTER":
                fields.add(new ConfigFieldDTO("url", "能力中心地址", "text", "https://skill.ooder.net"));
                fields.add(new ConfigFieldDTO("token", "访问令牌", "password", "可选"));
                break;
            case "GIT_REPOSITORY":
                fields.add(new ConfigFieldDTO("repoUrl", "仓库地址", "text", "https://github.com/user/repo.git"));
                fields.add(new ConfigFieldDTO("branch", "分支", "text", "main"));
                fields.add(new ConfigFieldDTO("token", "访问令牌", "password", "可选"));
                break;
            default:
                break;
        }
        
        return fields;
    }

    private net.ooder.skill.discovery.dto.discovery.CapabilityDTO convertToDiscoveryCapabilityDTO(CapabilityDTO cap) {
        net.ooder.skill.discovery.dto.discovery.CapabilityDTO dto = new net.ooder.skill.discovery.dto.discovery.CapabilityDTO();
        dto.setId(cap.getId());
        dto.setName(cap.getName());
        dto.setVersion(cap.getVersion());
        dto.setSkillId(cap.getSkillId());
        dto.setSceneType(cap.getSceneType());
        dto.setDescription(cap.getDescription());
        dto.setTags(cap.getTags());
        dto.setStatus(cap.getStatus());
        dto.setSkillForm(cap.getSkillForm());
        dto.setSceneCapability(cap.isSceneCapability());
        dto.setCategory(cap.getCategory());
        dto.setCapabilityCategory(cap.getCategory());
        dto.setBusinessCategory(cap.getCategory());
        if (cap.getInstalled() != null) {
            dto.setInstalled(cap.getInstalled());
        }
        return dto;
    }

    private net.ooder.skill.discovery.dto.discovery.CapabilityDTO convertRichSkillToCapabilityDTO(net.ooder.scene.skill.model.RichSkill skill) {
        net.ooder.skill.discovery.dto.discovery.CapabilityDTO dto = new net.ooder.skill.discovery.dto.discovery.CapabilityDTO();
        dto.setId(skill.getSkillId());
        dto.setName(skill.getName());
        dto.setVersion(skill.getVersion());
        dto.setSkillId(skill.getSkillId());
        dto.setDescription(skill.getDescription());
        dto.setStatus("available");
        dto.setInstalled(false);
        
        // 使用 SDK 提供的推断值
        String category = skill.getCategory() != null ? skill.getCategory().getCode() : null;
        String skillForm = skill.getForm() != null ? skill.getForm().name() : null;
        
        dto.setCategory(category);
        dto.setCapabilityCategory(category);
        dto.setBusinessCategory(category);
        dto.setSkillForm(skillForm);
        dto.setSceneCapability("SCENE".equals(skillForm));
        dto.setType("SCENE".equals(skillForm) ? "SCENE" : "SKILL");
        
        return dto;
    }

    private net.ooder.skill.discovery.dto.discovery.CapabilityDTO convertSkillPackageToCapabilityDTO(SkillPackage pkg) {
        log.debug("[convertSkillPackageToCapabilityDTO] Converting package: {}", pkg.getSkillId());
        
        // 使用 SDK 提供的 DiscoveryConverter 进行转换
        net.ooder.skill.discovery.dto.discovery.CapabilityDTO dto = DiscoveryConverter.toCapabilityDTO(pkg);
        
        // 检查安装状态
        boolean installed = checkIfInstalled(pkg.getSkillId());
        dto.setInstalled(installed);
        dto.setStatus(installed ? "installed" : "available");
        
        // SDK 3.0.1+ 已经自动推断 category 和 skillForm，无需 OS 层再次推断
        // 直接使用 SDK 返回的值
        String category = dto.getCategory();
        String skillForm = dto.getSkillForm();
        
        log.debug("[convertSkillPackageToCapabilityDTO] Package {}: category={}, skillForm={}", 
            pkg.getSkillId(), category, skillForm);
        
        // 确保所有相关字段一致
        dto.setCapabilityCategory(category);
        dto.setBusinessCategory(category);
        
        boolean isScene = "SCENE".equals(skillForm);
        dto.setSceneCapability(isScene);
        dto.setType(isScene ? "SCENE" : "SKILL");
        
        return dto;
    }
    
    private net.ooder.skill.discovery.dto.discovery.CapabilityDTO convertInstalledSkillToCapabilityDTO(InstalledSkill skill) {
        net.ooder.skill.discovery.dto.discovery.CapabilityDTO dto = new net.ooder.skill.discovery.dto.discovery.CapabilityDTO();
        dto.setId(skill.getSkillId());
        dto.setName(skill.getName());
        dto.setVersion(skill.getVersion());
        dto.setSkillId(skill.getSkillId());
        dto.setSceneType(skill.getSceneId());
        dto.setStatus(skill.getStatus());
        dto.setDependencies(skill.getDependencies());
        dto.setInstalled(true);
        
        // 使用 SDK 提供的推断值
        String category = skill.getCategory();
        String skillForm = skill.getSkillForm();
        
        dto.setCategory(category);
        dto.setCapabilityCategory(category);
        dto.setBusinessCategory(category);
        dto.setSkillForm(skillForm);
        
        boolean isScene = "SCENE".equals(skillForm);
        dto.setSceneCapability(isScene);
        dto.setType(isScene ? "SCENE" : "SKILL");
        
        return dto;
    }
    
    private boolean checkIfInstalled(String skillId) {
        if (skillPackageManager == null || skillId == null) {
            return false;
        }
        try {
            CompletableFuture<Boolean> future = skillPackageManager.isInstalled(skillId);
            return Boolean.TRUE.equals(future.get(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.debug("[checkIfInstalled] Could not check install status for {}: {}", skillId, e.getMessage());
            return false;
        }
    }
    
    private List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> filterInternalCapabilities(
            List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> capabilities) {
        if (capabilities == null || capabilities.isEmpty()) {
            return capabilities;
        }
        
        int originalSize = capabilities.size();
        List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> filtered = new ArrayList<>();
        
        for (net.ooder.skill.discovery.dto.discovery.CapabilityDTO cap : capabilities) {
            if (isInternalCapability(cap)) {
                log.debug("[filterInternalCapabilities] Filtering out internal capability: {}", cap.getSkillId());
                continue;
            }
            filtered.add(cap);
        }
        
        int filteredCount = originalSize - filtered.size();
        if (filteredCount > 0) {
            log.info("[filterInternalCapabilities] Filtered {} internal capabilities, remaining {}", 
                filteredCount, filtered.size());
        }
        
        return filtered;
    }
    
    private boolean isInternalCapability(net.ooder.skill.discovery.dto.discovery.CapabilityDTO cap) {
        if (cap == null) {
            return false;
        }
        
        if ("internal".equals(cap.getVisibility())) {
            return true;
        }
        
        if ("INTERNAL".equals(cap.getSkillForm())) {
            return true;
        }
        
        if ("SCENE_INTERNAL".equals(cap.getOwnership())) {
            return true;
        }
        
        String skillId = cap.getSkillId();
        if (skillId != null) {
            if (skillId.startsWith("skill-") && !isUserFacingSkill(cap)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isUserFacingSkill(net.ooder.skill.discovery.dto.discovery.CapabilityDTO cap) {
        String category = cap.getCategory();
        if (category == null) {
            return false;
        }
        
        String categoryLower = category.toLowerCase();
        return "llm".equals(categoryLower) 
            || "knowledge".equals(categoryLower)
            || "biz".equals(categoryLower)
            || "scene".equals(categoryLower);
    }

    @GetMapping("/capabilities")
    public ResultModel<PageResult<net.ooder.skill.discovery.dto.discovery.CapabilityDTO>> discoverCapabilities(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        log.info("[discoverCapabilities] Discovering capabilities - pageNum: {}, pageSize: {}", pageNum, pageSize);
        
        List<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> capabilities = new ArrayList<>();
        
        PageResult<net.ooder.skill.discovery.dto.discovery.CapabilityDTO> result = new PageResult<>();
        result.setList(capabilities);
        result.setTotal(0);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return ResultModel.success(result);
    }

    @GetMapping("/capabilities/types")
    public ResultModel<List<Map<String, Object>>> getCapabilityTypes() {
        log.info("[getCapabilityTypes] Getting capability types");
        
        List<Map<String, Object>> types = new ArrayList<>();
        
        Map<String, Object> type1 = new HashMap<>();
        type1.put("id", "skill");
        type1.put("name", "技能");
        type1.put("description", "可安装的功能模块");
        types.add(type1);
        
        Map<String, Object> type2 = new HashMap<>();
        type2.put("id", "driver");
        type2.put("name", "驱动");
        type2.put("description", "外部系统集成驱动");
        types.add(type2);
        
        Map<String, Object> type3 = new HashMap<>();
        type3.put("id", "connector");
        type3.put("name", "连接器");
        type3.put("description", "数据源连接器");
        types.add(type3);
        
        return ResultModel.success(types);
    }
}
