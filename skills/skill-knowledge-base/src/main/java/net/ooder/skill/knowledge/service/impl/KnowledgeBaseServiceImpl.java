package net.ooder.skill.knowledge.service.impl;

import net.ooder.skill.knowledge.model.KnowledgeBase;
import net.ooder.skill.knowledge.model.KbDocument;
import net.ooder.skill.knowledge.model.SearchResult;
import net.ooder.skill.knowledge.service.DocumentIndexService;
import net.ooder.skill.knowledge.service.KnowledgeBaseService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    private final Map<String, KnowledgeBase> kbStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, KbDocument>> docStore = new ConcurrentHashMap<>();
    private final DocumentIndexService indexService;
    
    public KnowledgeBaseServiceImpl(DocumentIndexService indexService) {
        this.indexService = indexService;
        initSampleData();
    }
    
    private void initSampleData() {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setId("kb-default");
        kb.setName("默认知识库");
        kb.setDescription("系统默认知识库");
        kb.setType(KnowledgeBase.KbType.GENERAL);
        kb.setVisibility(KnowledgeBase.KbVisibility.PRIVATE);
        kb.setStatus(KnowledgeBase.KbStatus.ACTIVE);
        kb.setOwnerId("system");
        kb.setCreatedAt(System.currentTimeMillis());
        kb.setUpdatedAt(System.currentTimeMillis());
        kb.setConfig(new KnowledgeBase.KbConfig());
        kb.getConfig().setChunkSize(500);
        kb.getConfig().setChunkOverlap(50);
        kb.getConfig().setTopK(5);
        kb.getConfig().setScoreThreshold(0.5);
        kbStore.put(kb.getId(), kb);
        docStore.put(kb.getId(), new ConcurrentHashMap<>());
    }
    
    @Override
    public KnowledgeBase create(KnowledgeBase kb) {
        if (kb.getId() == null || kb.getId().isEmpty()) {
            kb.setId("kb-" + UUID.randomUUID().toString().substring(0, 8));
        }
        kb.setCreatedAt(System.currentTimeMillis());
        kb.setUpdatedAt(System.currentTimeMillis());
        if (kb.getStatus() == null) {
            kb.setStatus(KnowledgeBase.KbStatus.ACTIVE);
        }
        if (kb.getConfig() == null) {
            kb.setConfig(new KnowledgeBase.KbConfig());
        }
        kbStore.put(kb.getId(), kb);
        docStore.put(kb.getId(), new ConcurrentHashMap<>());
        return kb;
    }
    
    @Override
    public Optional<KnowledgeBase> findById(String id) {
        return Optional.ofNullable(kbStore.get(id));
    }
    
    @Override
    public List<KnowledgeBase> findByOwner(String ownerId) {
        return kbStore.values().stream()
                .filter(kb -> ownerId.equals(kb.getOwnerId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findPublicBases() {
        return kbStore.values().stream()
                .filter(kb -> kb.getVisibility() == KnowledgeBase.KbVisibility.PRIVATE)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByType(String type) {
        return kbStore.values().stream()
                .filter(kb -> kb.getType() != null && kb.getType().name().equals(type))
                .collect(Collectors.toList());
    }
    
    @Override
    public KnowledgeBase update(KnowledgeBase kb) {
        KnowledgeBase existing = kbStore.get(kb.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kb.getId());
        }
        kb.setUpdatedAt(System.currentTimeMillis());
        kb.setCreatedAt(existing.getCreatedAt());
        kbStore.put(kb.getId(), kb);
        return kb;
    }
    
    @Override
    public void delete(String id) {
        kbStore.remove(id);
        docStore.remove(id);
        indexService.clearIndex(id);
    }
    
    @Override
    public KbDocument addDocument(String kbId, KbDocument document) {
        if (!kbStore.containsKey(kbId)) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId("doc-" + UUID.randomUUID().toString().substring(0, 8));
        }
        document.setCreatedAt(System.currentTimeMillis());
        document.setUpdatedAt(System.currentTimeMillis());
        docStore.get(kbId).put(document.getId(), document);
        
        if (document.getContent() != null && !document.getContent().isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", document.getTitle());
            metadata.put("source", document.getSource());
            indexService.indexDocument(kbId, document.getId(), document.getContent(), metadata);
        }
        
        return document;
    }
    
    @Override
    public void removeDocument(String kbId, String documentId) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        if (docs != null) {
            docs.remove(documentId);
            indexService.removeDocument(kbId, documentId);
        }
    }
    
    @Override
    public KbDocument updateDocument(String kbId, KbDocument document) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        if (docs == null || !docs.containsKey(document.getId())) {
            throw new IllegalArgumentException("Document not found: " + document.getId());
        }
        document.setUpdatedAt(System.currentTimeMillis());
        docs.put(document.getId(), document);
        
        if (document.getContent() != null) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", document.getTitle());
            metadata.put("source", document.getSource());
            indexService.updateDocument(kbId, document.getId(), document.getContent(), metadata);
        }
        
        return document;
    }
    
    @Override
    public List<KbDocument> listDocuments(String kbId) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        return docs == null ? Collections.emptyList() : new ArrayList<>(docs.values());
    }
    
    @Override
    public Optional<KbDocument> findDocument(String kbId, String documentId) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        return docs == null ? Optional.empty() : Optional.ofNullable(docs.get(documentId));
    }
    
    @Override
    public List<SearchResult> search(String kbId, String query, int topK, double threshold) {
        return indexService.search(kbId, query, topK, threshold);
    }
    
    @Override
    public List<SearchResult> searchMulti(List<String> kbIds, String query, int topK, double threshold) {
        return indexService.searchMulti(kbIds, query, topK, threshold);
    }
    
    @Override
    public void rebuildIndex(String kbId) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        if (docs == null) {
            return;
        }
        Map<String, String> contents = new HashMap<>();
        docs.forEach((docId, doc) -> {
            if (doc.getContent() != null) {
                contents.put(docId, doc.getContent());
            }
        });
        indexService.rebuildIndex(kbId, contents);
        
        KnowledgeBase kb = kbStore.get(kbId);
        if (kb != null) {
            kb.setUpdatedAt(System.currentTimeMillis());
        }
    }
    
    @Override
    public KnowledgeBase updateStatus(String kbId, String status) {
        KnowledgeBase kb = kbStore.get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        kb.setStatus(KnowledgeBase.KbStatus.valueOf(status));
        kb.setUpdatedAt(System.currentTimeMillis());
        return kb;
    }
    
    @Override
    public long getDocumentCount(String kbId) {
        Map<String, KbDocument> docs = docStore.get(kbId);
        return docs == null ? 0 : docs.size();
    }
    
    @Override
    public long getTotalChunkCount(String kbId) {
        return indexService.getDocumentCount(kbId);
    }
}
