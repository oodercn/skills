package net.ooder.skill.rag.service.impl;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RagResult;
import net.ooder.skill.rag.model.RetrievedDocument;
import net.ooder.skill.rag.service.RagEngine;
import net.ooder.skill.rag.strategy.RetrievalStrategy;
import net.ooder.skill.rag.strategy.impl.HybridStrategy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RagEngineImpl implements RagEngine {
    
    private final Map<String, RetrievalStrategy> strategies;
    private final Map<String, String> kbEndpoints;
    private RetrievalStrategy defaultStrategy;
    
    public RagEngineImpl() {
        this.strategies = new ConcurrentHashMap<>();
        this.kbEndpoints = new ConcurrentHashMap<>();
        initStrategies();
    }
    
    private void initStrategies() {
        HybridStrategy hybrid = new HybridStrategy();
        strategies.put("HYBRID", hybrid);
        strategies.put("KEYWORD", hybrid.getKeywordStrategy());
        strategies.put("SEMANTIC", hybrid.getSemanticStrategy());
        
        this.defaultStrategy = hybrid;
    }
    
    @Override
    public RagResult retrieve(RagContext context) {
        long startTime = System.currentTimeMillis();
        
        RetrievalStrategy strategy = strategies.get(context.getStrategy().toUpperCase());
        if (strategy == null) {
            strategy = defaultStrategy;
        }
        
        List<RetrievedDocument> documents = strategy.retrieve(context);
        
        RagResult result = new RagResult();
        result.setQuery(context.getQuery());
        result.setDocuments(documents);
        result.setStrategy(strategy.getName());
        result.setTotalRetrieved(documents.size());
        result.setRetrievalTimeMs(System.currentTimeMillis() - startTime);
        
        if (!documents.isEmpty()) {
            result.setMaxScore(documents.stream()
                    .mapToDouble(RetrievedDocument::getScore)
                    .max().orElse(0.0));
            result.setAvgScore(documents.stream()
                    .mapToDouble(RetrievedDocument::getScore)
                    .average().orElse(0.0));
        }
        
        result.setCombinedContext(buildCombinedContext(documents));
        
        return result;
    }
    
    @Override
    public String buildPrompt(RagContext context, RagResult result) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("基于以下参考信息回答问题：\n\n");
        
        if (result.getDocuments() != null && !result.getDocuments().isEmpty()) {
            int index = 1;
            for (RetrievedDocument doc : result.getDocuments()) {
                sb.append("【参考").append(index).append("】\n");
                if (doc.getTitle() != null) {
                    sb.append("标题：").append(doc.getTitle()).append("\n");
                }
                sb.append("内容：").append(doc.getContent()).append("\n");
                sb.append("相关度：").append(String.format("%.2f", doc.getScore())).append("\n\n");
                index++;
            }
        }
        
        sb.append("问题：").append(context.getQuery()).append("\n\n");
        sb.append("请根据以上参考信息回答问题，如果参考信息不足，请说明。");
        
        return sb.toString();
    }
    
    @Override
    public String buildPromptWithContext(String query, String context, String systemPrompt) {
        StringBuilder sb = new StringBuilder();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            sb.append(systemPrompt).append("\n\n");
        }
        
        if (context != null && !context.isEmpty()) {
            sb.append("参考信息：\n").append(context).append("\n\n");
        }
        
        sb.append("问题：").append(query);
        
        return sb.toString();
    }
    
    @Override
    public void registerKnowledgeBase(String kbId, String endpoint) {
        kbEndpoints.put(kbId, endpoint);
    }
    
    @Override
    public void unregisterKnowledgeBase(String kbId) {
        kbEndpoints.remove(kbId);
    }
    
    private String buildCombinedContext(List<RetrievedDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return "";
        }
        
        return documents.stream()
                .map(doc -> {
                    StringBuilder sb = new StringBuilder();
                    if (doc.getTitle() != null) {
                        sb.append("【").append(doc.getTitle()).append("】");
                    }
                    sb.append(doc.getContent());
                    return sb.toString();
                })
                .collect(Collectors.joining("\n\n"));
    }
    
    public void addDocument(String kbId, String docId, String content) {
        HybridStrategy hybrid = (HybridStrategy) strategies.get("HYBRID");
        if (hybrid != null) {
            hybrid.addDocument(kbId, docId, content);
        }
    }
}
