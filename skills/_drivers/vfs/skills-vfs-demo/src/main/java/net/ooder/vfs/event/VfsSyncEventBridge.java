package net.ooder.vfs.event;

import net.ooder.sdk.api.scene.store.ConflictInfo;
import net.ooder.sdk.api.scene.store.SyncEvent;
import net.ooder.sdk.api.scene.store.SyncListener;
import net.ooder.vfs.event.VfsSyncEvent.SyncAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VfsSyncEventBridge implements SyncListener {

    private static final Logger log = LoggerFactory.getLogger(VfsSyncEventBridge.class);

    private final VfsEventPublisher publisher;

    public VfsSyncEventBridge() {
        this.publisher = VfsEventPublisher.getInstance();
    }

    public VfsSyncEventBridge(VfsEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onSyncStarted(SyncEvent event) {
        SyncAction action = mapDirection(event);
        publisher.publishSyncEvent("VfsSyncService", action,
            event.getPath(), event.getPath());
    }

    @Override
    public void onSyncProgress(SyncEvent event, int progress) {
        log.debug("Sync progress: {}% for {}", progress, event.getPath());
    }

    @Override
    public void onSyncCompleted(SyncEvent event) {
        SyncAction action = event.getDirection() == SyncEvent.SyncDirection.LOCAL_TO_REMOTE
            ? SyncAction.PUSH_COMPLETED : SyncAction.PULL_COMPLETED;
        publisher.publishSyncEvent("VfsSyncService", action,
            event.getPath(), event.getPath());
    }

    @Override
    public void onSyncFailed(SyncEvent event, Throwable error) {
        publisher.publishEvent(new VfsSyncEvent("VfsSyncService",
            SyncAction.SYNC_ERROR, event.getPath(), event.getPath(), error));
    }

    @Override
    public void onConflictDetected(SyncEvent event, ConflictInfo conflict) {
        log.warn("Sync conflict detected for: {} - type={}", event.getPath(),
            conflict != null ? conflict.getType() : "unknown");
    }

    private SyncAction mapDirection(SyncEvent event) {
        if (event.getDirection() == SyncEvent.SyncDirection.LOCAL_TO_REMOTE) {
            return SyncAction.PUSH_STARTED;
        } else if (event.getDirection() == SyncEvent.SyncDirection.REMOTE_TO_LOCAL) {
            return SyncAction.PULL_STARTED;
        }
        return SyncAction.PUSH_STARTED;
    }
}
