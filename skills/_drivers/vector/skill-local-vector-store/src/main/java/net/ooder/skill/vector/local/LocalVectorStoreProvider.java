package net.ooder.skill.vector.local;

import net.ooder.spi.vector.VectorStoreProvider;
import net.ooder.spi.vector.VectorData;
import net.ooder.spi.vector.SearchResult;
import net.ooder.spi.vector.VectorStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地向量存储提供者（基于 SQLite）
 */
public class LocalVectorStoreProvider implements VectorStoreProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalVectorStoreProvider.class);
    
    private static final String PROVIDER_TYPE = "local";
    private static final String PROVIDER_NAME = "Local Vector Store";
    
    private DataSource dataSource;
    private VectorStoreConfig config;
    private boolean initialized = false;
    private final Map<String, float[]> vectorCache = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return PROVIDER_TYPE;
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public void initialize(VectorStoreConfig config) {
        logger.info("Initializing LocalVectorStoreProvider");
        this.config = config;
        
        try {
            this.dataSource = createDataSource(config);
            createTables();
            this.initialized = true;
            logger.info("LocalVectorStoreProvider initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize LocalVectorStoreProvider: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize vector store", e);
        }
    }
    
    @Override
    public void store(String id, float[] vector, Map<String, Object> metadata) {
        checkInitialized();
        
        String sql = "INSERT OR REPLACE INTO vectors (id, vector, metadata, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, vectorToString(vector));
            stmt.setString(3, metadataToString(metadata));
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setLong(5, System.currentTimeMillis());
            
            stmt.executeUpdate();
            
            vectorCache.put(id, vector);
            
            logger.debug("Stored vector: {}", id);
            
        } catch (SQLException e) {
            logger.error("Failed to store vector {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to store vector", e);
        }
    }
    
    @Override
    public void batchStore(List<VectorData> vectors) {
        checkInitialized();
        
        String sql = "INSERT OR REPLACE INTO vectors (id, vector, metadata, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (VectorData vectorData : vectors) {
                stmt.setString(1, vectorData.getId());
                stmt.setString(2, vectorToString(vectorData.getVector()));
                stmt.setString(3, metadataToString(vectorData.getMetadata()));
                stmt.setLong(4, System.currentTimeMillis());
                stmt.setLong(5, System.currentTimeMillis());
                stmt.addBatch();
                
                vectorCache.put(vectorData.getId(), vectorData.getVector());
            }
            
            stmt.executeBatch();
            logger.debug("Batch stored {} vectors", vectors.size());
            
        } catch (SQLException e) {
            logger.error("Failed to batch store vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to batch store vectors", e);
        }
    }
    
    @Override
    public List<SearchResult> search(float[] vector, int topK) {
        return search(vector, topK, null);
    }
    
    @Override
    public List<SearchResult> search(float[] vector, int topK, Map<String, Object> filter) {
        checkInitialized();
        
        List<SearchResult> results = new ArrayList<>();
        String sql = "SELECT id, vector, metadata FROM vectors";
        
        if (filter != null && !filter.isEmpty()) {
            sql += " WHERE " + buildFilterClause(filter);
        }
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<SearchResult> allResults = new ArrayList<>();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String vectorStr = rs.getString("vector");
                String metadataStr = rs.getString("metadata");
                
                float[] storedVector = stringToVector(vectorStr);
                float score = cosineSimilarity(vector, storedVector);
                
                SearchResult result = new SearchResult(id, storedVector, score, stringToMetadata(metadataStr));
                allResults.add(result);
            }
            
            allResults.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
            
            for (int i = 0; i < Math.min(topK, allResults.size()); i++) {
                results.add(allResults.get(i));
            }
            
            logger.debug("Found {} results for search (topK={})", results.size(), topK);
            
        } catch (SQLException e) {
            logger.error("Failed to search vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search vectors", e);
        }
        
        return results;
    }
    
    @Override
    public void delete(String id) {
        checkInitialized();
        
        String sql = "DELETE FROM vectors WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
            
            vectorCache.remove(id);
            
            logger.debug("Deleted vector: {}", id);
            
        } catch (SQLException e) {
            logger.error("Failed to delete vector {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete vector", e);
        }
    }
    
    @Override
    public void batchDelete(List<String> ids) {
        checkInitialized();
        
        String sql = "DELETE FROM vectors WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String id : ids) {
                stmt.setString(1, id);
                stmt.addBatch();
                vectorCache.remove(id);
            }
            
            stmt.executeBatch();
            logger.debug("Batch deleted {} vectors", ids.size());
            
        } catch (SQLException e) {
            logger.error("Failed to batch delete vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to batch delete vectors", e);
        }
    }
    
    @Override
    public VectorData get(String id) {
        checkInitialized();
        
        String sql = "SELECT id, vector, metadata FROM vectors WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String vectorStr = rs.getString("vector");
                    String metadataStr = rs.getString("metadata");
                    
                    return new VectorData(
                        id,
                        stringToVector(vectorStr),
                        stringToMetadata(metadataStr)
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.error("Failed to get vector {}: {}", id, e.getMessage(), e);
        }
        
        return null;
    }
    
    @Override
    public long count() {
        checkInitialized();
        
        String sql = "SELECT COUNT(*) FROM vectors";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            logger.error("Failed to count vectors: {}", e.getMessage(), e);
        }
        
        return 0;
    }
    
    @Override
    public void clear() {
        checkInitialized();
        
        String sql = "DELETE FROM vectors";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            vectorCache.clear();
            
            logger.info("Cleared all vectors");
            
        } catch (SQLException e) {
            logger.error("Failed to clear vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear vectors", e);
        }
    }
    
    @Override
    public void close() {
        if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
                logger.info("Vector store closed");
            } catch (Exception e) {
                logger.error("Failed to close vector store: {}", e.getMessage(), e);
            }
        }
    }
    
    @Override
    public boolean isHealthy() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SELECT 1");
            return true;
            
        } catch (SQLException e) {
            logger.error("Vector store health check failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private DataSource createDataSource(VectorStoreConfig config) {
        // 简化实现，实际应该使用连接池
        // 这里需要根据实际需求实现数据源创建
        return null; // 占位符，实际实现需要完善
    }
    
    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS vectors (
                id TEXT PRIMARY KEY,
                vector TEXT NOT NULL,
                metadata TEXT,
                created_at INTEGER,
                updated_at INTEGER
            )
            """;
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            logger.info("Vectors table created or already exists");
            
        }
    }
    
    private String vectorToString(float[] vector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        return sb.toString();
    }
    
    private float[] stringToVector(String str) {
        String[] parts = str.split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Float.parseFloat(parts[i]);
        }
        return vector;
    }
    
    private String metadataToString(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }
        // 简化实现，实际应该使用 JSON 序列化
        return metadata.toString();
    }
    
    private Map<String, Object> stringToMetadata(String str) {
        // 简化实现，实际应该使用 JSON 反序列化
        return new HashMap<>();
    }
    
    private float cosineSimilarity(float[] vectorA, float[] vectorB) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private String buildFilterClause(Map<String, Object> filter) {
        // 简化实现，实际需要根据元数据字段构建 SQL WHERE 子句
        return "1=1";
    }
    
    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Vector store not initialized");
        }
    }
}
