package net.ooder.vfs.event.test;

import net.ooder.sdk.api.event.Event;
import net.ooder.sdk.api.event.EventBus;
import net.ooder.sdk.api.event.EventHandler;
import net.ooder.sdk.api.event.impl.EventBusImpl;
import net.ooder.vfs.event.*;

import java.util.HashMap;
import java.util.concurrent.*;

public class EventContainer {

    private final String containerId;
    private final EventBus localBus;
    private final EventNetwork network;
    private final EventRecord record;
    private final ContainerLogger logger;
    private volatile boolean online = true;
    private volatile boolean running = false;
    private final ExecutorService deliveryExecutor;

    public EventContainer(String containerId, EventNetwork network, String logDir) {
        this.containerId = containerId;
        this.network = network;
        this.localBus = new EventBusImpl();
        this.record = new EventRecord(containerId);
        this.logger = new ContainerLogger(containerId, logDir);
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
        subscribeLocalHandlers();
        logger.info("Container started with independent EventBus: " + localBus.getClass().getSimpleName() + "@" + System.identityHashCode(localBus));
    }

    public void stop() {
        running = false;
        online = false;
        network.unregisterContainer(this);
        deliveryExecutor.shutdownNow();
        logger.info("Container stopped");
        logger.close();
    }

    public void goOffline() {
        online = false;
        record.setOffline(true);
        logger.info("Container went OFFLINE");
    }

    public void goOnline() {
        record.setOffline(false);
        online = true;
        logger.info("Container went ONLINE");
    }

    public boolean isOnline() { return online; }
    public String getContainerId() { return containerId; }
    public EventBus getLocalBus() { return localBus; }
    public EventRecord getRecord() { return record; }
    public ContainerLogger getLogger() { return logger; }

    private void attachSentRef(Event event) {
        if (event.getMetadata() != null) {
            event.getMetadata().put("_sentObjRef", System.identityHashCode(event));
        }
    }

    private int extractSentRef(Event event) {
        if (event.getMetadata() != null) {
            Object ref = event.getMetadata().get("_sentObjRef");
            if (ref instanceof Number) {
                return ((Number) ref).intValue();
            }
        }
        return 0;
    }

    private void subscribeLocalHandlers() {
        localBus.subscribe(VfsFileEvent.class, new EventHandler<VfsFileEvent>() {
            @Override
            public void handle(VfsFileEvent event) {
                if (!running || !online) return;
                if (containerId.equals(event.getSource())) return;
                String threadName = Thread.currentThread().getName();
                int objRef = System.identityHashCode(event);
                record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                logger.received(event.getEventId(), event.getEventType(), event.getSource(),
                    "thread=" + threadName + " ref=" + objRef);
                consumeFileEvent(event, threadName, objRef);
            }
        });

        localBus.subscribe(VfsFolderEvent.class, new EventHandler<VfsFolderEvent>() {
            @Override
            public void handle(VfsFolderEvent event) {
                if (!running || !online) return;
                if (containerId.equals(event.getSource())) return;
                String threadName = Thread.currentThread().getName();
                int objRef = System.identityHashCode(event);
                record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                logger.received(event.getEventId(), event.getEventType(), event.getSource(),
                    "thread=" + threadName + " ref=" + objRef);
                consumeFolderEvent(event, threadName, objRef);
            }
        });

        localBus.subscribe(VfsSyncEvent.class, new EventHandler<VfsSyncEvent>() {
            @Override
            public void handle(VfsSyncEvent event) {
                if (!running || !online) return;
                if (containerId.equals(event.getSource())) return;
                String threadName = Thread.currentThread().getName();
                int objRef = System.identityHashCode(event);
                record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                logger.received(event.getEventId(), event.getEventType(), event.getSource(),
                    "thread=" + threadName + " ref=" + objRef);
                consumeSyncEvent(event, threadName, objRef);
            }
        });
    }

    private void consumeFileEvent(VfsFileEvent event, String threadName, int objRef) {
        String fileId = event.getFileId();
        String fileName = event.getFileName();
        String folderId = event.getFolderId();
        VfsFileEvent.FileAction action = event.getAction();
        String versionId = event.getVersionId();
        long fileSize = event.getFileSize();
        String content = String.format("fileId=%s, fileName=%s, folderId=%s, action=%s, versionId=%s, fileSize=%d",
            fileId, fileName, folderId, action, versionId, fileSize);
        record.recordConsumed(event.getEventId(), "VfsFileEvent", content);
        logger.consumed(event.getEventId(), "VfsFileEvent", content, threadName, String.valueOf(objRef));

        int sentRef = extractSentRef(event);
        boolean isDifferent = sentRef != 0 && sentRef != objRef;
        logger.deepCopyCheck(event.getEventId(), sentRef, objRef, isDifferent);
    }

