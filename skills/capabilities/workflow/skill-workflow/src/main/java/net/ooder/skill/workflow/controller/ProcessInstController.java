package net.ooder.skill.workflow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import net.ooder.skill.workflow.dto.ProcessInstDTO;
import net.ooder.skill.workflow.dto.StartProcessRequest;
import net.ooder.skill.workflow.service.WorkflowService;

@RestController
@RequestMapping("/api/v1/workflow/process-instances")
@CrossOrigin(origins = "*")
public class ProcessInstController {

    private final WorkflowService workflowService;

    public ProcessInstController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping
    public List<ProcessInstDTO> listProcessInsts(@RequestParam(required = false) String status) {
        return workflowService.listProcessInsts(status);
    }

    @GetMapping("/my")
    public List<ProcessInstDTO> listMyProcessInsts(@RequestParam String userId) {
        return workflowService.listMyProcessInsts(userId);
    }

    @GetMapping("/started-by/{userId}")
    public List<ProcessInstDTO> listProcessInstsStartedBy(@PathVariable String userId) {
        return workflowService.listProcessInstsStartedBy(userId);
    }

    @GetMapping("/{id}")
    public ProcessInstDTO getProcessInst(@PathVariable String id) {
        return workflowService.getProcessInst(id);
    }

    @PostMapping
    public ProcessInstDTO startProcess(@RequestBody StartProcessRequest request, @RequestParam String userId) {
        return workflowService.startProcess(request, userId);
    }

    @PostMapping("/{id}/suspend")
    public ProcessInstDTO suspendProcess(@PathVariable String id) {
        return workflowService.suspendProcess(id);
    }

    @PostMapping("/{id}/resume")
    public ProcessInstDTO resumeProcess(@PathVariable String id) {
        return workflowService.resumeProcess(id);
    }

    @PostMapping("/{id}/terminate")
    public ProcessInstDTO terminateProcess(@PathVariable String id, @RequestParam(required = false) String reason) {
        return workflowService.terminateProcess(id, reason);
    }

    @GetMapping("/{id}/variables")
    public Map<String, Object> getProcessVariables(@PathVariable String id) {
        return workflowService.getProcessVariables(id);
    }

    @PutMapping("/{id}/variables")
    public void setProcessVariables(@PathVariable String id, @RequestBody Map<String, Object> variables) {
        workflowService.setProcessVariables(id, variables);
    }
}
