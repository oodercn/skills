package net.ooder.skill.llm.context.service.impl;

import net.ooder.skill.llm.context.model.BuiltContext;
import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.service.ContextBuilder;
import net.ooder.skill.llm.context.service.ContextExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContextBuilderImpl implements ContextBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(ContextBuilderImpl.class);
    
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final double CHARS_PER_TOKEN = 1.5;
    
    @Autowired
    private List<ContextExtractor> extractors;
    
    @PostConstruct
    public void init() {
        if (extractors != null) {
            extractors = extractors.stream()
                .sorted(Comparator.comparingInt(ContextExtractor::getPriority))
                .collect(Collectors.toList());
            log.info("ContextBuilder initialized with {} extractors", extractors.size());
        }
    }
    
    @Override
    public BuiltContext build(ContextRequest request) {
        BuiltContext context = new BuiltContext();
        context.setId(UUID.randomUUID().toString());
        
        int maxTokens = request.getMaxTokens() > 0 ? request.getMaxTokens() : DEFAULT_MAX_TOKENS;
        context.setMaxTokens(maxTokens);
        
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
        context.setMetadata(metadata);
        
        log.debug("Built context: {} sections, {} tokens, truncated={}", 
            sections.size(), totalTokens, truncated);
        
        return context;
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
