package net.ooder.bpm.designer.prompt;

import java.util.Map;

public class PromptTemplate {
    
    private String name;
    private String systemPrompt;
    private String userPromptTemplate;
    private String description;
    private Map<String, String> variables;
    
    public String render(Map<String, Object> context) {
        if (userPromptTemplate == null) {
            return "";
        }
        
        String result = userPromptTemplate;
        if (context != null) {
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        
        return result;
    }
    
    public String renderFull(Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            sb.append(systemPrompt);
            sb.append("\n\n");
        }
        
        sb.append(render(context));
        
        return sb.toString();
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    public String getUserPromptTemplate() { return userPromptTemplate; }
    public void setUserPromptTemplate(String userPromptTemplate) { this.userPromptTemplate = userPromptTemplate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, String> getVariables() { return variables; }
    public void setVariables(Map<String, String> variables) { this.variables = variables; }
}
