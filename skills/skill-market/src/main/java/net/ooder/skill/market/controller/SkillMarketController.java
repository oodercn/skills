package net.ooder.skill.market.controller;

import net.ooder.skill.market.dto.*;
import net.ooder.skill.market.service.SkillMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
