package net.ooder.skill.template.controller;

import net.ooder.skill.template.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FusionTemplateServiceImpl implements FusionTemplateService {

    private static final Logger log = LoggerFactory.getLogger(FusionTemplateServiceImpl.class);

    @Override
    public FusedWorkflowTemplateDTO fuse(FusionRequestDTO request) {
        FusedWorkflowTemplateDTO dto = new FusedWorkflowTemplateDTO();
        dto.setTemplateId("fused-" + UUID.randomUUID().toString().substring(0, 8));
        dto.setStatus("DRAFT");
        dto.setCreatedAt(System.currentTimeMillis());
        dto.setUpdatedAt(System.currentTimeMillis());
        return dto;
    }

    @Override
    public FusionPreviewDTO preview(FusionRequestDTO request) {
        FusionPreviewDTO dto = new FusionPreviewDTO();
        dto.setMatchScore(50);
        dto.setMatchLevel("MEDIUM");
        dto.setConflicts(Collections.emptyList());
        return dto;
    }

    @Override
    public List<FusedWorkflowTemplateDTO> list(String skillId, String procedureId, String status, int page, int pageSize) {
        return Collections.emptyList();
    }

    @Override
    public FusedWorkflowTemplateDTO get(String templateId) {
        log.warn("[get] templateId={}, not implemented", templateId);
        return null;
    }

    @Override
    public void delete(String templateId) {
        log.info("[delete] templateId={}", templateId);
    }

    @Override
    public FusedWorkflowTemplateDTO resolveConflict(String templateId, ConflictResolutionRequestDTO request) {
        log.warn("[resolveConflict] templateId={}, not implemented", templateId);
        return null;
    }

    @Override
    public List<TemplateVersionDTO> getVersionHistory(String templateId) {
        return Collections.emptyList();
    }

    @Override
    public FusedWorkflowTemplateDTO rollback(String templateId, int version) {
        log.warn("[rollback] templateId={}, version={}, not implemented", templateId, version);
        return null;
    }

    @Override
    public List<ProcedureMatchResultDTO> matchProcedures(String skillId) {
        return Collections.emptyList();
    }

    @Override
    public int calculateMatchScore(String procedureId, String skillId) {
        return 0;
    }
}
