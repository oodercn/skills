package net.ooder.vfs.sync;

import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OptimizedSyncPull {

    private static final Logger log = LoggerFactory.getLogger(OptimizedSyncPull.class);

    private final Path localDiskPath;
    private final String vfsPath;
    private final PathMappingStrategy pathMapping;
    private final int maxTaskSize;
    private final SyncProgressListener listener;

    public OptimizedSyncPull(Path localDiskPath, String vfsPath,
                              PathMappingStrategy pathMapping,
                              int maxTaskSize,
                              SyncProgressListener listener) {
        this.localDiskPath = localDiskPath;
        this.vfsPath = vfsPath;
        this.pathMapping = pathMapping;
        this.maxTaskSize = maxTaskSize;
        this.listener = listener;
    }

    public void execute() throws IOException {
        CtVfsService service = CtVfsFactory.getCtVfsService();
        long startTime = System.currentTimeMillis();

        syncFolders(service);
        syncFiles(service);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Pull completed in {}ms", elapsed);
    }

    private void syncFolders(CtVfsService service) throws IOException {
        try {
            Folder rootFolder = service.getFolderByPath(vfsPath);
            if (rootFolder == null) {
                log.warn("Remote folder not found: {}", vfsPath);
                return;
            }

            List<Folder> folders = service.getFolderById(rootFolder.getID()).getChildrenRecursivelyList();
            for (Folder folder : folders) {
                String localDirPath = pathMapping.vfsToLocal(
                    folder.getPath(),
                    localDiskPath.toAbsolutePath().toString(),
                    vfsPath
                );
                Path dir = Paths.get(localDirPath);
                if (Files.notExists(dir)) {
                    Files.createDirectories(dir);
                    log.debug("Created local directory: {}", localDirPath);
                }
            }
        } catch (Exception e) {
            log.error("Failed to sync folders from: {}", vfsPath, e);
        }
    }

    private void syncFiles(CtVfsService service) throws IOException {
        long fileStartTime = System.currentTimeMillis();
        try {
            Folder rootFolder = service.getFolderByPath(vfsPath);
            if (rootFolder == null) return;

            List<FileInfo> remoteFiles = service.getFolderById(rootFolder.getID()).getFileListRecursively();
            int total = remoteFiles.size();
            int downloaded = 0;
            int skipped = 0;
            int errors = 0;

            for (int i = 0; i < remoteFiles.size(); i++) {
                FileInfo remoteFile = remoteFiles.get(i);
                if (listener != null) listener.onFileScanned(total, i + 1);

                String localFilePath = pathMapping.vfsToLocal(
                    remoteFile.getPath(),
                    localDiskPath.toAbsolutePath().toString(),
                    vfsPath
                );

                File localFile = new File(localFilePath);
                if (localFile.getParentFile() != null && !localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }

                if (remoteFile.getCurrentVersion() == null || remoteFile.getCurrentVersion().getLength() <= 0) {
                    if (listener != null) listener.onFileSkipped(remoteFile.getPath(), "No version");
                    skipped++;
                    continue;
                }

                if (localFile.exists()) {
                    String localMD5 = net.ooder.common.md5.MD5.getHashString(localFile);
                    String remoteMD5 = remoteFile.getCurrentVersonFileHash();
                    if (localMD5 != null && localMD5.equals(remoteMD5)) {
                        if (listener != null) listener.onFileSkipped(remoteFile.getPath(), "MD5 match");
                        skipped++;
                        continue;
                    }
                }

                try {
                    long fileStart = System.currentTimeMillis();
                    InputStream stream = service.downLoad(remoteFile.getPath());
                    if (stream != null) {
                        copyStreamToFile(stream, localFile);
                        long fileElapsed = System.currentTimeMillis() - fileStart;
                        downloaded++;
                        if (listener != null) {
                            listener.onFileDownloaded(remoteFile.getPath(),
                                remoteFile.getCurrentVersion().getLength(), fileElapsed);
                        }
                    } else {
                        errors++;
                        if (listener != null) {
                            listener.onFileError(remoteFile.getPath(),
                                new IOException("Download stream is null"));
                        }
                    }
                } catch (Exception e) {
                    errors++;
                    log.error("Failed to download: {}", remoteFile.getPath(), e);
                    if (listener != null) listener.onFileError(remoteFile.getPath(), e);
                }
            }

            long elapsed = System.currentTimeMillis() - fileStartTime;
            if (listener != null) {
                listener.onComplete(new SyncSummary(total, 0, downloaded, skipped, errors, elapsed));
            }
        } catch (Exception e) {
            log.error("Failed to sync files from: {}", vfsPath, e);
        }
    }

    private void copyStreamToFile(InputStream input, File file) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } finally {
            input.close();
        }
    }
}
