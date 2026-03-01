package net.ooder.skill.rag.strategy.impl;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RetrievedDocument;
import net.ooder.skill.rag.strategy.RetrievalStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeywordStrategy implements RetrievalStrategy {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[a-zA-Z]+|[0-9]+");
    
    private final Map<String, Map<String, String>> kbDocuments;
    
    public KeywordStrategy() {
        this.kbDocuments = new ConcurrentHashMap<>();
        initSampleData();
    }
    
    private void initSampleData() {
        Map<String, String> docs = new HashMap<>();
        docs.put("doc-1", "OODER是一个低代码开发平台，支持快速构建企业应用。通过可视化配置实现业务逻辑。");
        docs.put("doc-2", "知识库是OODER的核心功能之一，支持文档管理和智能检索。使用BM25算法进行相关性排序。");
        docs.put("doc-3", "LLM集成支持多种大语言模型，包括DeepSeek、OpenAI等。支持流式输出和上下文管理。");
        kbDocuments.put("kb-default", docs);
    }
    
    @Override
    public String getName() {
        return "KEYWORD";
    }
    
    @Override
    public List<RetrievedDocument> retrieve(RagContext context) {
        List<String> queryTerms = extractTerms(context.getQuery());
        if (queryTerms.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<RetrievedDocument> results = new ArrayList<>();
        List<String> kbIds = context.getKbIds() != null ? context.getKbIds() : 
                new ArrayList<>(kbDocuments.keySet());
        
        for (String kbId : kbIds) {
            Map<String, String> docs = kbDocuments.get(kbId);
            if (docs == null) continue;
            
            for (Map.Entry<String, String> entry : docs.entrySet()) {
                double score = calculateKeywordScore(entry.getValue(), queryTerms);
                if (score >= context.getScoreThreshold()) {
                    RetrievedDocument doc = new RetrievedDocument();
                    doc.setId(entry.getKey());
                    doc.setKbId(kbId);
                    doc.setContent(entry.getValue());
                    doc.setSnippet(generateSnippet(entry.getValue(), 100));
                    doc.setScore(score);
                    results.add(doc);
                }
            }
        }
        
        return results.stream()
                .sorted(Comparator.comparingDouble(RetrievedDocument::getScore).reversed())
                .limit(context.getTopK())
                .collect(Collectors.toList());
    }
    
    private List<String> extractTerms(String text) {
        List<String> terms = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() > 1) {
                terms.add(word);
            }
        }
        return terms;
    }
    
    private double calculateKeywordScore(String content, List<String> queryTerms) {
        String lowerContent = content.toLowerCase();
        int matches = 0;
        for (String term : queryTerms) {
            if (lowerContent.contains(term)) {
                matches++;
            }
        }
        return queryTerms.isEmpty() ? 0 : (double) matches / queryTerms.size();
    }
    
    private String generateSnippet(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    public void addDocument(String kbId, String docId, String content) {
        kbDocuments.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>()).put(docId, content);
    }
    
    public void removeDocument(String kbId, String docId) {
        Map<String, String> docs = kbDocuments.get(kbId);
        if (docs != null) {
            docs.remove(docId);
        }
    }
}
