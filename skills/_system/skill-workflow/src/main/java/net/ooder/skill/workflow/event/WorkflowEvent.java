package net.ooder.skill.workflow.event;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流事件 - 用于解耦工作流与其他服务的依赖
 */
public class WorkflowEvent extends ApplicationEvent {

    public enum EventType {
        TASK_ARRIVED,           // 任务到达
        TASK_COMPLETED,         // 任务完成
        PROCESS_STARTED,        // 流程启动
        PROCESS_COMPLETED,      // 流程完成
        PROCESS_ABORTED         // 流程中止
    }

    private final EventType eventType;
    private final String targetId;
    private final String userId;
    private final Map<String, Object> payload;

    public WorkflowEvent(Object source, EventType eventType, String targetId, String userId) {
        super(source);
        this.eventType = eventType;
        this.targetId = targetId;
        this.userId = userId;
        this.payload = new HashMap<>();
    }

    public WorkflowEvent(Object source, EventType eventType, String targetId, String userId, Map<String, Object> payload) {
        super(source);
        this.eventType = eventType;
        this.targetId = targetId;
        this.userId = userId;
        this.payload = payload != null ? new HashMap<>(payload) : new HashMap<>();
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void addPayload(String key, Object value) {
        this.payload.put(key, value);
    }

    @Override
    public String toString() {
        return "WorkflowEvent{" +
                "eventType=" + eventType +
                ", targetId='" + targetId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
