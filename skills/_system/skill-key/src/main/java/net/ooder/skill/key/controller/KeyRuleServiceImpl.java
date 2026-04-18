package net.ooder.skill.key.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.ooder.skill.key.dto.*;

public class KeyRuleServiceImpl implements KeyRuleService {
    
    private final Map<String, KeyRuleDTO> ruleStore = new ConcurrentHashMap<>();
    
    @Override
    public List<KeyRuleDTO> getAllRules() {
        return new ArrayList<>(ruleStore.values());
    }
    
    @Override
    public KeyRuleDTO getRule(String ruleId) {
        return ruleStore.get(ruleId);
    }
    
    @Override
    public KeyRuleDTO createRule(KeyRuleDTO request) {
        if (request.getRuleId() == null || request.getRuleId().isEmpty()) {
            request.setRuleId("rule-" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        request.setCreatedAt(System.currentTimeMillis());
        request.setUpdatedAt(System.currentTimeMillis());
        
        if (request.getStatus() == null) {
            request.setStatus("active");
        }
        
        ruleStore.put(request.getRuleId(), request);
        return request;
    }
    
    @Override
    public KeyRuleDTO updateRule(KeyRuleDTO request) {
        KeyRuleDTO existing = ruleStore.get(request.getRuleId());
        if (existing == null) {
            return null;
        }
        
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getScope() != null) {
            existing.setScope(request.getScope());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getAllowedResources() != null) {
            existing.setAllowedResources(request.getAllowedResources());
        }
        if (request.getAllowedActions() != null) {
            existing.setAllowedActions(request.getAllowedActions());
        }
        
        existing.setUpdatedAt(System.currentTimeMillis());
        
        return existing;
    }
    
    @Override
    public void deleteRule(String ruleId) {
        ruleStore.remove(ruleId);
    }
    
    @Override
    public KeyRuleDTO enableRule(String ruleId) {
        KeyRuleDTO rule = ruleStore.get(ruleId);
        if (rule == null) {
            return null;
        }
        
        rule.setStatus("active");
        rule.setUpdatedAt(System.currentTimeMillis());
        
        return rule;
    }
    
    @Override
    public KeyRuleDTO disableRule(String ruleId) {
        KeyRuleDTO rule = ruleStore.get(ruleId);
        if (rule == null) {
            return null;
        }
        
        rule.setStatus("disabled");
        rule.setUpdatedAt(System.currentTimeMillis());
        
        return rule;
    }
}
