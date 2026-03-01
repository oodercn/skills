package net.ooder.skill.rag.model;

import java.util.Map;

public class RetrievedDocument {
    private String id;
    private String kbId;
    private String title;
    private String content;
    private String snippet;
    private double score;
    private Map<String, Object> metadata;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
