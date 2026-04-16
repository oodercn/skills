package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class ActionDefDTO {
    private String actionId;
    private String name;
    private String description;
    private String type;
    private String capability;
    private List<ParameterDefDTO> parameters;
    private ReturnDefDTO returns;

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCapability() { return capability; }
    public void setCapability(String capability) { this.capability = capability; }
    public List<ParameterDefDTO> getParameters() { return parameters; }
    public void setParameters(List<ParameterDefDTO> parameters) { this.parameters = parameters; }
    public ReturnDefDTO getReturns() { return returns; }
    public void setReturns(ReturnDefDTO returns) { this.returns = returns; }
}
