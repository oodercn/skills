package net.ooder.skill.test.controller;

import net.ooder.skill.test.model.SkillIndex;
import net.ooder.skill.test.model.SkillIndex.SkillEntry;
import net.ooder.skill.test.model.SkillMetadata;
import net.ooder.skill.test.service.MenuRegistry;
import net.ooder.skill.test.service.SkillDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class SkillTestController {
    
    @Autowired
    private SkillDiscoveryService discoveryService;
    
    @Autowired
    private MenuRegistry menuRegistry;
    
    @GetMapping("/categories")
    public ResponseEntity<List<SkillIndex.Category>> getCategories() {
        return ResponseEntity.ok(discoveryService.getCategories());
    }
    
    @GetMapping("/skills/remote")
    public ResponseEntity<List<SkillEntry>> getRemoteSkills(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(discoveryService.getRemoteSkillsByCategory(category));
        }
        return ResponseEntity.ok(discoveryService.getRemoteSkills());
    }
    
    @GetMapping("/skills/nexus-ui")
    public ResponseEntity<List<SkillEntry>> getNexusUiSkills() {
        return ResponseEntity.ok(discoveryService.getNexusUiSkills());
    }
    
    @GetMapping("/skills/local")
    public ResponseEntity<List<SkillMetadata>> getLocalSkills() {
        return ResponseEntity.ok(discoveryService.getLocalSkills());
    }
    
    @GetMapping("/skills/local/nexus-ui")
    public ResponseEntity<List<SkillMetadata>> getLocalNexusUiSkills() {
        return ResponseEntity.ok(discoveryService.getLocalNexusUiSkills());
    }
    
    @GetMapping("/skills/{skillId}")
    public ResponseEntity<?> getSkill(@PathVariable String skillId) {
        SkillMetadata local = discoveryService.getLocalSkill(skillId);
        if (local != null) {
            return ResponseEntity.ok(local);
        }
        
        SkillEntry remote = discoveryService.getRemoteSkill(skillId);
        if (remote != null) {
            return ResponseEntity.ok(remote);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/skills/{skillId}/install")
    public ResponseEntity<Map<String, Object>> installSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        
        boolean installed = discoveryService.installSkill(skillId);
        result.put("success", installed);
        result.put("skillId", skillId);
        
        if (installed) {
            SkillMetadata metadata = discoveryService.getLocalSkill(skillId);
            if (metadata != null) {
                menuRegistry.registerSkillMenu(metadata);
                result.put("menuRegistered", true);
                result.put("skill", metadata);
            }
        }
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Map<String, Object>> uninstallSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        
        menuRegistry.unregisterSkillMenu(skillId);
        boolean uninstalled = discoveryService.uninstallSkill(skillId);
        
        result.put("success", uninstalled);
        result.put("skillId", skillId);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh() {
        discoveryService.refresh();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("localSkills", discoveryService.getLocalSkills().size());
        result.put("remoteSkills", discoveryService.getRemoteSkills().size());
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/menu")
    public ResponseEntity<String> getMenuConfig() {
        return ResponseEntity.ok(menuRegistry.getMenuConfig());
    }
    
    @PostMapping("/menu/register-all")
    public ResponseEntity<Map<String, Object>> registerAllMenus() {
        List<SkillMetadata> skills = discoveryService.getLocalNexusUiSkills();
        menuRegistry.registerAllSkillMenus(skills);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("registeredCount", skills.size());
        result.put("skills", skills);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("localSkills", discoveryService.getLocalSkills().size());
        health.put("remoteSkills", discoveryService.getRemoteSkills().size());
        health.put("nexusUiSkills", discoveryService.getNexusUiSkills().size());
        health.put("localNexusUiSkills", discoveryService.getLocalNexusUiSkills().size());
        return ResponseEntity.ok(health);
    }
    
    @PostMapping("/install-all-nexus-ui")
    public ResponseEntity<Map<String, Object>> installAllNexusUiSkills() {
        List<SkillEntry> nexusUiSkills = discoveryService.getNexusUiSkills();
        
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        for (SkillEntry entry : nexusUiSkills) {
            boolean installed = discoveryService.installSkill(entry.getSkillId());
            if (installed) {
                SkillMetadata metadata = discoveryService.getLocalSkill(entry.getSkillId());
                if (metadata != null) {
                    menuRegistry.registerSkillMenu(metadata);
                }
                successCount++;
            } else {
                failCount++;
            }
        }
        
        result.put("total", nexusUiSkills.size());
        result.put("success", successCount);
        result.put("failed", failCount);
        result.put("skills", nexusUiSkills);
        
        return ResponseEntity.ok(result);
    }
}
