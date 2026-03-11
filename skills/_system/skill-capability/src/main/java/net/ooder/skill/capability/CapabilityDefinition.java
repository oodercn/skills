package net.ooder.skill.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CapabilityDefinition {
    
    private String id;
    private String name;
    private String description;
    private CapabilityCategory category;
    private String version;
    private List<CapabilityParameter> parameters;
    private List<String> returns;
    private List<String> examples;
    private boolean async;
    private int timeout;
    private String status;

    public CapabilityDefinition() {
        this.parameters = new ArrayList<>();
        this.returns = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.async = false;
        this.timeout = 30000;
        this.version = "1.0.0";
        this.status = "ACTIVE";
    }

    public static CapabilityDefinition of(String id, String name, String description) {
        CapabilityDefinition cap = new CapabilityDefinition();
        cap.setId(id);
        cap.setName(name);
        cap.setDescription(description);
        return cap;
    }

    public static CapabilityDefinition dataAccess(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.DATA_ACCESS);
        return cap;
    }

    public static CapabilityDefinition authentication(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.AUTHENTICATION);
        return cap;
    }

    public static CapabilityDefinition communication(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.COMMUNICATION);
        return cap;
    }

    public static CapabilityDefinition integration(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.INTEGRATION);
        return cap;
    }

    public static CapabilityDefinition processing(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.PROCESSING);
        return cap;
    }

    public static CapabilityDefinition storage(String id, String name, String description) {
        CapabilityDefinition cap = of(id, name, description);
        cap.setCategory(CapabilityCategory.STORAGE);
        return cap;
    }

    public void addParameter(CapabilityParameter param) {
        parameters.add(param);
    }

    public void addReturn(String returnType) {
        returns.add(returnType);
    }

    public void addExample(String example) {
        examples.add(example);
    }

    public boolean validateParameters(Map<String, Object> params) {
        if (params == null) params = new java.util.HashMap<>();
        for (CapabilityParameter param : parameters) {
            if (param.isRequired() && !params.containsKey(param.getName())) {
                return false;
            }
        }
        return true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CapabilityCategory getCategory() { return category; }
    public void setCategory(CapabilityCategory category) { this.category = category; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public List<CapabilityParameter> getParameters() { return parameters; }
    public void setParameters(List<CapabilityParameter> parameters) { this.parameters = parameters; }
    public List<String> getReturns() { return returns; }
    public void setReturns(List<String> returns) { this.returns = returns; }
    public List<String> getExamples() { return examples; }
    public void setExamples(List<String> examples) { this.examples = examples; }
    public boolean isAsync() { return async; }
    public void setAsync(boolean async) { this.async = async; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
