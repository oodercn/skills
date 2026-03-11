package net.ooder.skill.scheduler.quartz.dto;

import lombok.Data;

@Data
public class TaskExecution {
    private String executionId;
    private String taskId;
    private Long startTime;
    private Long endTime;
    private String status;
    private String result;
    private String error;
}
