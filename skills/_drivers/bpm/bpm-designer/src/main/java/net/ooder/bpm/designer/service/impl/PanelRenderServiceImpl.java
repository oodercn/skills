package net.ooder.bpm.designer.service.impl;

import net.ooder.bpm.designer.model.dto.*;
import net.ooder.bpm.designer.model.dto.CapabilityMatchingResultDTO.CapabilityMatch;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO.FormMatch;
import net.ooder.bpm.designer.model.dto.PanelRenderDataDTO.*;
import net.ooder.bpm.designer.model.dto.PerformerDerivationResultDTO.PerformerCandidate;
import net.ooder.bpm.designer.service.PanelRenderService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PanelRenderServiceImpl implements PanelRenderService {
    
    @Override
    public PanelRenderDataDTO buildPerformerPanel(PerformerDerivationResultDTO result) {
        PanelRenderDataDTO panel = new PanelRenderDataDTO();
        panel.setPanelType("PERFORMER");
        panel.setTitle("办理人配置");
        
        List<RenderSection> sections = new ArrayList<>();
        
        RenderSection basicSection = createBasicSection(result);
        sections.add(basicSection);
        
        if (result.getCandidates() != null && !result.getCandidates().isEmpty()) {
            RenderSection candidatesSection = createCandidatesSection(result.getCandidates());
            sections.add(candidatesSection);
        }
        
        if (result.getReasoning() != null) {
            RenderSection reasoningSection = createReasoningSection(result.getReasoning());
            sections.add(reasoningSection);
        }
        
        panel.setSections(sections);
        panel.setDerivedConfig(result.getDerivedConfig());
        
        List<DerivationSuggestion> suggestions = createPerformerSuggestions(result);
        panel.setSuggestions(suggestions);
        
        return panel;
    }
    
    @Override
    public PanelRenderDataDTO buildCapabilityPanel(CapabilityMatchingResultDTO result) {
        PanelRenderDataDTO panel = new PanelRenderDataDTO();
        panel.setPanelType("CAPABILITY");
        panel.setTitle("能力配置");
        
        List<RenderSection> sections = new ArrayList<>();
        
        if (result.getMatches() != null && !result.getMatches().isEmpty()) {
            RenderSection capabilitiesSection = createCapabilitiesSection(result.getMatches());
            sections.add(capabilitiesSection);
        }
        
        if (result.getReasoning() != null) {
            RenderSection reasoningSection = createReasoningSection(result.getReasoning());
            sections.add(reasoningSection);
        }
        
        panel.setSections(sections);
        panel.setDerivedConfig(result.getRecommendedBinding());
        
        return panel;
    }
    
    @Override
    public PanelRenderDataDTO buildFormPanel(FormMatchingResultDTO result) {
        PanelRenderDataDTO panel = new PanelRenderDataDTO();
        panel.setPanelType("FORM");
        panel.setTitle("表单配置");
        
        List<RenderSection> sections = new ArrayList<>();
        
        if (result.getMatches() != null && !result.getMatches().isEmpty()) {
            RenderSection formsSection = createFormsSection(result.getMatches());
            sections.add(formsSection);
        }
        
        if (result.getSuggestedSchema() != null) {
            RenderSection schemaSection = createSchemaSection(result.getSuggestedSchema());
            sections.add(schemaSection);
        }
        
        if (result.getReasoning() != null) {
            RenderSection reasoningSection = createReasoningSection(result.getReasoning());
            sections.add(reasoningSection);
        }
        
        panel.setSections(sections);
        
        return panel;
    }
    
    @Override
    public PanelRenderDataDTO buildActivityPanel(PerformerDerivationResultDTO performerResult,
            CapabilityMatchingResultDTO capabilityResult, FormMatchingResultDTO formResult) {
        PanelRenderDataDTO panel = new PanelRenderDataDTO();
        panel.setPanelType("ACTIVITY");
        panel.setTitle("活动配置");
        
        List<RenderSection> sections = new ArrayList<>();
        
        if (performerResult != null) {
            RenderSection performerSection = createPerformerSummarySection(performerResult);
            sections.add(performerSection);
        }
        
        if (capabilityResult != null && capabilityResult.getMatches() != null) {
            RenderSection capabilitySection = createCapabilitySummarySection(capabilityResult.getMatches());
            sections.add(capabilitySection);
        }
        
        if (formResult != null && formResult.getMatches() != null) {
            RenderSection formSection = createFormSummarySection(formResult.getMatches());
            sections.add(formSection);
        }
        
        panel.setSections(sections);
        
        Map<String, Object> derivedConfig = new HashMap<>();
        if (performerResult != null && performerResult.getDerivedConfig() != null) {
            derivedConfig.put("performer", performerResult.getDerivedConfig());
        }
        if (capabilityResult != null && capabilityResult.getRecommendedBinding() != null) {
            derivedConfig.put("capability", capabilityResult.getRecommendedBinding());
        }
        if (formResult != null && formResult.getMatches() != null && !formResult.getMatches().isEmpty()) {
            FormMatch firstMatch = formResult.getMatches().get(0);
            derivedConfig.put("form", Map.of(
                "formId", firstMatch.getFormId(),
                "formName", firstMatch.getFormName()
            ));
        }
        panel.setDerivedConfig(derivedConfig);
        
        return panel;
    }
    
    private RenderSection createBasicSection(PerformerDerivationResultDTO result) {
        RenderSection section = new RenderSection();
        section.setSectionId("basic");
        section.setTitle("基础配置");
        section.setRenderType(RenderType.FORM);
        section.setEditable(true);
        
        List<RenderItem> items = new ArrayList<>();
        
        if (result.getDerivedConfig() != null) {
            Map<String, Object> config = result.getDerivedConfig();
            
            RenderItem typeItem = new RenderItem();
            typeItem.setItemId("assigneeType");
            typeItem.setLabel("办理人类型");
            typeItem.setValue(config.get("assigneeType"));
            typeItem.setValueType(ValueType.STRING);
            typeItem.setDerived(true);
            typeItem.setConfidence(result.getConfidence());
            items.add(typeItem);
            
            RenderItem nameItem = new RenderItem();
            nameItem.setItemId("assigneeName");
            nameItem.setLabel("办理人");
            nameItem.setValue(config.get("assigneeName"));
            nameItem.setValueType(ValueType.STRING);
            nameItem.setDerived(true);
            nameItem.setConfidence(result.getConfidence());
            nameItem.setReasoning(result.getReasoning());
            items.add(nameItem);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createCandidatesSection(List<PerformerCandidate> candidates) {
        RenderSection section = new RenderSection();
        section.setSectionId("candidates");
        section.setTitle("候选人列表");
        section.setRenderType(RenderType.LIST);
        section.setCollapsible(true);
        
        List<RenderItem> items = new ArrayList<>();
        
        for (PerformerCandidate candidate : candidates) {
            RenderItem item = new RenderItem();
            item.setItemId(candidate.getUserId());
            item.setLabel(candidate.getUserName());
            item.setValue(candidate);
            item.setValueType(ValueType.USER);
            item.setDerived(true);
            item.setConfidence(candidate.getMatchScore());
            
            List<Alternative> alternatives = new ArrayList<>();
            alternatives.add(createAlternative(candidate.getUserId(), candidate.getUserName(), 
                candidate.getMatchScore(), candidate.getMatchReason()));
            item.setAlternatives(alternatives);
            
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createReasoningSection(String reasoning) {
        RenderSection section = new RenderSection();
        section.setSectionId("reasoning");
        section.setTitle("推导说明");
        section.setRenderType(RenderType.CARD);
        section.setCollapsible(true);
        
        List<RenderItem> items = new ArrayList<>();
        
        RenderItem item = new RenderItem();
        item.setItemId("reasoning");
        item.setLabel("AI推荐理由");
        item.setValue(reasoning);
        item.setValueType(ValueType.STRING);
        item.setDerived(true);
        items.add(item);
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createCapabilitiesSection(List<CapabilityMatch> matches) {
        RenderSection section = new RenderSection();
        section.setSectionId("capabilities");
        section.setTitle("匹配的能力");
        section.setRenderType(RenderType.CARD);
        
        List<RenderItem> items = new ArrayList<>();
        
        for (CapabilityMatch match : matches) {
            RenderItem item = new RenderItem();
            item.setItemId(match.getCapId());
            item.setLabel(match.getCapName());
            item.setValue(match);
            item.setValueType(ValueType.CAPABILITY);
            item.setDerived(true);
            item.setConfidence(match.getMatchScore());
            item.setReasoning(match.getMatchReason());
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createFormsSection(List<FormMatch> matches) {
        RenderSection section = new RenderSection();
        section.setSectionId("forms");
        section.setTitle("匹配的表单");
        section.setRenderType(RenderType.CARD);
        
        List<RenderItem> items = new ArrayList<>();
        
        for (FormMatch match : matches) {
            RenderItem item = new RenderItem();
            item.setItemId(match.getFormId());
            item.setLabel(match.getFormName());
            item.setValue(match);
            item.setValueType(ValueType.FORM);
            item.setDerived(true);
            item.setConfidence(match.getMatchScore());
            item.setReasoning(match.getMatchReason());
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createSchemaSection(FormMatchingResultDTO.FormSchema schema) {
        RenderSection section = new RenderSection();
        section.setSectionId("suggestedSchema");
        section.setTitle("建议生成的表单");
        section.setRenderType(RenderType.FORM);
        
        List<RenderItem> items = new ArrayList<>();
        
        RenderItem nameItem = new RenderItem();
        nameItem.setItemId("formName");
        nameItem.setLabel("表单名称");
        nameItem.setValue(schema.getFormName());
        nameItem.setValueType(ValueType.STRING);
        nameItem.setDerived(true);
        items.add(nameItem);
        
        if (schema.getFields() != null) {
            for (FormMatchingResultDTO.FormField field : schema.getFields()) {
                RenderItem fieldItem = new RenderItem();
                fieldItem.setItemId(field.getFieldId());
                fieldItem.setLabel(field.getFieldName());
                fieldItem.setValue(field.getType());
                fieldItem.setValueType(ValueType.STRING);
                fieldItem.setDerived(true);
                items.add(fieldItem);
            }
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createPerformerSummarySection(PerformerDerivationResultDTO result) {
        RenderSection section = new RenderSection();
        section.setSectionId("performerSummary");
        section.setTitle("办理人");
        section.setRenderType(RenderType.FORM);
        
        List<RenderItem> items = new ArrayList<>();
        
        if (result.getDerivedConfig() != null) {
            RenderItem item = new RenderItem();
            item.setItemId("performer");
            item.setLabel("办理人");
            item.setValue(result.getDerivedConfig().get("assigneeName"));
            item.setValueType(ValueType.STRING);
            item.setDerived(true);
            item.setConfidence(result.getConfidence());
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createCapabilitySummarySection(List<CapabilityMatch> matches) {
        RenderSection section = new RenderSection();
        section.setSectionId("capabilitySummary");
        section.setTitle("能力配置");
        section.setRenderType(RenderType.LIST);
        
        List<RenderItem> items = new ArrayList<>();
        
        for (CapabilityMatch match : matches) {
            RenderItem item = new RenderItem();
            item.setItemId(match.getCapId());
            item.setLabel(match.getCapName());
            item.setValue(match.getMatchScore());
            item.setValueType(ValueType.NUMBER);
            item.setDerived(true);
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private RenderSection createFormSummarySection(List<FormMatch> matches) {
        RenderSection section = new RenderSection();
        section.setSectionId("formSummary");
        section.setTitle("表单配置");
        section.setRenderType(RenderType.LIST);
        
        List<RenderItem> items = new ArrayList<>();
        
        for (FormMatch match : matches) {
            RenderItem item = new RenderItem();
            item.setItemId(match.getFormId());
            item.setLabel(match.getFormName());
            item.setValue(match.getCoverage());
            item.setValueType(ValueType.NUMBER);
            item.setDerived(true);
            items.add(item);
        }
        
        section.setItems(items);
        return section;
    }
    
    private Alternative createAlternative(Object value, String label, double score, String reason) {
        Alternative alt = new Alternative();
        alt.setValue(value);
        alt.setLabel(label);
        alt.setScore(score);
        alt.setReason(reason);
        return alt;
    }
    
    private List<DerivationSuggestion> createPerformerSuggestions(PerformerDerivationResultDTO result) {
        List<DerivationSuggestion> suggestions = new ArrayList<>();
        
        if (result.getStatus() == PerformerDerivationResultDTO.DerivationStatus.NEED_CLARIFICATION) {
            DerivationSuggestion suggestion = new DerivationSuggestion();
            suggestion.setType("CLARIFICATION");
            suggestion.setMessage("需要更多信息来确定办理人");
            suggestion.setAction("请提供更详细的活动描述或直接选择办理人");
            suggestions.add(suggestion);
        }
        
        if (result.getCandidates() != null && result.getCandidates().size() > 1) {
            DerivationSuggestion suggestion = new DerivationSuggestion();
            suggestion.setType("ALTERNATIVE");
            suggestion.setMessage("存在多个候选人");
            suggestion.setAction("可以从候选人列表中选择更合适的办理人");
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
}
