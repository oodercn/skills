package net.ooder.skill.business.service;

import net.ooder.skill.business.dto.*;

import java.util.List;
import java.util.Map;

public interface BusinessService {
    BusinessScenario createScenario(ScenarioCreateRequest request);
    List<BusinessScenario> listScenarios(String type, String creatorId);
    BusinessScenario getScenario(String scenarioId);
    BusinessScenario updateScenario(String scenarioId, ScenarioUpdateRequest request);
    boolean deleteScenario(String scenarioId);
    WorkflowExecution executeWorkflow(WorkflowExecuteRequest request);
    WorkflowExecution getExecutionStatus(String executionId);
    DataProcessResult processData(Map<String, Object> data);
}
