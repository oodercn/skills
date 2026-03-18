package net.ooder.skill.scene.storage;

import net.ooder.scene.skill.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class LocalFileStorageProvider implements StorageProvider {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageProvider.class);

    @Value("${ooder.storage.path:./data}")
    private String storagePath;

    @Override
    public String getProviderType() {
        return "local";
    }

    @Override
    public byte[] readFile(String filePath) {
        log.debug("[readFile] Reading file: {}", filePath);
        try {
            Path path = Paths.get(storagePath, filePath);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("[readFile] Failed to read file {}: {}", filePath, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean writeFile(String filePath, byte[] content, boolean overwrite) {
        log.debug("[writeFile] Writing file: {}", filePath);
        try {
            Path path = Paths.get(storagePath, filePath);
            Files.createDirectories(path.getParent());
            
            if (overwrite) {
                Files.write(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.write(path, content, StandardOpenOption.CREATE_NEW);
            }
            return true;
        } catch (Exception e) {
            log.error("[writeFile] Failed to write file {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteFile(String filePath, boolean recursive) {
        log.debug("[deleteFile] Deleting file: {}", filePath);
        try {
            Path path = Paths.get(storagePath, filePath);
            if (Files.exists(path)) {
                if (recursive && Files.isDirectory(path)) {
                    Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.error("[deleteFile] Failed to delete {}", p);
                            }
                        });
                } else {
                    Files.delete(path);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("[deleteFile] Failed to delete file {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public List<FileInfo> listFiles(String directoryPath, String pattern, boolean recursive) {
        log.debug("[listFiles] Listing files in: {}", directoryPath);
        List<FileInfo> files = new ArrayList<>();
        try {
            Path path = Paths.get(storagePath, directoryPath);
            if (!Files.exists(path)) {
                return files;
            }
            
            PathMatcher matcher = pattern != null ? 
                FileSystems.getDefault().getPathMatcher("glob:" + pattern) : null;
            
            Files.walk(path, recursive ? Integer.MAX_VALUE : 1)
                .filter(p -> !p.equals(path))
                .filter(p -> matcher == null || matcher.matches(p.getFileName()))
                .forEach(p -> {
                    FileInfo info = new FileInfo();
                    info.setPath(p.toString().substring(storagePath.length()));
                    info.setName(p.getFileName().toString());
                    info.setDirectory(Files.isDirectory(p));
                    try {
                        info.setSize(Files.size(p));
                        info.setLastModified(Files.getLastModifiedTime(p).toMillis());
                        info.setContentType(Files.probeContentType(p));
                    } catch (IOException e) {
                        log.error("[listFiles] Failed to get file info for {}", p);
                    }
                    files.add(info);
                });
        } catch (Exception e) {
            log.error("[listFiles] Failed to list files in {}: {}", directoryPath, e.getMessage());
        }
        return files;
    }

    @Override
    public boolean fileExists(String filePath) {
        Path path = Paths.get(storagePath, filePath);
        return Files.exists(path);
    }

    @Override
    public boolean createDirectory(String directoryPath) {
        log.debug("[createDirectory] Creating directory: {}", directoryPath);
        try {
            Path path = Paths.get(storagePath, directoryPath);
            Files.createDirectories(path);
            return true;
        } catch (Exception e) {
            log.error("[createDirectory] Failed to create directory {}: {}", directoryPath, e.getMessage());
            return false;
        }
    }

    @Override
    public FileInfo getFileInfo(String filePath) {
        log.debug("[getFileInfo] Getting file info: {}", filePath);
        try {
            Path path = Paths.get(storagePath, filePath);
            if (!Files.exists(path)) {
                return null;
            }
            
            FileInfo info = new FileInfo();
            info.setPath(filePath);
            info.setName(path.getFileName().toString());
            info.setDirectory(Files.isDirectory(path));
            info.setSize(Files.size(path));
            info.setLastModified(Files.getLastModifiedTime(path).toMillis());
            info.setContentType(Files.probeContentType(path));
            return info;
        } catch (Exception e) {
            log.error("[getFileInfo] Failed to get file info for {}: {}", filePath, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        log.debug("[copyFile] Copying {} to {}", sourcePath, targetPath);
        try {
            Path source = Paths.get(storagePath, sourcePath);
            Path target = Paths.get(storagePath, targetPath);
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            log.error("[copyFile] Failed to copy {} to {}: {}", sourcePath, targetPath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean moveFile(String sourcePath, String targetPath) {
        log.debug("[moveFile] Moving {} to {}", sourcePath, targetPath);
        try {
            Path source = Paths.get(storagePath, sourcePath);
            Path target = Paths.get(storagePath, targetPath);
            Files.createDirectories(target.getParent());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            log.error("[moveFile] Failed to move {} to {}: {}", sourcePath, targetPath, e.getMessage());
            return false;
        }
    }

    @Override
    public StorageQuota getQuota() {
        StorageQuota quota = new StorageQuota();
        try {
            Path path = Paths.get(storagePath);
            if (Files.exists(path)) {
                FileStore store = Files.getFileStore(path);
                quota.setTotalSpace(store.getTotalSpace());
                quota.setAvailableSpace(store.getUsableSpace());
                quota.setUsedSpace(store.getTotalSpace() - store.getUsableSpace());
                
                int fileCount = (int) Files.walk(path)
                    .filter(Files::isRegularFile)
                    .count();
                quota.setFileCount(fileCount);
            }
        } catch (Exception e) {
            log.error("[getQuota] Failed to get quota: {}", e.getMessage());
        }
        return quota;
    }
}
