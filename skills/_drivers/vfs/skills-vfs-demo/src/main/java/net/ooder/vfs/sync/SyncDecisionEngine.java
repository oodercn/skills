package net.ooder.vfs.sync;

import net.ooder.vfs.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SyncDecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(SyncDecisionEngine.class);

    private final ConflictStrategy conflictStrategy;

    public enum ConflictStrategy {
        LOCAL_WINS,
        REMOTE_WINS,
        SKIP
    }

    public SyncDecisionEngine() {
        this(ConflictStrategy.LOCAL_WINS);
    }

    public SyncDecisionEngine(ConflictStrategy conflictStrategy) {
        this.conflictStrategy = conflictStrategy;
    }

    public SyncAction decide(File localFile, FileInfo remoteFileInfo) {
        if (localFile == null && remoteFileInfo == null) {
            return SyncAction.SKIP;
        }
        if (localFile == null || !localFile.exists()) {
            return SyncAction.DELETE_REMOTE;
        }
        if (remoteFileInfo == null) {
            return SyncAction.UPLOAD;
        }

        String remoteMD5 = remoteFileInfo.getCurrentVersonFileHash();
        if (remoteMD5 == null || remoteMD5.isEmpty()) {
            return SyncAction.UPLOAD;
        }

        String localMD5 = computeLocalMD5(localFile);
        if (localMD5 == null) {
            return SyncAction.SKIP;
        }

        if (localMD5.equals(remoteMD5)) {
            log.debug("SKIP: MD5 match for {}", localFile.getName());
            return SyncAction.SKIP;
        }

        long localModified = localFile.lastModified();
        long remoteModified = 0;
        if (remoteFileInfo.getCurrentVersion() != null) {
            remoteModified = remoteFileInfo.getCurrentVersion().getCreateTime();
        }

        if (localModified > remoteModified) {
            return SyncAction.UPLOAD;
        }
        if (remoteModified > localModified) {
            return SyncAction.DOWNLOAD;
        }

        log.warn("CONFLICT: both modified, local={}, remote={}", localMD5, remoteMD5);
        return switch (conflictStrategy) {
            case LOCAL_WINS -> SyncAction.UPLOAD;
            case REMOTE_WINS -> SyncAction.DOWNLOAD;
            case SKIP -> SyncAction.CONFLICT;
        };
    }

    private String computeLocalMD5(File file) {
        if (!file.isFile() || file.length() == 0) {
            return null;
        }
        try {
            return net.ooder.common.md5.MD5.getHashString(file);
        } catch (Exception e) {
            log.error("Failed to compute MD5 for: {}", file.getAbsolutePath(), e);
            return null;
        }
    }
}
