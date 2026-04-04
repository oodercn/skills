package net.ooder.skill.workflow.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import net.ooder.skill.bpm.model.ProcessDef;
import net.ooder.skill.bpm.service.ProcessDefService;
import net.ooder.skill.workflow.dto.ProcessDefDTO;
import net.ooder.skill.workflow.service.WorkflowService;

@RestController
@RequestMapping("/api/v1/workflow/process-definitions")
@CrossOrigin(origins = "*")
public class ProcessDefController {

    private final WorkflowService workflowService;
    private final ProcessDefService processDefService;

    public ProcessDefController(WorkflowService workflowService, ProcessDefService processDefService) {
        this.workflowService = workflowService;
        this.processDefService = processDefService;
    }

    @GetMapping
    public List<ProcessDefDTO> listProcessDefs(@RequestParam(required = false) String category) {
        return workflowService.listProcessDefs(category);
    }

    @GetMapping("/published")
    public List<ProcessDefDTO> listPublishedProcessDefs() {
        return workflowService.listPublishedProcessDefs();
    }

    @GetMapping("/{id}")
    public ProcessDefDTO getProcessDef(@PathVariable String id) {
        return workflowService.getProcessDef(id);
    }

    @PostMapping
    public ProcessDefDTO createProcessDef(@RequestBody ProcessDefDTO dto) {
        return workflowService.createProcessDef(dto);
    }

    @PutMapping("/{id}")
    public ProcessDefDTO updateProcessDef(@PathVariable String id, @RequestBody ProcessDefDTO dto) {
        dto.setId(id);
        return workflowService.updateProcessDef(dto);
    }

    @DeleteMapping("/{id}")
    public boolean deleteProcessDef(@PathVariable String id) {
        return workflowService.deleteProcessDef(id);
    }

    @PostMapping("/{id}/publish")
    public ProcessDefDTO publishProcessDef(@PathVariable String id) {
        return workflowService.publishProcessDef(id);
    }

    @PostMapping("/{id}/unpublish")
    public ProcessDefDTO unpublishProcessDef(@PathVariable String id) {
        return workflowService.unpublishProcessDef(id);
    }
}
