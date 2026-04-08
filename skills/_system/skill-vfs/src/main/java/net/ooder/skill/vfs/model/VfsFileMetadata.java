package net.ooder.skill.vfs.model;

import java.time.LocalDateTime;

public class VfsFileMetadata {

    private String fileId;
    private String originalName;
    private String storedPath;
    private long size;
    private String mimeType;
    private String scope;
    private String refId;
    private String uploaderId;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private String previewUrl;
    private String checksum;

    public VfsFileMetadata() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredPath() { return storedPath; }
    public void setStoredPath(String storedPath) { this.storedPath = storedPath; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getRefId() { return refId; }
    public void setRefId(String refId) { this.refId = refId; }
    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public String getFormattedSize() {
        if (size < 1024) return size + "B";
        if (size < 1048576) return String.format("%.1fKB", size / 1024.0);
        if (size < 1073741824) return String.format("%.1fMB", size / 1048576.0);
        return String.format("%.1fGB", size / 1073741824.0);
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }
}
