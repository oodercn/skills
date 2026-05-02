package net.ooder.vfs.event;

import net.ooder.vfs.sync.SyncProgressListener;
import net.ooder.vfs.sync.SyncSummary;
import net.ooder.vfs.event.VfsSyncEvent.SyncAction;

public class EventBusSyncProgressAdapter implements SyncProgressListener {

    private final VfsEventPublisher publisher;
    private final String localPath;
    private final String vfsPath;

    public EventBusSyncProgressAdapter(String localPath, String vfsPath) {
        this.publisher = VfsEventPublisher.getInstance();
        this.localPath = localPath;
        this.vfsPath = vfsPath;
    }

    @Override
    public void onFileScanned(int total, int current) {
    }

    @Override
    public void onFileUploaded(String path, long bytes, long elapsedMs) {
        publisher.publishSyncEvent("SyncProgress", SyncAction.FILE_UPLOADED, localPath, vfsPath, bytes, elapsedMs);
    }

    @Override
    public void onFileDownloaded(String path, long bytes, long elapsedMs) {
        publisher.publishSyncEvent("SyncProgress", SyncAction.FILE_DOWNLOADED, localPath, vfsPath, bytes, elapsedMs);
    }

    @Override
    public void onFileSkipped(String path, String reason) {
        publisher.publishEvent(new VfsSyncEvent("SyncProgress", SyncAction.FILE_SKIPPED, localPath, vfsPath, reason));
    }

    @Override
    public void onFileError(String path, Throwable error) {
        publisher.publishEvent(new VfsSyncEvent("SyncProgress", SyncAction.FILE_ERROR, localPath, vfsPath, error));
    }

    @Override
    public void onComplete(SyncSummary summary) {
        SyncAction action = vfsPath != null ? SyncAction.PUSH_COMPLETED : SyncAction.PULL_COMPLETED;
        publisher.publishSyncEvent("SyncProgress", action, localPath, vfsPath);
    }
}
