package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class LocalDocument {
    
    private String docId;
    private String path;
    private String title;
    private String content;
    private String category;
    private String fileType;
    private long fileSize;
    private long lastModified;
    private Map<String, Object> metadata;

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
