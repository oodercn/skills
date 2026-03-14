package net.ooder.skill.scene.capability.activation;

import net.ooder.skill.scene.template.ActivationStepConfig;
import net.ooder.skill.scene.template.PrivateCapabilityConfig;
import net.ooder.skill.scene.template.SceneTemplate;
import net.ooder.skill.scene.template.SceneTemplateService;
import net.ooder.skill.scene.service.MenuAutoRegisterService;
import net.ooder.skill.scene.capability.service.KeyManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActivationServiceImpl implements ActivationService {

    private static final Logger log = LoggerFactory.getLogger(ActivationServiceImpl.class);

    private Map<String, ActivationProcess> processes = new ConcurrentHashMap<String, ActivationProcess>();

    @Autowired
    private NetworkActionExecutor networkActionExecutor;

    @Autowired
    private SceneTemplateService sceneTemplateService;

    @Autowired
    private MenuAutoRegisterService menuAutoRegisterService;
    
    @Autowired(required = false)
    private KeyManagementService keyManagementService;

    @Override
    public ActivationProcess getProcess(String installId) {
        return processes.get(installId);
    }

    @Override
    public ActivationProcess startProcess(String installId, String activator) {
        log.info("[startProcess] Starting activation process for: {} by {}", installId, activator);
        
        ActivationProcess process = ActivationProcess.createDefault(installId);
        process.setActivator(activator);
        process.setStatus(ActivationProcess.ActivationStatus.IN_PROGRESS);
        process.setCreateTime(System.currentTimeMillis());
        process.setUpdateTime(System.currentTimeMillis());
        
        processes.put(installId, process);
        
        return process;
    }

    @Override
    public ActivationProcess startProcessWithTemplate(String installId, String templateId, String sceneGroupId, String activator, String roleName) {
        log.info("[startProcessWithTemplate] Starting activation for template: {}, role: {}, by {}", templateId, roleName, activator);
        
        SceneTemplate template = sceneTemplateService.getTemplate(templateId);
        if (template == null) {
            log.warn("[startProcessWithTemplate] Template not found: {}", templateId);
            return null;
        }

        ActivationProcess process = new ActivationProcess();
        process.setProcessId("proc-" + System.currentTimeMillis());
        process.setInstallId(installId);
        process.setTemplateId(templateId);
        process.setSceneGroupId(sceneGroupId);
        process.setActivator(activator);
        process.setRoleName(roleName);
        process.setStatus(ActivationProcess.ActivationStatus.IN_PROGRESS);
        process.setCreateTime(System.currentTimeMillis());
        process.setUpdateTime(System.currentTimeMillis());
        process.setCurrentStep(0);
        process.setMenuRegistered(false);
        process.setNotificationSent(false);

        List<ActivationStepConfig> stepConfigs = template.getActivationSteps(roleName);
        List<ActivationProcess.ActivationStep> steps = new ArrayList<>();
        
        if (stepConfigs != null && !stepConfigs.isEmpty()) {
            for (ActivationStepConfig config : stepConfigs) {
                ActivationProcess.ActivationStep step = new ActivationProcess.ActivationStep();
                step.setStepId(config.getStepId());
                step.setName(config.getName());
                step.setDescription(config.getDescription());
                step.setRequired(config.isRequired());
                step.setSkippable(config.isSkippable());
                step.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
                steps.add(step);
            }
        } else {
            steps = createDefaultStepsForRole(roleName);
        }
        
        process.setSteps(steps);
        process.setTotalSteps(steps.size());

        List<PrivateCapabilityConfig> privateCapConfigs = template.getPrivateCapabilities();
        if (privateCapConfigs != null && !privateCapConfigs.isEmpty()) {
            List<ActivationProcess.PrivateCapabilityConfig> processCaps = new ArrayList<>();
            for (PrivateCapabilityConfig config : privateCapConfigs) {
                ActivationProcess.PrivateCapabilityConfig pc = new ActivationProcess.PrivateCapabilityConfig();
                pc.setCapId(config.getCapId());
                pc.setName(config.getName());
                pc.setDescription(config.getDescription());
                pc.setOptional(config.isOptional());
                pc.setEnabled(config.isEnabled());
                pc.setSkillId(config.getSkillId());
                processCaps.add(pc);
            }
            process.setPrivateCapabilities(processCaps);
        }

        processes.put(installId, process);
        
        return process;
    }

    private List<ActivationProcess.ActivationStep> createDefaultStepsForRole(String roleName) {
        List<ActivationProcess.ActivationStep> steps = new ArrayList<>();
        
        if ("MANAGER".equals(roleName) || "LEADER".equals(roleName)) {
            ActivationProcess.ActivationStep step1 = new ActivationProcess.ActivationStep();
            step1.setStepId("confirm-participants");
            step1.setName("确认参与者");
            step1.setDescription("确认场景的主导者和协作者");
            step1.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            step1.setRequired(true);
            step1.setSkippable(false);
            steps.add(step1);
            
            ActivationProcess.ActivationStep step2 = new ActivationProcess.ActivationStep();
            step2.setStepId("select-push-targets");
            step2.setName("选择推送目标");
            step2.setDescription("选择要推送的下属员工");
            step2.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            step2.setRequired(true);
            step2.setSkippable(false);
            steps.add(step2);
            
            ActivationProcess.ActivationStep step3 = new ActivationProcess.ActivationStep();
            step3.setStepId("config-conditions");
            step3.setName("配置驱动条件");
            step3.setDescription("配置场景的驱动条件和触发规则");
            step3.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            step3.setRequired(true);
            step3.setSkippable(false);
            steps.add(step3);
        } else if ("EMPLOYEE".equals(roleName)) {
            ActivationProcess.ActivationStep step1 = new ActivationProcess.ActivationStep();
            step1.setStepId("confirm-task");
            step1.setName("确认任务");
            step1.setDescription("您有一个新的任务，确认激活吗？");
            step1.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            step1.setRequired(true);
            step1.setSkippable(false);
            steps.add(step1);
            
            ActivationProcess.ActivationStep step2 = new ActivationProcess.ActivationStep();
            step2.setStepId("config-private-capabilities");
            step2.setName("配置私有能力");
            step2.setDescription("选择要启用的私有能力");
            step2.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            step2.setRequired(false);
            step2.setSkippable(true);
            steps.add(step2);
        }

        ActivationProcess.ActivationStep keyStep = new ActivationProcess.ActivationStep();
        keyStep.setStepId("get-key");
        keyStep.setName("获取KEY");
        keyStep.setDescription("获取访问安全数据的密钥");
        keyStep.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
        keyStep.setRequired(true);
        keyStep.setSkippable(false);
        steps.add(keyStep);
        
        ActivationProcess.ActivationStep confirmStep = new ActivationProcess.ActivationStep();
        confirmStep.setStepId("confirm-activation");
        confirmStep.setName("确认激活");
        confirmStep.setDescription("确认激活场景");
        confirmStep.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
        confirmStep.setRequired(true);
        confirmStep.setSkippable(false);
        steps.add(confirmStep);
        
        return steps;
    }

    @Override
    public ActivationProcess executeStep(String installId, String stepId, Map<String, Object> data) {
        log.info("[executeStep] Executing step: {} for install: {}", stepId, installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        for (ActivationProcess.ActivationStep step : process.getSteps()) {
            if (step.getStepId().equals(stepId)) {
                step.setStatus(ActivationProcess.ActivationStep.StepStatus.COMPLETED);
                step.setEndTime(System.currentTimeMillis());
                step.setData(data);
                break;
            }
        }
        
        updateCurrentStep(process);
        process.setUpdateTime(System.currentTimeMillis());
        return process;
    }

    private void updateCurrentStep(ActivationProcess process) {
        List<ActivationProcess.ActivationStep> steps = process.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getStatus() == ActivationProcess.ActivationStep.StepStatus.PENDING) {
                process.setCurrentStep(i);
                return;
            }
        }
        process.setCurrentStep(steps.size() - 1);
    }

    @Override
    public KeyResult getKey(String installId) {
        log.info("[getKey] Getting key for install: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            log.warn("[getKey] Process not found: {}", installId);
            KeyResult result = new KeyResult();
            result.setKeyStatus("ERROR");
            result.setMessage("Process not found");
            return result;
        }
        
        if (keyManagementService != null) {
            try {
                KeyManagementService.KeyGenerateRequest request = new KeyManagementService.KeyGenerateRequest();
                request.setUserId(process.getActivator());
                request.setSceneGroupId(process.getSceneGroupId());
                request.setInstallId(installId);
                request.setScope("scene:" + process.getSceneGroupId());
                request.setDescription("Scene activation key for " + process.getSceneGroupId());
                
                KeyManagementService.KeyInfo keyInfo = keyManagementService.generateKey(request);
                
                KeyResult result = new KeyResult();
                result.setKeyId(keyInfo.getKeyId());
                result.setKeyValue(keyInfo.getKeyValue());
                result.setKeyStatus(keyInfo.getStatus().name());
                result.setExpireTime(keyInfo.getExpireTime());
                result.setMessage("Key generated successfully");
                
                process.setKeyId(result.getKeyId());
                process.setKeyStatus(result.getKeyStatus());
                
                log.info("[getKey] Key generated via KeyManagementService: {}", result.getKeyId());
                return result;
                
            } catch (Exception e) {
                log.error("[getKey] Failed to generate key via KeyManagementService: {}", e.getMessage());
            }
        }
        
        KeyResult result = new KeyResult();
        result.setKeyId("key-" + installId);
        result.setKeyStatus("ACTIVE");
        result.setExpireTime(System.currentTimeMillis() + 86400000L);
        result.setMessage("Key generated (fallback mode)");
        
        process.setKeyId(result.getKeyId());
        process.setKeyStatus(result.getKeyStatus());
        
        return result;
    }

    @Override
    public ActivationProcess confirmActivation(String installId) {
        log.info("[confirmActivation] Confirming activation for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        process.setStatus(ActivationProcess.ActivationStatus.COMPLETED);
        process.setUpdateTime(System.currentTimeMillis());

        if (process.getTemplateId() != null && process.getSceneGroupId() != null) {
            try {
                menuAutoRegisterService.registerMenusOnActivation(
                    process.getSceneGroupId(),
                    process.getTemplateId(),
                    process.getActivator(),
                    process.getRoleName()
                );
                process.setMenuRegistered(true);
                log.info("[confirmActivation] Menus registered for user: {}", process.getActivator());
            } catch (Exception e) {
                log.error("[confirmActivation] Failed to register menus: {}", e.getMessage());
            }
        }
        
        return process;
    }

    @Override
    public ActivationProcess cancelActivation(String installId) {
        log.info("[cancelActivation] Cancelling activation for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        process.setStatus(ActivationProcess.ActivationStatus.CANCELLED);
        process.setUpdateTime(System.currentTimeMillis());
        
        return process;
    }

    @Override
    public List<ActivationProcess.NetworkAction> getNetworkActions(String installId) {
        log.info("[getNetworkActions] Getting network actions for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process != null) {
            return process.getNetworkActions();
        }
        
        return new ArrayList<ActivationProcess.NetworkAction>();
    }

    @Override
    public CompletableFuture<ActivationProcess> executeNetworkActions(String installId) {
        log.info("[executeNetworkActions] Executing network actions for: {}", installId);
        
        return networkActionExecutor.executeAll(installId).thenApply(statuses -> {
            ActivationProcess process = processes.get(installId);
            if (process != null) {
                for (NetworkActionExecutor.NetworkActionStatus status : statuses) {
                    for (ActivationProcess.NetworkAction action : process.getNetworkActions()) {
                        if (action.getActionId().equals(status.getAction())) {
                            if ("COMPLETED".equals(status.getStatus())) {
                                action.setStatus(ActivationProcess.NetworkAction.ActionStatus.COMPLETED);
                            } else {
                                action.setStatus(ActivationProcess.NetworkAction.ActionStatus.FAILED);
                            }
                            action.setTimestamp(status.getTimestamp());
                            break;
                        }
                    }
                }
                process.setUpdateTime(System.currentTimeMillis());
            }
            return process;
        });
    }

    @Override
    public ActivationProcess skipStep(String installId, String stepId) {
        log.info("[skipStep] Skipping step: {} for install: {}", stepId, installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        for (ActivationProcess.ActivationStep step : process.getSteps()) {
            if (step.getStepId().equals(stepId)) {
                if (step.isSkippable()) {
                    step.setStatus(ActivationProcess.ActivationStep.StepStatus.SKIPPED);
                    step.setEndTime(System.currentTimeMillis());
                } else {
                    log.warn("[skipStep] Step {} is not skippable", stepId);
                }
                break;
            }
        }
        
        updateCurrentStep(process);
        process.setUpdateTime(System.currentTimeMillis());
        return process;
    }

    @Override
    public ActivationProcess configurePrivateCapabilities(String installId, List<String> enabledCapabilityIds) {
        log.info("[configurePrivateCapabilities] Configuring private capabilities for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        List<String> enabled = new ArrayList<>();
        List<ActivationProcess.PrivateCapabilityConfig> caps = process.getPrivateCapabilities();
        if (caps != null) {
            for (ActivationProcess.PrivateCapabilityConfig cap : caps) {
                if (enabledCapabilityIds.contains(cap.getCapId())) {
                    cap.setEnabled(true);
                    enabled.add(cap.getCapId());
                } else {
                    cap.setEnabled(false);
                }
            }
        }
        
        process.setEnabledPrivateCapabilities(enabled);
        process.setUpdateTime(System.currentTimeMillis());
        
        return process;
    }

    @Override
    public List<ActivationProcess.PrivateCapabilityConfig> getPrivateCapabilities(String installId) {
        ActivationProcess process = processes.get(installId);
        if (process != null) {
            return process.getPrivateCapabilities();
        }
        return new ArrayList<>();
    }
}
