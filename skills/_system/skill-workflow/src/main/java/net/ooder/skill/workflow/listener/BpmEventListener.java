package net.ooder.skill.workflow.listener;

import net.ooder.bpm.client.event.BPMEvent;
import net.ooder.bpm.client.event.ProcessEvent;
import net.ooder.bpm.client.event.ActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class BpmEventListener {

    private static final Logger log = LoggerFactory.getLogger(BpmEventListener.class);

    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> processStartListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> processCompleteListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> taskArrivedListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> taskCompletedListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> routeToListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<BpmEventPayload>> routeBackListeners = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Object> eventCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("[BpmEventListener] Initialized - ready to receive BPM events");
    }

    @EventListener
    public void onBPMEvent(BPMEvent event) {
        log.debug("[onBPMEvent] type={}, source={}", event.getClass().getSimpleName(),
                event.getSource() != null ? event.getSource().toString() : "null");

        try {
            if (event instanceof ProcessEvent) {
                handleProcessEvent((ProcessEvent) event);
            } else if (event instanceof ActivityEvent) {
                handleActivityEvent((ActivityEvent) event);
            }
        } catch (Exception e) {
            log.warn("[onBPMEvent] Error processing event: {}", e.getMessage());
        }
    }

    private void handleProcessEvent(ProcessEvent event) {
        String eventType = invokeStringMethod(event, "getEventType");
        String processInstId = extractProcessInstId(event);

        switch (eventType) {
            case "START":
            case "PROCESS_START":
                fire(processStartListeners, "PROCESS_START", processInstId, event);
                break;
            case "COMPLETE":
            case "PROCESS_COMPLETE":
                fire(processCompleteListeners, "PROCESS_COMPLETE", processInstId, event);
                break;
            case "ABORT":
                fire(processCompleteListeners, "PROCESS_ABORT", processInstId, event);
                break;
            default:
                log.debug("[handleProcessEvent] Unhandled eventType={}", eventType);
        }
    }

    private void handleActivityEvent(ActivityEvent event) {
        String eventType = invokeStringMethod(event, "getEventType");
        String activityInstId = extractActivityInstId(event);

        switch (eventType) {
            case "ARRIVED":
            case "TASK_ARRIVED":
                fire(taskArrivedListeners, "TASK_ARRIVED", activityInstId, event);
                break;
            case "COMPLETED":
            case "TASK_COMPLETED":
                fire(taskCompletedListeners, "TASK_COMPLETED", activityInstId, event);
                break;
            case "ROUTE_TO":
                fire(routeToListeners, "ROUTE_TO", activityInstId, event);
                break;
            case "ROUTE_BACK":
                fire(routeBackListeners, "ROUTE_BACK", activityInstId, event);
                break;
            default:
                log.debug("[handleActivityEvent] Unhandled eventType={}", eventType);
        }
    }

    public void onProcessStart(Consumer<BpmEventPayload> listener) {
        processStartListeners.add(listener);
    }

    public void onProcessComplete(Consumer<BpmEventPayload> listener) {
        processCompleteListeners.add(listener);
    }

    public void onTaskArrived(Consumer<BpmEventPayload> listener) {
        taskArrivedListeners.add(listener);
    }

    public void onTaskCompleted(Consumer<BpmEventPayload> listener) {
        taskCompletedListeners.add(listener);
    }

    public void onRouteTo(Consumer<BpmEventPayload> listener) {
        routeToListeners.add(listener);
    }

    public void onRouteBack(Consumer<BpmEventPayload> listener) {
        routeBackListeners.add(listener);
    }

    private void fire(CopyOnWriteArrayList<Consumer<BpmEventPayload>> listeners,
                      String eventType, String targetId, Object rawEvent) {
        if (listeners.isEmpty()) return;
        BpmEventPayload payload = new BpmEventPayload();
        payload.setEventType(eventType);
        payload.setTargetId(targetId);
        payload.setRawEvent(rawEvent);
        payload.setTimestamp(System.currentTimeMillis());
        eventCache.put(eventType + ":" + targetId, payload);

        for (Consumer<BpmEventPayload> listener : listeners) {
            try {
                listener.accept(payload);
            } catch (Exception e) {
                log.warn("[fire] Listener error for {}: {}", eventType, e.getMessage());
            }
        }
        log.info("[BpmEventListener] Event fired: type={}, id={}, listeners={}",
                eventType, targetId, listeners.size());
    }

    public BpmEventPayload getLastEvent(String eventType, String targetId) {
        return (BpmEventPayload) eventCache.get(eventType + ":" + targetId);
    }

    private String extractProcessInstId(Object event) {
        return invokeStringMethod(event, "getProcessInstId");
    }

    private String extractActivityInstId(Object event) {
        return invokeStringMethod(event, "getActivityInstId");
    }

    private String invokeStringMethod(Object obj, String methodName) {
        if (obj == null) return "";
        try {
            java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static class BpmEventPayload {
        private String eventType;
        private String targetId;
        private Object rawEvent;
        private long timestamp;

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        public Object getRawEvent() { return rawEvent; }
        public void setRawEvent(Object rawEvent) { this.rawEvent = rawEvent; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
