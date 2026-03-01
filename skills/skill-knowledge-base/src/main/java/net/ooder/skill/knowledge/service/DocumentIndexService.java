package net.ooder.skill.knowledge.service;

import net.ooder.skill.knowledge.model.SearchResult;

import java.util.List;
import java.util.Map;

public interface DocumentIndexService {
    
    void indexDocument(String kbId, String docId, String content, Map<String, Object> metadata);
    
    void removeDocument(String kbId, String docId);
    
    void updateDocument(String kbId, String docId, String content, Map<String, Object> metadata);
    
    List<SearchResult> search(String kbId, String query, int topK, double threshold);
    
    List<SearchResult> searchMulti(List<String> kbIds, String query, int topK, double threshold);
    
    void clearIndex(String kbId);
    
    void rebuildIndex(String kbId, Map<String, String> documents);
    
    long getDocumentCount(String kbId);
    
    Map<String, Object> getIndexStats(String kbId);
}
