package net.ooder.skill.template.model;

import java.util.List;
import java.util.Map;

public class NexusUiConfig {
    private EntryConfig entry;
    private MenuConfig menu;
    private List<PageConfig> pages;
    private Map<String, Object> settings;

    public static class EntryConfig {
        private String title;
        private String icon;
        private String page;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
    }

    public static class MenuConfig {
        private int order;
        private boolean visible;
        private String parent;

        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public boolean isVisible() { return visible; }
        public void setVisible(boolean visible) { this.visible = visible; }
        public String getParent() { return parent; }
        public void setParent(String parent) { this.parent = parent; }
    }

    public static class PageConfig {
        private String id;
        private String title;
        private String path;
        private Map<String, Object> config;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    public EntryConfig getEntry() { return entry; }
    public void setEntry(EntryConfig entry) { this.entry = entry; }
    public MenuConfig getMenu() { return menu; }
    public void setMenu(MenuConfig menu) { this.menu = menu; }
    public List<PageConfig> getPages() { return pages; }
    public void setPages(List<PageConfig> pages) { this.pages = pages; }
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
}
