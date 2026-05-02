package net.ooder.vfs.event;

import net.ooder.sdk.api.event.Event;

import java.util.HashMap;
import java.util.Map;

public class VfsFileEvent extends Event {

    public enum FileAction {
        CREATED, UPDATED, DELETED, MOVED, VERSION_CREATED, LOCKED, UNLOCKED, RECYCLED
    }

    public VfsFileEvent() {
        super();
    }

    public VfsFileEvent(String source) {
        super(source);
    }

    public VfsFileEvent(String source, String fileId, String fileName,
                         String folderId, FileAction action) {
        super(source);
        setFileInfo(fileId, fileName, folderId, action, null, 0);
    }

    public VfsFileEvent(String source, String fileId, String fileName,
                         String folderId, FileAction action,
                         String versionId, long fileSize) {
        super(source);
        setFileInfo(fileId, fileName, folderId, action, versionId, fileSize);
    }

    public VfsFileEvent setFileInfo(String fileId, String fileName, String folderId, FileAction action) {
        return setFileInfo(fileId, fileName, folderId, action, null, 0);
    }

    public VfsFileEvent setFileInfo(String fileId, String fileName, String folderId,
                                     FileAction action, String versionId, long fileSize) {
        Map<String, Object> meta = getMetadata();
        if (meta == null) {
            meta = new HashMap<>();
            setMetadata(meta);
        }
        meta.put("fileId", fileId);
        meta.put("fileName", fileName);
        meta.put("folderId", folderId);
        meta.put("action", action.name());
        meta.put("versionId", versionId);
        meta.put("fileSize", fileSize);
        return this;
    }

    public String getFileId() { return getMetadata() != null ? (String) getMetadata().get("fileId") : null; }
    public String getFileName() { return getMetadata() != null ? (String) getMetadata().get("fileName") : null; }
    public String getFolderId() { return getMetadata() != null ? (String) getMetadata().get("folderId") : null; }
    public FileAction getAction() {
        String a = getMetadata() != null ? (String) getMetadata().get("action") : null;
        return a != null ? FileAction.valueOf(a) : null;
    }
    public String getVersionId() { return getMetadata() != null ? (String) getMetadata().get("versionId") : null; }
    public long getFileSize() { return getMetadata() != null ? ((Number) getMetadata().get("fileSize")).longValue() : 0; }
}
