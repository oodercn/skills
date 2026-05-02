package net.ooder.vfs.sync;

import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class OptimizedLocalFileVisitor implements FileVisitor<Path> {

    private static final Logger log = LoggerFactory.getLogger(OptimizedLocalFileVisitor.class);

    private final Set<String> remotePaths = new LinkedHashSet<>();
    private final Path localDiskPath;
    private final String vfsPath;
    private final PathMappingStrategy pathMapping;
    private final SyncDecisionEngine decisionEngine;
    private final VfsSyncFactory.SyncContext syncContext;
    private final int maxTaskSize;
    private final SyncProgressListener listener;
    private int scannedCount = 0;

    public OptimizedLocalFileVisitor(String vfsPath, Path localDiskPath,
                                      PathMappingStrategy pathMapping,
                                      SyncDecisionEngine decisionEngine,
                                      VfsSyncFactory.SyncContext syncContext,
                                      int maxTaskSize,
                                      SyncProgressListener listener) {
        this.vfsPath = vfsPath;
        this.localDiskPath = localDiskPath;
        this.pathMapping = pathMapping;
        this.decisionEngine = decisionEngine;
        this.syncContext = syncContext;
        this.maxTaskSize = maxTaskSize;
        this.listener = listener;

        loadRemotePaths();
    }

    private void loadRemotePaths() {
        try {
            CtVfsService service = CtVfsFactory.getCtVfsService();
            Folder folder = service.getFolderByPath(vfsPath);
            if (folder == null) return;

            List<Folder> folders = folder.getChildrenRecursivelyList();
            for (Folder child : folders) {
                if (child != null) remotePaths.add(child.getPath());
            }
            List<FileInfo> files = folder.getFileListRecursively();
            for (FileInfo file : files) {
                if (file != null) remotePaths.add(file.getPath());
            }
        } catch (Exception e) {
            log.error("Failed to load remote paths for: {}", vfsPath, e);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (!dir.toFile().isDirectory()) return FileVisitResult.CONTINUE;

        String vfsDirPath = pathMapping.localToVfs(
            dir.toFile().getAbsolutePath(),
            localDiskPath.toAbsolutePath().toString(),
            vfsPath
        );

        if (!remotePaths.contains(vfsDirPath)) {
            try {
                CtVfsFactory.getCtVfsService().mkDir(vfsDirPath);
                remotePaths.add(vfsDirPath);
                log.info("Created remote directory: {}", vfsDirPath);
            } catch (Exception e) {
                log.error("Failed to create remote directory: {}", vfsDirPath, e);
            }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
        scannedCount++;
        if (listener != null) {
            listener.onFileScanned(-1, scannedCount);
        }

        File localFile = filePath.toFile();
        if (!localFile.isFile() || localFile.length() == 0) {
            return FileVisitResult.CONTINUE;
        }

        String vfsFilePath = pathMapping.localToVfs(
            filePath.toFile().getAbsolutePath(),
            localDiskPath.toAbsolutePath().toString(),
            vfsPath
        );

        FileInfo remoteFileInfo = null;
        if (remotePaths.contains(vfsFilePath)) {
            try {
                remoteFileInfo = CtVfsFactory.getCtVfsService().getFileByPath(vfsFilePath);
            } catch (Exception e) {
                log.error("Failed to get remote file info: {}", vfsFilePath, e);
            }
        }

        SyncAction action = decisionEngine.decide(localFile, remoteFileInfo);

        switch (action) {
            case SKIP -> {
                if (listener != null) listener.onFileSkipped(vfsFilePath, "MD5 match");
                return FileVisitResult.CONTINUE;
            }
            case UPLOAD -> {
                syncContext.pendingTasks.add(
                    new OptimizedUploadTask(vfsFilePath, localFile, listener));
                return FileVisitResult.CONTINUE;
            }
            case CONFLICT -> {
                if (listener != null) listener.onFileSkipped(vfsFilePath, "Conflict");
                return FileVisitResult.CONTINUE;
            }
            default -> {
                return FileVisitResult.CONTINUE;
            }
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        log.warn("Failed to visit file: {}", file, exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
