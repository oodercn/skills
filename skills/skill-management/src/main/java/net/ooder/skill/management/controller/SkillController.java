package net.ooder.skill.management.controller;

import net.ooder.skill.management.SkillManager;
import net.ooder.skill.management.market.SkillListing;
import net.ooder.skill.management.market.SkillMarketManager;
import net.ooder.skill.management.market.SkillReview;
import net.ooder.skill.management.model.SkillContext;
import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skill")
public class SkillController {

    private final SkillManager skillManager;
    private final SkillMarketManager marketManager;

    public SkillController() {
        this.skillManager = SkillManager.getInstance();
        this.marketManager = SkillMarketManager.getInstance();
    }

    @GetMapping("/list")
    public Map<String, Object> listSkills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        List<SkillDefinition> skills;
        
        if (keyword != null && !keyword.isEmpty()) {
            skills = skillManager.findSkills(skill -> 
                skill.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                skill.getDescription().toLowerCase().contains(keyword.toLowerCase())
            );
        } else if (category != null && !category.isEmpty()) {
            skills = skillManager.getSkillsByCategory(category);
        } else {
            skills = skillManager.getAllSkills();
        }
        
        int total = skills.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<SkillDefinition> pagedSkills = skills.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", pagedSkills);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @GetMapping("/{skillId}")
    public Map<String, Object> getSkill(@PathVariable String skillId) {
        SkillDefinition skill = skillManager.getSkill(skillId);
        
        Map<String, Object> result = new HashMap<>();
        if (skill != null) {
            result.put("status", "success");
            result.put("data", skill);
        } else {
            result.put("status", "error");
            result.put("message", "Skill not found: " + skillId);
        }
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> addSkill(@RequestBody SkillDefinition skill) {
        Map<String, Object> result = new HashMap<>();
        try {
            SkillDefinition added = skillManager.addSkill(skill);
            result.put("status", "success");
            result.put("data", added);
            result.put("message", "Skill added successfully");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateSkill(@RequestBody SkillDefinition skill) {
        Map<String, Object> result = new HashMap<>();
        try {
            SkillDefinition updated = skillManager.updateSkill(skill);
            result.put("status", "success");
            result.put("data", updated);
            result.put("message", "Skill updated successfully");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{skillId}")
    public Map<String, Object> deleteSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        boolean deleted = skillManager.deleteSkill(skillId);
        if (deleted) {
            result.put("status", "success");
            result.put("message", "Skill deleted successfully");
        } else {
            result.put("status", "error");
            result.put("message", "Skill not found: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/start")
    public Map<String, Object> startSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        boolean started = skillManager.startSkill(skillId);
        if (started) {
            result.put("status", "success");
            result.put("message", "Skill started successfully");
        } else {
            result.put("status", "error");
            result.put("message", "Failed to start skill: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/stop")
    public Map<String, Object> stopSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        boolean stopped = skillManager.stopSkill(skillId);
        if (stopped) {
            result.put("status", "success");
            result.put("message", "Skill stopped successfully");
        } else {
            result.put("status", "error");
            result.put("message", "Failed to stop skill: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/execute")
    public Map<String, Object> executeSkill(
            @PathVariable String skillId,
            @RequestBody(required = false) Map<String, Object> params) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            SkillContext context = SkillContext.create();
            if (params != null) {
                context.params(params);
            }
            
            SkillResult skillResult = skillManager.executeSkill(skillId, context);
            result.put("status", skillResult.isSuccess() ? "success" : "error");
            result.put("data", skillResult.getData());
            result.put("message", skillResult.getMessage());
            result.put("executionTime", skillResult.getExecutionTime());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", skillManager.getSkillsByCategory(null)
            .stream()
            .map(SkillDefinition::getCategory)
            .distinct()
            .toList());
        return result;
    }

    @GetMapping("/market/list")
    public Map<String, Object> listMarketSkills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        List<SkillListing> skills;
        
        if (keyword != null && !keyword.isEmpty()) {
            skills = marketManager.searchSkills(keyword);
        } else if (category != null && !category.isEmpty()) {
            skills = marketManager.getSkillsByCategory(category);
        } else {
            skills = marketManager.getAllSkills();
        }
        
        int total = skills.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<SkillListing> pagedSkills = skills.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", pagedSkills);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @GetMapping("/market/{skillId}")
    public Map<String, Object> getMarketSkill(@PathVariable String skillId) {
        SkillListing listing = marketManager.getSkillListing(skillId);
        
        Map<String, Object> result = new HashMap<>();
        if (listing != null) {
            result.put("status", "success");
            result.put("data", listing);
        } else {
            result.put("status", "error");
            result.put("message", "Skill not found in market: " + skillId);
        }
        return result;
    }

    @GetMapping("/market/popular")
    public Map<String, Object> getPopularSkills(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<SkillListing> skills = marketManager.getPopularSkills(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", skills);
        return result;
    }

    @GetMapping("/market/latest")
    public Map<String, Object> getLatestSkills(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<SkillListing> skills = marketManager.getLatestSkills(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", skills);
        return result;
    }

    @GetMapping("/market/categories")
    public Map<String, Object> getMarketCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", marketManager.getCategories());
        return result;
    }

    @PostMapping("/market/{skillId}/rate")
    public Map<String, Object> rateSkill(
            @PathVariable String skillId,
            @RequestBody Map<String, Object> ratingData) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            double rating = ((Number) ratingData.get("rating")).doubleValue();
            String comment = (String) ratingData.getOrDefault("comment", "");
            String userId = (String) ratingData.getOrDefault("userId", "anonymous");
            
            boolean success = marketManager.rateSkill(skillId, rating, comment, userId);
            
            if (success) {
                result.put("status", "success");
                result.put("message", "Rating submitted successfully");
            } else {
                result.put("status", "error");
                result.put("message", "Failed to submit rating");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/market/{skillId}/reviews")
    public Map<String, Object> getSkillReviews(@PathVariable String skillId) {
        List<SkillReview> reviews = marketManager.getSkillReviews(skillId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", reviews);
        return result;
    }
}
