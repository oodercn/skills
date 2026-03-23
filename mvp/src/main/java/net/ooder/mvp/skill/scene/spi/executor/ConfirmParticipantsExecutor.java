package net.ooder.mvp.skill.scene.spi.executor;

import net.ooder.mvp.skill.scene.spi.SceneServices;
import net.ooder.mvp.skill.scene.spi.user.UserInfo;
import net.ooder.mvp.skill.scene.template.ActivationStepConfig;
import net.ooder.mvp.skill.scene.capability.activation.ActivationProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConfirmParticipantsExecutor implements ActivationStepExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(ConfirmParticipantsExecutor.class);
    
    private final SceneServices sceneServices;
    
    @Autowired
    public ConfirmParticipantsExecutor(SceneServices sceneServices) {
        this.sceneServices = sceneServices;
    }
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "CONFIRM_PARTICIPANTS".equals(stepConfig.getStepId()) ||
               "confirm-participants".equals(stepConfig.getStepId()) ||
               (stepConfig.getName() != null && stepConfig.getName().contains("确认参与者"));
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        String stepId = stepConfig.getStepId();
        log.info("[ConfirmParticipantsExecutor] Executing step: {} for process: {}", 
            stepId, process.getProcessId());
        
        StepResult result = new StepResult();
        result.setStepId(stepId);
        
        try {
            @SuppressWarnings("unchecked")
            List<String> participantIds = (List<String>) context.get("participants");
            
            if (participantIds == null || participantIds.isEmpty()) {
                participantIds = extractParticipantIds(context);
            }
            
            if (participantIds == null || participantIds.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("请选择参与者");
                result.setErrorCode("PARTICIPANTS_REQUIRED");
                return result;
            }
            
            Map<String, UserInfo> users = sceneServices.getUserService()
                .getUsers(participantIds);
            
            if (users.size() != participantIds.size()) {
                List<String> notFound = participantIds.stream()
                    .filter(id -> !users.containsKey(id))
                    .collect(Collectors.toList());
                result.setSuccess(false);
                result.setErrorMessage("以下用户不存在: " + String.join(", ", notFound));
                result.setErrorCode("USERS_NOT_FOUND");
                return result;
            }
            
            Map<String, Object> output = new HashMap<>();
            output.put("participantCount", participantIds.size());
            output.put("participants", users);
            output.put("participantIds", participantIds);
            
            result.setSuccess(true);
            result.setOutput(output);
            
            log.info("[ConfirmParticipantsExecutor] Confirmed {} participants for process {}", 
                participantIds.size(), process.getProcessId());
            
        } catch (Exception e) {
            log.error("[ConfirmParticipantsExecutor] Execution failed: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage("执行失败: " + e.getMessage());
            result.setErrorCode("EXECUTION_ERROR");
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> extractParticipantIds(Map<String, Object> context) {
        Object participants = context.get("participants");
        if (participants instanceof List) {
            return ((List<?>) participants).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        Object memberIds = context.get("memberIds");
        if (memberIds instanceof List) {
            return ((List<?>) memberIds).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        Object userIds = context.get("userIds");
        if (userIds instanceof List) {
            return ((List<?>) userIds).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        
        return null;
    }
}
