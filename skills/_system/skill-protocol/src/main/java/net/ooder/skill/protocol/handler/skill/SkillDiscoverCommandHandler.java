package net.ooder.skill.protocol.handler.skill;

import net.ooder.skill.management.SkillManager;
import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SkillDiscoverCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SkillManager skillManager;
    
    @Override
    public String getCommand() {
        return "skill.discover";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String category = getParamAsString(message, "category");
        String skillId = getParamAsString(message, "skill_id");
        
        List<SkillDefinition> skills;
        
        if (skillId != null && !skillId.isEmpty()) {
            SkillDefinition skill = skillManager.getSkill(skillId);
            if (skill == null) {
                return buildErrorResponse(message, ErrorCodes.SKILL_NOT_FOUND, 
                    "Skill not found: " + skillId);
            }
            skills = new ArrayList<>();
            skills.add(skill);
        } else if (category != null && !category.isEmpty()) {
            skills = skillManager.getSkillsByCategory(category);
        } else {
            skills = skillManager.getAllSkills();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SkillDefinition skill : skills) {
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("skill_id", skill.getSkillId());
            skillInfo.put("name", skill.getName());
            skillInfo.put("description", skill.getDescription());
            skillInfo.put("category", skill.getCategory());
            skillInfo.put("version", skill.getVersion());
            skillInfo.put("status", skill.getStatus().name());
            skillInfo.put("available", skill.isAvailable());
            skillInfo.put("run_count", skill.getRunCount());
            result.add(skillInfo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("skills", result);
        response.put("total", result.size());
        
        return buildSuccessResponse(message, response);
    }
}
