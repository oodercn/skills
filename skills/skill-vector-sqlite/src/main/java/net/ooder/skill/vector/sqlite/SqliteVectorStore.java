package net.ooder.skill.vector.sqlite;

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
    private final Map<String, VectorEntry> cache = new ConcurrentHashMap<>();
    
    @PostConstruct
    @Override
    public void initialize() {
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
            
            log.info("SqliteVectorStore initialized with dbPath: {}", dbPath);
        } catch (SQLException e) {
            log.error("Failed to initialize SqliteVectorStore", e);
            throw new RuntimeException("Failed to initialize vector store", e);
        }
    }
    
    @PreDestroy
    @Override
    public void shutdown() {
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
                long createdAt = rs.getLong("created_at");
                
                double[] embedding = bytesToEmbedding(blob);
                
                VectorEntry entry = new VectorEntry();
                entry.setId(id);
                entry.setEmbedding(embedding);
                entry.setCreatedAt(createdAt);
                entry.setMetadata(loadMetadata(id));
                
                cache.put(id, entry);
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
    public String addVector(String id, double[] embedding, Map<String, Object> metadata) {
        try {
            String insertVector = "INSERT OR REPLACE INTO vectors (id, embedding, created_at) VALUES (?, ?, ?)";
            
            try (PreparedStatement stmt = connection.prepareStatement(insertVector)) {
                stmt.setString(1, id);
                stmt.setBytes(2, embeddingToBytes(embedding));
                stmt.setLong(3, System.currentTimeMillis());
                stmt.executeUpdate();
            }
            
            saveMetadata(id, metadata);
            
            VectorEntry entry = new VectorEntry(id, embedding, metadata != null ? metadata : new HashMap<>());
            cache.put(id, entry);
            
            log.debug("Added vector: {}", id);
            return id;
            
        } catch (SQLException e) {
            log.error("Failed to add vector: {}", id, e);
            throw new RuntimeException("Failed to add vector", e);
        }
    }
    
    @Override
    public void addVectors(List<VectorEntry> entries) {
        try {
            connection.setAutoCommit(false);
            
            for (VectorEntry entry : entries) {
                addVector(entry.getId(), entry.getEmbedding(), entry.getMetadata());
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            
            log.info("Added {} vectors", entries.size());
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                log.error("Failed to rollback transaction", ex);
            }
            log.error("Failed to add vectors", e);
            throw new RuntimeException("Failed to add vectors", e);
        }
    }
    
    @Override
    public void updateVector(String id, double[] embedding, Map<String, Object> metadata) {
        addVector(id, embedding, metadata);
    }
    
    @Override
    public void deleteVector(String id) {
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
    public VectorEntry getVector(String id) {
        return cache.get(id);
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
    public List<SearchResult> searchSimilar(double[] queryEmbedding, int topK) {
        List<SearchResult> results = new ArrayList<>();
        
        for (VectorEntry entry : cache.values()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getEmbedding());
            
            SearchResult result = new SearchResult();
            result.setId(entry.getId());
            result.setScore(similarity);
            result.setMetadata(entry.getMetadata());
            results.add(result);
        }
        
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        if (results.size() > topK) {
            results = results.subList(0, topK);
        }
        
        return results;
    }
    
    @Override
    public List<SearchResult> searchSimilarWithFilter(double[] queryEmbedding, int topK, Map<String, Object> filter) {
        List<SearchResult> results = new ArrayList<>();
        
        for (VectorEntry entry : cache.values()) {
            if (!matchesFilter(entry.getMetadata(), filter)) {
                continue;
            }
            
            double similarity = cosineSimilarity(queryEmbedding, entry.getEmbedding());
            
            SearchResult result = new SearchResult();
            result.setId(entry.getId());
            result.setScore(similarity);
            result.setMetadata(entry.getMetadata());
            results.add(result);
        }
        
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        if (results.size() > topK) {
            results = results.subList(0, topK);
        }
        
        return results;
    }
    
    private boolean matchesFilter(Map<String, Object> metadata, Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) return true;
        if (metadata == null) return false;
        
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            Object value = metadata.get(entry.getKey());
            if (!Objects.equals(value, entry.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0 || normB == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private byte[] embeddingToBytes(double[] embedding) {
        byte[] bytes = new byte[embedding.length * 8];
        for (int i = 0; i < embedding.length; i++) {
            long bits = Double.doubleToLongBits(embedding[i]);
            bytes[i * 8] = (byte) (bits >> 56);
            bytes[i * 8 + 1] = (byte) (bits >> 48);
            bytes[i * 8 + 2] = (byte) (bits >> 40);
            bytes[i * 8 + 3] = (byte) (bits >> 32);
            bytes[i * 8 + 4] = (byte) (bits >> 24);
            bytes[i * 8 + 5] = (byte) (bits >> 16);
            bytes[i * 8 + 6] = (byte) (bits >> 8);
            bytes[i * 8 + 7] = (byte) bits;
        }
        return bytes;
    }
    
    private double[] bytesToEmbedding(byte[] bytes) {
        double[] embedding = new double[bytes.length / 8];
        for (int i = 0; i < embedding.length; i++) {
            long bits = ((long) (bytes[i * 8] & 0xFF) << 56) |
                        ((long) (bytes[i * 8 + 1] & 0xFF) << 48) |
                        ((long) (bytes[i * 8 + 2] & 0xFF) << 40) |
                        ((long) (bytes[i * 8 + 3] & 0xFF) << 32) |
                        ((long) (bytes[i * 8 + 4] & 0xFF) << 24) |
                        ((long) (bytes[i * 8 + 5] & 0xFF) << 16) |
                        ((long) (bytes[i * 8 + 6] & 0xFF) << 8) |
                        ((long) (bytes[i * 8 + 7] & 0xFF));
            embedding[i] = Double.longBitsToDouble(bits);
        }
        return embedding;
    }
    
    @Override
    public int getCount() {
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
}
