package net.ooder.bpm.controller;

import net.ooder.bpm.service.SkillDefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/skill")
public class SkillDefController {

    private static final Logger log = LoggerFactory.getLogger(SkillDefController.class);

    @Autowired
    private SkillDefService skillDefService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listSkills(
            @RequestParam(required = false) String form,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String provider) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> skills = skillDefService.listSkills(form, category, provider);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", skills);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to list skills", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> getSkill(@PathVariable String skillId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> skill = skillDefService.getSkill(skillId);
            if (skill == null) {
                response.put("code", 404);
                response.put("message", "Skill不存在: " + skillId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", skill);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get skill: {}", skillId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSkill(@RequestBody Map<String, Object> skillData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> created = skillDefService.createSkill(skillData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create skill", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> updateSkill(
            @PathVariable String skillId,
            @RequestBody Map<String, Object> skillData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            skillData.put("skillId", skillId);
            Map<String, Object> updated = skillDefService.updateSkill(skillData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update skill: {}", skillId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable String skillId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            skillDefService.deleteSkill(skillId);
            response.put("code", 200);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete skill: {}", skillId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/enums")
    public ResponseEntity<Map<String, Object>> getSkillEnums() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> enums = skillDefService.getSkillEnums();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", enums);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get skill enums", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
