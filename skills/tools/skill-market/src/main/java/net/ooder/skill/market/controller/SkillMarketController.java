package net.ooder.skill.market.controller;

import net.ooder.skill.market.dto.*;
import net.ooder.skill.market.service.SkillMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skillcenter/market")
public class SkillMarketController {

    @Autowired
    private SkillMarketService skillMarketService;

    @GetMapping("/list")
    public ResponseEntity<List<SkillPackage>> listSkills() {
        return ResponseEntity.ok(skillMarketService.listSkills());
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<SkillPackage>> searchSkills(@RequestBody SearchRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(20);
        }
        return ResponseEntity.ok(skillMarketService.searchSkills(request));
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillPackage> getSkill(@PathVariable String skillId) {
        SkillPackage pkg = skillMarketService.getSkill(skillId);
        if (pkg == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pkg);
    }

    @PostMapping("/{skillId}/install")
    public ResponseEntity<InstallResult> installSkill(
            @PathVariable String skillId,
            @RequestParam(required = false) String version) {
        InstallResult result = skillMarketService.installSkill(skillId, version);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<InstallResult> uninstallSkill(@PathVariable String skillId) {
        InstallResult result = skillMarketService.uninstallSkill(skillId);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/{skillId}/update")
    public ResponseEntity<InstallResult> updateSkill(@PathVariable String skillId) {
        InstallResult result = skillMarketService.updateSkill(skillId);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/{skillId}/auth")
    public ResponseEntity<AuthStatus> getAuthStatus(@PathVariable String skillId) {
        AuthStatus status = skillMarketService.getAuthStatus(skillId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/sdk/config")
    public ResponseEntity<SdkConfig> getSdkConfig() {
        return ResponseEntity.ok(skillMarketService.getSdkConfig());
    }

    @PutMapping("/sdk/config")
    public ResponseEntity<SdkConfig> updateSdkConfig(@RequestBody SdkConfig config) {
        return ResponseEntity.ok(skillMarketService.updateSdkConfig(config));
    }

    @GetMapping("/sdk/status")
    public ResponseEntity<SdkStatus> getSdkStatus() {
        return ResponseEntity.ok(skillMarketService.getSdkStatus());
    }

    @PostMapping("/sdk/mode")
    public ResponseEntity<Map<String, Object>> switchMode(@RequestBody Map<String, String> request) {
        String mode = request.get("mode");
        if (mode == null || (!mode.equals("mock") && !mode.equals("real"))) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid mode. Must be 'mock' or 'real'");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        boolean result = skillMarketService.switchMode(mode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "Mode switched to " + mode : "Failed to switch mode");
        response.put("mode", mode);
        return ResponseEntity.ok(response);
    }
}
