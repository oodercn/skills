package net.ooder.skill.capability;

public class CapabilityParameter {
    
    private String name;
    private String type;
    private String description;
    private boolean required;
    private Object defaultValue;

    public CapabilityParameter() {
        this.required = false;
    }

    public static CapabilityParameter of(String name, String type, String description) {
        CapabilityParameter param = new CapabilityParameter();
        param.setName(name);
        param.setType(type);
        param.setDescription(description);
        return param;
    }

    public static CapabilityParameter required(String name, String type, String description) {
        CapabilityParameter param = of(name, type, description);
        param.setRequired(true);
        return param;
    }

    public boolean validate(Object value) {
        if (value == null) {
            return !required;
        }
        return true;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public Object getDefaultValue() { return defaultValue; }
    public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
}
