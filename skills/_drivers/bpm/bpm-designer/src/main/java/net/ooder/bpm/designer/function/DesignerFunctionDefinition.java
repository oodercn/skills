package net.ooder.bpm.designer.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DesignerFunctionDefinition {
    
    private String name;
    private String description;
    private Map<String, ParameterDefinition> parameters;
    private List<String> required;
    private FunctionCategory category;
    private Function<Map<String, Object>, Object> handler;
    
    public enum FunctionCategory {
        ORGANIZATION,
        CAPABILITY,
        FORM,
        SCENE,
        WORKFLOW
    }
    
    public static DesignerFunctionDefinitionBuilder builder() {
        return new DesignerFunctionDefinitionBuilder();
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, ParameterDefinition> getParameters() { return parameters; }
    public void setParameters(Map<String, ParameterDefinition> parameters) { this.parameters = parameters; }
    public List<String> getRequired() { return required; }
    public void setRequired(List<String> required) { this.required = required; }
    public FunctionCategory getCategory() { return category; }
    public void setCategory(FunctionCategory category) { this.category = category; }
    public Function<Map<String, Object>, Object> getHandler() { return handler; }
    public void setHandler(Function<Map<String, Object>, Object> handler) { this.handler = handler; }
    
    public Map<String, Object> toOpenAISchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", name);
        schema.put("description", description);
        
        Map<String, Object> paramsSchema = new HashMap<>();
        paramsSchema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        if (parameters != null) {
            for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().toSchema());
            }
        }
        paramsSchema.put("properties", properties);
        paramsSchema.put("required", required != null ? required : new ArrayList<>());
        
        schema.put("parameters", paramsSchema);
        return schema;
    }
    
    public static class ParameterDefinition {
        private String type;
        private String description;
        private List<String> enumValues;
        
        public ParameterDefinition(String type, String description) {
            this.type = type;
            this.description = description;
        }
        
        public Map<String, Object> toSchema() {
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", type);
            schema.put("description", description);
            if (enumValues != null && !enumValues.isEmpty()) {
                schema.put("enum", enumValues);
            }
            return schema;
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getEnumValues() { return enumValues; }
        public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    }
    
    public static class DesignerFunctionDefinitionBuilder {
        private DesignerFunctionDefinition function = new DesignerFunctionDefinition();
        
        public DesignerFunctionDefinitionBuilder name(String name) {
            function.setName(name);
            return this;
        }
        
        public DesignerFunctionDefinitionBuilder description(String description) {
            function.setDescription(description);
            return this;
        }
        
        public DesignerFunctionDefinitionBuilder category(FunctionCategory category) {
            function.setCategory(category);
            return this;
        }
        
        public DesignerFunctionDefinitionBuilder addParameter(String name, String type, String description, boolean required) {
            if (function.getParameters() == null) {
                function.setParameters(new HashMap<>());
            }
            if (function.getRequired() == null) {
                function.setRequired(new ArrayList<>());
            }
            function.getParameters().put(name, new ParameterDefinition(type, description));
            if (required) {
                function.getRequired().add(name);
            }
            return this;
        }
        
        public DesignerFunctionDefinitionBuilder addEnumParameter(String name, String description, List<String> enumValues, boolean required) {
            if (function.getParameters() == null) {
                function.setParameters(new HashMap<>());
            }
            if (function.getRequired() == null) {
                function.setRequired(new ArrayList<>());
            }
            ParameterDefinition param = new ParameterDefinition("string", description);
            param.setEnumValues(enumValues);
            function.getParameters().put(name, param);
            if (required) {
                function.getRequired().add(name);
            }
            return this;
        }
        
        public DesignerFunctionDefinitionBuilder handler(Function<Map<String, Object>, Object> handler) {
            function.setHandler(handler);
            return this;
        }
        
        public DesignerFunctionDefinition build() {
            return function;
        }
    }
}
