package net.ooder.skill.search.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.search.SearchProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ElasticSearchProvider implements SearchProvider {
    
    private String hosts = "http://localhost:9200";
    private String username;
    private String password;
    
    private final Map<String, Map<String, Map<String, Object>>> indices = new ConcurrentHashMap<>();
    
    public ElasticSearchProvider() {
        this.username = System.getenv("ES_USERNAME");
        this.password = System.getenv("ES_PASSWORD");
    }
    
    @Override
    public String getProviderType() {
        return "elasticsearch";
    }
    
    @Override
    public IndexResult index(IndexRequest request) {
        log.info("Index document: index={}, docId={}", request.getIndexName(), request.getDocId());
        
        String docId = request.getDocId() != null ? request.getDocId() : UUID.randomUUID().toString();
        
        indices.computeIfAbsent(request.getIndexName(), k -> new ConcurrentHashMap<>())
               .put(docId, request.getDocument());
        
        IndexResult result = new IndexResult();
        result.setSuccess(true);
        result.setIndexName(request.getIndexName());
        result.setDocId(docId);
        result.setStatus("created");
        result.setIndexedAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public IndexResult indexBatch(List<IndexRequest> requests) {
        log.info("Index batch documents: count={}", requests.size());
        
        for (IndexRequest request : requests) {
            index(request);
        }
        
        IndexResult result = new IndexResult();
        result.setSuccess(true);
        result.setStatus("created");
        result.setIndexedAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public boolean deleteIndex(String indexName, String docId) {
        log.info("Delete document: index={}, docId={}", indexName, docId);
        
        Map<String, Map<String, Object>> index = indices.get(indexName);
        if (index != null) {
            return index.remove(docId) != null;
        }
        return false;
    }
    
    @Override
    public boolean deleteIndexBatch(String indexName, List<String> docIds) {
        log.info("Delete batch documents: index={}, count={}", indexName, docIds.size());
        
        Map<String, Map<String, Object>> index = indices.get(indexName);
        if (index != null) {
            for (String docId : docIds) {
                index.remove(docId);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public SearchResult search(SearchRequest request) {
        log.info("Search: index={}, query={}", request.getIndexName(), request.getQuery());
        
        long startTime = System.currentTimeMillis();
        
        Map<String, Map<String, Object>> index = indices.get(request.getIndexName());
        List<Map<String, Object>> hits = new ArrayList<>();
        
        if (index != null) {
            for (Map<String, Object> doc : index.values()) {
                if (request.getQuery() == null || request.getQuery().isEmpty()) {
                    hits.add(doc);
                } else {
                    String query = request.getQuery().toLowerCase();
                    for (Object value : doc.values()) {
                        if (value != null && value.toString().toLowerCase().contains(query)) {
                            hits.add(doc);
                            break;
                        }
                    }
                }
            }
        }
        
        int from = request.getFrom();
        int size = request.getSize() > 0 ? request.getSize() : 10;
        
        List<Map<String, Object>> pagedHits = new ArrayList<>();
        for (int i = from; i < Math.min(from + size, hits.size()); i++) {
            pagedHits.add(hits.get(i));
        }
        
        SearchResult result = new SearchResult();
        result.setSuccess(true);
        result.setTotal(hits.size());
        result.setHits(pagedHits);
        result.setTook(System.currentTimeMillis() - startTime);
        
        return result;
    }
    
    @Override
    public SearchResult searchWithHighlight(SearchRequest request, List<String> highlightFields) {
        SearchResult result = search(request);
        
        List<Map<String, Object>> highlights = new ArrayList<>();
        for (Map<String, Object> hit : result.getHits()) {
            Map<String, Object> highlight = new HashMap<>();
            for (String field : highlightFields) {
                Object value = hit.get(field);
                if (value != null) {
                    highlight.put(field, "<em>" + value + "</em>");
                }
            }
            highlights.add(highlight);
        }
        result.setHighlights(highlights);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getDocument(String indexName, String docId) {
        Map<String, Map<String, Object>> index = indices.get(indexName);
        if (index != null) {
            return index.get(docId);
        }
        return null;
    }
    
    @Override
    public boolean updateDocument(String indexName, String docId, Map<String, Object> updates) {
        log.info("Update document: index={}, docId={}", indexName, docId);
        
        Map<String, Map<String, Object>> index = indices.get(indexName);
        if (index != null) {
            Map<String, Object> doc = index.get(docId);
            if (doc != null) {
                doc.putAll(updates);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public long count(String indexName, Map<String, Object> query) {
        Map<String, Map<String, Object>> index = indices.get(indexName);
        return index != null ? index.size() : 0;
    }
    
    @Override
    public AggregationResult aggregate(String indexName, String field, String aggType, Map<String, Object> options) {
        log.info("Aggregate: index={}, field={}, type={}", indexName, field, aggType);
        
        AggregationResult result = new AggregationResult();
        result.setSuccess(true);
        result.setField(field);
        result.setAggType(aggType);
        result.setResult(new HashMap<>());
        
        return result;
    }
    
    public void setHosts(String hosts) { this.hosts = hosts; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
