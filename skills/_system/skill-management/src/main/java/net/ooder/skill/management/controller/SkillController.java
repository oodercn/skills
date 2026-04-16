package net.ooder.skill.management.controller;

import net.ooder.skill.management.SkillManager;
import net.ooder.skill.management.dto.*;
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
import java.util.stream.Collectors;

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
    public SkillListResultDTO listSkills(
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
        
        SkillListResultDTO result = new SkillListResultDTO();
        result.setStatus("success");
        result.setData(pagedSkills);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    @GetMapping("/{skillId}")
    public SkillResultDTO getSkill(@PathVariable String skillId) {
        SkillDefinition skill = skillManager.getSkill(skillId);
        
        SkillResultDTO result = new SkillResultDTO();
        if (skill != null) {
            result.setStatus("success");
            result.setData(skill);
        } else {
            result.setStatus("error");
            result.setMessage("Skill not found: " + skillId);
        }
        return result;
    }

    @PostMapping("/add")
    public SkillResultDTO addSkill(@RequestBody SkillDefinition skill) {
        SkillResultDTO result = new SkillResultDTO();
        try {
            SkillDefinition added = skillManager.addSkill(skill);
            result.setStatus("success");
            result.setData(added);
            result.setMessage("Skill added successfully");
        } catch (Exception e) {
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public SkillResultDTO updateSkill(@RequestBody SkillDefinition skill) {
        SkillResultDTO result = new SkillResultDTO();
        try {
            SkillDefinition updated = skillManager.updateSkill(skill);
            result.setStatus("success");
            result.setData(updated);
            result.setMessage("Skill updated successfully");
        } catch (Exception e) {
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{skillId}")
    public SkillResultDTO deleteSkill(@PathVariable String skillId) {
        SkillResultDTO result = new SkillResultDTO();
        boolean deleted = skillManager.deleteSkill(skillId);
        if (deleted) {
            result.setStatus("success");
            result.setMessage("Skill deleted successfully");
        } else {
            result.setStatus("error");
            result.setMessage("Skill not found: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/start")
    public SkillResultDTO startSkill(@PathVariable String skillId) {
        SkillResultDTO result = new SkillResultDTO();
        boolean started = skillManager.startSkill(skillId);
        if (started) {
            result.setStatus("success");
            result.setMessage("Skill started successfully");
        } else {
            result.setStatus("error");
            result.setMessage("Failed to start skill: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/stop")
    public SkillResultDTO stopSkill(@PathVariable String skillId) {
        SkillResultDTO result = new SkillResultDTO();
        boolean stopped = skillManager.stopSkill(skillId);
        if (stopped) {
            result.setStatus("success");
            result.setMessage("Skill stopped successfully");
        } else {
            result.setStatus("error");
            result.setMessage("Failed to stop skill: " + skillId);
        }
        return result;
    }

    @PostMapping("/{skillId}/execute")
    public SkillExecuteResultDTO executeSkill(
            @PathVariable String skillId,
            @RequestBody(required = false) SkillExecuteRequest request) {
        
        SkillExecuteResultDTO result = new SkillExecuteResultDTO();
        try {
            SkillContext context = SkillContext.create();
            if (request != null && request.getParams() != null) {
                context.params(request.getParams());
            }
            
            SkillResult skillResult = skillManager.executeSkill(skillId, context);
            result.setStatus(skillResult.isSuccess() ? "success" : "error");
            result.setData(skillResult.getData());
            result.setMessage(skillResult.getMessage());
            result.setExecutionTime(skillResult.getExecutionTime());
        } catch (Exception e) {
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @GetMapping("/categories")
    public SkillResultDTO getCategories() {
        SkillResultDTO result = new SkillResultDTO();
        result.setStatus("success");
        result.setData(skillManager.getSkillsByCategory(null)
            .stream()
            .map(SkillDefinition::getCategory)
            .distinct()
            .collect(Collectors.toList()));
        return result;
    }

    @GetMapping("/market/list")
    public SkillListResultDTO listMarketSkills(
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
        
        SkillListResultDTO result = new SkillListResultDTO();
        result.setStatus("success");
        result.setData(pagedSkills);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    @GetMapping("/market/{skillId}")
    public SkillResultDTO getMarketSkill(@PathVariable String skillId) {
        SkillListing listing = marketManager.getSkillListing(skillId);
        
        SkillResultDTO result = new SkillResultDTO();
        if (listing != null) {
            result.setStatus("success");
            result.setData(listing);
        } else {
            result.setStatus("error");
            result.setMessage("Skill not found in market: " + skillId);
        }
        return result;
    }

    @GetMapping("/market/popular")
    public SkillListResultDTO getPopularSkills(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<SkillListing> skills = marketManager.getPopularSkills(limit);
        
        SkillListResultDTO result = new SkillListResultDTO();
        result.setStatus("success");
        result.setData(skills);
        return result;
    }

    @GetMapping("/market/latest")
    public SkillListResultDTO getLatestSkills(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<SkillListing> skills = marketManager.getLatestSkills(limit);
        
        SkillListResultDTO result = new SkillListResultDTO();
        result.setStatus("success");
        result.setData(skills);
        return result;
    }

    @GetMapping("/market/categories")
    public SkillResultDTO getMarketCategories() {
        SkillResultDTO result = new SkillResultDTO();
        result.setStatus("success");
        result.setData(marketManager.getCategories());
        return result;
    }

    @PostMapping("/market/{skillId}/rate")
    public SkillResultDTO rateSkill(
            @PathVariable String skillId,
            @RequestBody SkillRateRequest ratingData) {
        
        SkillResultDTO result = new SkillResultDTO();
        try {
            double rating = ratingData.getRating();
            String comment = ratingData.getComment() != null ? ratingData.getComment() : "";
            String userId = ratingData.getUserId() != null ? ratingData.getUserId() : "anonymous";
            
            boolean success = marketManager.rateSkill(skillId, rating, comment, userId);
            
            if (success) {
                result.setStatus("success");
                result.setMessage("Rating submitted successfully");
            } else {
                result.setStatus("error");
                result.setMessage("Failed to submit rating");
            }
        } catch (Exception e) {
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @GetMapping("/market/{skillId}/reviews")
    public SkillListResultDTO getSkillReviews(@PathVariable String skillId) {
        List<SkillReview> reviews = marketManager.getSkillReviews(skillId);
        
        SkillListResultDTO result = new SkillListResultDTO();
        result.setStatus("success");
        result.setData(reviews);
        return result;
    }
}
