package net.ooder.skill.template.controller;

import net.ooder.skill.template.dto.PageResult;
import net.ooder.skill.template.dto.FusedWorkflowTemplateDTO;
import net.ooder.skill.template.dto.FusionPreviewDTO;
import net.ooder.skill.template.dto.FusionRequestDTO;
import net.ooder.skill.template.dto.ConflictResolutionRequestDTO;
import net.ooder.skill.template.dto.TemplateVersionDTO;
import net.ooder.skill.template.dto.ProcedureMatchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fused-templates")
public class FusionTemplateController {

    private static final Logger log = LoggerFactory.getLogger(FusionTemplateController.class);

    @Autowired(required = false)
    private FusionTemplateService fusionService;

    @PostMapping("/fuse")
    public ResponseEntity<FusedWorkflowTemplateDTO> fuse(@RequestBody FusionRequestDTO request) {
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        FusedWorkflowTemplateDTO template = fusionService.fuse(request);
        return ResponseEntity.ok(template);
    }

    @PostMapping("/preview")
    public ResponseEntity<FusionPreviewDTO> preview(@RequestBody FusionRequestDTO request) {
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        FusionPreviewDTO preview = fusionService.preview(request);
        return ResponseEntity.ok(preview);
    }

    @GetMapping
    public ResponseEntity<PageResult<FusedWorkflowTemplateDTO>> list(
            @RequestParam(required = false) String skillId,
            @RequestParam(required = false) String procedureId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        if (fusionService == null) {
            return ResponseEntity.ok(new PageResult<>());
        }
        
        try {
            List<FusedWorkflowTemplateDTO> templates = fusionService.list(skillId, procedureId, status, page, pageSize);
            
            PageResult<FusedWorkflowTemplateDTO> result = new PageResult<>();
            result.setItems(templates != null ? templates : new ArrayList<>());
            result.setPageNum(page);
            result.setPageSize(pageSize);
            result.setTotal(templates != null ? templates.size() : 0);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new PageResult<>());
        }
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<FusedWorkflowTemplateDTO> get(@PathVariable String templateId) {
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        FusedWorkflowTemplateDTO template = fusionService.get(templateId);
        if (template == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(template);
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> delete(@PathVariable String templateId) {
        if (fusionService != null) {
            fusionService.delete(templateId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{templateId}/resolve-conflict")
    public ResponseEntity<FusedWorkflowTemplateDTO> resolveConflict(
            @PathVariable String templateId,
            @RequestBody ConflictResolutionRequestDTO request) {
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        FusedWorkflowTemplateDTO template = fusionService.resolveConflict(templateId, request);
        return ResponseEntity.ok(template);
    }

    @GetMapping("/{templateId}/versions")
    public ResponseEntity<List<TemplateVersionDTO>> getVersions(@PathVariable String templateId) {
        if (fusionService == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<TemplateVersionDTO> versions = fusionService.getVersionHistory(templateId);
        return ResponseEntity.ok(versions != null ? versions : new ArrayList<>());
    }

    @PostMapping("/{templateId}/rollback")
    public ResponseEntity<FusedWorkflowTemplateDTO> rollback(
            @PathVariable String templateId,
            @RequestParam int version) {
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        FusedWorkflowTemplateDTO template = fusionService.rollback(templateId, version);
        return ResponseEntity.ok(template);
    }

    @GetMapping("/match/{skillId}")
    public ResponseEntity<List<ProcedureMatchResultDTO>> matchProcedures(@PathVariable String skillId) {
        if (fusionService == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<ProcedureMatchResultDTO> matches = fusionService.matchProcedures(skillId);
        return ResponseEntity.ok(matches != null ? matches : new ArrayList<>());
    }

    @GetMapping("/match-score")
    public ResponseEntity<Map<String, Object>> calculateMatchScore(
            @RequestParam String procedureId,
            @RequestParam String skillId) {
        
        if (fusionService == null) {
            return ResponseEntity.internalServerError().build();
        }
        
        int score = fusionService.calculateMatchScore(procedureId, skillId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("procedureId", procedureId);
        result.put("skillId", skillId);
        result.put("matchScore", score);
        result.put("level", getMatchLevel(score));
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<Map<String, Object>>> listPriorities() {
        List<Map<String, Object>> priorities = new ArrayList<>();
        
        String[] priorityNames = {"ENTERPRISE_FIRST", "SKILL_FIRST", "MERGE", "USER_DECIDE"};
        String[] priorityLabels = {"企业规范优先", "技能定义优先", "合并", "用户决定"};
        String[] priorityDescs = {
            "当存在冲突时，使用企业规范流程的值",
            "当存在冲突时，使用技能定义的值",
            "尝试合并两边的值，无法合并时提示用户",
            "所有冲突都需要用户手动决定"
        };
        
        for (int i = 0; i < priorityNames.length; i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("code", priorityNames[i]);
            p.put("name", priorityLabels[i]);
            p.put("description", priorityDescs[i]);
            priorities.add(p);
        }
        
        return ResponseEntity.ok(priorities);
    }

    private String getMatchLevel(int score) {
        if (score >= 90) return "HIGH";
        if (score >= 70) return "MEDIUM";
        if (score >= 50) return "LOW";
        return "NONE";
    }
}
