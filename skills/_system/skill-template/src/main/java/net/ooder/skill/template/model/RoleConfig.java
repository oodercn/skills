package net.ooder.skill.template.model;

import java.util.List;

public class RoleConfig {
    private String id;
    private String name;
    private String description;
    private List<String> capabilities;
    private List<ActionConfig> actions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public List<ActionConfig> getActions() { return actions; }
    public void setActions(List<ActionConfig> actions) { this.actions = actions; }
}
