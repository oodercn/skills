package net.ooder.skill.knowledge.model;

import java.util.Map;

public class KnowledgeBase {
    
    private String id;
    private String name;
    private String description;
    private KbType type;
    private KbVisibility visibility;
    private KbStatus status;
    private String ownerId;
    private String departmentId;
    private int documentCount;
    private int totalChunks;
    private int totalTokens;
    private KbConfig config;
    private Map<String, Object> metadata;
    private long createdAt;
    private long updatedAt;

    public enum KbType {
        GENERAL("GENERAL", "通用知识库"),
        DOMAIN("DOMAIN", "领域知识库"),
        SCENE("SCENE", "场景知识库"),
        PERSONAL("PERSONAL", "个人知识库");

        private final String code;
        private final String description;

        KbType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public enum KbVisibility {
        ENTERPRISE("ENTERPRISE", "企业可见"),
        DEPARTMENT("DEPARTMENT", "部门可见"),
        PRIVATE("PRIVATE", "私有");

        private final String code;
        private final String description;

        KbVisibility(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public enum KbStatus {
        ACTIVE("ACTIVE", "激活"),
        INACTIVE("INACTIVE", "停用"),
        PROCESSING("PROCESSING", "处理中");

        private final String code;
        private final String description;

        KbStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public static class KbConfig {
        private int chunkSize = 500;
        private int chunkOverlap = 50;
        private String embeddingModel = "text-embedding-3-small";
        private int topK = 5;
        private double scoreThreshold = 0.7;
        private boolean rerankEnabled = false;

        public int getChunkSize() { return chunkSize; }
        public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }
        public int getChunkOverlap() { return chunkOverlap; }
        public void setChunkOverlap(int chunkOverlap) { this.chunkOverlap = chunkOverlap; }
        public String getEmbeddingModel() { return embeddingModel; }
        public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }
        public double getScoreThreshold() { return scoreThreshold; }
        public void setScoreThreshold(double scoreThreshold) { this.scoreThreshold = scoreThreshold; }
        public boolean isRerankEnabled() { return rerankEnabled; }
        public void setRerankEnabled(boolean rerankEnabled) { this.rerankEnabled = rerankEnabled; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public KbType getType() { return type; }
    public void setType(KbType type) { this.type = type; }
    public KbVisibility getVisibility() { return visibility; }
    public void setVisibility(KbVisibility visibility) { this.visibility = visibility; }
    public KbStatus getStatus() { return status; }
    public void setStatus(KbStatus status) { this.status = status; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public int getDocumentCount() { return documentCount; }
    public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public KbConfig getConfig() { return config; }
    public void setConfig(KbConfig config) { this.config = config; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
