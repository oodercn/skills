package net.ooder.vfs.sync;

public enum SyncAction {
    UPLOAD,
    DOWNLOAD,
    CONFLICT,
    SKIP,
    DELETE_LOCAL,
    DELETE_REMOTE
}
