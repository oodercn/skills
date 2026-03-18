package net.ooder.mvp.skill.scene.template;

import java.util.List;

public class UiSkillConfig {
    private String id;
    private String name;
    private String entryUrl;
    private String icon;
    private List<String> roles;
    private int order;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEntryUrl() { return entryUrl; }
    public void setEntryUrl(String entryUrl) { this.entryUrl = entryUrl; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
