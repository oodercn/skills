package net.ooder.spi.vector;

import java.util.List;
import java.util.Map;

public interface VectorStoreProvider {
    
    String getProviderType();
    
    String getProviderName();
    
    void initialize(VectorStoreConfig config);
    
    void store(String id, float[] vector, Map<String, Object> metadata);
    
    void batchStore(List<VectorData> vectors);
    
    List<SearchResult> search(float[] vector, int topK);
    
    List<SearchResult> search(float[] vector, int topK, Map<String, Object> filter);
    
    void delete(String id);
    
    void batchDelete(List<String> ids);
    
    VectorData get(String id);
    
    long count();
    
    void clear();
    
    void close();
    
    boolean isHealthy();
}
