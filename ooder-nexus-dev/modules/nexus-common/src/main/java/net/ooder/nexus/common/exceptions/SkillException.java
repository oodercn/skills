package net.ooder.nexus.common.exceptions;

public class SkillException extends NexusException {
    
    private String skillId;
    
    public SkillException(String skillId, String message) {
        super("SKILL_ERROR", message, skillId);
        this.skillId = skillId;
    }
    
    public SkillException(String skillId, String message, Throwable cause) {
        super(message, cause);
        this.skillId = skillId;
    }
    
    public String getSkillId() {
        return skillId;
    }
}
