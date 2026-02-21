package net.ooder.skill.business.dto;

import lombok.Data;
import java.util.Map;

@Data
public class WorkflowExecution {
    private String executionId;
    private String scenarioId;
    private String status;
    private Integer currentStep;
    private Integer totalSteps;
    private Map<String, Object> context;
    private Map<String, Object> result;
    private Long startTime;
    private Long endTime;
    private String error;
}
