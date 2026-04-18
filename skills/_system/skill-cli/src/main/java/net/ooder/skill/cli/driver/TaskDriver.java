package net.ooder.skill.cli.driver;

import net.ooder.skill.cli.model.TaskEntity;
import net.ooder.skill.cli.model.TaskStatus;
import net.ooder.skill.cli.model.SubmitResult;

import java.util.List;
import java.util.Map;

public interface TaskDriver {
    
    String getDriverId();
    
    String getDriverName();
    
    boolean isAvailable();
    
    SubmitResult submit(String sceneId, String skillId, String capabilityId, 
                        Map<String, Object> params);
    
    boolean cancel(String taskId);
    
    boolean pause(String taskId);
    
    boolean resume(String taskId);
    
    TaskEntity getTask(String taskId);
    
    TaskStatus getStatus(String taskId);
    
    List<TaskEntity> getTasksByScene(String sceneId);
    
    List<TaskEntity> getTasksBySkill(String skillId);
    
    List<TaskEntity> getActiveTasks();
    
    Map<String, Object> getResult(String taskId);
    
    void refresh();
}
