package net.ooder.skill.business.dto;

import lombok.Data;
import java.util.Map;

@Data
public class WorkflowExecuteRequest {
    private String scenarioId;
    private Map<String, Object> context;
}
