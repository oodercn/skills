package net.ooder.skill.notification.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationListResult {
    
    private List<NotificationDTO> notifications;
    private Map<String, Integer> unreadCounts;

    public NotificationListResult() {
        this.unreadCounts = new HashMap<>();
        this.unreadCounts.put("all", 0);
        this.unreadCounts.put("todo", 0);
        this.unreadCounts.put("a2a", 0);
        this.unreadCounts.put("system", 0);
    }

    public List<NotificationDTO> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDTO> notifications) { this.notifications = notifications; }

    public Map<String, Integer> getUnreadCounts() { return unreadCounts; }
    public void setUnreadCounts(Map<String, Integer> unreadCounts) { this.unreadCounts = unreadCounts; }
}
