package net.ooder.skill.knowledge.service.impl;

import net.ooder.skill.knowledge.model.SearchResult;
import net.ooder.skill.knowledge.service.DocumentIndexService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocumentIndexServiceImpl implements DocumentIndexService {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[a-zA-Z]+|[0-9]+");
    private static final double K1 = 1.5;
    private static final double B = 0.75;
    
    private final Map<String, Map<String, IndexedDocument>> indexStore = new ConcurrentHashMap<>();
    private final Map<String, IndexStats> statsStore = new ConcurrentHashMap<>();
    
    private static class IndexedDocument {
        String docId;
        String content;
        Map<String, Object> metadata;
        List<String> tokens;
        Map<String, Integer> termFreq;
        int length;
        
        IndexedDocument(String docId, String content, Map<String, Object> metadata) {
            this.docId = docId;
            this.content = content;
            this.metadata = metadata;
            this.tokens = tokenize(content);
            this.length = tokens.size();
            this.termFreq = calculateTermFreq(tokens);
        }
        
        private List<String> tokenize(String text) {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
            while (matcher.find()) {
                String word = matcher.group();
                if (word.length() > 1) {
                    tokens.add(word);
                }
            }
            return tokens;
        }
        
        private Map<String, Integer> calculateTermFreq(List<String> tokens) {
            Map<String, Integer> freq = new HashMap<>();
            for (String token : tokens) {
                freq.merge(token, 1, Integer::sum);
            }
            return freq;
        }
    }
    
    private static class IndexStats {
        int totalDocs;
        int totalLength;
        Map<String, Integer> docFreq = new HashMap<>();
        
        double avgLength() {
            return totalDocs > 0 ? (double) totalLength / totalDocs : 0;
        }
    }
    
    @Override
    public void indexDocument(String kbId, String docId, String content, Map<String, Object> metadata) {
        Map<String, IndexedDocument> index = indexStore.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>());
        IndexStats stats = statsStore.computeIfAbsent(kbId, k -> new IndexStats());
        
        IndexedDocument existing = index.get(docId);
        if (existing != null) {
            stats.totalDocs--;
            stats.totalLength -= existing.length;
            for (String term : existing.termFreq.keySet()) {
                stats.docFreq.merge(term, -1, Integer::sum);
                if (stats.docFreq.get(term) <= 0) {
                    stats.docFreq.remove(term);
                }
            }
        }
        
        IndexedDocument doc = new IndexedDocument(docId, content, metadata);
        index.put(docId, doc);
        
        stats.totalDocs++;
        stats.totalLength += doc.length;
        for (String term : doc.termFreq.keySet()) {
            stats.docFreq.merge(term, 1, Integer::sum);
        }
    }
    
    @Override
    public void removeDocument(String kbId, String docId) {
        Map<String, IndexedDocument> index = indexStore.get(kbId);
        IndexStats stats = statsStore.get(kbId);
        
        if (index != null && stats != null) {
            IndexedDocument doc = index.remove(docId);
            if (doc != null) {
                stats.totalDocs--;
                stats.totalLength -= doc.length;
                for (String term : doc.termFreq.keySet()) {
                    stats.docFreq.merge(term, -1, Integer::sum);
                    if (stats.docFreq.get(term) <= 0) {
                        stats.docFreq.remove(term);
                    }
                }
            }
        }
    }
    
    @Override
    public void updateDocument(String kbId, String docId, String content, Map<String, Object> metadata) {
        indexDocument(kbId, docId, content, metadata);
    }
    
    @Override
    public List<SearchResult> search(String kbId, String query, int topK, double threshold) {
        Map<String, IndexedDocument> index = indexStore.get(kbId);
        IndexStats stats = statsStore.get(kbId);
        
        if (index == null || stats == null || stats.totalDocs == 0) {
            return Collections.emptyList();
        }
        
        List<String> queryTokens = tokenize(query);
        if (queryTokens.isEmpty()) {
            return Collections.emptyList();
        }
        
        Map<String, Double> scores = new HashMap<>();
        double avgLength = stats.avgLength();
        
        for (IndexedDocument doc : index.values()) {
            double score = calculateBM25(doc, queryTokens, stats, avgLength);
            if (score >= threshold) {
                scores.put(doc.docId, score);
            }
        }
        
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topK)
                .map(entry -> createSearchResult(index.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SearchResult> searchMulti(List<String> kbIds, String query, int topK, double threshold) {
        List<SearchResult> allResults = new ArrayList<>();
        for (String kbId : kbIds) {
            allResults.addAll(search(kbId, query, topK * 2, threshold));
        }
        
        return allResults.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }
    
    @Override
    public void clearIndex(String kbId) {
        indexStore.remove(kbId);
        statsStore.remove(kbId);
    }
    
    @Override
    public void rebuildIndex(String kbId, Map<String, String> documents) {
        clearIndex(kbId);
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            indexDocument(kbId, entry.getKey(), entry.getValue(), new HashMap<>());
        }
    }
    
    @Override
    public long getDocumentCount(String kbId) {
        Map<String, IndexedDocument> index = indexStore.get(kbId);
        return index == null ? 0 : index.size();
    }
    
    @Override
    public Map<String, Object> getIndexStats(String kbId) {
        IndexStats stats = statsStore.get(kbId);
        Map<String, Object> result = new HashMap<>();
        if (stats != null) {
            result.put("totalDocs", stats.totalDocs);
            result.put("totalLength", stats.totalLength);
            result.put("avgLength", stats.avgLength());
            result.put("uniqueTerms", stats.docFreq.size());
        }
        return result;
    }
    
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() > 1) {
                tokens.add(word);
            }
        }
        return tokens;
    }
    
    private double calculateBM25(IndexedDocument doc, List<String> queryTokens, IndexStats stats, double avgLength) {
        double score = 0.0;
        int N = stats.totalDocs;
        
        for (String term : queryTokens) {
            Integer tf = doc.termFreq.get(term);
            if (tf == null) continue;
            
            Integer df = stats.docFreq.get(term);
            if (df == null) continue;
            
            double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1);
            double tfNorm = (tf * (K1 + 1)) / (tf + K1 * (1 - B + B * doc.length / avgLength));
            score += idf * tfNorm;
        }
        
        return score;
    }
    
    private SearchResult createSearchResult(IndexedDocument doc, double score) {
        SearchResult result = new SearchResult();
        result.setDocId(doc.docId);
        result.setScore(score);
        result.setMetadata(doc.metadata);
        
        String snippet = generateSnippet(doc.content, doc.tokens, 100);
        result.setSnippet(snippet);
        
        if (doc.metadata != null) {
            result.setTitle((String) doc.metadata.getOrDefault("title", "Untitled"));
        }
        
        return result;
    }
    
    private String generateSnippet(String content, List<String> tokens, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
