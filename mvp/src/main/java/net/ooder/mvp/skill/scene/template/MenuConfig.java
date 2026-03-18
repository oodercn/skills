package net.ooder.mvp.skill.scene.template;

import java.util.List;

public class MenuConfig {
    private String id;
    private String name;
    private String icon;
    private String url;
    private int order;
    private boolean visible;
    private List<MenuConfig> children;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public List<MenuConfig> getChildren() { return children; }
    public void setChildren(List<MenuConfig> children) { this.children = children; }
}
