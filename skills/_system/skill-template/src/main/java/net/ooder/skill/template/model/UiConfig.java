package net.ooder.skill.template.model;

import java.util.Map;

public class UiConfig {
    private NexusUiConfig nexusUi;
    private Map<String, Object> custom;

    public NexusUiConfig getNexusUi() { return nexusUi; }
    public void setNexusUi(NexusUiConfig nexusUi) { this.nexusUi = nexusUi; }
    public Map<String, Object> getCustom() { return custom; }
    public void setCustom(Map<String, Object> custom) { this.custom = custom; }
}
