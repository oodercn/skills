package net.ooder.bpm.designer.service;

import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO;

import java.util.List;

public interface FormMatchingService {
    
    FormMatchingResultDTO match(DesignerContextDTO context, String activityDesc);
    
    FormMatchingResultDTO smartMatch(DesignerContextDTO context, String activityDesc);
    
    FormMatchingResultDTO.FormSchema generateSchema(DesignerContextDTO context, String activityDesc);
    
    List<FormMatchingResultDTO.FormMatch> matchByFields(List<String> requiredFields);
}
