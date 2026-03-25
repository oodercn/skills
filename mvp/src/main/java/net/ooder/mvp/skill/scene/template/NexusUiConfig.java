package net.ooder.mvp.skill.scene.template;

public class NexusUiConfig {
    private EntryConfig entry;
    private MenuPositionConfig menu;
    private LayoutConfig layout;

    public EntryConfig getEntry() { return entry; }
    public void setEntry(EntryConfig entry) { this.entry = entry; }
    public MenuPositionConfig getMenu() { return menu; }
    public void setMenu(MenuPositionConfig menu) { this.menu = menu; }
    public LayoutConfig getLayout() { return layout; }
    public void setLayout(LayoutConfig layout) { this.layout = layout; }

    public static class EntryConfig {
        private String page;
        private String title;
        private String icon;

        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public static class MenuPositionConfig {
        private String position;
        private String category;
        private int order;

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
    }

    public static class LayoutConfig {
        private String type;
        private boolean sidebar;
        private boolean header;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isSidebar() { return sidebar; }
        public void setSidebar(boolean sidebar) { this.sidebar = sidebar; }
        public boolean isHeader() { return header; }
        public void setHeader(boolean header) { this.header = header; }
    }
}
