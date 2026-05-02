package net.ooder.vfs.event;

import net.ooder.sdk.api.event.Event;
import net.ooder.sdk.api.event.EventBus;
import net.ooder.sdk.api.event.EventHandler;
import net.ooder.sdk.core.event.CoreEvent;
import net.ooder.sdk.core.event.CoreEventListener;
import net.ooder.sdk.core.event.EventBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VfsEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(VfsEventPublisher.class);

    private static volatile VfsEventPublisher instance;

    private final EventBus eventBus;
    private final List<VfsEventListener> localListeners = new CopyOnWriteArrayList<>();

    private VfsEventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static VfsEventPublisher getInstance() {
        if (instance == null) {
            synchronized (VfsEventPublisher.class) {
                if (instance == null) {
                    instance = new VfsEventPublisher(null);
                }
            }
        }
        return instance;
    }

    public static synchronized VfsEventPublisher initialize(EventBus eventBus) {
        if (instance == null) {
            instance = new VfsEventPublisher(eventBus);
            log.info("VfsEventPublisher initialized with EventBus: {}",
                eventBus != null ? eventBus.getClass().getSimpleName() : "null");
        } else if (instance.eventBus == null && eventBus != null) {
            try {
                var field = VfsEventPublisher.class.getDeclaredField("eventBus");
                field.setAccessible(true);
                field.set(instance, eventBus);
                log.info("VfsEventPublisher EventBus injected: {}", eventBus.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Failed to inject EventBus", e);
            }
        }
        return instance;
    }

    public void publishEvent(Event event) {
        if (event == null) return;
        log.debug("Publishing VFS API event: {} [{}]", event.getEventType(), event.getEventId());

        for (VfsEventListener listener : localListeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                log.error("Local listener error on event: {}", event.getEventType(), e);
            }
        }

        if (eventBus != null) {
            try {
                eventBus.publish(event);
            } catch (Exception e) {
                log.error("EventBus publish error: {}", event.getEventType(), e);
            }
        }
    }

    public void publishCoreEvent(CoreEvent event) {
        if (event == null) return;
        log.debug("Publishing VFS Core event: {} [{}]", event.getEventType(), event.getEventId());

        for (VfsEventListener listener : localListeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                log.error("Local listener error on core event: {}", event.getEventType(), e);
            }
        }

        try {
            EventBean.getInstance().publish(event);
        } catch (Exception e) {
            log.error("EventBean publish error: {}", event.getEventType(), e);
        }
    }

    public void publishFolderEvent(String source, String folderId, String folderName,
                                    String parentId, VfsFolderEvent.FolderAction action) {
        publishEvent(new VfsFolderEvent(source, folderId, folderName, parentId, action));
    }

    public void publishFileEvent(String source, String fileId, String fileName,
                                  String folderId, VfsFileEvent.FileAction action) {
        publishEvent(new VfsFileEvent(source, fileId, fileName, folderId, action));
    }

    public void publishFileEvent(String source, String fileId, String fileName,
                                  String folderId, VfsFileEvent.FileAction action,
                                  String versionId, long fileSize) {
        publishEvent(new VfsFileEvent(source, fileId, fileName, folderId, action, versionId, fileSize));
    }

    public void publishSyncEvent(String source, VfsSyncEvent.SyncAction action,
                                  String localPath, String vfsPath) {
        publishEvent(new VfsSyncEvent(source, action, localPath, vfsPath));
    }

    public void publishSyncEvent(String source, VfsSyncEvent.SyncAction action,
                                  String localPath, String vfsPath,
                                  long bytesTransferred, long elapsedMs) {
        publishEvent(new VfsSyncEvent(source, action, localPath, vfsPath, bytesTransferred, elapsedMs));
    }

    public void publishServerEvent(String source, VfsServerEvent.ServerAction action,
                                    String serverId, String message) {
        publishCoreEvent(new VfsServerEvent(source, action, serverId, message));
    }

    public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventBus != null) {
            eventBus.subscribe(eventType, handler);
            log.info("Subscribed {} to EventBus for event type: {}", handler, eventType.getSimpleName());
        } else {
            log.warn("EventBus not available, subscription for {} not registered", eventType.getSimpleName());
        }
    }

    public <T extends Event> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventBus != null) {
            eventBus.unsubscribe(eventType, handler);
        }
    }

    public <T extends CoreEvent> void subscribeCore(Class<T> eventType, CoreEventListener<T> listener) {
        EventBean.getInstance().subscribe(eventType, listener);
        log.info("Subscribed {} to EventBean for core event type: {}", listener, eventType.getSimpleName());
    }

    public <T extends CoreEvent> void unsubscribeCore(Class<T> eventType, CoreEventListener<T> listener) {
        EventBean.getInstance().unsubscribe(eventType, listener);
    }

    public void addLocalListener(VfsEventListener listener) {
        localListeners.add(listener);
    }

    public void removeLocalListener(VfsEventListener listener) {
        localListeners.remove(listener);
    }

    public void unsubscribeAll() {
        localListeners.clear();
    }

    public boolean hasEventBus() {
        return eventBus != null;
    }
}
