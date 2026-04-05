package net.ooder.skill.scene.dto.dailyreport;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ReminderRequestDTO {
    
    @NotBlank(message = "鍦烘櫙缁処D涓嶈兘涓虹┖")
    private String sceneGroupId;
    
    private List<String> targetUsers;
    
    private String message;

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public List<String> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(List<String> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
