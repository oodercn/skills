package net.ooder.skill.rag.model;

import java.util.List;

public class RagResult {
    private String query;
    private List<RetrievedDocument> documents;
    private String combinedContext;
    private double maxScore;
    private double avgScore;
    private int totalRetrieved;
    private long retrievalTimeMs;
    private String strategy;
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<RetrievedDocument> getDocuments() { return documents; }
    public void setDocuments(List<RetrievedDocument> documents) { this.documents = documents; }
    
    public String getCombinedContext() { return combinedContext; }
    public void setCombinedContext(String combinedContext) { this.combinedContext = combinedContext; }
    
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    
    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
    
    public int getTotalRetrieved() { return totalRetrieved; }
    public void setTotalRetrieved(int totalRetrieved) { this.totalRetrieved = totalRetrieved; }
    
    public long getRetrievalTimeMs() { return retrievalTimeMs; }
    public void setRetrievalTimeMs(long retrievalTimeMs) { this.retrievalTimeMs = retrievalTimeMs; }
    
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
}
