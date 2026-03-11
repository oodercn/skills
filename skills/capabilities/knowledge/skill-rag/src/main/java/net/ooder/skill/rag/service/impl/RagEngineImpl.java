package net.ooder.skill.rag.service.impl;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RagResult;
import net.ooder.skill.rag.model.RetrievedDocument;
import net.ooder.skill.rag.service.RagEngine;
import net.ooder.skill.rag.strategy.RetrievalStrategy;

import net.ooder.scene.skill.rag.RagApi;
import net.ooder.scene.skill.rag.KnowledgeBaseConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RagEngineImpl implements RagEngine {
    
    @Autowired
    private RagApi ragApi;
    
    @Autowired
    private List<RetrievalStrategy> strategies;
    
    private final Map<String, RetrievalStrategy> strategyMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        if (strategies != null) {
            for (RetrievalStrategy strategy : strategies) {
                strategyMap.put(strategy.getName().toUpperCase(), strategy);
            }
        }
    }
    
    @Override
    public RagResult retrieve(RagContext context) {
        net.ooder.scene.skill.rag.RagContext sdkContext = convertToSdkContext(context);
        
        net.ooder.scene.skill.rag.RagResult sdkResult = ragApi.retrieve(sdkContext);
        
        return convertToSkillResult(sdkResult);
    }
    
    private net.ooder.scene.skill.rag.RagContext convertToSdkContext(RagContext context) {
        net.ooder.scene.skill.rag.RagContext sdkContext = new net.ooder.scene.skill.rag.RagContext();
        sdkContext.setQuery(context.getQuery());
        sdkContext.setTopK(context.getTopK() > 0 ? context.getTopK() : 10);
        sdkContext.setThreshold(context.getThreshold() > 0 ? context.getThreshold() : 0.5);
        
        if (context.getKbIds() != null && !context.getKbIds().isEmpty()) {
            sdkContext.setKbIds(context.getKbIds());
        }
        
        if (context.getParams() != null) {
            sdkContext.setParams(context.getParams());
        }
        
        return sdkContext;
    }
    
    private RagResult convertToSkillResult(net.ooder.scene.skill.rag.RagResult sdkResult) {
        RagResult result = new RagResult();
        result.setQuery(sdkResult.getQuery());
        
        if (sdkResult.getDocuments() != null) {
            List<RetrievedDocument> docs = sdkResult.getDocuments().stream()
                .map(this::convertToRetrievedDocument)
                .collect(Collectors.toList());
            result.setDocuments(docs);
        }
        
        if (sdkResult.getMetadata() != null) {
            result.setMetadata(sdkResult.getMetadata());
        }
        
        return result;
    }
    
    private RetrievedDocument convertToRetrievedDocument(net.ooder.scene.skill.rag.RetrievedDocument sdkDoc) {
        RetrievedDocument doc = new RetrievedDocument();
        doc.setId(sdkDoc.getId());
        doc.setContent(sdkDoc.getContent());
        doc.setScore(sdkDoc.getScore());
        doc.setSource(sdkDoc.getSource());
        
        if (sdkDoc.getMetadata() != null) {
            doc.setMetadata(sdkDoc.getMetadata());
        }
        
        return doc;
    }

    @Override
    public String buildPrompt(RagContext context, RagResult result) {
        return ragApi.augmentPrompt(context.getQuery(), convertToSdkResult(result));
    }
    
    private net.ooder.scene.skill.rag.RagResult convertToSdkResult(RagResult result) {
        net.ooder.scene.skill.rag.RagResult sdkResult = new net.ooder.scene.skill.rag.RagResult();
        sdkResult.setQuery(result.getQuery());
        
        if (result.getDocuments() != null) {
            List<net.ooder.scene.skill.rag.RetrievedDocument> sdkDocs = result.getDocuments().stream()
                .map(this::convertToSdkDocument)
                .collect(Collectors.toList());
            sdkResult.setDocuments(sdkDocs);
        }
        
        return sdkResult;
    }
    
    private net.ooder.scene.skill.rag.RetrievedDocument convertToSdkDocument(RetrievedDocument doc) {
        net.ooder.scene.skill.rag.RetrievedDocument sdkDoc = new net.ooder.scene.skill.rag.RetrievedDocument();
        sdkDoc.setId(doc.getId());
        sdkDoc.setContent(doc.getContent());
        sdkDoc.setScore(doc.getScore());
        sdkDoc.setSource(doc.getSource());
        sdkDoc.setMetadata(doc.getMetadata());
        return sdkDoc;
    }

    @Override
    public String buildPromptWithContext(String query, String context, String systemPrompt) {
        RagContext ragContext = new RagContext();
        ragContext.setQuery(query);
        
        RagResult mockResult = new RagResult();
        mockResult.setQuery(query);
        mockResult.setDocuments(Collections.emptyList());
        
        String augmentedPrompt = buildPrompt(ragContext, mockResult);
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            return systemPrompt + "\n\n" + augmentedPrompt;
        }
        
        return augmentedPrompt;
    }

    @Override
    public void registerKnowledgeBase(String kbId, String endpoint) {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        config.setEndpoint(endpoint);
        ragApi.registerKnowledgeBase(kbId, config);
    }

    @Override
    public void unregisterKnowledgeBase(String kbId) {
        ragApi.unregisterKnowledgeBase(kbId);
    }
    
    public RagResult hybridRetrieve(RagContext context, List<String> kbIds) {
        net.ooder.scene.skill.rag.RagContext sdkContext = convertToSdkContext(context);
        
        net.ooder.scene.skill.rag.RagResult sdkResult = ragApi.hybridRetrieve(sdkContext, kbIds);
        
        return convertToSkillResult(sdkResult);
    }
    
    public String generate(String query, RagContext context) {
        net.ooder.scene.skill.rag.RagContext sdkContext = convertToSdkContext(context);
        return ragApi.generate(query, sdkContext);
    }
    
    @Override
    public List<String> getAvailableStrategies() {
        return new ArrayList<>(strategyMap.keySet());
    }
    
    @Override
    public RagResult retrieveWithStrategy(RagContext context, String strategyName) {
        RetrievalStrategy strategy = strategyMap.get(strategyName.toUpperCase());
        if (strategy != null) {
            context.setStrategy(strategyName);
        }
        return retrieve(context);
    }
}
