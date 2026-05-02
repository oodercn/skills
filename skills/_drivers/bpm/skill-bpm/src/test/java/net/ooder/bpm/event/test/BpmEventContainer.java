package net.ooder.bpm.event.test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BpmEventContainer {

    private final String containerId;
    private final BpmEventNetwork network;
    private final BpmEventRecord record;
    private volatile boolean online = true;
    private volatile boolean running = false;
    private final ExecutorService deliveryExecutor;
    private final List<BpmEventHandler> handlers = new CopyOnWriteArrayList<>();
    private final AtomicInteger eventCounter = new AtomicInteger(0);
    private final Set<BpmEventDomain> subscribedDomains;
    private final CountDownLatch deliveryLatch;

    public BpmEventContainer(String containerId, BpmEventNetwork network) {
        this(containerId, network, null, 0);
    }

    public BpmEventContainer(String containerId, BpmEventNetwork network, Set<BpmEventDomain> subscribedDomains) {
        this(containerId, network, subscribedDomains, 0);
    }

    public BpmEventContainer(String containerId, BpmEventNetwork network, Set<BpmEventDomain> subscribedDomains, int expectedDeliveries) {
        this.containerId = containerId;
        this.network = network;
        this.record = new BpmEventRecord(containerId);
        this.subscribedDomains = subscribedDomains != null ? EnumSet.copyOf(subscribedDomains) : EnumSet.noneOf(BpmEventDomain.class);
        this.deliveryLatch = expectedDeliveries > 0 ? new CountDownLatch(expectedDeliveries) : null;
        this.deliveryExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "delivery-" + containerId);
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (running) return;
        running = true;
        online = true;
        network.registerContainer(this);
        registerDefaultHandlers();
    }

    public void stop() {
        running = false;
        online = false;
        network.unregisterContainer(this);
        deliveryExecutor.shutdownNow();
    }

    public void goOffline() {
        online = false;
        record.setOffline(true);
    }

    public void goOnline() {
        record.setOffline(false);
        online = true;
    }

    public boolean isOnline() { return online; }
    public String getContainerId() { return containerId; }
    public BpmEventRecord getRecord() { return record; }
    public Set<BpmEventDomain> getSubscribedDomains() { return subscribedDomains; }

    public void subscribe(BpmEventHandler handler) {
        handlers.add(handler);
    }

    public void unsubscribe(BpmEventHandler handler) {
        handlers.remove(handler);
    }

    private void registerDefaultHandlers() {
        subscribe(event -> {
            if (!running || !online) return;
            if (containerId.equals(event.getSource())) return;
            record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
            consumeEvent(event);
        });
    }

    private void consumeEvent(BpmTestEvent event) {
        Map<String, String> extractedFields = extractFieldsFromEvent(event);
        record.recordConsumed(event.getEventId(), event.getEventType(), extractedFields);
        eventCounter.incrementAndGet();
        if (deliveryLatch != null) {
            deliveryLatch.countDown();
        }
    }

    private Map<String, String> extractFieldsFromEvent(BpmTestEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("eventType", event.getEventType());
        fields.put("domain", event.getDomain() != null ? event.getDomain().name() : "null");
        fields.put("payload", event.getPayload());
        fields.put("source", event.getSource());
        if (event.getSceneEventCode() != null) {
            fields.put("sceneEventCode", event.getSceneEventCode());
        }
        if (event.getMetadata() != null) {
            List<String> keys = new ArrayList<>(event.getMetadata().keySet());
            Collections.sort(keys);
            for (String key : keys) {
                String value = event.getMetadata().get(key);
                fields.put(key, value != null ? value : "null");
            }
        }
        return fields;
    }

    public boolean awaitDelivery(long timeoutMs) throws InterruptedException {
        if (deliveryLatch == null) return true;
        return deliveryLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public void publishEvent(BpmTestEvent event) {
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        network.broadcast(containerId, event);
    }

    public void publishEventSync(BpmTestEvent event) {
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        network.broadcastSync(containerId, event);
    }

    public void sendTo(String targetId, BpmTestEvent event) {
        record.recordSent(event.getEventId(), event.getEventType(), targetId, event);
        network.sendTo(containerId, targetId, event);
    }

    public void publishProcessEvent(String processInstId, String state) {
        publishEventSync(BpmTestEvent.processEvent(containerId, processInstId, state));
    }

    public void publishActivityEvent(String activityInstId, String agentId, String action) {
        publishEventSync(BpmTestEvent.activityEvent(containerId, activityInstId, agentId, action));
    }

    public void publishSkillFlowEvent(String skillFlowId, BpmSkillFlowState fromState, BpmSkillFlowState toState) {
        publishEventSync(BpmTestEvent.skillFlowEvent(containerId, skillFlowId, fromState, toState));
    }

    public void publishAgentEvent(String agentId, String messageType, String taskDesc) {
        publishEventSync(BpmTestEvent.agentEvent(containerId, agentId, messageType, taskDesc));
    }

    public void publishSceneBridgeEvent(String sceneEventCode, Map<String, String> scenePayload) {
        publishEventSync(BpmTestEvent.sceneBridgeEvent(containerId, sceneEventCode, scenePayload));
    }

    void deliverEvent(BpmTestEvent event) {
        if (!running) return;
        deliveryExecutor.submit(() -> {
            try {
                for (BpmEventHandler handler : handlers) {
                    handler.handle(event);
                }
            } catch (Exception e) {
                System.err.println("[" + containerId + "] Delivery error: " + e.getMessage());
            }
        });
    }

    void deliverEventSync(BpmTestEvent event) {
        if (!running) return;
        Future<?> future = deliveryExecutor.submit(() -> {
            try {
                for (BpmEventHandler handler : handlers) {
                    handler.handle(event);
                }
            } catch (Exception e) {
                System.err.println("[" + containerId + "] Sync delivery error: " + e.getMessage());
            }
        });
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("[" + containerId + "] Sync delivery timeout: " + e.getMessage());
        }
    }

    public int getEventCounter() {
        return eventCounter.get();
    }

    public interface BpmEventHandler {
        void handle(BpmTestEvent event);
    }
}
