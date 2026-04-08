package net.ooder.skill.notification.dto;

import java.util.Map;

public class SendMessageRequest {
    
    private String channel;
    private String title;
    private String content;
    private Map<String, Object> params;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
