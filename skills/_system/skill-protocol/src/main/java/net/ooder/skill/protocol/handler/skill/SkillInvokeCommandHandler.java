package net.ooder.skill.protocol.handler.skill;

import net.ooder.skill.management.SkillManager;
import net.ooder.skill.management.model.SkillContext;
import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillException;
import net.ooder.skill.management.model.SkillResult;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SkillInvokeCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SkillManager skillManager;
    
    @Override
    public String getCommand() {
        return "skill.invoke";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String skillId = getParamAsString(message, "skill_id");
        
        if (skillId == null || skillId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: skill_id");
        }
        
        SkillDefinition skill = skillManager.getSkill(skillId);
        if (skill == null) {
            return buildErrorResponse(message, ErrorCodes.SKILL_NOT_FOUND, 
                "Skill not found: " + skillId);
        }
        
        if (!skill.isAvailable()) {
            return buildErrorResponse(message, ErrorCodes.SKILL_INVOKE_ERROR, 
                "Skill is not available: " + skillId);
        }
        
        SkillContext context = new SkillContext();
        
        Map<String, Object> params = message.getParams();
        if (params != null) {
            context.setParams(params);
        }
        
        try {
            SkillResult result = skillManager.executeSkill(skillId, context);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("data", result.getData());
            response.put("execution_time", result.getExecutionTime());
            response.put("skill_id", result.getSkillId());
            
            if (result.isSuccess()) {
                return buildSuccessResponse(message, response);
            } else {
                return buildErrorResponse(message, ErrorCodes.SKILL_INVOKE_ERROR, 
                    result.getMessage());
            }
            
        } catch (SkillException e) {
            logger.error("Skill invocation failed: " + skillId, e);
            return buildErrorResponse(message, ErrorCodes.SKILL_INVOKE_ERROR, 
                "Skill invocation failed: " + e.getMessage());
        }
    }
}
