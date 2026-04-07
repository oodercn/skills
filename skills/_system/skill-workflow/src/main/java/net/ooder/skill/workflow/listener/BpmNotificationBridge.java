package net.ooder.skill.workflow.listener;

import net.ooder.skill.notification.dto.NotificationDTO;
import net.ooder.skill.notification.service.NotificationService;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnClass(NotificationService.class)
@ConditionalOnBean(NotificationService.class)
public class BpmNotificationBridge {

    private static final Logger log = LoggerFactory.getLogger(BpmNotificationBridge.class);

    @Autowired(required = false)
    private BpmEventListener bpmEventListener;

    @Autowired(required = false)
    private NotificationService notificationService;

    @PostConstruct
    public void init() {
        if (bpmEventListener == null || notificationService == null) {
            log.info("[BpmNotificationBridge] Skipped - BpmEventListener or NotificationService not available");
            return;
        }

        bpmEventListener.onTaskArrived(this::onTaskArrived);
        bpmEventListener.onTaskCompleted(this::onTaskCompleted);
        bpmEventListener.onProcessStart(this::onProcessStart);
        bpmEventListener.onProcessComplete(this::onProcessComplete);

        log.info("[BpmNotificationBridge] Initialized - BPM events will generate notifications");
    }

    private void onTaskArrived(BpmEventListener.BpmEventPayload payload) {
        try {
            String userId = resolveTargetUser(payload);
            if (userId == null) return;

            NotificationDTO notification = new NotificationDTO();
            notification.setNotificationId("bpm-notify-" + System.currentTimeMillis());
            notification.setType("bpm-task");
            notification.setTitle("新待办任务");
            notification.setContent("您有一个新的工作流待办需要处理: " + payload.getTargetId());
            notification.setSender("workflow-engine");
            notification.setSenderName("工作流引擎");
            notification.setTimestamp(System.currentTimeMillis());
            notification.setRead(false);
            notification.setActionType("link");
            notification.setActionUrl("/bpm/todo?activityInstId=" + payload.getTargetId());
            notification.setPriority("normal");

            notificationService.addNotification(userId, notification);
            log.info("[BpmNotificationBridge] Task arrived notification sent to userId={}, activityInstId={}",
                userId, payload.getTargetId());
        } catch (Exception e) {
            log.warn("[BpmNotificationBridge] Failed to send task-arrived notification: {}", e.getMessage());
        }
    }

    private void onTaskCompleted(BpmEventListener.BpmEventPayload payload) {
        try {
            String userId = resolveTargetUser(payload);
            if (userId == null) return;

            NotificationDTO notification = new NotificationDTO();
            notification.setNotificationId("bpm-notify-" + System.currentTimeMillis());
            notification.setType("bpm-completed");
            notification.setTitle("任务已完成");
            notification.setContent("工作流任务已处理完成: " + payload.getTargetId());
            notification.setSender("workflow-engine");
            notification.setSenderName("工作流引擎");
            notification.setTimestamp(System.currentTimeMillis());
            notification.setRead(false);
            notification.setPriority("low");

            notificationService.addNotification(userId, notification);
            log.info("[BpmNotificationBridge] Task completed notification sent to userId={}", userId);
        } catch (Exception e) {
            log.warn("[BpmNotificationBridge] Failed to send task-completed notification: {}", e.getMessage());
        }
    }

    private void onProcessStart(BpmEventListener.BpmEventPayload payload) {
        try {
            String userId = TenantContext.getUserId();
            if (userId == null) userId = "default-user";

            NotificationDTO notification = new NotificationDTO();
            notification.setNotificationId("bpm-notify-" + System.currentTimeMillis());
            notification.setType("bpm-process");
            notification.setTitle("流程已启动");
            notification.setContent("新的流程实例已创建: " + payload.getTargetId());
            notification.setSender("workflow-engine");
            notification.setSenderName("工作流引擎");
            notification.setTimestamp(System.currentTimeMillis());
            notification.setRead(false);
            notification.setActionType("link");
            notification.setActionUrl("/bpm/process/" + payload.getTargetId());
            notification.setPriority("normal");

            notificationService.addNotification(userId, notification);
            log.info("[BpmNotificationBridge] Process start notification sent to userId={}, processInstId={}",
                userId, payload.getTargetId());
        } catch (Exception e) {
            log.warn("[BpmNotificationBridge] Failed to send process-start notification: {}", e.getMessage());
        }
    }

    private void onProcessComplete(BpmEventListener.BpmEventPayload payload) {
        try {
            String userId = TenantContext.getUserId();
            if (userId == null) userId = "default-user";

            NotificationDTO notification = new NotificationDTO();
            notification.setNotificationId("bpm-notify-" + System.currentTimeMillis());
            notification.setType("bpm-process-complete");
            notification.setTitle("流程已完成");
            notification.setContent("流程实例已完成: " + payload.getTargetId());
            notification.setSender("workflow-engine");
            notification.setSenderName("工作流引擎");
            notification.setTimestamp(System.currentTimeMillis());
            notification.setRead(false);
            notification.setPriority("low");

            notificationService.addNotification(userId, notification);
            log.info("[BpmNotificationBridge] Process complete notification sent to userId={}", userId);
        } catch (Exception e) {
            log.warn("[BpmNotificationBridge] Failed to send process-complete notification: {}", e.getMessage());
        }
    }

    private String resolveTargetUser(BpmEventListener.BpmEventPayload payload) {
        String userId = TenantContext.getUserId();
        if (userId != null && !userId.isEmpty()) return userId;

        if (payload.getRawEvent() != null) {
            try {
                Object performerObj = invokeMethod(payload.getRawEvent(), "getPerformer");
                if (performerObj != null) return performerObj.toString();
                Object userObj = invokeMethod(payload.getRawEvent(), "getUserID");
                if (userObj != null) return userObj.toString();
            } catch (Exception ignored) {}
        }

        return null;
    }

    private Object invokeMethod(Object target, String methodName) throws Exception {
        java.lang.reflect.Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }
}
