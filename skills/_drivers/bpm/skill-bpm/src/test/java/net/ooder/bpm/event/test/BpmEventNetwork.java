package net.ooder.bpm.event.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BpmEventNetwork {

    public enum TransportMode {
        DIRECT_MEMORY,
        JAVA_SERIALIZATION,
        JSON_SERIALIZATION
    }

    private final Map<String, BpmEventContainer> containers = new ConcurrentHashMap<>();
    private final ExecutorService networkExecutor;
    private final AtomicLong totalTransmitted = new AtomicLong(0);
    private final AtomicLong totalBytesTransmitted = new AtomicLong(0);
    private final long simulatedLatencyMs;
    private final TransportMode transportMode;
    private final Map<String, Set<BpmEventDomain>> subscriptionFilters = new ConcurrentHashMap<>();
    private final AtomicLong serializationErrors = new AtomicLong(0);

    public BpmEventNetwork() {
        this(TransportMode.JAVA_SERIALIZATION, 0);
    }

    public BpmEventNetwork(TransportMode transportMode, long simulatedLatencyMs) {
        this.transportMode = transportMode;
        this.simulatedLatencyMs = simulatedLatencyMs;
        this.networkExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "bpm-network-thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void registerContainer(BpmEventContainer container) {
        containers.put(container.getContainerId(), container);
        if (container.getSubscribedDomains() != null && !container.getSubscribedDomains().isEmpty()) {
            subscriptionFilters.put(container.getContainerId(), container.getSubscribedDomains());
        }
    }

    public void unregisterContainer(BpmEventContainer container) {
        containers.remove(container.getContainerId());
        subscriptionFilters.remove(container.getContainerId());
    }

    private boolean shouldDeliver(BpmEventContainer target, BpmTestEvent event) {
        if (!target.isOnline()) return false;
        Set<BpmEventDomain> filter = subscriptionFilters.get(target.getContainerId());
        if (filter == null || filter.isEmpty()) return true;
        return event.getDomain() != null && filter.contains(event.getDomain());
    }

    private BpmTestEvent transport(BpmTestEvent original) {
        if (original == null) return null;
        try {
            switch (transportMode) {
                case JAVA_SERIALIZATION: {
                    byte[] bytes = original.serialize();
                    totalBytesTransmitted.addAndGet(bytes.length);
                    return BpmTestEvent.deserialize(bytes);
                }
                case JSON_SERIALIZATION: {
                    String json = original.serializeToJson();
                    totalBytesTransmitted.addAndGet(json.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
                    return BpmTestEvent.deserializeFromJson(json);
                }
                case DIRECT_MEMORY:
                default:
                    return original;
            }
        } catch (Exception e) {
            serializationErrors.incrementAndGet();
            throw new RuntimeException("Transport failed: " + e.getMessage(), e);
        }
    }

    public void broadcast(String sourceId, BpmTestEvent event) {
        networkExecutor.submit(() -> {
            try {
                if (simulatedLatencyMs > 0) {
                    Thread.sleep(simulatedLatencyMs);
                }
                for (BpmEventContainer target : containers.values()) {
                    if (target.getContainerId().equals(sourceId)) continue;
                    if (!shouldDeliver(target, event)) continue;

                    BpmTestEvent deliveredEvent = transport(event);
                    target.deliverEvent(deliveredEvent);
                    totalTransmitted.incrementAndGet();
                }
            } catch (Exception e) {
                System.err.println("[BpmNetwork] broadcast error: " + e.getMessage());
            }
        });
    }

    public void broadcastSync(String sourceId, BpmTestEvent event) {
        for (BpmEventContainer target : containers.values()) {
            if (target.getContainerId().equals(sourceId)) continue;
            if (!shouldDeliver(target, event)) continue;

            BpmTestEvent deliveredEvent = transport(event);
            target.deliverEventSync(deliveredEvent);
            totalTransmitted.incrementAndGet();
        }
    }

    public void sendTo(String sourceId, String targetId, BpmTestEvent event) {
        BpmEventContainer target = containers.get(targetId);
        if (target == null || !target.isOnline()) return;
        if (target.getContainerId().equals(sourceId)) return;
        if (!shouldDeliver(target, event)) return;

        BpmTestEvent deliveredEvent = transport(event);
        target.deliverEventSync(deliveredEvent);
        totalTransmitted.incrementAndGet();
    }

    public long getTotalTransmitted() { return totalTransmitted.get(); }
    public long getTotalBytesTransmitted() { return totalBytesTransmitted.get(); }
    public long getSerializationErrors() { return serializationErrors.get(); }
    public TransportMode getTransportMode() { return transportMode; }

    public int getOnlineContainerCount() {
        return (int) containers.values().stream().filter(BpmEventContainer::isOnline).count();
    }

    public void shutdown() {
        networkExecutor.shutdownNow();
    }
}
