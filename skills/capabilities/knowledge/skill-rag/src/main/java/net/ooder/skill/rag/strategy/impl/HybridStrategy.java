package net.ooder.skill.rag.strategy.impl;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RetrievedDocument;
import net.ooder.skill.rag.strategy.RetrievalStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class HybridStrategy implements RetrievalStrategy {
    
    private static final double KEYWORD_WEIGHT = 0.4;
    private static final double SEMANTIC_WEIGHT = 0.6;
    
    private final KeywordStrategy keywordStrategy;
    private final SemanticStrategy semanticStrategy;
    
    public HybridStrategy() {
        this.keywordStrategy = new KeywordStrategy();
        this.semanticStrategy = new SemanticStrategy();
    }
    
    @Override
    public String getName() {
        return "HYBRID";
    }
    
    @Override
    public List<RetrievedDocument> retrieve(RagContext context) {
        List<RetrievedDocument> keywordResults = keywordStrategy.retrieve(context);
        List<RetrievedDocument> semanticResults = semanticStrategy.retrieve(context);
        
        Map<String, RetrievedDocument> mergedDocs = new HashMap<>();
        Map<String, Double> scores = new HashMap<>();
        
        for (RetrievedDocument doc : keywordResults) {
            String key = doc.getKbId() + ":" + doc.getId();
            mergedDocs.put(key, doc);
            scores.merge(key, doc.getScore() * KEYWORD_WEIGHT, Double::sum);
        }
        
        for (RetrievedDocument doc : semanticResults) {
            String key = doc.getKbId() + ":" + doc.getId();
            if (mergedDocs.containsKey(key)) {
                scores.merge(key, doc.getScore() * SEMANTIC_WEIGHT, Double::sum);
            } else {
                mergedDocs.put(key, doc);
                scores.put(key, doc.getScore() * SEMANTIC_WEIGHT);
            }
        }
        
        for (Map.Entry<String, RetrievedDocument> entry : mergedDocs.entrySet()) {
            entry.getValue().setScore(scores.get(entry.getKey()));
        }
        
        return mergedDocs.values().stream()
                .filter(doc -> doc.getScore() >= context.getScoreThreshold())
                .sorted(Comparator.comparingDouble(RetrievedDocument::getScore).reversed())
                .limit(context.getTopK())
                .collect(Collectors.toList());
    }
    
    public void addDocument(String kbId, String docId, String content) {
        keywordStrategy.addDocument(kbId, docId, content);
        semanticStrategy.addDocument(kbId, docId, content);
    }
    
    public KeywordStrategy getKeywordStrategy() {
        return keywordStrategy;
    }
    
    public SemanticStrategy getSemanticStrategy() {
        return semanticStrategy;
    }
}
