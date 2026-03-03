package net.ooder.skill.llm.context.service.impl;

import net.ooder.skill.llm.context.model.BuiltContext;
import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.service.ContextBuilder;
import net.ooder.skill.llm.context.service.ContextExtractor;

import net.ooder.sdk.llm.LlmSdk;
import net.ooder.sdk.llm.LlmSdkFactory;
import net.ooder.sdk.llm.nlp.NlpInteractionApi;
import net.ooder.sdk.llm.nlp.model.ContextOperation;
import net.ooder.sdk.llm.nlp.model.ContextOperationResult;
import net.ooder.sdk.llm.common.enums.MemoryType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ContextBuilderImpl implements ContextBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(ContextBuilderImpl.class);
    
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final double CHARS_PER_TOKEN = 1.5;
    
    @Autowired
    private List<ContextExtractor> extractors;
    
    private final LlmSdk llmSdk;
    private final NlpInteractionApi nlpApi;
    private final Map<String, Map<String, Object>> sessionContextCache = new ConcurrentHashMap<>();
    
    public ContextBuilderImpl() {
        this.llmSdk = LlmSdkFactory.create();
        this.nlpApi = llmSdk.getNlpInteractionApi();
    }

    @PostConstruct
    public void init() {
        if (extractors != null) {
            extractors = extractors.stream()
                .sorted(Comparator.comparingInt(ContextExtractor::getPriority))
                .collect(Collectors.toList());
            log.info("ContextBuilder initialized with {} extractors, SDK NlpInteractionApi enabled", extractors.size());
        }
    }
    
    @Override
    public BuiltContext build(ContextRequest request) {
        BuiltContext context = new BuiltContext();
        context.setId(UUID.randomUUID().toString());
        
        int maxTokens = request.getMaxTokens() > 0 ? request.getMaxTokens() : DEFAULT_MAX_TOKENS;
        context.setMaxTokens(maxTokens);
        
        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();
        
        List<BuiltContext.ContextSection> sections = new ArrayList<>();
        int totalTokens = 0;
        boolean truncated = false;
        
        if (extractors != null) {
            for (ContextExtractor extractor : extractors) {
                try {
                    String content = extractor.extract(request);
                    if (content != null && !content.isEmpty()) {
                        int tokens = extractor.estimateTokens(content);
                        
                        if (totalTokens + tokens > maxTokens) {
                            int remaining = maxTokens - totalTokens;
                            if (remaining > 100) {
                                content = truncateToTokenLimit(content, remaining);
                                tokens = countTokens(content);
                                truncated = true;
                            } else {
                                truncated = true;
                                break;
                            }
                        }
                        
                        BuiltContext.ContextSection section = new BuiltContext.ContextSection();
                        section.setSourceId(extractor.getType());
                        section.setType(extractor.getType());
                        section.setContent(content);
                        section.setTokens(tokens);
                        section.setPriority(extractor.getPriority());
                        sections.add(section);
                        
                        totalTokens += tokens;
                        
                        storeContextWithSdk(sessionId, extractor.getType(), content);
                    }
                } catch (Exception e) {
                    log.warn("Extractor {} failed: {}", extractor.getType(), e.getMessage());
                }
            }
        }
        
        context.setSections(sections);
        context.setTotalTokens(totalTokens);
        context.setTruncated(truncated);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("extractorCount", sections.size());
        metadata.put("requestUserId", request.getUserId());
        metadata.put("requestSceneId", request.getSceneId());
        metadata.put("sessionId", sessionId);
        metadata.put("sdkEnabled", true);
        context.setMetadata(metadata);
        
        log.debug("Built context: {} sections, {} tokens, truncated={}, sessionId={}", 
            sections.size(), totalTokens, truncated, sessionId);
        
        return context;
    }
    
    private void storeContextWithSdk(String sessionId, String key, String value) {
        try {
            ContextOperation operation = new ContextOperation();
            operation.setSessionId(sessionId);
            operation.setOperation("SET");
            operation.setKey(key);
            operation.setValue(value);
            operation.setMemoryType(MemoryType.SHORT_TERM);
            
            ContextOperationResult result = nlpApi.manageContext(operation);
            if (result != null && result.isSuccess()) {
                log.debug("Context stored via SDK: sessionId={}, key={}", sessionId, key);
            }
            
            Map<String, Object> sessionContext = sessionContextCache.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
            sessionContext.put(key, value);
            
        } catch (Exception e) {
            log.warn("Failed to store context via SDK: {}", e.getMessage());
        }
    }
    
    public Object getContextValue(String sessionId, String key) {
        Map<String, Object> sessionContext = sessionContextCache.get(sessionId);
        if (sessionContext != null) {
            return sessionContext.get(key);
        }
        
        try {
            ContextOperation operation = new ContextOperation();
            operation.setSessionId(sessionId);
            operation.setOperation("GET");
            operation.setKey(key);
            
            ContextOperationResult result = nlpApi.manageContext(operation);
            if (result != null && result.isSuccess()) {
                return result.getValue();
            }
        } catch (Exception e) {
            log.warn("Failed to get context via SDK: {}", e.getMessage());
        }
        
        return null;
    }
    
    public void clearSessionContext(String sessionId) {
        sessionContextCache.remove(sessionId);
        
        try {
            ContextOperation operation = new ContextOperation();
            operation.setSessionId(sessionId);
            operation.setOperation("CLEAR");
            
            nlpApi.manageContext(operation);
            log.debug("Session context cleared: sessionId={}", sessionId);
        } catch (Exception e) {
            log.warn("Failed to clear context via SDK: {}", e.getMessage());
        }
    }
    
    public Map<String, Object> getSessionContext(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> cached = sessionContextCache.get(sessionId);
        if (cached != null) {
            result.putAll(cached);
        }
        
        try {
            ContextOperation operation = new ContextOperation();
            operation.setSessionId(sessionId);
            operation.setOperation("GET_ALL");
            
            ContextOperationResult sdkResult = nlpApi.manageContext(operation);
            if (sdkResult != null && sdkResult.getContext() != null) {
                result.putAll(sdkResult.getContext());
            }
        } catch (Exception e) {
            log.warn("Failed to get session context via SDK: {}", e.getMessage());
        }
        
        return result;
    }

    @Override
    public String buildPrompt(ContextRequest request) {
        BuiltContext context = build(request);
        return context.toPrompt();
    }

    @Override
    public int countTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil(text.length() / CHARS_PER_TOKEN);
    }

    @Override
    public String truncateToTokenLimit(String text, int maxTokens) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        int maxChars = (int) (maxTokens * CHARS_PER_TOKEN);
        if (text.length() <= maxChars) {
            return text;
        }
        
        return text.substring(0, maxChars - 3) + "...";
    }
}
