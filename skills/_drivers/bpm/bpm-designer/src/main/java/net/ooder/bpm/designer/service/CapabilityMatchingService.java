package net.ooder.bpm.designer.service;

import net.ooder.bpm.designer.model.dto.CapabilityMatchingResultDTO;
import net.ooder.bpm.designer.model.dto.DesignerContextDTO;

import java.util.List;
import java.util.Map;

public interface CapabilityMatchingService {
    
    CapabilityMatchingResultDTO match(DesignerContextDTO context, String activityDesc);
    
    CapabilityMatchingResultDTO smartMatch(DesignerContextDTO context, String activityDesc);
    
    List<CapabilityMatchingResultDTO.CapabilityMatch> matchByKeywords(List<String> keywords);
    
    Map<String, Object> buildBindingConfig(String capId, Map<String, Object> context);
}
