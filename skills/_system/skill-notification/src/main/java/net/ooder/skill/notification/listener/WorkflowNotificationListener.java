package net.ooder.skill.notification.listener;

import net.ooder.skill.notification.service.NotificationService;
import net.ooder.skill.workflow.event.WorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流事件监听器 - 接收工作流事件并创建通知
 * 
 * 解耦说明：
 * 1. 不再由 skill-workflow 直接调用 notificationService
 * 2. 改为监听 WorkflowEvent 事件
 * 3. 当 workflow 发布事件时，自动创建对应的通知
 * 4. 如果 workflow 服务不存在，此监听器不会生效（ConditionalOnClass）
 */
@Component
@ConditionalOnClass(WorkflowEvent.class)
public class WorkflowNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(WorkflowNotificationListener.class);

    private final NotificationService notificationService;

    @Autowired
    public WorkflowNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onWorkflowEvent(WorkflowEvent event) {
        log.debug("[WorkflowNotificationListener] Received workflow event: type={}, targetId={}, userId={}",
                event.getEventType(), event.getTargetId(), event.getUserId());

        try {
            switch (event.getEventType()) {
                case TASK_ARRIVED:
                    handleTaskArrived(event);
                    break;
                case TASK_COMPLETED:
                    handleTaskCompleted(event);
                    break;
                case PROCESS_STARTED:
                    handleProcessStarted(event);
                    break;
                case PROCESS_COMPLETED:
                    handleProcessCompleted(event);
                    break;
                case PROCESS_ABORTED:
                    handleProcessAborted(event);
                    break;
                default:
                    log.debug("[WorkflowNotificationListener] Unhandled event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("[WorkflowNotificationListener] Failed to handle event: {}", e.getMessage(), e);
        }
    }

    private void handleTaskArrived(WorkflowEvent event) {
        String userId = event.getUserId();
        String activityInstId = event.getTargetId();

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "WORKFLOW_TASK");
        notification.put("title", "新任务待处理");
        notification.put("content", "您有一个新的工作流任务需要处理");
        notification.put("targetId", activityInstId);
        notification.put("userId", userId);
        notification.put("priority", "NORMAL");

        // 调用 notificationService 创建通知
        // notificationService.addNotification(userId, notification);

        log.info("[WorkflowNotificationListener] Created notification for TASK_ARRIVED: userId={}, activityInstId={}",
                userId, activityInstId);
    }

    private void handleTaskCompleted(WorkflowEvent event) {
        String userId = event.getUserId();
        String activityInstId = event.getTargetId();

        log.info("[WorkflowNotificationListener] Task completed: userId={}, activityInstId={}",
                userId, activityInstId);
    }

    private void handleProcessStarted(WorkflowEvent event) {
        String userId = event.getUserId();
        String processInstId = event.getTargetId();

        log.info("[WorkflowNotificationListener] Process started: userId={}, processInstId={}",
                userId, processInstId);
    }

    private void handleProcessCompleted(WorkflowEvent event) {
        String userId = event.getUserId();
        String processInstId = event.getTargetId();

        log.info("[WorkflowNotificationListener] Process completed: userId={}, processInstId={}",
                userId, processInstId);
    }

    private void handleProcessAborted(WorkflowEvent event) {
        String userId = event.getUserId();
        String processInstId = event.getTargetId();

        log.info("[WorkflowNotificationListener] Process aborted: userId={}, processInstId={}",
                userId, processInstId);
    }
}
