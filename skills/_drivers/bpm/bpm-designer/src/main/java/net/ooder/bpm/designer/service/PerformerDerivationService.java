package net.ooder.bpm.designer.service;

import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.PerformerDerivationResultDTO;

import java.util.List;
import java.util.Map;

public interface PerformerDerivationService {
    
    PerformerDerivationResultDTO derive(DesignerContextDTO context, String activityDesc);
    
    PerformerDerivationResultDTO deriveWithCandidates(DesignerContextDTO context, 
            String activityDesc, List<String> candidateUserIds);
    
    List<PerformerDerivationResultDTO.PerformerCandidate> searchCandidates(String query, Map<String, Object> filters);
}
