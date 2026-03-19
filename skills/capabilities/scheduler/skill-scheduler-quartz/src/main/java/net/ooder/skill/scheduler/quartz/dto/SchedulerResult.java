package net.ooder.skill.scheduler.quartz.dto;

import lombok.Data;

@Data
public class SchedulerResult {
    private Boolean success;
    private String message;
    private String taskId;
    
    public static SchedulerResult success(String taskId) {
        SchedulerResult result = new SchedulerResult();
        result.setSuccess(true);
        result.setMessage("Success");
        result.setTaskId(taskId);
        return result;
    }
    
    public static SchedulerResult success(String message, String taskId) {
        SchedulerResult result = new SchedulerResult();
        result.setSuccess(true);
        result.setMessage(message);
        result.setTaskId(taskId);
        return result;
    }
    
    public static SchedulerResult fail(String message) {
        SchedulerResult result = new SchedulerResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
