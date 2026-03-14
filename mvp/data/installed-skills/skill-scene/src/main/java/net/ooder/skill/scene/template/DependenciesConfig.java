package net.ooder.skill.scene.template;

import java.util.List;
import java.util.Map;

public class DependenciesConfig {
    private List<DependencyConfig> required;
    private List<DependencyConfig> optional;

    public List<DependencyConfig> getRequired() { return required; }
    public void setRequired(List<DependencyConfig> required) { this.required = required; }
    public List<DependencyConfig> getOptional() { return optional; }
    public void setOptional(List<DependencyConfig> optional) { this.optional = optional; }
}
