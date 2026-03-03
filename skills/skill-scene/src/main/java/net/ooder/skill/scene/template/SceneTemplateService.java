package net.ooder.skill.scene.template;

import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.InstallRequest.InstallMode;
import net.ooder.skill.scene.dto.SceneDefinitionDTO;
import net.ooder.skill.scene.dto.CapabilityDTO;
import net.ooder.skill.scene.service.SceneService;
import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.service.CapabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SceneTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SceneTemplateService.class);

    @Autowired(required = false)
    @Qualifier("skillPackageManager")
    private SkillPackageManager skillPackageManager;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private SceneTemplateLoader templateLoader;

    private final Map<String, SceneTemplate> builtinTemplates = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> progressEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public List<SceneTemplate> listTemplates() {
        List<SceneTemplate> allTemplates = new ArrayList<>();
        allTemplates.addAll(templateLoader.getAllTemplates());
        allTemplates.addAll(builtinTemplates.values());
        return allTemplates;
    }

    public SceneTemplate getTemplate(String templateId) {
        SceneTemplate template = templateLoader.getTemplate(templateId);
        if (template == null) {
            template = builtinTemplates.get(templateId);
        }
        return template;
    }

    public SseEmitter deployTemplateWithProgress(String templateId) {
        SseEmitter emitter = new SseEmitter(300000L);
        progressEmitters.put(templateId, emitter);
        
        final String tid = templateId;
        
        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                progressEmitters.remove(tid);
            }
        });
        
        emitter.onTimeout(new Runnable() {
            @Override
            public void run() {
                progressEmitters.remove(tid);
                log.warn("[SSE] Template deployment timeout: {}", tid);
            }
        });
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DeployResult result = deployTemplate(tid, new InstallProgressCallback() {
                        private int currentSkill = 0;
                        private int totalSkills = 0;
                        
                        @Override
                        public void onInstallStart(String templateId, int total) {
                            totalSkills = total;
                            sendProgress(templateId, "start", "开始部署模板", 0, total);
                        }
                        
                        @Override
                        public void onSkillStart(String skillId, int current, int total) {
                            currentSkill = current;
                            sendProgress(tid, "skill_start", "正在安装: " + skillId, current - 1, total);
                        }
                        
                        @Override
                        public void onSkillProgress(String skillId, String phase, int progress) {
                            sendProgress(tid, "skill_progress", skillId + " - " + phase, currentSkill, totalSkills);
                        }
                        
                        @Override
                        public void onSkillComplete(String skillId, boolean success, String message) {
                            String status = success ? "success" : "failed";
                            sendProgress(tid, "skill_complete", skillId + ": " + message, currentSkill, totalSkills);
                        }
                        
                        @Override
                        public void onDependencyStart(String parentSkillId, String dependencyId) {
                            sendProgress(tid, "dep_start", "安装依赖: " + dependencyId, currentSkill, totalSkills);
                        }
                        
                        @Override
                        public void onDependencyComplete(String parentSkillId, String dependencyId, boolean success) {
                            String status = success ? "成功" : "失败";
                            sendProgress(tid, "dep_complete", "依赖 " + dependencyId + " " + status, currentSkill, totalSkills);
                        }
                        
                        @Override
                        public void onInstallComplete(String templateId, boolean success, String message) {
                            String status = success ? "success" : "failed";
                            sendProgress(tid, "complete", message, totalSkills, totalSkills);
                        }
                        
                        @Override
                        public void onRollbackStart(String templateId, List<String> installedSkills) {
                            sendProgress(tid, "rollback_start", "开始回滚已安装的 " + installedSkills.size() + " 个技能", 0, installedSkills.size());
                        }
                        
                        @Override
                        public void onRollbackComplete(String templateId, boolean success) {
                            String status = success ? "成功" : "失败";
                            sendProgress(tid, "rollback_complete", "回滚" + status, 0, 0);
                        }
                        
                        private void sendProgress(String templateId, String event, String message, int current, int total) {
                            try {
                                SseEmitter em = progressEmitters.get(templateId);
                                if (em != null) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("event", event);
                                    data.put("message", message);
                                    data.put("current", current);
                                    data.put("total", total);
                                    data.put("progress", total > 0 ? (current * 100 / total) : 0);
                                    em.send(SseEmitter.event().name("progress").data(data));
                                }
                            } catch (IOException e) {
                                log.error("[SSE] Failed to send progress: {}", e.getMessage());
                            }
                        }
                    });
                    
                    SseEmitter em = progressEmitters.get(tid);
                    if (em != null) {
                        Map<String, Object> finalData = new HashMap<>();
                        finalData.put("success", result.isSuccess());
                        finalData.put("sceneId", result.getSceneId());
                        finalData.put("installedSkills", result.getInstalledSkills());
                        finalData.put("failedSkills", result.getFailedSkills());
                        finalData.put("message", result.getMessage());
                        em.send(SseEmitter.event().name("result").data(finalData));
                        em.complete();
                    }
                } catch (Exception e) {
                    log.error("[deployTemplateWithProgress] Error: {}", e.getMessage());
                    try {
                        SseEmitter em = progressEmitters.get(tid);
                        if (em != null) {
                            em.send(SseEmitter.event().name("error").data(e.getMessage()));
                            em.completeWithError(e);
                        }
                    } catch (IOException ioe) {
                        log.error("[SSE] Error sending error: {}", ioe.getMessage());
                    }
                }
            }
        });
        
        return emitter;
    }

    public DeployResult deployTemplate(String templateId) {
        return deployTemplate(templateId, null);
    }

    public DeployResult deployTemplate(String templateId, InstallProgressCallback callback) {
        SceneTemplate template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        log.info("[deployTemplate] Deploying template: {} ({})", templateId, template.getName());
        DeployResult result = new DeployResult();
        result.setTemplateId(templateId);
        result.setTemplateName(template.getName());
        result.setStartTime(System.currentTimeMillis());

        List<String> installedSkills = new ArrayList<>();
        List<String> skippedSkills = new ArrayList<>();
        List<String> failedSkills = new ArrayList<>();

        try {
            List<String> installOrder = getInstallOrder(template);
            int totalSkills = installOrder.size();
            
            if (callback != null) {
                callback.onInstallStart(templateId, totalSkills);
            }
            
            log.info("[deployTemplate] Install order: {}", installOrder);

            int current = 0;
            for (String skillId : installOrder) {
                current++;
                try {
                    if (skillPackageManager != null) {
                        boolean isInstalled = skillPackageManager.isInstalled(skillId).get();
                        if (isInstalled) {
                            log.info("[deployTemplate] Skill already installed: {}", skillId);
                            skippedSkills.add(skillId);
                            installedSkills.add(skillId);
                            if (callback != null) {
                                callback.onSkillComplete(skillId, true, "已安装");
                            }
                            continue;
                        }

                        if (callback != null) {
                            callback.onSkillStart(skillId, current, totalSkills);
                        }

                        log.info("[deployTemplate] Installing skill with dependencies: {}", skillId);
                        InstallResultWithDependencies installResult = skillPackageManager
                            .installWithDependencies(skillId, InstallMode.TOPOLOGICAL).get();
                        
                        if (installResult.isSuccess()) {
                            installedSkills.add(skillId);
                            log.info("[deployTemplate] Successfully installed: {} with {} dependencies", 
                                skillId, installResult.getInstalledDependencies().size());
                            if (callback != null) {
                                callback.onSkillComplete(skillId, true, "安装成功");
                            }
                        } else {
                            failedSkills.add(skillId);
                            log.error("[deployTemplate] Failed to install: {} - {}", skillId, installResult.getMessage());
                            if (callback != null) {
                                callback.onSkillComplete(skillId, false, installResult.getMessage());
                            }
                            
                            if (isRequiredSkill(template, skillId)) {
                                throw new RuntimeException("Required skill installation failed: " + skillId);
                            }
                        }
                    } else {
                        log.warn("[deployTemplate] SkillPackageManager not available, skipping: {}", skillId);
                        skippedSkills.add(skillId);
                        if (callback != null) {
                            callback.onSkillComplete(skillId, true, "跳过（无PackageManager）");
                        }
                    }
                } catch (Exception e) {
                    log.error("[deployTemplate] Failed to install skill: {}", skillId, e);
                    failedSkills.add(skillId);
                    if (callback != null) {
                        callback.onSkillComplete(skillId, false, e.getMessage());
                    }
                    
                    if (isRequiredSkill(template, skillId)) {
                        log.error("[deployTemplate] Required skill failed, starting rollback...");
                        rollbackInstall(installedSkills, callback, templateId);
                        throw new RuntimeException("Required skill installation failed: " + skillId, e);
                    }
                }
            }

            result.setInstalledSkills(installedSkills);
            result.setSkippedSkills(skippedSkills);
            result.setFailedSkills(failedSkills);

            SceneDefinitionDTO sceneDef = new SceneDefinitionDTO();
            sceneDef.setSceneId(UUID.randomUUID().toString());
            sceneDef.setName(template.getName());
            
            if (template.getSpec() != null && template.getSpec().getScene() != null) {
                SceneTemplate.SceneConfig sceneConfig = template.getSpec().getScene();
                sceneDef.setType(sceneConfig.getType() != null ? sceneConfig.getType() : "general");
                sceneDef.setDescription(sceneConfig.getDescription());
            } else {
                sceneDef.setType("general");
                sceneDef.setDescription(template.getDescription());
            }
            
            SceneDefinitionDTO createdScene = sceneService.create(sceneDef);
            result.setSceneId(createdScene.getSceneId());
            log.info("[deployTemplate] Created scene: {}", createdScene.getSceneId());

            List<String> boundCapabilities = new ArrayList<>();
            if (template.getSpec() != null && template.getSpec().getCapabilities() != null) {
                for (SceneTemplate.CapabilityDef cap : template.getSpec().getCapabilities()) {
                    try {
                        CapabilityDTO capDto = new CapabilityDTO();
                        capDto.setCapId(cap.getId());
                        capDto.setName(cap.getName());
                        capDto.setDescription(cap.getDescription());
                        sceneService.addCapability(createdScene.getSceneId(), capDto);
                        
                        boundCapabilities.add(cap.getId());
                        log.info("[deployTemplate] Bound capability: {} to scene: {}", cap.getId(), createdScene.getSceneId());
                    } catch (Exception e) {
                        log.error("[deployTemplate] Failed to bind capability: {}", cap.getId(), e);
                    }
                }
            }

            result.setBoundCapabilities(boundCapabilities);
            result.setSuccess(true);
            result.setMessage("Template deployed successfully");
            
            if (callback != null) {
                callback.onInstallComplete(templateId, true, "部署成功");
            }

        } catch (Exception e) {
            log.error("[deployTemplate] Failed to deploy template: {}", templateId, e);
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            
            if (callback != null) {
                callback.onInstallComplete(templateId, false, e.getMessage());
            }
        }

        result.setEndTime(System.currentTimeMillis());
        result.setDuration(result.getEndTime() - result.getStartTime());
        return result;
    }

    private void rollbackInstall(List<String> installedSkills, InstallProgressCallback callback, String templateId) {
        if (installedSkills == null || installedSkills.isEmpty()) {
            return;
        }
        
        if (callback != null) {
            callback.onRollbackStart(templateId, installedSkills);
        }
        
        log.info("[rollback] Starting rollback for {} skills", installedSkills.size());
        
        Collections.reverse(installedSkills);
        boolean allSuccess = true;
        
        for (String skillId : installedSkills) {
            try {
                if (skillPackageManager != null) {
                    skillPackageManager.uninstall(skillId).get();
                    log.info("[rollback] Uninstalled: {}", skillId);
                }
            } catch (Exception e) {
                log.error("[rollback] Failed to uninstall: {}", skillId, e);
                allSuccess = false;
            }
        }
        
        if (callback != null) {
            callback.onRollbackComplete(templateId, allSuccess);
        }
        
        log.info("[rollback] Rollback completed, success: {}", allSuccess);
    }

    private List<String> getInstallOrder(SceneTemplate template) {
        if (template.getSpec() != null && template.getSpec().getInstallOrder() != null) {
            return template.getSpec().getInstallOrder();
        }
        
        List<String> order = new ArrayList<>();
        
        if (template.getSpec() != null && template.getSpec().getSkills() != null) {
            List<SceneTemplate.SkillRef> required = template.getRequiredSkills();
            List<SceneTemplate.SkillRef> optional = template.getOptionalSkills();
            
            for (SceneTemplate.SkillRef ref : required) {
                order.add(ref.getId());
            }
            for (SceneTemplate.SkillRef ref : optional) {
                order.add(ref.getId());
            }
        }
        
        return order;
    }

    private boolean isRequiredSkill(SceneTemplate template, String skillId) {
        if (template.getSpec() == null || template.getSpec().getSkills() == null) {
            return true;
        }
        for (SceneTemplate.SkillRef ref : template.getSpec().getSkills()) {
            if (ref.getId().equals(skillId)) {
                return ref.isRequired();
            }
        }
        return false;
    }

    public InstallResult installTemplateDependencies(String templateId) {
        log.info("[installTemplateDependencies] Installing dependencies for template: {}", templateId);
        
        SceneTemplate template = getTemplate(templateId);
        if (template == null) {
            InstallResult result = new InstallResult();
            result.setTemplateId(templateId);
            result.setSuccess(false);
            result.setMessage("Template not found: " + templateId);
            return result;
        }
        
        InstallResult result = new InstallResult();
        result.setTemplateId(templateId);
        result.setInstalledSkills(new ArrayList<String>());
        result.setSkippedSkills(new ArrayList<String>());
        result.setFailedSkills(new ArrayList<String>());
        
        List<String> installOrder = getInstallOrder(template);
        log.info("[installTemplateDependencies] Install order: {}", installOrder);
        
        for (String skillId : installOrder) {
            try {
                boolean installed = false;
                if (skillPackageManager != null) {
                    installed = skillPackageManager.isInstalled(skillId).get();
                }
                
                if (installed) {
                    log.info("[installTemplateDependencies] Skill already installed: {}", skillId);
                    result.getSkippedSkills().add(skillId);
                } else {
                    log.info("[installTemplateDependencies] Installing skill: {}", skillId);
                    if (skillPackageManager != null) {
                        skillPackageManager.installWithDependencies(skillId, net.ooder.skills.api.InstallRequest.InstallMode.FULL_INSTALL).get();
                        result.getInstalledSkills().add(skillId);
                        log.info("[installTemplateDependencies] Successfully installed: {}", skillId);
                    } else {
                        result.getSkippedSkills().add(skillId);
                    }
                }
            } catch (Exception e) {
                log.error("[installTemplateDependencies] Failed to install: {}", skillId, e);
                result.getFailedSkills().add(skillId);
            }
        }
        
        if (result.getFailedSkills().isEmpty()) {
            result.setSuccess(true);
            result.setMessage("All dependencies installed successfully");
        } else {
            result.setSuccess(false);
            result.setMessage("Failed to install: " + String.join(", ", result.getFailedSkills()));
        }
        
        return result;
    }

    public static class DeployResult {
        private String templateId;
        private String templateName;
        private String sceneId;
        private boolean success;
        private String message;
        private List<String> installedSkills;
        private List<String> skippedSkills;
        private List<String> failedSkills;
        private List<String> boundCapabilities;
        private long startTime;
        private long endTime;
        private long duration;

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getTemplateName() { return templateName; }
        public void setTemplateName(String templateName) { this.templateName = templateName; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        public List<String> getSkippedSkills() { return skippedSkills; }
        public void setSkippedSkills(List<String> skippedSkills) { this.skippedSkills = skippedSkills; }
        public List<String> getFailedSkills() { return failedSkills; }
        public void setFailedSkills(List<String> failedSkills) { this.failedSkills = failedSkills; }
        public List<String> getBoundCapabilities() { return boundCapabilities; }
        public void setBoundCapabilities(List<String> boundCapabilities) { this.boundCapabilities = boundCapabilities; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }

    public static class InstallResult {
        private String templateId;
        private boolean success;
        private String message;
        private List<String> installedSkills;
        private List<String> skippedSkills;
        private List<String> failedSkills;

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        public List<String> getSkippedSkills() { return skippedSkills; }
        public void setSkippedSkills(List<String> skippedSkills) { this.skippedSkills = skippedSkills; }
        public List<String> getFailedSkills() { return failedSkills; }
        public void setFailedSkills(List<String> failedSkills) { this.failedSkills = failedSkills; }
    }
}
