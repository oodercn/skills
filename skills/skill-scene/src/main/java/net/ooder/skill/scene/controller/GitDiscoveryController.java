package net.ooder.skill.scene.controller;

import javax.validation.Valid;
import net.ooder.skill.scene.discovery.SkillIndexLoader;
import net.ooder.skill.scene.dto.discovery.*;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.capability.model.SceneType;
import net.ooder.skill.scene.capability.model.SkillForm;
import net.ooder.skill.scene.capability.model.CapabilityCategory;
import net.ooder.skill.scene.capability.model.Visibility;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.service.UnifiedSceneService;
import net.ooder.sdk.discovery.git.GitHubDiscoverer;
import net.ooder.sdk.discovery.git.GiteeDiscoverer;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.skills.api.InstallResult;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.SkillPackageManager;
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
public class GitDiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(GitDiscoveryController.class);

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Value("${ooder.gitee.token:}")
    private String defaultGiteeToken;

    @Value("${ooder.github.token:}")
    private String defaultGithubToken;
    
    @Value("${ooder.discovery.use-index-first:true}")
    private boolean useIndexFirst;

    @Autowired(required = false)
    private UnifiedSceneService unifiedSceneService;

    @Autowired(required = false)
    private GitHubDiscoverer gitHubDiscoverer;

    @Autowired(required = false)
    private GiteeDiscoverer giteeDiscoverer;

    @Autowired(required = false)
    private SkillService skillService;

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    @Autowired(required = false)
    private net.ooder.skill.scene.capability.service.CapabilityService capabilityService;
    
    @Autowired
    private SkillIndexLoader skillIndexLoader;
    
    @Autowired
    private net.ooder.skill.scene.capability.service.CapabilityClassificationService classificationService;
    
    @Autowired
    private net.ooder.skill.scene.capability.service.SkillStatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResultModel<SkillStatisticsDTO> getStatistics() {
        log.info("[getStatistics] Getting skill statistics");
        SkillStatisticsDTO stats = statisticsService.getStatistics();
        return ResultModel.success(stats);
    }

    @PostMapping("/github")
    public ResultModel<DiscoveryResultDTO> discoverFromGitHub(@RequestBody @Valid GitDiscoveryConfigDTO config) {
        log.info("[discoverFromGitHub] repoUrl: {}, branch: {}, mockEnabled: {}, useIndexFirst: {}", 
            config.getRepoUrl(), config.getBranch(), mockEnabled, useIndexFirst);
        
        DiscoveryResultDTO result = new DiscoveryResultDTO();
        result.setMethod("GITHUB");
        result.setRepoUrl(config.getRepoUrl());
        result.setBranch(config.getBranch());
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        String errorMessage = null;
        boolean fromCache = false;
        
        if (useIndexFirst && skillIndexLoader != null) {
            log.info("[discoverFromGitHub] Using skill-index.yaml (useIndexFirst=true)");
            capabilities = skillIndexLoader.getSkillsFromIndex("GITHUB");
            fromCache = true;
            log.info("[discoverFromGitHub] Found {} capabilities from skill-index.yaml", capabilities.size());
        }
        
        if (capabilities.isEmpty() && gitHubDiscoverer != null && config.getRepoUrl() != null && !config.getRepoUrl().isEmpty()) {
            try {
                String[] parts = parseRepoUrl(config.getRepoUrl());
                if (parts != null) {
                    List<SkillPackage> packages = gitHubDiscoverer.discoverSkills(parts[0], parts[1]).get();
                    capabilities = convertToCapabilities(packages, "GITHUB");
                    fromCache = false;
                    log.info("[discoverFromGitHub] Found {} capabilities from GitHub", capabilities.size());
                }
            } catch (Exception e) {
                log.error("[discoverFromGitHub] error: {}", e.getMessage());
                errorMessage = e.getMessage();
            }
        } else if (capabilities.isEmpty() && gitHubDiscoverer == null) {
            errorMessage = "GitHubDiscoverer not available";
        }
        
        if (capabilities.isEmpty() && mockEnabled) {
            log.info("[discoverFromGitHub] Using mock data (mockEnabled=true)");
            capabilities = getMockGitHubCapabilities(config.getRepoUrl());
            fromCache = false;
        } else if (capabilities.isEmpty()) {
            log.warn("[discoverFromGitHub] No capabilities found, mock disabled. Error: {}", errorMessage);
            result.setErrorMessage(errorMessage != null ? errorMessage : "No capabilities found and mock is disabled");
        }
        
        result.setCapabilities(capabilities);
        result.setScanTime(System.currentTimeMillis());
        result.setFromCache(fromCache);
        
        return ResultModel.success(result);
    }

    @PostMapping("/gitee")
    public ResultModel<DiscoveryResultDTO> discoverFromGitee(@RequestBody @Valid GitDiscoveryConfigDTO config) {
        String effectiveToken = config.getToken() != null && !config.getToken().isEmpty() 
            ? config.getToken() : defaultGiteeToken;
        
        log.info("[discoverFromGitee] repoUrl: {}, branch: {}, token: {}, mockEnabled: {}, useIndexFirst: {}", 
            config.getRepoUrl(), config.getBranch(),
            effectiveToken != null ? "***" : "null", mockEnabled, useIndexFirst);
        
        log.info("[discoverFromGitee] unifiedSceneService: {}, giteeDiscoverer: {}, skillIndexLoader: {}", 
            unifiedSceneService != null ? "available" : "null", 
            giteeDiscoverer != null ? "available" : "null",
            skillIndexLoader != null ? "available" : "null");
        
        DiscoveryResultDTO result = new DiscoveryResultDTO();
        result.setMethod("GITEE");
        result.setRepoUrl(config.getRepoUrl());
        result.setBranch(config.getBranch());
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        String errorMessage = null;
        boolean fromCache = false;
        
        if (useIndexFirst && skillIndexLoader != null) {
            log.info("[discoverFromGitee] Using skill-index.yaml (useIndexFirst=true)");
            capabilities = skillIndexLoader.getAllCapabilities("GITEE");
            fromCache = true;
            log.info("[discoverFromGitee] Found {} capabilities (skills + scenes) from skill-index.yaml", capabilities.size());
        }
        
        if (capabilities.isEmpty() && unifiedSceneService != null) {
            try {
                String[] parts = parseRepoUrl(config.getRepoUrl());
                if (parts != null && parts.length >= 2) {
                    Map<String, Object> options = new HashMap<>();
                    if (effectiveToken != null && !effectiveToken.isEmpty()) {
                        options.put("token", effectiveToken);
                    }
                    if (config.getBranch() != null && !config.getBranch().isEmpty()) {
                        options.put("branch", config.getBranch());
                    }
                    
                    CompletableFuture<UnifiedSceneService.DiscoveryResult> future = 
                        unifiedSceneService.discoverSkills(parts[0], parts[1], options);
                    UnifiedSceneService.DiscoveryResult discoveryResult = future.get();
                    
                    if (discoveryResult != null && discoveryResult.getSkills() != null) {
                        for (UnifiedSceneService.SkillInfo skill : discoveryResult.getSkills()) {
                            CapabilityDTO cap = new CapabilityDTO();
                            cap.setId(skill.getSkillId());
                            cap.setName(skill.getName());
                            cap.setDescription(skill.getDescription());
                            cap.setVersion(skill.getVersion());
                            cap.setSource("GITEE");
                            cap.setStatus("available");
                            
                            String skillId = skill.getSkillId();
                            boolean isScene = skillId != null && 
                                (skillId.contains("-scene") || skillId.endsWith("-scene") || 
                                 "daily-log-scene".equals(skillId));
                            cap.setType(isScene ? "SCENE" : "SKILL");
                            cap.setSceneCapability(isScene);
                            
                            capabilities.add(cap);
                        }
                        fromCache = false;
                        log.info("[discoverFromGitee] Found {} capabilities via UnifiedSceneService", capabilities.size());
                    }
                }
            } catch (Exception e) {
                log.error("[discoverFromGitee] UnifiedSceneService error: {}", e.getMessage());
                errorMessage = e.getMessage();
            }
        } else if (capabilities.isEmpty() && giteeDiscoverer != null && config.getRepoUrl() != null && !config.getRepoUrl().isEmpty()){
            try {
                String[] parts = parseRepoUrl(config.getRepoUrl());
                if (parts != null) {
                    List<SkillPackage> packages = giteeDiscoverer.discoverSkills(parts[0]).get();
                    capabilities = convertToCapabilities(packages, "GITEE");
                    fromCache = false;
                    log.info("[discoverFromGitee] Found {} capabilities from Gitee", capabilities.size());
                }
            } catch (Exception e) {
                log.error("[discoverFromGitee] error: {}", e.getMessage());
                errorMessage = e.getMessage();
            }
        } else if (capabilities.isEmpty() && giteeDiscoverer == null && unifiedSceneService == null) {
            errorMessage = "Neither UnifiedSceneService nor GiteeDiscoverer available";
        }
        
        if (capabilities.isEmpty() && mockEnabled) {
            log.info("[discoverFromGitee] Using mock data (mockEnabled=true)");
            capabilities = getMockGiteeCapabilities(config.getRepoUrl());
            fromCache = false;
        } else if (capabilities.isEmpty()) {
            log.warn("[discoverFromGitee] No capabilities found, mock disabled. Error: {}", errorMessage);
            result.setErrorMessage(errorMessage != null ? errorMessage : "No capabilities found and mock is disabled");
        }
        
        result.setCapabilities(capabilities);
        result.setScanTime(System.currentTimeMillis());
        result.setFromCache(fromCache);
        
        return ResultModel.success(result);
    }

    @PostMapping("/git")
    public ResultModel<DiscoveryResultDTO> discoverFromGit(@RequestBody @Valid GitDiscoveryConfigDTO config) {
        log.info("[discoverFromGit] repoUrl: {}, branch: {}, mockEnabled: {}", config.getRepoUrl(), config.getBranch(), mockEnabled);
        
        DiscoveryResultDTO result = new DiscoveryResultDTO();
        result.setMethod("GIT_REPOSITORY");
        result.setRepoUrl(config.getRepoUrl());
        result.setBranch(config.getBranch());
        
        if (mockEnabled) {
            result.setCapabilities(getMockGitCapabilities(config.getRepoUrl()));
        } else {
            result.setCapabilities(new ArrayList<>());
            result.setErrorMessage("Git repository discovery not implemented and mock is disabled");
        }
        result.setScanTime(System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/github/search")
    public ResultModel<List<RepositoryDTO>> searchGitHub(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String topic) {
        
        log.info("[searchGitHub] keyword: {}, topic: {}, mockEnabled: {}", keyword, topic, mockEnabled);
        
        if (!mockEnabled) {
            log.warn("[searchGitHub] Search not implemented and mock is disabled");
            return ResultModel.success(new ArrayList<>());
        }
        
        List<RepositoryDTO> results = new ArrayList<>();
        
        results.add(createSkillRepo(
            "ooderCN/skill-daily-report",
            "日志汇报技能",
            "提供日志提醒、提交、汇总、分析能力，支持定时提醒、表单提交、数据汇总、AI分析等功能",
            "https://github.com/ooderCN/skill-daily-report",
            128, 45, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-notification",
            "通知技能",
            "邮件、短信、站内信通知能力，支持模板消息、批量发送、定时发送",
            "https://github.com/ooderCN/skill-notification",
            256, 89, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "会议管理技能",
            "会议预约、提醒、纪要能力，支持会议室预定、参会人员管理、会议记录生成",
            "https://github.com/ooderCN/skill-meeting",
            96, 32, "v2.3"
        ));
        
        return ResultModel.success(results);
    }

    @GetMapping("/gitee/search")
    public ResultModel<List<RepositoryDTO>> searchGitee(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String topic,
        @RequestParam(required = false) String token)
{
        
        log.info("[searchGitee] keyword: {}, topic: {}, mockEnabled: {}", keyword, topic, mockEnabled);
        
        if (!mockEnabled) {
            log.warn("[searchGitee] Search not implemented and mock is disabled");
            return ResultModel.success(new ArrayList<>());
        }
        
        List<RepositoryDTO> results = getDefaultGiteeRepos();
        
        return ResultModel.success(results);
    }

    @PostMapping("/install")
    public ResultModel<InstallResultDTO> installSkill(@RequestBody @Valid InstallSkillRequestDTO request) {
        log.info("[installSkill] skillId: {}, source: {}, selectedRole: {}, mockEnabled: {}", 
            request.getSkillId(), request.getSource(), request.getSelectedRole(), mockEnabled);
        
        log.info("[installSkill] Participants: leader={}, pushType={}, collaborators={}", 
            request.getParticipants() != null ? request.getParticipants().getLeader() : null,
            request.getParticipants() != null ? request.getParticipants().getPushType() : null,
            request.getParticipants() != null ? request.getParticipants().getCollaborators() : null);
        
        log.info("[installSkill] DriverConditions: {}", request.getDriverConditions());
        
        log.info("[installSkill] LLMConfig: provider={}, enableFunctionCall={}", 
            request.getLlmConfig() != null ? request.getLlmConfig().getProvider() : null,
            request.getLlmConfig() != null ? request.getLlmConfig().getEnableFunctionCall() : null);
        
        InstallResultDTO result = new InstallResultDTO();
        result.setSkillId(request.getSkillId());
        result.setInstallTime(System.currentTimeMillis());
        
        if (mockEnabled) {
            log.info("[installSkill] Mock mode enabled, returning success for {}", request.getSkillId());
            skillIndexLoader.markAsInstalled(request.getSkillId());
            result.setStatus("installed");
            result.setMessage("Skill installed successfully (mock mode)");
            result.setCapabilities(getCapabilitiesForSkill(request.getSkillId()));
            result.setSelectedRole(request.getSelectedRole());
            result.setDriverConditions(request.getDriverConditions());
            return ResultModel.success(result);
        }
        
        String downloadUrl = skillIndexLoader.getDownloadUrl(request.getSkillId());
        log.info("[installSkill] Download URL for {}: {}", request.getSkillId(), downloadUrl);
        
        if (skillPackageManager != null && downloadUrl != null) {
            try {
                InstallRequest installRequest = new InstallRequest();
                installRequest.setSkillId(request.getSkillId());
                installRequest.setDownloadUrl(downloadUrl);
                installRequest.setMode(InstallRequest.InstallMode.FULL_INSTALL);
                
                applyInstallConfig(installRequest, request);
                
                InstallResult installResult = skillPackageManager.install(installRequest).get();
                
                if (installResult != null && installResult.isSuccess()){
                    result.setStatus("installed");
                    result.setMessage("Skill installed successfully");
                    
                    SkillManifest manifest = skillPackageManager.getManifest(request.getSkillId()).get();
                    if (manifest != null) {
                        result.setCapabilities(convertManifestToCapabilities(manifest));
                    }
                    
                    result.setSelectedRole(request.getSelectedRole());
                    result.setDriverConditions(request.getDriverConditions());
                    
                    log.info("[installSkill] Skill {} installed successfully from URL", request.getSkillId());
                } else {
                    result.setStatus("failed");
                    result.setMessage(installResult != null ? installResult.getError() : "Unknown error");
                    log.warn("[installSkill] Failed to install skill {}: {}", 
                        request.getSkillId(), result.getMessage());
                }
            } catch (Exception e) {
                log.error("[installSkill] Error installing skill {}: {}", request.getSkillId(), e.getMessage());
                result.setStatus("error");
                result.setMessage(e.getMessage());
            }
        } else if (skillPackageManager != null) {
            try {
                InstallRequest.InstallMode mode = InstallRequest.InstallMode.FULL_INSTALL;
                InstallResultWithDependencies installResult = skillPackageManager
                    .installWithDependencies(request.getSkillId(), mode)
                    .get();
                
                if (installResult != null && installResult.isSuccess()){
                    result.setStatus(installResult.getStatus());
                    result.setMessage("Skill installed with " + 
                        installResult.getInstalledDependencies().size() + " dependencies");
                    
                    SkillManifest manifest = skillPackageManager.getManifest(request.getSkillId()).get();
                    if (manifest != null) {
                        result.setCapabilities(convertManifestToCapabilities(manifest));
                    }
                    
                    result.setInstalledDependencies(installResult.getInstalledDependencies());
                    result.setExistingDependencies(installResult.getExistingDependencies());
                    result.setSelectedRole(request.getSelectedRole());
                    result.setDriverConditions(request.getDriverConditions());
                    
                    log.info("[installSkill] Skill {} installed successfully with {} dependencies", 
                        request.getSkillId(), installResult.getInstalledDependencies().size());
                } else {
                    result.setStatus("failed");
                    result.setMessage(installResult != null ? installResult.getError() : "Unknown error");
                    result.setFailedDependencies(installResult != null ? installResult.getFailedDependencies() : new ArrayList<>());
                    log.warn("[installSkill] Failed to install skill {}: {}", 
                        request.getSkillId(), result.getMessage());
                }
            } catch (Exception e) {
                log.error("[installSkill] Error installing skill {}: {}", request.getSkillId(), e.getMessage());
                result.setStatus("error");
                result.setMessage(e.getMessage());
            }
        } else if (skillService != null) {
            try {
                InstallResult installResult = skillService.installSkill(request.getSkillId()).get();
                
                if (installResult != null && installResult.isSuccess())
{
                    result.setStatus("installed");
                    result.setMessage("Skill installed successfully");
                    
                    SkillManifest manifest = skillService.getSkillManifest(request.getSkillId()).get();
                    if (manifest != null)
{
                        result.setCapabilities(convertManifestToCapabilities(manifest));
                    }
                    
                    result.setSelectedRole(request.getSelectedRole());
                    result.setDriverConditions(request.getDriverConditions());
                    
                    log.info("[installSkill] Skill {} installed successfully via SkillService", request.getSkillId());
                } else {
                    result.setStatus("failed");
                    result.setMessage(installResult != null ? installResult.getError() : "Unknown error");
                    log.warn("[installSkill] Failed to install skill {}: {}", 
                        request.getSkillId(), result.getMessage());
                }
            } catch (Exception e) {
                log.error("[installSkill] Error installing skill {}: {}", request.getSkillId(), e.getMessage());
                result.setStatus("error");
                result.setMessage(e.getMessage());
            }
        } else {
            log.warn("[installSkill] SkillPackageManager not available, using mock data");
            result.setStatus(mockEnabled ? "installed" : "unavailable");
            if (mockEnabled) {
                result.setCapabilities(getCapabilitiesForSkill(request.getSkillId()));
                result.setSelectedRole(request.getSelectedRole());
                result.setDriverConditions(request.getDriverConditions());
            }
        }
        
        return ResultModel.success(result);
    }
    
    private void applyInstallConfig(InstallRequest installRequest, InstallSkillRequestDTO request) {
        if (request.getSelectedRole() != null) {
            installRequest.addOption("selectedRole", request.getSelectedRole());
        }
        
        if (request.getParticipants() != null) {
            InstallSkillRequestDTO.ParticipantConfig pc = request.getParticipants();
            if (pc.getLeader() != null) {
                installRequest.addOption("leader", pc.getLeader());
            }
            if (pc.getPushType() != null) {
                installRequest.addOption("pushType", pc.getPushType());
            }
            if (pc.getCollaborators() != null && !pc.getCollaborators().isEmpty()) {
                installRequest.addOption("collaborators", pc.getCollaborators());
            }
        }
        
        if (request.getDriverConditions() != null && !request.getDriverConditions().isEmpty()) {
            installRequest.addOption("driverConditions", request.getDriverConditions());
        }
        
        if (request.getLlmConfig() != null) {
            InstallSkillRequestDTO.LLMConfig llm = request.getLlmConfig();
            if (llm.getProvider() != null) {
                installRequest.addOption("llmProvider", llm.getProvider());
            }
            if (llm.getModel() != null) {
                installRequest.addOption("llmModel", llm.getModel());
            }
            if (llm.getSystemPrompt() != null) {
                installRequest.addOption("systemPrompt", llm.getSystemPrompt());
            }
            if (llm.getEnableFunctionCall() != null) {
                installRequest.addOption("enableFunctionCall", llm.getEnableFunctionCall());
            }
            if (llm.getFunctionTools() != null && !llm.getFunctionTools().isEmpty()) {
                installRequest.addOption("functionTools", llm.getFunctionTools());
            }
            if (llm.getParameters() != null && !llm.getParameters().isEmpty()) {
                installRequest.addOption("llmParameters", llm.getParameters());
            }
            if (llm.getKnowledge() != null) {
                InstallSkillRequestDTO.KnowledgeConfig kc = llm.getKnowledge();
                if (kc.getEnabled() != null && kc.getEnabled()) {
                    installRequest.addOption("knowledgeEnabled", true);
                    if (kc.getTopK() != null) {
                        installRequest.addOption("ragTopK", kc.getTopK());
                    }
                    if (kc.getScoreThreshold() != null) {
                        installRequest.addOption("ragThreshold", kc.getScoreThreshold());
                    }
                    if (kc.getBases() != null && !kc.getBases().isEmpty()) {
                        installRequest.addOption("knowledgeBases", kc.getBases());
                    }
                }
            }
        }
    }

    private List<CapabilityDTO> convertManifestToCapabilities(SkillManifest manifest) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        if (manifest == null || manifest.getCapabilities() == null)
{
            return capabilities;
        }
        
        for (Object capObj : manifest.getCapabilities())
{
            CapabilityDTO cap = new CapabilityDTO();
            if (capObj instanceof Map)
{
                Map<?, ?> capMap = (Map<?, ?>) capObj;
                cap.setId(String.valueOf(capMap.get("id")));
                cap.setName(String.valueOf(capMap.get("name")));
                cap.setDescription(String.valueOf(capMap.get("description")));
                cap.setVersion(manifest.getVersion());
                cap.setStatus("installed");
            }
            capabilities.add(cap);
        }
        return capabilities;
    }

    private String[] parseRepoUrl(String repoUrl) {
        if (repoUrl == null || repoUrl.isEmpty())
 return null;
        
        try {
            String url = repoUrl.replaceAll("(https?://)?(github\\.com|gitee\\.com)/", "");
            String[] parts = url.split("/");
            if (parts.length >= 2)
            {
                return new String[]{parts[0], parts[1].replaceAll("\\.git$", "")};
            }
        } catch (Exception e)
 {
            log.error("[parseRepoUrl] error: {}", e.getMessage());
        }
        return null;
    }

    private List<CapabilityDTO> convertToCapabilities(List<SkillPackage> packages, String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        if (packages == null)
 return capabilities;
        
        for (SkillPackage pkg : packages)
{
            CapabilityDTO cap = new CapabilityDTO();
            cap.setId(pkg.getSkillId());
            cap.setName(pkg.getName());
            cap.setDescription(pkg.getDescription());
            cap.setVersion(pkg.getVersion());
            cap.setSource(source);
            cap.setStatus("available");
            
            boolean isScene = checkSceneCapability(pkg);
            
            cap.setType(isScene ? "SCENE" : "SKILL");
            cap.setSceneCapability(isScene);
            
            capabilities.add(cap);
        }
        return capabilities;
    }
    
    private boolean checkSceneCapability(SkillPackage pkg) {
        try {
            java.lang.reflect.Method getSceneCapability = pkg.getClass().getMethod("getSceneCapability");
            Boolean sceneCapability = (Boolean) getSceneCapability.invoke(pkg);
            if (sceneCapability != null && sceneCapability) {
                return true;
            }
        } catch (Exception ignored) {
        }
        
        try {
            java.lang.reflect.Method getSceneId = pkg.getClass().getMethod("getSceneId");
            String sceneId = (String) getSceneId.invoke(pkg);
            if (sceneId != null && !sceneId.isEmpty()) {
                return true;
            }
        } catch (Exception ignored) {
        }
        
        String skillId = pkg.getSkillId();
        return skillId != null && 
            (skillId.contains("-scene") || skillId.endsWith("-scene") || 
             "daily-log-scene".equals(skillId));
    }

    private List<RepositoryDTO> getDefaultGiteeRepos() {
        List<RepositoryDTO> results = new ArrayList<>();
        
        results.add(createSkillRepo(
            "ooderCN/skill-daily-report",
            "日志汇报技能",
            "提供日志提醒、提交、汇总、分析能力，支持定时提醒、表单提交、数据汇总、AI分析等功能",
            "https://gitee.com/ooderCN/skill-daily-report",
            128, 45, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "会议管理技能",
            "会议预约、提醒、纪要能力，支持会议室预定、参会人员管理、会议记录生成",
            "https://gitee.com/ooderCN/skill-meeting",
            96, 32, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-network",
            "网络管理技能",
            "网络管理服务，支持网络配置、状态监控、故障诊断",
            "https://gitee.com/ooderCN/skill-network",
            64, 23, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-security",
            "安全管理技能",
            "安全管理服务，支持权限控制、审计日志、安全扫描",
            "https://gitee.com/ooderCN/skill-security",
            85, 28, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-im",
            "即时通讯技能",
            "即时通讯服务，支持消息发送、群组管理、消息推送",
            "https://gitee.com/ooderCN/skill-im",
            156, 67, "v2.3"
        ));
        
        return results;
    }

    private List<CapabilityDTO> getMockGitHubCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "daily-log-scene", "日志汇报场景", "SCENE",
            "完整的日志汇报场景能力，包含提醒、提交、汇总、分析等闭环流程", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "日志汇总", "SERVICE",
            "汇总所有员工提交的日志", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-analyze", "日志分析", "AI",
            "使用AI分析日志内容，提取关键信息", "2.3", "GITHUB"
        ));
        
        return capabilities;
    }

    private List<CapabilityDTO> getMockGiteeCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "daily-log-scene", "日志汇报场景", "SCENE",
            "完整的日志汇报场景能力，包含提醒、提交、汇总、分析等闭环流程", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-remind", "日志提醒", "COMMUNICATION",
            "定时提醒员工提交工作日志", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "日志提交", "SERVICE",
            "员工提交工作日志的表单能力", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "日志汇总", "SERVICE",
            "汇总所有员工提交的日志", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "notification-email", "邮件通知", "COMMUNICATION",
            "发送邮件通知", "2.3", "GITEE"
        ));
        
        return capabilities;
    }

    private List<CapabilityDTO> getMockGitCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "data-backup", "数据备份", "STORAGE",
            "自动备份场景数据到云端或本地存储", "2.3", "GIT_REPOSITORY"
        ));
        
        capabilities.add(createCapability(
            "system-monitor", "系统监控", "MONITORING",
            "监控系统运行状态，包括CPU、内存、网络等", "2.3", "GIT_REPOSITORY"
        ));
        
        return capabilities;
    }

    private CapabilityDTO createCapability(String id, String name, String type, 
            String description, String version, String source) {
        CapabilityDTO cap = new CapabilityDTO();
        cap.setId(id);
        cap.setName(name);
        cap.setType(type);
        cap.setDescription(description);
        cap.setVersion(version);
        cap.setSource(source);
        cap.setStatus("available");
        cap.setSceneCapability("SCENE".equals(type));
        return cap;
    }

    private RepositoryDTO createSkillRepo(String fullName, String name, String description,
            String htmlUrl, int stars, int forks, String latestVersion) {
        RepositoryDTO repo = new RepositoryDTO();
        repo.setFullName(fullName);
        repo.setName(name);
        repo.setDescription(description);
        repo.setHtmlUrl(htmlUrl);
        repo.setStars(stars);
        repo.setForks(forks);
        repo.setLatestVersion(latestVersion);
        repo.setInstallCount(stars * 10);
        repo.setRating(4.5 + Math.random() * 0.5);
        return repo;
    }

    private List<CapabilityDTO> getCapabilitiesForSkill(String skillId) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        if ("skill-daily-report".equals(skillId))
{
            capabilities.add(createCapability("report-remind", "日志提醒", "COMMUNICATION", "定时提醒", "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-submit", "日志提交", "SERVICE", "提交日志", "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-aggregate", "日志汇总", "SERVICE", "汇总日志", "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-analyze", "日志分析", "AI", "分析日志", "2.3", "INSTALLED"));
        }
 else if ("skill-notification".equals(skillId))
        {
            capabilities.add(createCapability("notification-email", "邮件通知", "COMMUNICATION", "发送邮件", "2.3", "INSTALLED"));
            capabilities.add(createCapability("notification-sms", "短信通知", "COMMUNICATION", "发送短信", "2.3", "INSTALLED"));
        }
        
        return capabilities;
    }

    @PostMapping("/local")
    public ResultModel<DiscoveryResultDTO> discoverFromLocal(@RequestBody(required = false) LocalDiscoveryConfigDTO config) {
        log.info("[discoverFromLocal] Scanning local installed skills, config: {}", config);
        
        DiscoveryResultDTO result = new DiscoveryResultDTO();
        result.setMethod("LOCAL_FS");
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        SkillForm formFilter = null;
        SceneType sceneTypeFilter = null;
        CapabilityCategory categoryFilter = null;
        
        if (config != null) {
            if (config.getSkillForm() != null && !config.getSkillForm().isEmpty()) {
                try { formFilter = SkillForm.fromCode(config.getSkillForm()); } catch (Exception e) {}
            }
            if (config.getSceneType() != null && !config.getSceneType().isEmpty()) {
                try { sceneTypeFilter = SceneType.fromCode(config.getSceneType()); } catch (Exception e) {}
            }
            if (config.getSkillCategory() != null && !config.getSkillCategory().isEmpty()) {
                try { categoryFilter = CapabilityCategory.fromCode(config.getSkillCategory()); } catch (Exception e) {}
            }
        }
        
        log.info("[discoverFromLocal] Filters - form: {}, sceneType: {}, category: {}", 
            formFilter, sceneTypeFilter, categoryFilter);
        
        List<CapabilityDTO> entryCapabilities = skillIndexLoader.getSkillsFromEntryFiles("LOCAL");
        log.info("[discoverFromLocal] Found {} capabilities through skill-index-entry.yaml", entryCapabilities.size());
        
        for (CapabilityDTO cap : entryCapabilities) {
            String skillForm = cap.getSkillForm();
            if (formFilter != null && skillForm != null && !formFilter.getCode().equals(skillForm)) {
                continue;
            }
            
            String sceneType = cap.getSceneType();
            if (sceneTypeFilter != null && sceneType != null && !sceneTypeFilter.getCode().equals(sceneType)) {
                continue;
            }
            
            String capabilityCategory = cap.getCapabilityCategory();
            if (categoryFilter != null && capabilityCategory != null && !categoryFilter.getCode().equalsIgnoreCase(capabilityCategory)) {
                continue;
            }
            
            cap.setSource("LOCAL_FS");
            cap.setStatus("available");
            cap.setInstalled(checkIfInstalled(cap.getId()));
            capabilities.add(cap);
        }
        
        log.info("[discoverFromLocal] Returning {} capabilities after filters", capabilities.size());
        
        result.setCapabilities(capabilities);
        result.setScanTime(System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/capabilities/detail/{capabilityId}/roles")
    public ResultModel<List<Map<String, Object>>> getCapabilityRoles(@PathVariable String capabilityId) {
        log.info("[getCapabilityRoles] capabilityId: {}", capabilityId);
        
        List<Map<String, Object>> roles = new ArrayList<>();
        
        if ("daily-log-scene".equals(capabilityId) || capabilityId.contains("log") || capabilityId.contains("report")) {
            roles.add(createRole("LEADER", "领导", "管理者角色，配置场景参数、查看团队日志", "ri-user-star-line", 
                Arrays.asList("CONFIG", "VIEW_ALL", "MANAGE")));
            roles.add(createRole("EMPLOYEE", "员工", "参与者角色，填写日志、查看个人记录", "ri-user-line", 
                Arrays.asList("WRITE", "VIEW_SELF")));
            roles.add(createRole("HR", "HR", "观察者角色，查看团队日志、统计分析", "ri-team-line", 
                Arrays.asList("VIEW_ALL", "ANALYZE", "EXPORT")));
        } else if (capabilityId.contains("meeting")) {
            roles.add(createRole("ORGANIZER", "组织者", "创建和管理会议", "ri-user-star-line", 
                Arrays.asList("CREATE", "MANAGE", "CANCEL")));
            roles.add(createRole("PARTICIPANT", "参与者", "参加会议", "ri-user-line", 
                Arrays.asList("JOIN", "VIEW")));
        } else {
            roles.add(createRole("MANAGER", "管理者", "拥有完整管理权限", "ri-user-star-line", 
                Arrays.asList("READ", "WRITE", "CONFIG", "DELETE")));
            roles.add(createRole("USER", "普通用户", "基础使用权限", "ri-user-line", 
                Arrays.asList("READ", "WRITE")));
        }
        
        return ResultModel.success(roles);
    }
    
    private Map<String, Object> createRole(String name, String displayName, String description, String icon, List<String> permissions) {
        Map<String, Object> role = new HashMap<>();
        role.put("name", name);
        role.put("displayName", displayName);
        role.put("description", description);
        role.put("icon", icon);
        role.put("permissions", permissions);
        return role;
    }
    
    private boolean checkIfInstalled(String skillId) {
        if (skillId == null) {
            return false;
        }
        if (skillPackageManager == null) {
            return false;
        }
        try {
            return skillPackageManager.isInstalled(skillId).get();
        } catch (Exception e) {
            log.debug("[checkIfInstalled] Failed to check installation status for {}: {}", skillId, e.getMessage());
            return false;
        }
    }
}
