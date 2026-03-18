package net.ooder.mvp.skill.scene.install;

import net.ooder.mvp.skill.scene.service.SceneGroupService;
import net.ooder.mvp.skill.scene.service.SceneService;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.InstallRequest.InstallMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class InstallSceneService {

    private static final Logger log = LoggerFactory.getLogger(InstallSceneService.class);

    @Autowired
    private SceneGroupService sceneGroupService;

    @Autowired
    private SceneService sceneService;

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, InstallProgress> progressMap = new ConcurrentHashMap<>();

    public List<InstallStep> getInstallSteps(String profile) {
        List<InstallStep> steps = new ArrayList<>();
        
        steps.add(new InstallStep(1, "环境检查", "检查系统环境和依赖", true, false));
        steps.add(new InstallStep(2, "基础技能安装", "安装核心技能包", true, false));
        steps.add(new InstallStep(3, "能力配置", "配置系统能力", true, false));
        steps.add(new InstallStep(4, "场景初始化", "创建默认场景", false, true));
        steps.add(new InstallStep(5, "完成验证", "验证安装结果", false, true));
        
        return steps;
    }

    public List<SkillPackage> getRequiredSkills(String profile) {
        List<SkillPackage> skills = new ArrayList<>();
        
        skills.add(new SkillPackage("skill-scene", "场景管理技能包", "核心场景管理功能", "2.3.1", true, true));
        skills.add(new SkillPackage("skill-common", "公共基础技能包", "公共模型和服务", "2.3.1", true, false));
        skills.add(new SkillPackage("skill-org-base", "组织管理技能包", "组织和用户管理", "2.3.1", true, false));
        skills.add(new SkillPackage("skill-hotplug-starter", "热插拔技能包", "技能热加载功能", "2.3.1", false, true));
        
        if ("enterprise".equals(profile) || "cloud".equals(profile)) {
            skills.add(new SkillPackage("skill-health", "健康检查技能包", "系统健康监控", "2.3.1", false, true));
            skills.add(new SkillPackage("skill-llm-chat", "LLM对话技能包", "大语言模型对话", "2.3.1", false, true));
        }
        
        return skills;
    }

    public SseEmitter startInstallWithProgress(String profile, Map<String, Object> config) {
        String installId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(300000L);
        emitters.put(installId, emitter);
        
        InstallProgress progress = new InstallProgress(installId, profile);
        progressMap.put(installId, progress);
        
        emitter.onCompletion(() -> {
            emitters.remove(installId);
            progressMap.remove(installId);
        });
        
        emitter.onTimeout(() -> {
            emitters.remove(installId);
            progressMap.remove(installId);
        });
        
        executor.execute(() -> executeInstall(installId, profile, config, emitter));
        
        return emitter;
    }

    private void executeInstall(String installId, String profile, Map<String, Object> config, SseEmitter emitter) {
        InstallProgress progress = progressMap.get(installId);
        
        try {
            Map<String, Object> startData = new HashMap<>();
            startData.put("installId", installId);
            startData.put("profile", profile);
            startData.put("message", "开始安装流程");
            startData.put("sdkAvailable", skillPackageManager != null);
            sendEvent(emitter, "start", startData);
            
            List<InstallStep> steps = getInstallSteps(profile);
            List<SkillPackage> skills = getRequiredSkills(profile);
            
            progress.setTotalSteps(steps.size());
            progress.setTotalSkills(skills.size());
            
            for (int i = 0; i < steps.size(); i++) {
                InstallStep step = steps.get(i);
                progress.setCurrentStep(i + 1);
                progress.setCurrentStepName(step.getName());
                
                Map<String, Object> stepData = new HashMap<>();
                stepData.put("step", i + 1);
                stepData.put("total", steps.size());
                stepData.put("name", step.getName());
                stepData.put("description", step.getDescription());
                stepData.put("required", step.isRequired());
                sendEvent(emitter, "step", stepData);
                
                Thread.sleep(500);
                
                if (i == 1) {
                    for (int j = 0; j < skills.size(); j++) {
                        SkillPackage skill = skills.get(j);
                        progress.setCurrentSkillIndex(j + 1);
                        progress.setCurrentSkillName(skill.getName());
                        
                        Map<String, Object> skillData = new HashMap<>();
                        skillData.put("skillId", skill.getId());
                        skillData.put("name", skill.getName());
                        skillData.put("desc", skill.getDesc());
                        skillData.put("version", skill.getVersion());
                        skillData.put("index", j + 1);
                        skillData.put("total", skills.size());
                        skillData.put("isCurrentSystem", skill.isCurrentSystem());
                        sendEvent(emitter, "skill", skillData);
                        
                        String installStatus;
                        String installMessage;
                        
                        if (skill.isCurrentSystem()) {
                            installStatus = "skipped";
                            installMessage = "当前系统，跳过安装";
                            progress.addInstalledSkill(skill.getId());
                        } else {
                            InstallResult installResult = installSkillPackage(skill.getId());
                            installStatus = installResult.success ? "installed" : "failed";
                            installMessage = installResult.message;
                            if (installResult.success) {
                                progress.addInstalledSkill(skill.getId());
                            }
                        }
                        
                        Thread.sleep(300);
                        
                        Map<String, Object> skillProgressData = new HashMap<>();
                        skillProgressData.put("skillId", skill.getId());
                        skillProgressData.put("status", installStatus);
                        skillProgressData.put("message", installMessage);
                        sendEvent(emitter, "skillProgress", skillProgressData);
                    }
                }
                
                Map<String, Object> stepCompleteData = new HashMap<>();
                stepCompleteData.put("step", i + 1);
                stepCompleteData.put("name", step.getName());
                sendEvent(emitter, "stepComplete", stepCompleteData);
            }
            
            progress.setStatus("completed");
            progress.setCompletedAt(new Date());
            
            Map<String, Object> completeData = new HashMap<>();
            completeData.put("installId", installId);
            completeData.put("status", "completed");
            completeData.put("message", "安装流程完成");
            completeData.put("installedSkills", progress.getInstalledSkills());
            completeData.put("profile", profile);
            sendEvent(emitter, "complete", completeData);
            
            emitter.complete();
            
        } catch (Exception e) {
            log.error("[InstallScene] Error during install: {}", e.getMessage());
            progress.setStatus("failed");
            progress.setError(e.getMessage());
            
            try {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("installId", installId);
                errorData.put("error", e.getMessage());
                sendEvent(emitter, "error", errorData);
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("[InstallScene] Error sending error event: {}", ex.getMessage());
            }
        }
    }

    private InstallResult installSkillPackage(String skillId) {
        if (skillPackageManager == null) {
            log.warn("[InstallScene] SkillPackageManager not available, mock mode");
            return new InstallResult(mockEnabled, mockEnabled ? "模拟安装成功" : "SkillPackageManager 不可用");
        }
        
        try {
            log.info("[InstallScene] Installing skill: {}", skillId);
            
            CompletableFuture<Boolean> isInstalledFuture = skillPackageManager.isInstalled(skillId);
            Boolean isInstalled = isInstalledFuture.get(10, TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(isInstalled)) {
                log.info("[InstallScene] Skill already installed: {}", skillId);
                return new InstallResult(true, "已安装，跳过");
            }
            
            CompletableFuture<InstallResultWithDependencies> installFuture = 
                skillPackageManager.installWithDependencies(skillId, InstallMode.FULL_INSTALL);
            
            InstallResultWithDependencies result = installFuture.get(60, TimeUnit.SECONDS);
            
            if (result != null && result.isSuccess()) {
                log.info("[InstallScene] Successfully installed: {}", skillId);
                String message = "安装成功";
                if (result.getInstalledDependencies() != null && !result.getInstalledDependencies().isEmpty()) {
                    message += " (包含 " + result.getInstalledDependencies().size() + " 个依赖)";
                }
                return new InstallResult(true, message);
            } else {
                String error = result != null ? result.getError() : "未知错误";
                log.error("[InstallScene] Failed to install {}: {}", skillId, error);
                return new InstallResult(false, "安装失败: " + error);
            }
            
        } catch (TimeoutException e) {
            log.error("[InstallScene] Install timeout for {}", skillId);
            return new InstallResult(false, "安装超时");
        } catch (Exception e) {
            log.error("[InstallScene] Install error for {}: {}", skillId, e.getMessage());
            return new InstallResult(false, "安装异常: " + e.getMessage());
        }
    }

    private void sendEvent(SseEmitter emitter, String name, Map<String, Object> data) {
        try {
            emitter.send(SseEmitter.event()
                .name(name)
                .data(data));
        } catch (IOException e) {
            log.error("[InstallScene] Error sending event: {}", e.getMessage());
        }
    }

    public InstallProgress getProgress(String installId) {
        return progressMap.get(installId);
    }

    private static class InstallResult {
        boolean success;
        String message;
        
        InstallResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class InstallStep {
        private int index;
        private String name;
        private String description;
        private boolean required;
        private boolean recommended;

        public InstallStep(int index, String name, String description, boolean required, boolean recommended) {
            this.index = index;
            this.name = name;
            this.description = description;
            this.required = required;
            this.recommended = recommended;
        }

        public int getIndex() { return index; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isRequired() { return required; }
        public boolean isRecommended() { return recommended; }
    }

    public static class SkillPackage {
        private String id;
        private String name;
        private String desc;
        private String version;
        private boolean required;
        private boolean currentSystem;

        public SkillPackage(String id, String name, String desc, String version, boolean required, boolean currentSystem) {
            this.id = id;
            this.name = name;
            this.desc = desc;
            this.version = version;
            this.required = required;
            this.currentSystem = currentSystem;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDesc() { return desc; }
        public String getVersion() { return version; }
        public boolean isRequired() { return required; }
        public boolean isCurrentSystem() { return currentSystem; }
    }

    public static class InstallProgress {
        private String installId;
        private String profile;
        private String status;
        private int totalSteps;
        private int currentStep;
        private String currentStepName;
        private int totalSkills;
        private int currentSkillIndex;
        private String currentSkillName;
        private List<String> installedSkills = new ArrayList<>();
        private Date startedAt;
        private Date completedAt;
        private String error;

        public InstallProgress(String installId, String profile) {
            this.installId = installId;
            this.profile = profile;
            this.status = "running";
            this.startedAt = new Date();
        }

        public String getInstallId() { return installId; }
        public String getProfile() { return profile; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        public int getCurrentStep() { return currentStep; }
        public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
        public String getCurrentStepName() { return currentStepName; }
        public void setCurrentStepName(String currentStepName) { this.currentStepName = currentStepName; }
        public int getTotalSkills() { return totalSkills; }
        public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }
        public int getCurrentSkillIndex() { return currentSkillIndex; }
        public void setCurrentSkillIndex(int currentSkillIndex) { this.currentSkillIndex = currentSkillIndex; }
        public String getCurrentSkillName() { return currentSkillName; }
        public void setCurrentSkillName(String currentSkillName) { this.currentSkillName = currentSkillName; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void addInstalledSkill(String skillId) { this.installedSkills.add(skillId); }
        public Date getStartedAt() { return startedAt; }
        public Date getCompletedAt() { return completedAt; }
        public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
