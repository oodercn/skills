package net.ooder.skill.notification.service;

import net.ooder.skill.notification.dto.NotificationDTO;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    NotificationListResult getNotifications(String userId, String tab, int limit);

    Map<String, Integer> getUnreadCounts(String userId);

    void markAsRead(String userId, String notificationId);

    void markAllAsRead(String userId, String tab);

    Map<String, Object> handleAction(String userId, String notificationId, String action);

    void addNotification(String userId, NotificationDTO notification);

    class NotificationListResult {
        private List<NotificationDTO> notifications;
        private int total;
        private int unread;

        public List<NotificationDTO> getNotifications() { return notifications; }
        public void setNotifications(List<NotificationDTO> notifications) { this.notifications = notifications; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getUnread() { return unread; }
        public void setUnread(int unread) { this.unread = unread; }
    }
}
