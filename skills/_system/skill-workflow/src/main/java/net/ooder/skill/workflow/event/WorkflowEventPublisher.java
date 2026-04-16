package net.ooder.skill.workflow.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 工作流事件发布器
 */
@Component
public class WorkflowEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WorkflowEventPublisher.class);

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WorkflowEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishTaskArrived(String activityInstId, String userId, Map<String, Object> payload) {
        publishEvent(WorkflowEvent.EventType.TASK_ARRIVED, activityInstId, userId, payload);
    }

    public void publishTaskCompleted(String activityInstId, String userId, Map<String, Object> payload) {
        publishEvent(WorkflowEvent.EventType.TASK_COMPLETED, activityInstId, userId, payload);
    }

    public void publishProcessStarted(String processInstId, String userId, Map<String, Object> payload) {
        publishEvent(WorkflowEvent.EventType.PROCESS_STARTED, processInstId, userId, payload);
    }

    public void publishProcessCompleted(String processInstId, String userId, Map<String, Object> payload) {
        publishEvent(WorkflowEvent.EventType.PROCESS_COMPLETED, processInstId, userId, payload);
    }

    public void publishProcessAborted(String processInstId, String userId, Map<String, Object> payload) {
        publishEvent(WorkflowEvent.EventType.PROCESS_ABORTED, processInstId, userId, payload);
    }

    private void publishEvent(WorkflowEvent.EventType eventType, String targetId, String userId, Map<String, Object> payload) {
        WorkflowEvent event = new WorkflowEvent(this, eventType, targetId, userId, payload);
        eventPublisher.publishEvent(event);
        log.debug("[WorkflowEventPublisher] Published event: type={}, targetId={}, userId={}",
                eventType, targetId, userId);
    }
}
