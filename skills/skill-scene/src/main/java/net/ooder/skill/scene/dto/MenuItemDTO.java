package net.ooder.skill.scene.dto;

public class MenuItemDTO {
    private String id;
    private String name;
    private String url;
    private String icon;
    private int sort;
    private boolean active;
    private String parentRoleId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getSort() { return sort; }
    public void setSort(int sort) { this.sort = sort; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getParentRoleId() { return parentRoleId; }
    public void setParentRoleId(String parentRoleId) { this.parentRoleId = parentRoleId; }
}
