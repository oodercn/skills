package net.ooder.skill.vfs.impl;

import net.ooder.skill.vfs.VfsService;
import net.ooder.skill.vfs.model.VfsFileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LocalVfsServiceImpl implements VfsService {

    private static final Logger log = LoggerFactory.getLogger(LocalVfsServiceImpl.class);

    @Value("${vfs.storage.root:./data/vfs}")
    private String storageRoot;

    @Value("${vfs.preview.base-url:/api/v1/chat/files}")
    private String previewBaseUrl;

    private final Map<String, VfsFileMetadata> metadataStore = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public VfsFileMetadata upload(MultipartFile file, String scope, String refId) {
        try {
            return upload(file.getInputStream(), file.getOriginalFilename(),
                file.getSize(), file.getContentType(), scope, refId);
        } catch (IOException e) {
            throw new RuntimeException("VFS upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public VfsFileMetadata upload(InputStream inputStream, String originalFilename,
                                   long size, String contentType, String scope, String refId) {
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String datePath = LocalDateTime.now().format(DATE_FMT);
        String safeScope = (scope != null ? scope : "UNKNOWN").toLowerCase();
        String relativePath = safeScope + "/" + datePath + "/" + fileId + "_" + sanitizeFileName(originalFilename);
        Path fullPath = Paths.get(storageRoot, relativePath).normalize();

        try {
            Files.createDirectories(fullPath.getParent());
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);

            VfsFileMetadata metadata = new VfsFileMetadata();
            metadata.setFileId(fileId);
            metadata.setOriginalName(originalFilename);
            metadata.setStoredPath(fullPath.toString());
            metadata.setSize(size);
            metadata.setMimeType(contentType != null ? contentType : "application/octet-stream");
            metadata.setScope(safeScope);
            metadata.setRefId(refId != null ? refId : "");
            metadata.setPreviewUrl(previewBaseUrl + "/" + fileId + "/preview");
            metadata.setChecksum(computeChecksum(fullPath));

            generateThumbnail(metadata, fullPath);

            metadataStore.put(fileId, metadata);
            log.info("[VFS] Uploaded: {} ({}) -> {}", originalFilename, metadata.getFormattedSize(), fileId);
            return metadata;

        } catch (IOException e) {
            throw new RuntimeException("VFS write failed for " + fullPath, e);
        }
    }

    @Override
    public InputStream download(String fileId) {
        VfsFileMetadata meta = metadataStore.get(fileId);
        if (meta == null) throw new RuntimeException("File not found: " + fileId);
        try { return new FileInputStream(meta.getStoredPath()); }
        catch (FileNotFoundException e) { throw new RuntimeException("File not found on disk: " + meta.getStoredPath(), e); }
    }

    @Override
    public VfsFileMetadata getMetadata(String fileId) { return metadataStore.get(fileId); }

    @Override
    public boolean delete(String fileId) {
        VfsFileMetadata meta = metadataStore.remove(fileId);
        if (meta == null) return false;
        try { Files.deleteIfExists(Paths.get(meta.getStoredPath())); return true; }
        catch (IOException e) { log.warn("[VFS] Failed to delete file {}: {}", fileId, e.getMessage()); return false; }
    }

    @Override
    public List<VfsFileMetadata> listByRef(String scope, String refId) {
        return metadataStore.values().stream()
            .filter(m -> scope.equals(m.getScope()) && refId.equals(m.getRefId()))
            .sorted(Comparator.comparing(VfsFileMetadata::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public String getPreviewUrl(String fileId, int expirySeconds) {
        VfsFileMetadata meta = metadataStore.get(fileId);
        if (meta == null) return null;
        return previewBaseUrl + "/" + fileId + "/preview?t=" + (System.currentTimeMillis() + expirySeconds * 1000L);
    }

    private String sanitizeFileName(String name) {
        if (name == null) return "unnamed";
        return name.replaceAll("[^a-zA-Z0-9._\\-\\u4e00-\\u9fa5]", "_");
    }

    private String computeChecksum(Path path) {
        try { return Long.toHexString(Files.size(path)) + "-" + path.toFile().lastModified(); }
        catch (Exception e) { return "unknown"; }
    }

    private void generateThumbnail(VfsFileMetadata meta, Path path) {
        if (meta.isImage()) meta.setThumbnailUrl(previewBaseUrl + "/" + meta.getFileId() + "/thumbnail");
        else if (meta.isPdf()) meta.setThumbnailUrl("/assets/icons/file-pdf.svg");
        else meta.setThumbnailUrl("/assets/icons/file-generic.svg");
    }
}
