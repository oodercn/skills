package net.ooder.vfs.event.test;

import net.ooder.sdk.api.event.Event;
import net.ooder.vfs.event.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class EventNetwork {

    private final Map<String, EventContainer> containers = new ConcurrentHashMap<>();
    private final ExecutorService networkExecutor;
    private final AtomicLong totalTransmitted = new AtomicLong(0);
    private final long simulatedLatencyMs;
    private final boolean deepCopyEnabled;

    public EventNetwork() {
        this(false, 0);
    }

    public EventNetwork(boolean deepCopyEnabled, long simulatedLatencyMs) {
        this.deepCopyEnabled = deepCopyEnabled;
        this.simulatedLatencyMs = simulatedLatencyMs;
        this.networkExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "network-thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void registerContainer(EventContainer container) {
        containers.put(container.getContainerId(), container);
    }

    public void unregisterContainer(EventContainer container) {
        containers.remove(container.getContainerId());
    }

    public void broadcast(String sourceId, Event event) {
        networkExecutor.submit(() -> {
            try {
                if (simulatedLatencyMs > 0) {
                    Thread.sleep(simulatedLatencyMs);
                }

                for (EventContainer target : containers.values()) {
                    if (target.getContainerId().equals(sourceId)) continue;
                    if (!target.isOnline()) continue;

                    Event deliveredEvent = deepCopyEnabled ? deepCopyEvent(event) : event;
                    target.deliverEvent(deliveredEvent);
                    totalTransmitted.incrementAndGet();
                }
            } catch (Exception e) {
                System.err.println("[Network] broadcast error: " + e.getMessage());
            }
        });
    }

    public void broadcastSync(String sourceId, Event event) {
        for (EventContainer target : containers.values()) {
            if (target.getContainerId().equals(sourceId)) continue;
            if (!target.isOnline()) continue;

            Event deliveredEvent = deepCopyEnabled ? deepCopyEvent(event) : event;
            target.deliverEventSync(deliveredEvent);
            totalTransmitted.incrementAndGet();
        }
    }

    private Event deepCopyEvent(Event original) {
        if (original instanceof VfsFileEvent) {
            return deepCopyFileEvent((VfsFileEvent) original);
        }
        if (original instanceof VfsFolderEvent) {
            return deepCopyFolderEvent((VfsFolderEvent) original);
        }
        if (original instanceof VfsSyncEvent) {
            return deepCopySyncEvent((VfsSyncEvent) original);
        }
        return original;
    }

    private VfsFileEvent deepCopyFileEvent(VfsFileEvent src) {
        VfsFileEvent copy = new VfsFileEvent();
        copy.setEventId(src.getEventId());
        copy.setEventType(src.getEventType());
        copy.setTimestamp(src.getTimestamp());
        copy.setSource(src.getSource());
        if (src.getMetadata() != null) {
            copy.setMetadata(new HashMap<>(src.getMetadata()));
        }
        return copy;
    }

    private VfsFolderEvent deepCopyFolderEvent(VfsFolderEvent src) {
        VfsFolderEvent copy = new VfsFolderEvent();
        copy.setEventId(src.getEventId());
        copy.setEventType(src.getEventType());
        copy.setTimestamp(src.getTimestamp());
        copy.setSource(src.getSource());
        if (src.getMetadata() != null) {
            copy.setMetadata(new HashMap<>(src.getMetadata()));
        }
        return copy;
    }

    private VfsSyncEvent deepCopySyncEvent(VfsSyncEvent src) {
        VfsSyncEvent copy = new VfsSyncEvent();
        copy.setEventId(src.getEventId());
        copy.setEventType(src.getEventType());
        copy.setTimestamp(src.getTimestamp());
        copy.setSource(src.getSource());
        if (src.getMetadata() != null) {
            copy.setMetadata(new HashMap<>(src.getMetadata()));
        }
        return copy;
    }

    public long getTotalTransmitted() { return totalTransmitted.get(); }

    public void shutdown() {
        networkExecutor.shutdownNow();
    }
}
