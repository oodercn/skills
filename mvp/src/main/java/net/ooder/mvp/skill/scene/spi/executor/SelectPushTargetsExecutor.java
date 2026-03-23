package net.ooder.mvp.skill.scene.spi.executor;

import net.ooder.mvp.skill.scene.spi.SceneServices;
import net.ooder.mvp.skill.scene.spi.org.DepartmentInfo;
import net.ooder.mvp.skill.scene.template.ActivationStepConfig;
import net.ooder.mvp.skill.scene.capability.activation.ActivationProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SelectPushTargetsExecutor implements ActivationStepExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(SelectPushTargetsExecutor.class);
    
    private final SceneServices sceneServices;
    
    @Autowired
    public SelectPushTargetsExecutor(SceneServices sceneServices) {
        this.sceneServices = sceneServices;
    }
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "SELECT_PUSH_TARGETS".equals(stepConfig.getStepId()) ||
               "select-push-targets".equals(stepConfig.getStepId()) ||
               (stepConfig.getName() != null && stepConfig.getName().contains("选择推送目标"));
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        String stepId = stepConfig.getStepId();
        log.info("[SelectPushTargetsExecutor] Executing step: {} for process: {}", 
            stepId, process.getProcessId());
        
        StepResult result = new StepResult();
        result.setStepId(stepId);
        
        try {
            @SuppressWarnings("unchecked")
            List<String> targetIds = extractTargetIds(context);
            
            if (targetIds == null || targetIds.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("请选择推送目标");
                result.setErrorCode("TARGETS_REQUIRED");
                return result;
            }
            
            String pushType = (String) context.getOrDefault("pushType", "USER");
            
            Map<String, Object> output = new HashMap<>();
            output.put("pushTargetCount", targetIds.size());
            output.put("pushType", pushType);
            output.put("targetIds", targetIds);
            
            if ("DEPARTMENT".equals(pushType)) {
                Map<String, DepartmentInfo> departments = new HashMap<>();
                for (String deptId : targetIds) {
                    DepartmentInfo dept = sceneServices.getOrganizationService()
                        .getDepartment(deptId);
                    if (dept != null) {
                        departments.put(deptId, dept);
                    }
                }
                output.put("departments", departments);
                
                List<String> allMemberIds = new ArrayList<>();
                for (String deptId : targetIds) {
                    List<String> members = sceneServices.getOrganizationService()
                        .getDepartmentMembers(deptId);
                    allMemberIds.addAll(members);
                }
                output.put("totalMemberCount", allMemberIds.size());
                output.put("allMemberIds", allMemberIds);
                
            } else if ("USER".equals(pushType)) {
                Map<String, net.ooder.mvp.skill.scene.spi.user.UserInfo> users = sceneServices.getUserService()
                    .getUsers(targetIds);
                output.put("users", users);
            }
            
            result.setSuccess(true);
            result.setOutput(output);
            
            log.info("[SelectPushTargetsExecutor] Selected {} push targets for process {}", 
                targetIds.size(), process.getProcessId());
            
        } catch (Exception e) {
            log.error("[SelectPushTargetsExecutor] Execution failed: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage("执行失败: " + e.getMessage());
            result.setErrorCode("EXECUTION_ERROR");
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> extractTargetIds(Map<String, Object> context) {
        Object targetIds = context.get("pushTargets");
        if (targetIds instanceof List) {
            return ((List<?>) targetIds).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        targetIds = context.get("targetIds");
        if (targetIds instanceof List) {
            return ((List<?>) targetIds).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        targetIds = context.get("departmentIds");
        if (targetIds instanceof List) {
            return ((List<?>) targetIds).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        return null;
    }
}
