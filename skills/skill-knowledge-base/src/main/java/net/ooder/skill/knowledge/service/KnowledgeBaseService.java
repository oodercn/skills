package net.ooder.skill.knowledge.service;

import net.ooder.skill.knowledge.model.KnowledgeBase;
import net.ooder.skill.knowledge.model.KbDocument;
import net.ooder.skill.knowledge.model.SearchResult;

import java.util.List;
import java.util.Optional;

public interface KnowledgeBaseService {
    
    KnowledgeBase create(KnowledgeBase kb);
    
    Optional<KnowledgeBase> findById(String id);
    
    List<KnowledgeBase> findByOwner(String ownerId);
    
    List<KnowledgeBase> findPublicBases();
    
    List<KnowledgeBase> findByType(String type);
    
    KnowledgeBase update(KnowledgeBase kb);
    
    void delete(String id);
    
    KbDocument addDocument(String kbId, KbDocument document);
    
    void removeDocument(String kbId, String documentId);
    
    KbDocument updateDocument(String kbId, KbDocument document);
    
    List<KbDocument> listDocuments(String kbId);
    
    Optional<KbDocument> findDocument(String kbId, String documentId);
    
    List<SearchResult> search(String kbId, String query, int topK, double threshold);
    
    List<SearchResult> searchMulti(List<String> kbIds, String query, int topK, double threshold);
    
    void rebuildIndex(String kbId);
    
    KnowledgeBase updateStatus(String kbId, String status);
    
    long getDocumentCount(String kbId);
    
    long getTotalChunkCount(String kbId);
}
