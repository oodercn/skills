package net.ooder.skill.template.controller;

import java.util.List;
import net.ooder.skill.template.dto.*;

public interface FusionTemplateService {
    
    FusedWorkflowTemplateDTO fuse(FusionRequestDTO request);
    
    FusionPreviewDTO preview(FusionRequestDTO request);
    
    List<FusedWorkflowTemplateDTO> list(String skillId, String procedureId, String status, int page, int pageSize);
    
    FusedWorkflowTemplateDTO get(String templateId);
    
    void delete(String templateId);
    
    FusedWorkflowTemplateDTO resolveConflict(String templateId, ConflictResolutionRequestDTO request);
    
    List<TemplateVersionDTO> getVersionHistory(String templateId);
    
    FusedWorkflowTemplateDTO rollback(String templateId, int version);
    
    List<ProcedureMatchResultDTO> matchProcedures(String skillId);
    
    int calculateMatchScore(String procedureId, String skillId);
}
