package net.ooder.skill.test.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.sdk.service.storage.persistence.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DocumentIndexService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentIndexService.class);
    
    private static final String NS_DOCUMENTS = "documents";
    private static final String NS_KB_DOCUMENTS = "kb-documents";
    private static final String NS_KB_META = "kb-meta";
    
    @Value("${ooder.kb.storage-path:./kb-storage}")
    private String storagePath;
    
    @Value("${ooder.kb.index-path:./kb-index}")
    private String indexPath;
    
    private Path storageDir;
    private Path indexDir;
    private StorageManager storageManager;
    private ObjectMapper objectMapper;
    
    private final Map<String, DocumentMeta> documentMetas = new ConcurrentHashMap<>();
    private final Map<String, List<IndexEntry>> invertedIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> kbDocuments = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> knowledgeBases = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        try {
            storageDir = Paths.get(storagePath);
            Files.createDirectories(storageDir);
            
            indexDir = Paths.get(indexPath);
            Files.createDirectories(indexDir);
            
            storageManager = new StorageManager(indexPath);
            storageManager.setCacheEnabled(true);
            storageManager.setMaxCacheSize(10000);
            
            objectMapper = new ObjectMapper();
            
            loadFromStorage();
            log.info("DocumentIndexService initialized, storage: {}, index: {}", storagePath, indexPath);
        } catch (Exception e) {
            log.error("Failed to initialize DocumentIndexService", e);
        }
    }
    
    public String storeDocument(String kbId, String fileName, byte[] content) {
        try {
            String docId = "doc-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
            
            Path kbDir = storageDir.resolve(kbId);
            Files.createDirectories(kbDir);
            
            Path docPath = kbDir.resolve(docId + "-" + fileName);
            Files.write(docPath, content);
            
            DocumentMeta meta = new DocumentMeta();
            meta.setDocId(docId);
            meta.setKbId(kbId);
            meta.setFileName(fileName);
            meta.setFilePath(docPath.toString());
            meta.setFileSize(content.length);
            meta.setCreateTime(new Date());
            meta.setStatus("indexed");
            
            documentMetas.put(docId, meta);
            saveDocumentMeta(meta);
            
            kbDocuments.computeIfAbsent(kbId, k -> new ArrayList<>()).add(docId);
            saveKbDocuments(kbId);
            
            String textContent = extractText(content, fileName);
            indexDocument(docId, textContent);
            
            log.info("Document stored: {} -> {}", docId, fileName);
            return docId;
        } catch (Exception e) {
            log.error("Failed to store document: {}", fileName, e);
            return null;
        }
    }
    
    private String extractText(byte[] content, String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".txt") || lowerName.endsWith(".md")) {
            return new String(content, StandardCharsets.UTF_8);
        } else if (lowerName.endsWith(".json")) {
            return new String(content, StandardCharsets.UTF_8);
        } else {
            try {
                String text = new String(content, StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                for (char c : text.toCharArray()) {
                    if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                        sb.append(c);
                    } else if (isChineseChar(c)) {
                        sb.append(c);
                    } else if ("\u3001\u3002\u3003\u3000\uFF01\uFF1F\uFF1B\uFF1A\u3010\u3011\u300A\u300B".indexOf(c) >= 0) {
                        sb.append(' ');
                    } else if (c >= 32 && c < 127) {
                        sb.append(c);
                    }
                }
                return sb.toString();
            } catch (Exception e) {
                log.warn("Failed to extract text from {}, using fallback", fileName);
                StringBuilder sb = new StringBuilder();
                for (byte b : content) {
                    if (b >= 32 && b < 127) {
                        sb.append((char) b);
                    } else if (b == '\n' || b == '\r' || b == '\t') {
                        sb.append(' ');
                    }
                }
                return sb.toString();
            }
        }
    }
    
    private void indexDocument(String docId, String content) {
        Map<String, Integer> termFreq = new HashMap<>();
        
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (isChineseChar(c)) {
                if (currentWord.length() > 0) {
                    String word = currentWord.toString().toLowerCase();
                    if (word.length() > 1) {
                        termFreq.merge(word, 1, Integer::sum);
                    }
                    currentWord.setLength(0);
                }
                
                termFreq.merge(String.valueOf(c).toLowerCase(), 1, Integer::sum);
                
                if (i + 1 < content.length() && isChineseChar(content.charAt(i + 1))) {
                    String bigram = content.substring(i, i + 2).toLowerCase();
                    termFreq.merge(bigram, 1, Integer::sum);
                }
            } else if (Character.isLetterOrDigit(c)) {
                currentWord.append(c);
            } else {
                if (currentWord.length() > 1) {
                    String word = currentWord.toString().toLowerCase();
                    if (word.length() > 1) {
                        termFreq.merge(word, 1, Integer::sum);
                    }
                    currentWord.setLength(0);
                }
            }
        }
        
        if (currentWord.length() > 1) {
            String word = currentWord.toString().toLowerCase();
            if (word.length() > 1) {
                termFreq.merge(word, 1, Integer::sum);
            }
        }
        
        int totalTerms = termFreq.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
            String term = entry.getKey();
            int freq = entry.getValue();
            
            IndexEntry ie = new IndexEntry();
            ie.setDocId(docId);
            ie.setTerm(term);
            ie.setFrequency(freq);
            ie.setScore((double) freq / Math.max(totalTerms, 1));
            
            invertedIndex.computeIfAbsent(term, k -> new ArrayList<>()).add(ie);
        }
        
        log.debug("Indexed document {} with {} unique terms", docId, termFreq.size());
    }
    
    private boolean isChineseChar(char c) {
        return (c >= 0x4E00 && c <= 0x9FFF) ||
               (c >= 0x3400 && c <= 0x4DBF) ||
               (c >= 0x20000 && c <= 0x2A6DF) ||
               (c >= 0x2A700 && c <= 0x2B73F) ||
               (c >= 0x2B740 && c <= 0x2B81F) ||
               (c >= 0x2B820 && c <= 0x2CEAF) ||
               (c >= 0xF900 && c <= 0xFAFF) ||
               (c >= 0x2F800 && c <= 0x2FA1F);
    }
    
    public List<SearchResult> search(String query, int topK, double threshold) {
        Map<String, Double> docScores = new HashMap<>();
        
        List<String> queryTerms = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            
            if (isChineseChar(c)) {
                if (currentWord.length() > 1) {
                    queryTerms.add(currentWord.toString().toLowerCase());
                    currentWord.setLength(0);
                }
                
                queryTerms.add(String.valueOf(c).toLowerCase());
                
                if (i + 1 < query.length() && isChineseChar(query.charAt(i + 1))) {
                    queryTerms.add(query.substring(i, i + 2).toLowerCase());
                }
            } else if (Character.isLetterOrDigit(c)) {
                currentWord.append(c);
            } else {
                if (currentWord.length() > 1) {
                    queryTerms.add(currentWord.toString().toLowerCase());
                    currentWord.setLength(0);
                }
            }
        }
        
        if (currentWord.length() > 1) {
            queryTerms.add(currentWord.toString().toLowerCase());
        }
        
        log.debug("Search query '{}' split into terms: {}", query, queryTerms);
        
        for (String term : queryTerms) {
            if (term.length() > 0) {
                List<IndexEntry> entries = invertedIndex.get(term);
                if (entries != null) {
                    for (IndexEntry ie : entries) {
                        docScores.merge(ie.getDocId(), ie.getScore(), Double::sum);
                    }
                }
            }
        }
        
        int queryTermCount = queryTerms.size() > 0 ? queryTerms.size() : 1;
        
        return docScores.entrySet().stream()
                .map(e -> {
                    SearchResult sr = new SearchResult();
                    sr.setDocId(e.getKey());
                    sr.setScore(e.getValue() / queryTermCount);
                    return sr;
                })
                .filter(sr -> sr.getScore() >= threshold)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(topK)
                .collect(Collectors.toList());
    }
    
    public DocumentMeta getDocumentMeta(String docId) {
        return documentMetas.get(docId);
    }
    
    public List<DocumentMeta> getDocumentsByKb(String kbId) {
        List<String> docIds = kbDocuments.get(kbId);
        if (docIds == null) {
            return new ArrayList<>();
        }
        return docIds.stream()
                .map(documentMetas::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public boolean deleteDocument(String docId) {
        DocumentMeta meta = documentMetas.remove(docId);
        if (meta == null) {
            return false;
        }
        
        try {
            Files.deleteIfExists(Paths.get(meta.getFilePath()));
        } catch (Exception e) {
            log.warn("Failed to delete file: {}", meta.getFilePath());
        }
        
        List<String> docs = kbDocuments.get(meta.getKbId());
        if (docs != null) {
            docs.remove(docId);
            saveKbDocuments(meta.getKbId());
        }
        
        for (List<IndexEntry> entries : invertedIndex.values()) {
            entries.removeIf(ie -> ie.getDocId().equals(docId));
        }
        
        try {
            storageManager.delete(NS_DOCUMENTS, docId);
        } catch (Exception e) {
            log.warn("Failed to delete document from storage", e);
        }
        
        log.info("Document deleted: {}", docId);
        return true;
    }
    
    public int getDocumentCount(String kbId) {
        List<String> docs = kbDocuments.get(kbId);
        return docs != null ? docs.size() : 0;
    }
    
    public String createKnowledgeBase(String name, String description) {
        String kbId = "kb-" + System.currentTimeMillis();
        
        Map<String, Object> kb = new HashMap<>();
        kb.put("id", kbId);
        kb.put("name", name);
        kb.put("description", description != null ? description : "");
        kb.put("docCount", 0);
        kb.put("createTime", new Date().toString());
        kb.put("updateTime", new Date().toString());
        
        knowledgeBases.put(kbId, kb);
        kbDocuments.put(kbId, new ArrayList<>());
        
        try {
            saveJson(NS_KB_META, kbId, kb);
            saveKbDocuments(kbId);
        } catch (Exception e) {
            log.error("Failed to save knowledge base", e);
        }
        
        log.info("Knowledge base created: {}", kbId);
        return kbId;
    }
    
    public List<Map<String, Object>> listKnowledgeBases() {
        return new ArrayList<>(knowledgeBases.values());
    }
    
    public Map<String, Object> getKnowledgeBase(String kbId) {
        return knowledgeBases.get(kbId);
    }
    
    public boolean deleteKnowledgeBase(String kbId) {
        Map<String, Object> removed = knowledgeBases.remove(kbId);
        if (removed == null) {
            return false;
        }
        
        List<String> docs = kbDocuments.remove(kbId);
        if (docs != null) {
            for (String docId : docs) {
                deleteDocument(docId);
            }
        }
        
        try {
            storageManager.delete(NS_KB_META, kbId);
            storageManager.delete(NS_KB_DOCUMENTS, kbId);
        } catch (Exception e) {
            log.warn("Failed to delete KB from storage", e);
        }
        
        log.info("Knowledge base deleted: {}", kbId);
        return true;
    }
    
    private void saveDocumentMeta(DocumentMeta meta) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("docId", meta.getDocId());
            data.put("kbId", meta.getKbId());
            data.put("fileName", meta.getFileName());
            data.put("filePath", meta.getFilePath());
            data.put("fileSize", meta.getFileSize());
            data.put("createTime", meta.getCreateTime().getTime());
            data.put("status", meta.getStatus());
            
            saveJson(NS_DOCUMENTS, meta.getDocId(), data);
        } catch (Exception e) {
            log.error("Failed to save document meta", e);
        }
    }
    
    private void saveKbDocuments(String kbId) {
        try {
            List<String> docs = kbDocuments.get(kbId);
            if (docs != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("kbId", kbId);
                data.put("documents", docs);
                data.put("count", docs.size());
                saveJson(NS_KB_DOCUMENTS, kbId, data);
            }
        } catch (Exception e) {
            log.error("Failed to save KB documents", e);
        }
    }
    
    private void saveJson(String namespace, String key, Map<String, Object> data) throws IOException {
        Path nsDir = indexDir.resolve(namespace);
        Files.createDirectories(nsDir);
        Path filePath = nsDir.resolve(sanitizeKey(key) + ".json");
        String json = objectMapper.writeValueAsString(data);
        Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
        log.debug("Saved JSON to: {}", filePath);
    }
    
    private Map<String, Object> loadJson(String namespace, String key) throws IOException {
        Path filePath = indexDir.resolve(namespace).resolve(sanitizeKey(key) + ".json");
        if (!Files.exists(filePath)) {
            return null;
        }
        String json = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }
    
    private void deleteJson(String namespace, String key) throws IOException {
        Path filePath = indexDir.resolve(namespace).resolve(sanitizeKey(key) + ".json");
        Files.deleteIfExists(filePath);
        log.debug("Deleted JSON: {}", filePath);
    }
    
    private List<String> listJsonKeys(String namespace) {
        List<String> keys = new ArrayList<>();
        Path nsDir = indexDir.resolve(namespace);
        if (Files.exists(nsDir) && Files.isDirectory(nsDir)) {
            try {
                Files.list(nsDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        keys.add(name.substring(0, name.length() - 5));
                    });
            } catch (IOException e) {
                log.warn("Failed to list keys in namespace: {}", namespace);
            }
        }
        return keys;
    }
    
    private String sanitizeKey(String key) {
        return key;
    }
    
    private void loadFromStorage() {
        try {
            List<String> kbIds = listJsonKeys(NS_KB_META);
            for (String kbId : kbIds) {
                Map<String, Object> kb = loadJson(NS_KB_META, kbId);
                if (kb != null) {
                    knowledgeBases.put(kbId, kb);
                }
            }
            
            List<String> kbDocKeys = listJsonKeys(NS_KB_DOCUMENTS);
            for (String kbId : kbDocKeys) {
                Map<String, Object> data = loadJson(NS_KB_DOCUMENTS, kbId);
                if (data != null) {
                    @SuppressWarnings("unchecked")
                    List<String> docs = (List<String>) data.get("documents");
                    if (docs != null) {
                        kbDocuments.put(kbId, new ArrayList<>(docs));
                    }
                }
            }
            
            List<String> docIds = listJsonKeys(NS_DOCUMENTS);
            for (String docId : docIds) {
                Map<String, Object> data = loadJson(NS_DOCUMENTS, docId);
                if (data != null) {
                    DocumentMeta meta = new DocumentMeta();
                    meta.setDocId((String) data.get("docId"));
                    meta.setKbId((String) data.get("kbId"));
                    meta.setFileName((String) data.get("fileName"));
                    meta.setFilePath((String) data.get("filePath"));
                    meta.setFileSize(((Number) data.get("fileSize")).longValue());
                    meta.setCreateTime(new Date(((Number) data.get("createTime")).longValue()));
                    meta.setStatus((String) data.get("status"));
                    
                    documentMetas.put(docId, meta);
                    
                    Path docPath = Paths.get(meta.getFilePath());
                    if (Files.exists(docPath)) {
                        String content = new String(Files.readAllBytes(docPath), StandardCharsets.UTF_8);
                        indexDocument(docId, content);
                    }
                }
            }
            
            log.info("Index loaded: {} documents, {} terms, {} knowledge bases", 
                    documentMetas.size(), invertedIndex.size(), knowledgeBases.size());
        } catch (Exception e) {
            log.warn("Failed to load from storage, starting fresh", e);
        }
    }
    
    public static class DocumentMeta {
        private String docId;
        private String kbId;
        private String fileName;
        private String filePath;
        private long fileSize;
        private Date createTime;
        private String status;
        
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class IndexEntry {
        private String docId;
        private String term;
        private int frequency;
        private double score;
        
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
        public int getFrequency() { return frequency; }
        public void setFrequency(int frequency) { this.frequency = frequency; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
    }
    
    public static class SearchResult {
        private String docId;
        private double score;
        
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
    }
}