package net.ooder.bpm.designer.service;

import net.ooder.bpm.designer.model.dto.*;

public interface PanelRenderService {
    
    PanelRenderDataDTO buildPerformerPanel(PerformerDerivationResultDTO result);
    
    PanelRenderDataDTO buildCapabilityPanel(CapabilityMatchingResultDTO result);
    
    PanelRenderDataDTO buildFormPanel(FormMatchingResultDTO result);
    
    PanelRenderDataDTO buildActivityPanel(PerformerDerivationResultDTO performerResult,
            CapabilityMatchingResultDTO capabilityResult, FormMatchingResultDTO formResult);
}
