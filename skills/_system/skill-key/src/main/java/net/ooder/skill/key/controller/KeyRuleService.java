package net.ooder.skill.key.controller;

import java.util.List;
import net.ooder.skill.key.dto.*;

public interface KeyRuleService {
    
    List<KeyRuleDTO> getAllRules();
    
    KeyRuleDTO getRule(String ruleId);
    
    KeyRuleDTO createRule(KeyRuleDTO request);
    
    KeyRuleDTO updateRule(KeyRuleDTO request);
    
    void deleteRule(String ruleId);
    
    KeyRuleDTO enableRule(String ruleId);
    
    KeyRuleDTO disableRule(String ruleId);
}
