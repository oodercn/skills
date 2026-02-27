package net.ooder.nexus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ooder.nexus.model.SkillIndex;
import net.ooder.nexus.model.SkillIndex.SkillEntry;
import net.ooder.nexus.service.SkillDiscoveryService;
import net.ooder.skill.installer.SkillInstallerService;
import net.ooder.skill.installer.SkillInstallerService.InstallProgress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    
    private final SkillDiscoveryService discoveryService;
    private final SkillInstallerService installerService;
    
    @GetMapping("/index")
    public ResponseEntity<SkillIndex> getSkillIndex() {
        return ResponseEntity.ok(discoveryService.getSkillIndex());
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<SkillIndex.Category>> getCategories() {
        return ResponseEntity.ok(discoveryService.getCategories());
    }
    
    @GetMapping("/remote")
    public ResponseEntity<List<SkillEntry>> getRemoteSkills(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(discoveryService.getRemoteSkillsByCategory(category));
        }
        return ResponseEntity.ok(discoveryService.getRemoteSkills());
    }
    
    @GetMapping("/local")
    public ResponseEntity<List<Map<String, Object>>> getLocalSkills() {
        return ResponseEntity.ok(discoveryService.getLocalSkills());
    }
    
    @GetMapping("/installed")
    public ResponseEntity<List<String>> getInstalledSkills() {
        return ResponseEntity.ok(installerService.listInstalled());
    }
    
    @GetMapping("/{skillId}")
    public ResponseEntity<?> getSkill(@PathVariable String skillId) {
        SkillEntry remote = discoveryService.getRemoteSkill(skillId);
        if (remote != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("remote", remote);
            result.put("installed", installerService.isInstalled(skillId));
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{skillId}/install")
    public ResponseEntity<Map<String, Object>> installSkill(@PathVariable String skillId) {
        log.info("Installing skill: {}", skillId);
        
        SkillEntry entry = discoveryService.getRemoteSkill(skillId);
        if (entry == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Skill not found in index"));
        }
        
        String primaryUrl = entry.getDownloadUrl();
        String fallbackUrl = entry.getGiteeDownloadUrl();
        
        boolean success = installerService.installFromUrl(skillId, primaryUrl, fallbackUrl);
        
        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("success", success);
        
        if (success) {
            result.put("message", "Skill installed successfully");
        } else {
            InstallProgress progress = installerService.getProgress(skillId);
            result.put("error", progress != null ? progress.getError() : "Installation failed");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> uninstallSkill(@PathVariable String skillId) {
        log.info("Uninstalling skill: {}", skillId);
        
        boolean success = installerService.uninstall(skillId);
        
        return ResponseEntity.ok(Map.of(
            "skillId", skillId,
            "success", success,
            "message", success ? "Skill uninstalled" : "Failed to uninstall"
        ));
    }
    
    @GetMapping("/{skillId}/progress")
    public ResponseEntity<InstallProgress> getInstallProgress(@PathVariable String skillId) {
        InstallProgress progress = installerService.getProgress(skillId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh() {
        discoveryService.refresh();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Skill index refreshed"
        ));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SkillEntry>> searchSkills(@RequestParam String query) {
        List<SkillEntry> results = discoveryService.searchSkills(query);
        return ResponseEntity.ok(results);
    }
}
