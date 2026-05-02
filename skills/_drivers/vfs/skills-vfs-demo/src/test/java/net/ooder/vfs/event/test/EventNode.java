package net.ooder.vfs.event.test;

import net.ooder.sdk.api.event.Event;
import net.ooder.sdk.api.event.EventBus;
import net.ooder.sdk.api.event.EventHandler;
import net.ooder.sdk.api.event.impl.EventBusImpl;
import net.ooder.sdk.core.event.CoreEvent;
import net.ooder.sdk.core.event.CoreEventListener;
import net.ooder.sdk.core.event.EventBean;
import net.ooder.vfs.event.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class EventNode {

    private final String nodeId;
    private final EventBus eventBus;
    private final EventRecord record;
    private final List<EventHandler<?>> handlers = new CopyOnWriteArrayList<>();
    private final List<CoreEventListener<?>> coreListeners = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;
    private volatile boolean online = true;
    private final ReentrantLock publishLock = new ReentrantLock();
    private final AtomicInteger concurrentPublishCount = new AtomicInteger(0);
    private final AtomicInteger maxConcurrentPublish = new AtomicInteger(0);
    private final AtomicInteger totalConcurrentAttempts = new AtomicInteger(0);

    public EventNode(String nodeId) {
        this.nodeId = nodeId;
        this.eventBus = new EventBusImpl();
        this.record = new EventRecord(nodeId);
    }

    public EventNode(String nodeId, EventBus sharedEventBus) {
        this.nodeId = nodeId;
        this.eventBus = sharedEventBus;
        this.record = new EventRecord(nodeId);
    }

    public void start() {
        if (running) return;
        running = true;
        online = true;
        subscribeAllEventTypes();
    }

    public void stop() {
        running = false;
        online = false;
        unsubscribeAll();
    }

    public void goOffline() {
        online = false;
        record.setOffline(true);
    }

    public void goOnline() {
        record.setOffline(false);
        online = true;
    }

    public boolean isOnline() {
        return online;
    }

    public void shutdown() {
        stop();
        eventBus.shutdown();
    }

    private void subscribeAllEventTypes() {
        subscribeEventType(VfsFileEvent.class);
        subscribeEventType(VfsFolderEvent.class);
        subscribeEventType(VfsSyncEvent.class);
        subscribeCoreEventType(VfsServerEvent.class);
    }

    private void unsubscribeAll() {
        handlers.clear();
        coreListeners.clear();
    }

    private <T extends Event> void subscribeEventType(Class<T> eventType) {
        EventHandler<T> handler = new EventHandler<T>() {
            @Override
            public void handle(T event) {
                if (!running) return;
                if (!online) {
                    record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                    return;
                }
                String source = event.getSource();
                if (source != null && !source.equals(nodeId)) {
                    record.recordReceived(event.getEventId(), event.getEventType(), source, event);
                }
            }
        };
        eventBus.subscribe(eventType, handler);
        handlers.add(handler);
    }

    private <T extends CoreEvent> void subscribeCoreEventType(Class<T> eventType) {
        CoreEventListener<T> listener = new CoreEventListener<T>() {
            @Override
            public void onEvent(T event) {
                if (!running) return;
                if (!online) {
                    record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                    return;
                }
                String source = event.getSource();
                if (source != null && !source.equals(nodeId)) {
                    record.recordReceived(event.getEventId(), event.getEventType(), source, event);
                }
            }
        };
        EventBean.getInstance().subscribe(eventType, listener);
        coreListeners.add(listener);
    }

    public void publishFileEvent(String fileId, String fileName, String folderId,
                                  VfsFileEvent.FileAction action) {
        VfsFileEvent event = new VfsFileEvent(nodeId, fileId, fileName, folderId, action);
        recordAndPublish(event);
    }

    public void publishFolderEvent(String folderId, String folderName, String parentId,
                                    VfsFolderEvent.FolderAction action) {
        VfsFolderEvent event = new VfsFolderEvent(nodeId, folderId, folderName, parentId, action);
        recordAndPublish(event);
    }

    public void publishSyncEvent(VfsSyncEvent.SyncAction action, String localPath, String vfsPath) {
        VfsSyncEvent event = new VfsSyncEvent(nodeId, action, localPath, vfsPath);
        recordAndPublish(event);
    }

    public void publishServerEvent(VfsServerEvent.ServerAction action, String serverId, String message) {
        VfsServerEvent event = new VfsServerEvent(nodeId, action, serverId, message);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        EventBean.getInstance().publish(event);
    }

    public void publishFileEventSync(String fileId, String fileName, String folderId,
                                      VfsFileEvent.FileAction action) {
        VfsFileEvent event = new VfsFileEvent(nodeId, fileId, fileName, folderId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        eventBus.publishSync(event);
    }

    public void publishFolderEventSync(String folderId, String folderName, String parentId,
                                        VfsFolderEvent.FolderAction action) {
        VfsFolderEvent event = new VfsFolderEvent(nodeId, folderId, folderName, parentId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        eventBus.publishSync(event);
    }

    private void recordAndPublish(Event event) {
        publishLock.lock();
        try {
            int current = concurrentPublishCount.incrementAndGet();
            maxConcurrentPublish.updateAndGet(v -> Math.max(v, current));
            totalConcurrentAttempts.incrementAndGet();

            record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
            eventBus.publish(event);
        } finally {
            concurrentPublishCount.decrementAndGet();
            publishLock.unlock();
        }
    }

    public void publishConcurrent(Runnable publishAction, CountDownLatch startLatch) {
        try {
            startLatch.await();
            publishAction.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getMaxConcurrentPublish() { return maxConcurrentPublish.get(); }
    public int getTotalConcurrentAttempts() { return totalConcurrentAttempts.get(); }
    public void resetConcurrentStats() {
        maxConcurrentPublish.set(0);
        totalConcurrentAttempts.set(0);
    }

    public String getNodeId() { return nodeId; }
    public EventBus getEventBus() { return eventBus; }
    public EventRecord getRecord() { return record; }
    public boolean isRunning() { return running; }
    public ReentrantLock getPublishLock() { return publishLock; }
}