    private void consumeFolderEvent(VfsFolderEvent event, String threadName, int objRef) {
        String folderId = event.getFolderId();
        String folderName = event.getFolderName();
        String parentId = event.getParentId();
        VfsFolderEvent.FolderAction action = event.getAction();
        String content = String.format("folderId=%s, folderName=%s, parentId=%s, action=%s",
            folderId, folderName, parentId, action);
        record.recordConsumed(event.getEventId(), "VfsFolderEvent", content);
        logger.consumed(event.getEventId(), "VfsFolderEvent", content, threadName, String.valueOf(objRef));

        int sentRef = extractSentRef(event);
        boolean isDifferent = sentRef != 0 && sentRef != objRef;
        logger.deepCopyCheck(event.getEventId(), sentRef, objRef, isDifferent);
    }

    private void consumeSyncEvent(VfsSyncEvent event, String threadName, int objRef) {
        VfsSyncEvent.SyncAction action = event.getAction();
        String localPath = event.getLocalPath();
        String vfsPath = event.getVfsPath();
        long bytes = event.getBytesTransferred();
        long elapsed = event.getElapsedMs();
        String reason = event.getReason();
        String content;
        if (reason != null) {
            content = String.format("action=%s, localPath=%s, vfsPath=%s, bytes=%d, elapsedMs=%d, reason=%s",
                action, localPath, vfsPath, bytes, elapsed, reason);
        } else {
            content = String.format("action=%s, localPath=%s, vfsPath=%s, bytes=%d, elapsedMs=%d",
                action, localPath, vfsPath, bytes, elapsed);
        }
        record.recordConsumed(event.getEventId(), "VfsSyncEvent", content);
        logger.consumed(event.getEventId(), "VfsSyncEvent", content, threadName, String.valueOf(objRef));

        int sentRef = extractSentRef(event);
        boolean isDifferent = sentRef != 0 && sentRef != objRef;
        logger.deepCopyCheck(event.getEventId(), sentRef, objRef, isDifferent);
    }

    public void publishFileEvent(String fileId, String fileName, String folderId,
                                  VfsFileEvent.FileAction action) {
        VfsFileEvent event = new VfsFileEvent(containerId, fileId, fileName, folderId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        attachSentRef(event);
        logger.sent(event.getEventId(), event.getEventType(),
            "fileId=" + fileId + " fileName=" + fileName + " action=" + action + " ref=" + System.identityHashCode(event));
        network.broadcast(containerId, event);
    }

    public void publishFolderEvent(String folderId, String folderName, String parentId,
                                    VfsFolderEvent.FolderAction action) {
        VfsFolderEvent event = new VfsFolderEvent(containerId, folderId, folderName, parentId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        attachSentRef(event);
        logger.sent(event.getEventId(), event.getEventType(),
            "folderId=" + folderId + " folderName=" + folderName + " action=" + action + " ref=" + System.identityHashCode(event));
        network.broadcast(containerId, event);
    }

    public void publishSyncEvent(VfsSyncEvent.SyncAction action, String localPath, String vfsPath) {
        VfsSyncEvent event = new VfsSyncEvent(containerId, action, localPath, vfsPath);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        attachSentRef(event);
        logger.sent(event.getEventId(), event.getEventType(),
            "action=" + action + " localPath=" + localPath + " ref=" + System.identityHashCode(event));
        network.broadcast(containerId, event);
    }

    public void publishFileEventSync(String fileId, String fileName, String folderId,
                                      VfsFileEvent.FileAction action) {
        VfsFileEvent event = new VfsFileEvent(containerId, fileId, fileName, folderId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        attachSentRef(event);
        logger.sent(event.getEventId(), event.getEventType(),
            "fileId=" + fileId + " fileName=" + fileName + " action=" + action + " ref=" + System.identityHashCode(event));
        network.broadcastSync(containerId, event);
    }

    public void publishFolderEventSync(String folderId, String folderName, String parentId,
                                        VfsFolderEvent.FolderAction action) {
        VfsFolderEvent event = new VfsFolderEvent(containerId, folderId, folderName, parentId, action);
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        attachSentRef(event);
        logger.sent(event.getEventId(), event.getEventType(),
            "folderId=" + folderId + " folderName=" + folderName + " action=" + action + " ref=" + System.identityHashCode(event));
        network.broadcastSync(containerId, event);
    }

    void deliverEvent(Event event) {
        if (!running) return;
        deliveryExecutor.submit(() -> {
            try {
                localBus.publishSync(event);
            } catch (Exception e) {
                logger.info("Delivery error: " + e.getMessage());
            }
        });
    }

    void deliverEventSync(Event event) {
        if (!running) return;
        Future<?> future = deliveryExecutor.submit(() -> {
            try {
                localBus.publishSync(event);
            } catch (Exception e) {
                logger.info("Sync delivery error: " + e.getMessage());
            }
        });
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("Sync delivery timeout: " + e.getMessage());
        }
    }
}
