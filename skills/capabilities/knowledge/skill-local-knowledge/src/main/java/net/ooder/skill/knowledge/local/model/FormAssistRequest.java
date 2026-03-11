package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class FormAssistRequest {
    private String formId;
    private String userInput;
    private Map<String, Object> currentData;
    private Map<String, Object> formSchema;
    
    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    public Map<String, Object> getCurrentData() { return currentData; }
    public void setCurrentData(Map<String, Object> currentData) { this.currentData = currentData; }
    public Map<String, Object> getFormSchema() { return formSchema; }
    public void setFormSchema(Map<String, Object> formSchema) { this.formSchema = formSchema; }
}
