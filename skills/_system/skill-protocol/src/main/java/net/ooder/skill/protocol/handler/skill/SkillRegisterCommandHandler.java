package net.ooder.skill.protocol.handler.skill;

import net.ooder.skill.management.SkillManager;
import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SkillRegisterCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SkillManager skillManager;
    
    @Override
    public String getCommand() {
        return "skill.register";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String skillId = getParamAsString(message, "skill_id");
        String name = getParamAsString(message, "name");
        String description = getParamAsString(message, "description");
        String category = getParamAsString(message, "category");
        String version = getParamAsString(message, "version");
        
        if (skillId == null || skillId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: skill_id");
        }
        
        if (name == null || name.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: name");
        }
        
        SkillDefinition existingSkill = skillManager.getSkill(skillId);
        if (existingSkill != null) {
            return buildErrorResponse(message, ErrorCodes.CONFLICT, 
                "Skill already registered: " + skillId);
        }
        
        SkillDefinition skill = new SkillDefinition();
        skill.setSkillId(skillId);
        skill.setName(name);
        skill.setDescription(description != null ? description : "");
        skill.setCategory(category != null ? category : "general");
        skill.setVersion(version != null ? version : "1.0.0");
        skill.setStatus(SkillDefinition.SkillStatus.IDLE);
        
        skillManager.registerSkill(skill);
        
        Map<String, Object> response = new HashMap<>();
        response.put("skill_id", skillId);
        response.put("name", name);
        response.put("status", "registered");
        response.put("message", "Skill registered successfully");
        
        return buildSuccessResponse(message, response);
    }
}
