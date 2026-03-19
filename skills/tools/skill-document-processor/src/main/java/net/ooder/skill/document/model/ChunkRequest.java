package net.ooder.skill.document.model;

public class ChunkRequest {
    private String content;
    private Integer chunkSize = 500;
    private Integer chunkOverlap = 50;
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }
    
    public Integer getChunkOverlap() { return chunkOverlap; }
    public void setChunkOverlap(Integer chunkOverlap) { this.chunkOverlap = chunkOverlap; }
}
