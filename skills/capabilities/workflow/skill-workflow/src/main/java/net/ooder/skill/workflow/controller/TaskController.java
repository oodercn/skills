package net.ooder.skill.workflow.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import net.ooder.skill.workflow.dto.CompleteTaskRequest;
import net.ooder.skill.workflow.dto.TaskDTO;
import net.ooder.skill.workflow.service.WorkflowService;

@RestController
@RequestMapping("/api/v1/workflow/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final WorkflowService workflowService;

    public TaskController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/my")
    public List<TaskDTO> listMyTasks(@RequestParam String userId) {
        return workflowService.listMyTasks(userId);
    }

    @GetMapping("/my/completed")
    public List<TaskDTO> listMyCompletedTasks(@RequestParam String userId) {
        return workflowService.listMyCompletedTasks(userId);
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable String id) {
        return workflowService.getTask(id);
    }

    @PostMapping("/{id}/claim")
    public TaskDTO claimTask(@PathVariable String id, @RequestParam String userId) {
        return workflowService.claimTask(id, userId);
    }

    @PostMapping("/{id}/release")
    public TaskDTO releaseTask(@PathVariable String id, @RequestParam String userId) {
        return workflowService.releaseTask(id, userId);
    }

    @PostMapping("/{id}/complete")
    public TaskDTO completeTask(@PathVariable String id, @RequestBody CompleteTaskRequest request) {
        request.setTaskId(id);
        return workflowService.completeTask(request);
    }

    @GetMapping("/process/{processInstId}")
    public List<TaskDTO> listTasksByProcess(@PathVariable String processInstId) {
        return workflowService.listTasksByProcess(processInstId);
    }
}
