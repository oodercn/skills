package net.ooder.skill.knowledge.local.service.impl;

import net.ooder.skill.knowledge.local.model.LocalDocument;
import net.ooder.skill.knowledge.local.model.SearchResult;
import net.ooder.skill.knowledge.local.service.LocalIndexService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocalIndexServiceImpl implements LocalIndexService {
    
    private static final Logger log = LoggerFactory.getLogger(LocalIndexServiceImpl.class);
    
    @Value("${knowledge.local.docPath:./docs}")
    private String docPath;
    
    private final Map<String, LocalDocument> documentMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Double>> invertedIndex = new ConcurrentHashMap<>();
    private final Map<String, Integer> documentFrequency = new ConcurrentHashMap<>();
    private final Map<String, Double> idfCache = new ConcurrentHashMap<>();
    
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".md", ".txt", ".json", ".yaml", ".yml", ".xml", ".html", ".csv"
    ));
    
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "的", "是", "在", "了", "和", "与", "或", "有", "这", "那", "我", "你", "他",
        "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did", "will", "would", "could", "should"
    ));
    
    @PostConstruct
    public void init() {
        log.info("Initializing local index with docPath: {}", docPath);
        scanAndIndex(docPath);
    }
    
    @Override
    public void scanAndIndex(String path) {
        File root = new File(path);
        if (!root.exists()) {
            log.warn("Document path does not exist: {}", path);
            return;
        }
        
        if (!root.isDirectory()) {
            log.warn("Document path is not a directory: {}", path);
            return;
        }
        
        clearIndex();
        scanDirectory(root, "");
        calculateIdf();
        
        log.info("Indexed {} documents from {}", documentMap.size(), path);
    }
    
    private void clearIndex() {
        documentMap.clear();
        invertedIndex.clear();
        documentFrequency.clear();
        idfCache.clear();
    }
    
    private void scanDirectory(File dir, String relativePath) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, relativePath + "/" + file.getName());
            } else if (isSupported(file)) {
                indexFile(file, relativePath);
            }
        }
    }
    
    private boolean isSupported(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (name.endsWith(ext)) return true;
        }
        return false;
    }
    
    private void indexFile(File file, String relativePath) {
        try {
            String content = readFileContent(file);
            String docId = relativePath + "/" + file.getName();
            
            LocalDocument doc = new LocalDocument();
            doc.setDocId(docId);
            doc.setPath(file.getAbsolutePath());
            doc.setTitle(extractTitle(content, file.getName()));
            doc.setContent(content);
            doc.setCategory(detectCategory(content, file.getName()));
            doc.setFileType(getFileExtension(file.getName()));
            doc.setFileSize(file.length());
            doc.setLastModified(file.lastModified());
            
            documentMap.put(docId, doc);
            indexDocument(docId, content);
            
        } catch (IOException e) {
            log.warn("Failed to index file: {}", file.getPath(), e);
        }
    }
    
    private String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), "UTF-8");
    }
    
    private String extractTitle(String content, String fileName) {
        Pattern titlePattern = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
        Matcher matcher = titlePattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return fileName.replaceAll("\\.[^.]+$", "");
    }
    
    private String detectCategory(String content, String fileName) {
        String lower = content.toLowerCase();
        
        if (lower.contains("api") || lower.contains("接口") || lower.contains("endpoint")) {
            return "API";
        }
        if (lower.contains("配置") || lower.contains("config") || lower.contains("设置")) {
            return "CONFIGURATION";
        }
        if (lower.contains("教程") || lower.contains("指南") || lower.contains("tutorial")) {
            return "TUTORIAL";
        }
        if (lower.contains("场景") || lower.contains("scene") || lower.contains("用例")) {
            return "SCENE";
        }
        if (lower.contains("skill") || lower.contains("技能") || lower.contains("能力")) {
            return "SKILL";
        }
        
        return "GENERAL";
    }
    
    private String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot) : "";
    }
    
    private void indexDocument(String docId, String content) {
        Map<String, Integer> termFrequency = new HashMap<>();
        String[] tokens = tokenize(content);
        
        for (String token : tokens) {
            if (token.length() < 2 || STOP_WORDS.contains(token)) continue;
            termFrequency.merge(token, 1, Integer::sum);
        }
        
        int totalTerms = tokens.length;
        
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            
            double tfScore = (double) tf / totalTerms;
            
            invertedIndex.computeIfAbsent(term, k -> new ConcurrentHashMap<>())
                .put(docId, tfScore);
            
            documentFrequency.merge(term, 1, Integer::sum);
        }
    }
    
    private String[] tokenize(String content) {
        return content.toLowerCase()
            .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
            .split("\\s+");
    }
    
    private void calculateIdf() {
        int totalDocs = documentMap.size();
        
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            String term = entry.getKey();
            int df = entry.getValue();
            double idf = Math.log((double) totalDocs / (df + 1));
            idfCache.put(term, idf);
        }
    }
    
    @Override
    public List<SearchResult> search(String query, int topK) {
        String[] queryTokens = tokenize(query);
        Map<String, Double> scores = new HashMap<>();
        
        for (String token : queryTokens) {
            if (token.length() < 2 || STOP_WORDS.contains(token)) continue;
            
            Map<String, Double> postings = invertedIndex.get(token);
            if (postings == null) continue;
            
            Double idf = idfCache.get(token);
            if (idf == null) idf = 1.0;
            
            for (Map.Entry<String, Double> posting : postings.entrySet()) {
                String docId = posting.getKey();
                double tf = posting.getValue();
                double tfidf = tf * idf;
                scores.merge(docId, tfidf, Double::sum);
            }
        }
        
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        List<SearchResult> results = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : sorted) {
            if (count >= topK) break;
            
            LocalDocument doc = documentMap.get(entry.getKey());
            if (doc != null) {
                SearchResult result = new SearchResult();
                result.setDocId(doc.getDocId());
                result.setTitle(doc.getTitle());
                result.setPath(doc.getPath());
                result.setCategory(doc.getCategory());
                result.setScore(entry.getValue());
                result.setSnippet(createSnippet(doc.getContent(), query));
                results.add(result);
                count++;
            }
        }
        
        return results;
    }
    
    private String createSnippet(String content, String query) {
        int maxLength = 200;
        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        
        int pos = lowerContent.indexOf(lowerQuery.split("\\s+")[0]);
        if (pos < 0) pos = 0;
        
        int start = Math.max(0, pos - 50);
        int end = Math.min(content.length(), pos + maxLength);
        
        String snippet = content.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < content.length()) snippet = snippet + "...";
        
        return snippet;
    }
    
    @Override
    public List<SearchResult> searchWithFilters(String query, Map<String, Object> filters, int topK) {
        List<SearchResult> baseResults = search(query, Integer.MAX_VALUE);
        
        List<SearchResult> filtered = new ArrayList<>();
        for (SearchResult result : baseResults) {
            if (matchesFilters(result, filters)) {
                filtered.add(result);
            }
        }
        
        if (filtered.size() <= topK) {
            return filtered;
        }
        return filtered.subList(0, topK);
    }
    
    private boolean matchesFilters(SearchResult result, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) return true;
        
        if (filters.containsKey("category")) {
            if (!filters.get("category").equals(result.getCategory())) {
                return false;
            }
        }
        
        if (filters.containsKey("fileType")) {
            LocalDocument doc = documentMap.get(result.getDocId());
            if (doc != null && !filters.get("fileType").equals(doc.getFileType())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public LocalDocument getDocument(String docId) {
        return documentMap.get(docId);
    }
    
    @Override
    public List<LocalDocument> listDocuments(String path) {
        List<LocalDocument> docs = new ArrayList<>();
        for (LocalDocument doc : documentMap.values()) {
            if (path == null || path.isEmpty() || doc.getDocId().startsWith(path)) {
                docs.add(doc);
            }
        }
        return docs;
    }
    
    @Override
    public int getDocumentCount() {
        return documentMap.size();
    }
    
    @Override
    public void reindex() {
        log.info("Reindexing documents...");
        scanAndIndex(docPath);
    }
}
