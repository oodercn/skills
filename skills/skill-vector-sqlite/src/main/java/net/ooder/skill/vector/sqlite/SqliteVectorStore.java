package net.ooder.skill.vector.sqlite;

import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SqliteVectorStore implements VectorStore {
    
    private static final Logger log = LoggerFactory.getLogger(SqliteVectorStore.class);
    
    @Value("${vector.sqlite.dbPath:./data/vectors.db}")
    private String dbPath;
    
    @Value("${vector.sqlite.embeddingDimension:1536}")
    private int embeddingDimension;
    
    private Connection connection;
    private final Map<String, VectorData> cache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        try {
            File dbFile = new File(dbPath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
            
            createTables();
            loadCache();
            
            log.info("SqliteVectorStore initialized with dbPath: {}, SDK VectorStore interface enabled", dbPath);
        } catch (SQLException e) {
            log.error("Failed to initialize SqliteVectorStore", e);
            throw new RuntimeException("Failed to initialize vector store", e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            log.info("SqliteVectorStore shutdown");
        } catch (SQLException e) {
            log.error("Failed to shutdown SqliteVectorStore", e);
        }
    }
    
    private void createTables() throws SQLException {
        String createVectorsTable = "CREATE TABLE IF NOT EXISTS vectors (" +
            "id TEXT PRIMARY KEY," +
            "embedding BLOB NOT NULL," +
            "created_at INTEGER NOT NULL" +
            ")";
        
        String createMetadataTable = "CREATE TABLE IF NOT EXISTS metadata (" +
            "id TEXT NOT NULL," +
            "key TEXT NOT NULL," +
            "value TEXT," +
            "PRIMARY KEY (id, key)," +
            "FOREIGN KEY (id) REFERENCES vectors(id) ON DELETE CASCADE" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createVectorsTable);
            stmt.execute(createMetadataTable);
        }
    }
    
    private void loadCache() throws SQLException {
        String sql = "SELECT id, embedding, created_at FROM vectors";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                byte[] blob = rs.getBytes("embedding");
                
                float[] embedding = bytesToEmbedding(blob);
                Map<String, Object> metadata = loadMetadata(id);
                
                VectorData data = new VectorData(id, embedding, metadata);
                cache.put(id, data);
            }
        }
        
        log.info("Loaded {} vectors into cache", cache.size());
    }
    
    private Map<String, Object> loadMetadata(String id) throws SQLException {
        Map<String, Object> metadata = new HashMap<>();
        String sql = "SELECT key, value FROM metadata WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("key");
                    String value = rs.getString("value");
                    metadata.put(key, parseValue(value));
                }
            }
        }
        
        return metadata;
    }
    
    private Object parseValue(String value) {
        if (value == null) return null;
        
        if (value.startsWith("int:")) {
            return Integer.parseInt(value.substring(4));
        }
        if (value.startsWith("long:")) {
            return Long.parseLong(value.substring(5));
        }
        if (value.startsWith("double:")) {
            return Double.parseDouble(value.substring(7));
        }
        if (value.startsWith("bool:")) {
            return Boolean.parseBoolean(value.substring(5));
        }
        
        return value;
    }
    
    private String serializeValue(Object value) {
        if (value == null) return null;
        
        if (value instanceof Integer) {
            return "int:" + value;
        }
        if (value instanceof Long) {
            return "long:" + value;
        }
        if (value instanceof Double) {
            return "double:" + value;
        }
        if (value instanceof Boolean) {
            return "bool:" + value;
        }
        
        return value.toString();
    }

    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        try {
            String insertVector = "INSERT OR REPLACE INTO vectors (id, embedding, created_at) VALUES (?, ?, ?)";
            
            try (PreparedStatement stmt = connection.prepareStatement(insertVector)) {
                stmt.setString(1, id);
                stmt.setBytes(2, embeddingToBytes(vector));
                stmt.setLong(3, System.currentTimeMillis());
                stmt.executeUpdate();
            }
            
            saveMetadata(id, metadata);
            
            VectorData data = new VectorData(id, vector, metadata != null ? metadata : new HashMap<>());
            cache.put(id, data);
            
            log.debug("Inserted vector: {}", id);
            
        } catch (SQLException e) {
            log.error("Failed to insert vector: {}", id, e);
            throw new RuntimeException("Failed to insert vector", e);
        }
    }
    
    @Override
    public void batchInsert(List<VectorData> vectors) {
        try {
            connection.setAutoCommit(false);
            
            for (VectorData data : vectors) {
                insert(data.getId(), data.getVector(), data.getMetadata());
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            
            log.info("Batch inserted {} vectors", vectors.size());
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                log.error("Failed to rollback transaction", ex);
            }
            log.error("Failed to batch insert vectors", e);
            throw new RuntimeException("Failed to batch insert vectors", e);
        }
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        List<SearchResult> results = new ArrayList<>();
        
        for (VectorData data : cache.values()) {
            if (filters != null && !matchesFilter(data.getMetadata(), filters)) {
                continue;
            }
            
            float similarity = cosineSimilarity(queryVector, data.getVector());
            
            SearchResult result = new SearchResult();
            result.setId(data.getId());
            result.setScore(similarity);
            result.setMetadata(data.getMetadata());
            results.add(result);
        }
        
        results.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        
        if (results.size() > topK) {
            results = results.subList(0, topK);
        }
        
        return results;
    }
    
    private boolean matchesFilter(Map<String, Object> metadata, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) return true;
        if (metadata == null) return false;
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            Object value = metadata.get(entry.getKey());
            if (!Objects.equals(value, entry.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    private float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0 || normB == 0) {
            return 0.0f;
        }
        
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private void saveMetadata(String id, Map<String, Object> metadata) throws SQLException {
        String deleteMetadata = "DELETE FROM metadata WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteMetadata)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
        
        if (metadata != null && !metadata.isEmpty()) {
            String insertMetadata = "INSERT INTO metadata (id, key, value) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertMetadata)) {
                for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                    stmt.setString(1, id);
                    stmt.setString(2, entry.getKey());
                    stmt.setString(3, serializeValue(entry.getValue()));
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @Override
    public void delete(String id) {
        try {
            String deleteMetadata = "DELETE FROM metadata WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteMetadata)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
            
            String deleteVector = "DELETE FROM vectors WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteVector)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
            
            cache.remove(id);
            
            log.debug("Deleted vector: {}", id);
            
        } catch (SQLException e) {
            log.error("Failed to delete vector: {}", id, e);
            throw new RuntimeException("Failed to delete vector", e);
        }
    }
    
    @Override
    public void deleteByMetadata(Map<String, Object> filters) {
        List<String> toDelete = new ArrayList<>();
        
        for (Map.Entry<String, VectorData> entry : cache.entrySet()) {
            if (matchesFilter(entry.getValue().getMetadata(), filters)) {
                toDelete.add(entry.getKey());
            }
        }
        
        for (String id : toDelete) {
            delete(id);
        }
        
        log.info("Deleted {} vectors by metadata filter", toDelete.size());
    }
    
    @Override
    public int getDimension() {
        return embeddingDimension;
    }
    
    @Override
    public long count() {
        return cache.size();
    }
    
    @Override
    public void clear() {
        try {
            String clearMetadata = "DELETE FROM metadata";
            String clearVectors = "DELETE FROM vectors";
            
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(clearMetadata);
                stmt.execute(clearVectors);
            }
            
            cache.clear();
            
            log.info("Cleared all vectors");
            
        } catch (SQLException e) {
            log.error("Failed to clear vectors", e);
            throw new RuntimeException("Failed to clear vectors", e);
        }
    }
    
    private byte[] embeddingToBytes(float[] embedding) {
        byte[] bytes = new byte[embedding.length * 4];
        for (int i = 0; i < embedding.length; i++) {
            int bits = Float.floatToIntBits(embedding[i]);
            bytes[i * 4] = (byte) (bits >> 24);
            bytes[i * 4 + 1] = (byte) (bits >> 16);
            bytes[i * 4 + 2] = (byte) (bits >> 8);
            bytes[i * 4 + 3] = (byte) bits;
        }
        return bytes;
    }
    
    private float[] bytesToEmbedding(byte[] bytes) {
        float[] embedding = new float[bytes.length / 4];
        for (int i = 0; i < embedding.length; i++) {
            int bits = ((bytes[i * 4] & 0xFF) << 24) |
                       ((bytes[i * 4 + 1] & 0xFF) << 16) |
                       ((bytes[i * 4 + 2] & 0xFF) << 8) |
                       (bytes[i * 4 + 3] & 0xFF);
            embedding[i] = Float.intBitsToFloat(bits);
        }
        return embedding;
    }
}
