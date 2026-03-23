package net.ooder.skill.scene.controller;

import jakarta.validation.Valid;
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
    
    @Autowired(required = false)
    private net.ooder.skill.scene.capability.service.CapabilityStateService capabilityStateService;
    
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
            "鏃ュ織姹囨姤鎶€鑳?,
            "鎻愪緵鏃ュ織鎻愰啋銆佹彁浜ゃ€佹眹鎬汇€佸垎鏋愯兘鍔涳紝鏀寔瀹氭椂鎻愰啋銆佽〃鍗曟彁浜ゃ€佹暟鎹眹鎬汇€丄I鍒嗘瀽绛夊姛鑳?,
            "https://github.com/ooderCN/skill-daily-report",
            128, 45, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-notification",
            "閫氱煡鎶€鑳?,
            "閭欢銆佺煭淇°€佺珯鍐呬俊閫氱煡鑳藉姏锛屾敮鎸佹ā鏉挎秷鎭€佹壒閲忓彂閫併€佸畾鏃跺彂閫?,
            "https://github.com/ooderCN/skill-notification",
            256, 89, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "浼氳绠＄悊鎶€鑳?,
            "浼氳棰勭害銆佹彁閱掋€佺邯瑕佽兘鍔涳紝鏀寔浼氳瀹ら瀹氥€佸弬浼氫汉鍛樼鐞嗐€佷細璁褰曠敓鎴?,
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
        Map<String, String> options = new HashMap<>();
        
        if (request.getSelectedRole() != null) {
            options.put("selectedRole", request.getSelectedRole());
        }
        
        if (request.getParticipants() != null) {
            InstallSkillRequestDTO.ParticipantConfig pc = request.getParticipants();
            if (pc.getLeader() != null) {
                options.put("leader", pc.getLeader());
            }
            if (pc.getPushType() != null) {
                options.put("pushType", pc.getPushType());
            }
            if (pc.getCollaborators() != null && !pc.getCollaborators().isEmpty()) {
                options.put("collaborators", String.join(",", pc.getCollaborators()));
            }
        }
        
        if (request.getDriverConditions() != null && !request.getDriverConditions().isEmpty()) {
            try {
                options.put("driverConditions", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request.getDriverConditions()));
            } catch (Exception e) {
                log.warn("Failed to serialize driverConditions", e);
            }
        }
        
        if (request.getLlmConfig() != null) {
            InstallSkillRequestDTO.LLMConfig llm = request.getLlmConfig();
            if (llm.getProvider() != null) {
                options.put("llmProvider", llm.getProvider());
            }
            if (llm.getModel() != null) {
                options.put("llmModel", llm.getModel());
            }
            if (llm.getSystemPrompt() != null) {
                options.put("systemPrompt", llm.getSystemPrompt());
            }
            if (llm.getEnableFunctionCall() != null) {
                options.put("enableFunctionCall", llm.getEnableFunctionCall().toString());
            }
            if (llm.getFunctionTools() != null && !llm.getFunctionTools().isEmpty()) {
                try {
                    options.put("functionTools", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(llm.getFunctionTools()));
                } catch (Exception e) {
                    log.warn("Failed to serialize functionTools", e);
                }
            }
            if (llm.getParameters() != null && !llm.getParameters().isEmpty()) {
                try {
                    options.put("llmParameters", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(llm.getParameters()));
                } catch (Exception e) {
                    log.warn("Failed to serialize llmParameters", e);
                }
            }
            if (llm.getKnowledge() != null) {
                InstallSkillRequestDTO.KnowledgeConfig kc = llm.getKnowledge();
                if (kc.getEnabled() != null && kc.getEnabled()) {
                    options.put("knowledgeEnabled", "true");
                    if (kc.getTopK() != null) {
                        options.put("ragTopK", kc.getTopK().toString());
                    }
                    if (kc.getScoreThreshold() != null) {
                        options.put("ragThreshold", kc.getScoreThreshold().toString());
                    }
                    if (kc.getBases() != null && !kc.getBases().isEmpty()) {
                        options.put("knowledgeBases", String.join(",", kc.getBases()));
                    }
                }
            }
        }
        
        if (!options.isEmpty()) {
            installRequest.setOptions(options);
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
            "鏃ュ織姹囨姤鎶€鑳?,
            "鎻愪緵鏃ュ織鎻愰啋銆佹彁浜ゃ€佹眹鎬汇€佸垎鏋愯兘鍔涳紝鏀寔瀹氭椂鎻愰啋銆佽〃鍗曟彁浜ゃ€佹暟鎹眹鎬汇€丄I鍒嗘瀽绛夊姛鑳?,
            "https://gitee.com/ooderCN/skill-daily-report",
            128, 45, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-meeting",
            "浼氳绠＄悊鎶€鑳?,
            "浼氳棰勭害銆佹彁閱掋€佺邯瑕佽兘鍔涳紝鏀寔浼氳瀹ら瀹氥€佸弬浼氫汉鍛樼鐞嗐€佷細璁褰曠敓鎴?,
            "https://gitee.com/ooderCN/skill-meeting",
            96, 32, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-network",
            "缃戠粶绠＄悊鎶€鑳?,
            "缃戠粶绠＄悊鏈嶅姟锛屾敮鎸佺綉缁滈厤缃€佺姸鎬佺洃鎺с€佹晠闅滆瘖鏂?,
            "https://gitee.com/ooderCN/skill-network",
            64, 23, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-security",
            "瀹夊叏绠＄悊鎶€鑳?,
            "瀹夊叏绠＄悊鏈嶅姟锛屾敮鎸佹潈闄愭帶鍒躲€佸璁℃棩蹇椼€佸畨鍏ㄦ壂鎻?,
            "https://gitee.com/ooderCN/skill-security",
            85, 28, "v2.3"
        ));
        
        results.add(createSkillRepo(
            "ooderCN/skill-im",
            "鍗虫椂閫氳鎶€鑳?,
            "鍗虫椂閫氳鏈嶅姟锛屾敮鎸佹秷鎭彂閫併€佺兢缁勭鐞嗐€佹秷鎭帹閫?,
            "https://gitee.com/ooderCN/skill-im",
            156, 67, "v2.3"
        ));
        
        return results;
    }

    private List<CapabilityDTO> getMockGitHubCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "daily-log-scene", "鏃ュ織姹囨姤鍦烘櫙", "SCENE",
            "瀹屾暣鐨勬棩蹇楁眹鎶ュ満鏅兘鍔涳紝鍖呭惈鎻愰啋銆佹彁浜ゃ€佹眹鎬汇€佸垎鏋愮瓑闂幆娴佺▼", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-remind", "鏃ュ織鎻愰啋", "COMMUNICATION",
            "瀹氭椂鎻愰啋鍛樺伐鎻愪氦宸ヤ綔鏃ュ織", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "鏃ュ織鎻愪氦", "SERVICE",
            "鍛樺伐鎻愪氦宸ヤ綔鏃ュ織鐨勮〃鍗曡兘鍔?, "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "鏃ュ織姹囨€?, "SERVICE",
            "姹囨€绘墍鏈夊憳宸ユ彁浜ょ殑鏃ュ織", "2.3", "GITHUB"
        ));
        
        capabilities.add(createCapability(
            "report-analyze", "鏃ュ織鍒嗘瀽", "AI",
            "浣跨敤AI鍒嗘瀽鏃ュ織鍐呭锛屾彁鍙栧叧閿俊鎭?, "2.3", "GITHUB"
        ));
        
        return capabilities;
    }

    private List<CapabilityDTO> getMockGiteeCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "daily-log-scene", "鏃ュ織姹囨姤鍦烘櫙", "SCENE",
            "瀹屾暣鐨勬棩蹇楁眹鎶ュ満鏅兘鍔涳紝鍖呭惈鎻愰啋銆佹彁浜ゃ€佹眹鎬汇€佸垎鏋愮瓑闂幆娴佺▼", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-remind", "鏃ュ織鎻愰啋", "COMMUNICATION",
            "瀹氭椂鎻愰啋鍛樺伐鎻愪氦宸ヤ綔鏃ュ織", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-submit", "鏃ュ織鎻愪氦", "SERVICE",
            "鍛樺伐鎻愪氦宸ヤ綔鏃ュ織鐨勮〃鍗曡兘鍔?, "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "report-aggregate", "鏃ュ織姹囨€?, "SERVICE",
            "姹囨€绘墍鏈夊憳宸ユ彁浜ょ殑鏃ュ織", "2.3", "GITEE"
        ));
        
        capabilities.add(createCapability(
            "notification-email", "閭欢閫氱煡", "COMMUNICATION",
            "鍙戦€侀偖浠堕€氱煡", "2.3", "GITEE"
        ));
        
        return capabilities;
    }

    private List<CapabilityDTO> getMockGitCapabilities(String repoUrl) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createCapability(
            "data-backup", "鏁版嵁澶囦唤", "STORAGE",
            "鑷姩澶囦唤鍦烘櫙鏁版嵁鍒颁簯绔垨鏈湴瀛樺偍", "2.3", "GIT_REPOSITORY"
        ));
        
        capabilities.add(createCapability(
            "system-monitor", "绯荤粺鐩戞帶", "MONITORING",
            "鐩戞帶绯荤粺杩愯鐘舵€侊紝鍖呮嫭CPU銆佸唴瀛樸€佺綉缁滅瓑", "2.3", "GIT_REPOSITORY"
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
            capabilities.add(createCapability("report-remind", "鏃ュ織鎻愰啋", "COMMUNICATION", "瀹氭椂鎻愰啋", "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-submit", "鏃ュ織鎻愪氦", "SERVICE", "鎻愪氦鏃ュ織", "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-aggregate", "鏃ュ織姹囨€?, "SERVICE", "姹囨€绘棩蹇?, "2.3", "INSTALLED"));
            capabilities.add(createCapability("report-analyze", "鏃ュ織鍒嗘瀽", "AI", "鍒嗘瀽鏃ュ織", "2.3", "INSTALLED"));
        }
 else if ("skill-notification".equals(skillId))
        {
            capabilities.add(createCapability("notification-email", "閭欢閫氱煡", "COMMUNICATION", "鍙戦€侀偖浠?, "2.3", "INSTALLED"));
            capabilities.add(createCapability("notification-sms", "鐭俊閫氱煡", "COMMUNICATION", "鍙戦€佺煭淇?, "2.3", "INSTALLED"));
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
            roles.add(createRole("LEADER", "棰嗗", "绠＄悊鑰呰鑹诧紝閰嶇疆鍦烘櫙鍙傛暟銆佹煡鐪嬪洟闃熸棩蹇?, "ri-user-star-line", 
                Arrays.asList("CONFIG", "VIEW_ALL", "MANAGE")));
            roles.add(createRole("EMPLOYEE", "鍛樺伐", "鍙備笌鑰呰鑹诧紝濉啓鏃ュ織銆佹煡鐪嬩釜浜鸿褰?, "ri-user-line", 
                Arrays.asList("WRITE", "VIEW_SELF")));
            roles.add(createRole("HR", "HR", "瑙傚療鑰呰鑹诧紝鏌ョ湅鍥㈤槦鏃ュ織銆佺粺璁″垎鏋?, "ri-team-line", 
                Arrays.asList("VIEW_ALL", "ANALYZE", "EXPORT")));
        } else if (capabilityId.contains("meeting")) {
            roles.add(createRole("ORGANIZER", "缁勭粐鑰?, "鍒涘缓鍜岀鐞嗕細璁?, "ri-user-star-line", 
                Arrays.asList("CREATE", "MANAGE", "CANCEL")));
            roles.add(createRole("PARTICIPANT", "鍙備笌鑰?, "鍙傚姞浼氳", "ri-user-line", 
                Arrays.asList("JOIN", "VIEW")));
        } else {
            roles.add(createRole("MANAGER", "绠＄悊鑰?, "鎷ユ湁瀹屾暣绠＄悊鏉冮檺", "ri-user-star-line", 
                Arrays.asList("READ", "WRITE", "CONFIG", "DELETE")));
            roles.add(createRole("USER", "鏅€氱敤鎴?, "鍩虹浣跨敤鏉冮檺", "ri-user-line", 
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
        if (capabilityStateService != null) {
            return capabilityStateService.isInstalled(skillId);
        }
        if (skillPackageManager != null) {
            try {
                return skillPackageManager.isInstalled(skillId).get();
            } catch (Exception e) {
                log.debug("[checkIfInstalled] Failed to check installation status for {}: {}", skillId, e.getMessage());
            }
        }
        return false;
    }
}
