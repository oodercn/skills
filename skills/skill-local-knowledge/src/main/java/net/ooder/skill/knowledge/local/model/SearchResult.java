package net.ooder.skill.knowledge.local.model;

import java.util.List;
import java.util.Map;

public class SearchResult {
    
    private String docId;
    private String title;
    private String snippet;
    private String path;
    private String category;
    private double score;
    private Map<String, Object> metadata;
    private List<String> highlights;

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
}
