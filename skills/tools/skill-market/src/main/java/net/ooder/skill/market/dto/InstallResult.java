package net.ooder.skill.market.dto;

import lombok.Data;

@Data
public class InstallResult {
    private Boolean success;
    private String message;
    private String skillId;
    private String version;
    
    public static InstallResult success(String skillId, String version) {
        InstallResult result = new InstallResult();
        result.setSuccess(true);
        result.setMessage("Skill installed successfully");
        result.setSkillId(skillId);
        result.setVersion(version);
        return result;
    }
    
    public static InstallResult fail(String message) {
        InstallResult result = new InstallResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
