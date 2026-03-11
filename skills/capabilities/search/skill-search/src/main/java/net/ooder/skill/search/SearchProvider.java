package net.ooder.skill.search;

import java.util.List;
import java.util.Map;

public interface SearchProvider {
    
    String getProviderType();
    
    IndexResult index(IndexRequest request);
    
    IndexResult indexBatch(List<IndexRequest> requests);
    
    boolean deleteIndex(String indexName, String docId);
    
    boolean deleteIndexBatch(String indexName, List<String> docIds);
    
    SearchResult search(SearchRequest request);
    
    SearchResult searchWithHighlight(SearchRequest request, List<String> highlightFields);
    
    Map<String, Object> getDocument(String indexName, String docId);
    
    boolean updateDocument(String indexName, String docId, Map<String, Object> updates);
    
    long count(String indexName, Map<String, Object> query);
    
    AggregationResult aggregate(String indexName, String field, String aggType, Map<String, Object> options);
    
    public static class IndexRequest {
        private String indexName;
        private String docId;
        private Map<String, Object> document;
        private Map<String, Object> metadata;
        
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public Map<String, Object> getDocument() { return document; }
        public void setDocument(Map<String, Object> document) { this.document = document; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class IndexResult {
        private boolean success;
        private String indexName;
        private String docId;
        private String status;
        private String errorCode;
        private String errorMessage;
        private long indexedAt;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getIndexedAt() { return indexedAt; }
        public void setIndexedAt(long indexedAt) { this.indexedAt = indexedAt; }
    }
    
    public static class SearchRequest {
        private String indexName;
        private List<String> indexNames;
        private String query;
        private Map<String, Object> filters;
        private List<String> fields;
        private List<SortField> sortFields;
        private int from;
        private int size;
        private boolean explain;
        
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        public List<String> getIndexNames() { return indexNames; }
        public void setIndexNames(List<String> indexNames) { this.indexNames = indexNames; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
        public List<String> getFields() { return fields; }
        public void setFields(List<String> fields) { this.fields = fields; }
        public List<SortField> getSortFields() { return sortFields; }
        public void setSortFields(List<SortField> sortFields) { this.sortFields = sortFields; }
        public int getFrom() { return from; }
        public void setFrom(int from) { this.from = from; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public boolean isExplain() { return explain; }
        public void setExplain(boolean explain) { this.explain = explain; }
    }
    
    public static class SortField {
        private String field;
        private String order;
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getOrder() { return order; }
        public void setOrder(String order) { this.order = order; }
    }
    
    public static class SearchResult {
        private boolean success;
        private long total;
        private List<Map<String, Object>> hits;
        private List<Map<String, Object>> highlights;
        private Map<String, Object> aggregations;
        private long took;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public List<Map<String, Object>> getHits() { return hits; }
        public void setHits(List<Map<String, Object>> hits) { this.hits = hits; }
        public List<Map<String, Object>> getHighlights() { return highlights; }
        public void setHighlights(List<Map<String, Object>> highlights) { this.highlights = highlights; }
        public Map<String, Object> getAggregations() { return aggregations; }
        public void setAggregations(Map<String, Object> aggregations) { this.aggregations = aggregations; }
        public long getTook() { return took; }
        public void setTook(long took) { this.took = took; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class AggregationResult {
        private boolean success;
        private String field;
        private String aggType;
        private Map<String, Object> result;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getAggType() { return aggType; }
        public void setAggType(String aggType) { this.aggType = aggType; }
        public Map<String, Object> getResult() { return result; }
        public void setResult(Map<String, Object> result) { this.result = result; }
    }
}
