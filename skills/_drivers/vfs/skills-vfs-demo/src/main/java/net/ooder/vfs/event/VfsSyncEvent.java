package net.ooder.vfs.event;

import net.ooder.sdk.api.event.Event;

import java.util.HashMap;
import java.util.Map;

public class VfsSyncEvent extends Event {

    public enum SyncAction {
        PUSH_STARTED, PUSH_COMPLETED, PULL_STARTED, PULL_COMPLETED,
        FILE_UPLOADED, FILE_DOWNLOADED, FILE_SKIPPED, FILE_ERROR,
        DIRECTORY_CREATED, DIRECTORY_DELETED, SYNC_ERROR
    }

    public VfsSyncEvent() {
        super();
    }

    public VfsSyncEvent(String source) {
        super(source);
    }

    public VfsSyncEvent(String source, SyncAction action, String localPath, String vfsPath) {
        super(source);
        setSyncInfo(action, localPath, vfsPath);
    }

    public VfsSyncEvent(String source, SyncAction action, String localPath, String vfsPath,
                         long bytesTransferred, long elapsedMs) {
        super(source);
        setSyncInfo(action, localPath, vfsPath, bytesTransferred, elapsedMs);
    }

    public VfsSyncEvent(String source, SyncAction action, String localPath, String vfsPath,
                         String reason) {
        super(source);
        setSyncInfo(action, localPath, vfsPath, reason);
    }

    public VfsSyncEvent(String source, SyncAction action, String localPath, String vfsPath,
                         Throwable error) {
        super(source);
        setSyncInfo(action, localPath, vfsPath, error);
    }

    public VfsSyncEvent setSyncInfo(SyncAction action, String localPath, String vfsPath) {
        return setSyncInfo(action, localPath, vfsPath, 0, 0, null, null);
    }

    public VfsSyncEvent setSyncInfo(SyncAction action, String localPath, String vfsPath,
                                     long bytesTransferred, long elapsedMs) {
        return setSyncInfo(action, localPath, vfsPath, bytesTransferred, elapsedMs, null, null);
    }

    public VfsSyncEvent setSyncInfo(SyncAction action, String localPath, String vfsPath,
                                     String reason) {
        return setSyncInfo(action, localPath, vfsPath, 0, 0, reason, null);
    }

    public VfsSyncEvent setSyncInfo(SyncAction action, String localPath, String vfsPath,
                                     Throwable error) {
        return setSyncInfo(action, localPath, vfsPath, 0, 0, null, error);
    }

    public VfsSyncEvent setSyncInfo(SyncAction action, String localPath, String vfsPath,
                                     long bytesTransferred, long elapsedMs,
                                     String reason, Throwable error) {
        Map<String, Object> meta = getMetadata();
        if (meta == null) {
            meta = new HashMap<>();
            setMetadata(meta);
        }
        meta.put("action", action.name());
        meta.put("localPath", localPath);
        meta.put("vfsPath", vfsPath);
        meta.put("bytesTransferred", bytesTransferred);
        meta.put("elapsedMs", elapsedMs);
        if (reason != null) meta.put("reason", reason);
        if (error != null) meta.put("errorMessage", error.getMessage());
        return this;
    }

    public SyncAction getAction() {
        String a = getMetadata() != null ? (String) getMetadata().get("action") : null;
        return a != null ? SyncAction.valueOf(a) : null;
    }
    public String getLocalPath() { return getMetadata() != null ? (String) getMetadata().get("localPath") : null; }
    public String getVfsPath() { return getMetadata() != null ? (String) getMetadata().get("vfsPath") : null; }
    public long getBytesTransferred() { return getMetadata() != null ? ((Number) getMetadata().get("bytesTransferred")).longValue() : 0; }
    public long getElapsedMs() { return getMetadata() != null ? ((Number) getMetadata().get("elapsedMs")).longValue() : 0; }
    public String getReason() { return getMetadata() != null ? (String) getMetadata().get("reason") : null; }
}
