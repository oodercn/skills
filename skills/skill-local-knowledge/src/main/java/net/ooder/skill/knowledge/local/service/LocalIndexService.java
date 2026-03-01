package net.ooder.skill.knowledge.local.service;

import net.ooder.skill.knowledge.local.model.SearchResult;
import net.ooder.skill.knowledge.local.model.LocalDocument;

import java.util.List;
import java.util.Map;

public interface LocalIndexService {
    
    void scanAndIndex(String path);
    
    List<SearchResult> search(String query, int topK);
    
    List<SearchResult> searchWithFilters(String query, Map<String, Object> filters, int topK);
    
    LocalDocument getDocument(String docId);
    
    List<LocalDocument> listDocuments(String path);
    
    int getDocumentCount();
    
    void reindex();
}
