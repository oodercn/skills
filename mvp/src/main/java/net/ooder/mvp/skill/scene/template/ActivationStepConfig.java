package net.ooder.mvp.skill.scene.template;

import java.util.List;
import java.util.Map;

public class ActivationStepConfig {
    private String stepId;
    private String name;
    private String description;
    private boolean required;
    private boolean skippable;
    private boolean autoExecute;
    private List<ActionConfig> actions;
    private List<String> privateCapabilities;

    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public boolean isSkippable() { return skippable; }
    public void setSkippable(boolean skippable) { this.skippable = skippable; }
    public boolean isAutoExecute() { return autoExecute; }
    public void setAutoExecute(boolean autoExecute) { this.autoExecute = autoExecute; }
    public List<ActionConfig> getActions() { return actions; }
    public void setActions(List<ActionConfig> actions) { this.actions = actions; }
    public List<String> getPrivateCapabilities() { return privateCapabilities; }
    public void setPrivateCapabilities(List<String> privateCapabilities) { this.privateCapabilities = privateCapabilities; }
}
