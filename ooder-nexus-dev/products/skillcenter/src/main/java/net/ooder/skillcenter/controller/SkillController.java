package net.ooder.skillcenter.controller;

import net.ooder.nexus.protocol.dto.ApiResponse;
import net.ooder.nexus.protocol.dto.PageResult;
import net.ooder.nexus.protocol.dto.SkillMetadata;
import net.ooder.skillcenter.service.SkillManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {
    
    @Autowired
    private SkillManagementService skillService;
    
    @GetMapping("/index")
    public ResponseEntity<ApiResponse<List<SkillMetadata>>> getIndex() {
        List<SkillMetadata> skills = skillService.getRemoteSkills();
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
    
    @GetMapping("/local")
    public ResponseEntity<ApiResponse<List<SkillMetadata>>> getLocalSkills() {
        List<SkillMetadata> skills = skillService.getLocalSkills();
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
    
    @GetMapping("/local/category/{category}")
    public ResponseEntity<ApiResponse<List<SkillMetadata>>> getLocalSkillsByCategory(
            @PathVariable String category) {
        List<SkillMetadata> skills = skillService.getLocalSkillsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
    
    @GetMapping("/local/{skillId}")
    public ResponseEntity<ApiResponse<SkillMetadata>> getLocalSkill(
            @PathVariable String skillId) {
        SkillMetadata skill = skillService.getLocalSkill(skillId);
        if (skill == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "Skill not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(skill));
    }
    
    @GetMapping("/remote")
    public ResponseEntity<ApiResponse<List<SkillMetadata>>> getRemoteSkills() {
        List<SkillMetadata> skills = skillService.getRemoteSkills();
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
    
    @GetMapping("/remote/{skillId}")
    public ResponseEntity<ApiResponse<SkillMetadata>> getRemoteSkill(
            @PathVariable String skillId) {
        SkillMetadata skill = skillService.getRemoteSkill(skillId);
        if (skill == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "Skill not found in index"));
        }
        return ResponseEntity.ok(ApiResponse.success(skill));
    }
    
    @PostMapping("/install/{skillId}")
    public ResponseEntity<ApiResponse<Boolean>> installSkill(
            @PathVariable String skillId) {
        try {
            boolean result = skillService.installSkill(skillId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "Failed to install: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/uninstall/{skillId}")
    public ResponseEntity<ApiResponse<Boolean>> uninstallSkill(
            @PathVariable String skillId) {
        try {
            boolean result = skillService.uninstallSkill(skillId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "Failed to uninstall: " + e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Boolean>> refresh() {
        try {
            skillService.refresh();
            return ResponseEntity.ok(ApiResponse.success(true));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "Failed to refresh: " + e.getMessage()));
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = skillService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
