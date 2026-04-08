package net.ooder.skill.notification.service.impl;

import net.ooder.skill.notification.dto.NotificationDTO;
import net.ooder.skill.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final Map<String, List<NotificationDTO>> userNotifications = new ConcurrentHashMap<>();

    public NotificationServiceImpl() {
        initTestData();
    }

    private void initTestData() {
        List<NotificationDTO> notifications = new ArrayList<>();
        
        NotificationDTO n1 = new NotificationDTO();
        n1.setNotificationId("notif-001");
        n1.setType("system");
        n1.setTitle("系统通知");
        n1.setContent("欢迎使用 Ooder 平台！");
        n1.setSender("system");
        n1.setSenderName("系统");
        n1.setTimestamp(System.currentTimeMillis() - 3600000);
        n1.setRead(false);
        n1.setPriority("normal");
        notifications.add(n1);

        NotificationDTO n2 = new NotificationDTO();
        n2.setNotificationId("notif-002");
        n2.setType("skill");
        n2.setTitle("技能安装成功");
        n2.setContent("skill-knowledge 已成功安装");
        n2.setSender("skill-discovery");
        n2.setSenderName("技能发现服务");
        n2.setTimestamp(System.currentTimeMillis() - 1800000);
        n2.setRead(false);
        n2.setPriority("normal");
        n2.setActionType("link");
        n2.setActionUrl("/console/pages/capability-discovery.html");
        notifications.add(n2);

        userNotifications.put("default-user", notifications);
        
        log.info("NotificationService initialized with {} notifications", notifications.size());
    }

    @Override
    public NotificationListResult getNotifications(String userId, String tab, int limit) {
        List<NotificationDTO> allNotifications = userNotifications.getOrDefault(userId, new ArrayList<>());
        
        List<NotificationDTO> filtered;
        if ("all".equals(tab)) {
            filtered = allNotifications;
        } else if ("unread".equals(tab)) {
            filtered = allNotifications.stream().filter(n -> !n.isRead()).toList();
        } else {
            filtered = allNotifications.stream().filter(n -> tab.equals(n.getType())).toList();
        }

        List<NotificationDTO> result = filtered.stream()
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .limit(limit)
            .toList();

        NotificationListResult listResult = new NotificationListResult();
        listResult.setNotifications(result);
        listResult.setTotal(allNotifications.size());
        listResult.setUnread((int) allNotifications.stream().filter(n -> !n.isRead()).count());
        
        return listResult;
    }

    @Override
    public Map<String, Integer> getUnreadCounts(String userId) {
        List<NotificationDTO> notifications = userNotifications.getOrDefault(userId, new ArrayList<>());
        
        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", (int) notifications.stream().filter(n -> !n.isRead()).count());
        counts.put("system", (int) notifications.stream().filter(n -> !n.isRead() && "system".equals(n.getType())).count());
        counts.put("skill", (int) notifications.stream().filter(n -> !n.isRead() && "skill".equals(n.getType())).count());
        
        return counts;
    }

    @Override
    public void markAsRead(String userId, String notificationId) {
        List<NotificationDTO> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.stream()
                .filter(n -> notificationId.equals(n.getNotificationId()))
                .forEach(n -> n.setRead(true));
        }
        log.info("Marked notification {} as read for user {}", notificationId, userId);
    }

    @Override
    public void markAllAsRead(String userId, String tab) {
        List<NotificationDTO> notifications = userNotifications.get(userId);
        if (notifications != null) {
            if ("all".equals(tab) || tab == null) {
                notifications.forEach(n -> n.setRead(true));
            } else {
                notifications.stream()
                    .filter(n -> tab.equals(n.getType()))
                    .forEach(n -> n.setRead(true));
            }
        }
        log.info("Marked all {} notifications as read for user {}", tab != null ? tab : "all", userId);
    }

    @Override
    public Map<String, Object> handleAction(String userId, String notificationId, String action) {
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("action", action);
        result.put("success", true);
        result.put("message", "操作成功");
        
        markAsRead(userId, notificationId);
        
        return result;
    }

    @Override
    public void addNotification(String userId, NotificationDTO notification) {
        List<NotificationDTO> notifications = userNotifications.computeIfAbsent(userId, k -> new ArrayList<>());
        notifications.add(notification);
        log.info("Added notification {} for user {}", notification.getNotificationId(), userId);
    }
}
