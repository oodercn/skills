package net.ooder.skill.notification.dto;

import java.util.Map;

public class NotificationActionResultDTO {
    
    private String notificationId;
    private String action;
    private boolean success;
    private String message;
    private Map<String, Object> data;

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
