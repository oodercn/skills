package net.ooder.skill.rag.strategy.impl;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RetrievedDocument;
import net.ooder.skill.rag.strategy.RetrievalStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SemanticStrategy implements RetrievalStrategy {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[a-zA-Z]+|[0-9]+");
    private static final int VECTOR_SIZE = 128;
    
    private final Map<String, Map<String, String>> kbDocuments;
    private final Map<String, Map<String, double[]>> kbVectors;
    
    public SemanticStrategy() {
        this.kbDocuments = new ConcurrentHashMap<>();
        this.kbVectors = new ConcurrentHashMap<>();
        initSampleData();
    }
    
    private void initSampleData() {
        Map<String, String> docs = new HashMap<>();
        docs.put("doc-1", "OODER是一个低代码开发平台，支持快速构建企业应用。通过可视化配置实现业务逻辑。");
        docs.put("doc-2", "知识库是OODER的核心功能之一，支持文档管理和智能检索。使用BM25算法进行相关性排序。");
        docs.put("doc-3", "LLM集成支持多种大语言模型，包括DeepSeek、OpenAI等。支持流式输出和上下文管理。");
        kbDocuments.put("kb-default", docs);
        
        Map<String, double[]> vectors = new HashMap<>();
        for (Map.Entry<String, String> entry : docs.entrySet()) {
            vectors.put(entry.getKey(), generateSimpleVector(entry.getValue()));
        }
        kbVectors.put("kb-default", vectors);
    }
    
    @Override
    public String getName() {
        return "SEMANTIC";
    }
    
    @Override
    public List<RetrievedDocument> retrieve(RagContext context) {
        double[] queryVector = generateSimpleVector(context.getQuery());
        
        List<ScoredDoc> scored = new ArrayList<>();
        List<String> kbIds = context.getKbIds() != null ? context.getKbIds() : 
                new ArrayList<>(kbDocuments.keySet());
        
        for (String kbId : kbIds) {
            Map<String, double[]> vectors = kbVectors.get(kbId);
            Map<String, String> docs = kbDocuments.get(kbId);
            if (vectors == null || docs == null) continue;
            
            for (Map.Entry<String, double[]> entry : vectors.entrySet()) {
                double similarity = cosineSimilarity(queryVector, entry.getValue());
                if (similarity >= context.getScoreThreshold()) {
                    ScoredDoc sd = new ScoredDoc();
                    sd.kbId = kbId;
                    sd.docId = entry.getKey();
                    sd.score = similarity;
                    sd.content = docs.get(entry.getKey());
                    scored.add(sd);
                }
            }
        }
        
        return scored.stream()
                .sorted(Comparator.comparingDouble(s -> -s.score))
                .limit(context.getTopK())
                .map(this::toRetrievedDocument)
                .collect(Collectors.toList());
    }
    
    private RetrievedDocument toRetrievedDocument(ScoredDoc sd) {
        RetrievedDocument doc = new RetrievedDocument();
        doc.setId(sd.docId);
        doc.setKbId(sd.kbId);
        doc.setContent(sd.content);
        doc.setSnippet(generateSnippet(sd.content, 100));
        doc.setScore(sd.score);
        return doc;
    }
    
    private double[] generateSimpleVector(String text) {
        double[] vector = new double[VECTOR_SIZE];
        Arrays.fill(vector, 0.0);
        
        List<String> tokens = tokenize(text);
        for (String token : tokens) {
            int hash = Math.abs(token.hashCode()) % VECTOR_SIZE;
            vector[hash] += 1.0;
        }
        
        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < VECTOR_SIZE; i++) {
                vector[i] /= norm;
            }
        }
        
        return vector;
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
    
    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) return 0.0;
        
        double dotProduct = 0.0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
        }
        return dotProduct;
    }
    
    private String generateSnippet(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    public void addDocument(String kbId, String docId, String content) {
        kbDocuments.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>()).put(docId, content);
        kbVectors.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>())
                .put(docId, generateSimpleVector(content));
    }
    
    private static class ScoredDoc {
        String kbId;
        String docId;
        double score;
        String content;
    }
}
