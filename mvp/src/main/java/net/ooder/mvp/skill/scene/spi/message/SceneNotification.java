package net.ooder.mvp.skill.scene.spi.message;

import java.util.Map;

public class SceneNotification {
    
    private String title;
    private String content;
    private String actionUrl;
    private Map<String, Object> extra;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}
