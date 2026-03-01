package net.ooder.nexus.common.model;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    
    private String id;
    private String title;
    private String icon;
    private String href;
    private String category;
    private int order;
    private String permission;
    private List<MenuItem> children = new ArrayList<>();
    private boolean visible = true;
    private boolean expanded = false;
    
    public MenuItem() {}
    
    public MenuItem(String id, String title, String icon, String href) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.href = href;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public List<MenuItem> getChildren() {
        return children;
    }
    
    public void setChildren(List<MenuItem> children) {
        this.children = children != null ? children : new ArrayList<>();
    }
    
    public void addChild(MenuItem child) {
        this.children.add(child);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
