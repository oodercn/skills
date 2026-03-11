package net.ooder.skill.scene.capability.service;

import net.ooder.skill.scene.capability.model.BusinessCategory;
import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityCategory;
import net.ooder.skill.scene.capability.model.SkillForm;
import net.ooder.skill.scene.capability.model.Visibility;
import net.ooder.skill.scene.dto.discovery.SkillStatisticsDTO;
import net.ooder.skill.scene.dto.discovery.SkillStatisticsDTO.CategoryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillStatisticsService {
    
    @Autowired
    private CapabilityService capabilityService;
    
    public SkillStatisticsDTO getStatistics() {
        List<Capability> capabilities = capabilityService.findAll();
        
        SkillStatisticsDTO dto = new SkillStatisticsDTO();
        dto.setTotal(capabilities.size());
        
        dto.setByBusinessCategory(calculateBusinessCategoryStats(capabilities));
        dto.setBySkillForm(calculateSkillFormStats(capabilities));
        dto.setByVisibility(calculateVisibilityStats(capabilities));
        dto.setByCategory(calculateCategoryStats(capabilities));
        
        return dto;
    }
    
    private List<CategoryStatistics> calculateBusinessCategoryStats(List<Capability> capabilities) {
        Map<String, Integer> counts = new HashMap<>();
        
        for (Capability cap : capabilities) {
            String bc = cap.getBusinessCategory();
            if (bc == null || bc.isEmpty()) {
                bc = "OTHER";
            }
            counts.merge(bc, 1, Integer::sum);
        }
        
        List<CategoryStatistics> stats = new ArrayList<>();
        for (BusinessCategory bc : BusinessCategory.values()) {
            int count = counts.getOrDefault(bc.getCode(), 0);
            if (count > 0) {
                stats.add(new CategoryStatistics(bc.getCode(), bc.getName(), count, capabilities.size()));
            }
        }
        
        return stats;
    }
    
    private List<CategoryStatistics> calculateSkillFormStats(List<Capability> capabilities) {
        Map<SkillForm, Integer> counts = new HashMap<>();
        
        for (Capability cap : capabilities) {
            SkillForm form = cap.getSkillForm();
            if (form == null) {
                form = SkillForm.PROVIDER;
            }
            counts.merge(form, 1, Integer::sum);
        }
        
        List<CategoryStatistics> stats = new ArrayList<>();
        for (SkillForm form : SkillForm.values()) {
            int count = counts.getOrDefault(form, 0);
            stats.add(new CategoryStatistics(form.getCode(), form.getName(), count, capabilities.size()));
        }
        
        return stats;
    }
    
    private List<CategoryStatistics> calculateVisibilityStats(List<Capability> capabilities) {
        Map<Visibility, Integer> counts = new HashMap<>();
        
        for (Capability cap : capabilities) {
            Visibility vis = cap.getVisibilityEnum();
            if (vis == null) {
                vis = Visibility.PUBLIC;
            }
            counts.merge(vis, 1, Integer::sum);
        }
        
        List<CategoryStatistics> stats = new ArrayList<>();
        for (Visibility vis : Visibility.values()) {
            int count = counts.getOrDefault(vis, 0);
            stats.add(new CategoryStatistics(vis.getCode(), vis.getName(), count, capabilities.size()));
        }
        
        return stats;
    }
    
    private List<CategoryStatistics> calculateCategoryStats(List<Capability> capabilities) {
        Map<CapabilityCategory, Integer> counts = new HashMap<>();
        
        for (Capability cap : capabilities) {
            CapabilityCategory cat = cap.getCapabilityCategory();
            if (cat == null) {
                cat = CapabilityCategory.UTIL;
            }
            counts.merge(cat, 1, Integer::sum);
        }
        
        List<CategoryStatistics> stats = new ArrayList<>();
        for (CapabilityCategory cat : CapabilityCategory.values()) {
            int count = counts.getOrDefault(cat, 0);
            if (count > 0) {
                stats.add(new CategoryStatistics(cat.getCode(), cat.getName(), count, capabilities.size()));
            }
        }
        
        return stats;
    }
}
