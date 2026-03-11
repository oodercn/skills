package net.ooder.skill.knowledge.service.impl;

import net.ooder.skill.knowledge.model.KnowledgeBase;
import net.ooder.skill.knowledge.model.KbDocument;
import net.ooder.skill.knowledge.model.SearchResult;
import net.ooder.skill.knowledge.service.DocumentIndexService;
import net.ooder.skill.knowledge.service.KnowledgeBaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    @Autowired
    private DocumentIndexService indexService;
    
    private final Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, List<KbDocument>> documents = new ConcurrentHashMap<>();

    @Override
    public KnowledgeBase create(KnowledgeBase kb) {
        String id = UUID.randomUUID().toString();
        kb.setId(id);
        kb.setCreatedAt(System.currentTimeMillis());
        kb.setUpdatedAt(System.currentTimeMillis());
        kb.setDocumentCount(0);
        knowledgeBases.put(id, kb);
        documents.put(id, new ArrayList<>());
        return kb;
    }

    @Override
    public Optional<KnowledgeBase> findById(String id) {
        return Optional.ofNullable(knowledgeBases.get(id));
    }

    @Override
    public List<KnowledgeBase> findByOwner(String ownerId) {
        return knowledgeBases.values().stream()
            .filter(kb -> ownerId.equals(kb.getOwnerId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeBase> findPublicBases() {
        return knowledgeBases.values().stream()
            .filter(kb -> "PUBLIC".equals(kb.getVisibility() != null ? kb.getVisibility().name() : null))
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeBase> findByType(String type) {
        return knowledgeBases.values().stream()
            .filter(kb -> type.equals(kb.getType() != null ? kb.getType().name() : null))
            .collect(Collectors.toList());
    }

    @Override
    public KnowledgeBase update(KnowledgeBase kb) {
        KnowledgeBase existing = knowledgeBases.get(kb.getId());
        if (existing != null) {
            kb.setUpdatedAt(System.currentTimeMillis());
            kb.setCreatedAt(existing.getCreatedAt());
            kb.setDocumentCount(existing.getDocumentCount());
            knowledgeBases.put(kb.getId(), kb);
            return kb;
        }
        throw new IllegalArgumentException("Knowledge base not found: " + kb.getId());
    }

    @Override
    public void delete(String id) {
        knowledgeBases.remove(id);
        documents.remove(id);
        indexService.clearIndex(id);
    }

    @Override
    public KbDocument addDocument(String kbId, KbDocument document) {
        List<KbDocument> docs = documents.computeIfAbsent(kbId, k -> new ArrayList<>());
        String docId = UUID.randomUUID().toString();
        document.setId(docId);
        document.setKbId(kbId);
        document.setCreatedAt(System.currentTimeMillis());
        document.setUpdatedAt(System.currentTimeMillis());
        docs.add(document);
        
        KnowledgeBase kb = knowledgeBases.get(kbId);
        if (kb != null) {
            kb.setDocumentCount(docs.size());
        }
        
        indexService.indexDocument(kbId, docId, document.getContent());
        
        return document;
    }

    @Override
    public void removeDocument(String kbId, String documentId) {
        List<KbDocument> docs = documents.get(kbId);
        if (docs != null) {
            docs.removeIf(doc -> documentId.equals(doc.getId()));
            
            KnowledgeBase kb = knowledgeBases.get(kbId);
            if (kb != null) {
                kb.setDocumentCount(docs.size());
            }
        }
        
        indexService.removeDocument(kbId, documentId);
    }

    @Override
    public KbDocument updateDocument(String kbId, KbDocument document) {
        removeDocument(kbId, document.getId());
        return addDocument(kbId, document);
    }

    @Override
    public List<KbDocument> listDocuments(String kbId) {
        return documents.getOrDefault(kbId, Collections.emptyList());
    }

    @Override
    public Optional<KbDocument> findDocument(String kbId, String documentId) {
        List<KbDocument> docs = documents.get(kbId);
        if (docs != null) {
            return docs.stream()
                .filter(doc -> documentId.equals(doc.getId()))
                .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public List<SearchResult> search(String kbId, String query, int topK, double threshold) {
        return indexService.search(kbId, query, topK, threshold);
    }

    @Override
    public List<SearchResult> searchMulti(List<String> kbIds, String query, int topK, double threshold) {
        List<SearchResult> results = new ArrayList<>();
        for (String kbId : kbIds) {
            results.addAll(search(kbId, query, topK, threshold));
        }
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results.stream().limit(topK).collect(Collectors.toList());
    }

    @Override
    public void rebuildIndex(String kbId) {
        indexService.clearIndex(kbId);
        List<KbDocument> docs = documents.get(kbId);
        if (docs != null) {
            for (KbDocument doc : docs) {
                indexService.indexDocument(kbId, doc.getId(), doc.getContent());
            }
        }
    }

    @Override
    public KnowledgeBase updateStatus(String kbId, String status) {
        KnowledgeBase kb = knowledgeBases.get(kbId);
        if (kb != null) {
            kb.setUpdatedAt(System.currentTimeMillis());
            return kb;
        }
        throw new IllegalArgumentException("Knowledge base not found: " + kbId);
    }

    @Override
    public long getDocumentCount(String kbId) {
        List<KbDocument> docs = documents.get(kbId);
        return docs != null ? docs.size() : 0;
    }

    @Override
    public long getTotalChunkCount(String kbId) {
        return indexService.getDocumentCount(kbId);
    }
}
