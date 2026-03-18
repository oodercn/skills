package net.ooder.skill.notification.service.impl;

import net.ooder.skill.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();
    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    @Override
    public NotificationResult send(NotificationRequest request) {
        String notificationId = "notif-" + UUID.randomUUID().toString().substring(0, 8);
        
        int sentCount = 0;
        int failedCount = 0;
        
        List<String> targets = request.getTargets();
        if (targets == null || targets.isEmpty()) {
            return new NotificationResultImpl(false, "目标用户不能为空", null, 0, 0);
        }
        
        for (String target : targets) {
            try {
                NotificationImpl notif = new NotificationImpl();
                notif.notificationId = notificationId + ":" + target;
                notif.title = request.getTitle();
                notif.content = request.getContent();
                notif.type = request.getType() != null ? request.getType() : "info";
                notif.userId = target;
                notif.createTime = System.currentTimeMillis();
                notif.read = false;
                notif.status = "SENT";
                
                notifications.put(notif.notificationId, notif);
                sentCount++;
            } catch (Exception e) {
                failedCount++;
            }
        }
        
        return new NotificationResultImpl(true, "发送完成", notificationId, sentCount, failedCount);
    }

    @Override
    public List<Notification> listByUser(String userId, int page, int size) {
        return notifications.values().stream()
            .filter(n -> userId.equals(n.getUserId()))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .skip(page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> listByScene(String sceneGroupId, int page, int size) {
        return notifications.values().stream()
            .filter(n -> n instanceof NotificationImpl)
            .filter(n -> sceneGroupId.equals(((NotificationImpl) n).sceneGroupId))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .skip(page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId) {
        Notification notif = notifications.get(notificationId);
        if (notif instanceof NotificationImpl) {
            ((NotificationImpl) notif).read = true;
        }
    }

    @Override
    public void markAllAsRead(String userId) {
        notifications.values().stream()
            .filter(n -> userId.equals(n.getUserId()))
            .filter(n -> n instanceof NotificationImpl)
            .forEach(n -> ((NotificationImpl) n).read = true);
    }

    @Override
    public int getUnreadCount(String userId) {
        return (int) notifications.values().stream()
            .filter(n -> userId.equals(n.getUserId()))
            .filter(n -> !n.isRead())
            .count();
    }

    @Override
    public void delete(String notificationId) {
        notifications.remove(notificationId);
    }

    private static class NotificationImpl implements Notification {
        private String notificationId;
        private String title;
        private String content;
        private String type;
        private String status;
        private String userId;
        private String sceneGroupId;
        private long createTime;
        private boolean read;

        @Override public String getNotificationId() { return notificationId; }
        @Override public String getTitle() { return title; }
        @Override public String getContent() { return content; }
        @Override public String getType() { return type; }
        @Override public String getStatus() { return status; }
        @Override public String getUserId() { return userId; }
        public String getSceneGroupId() { return sceneGroupId; }
        @Override public long getCreateTime() { return createTime; }
        @Override public boolean isRead() { return read; }
    }

    private static class NotificationResultImpl implements NotificationResult {
        private final boolean success;
        private final String message;
        private final String notificationId;
        private final int sentCount;
        private final int failedCount;

        NotificationResultImpl(boolean success, String message, String notificationId, int sentCount, int failedCount) {
            this.success = success;
            this.message = message;
            this.notificationId = notificationId;
            this.sentCount = sentCount;
            this.failedCount = failedCount;
        }

        @Override public boolean isSuccess() { return success; }
        @Override public String getMessage() { return message; }
        @Override public String getNotificationId() { return notificationId; }
        @Override public int getSentCount() { return sentCount; }
        @Override public int getFailedCount() { return failedCount; }
    }

    private static class Subscription {
        String subscriptionId;
        String userId;
        String eventType;
        List<String> channels;
        boolean enabled;
    }
}
