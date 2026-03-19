package net.ooder.skill.business.controller;

import net.ooder.skill.business.dto.*;
import net.ooder.skill.business.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @PostMapping("/scenario/create")
    public ResponseEntity<BusinessScenario> createScenario(@RequestBody ScenarioCreateRequest request) {
        BusinessScenario scenario = businessService.createScenario(request);
        return ResponseEntity.ok(scenario);
    }

    @GetMapping("/scenario/list")
    public ResponseEntity<List<BusinessScenario>> listScenarios(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String creatorId) {
        return ResponseEntity.ok(businessService.listScenarios(type, creatorId));
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<BusinessScenario> getScenario(@PathVariable String scenarioId) {
        BusinessScenario scenario = businessService.getScenario(scenarioId);
        if (scenario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scenario);
    }

    @PutMapping("/scenario/{scenarioId}")
    public ResponseEntity<BusinessScenario> updateScenario(
            @PathVariable String scenarioId,
            @RequestBody ScenarioUpdateRequest request) {
        BusinessScenario scenario = businessService.updateScenario(scenarioId, request);
        if (scenario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scenario);
    }

    @DeleteMapping("/scenario/{scenarioId}")
    public ResponseEntity<Boolean> deleteScenario(@PathVariable String scenarioId) {
        boolean result = businessService.deleteScenario(scenarioId);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/workflow/execute")
    public ResponseEntity<WorkflowExecution> executeWorkflow(@RequestBody WorkflowExecuteRequest request) {
        WorkflowExecution execution = businessService.executeWorkflow(request);
        if (execution == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(execution);
    }

    @GetMapping("/workflow/status/{executionId}")
    public ResponseEntity<WorkflowExecution> getExecutionStatus(@PathVariable String executionId) {
        WorkflowExecution execution = businessService.getExecutionStatus(executionId);
        if (execution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(execution);
    }

    @PostMapping("/data/process")
    public ResponseEntity<DataProcessResult> processData(@RequestBody Map<String, Object> data) {
        DataProcessResult result = businessService.processData(data);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }
}
