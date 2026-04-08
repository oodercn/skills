package net.ooder.bpm.designer.model.dto;

import java.util.List;
import java.util.Map;

public class FormMatchingResultDTO {
    
    private MatchingStatus status;
    private List<FormMatch> matches;
    private String reasoning;
    private FormSchema suggestedSchema;
    private List<FunctionCallTraceDTO> functionTraces;
    
    public enum MatchingStatus {
        EXACT_MATCH,
        PARTIAL_MATCH,
        NO_MATCH,
        NEED_CLARIFICATION,
        SUGGESTED
    }
    
    public static class FormMatch {
        private String formId;
        private String formName;
        private String description;
        private String category;
        private double matchScore;
        private List<FieldMapping> fieldMappings;
        private double coverage;
        private String matchReason;
        
        public String getFormId() { return formId; }
        public void setFormId(String formId) { this.formId = formId; }
        public String getFormName() { return formName; }
        public void setFormName(String formName) { this.formName = formName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public List<FieldMapping> getFieldMappings() { return fieldMappings; }
        public void setFieldMappings(List<FieldMapping> fieldMappings) { this.fieldMappings = fieldMappings; }
        public double getCoverage() { return coverage; }
        public void setCoverage(double coverage) { this.coverage = coverage; }
        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    }
    
    public static class FieldMapping {
        private String activityField;
        private String formField;
        private double mappingScore;
        
        public String getActivityField() { return activityField; }
        public void setActivityField(String activityField) { this.activityField = activityField; }
        public String getFormField() { return formField; }
        public void setFormField(String formField) { this.formField = formField; }
        public double getMappingScore() { return mappingScore; }
        public void setMappingScore(double mappingScore) { this.mappingScore = mappingScore; }
    }
    
    public static class FormSchema {
        private String formId;
        private String formName;
        private String description;
        private String category;
        private List<FormField> fields;
        private boolean generated;
        
        public String getFormId() { return formId; }
        public void setFormId(String formId) { this.formId = formId; }
        public String getFormName() { return formName; }
        public void setFormName(String formName) { this.formName = formName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<FormField> getFields() { return fields; }
        public void setFields(List<FormField> fields) { this.fields = fields; }
        public boolean isGenerated() { return generated; }
        public void setGenerated(boolean generated) { this.generated = generated; }
    }
    
    public static class FormField {
        private String fieldId;
        private String fieldName;
        private String type;
        private boolean required;
        
        public String getFieldId() { return fieldId; }
        public void setFieldId(String fieldId) { this.fieldId = fieldId; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
    }
    
    public static FormMatchingResultDTO exactMatch(List<FormMatch> matches, String reasoning) {
        FormMatchingResultDTO result = new FormMatchingResultDTO();
        result.setStatus(MatchingStatus.EXACT_MATCH);
        result.setMatches(matches);
        result.setReasoning(reasoning);
        return result;
    }
    
    public static FormMatchingResultDTO partialMatch(List<FormMatch> matches, String reasoning) {
        FormMatchingResultDTO result = new FormMatchingResultDTO();
        result.setStatus(MatchingStatus.PARTIAL_MATCH);
        result.setMatches(matches);
        result.setReasoning(reasoning);
        return result;
    }
    
    public static FormMatchingResultDTO suggested(FormSchema schema, String reasoning) {
        FormMatchingResultDTO result = new FormMatchingResultDTO();
        result.setStatus(MatchingStatus.SUGGESTED);
        result.setSuggestedSchema(schema);
        result.setReasoning(reasoning);
        return result;
    }
    
    public static FormMatchingResultDTO noMatch(String reasoning) {
        FormMatchingResultDTO result = new FormMatchingResultDTO();
        result.setStatus(MatchingStatus.NO_MATCH);
        result.setReasoning(reasoning);
        return result;
    }
    
    public MatchingStatus getStatus() { return status; }
    public void setStatus(MatchingStatus status) { this.status = status; }
    public List<FormMatch> getMatches() { return matches; }
    public void setMatches(List<FormMatch> matches) { this.matches = matches; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public FormSchema getSuggestedSchema() { return suggestedSchema; }
    public void setSuggestedSchema(FormSchema suggestedSchema) { this.suggestedSchema = suggestedSchema; }
    public List<FunctionCallTraceDTO> getFunctionTraces() { return functionTraces; }
    public void setFunctionTraces(List<FunctionCallTraceDTO> functionTraces) { this.functionTraces = functionTraces; }
}
