package net.ooder.skill.workflow.listener;

import net.ooder.skill.workflow.event.WorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * BPM 通知桥接器 - 通过事件监听解耦对 notification 服务的直接依赖
 * 
 * 解耦说明：
 * 1. 不再直接依赖 skill-notification 的 NotificationService
 * 2. 改为监听 WorkflowEvent 事件
 * 3. 由 notification 服务自行订阅 WorkflowEvent 并处理
 * 4. 如果 notification 服务不存在，事件会被忽略，不影响工作流核心功能
 */
@Component
@ConditionalOnProperty(name = "bpm.notification.bridge.enabled", havingValue = "true", matchIfMissing = true)
public class BpmNotificationBridge {

    private static final Logger log = LoggerFactory.getLogger(BpmNotificationBridge.class);

    @EventListener
    public void onWorkflowEvent(WorkflowEvent event) {
        log.debug("[BpmNotificationBridge] Received workflow event: type={}, targetId={}, userId={}",
                event.getEventType(), event.getTargetId(), event.getUserId());

        // 注意：这里不再直接调用 notificationService
        // 而是由 skill-notification 模块自行订阅 WorkflowEvent 并处理
        // 这样可以完全解耦 skill-workflow 对 skill-notification 的依赖

        switch (event.getEventType()) {
            case TASK_ARRIVED:
                log.info("[BpmNotificationBridge] Task arrived event received for activityInstId={}, userId={}",
                        event.getTargetId(), event.getUserId());
                // 旧代码：notificationService.addNotification(userId, notification);
                // 新方式：由 notification 服务监听此事件并处理
                break;
            case TASK_COMPLETED:
                log.info("[BpmNotificationBridge] Task completed event received for activityInstId={}, userId={}",
                        event.getTargetId(), event.getUserId());
                break;
            case PROCESS_STARTED:
                log.info("[BpmNotificationBridge] Process started event received for processInstId={}, userId={}",
                        event.getTargetId(), event.getUserId());
                break;
            case PROCESS_COMPLETED:
                log.info("[BpmNotificationBridge] Process completed event received for processInstId={}, userId={}",
                        event.getTargetId(), event.getUserId());
                break;
            default:
                log.debug("[BpmNotificationBridge] Unhandled event type: {}", event.getEventType());
        }
    }
}
