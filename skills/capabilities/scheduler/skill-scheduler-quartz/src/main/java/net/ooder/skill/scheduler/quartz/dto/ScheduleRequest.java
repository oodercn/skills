package net.ooder.skill.scheduler.quartz.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ScheduleRequest {
    private String taskName;
    private String cronExpression;
    private Map<String, Object> taskData;
    private Map<String, Object> options;
}
