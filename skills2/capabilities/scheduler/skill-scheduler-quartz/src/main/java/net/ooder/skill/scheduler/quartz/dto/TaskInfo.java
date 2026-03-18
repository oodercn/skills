package net.ooder.skill.scheduler.quartz.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TaskInfo {
    private String taskId;
    private String taskName;
    private String cronExpression;
    private String status;
    private Long lastExecutionTime;
    private Long nextExecutionTime;
    private Integer executionCount;
    private Map<String, Object> taskData;
    private Long createTime;
}
