package net.ooder.skill.document.model;

import java.util.Map;

public class Document {
    
    private String id;
    private String kbId;
    private String title;
    private String content;
    private String source;
    private String sourceType;
    private long contentLength;
    private int chunkCount;
    private DocumentStatus status;
    private Map<String, Object> metadata;
    private long createdAt;
    private long updatedAt;

    public enum DocumentStatus {
        PENDING("PENDING", "待处理"),
        PROCESSING("PROCESSING", "处理中"),
        INDEXED("INDEXED", "已索引"),
        FAILED("FAILED", "处理失败");

        private final String code;
        private final String description;

        DocumentStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public enum SourceType {
        FILE("FILE", "文件上传"),
        URL("URL", "URL导入"),
        TEXT("TEXT", "文本输入"),
        API("API", "API推送");

        private final String code;
        private final String description;

        SourceType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public long getContentLength() { return contentLength; }
    public void setContentLength(long contentLength) { this.contentLength = contentLength; }
    public int getChunkCount() { return chunkCount; }
    public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
