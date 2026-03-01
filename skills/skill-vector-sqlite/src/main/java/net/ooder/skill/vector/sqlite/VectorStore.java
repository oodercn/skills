package net.ooder.skill.vector.sqlite;

import java.util.List;
import java.util.Map;

public interface VectorStore {
    
    void initialize();
    
    void shutdown();
    
    String addVector(String id, double[] embedding, Map<String, Object> metadata);
    
    void addVectors(List<VectorEntry> entries);
    
    void updateVector(String id, double[] embedding, Map<String, Object> metadata);
    
    void deleteVector(String id);
    
    VectorEntry getVector(String id);
    
    List<SearchResult> searchSimilar(double[] queryEmbedding, int topK);
    
    List<SearchResult> searchSimilarWithFilter(double[] queryEmbedding, int topK, Map<String, Object> filter);
    
    int getCount();
    
    void clear();
    
    class VectorEntry {
        private String id;
        private double[] embedding;
        private Map<String, Object> metadata;
        private long createdAt;
        
        public VectorEntry() {}
        
        public VectorEntry(String id, double[] embedding, Map<String, Object> metadata) {
            this.id = id;
            this.embedding = embedding;
            this.metadata = metadata;
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public double[] getEmbedding() { return embedding; }
        public void setEmbedding(double[] embedding) { this.embedding = embedding; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
    
    class SearchResult {
        private String id;
        private double score;
        private Map<String, Object> metadata;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
