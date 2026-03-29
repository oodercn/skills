package net.ooder.skill.im.dingding.dto;

import java.io.Serializable;

public class DingMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String title;
    private String content;
    private int reminderType;
    private String reminderTime;
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getReminderType() { return reminderType; }
    public void setReminderType(int reminderType) { this.reminderType = reminderType; }
    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }
}
