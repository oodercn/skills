package net.ooder.skill.document.model;

import java.util.List;
import java.util.Map;

public class ProcessingResult {
    
    private String docId;
    private boolean success;
    private String errorMessage;
    private int chunkCount;
    private int totalTokens;
    private List<DocumentChunk> chunks;
    private Map<String, Object> statistics;
    private long processingTime;

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public int getChunkCount() { return chunkCount; }
    public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public List<DocumentChunk> getChunks() { return chunks; }
    public void setChunks(List<DocumentChunk> chunks) { this.chunks = chunks; }
    public Map<String, Object> getStatistics() { return statistics; }
    public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
}
