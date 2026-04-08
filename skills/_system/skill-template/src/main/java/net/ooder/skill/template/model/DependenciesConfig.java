package net.ooder.skill.template.model;

import java.util.List;

public class DependenciesConfig {
    private List<DependencyConfig> required;
    private List<DependencyConfig> optional;

    public List<DependencyConfig> getRequired() { return required; }
    public void setRequired(List<DependencyConfig> required) { this.required = required; }
    public List<DependencyConfig> getOptional() { return optional; }
    public void setOptional(List<DependencyConfig> optional) { this.optional = optional; }
}
