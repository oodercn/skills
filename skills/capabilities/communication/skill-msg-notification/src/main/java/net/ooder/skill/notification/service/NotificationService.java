package net.ooder.skill.notification.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    NotificationResult send(NotificationRequest request);

    List<Notification> listByUser(String userId, int page, int size);

    List<Notification> listByScene(String sceneGroupId, int page, int size);

    void markAsRead(String notificationId);

    void markAllAsRead(String userId);

    int getUnreadCount(String userId);

    void delete(String notificationId);

    interface NotificationRequest {
        String getTitle();
        String getContent();
        String getType();
        List<String> getTargets();
        List<String> getChannels();
        Map<String, Object> getData();
    }

    interface Notification {
        String getNotificationId();
        String getTitle();
        String getContent();
        String getType();
        String getStatus();
        String getUserId();
        long getCreateTime();
        boolean isRead();
    }

    interface NotificationResult {
        boolean isSuccess();
        String getMessage();
        String getNotificationId();
        int getSentCount();
        int getFailedCount();
    }
}
