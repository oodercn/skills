package net.ooder.skill.business.service.impl;

import net.ooder.skill.business.dto.*;
import net.ooder.skill.business.service.BusinessService;
import net.ooder.skill.common.storage.JsonStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Value("${business.data-path:./data/business}")
    private String dataPath;
    
    private JsonStorage storage;
    private Map<String, BusinessScenario> scenarios;
    private Map<String, WorkflowExecution> executions;
    
    private static final String SCENARIOS_KEY = "scenarios";
    
    @PostConstruct
    public void init() {
        storage = new JsonStorage(dataPath);
        scenarios = new ConcurrentHashMap<>();
        executions = new ConcurrentHashMap<>();
        
        List<BusinessScenario> savedScenarios = storage.loadList(SCENARIOS_KEY, BusinessScenario.class);
        if (savedScenarios != null) {
            for (BusinessScenario scenario : savedScenarios) {
                scenarios.put(scenario.getScenarioId(), scenario);
            }
        }
    }
    
    private void saveScenarios() {
        storage.save(SCENARIOS_KEY, new ArrayList<>(scenarios.values()));
    }

    @Override
    public BusinessScenario createScenario(ScenarioCreateRequest request) {
        BusinessScenario scenario = new BusinessScenario();
        scenario.setScenarioId("scenario-" + UUID.randomUUID().toString().substring(0, 8));
        scenario.setName(request.getName());
        scenario.setDescription(request.getDescription());
        scenario.setType(request.getType() != null ? request.getType() : "default");
        scenario.setStatus("created");
        scenario.setSteps(request.getSteps() != null ? request.getSteps() : new ArrayList<>());
        scenario.setConfig(request.getConfig() != null ? request.getConfig() : new HashMap<>());
        scenario.setCreatorId(request.getCreatorId());
        scenario.setCreateTime(System.currentTimeMillis());
        scenario.setUpdateTime(System.currentTimeMillis());
        
        scenarios.put(scenario.getScenarioId(), scenario);
        saveScenarios();
        
        return scenario;
    }

    @Override
    public List<BusinessScenario> listScenarios(String type, String creatorId) {
        return scenarios.values().stream()
            .filter(s -> type == null || type.isEmpty() || type.equals(s.getType()))
            .filter(s -> creatorId == null || creatorId.isEmpty() || creatorId.equals(s.getCreatorId()))
            .collect(Collectors.toList());
    }

    @Override
    public BusinessScenario getScenario(String scenarioId) {
        return scenarios.get(scenarioId);
    }

    @Override
    public BusinessScenario updateScenario(String scenarioId, ScenarioUpdateRequest request) {
        BusinessScenario scenario = scenarios.get(scenarioId);
        if (scenario == null) {
            return null;
        }
        
        if (request.getName() != null) {
            scenario.setName(request.getName());
        }
        if (request.getDescription() != null) {
            scenario.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            scenario.setType(request.getType());
        }
        if (request.getSteps() != null) {
            scenario.setSteps(request.getSteps());
        }
        if (request.getConfig() != null) {
            scenario.setConfig(request.getConfig());
        }
        scenario.setUpdateTime(System.currentTimeMillis());
        
        saveScenarios();
        return scenario;
    }

    @Override
    public boolean deleteScenario(String scenarioId) {
        BusinessScenario removed = scenarios.remove(scenarioId);
        if (removed != null) {
            saveScenarios();
            return true;
        }
        return false;
    }

    @Override
    public WorkflowExecution executeWorkflow(WorkflowExecuteRequest request) {
        BusinessScenario scenario = scenarios.get(request.getScenarioId());
        if (scenario == null) {
            return null;
        }
        
        WorkflowExecution execution = new WorkflowExecution();
        execution.setExecutionId("exec-" + UUID.randomUUID().toString().substring(0, 8));
        execution.setScenarioId(request.getScenarioId());
        execution.setStatus("running");
        execution.setCurrentStep(0);
        execution.setTotalSteps(scenario.getSteps() != null ? scenario.getSteps().size() : 0);
        execution.setContext(request.getContext() != null ? request.getContext() : new HashMap<>());
        execution.setStartTime(System.currentTimeMillis());
        
        executions.put(execution.getExecutionId(), execution);
        
        simulateWorkflowExecution(execution, scenario);
        
        return execution;
    }
    
    private void simulateWorkflowExecution(WorkflowExecution execution, BusinessScenario scenario) {
        new Thread(() -> {
            try {
                if (scenario.getSteps() != null) {
                    for (int i = 0; i < scenario.getSteps().size(); i++) {
                        Thread.sleep(100);
                        execution.setCurrentStep(i + 1);
                    }
                }
                
                execution.setStatus("completed");
                execution.setEndTime(System.currentTimeMillis());
                
                Map<String, Object> result = new HashMap<>();
                result.put("stepsExecuted", execution.getTotalSteps());
                result.put("duration", execution.getEndTime() - execution.getStartTime());
                execution.setResult(result);
            } catch (InterruptedException e) {
                execution.setStatus("failed");
                execution.setError(e.getMessage());
                execution.setEndTime(System.currentTimeMillis());
            }
        }).start();
    }

    @Override
    public WorkflowExecution getExecutionStatus(String executionId) {
        return executions.get(executionId);
    }

    @Override
    public DataProcessResult processData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return DataProcessResult.fail("No data provided");
        }
        
        Map<String, Object> processed = new HashMap<>();
        processed.put("original", data);
        processed.put("processedAt", System.currentTimeMillis());
        processed.put("keys", data.keySet());
        processed.put("size", data.size());
        
        return DataProcessResult.success(processed);
    }
}
