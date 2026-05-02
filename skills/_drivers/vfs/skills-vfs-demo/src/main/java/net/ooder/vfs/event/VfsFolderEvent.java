package net.ooder.vfs.event;

import net.ooder.sdk.api.event.Event;

import java.util.HashMap;
import java.util.Map;

public class VfsFolderEvent extends Event {

    public enum FolderAction {
        CREATED, UPDATED, DELETED, MOVED, CHILDREN_CHANGED, STATE_CHANGED
    }

    public VfsFolderEvent() {
        super();
    }

    public VfsFolderEvent(String source) {
        super(source);
    }

    public VfsFolderEvent(String source, String folderId, String folderName,
                           String parentId, FolderAction action) {
        super(source);
        setFolderInfo(folderId, folderName, parentId, action);
    }

    public VfsFolderEvent setFolderInfo(String folderId, String folderName,
                                         String parentId, FolderAction action) {
        Map<String, Object> meta = getMetadata();
        if (meta == null) {
            meta = new HashMap<>();
            setMetadata(meta);
        }
        meta.put("folderId", folderId);
        meta.put("folderName", folderName);
        meta.put("parentId", parentId);
        meta.put("action", action.name());
        return this;
    }

    public String getFolderId() { return getMetadata() != null ? (String) getMetadata().get("folderId") : null; }
    public String getFolderName() { return getMetadata() != null ? (String) getMetadata().get("folderName") : null; }
    public String getParentId() { return getMetadata() != null ? (String) getMetadata().get("parentId") : null; }
    public FolderAction getAction() {
        String a = getMetadata() != null ? (String) getMetadata().get("action") : null;
        return a != null ? FolderAction.valueOf(a) : null;
    }
}
