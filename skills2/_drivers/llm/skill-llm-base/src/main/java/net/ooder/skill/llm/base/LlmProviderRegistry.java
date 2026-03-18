package net.ooder.skill.llm.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LlmProviderRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(LlmProviderRegistry.class);
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, String> modelToProvider = new ConcurrentHashMap<>();
    
    public void register(LlmProvider provider) {
        if (provider == null) {
            return;
        }
        String providerId = provider.getProviderId();
        providers.put(providerId, provider);
        
        for (LlmModel model : provider.getAvailableModels()) {
            modelToProvider.put(model.getId(), providerId);
        }
        
        log.info("Registered LLM provider: {} with {} models", providerId, provider.getAvailableModels().size());
    }
    
    public void unregister(String providerId) {
        LlmProvider removed = providers.remove(providerId);
        if (removed != null) {
            for (LlmModel model : removed.getAvailableModels()) {
                modelToProvider.remove(model.getId());
            }
            log.info("Unregistered LLM provider: {}", providerId);
        }
    }
    
    public LlmProvider getProvider(String providerId) {
        return providers.get(providerId);
    }
    
    public LlmProvider getProviderForModel(String modelId) {
        String providerId = modelToProvider.get(modelId);
        return providerId != null ? providers.get(providerId) : null;
    }
    
    public List<LlmProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }
    
    public List<LlmModel> getAllModels() {
        List<LlmModel> allModels = new ArrayList<>();
        for (LlmProvider provider : providers.values()) {
            allModels.addAll(provider.getAvailableModels());
        }
        return allModels;
    }
    
    public LlmResponse chat(String modelId, String message) {
        LlmProvider provider = getProviderForModel(modelId);
        if (provider == null) {
            throw new RuntimeException("No provider found for model: " + modelId);
        }
        LlmRequest request = LlmRequest.of(modelId, message);
        return provider.chat(request);
    }
    
    public LlmResponse chat(String modelId, String systemPrompt, String userMessage) {
        LlmProvider provider = getProviderForModel(modelId);
        if (provider == null) {
            throw new RuntimeException("No provider found for model: " + modelId);
        }
        LlmRequest request = LlmRequest.of(modelId, systemPrompt, userMessage);
        return provider.chat(request);
    }
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("providerCount", providers.size());
        stats.put("modelCount", modelToProvider.size());
        
        Map<String, Integer> modelsByProvider = new HashMap<>();
        for (Map.Entry<String, LlmProvider> entry : providers.entrySet()) {
            modelsByProvider.put(entry.getKey(), entry.getValue().getAvailableModels().size());
        }
        stats.put("modelsByProvider", modelsByProvider);
        
        return stats;
    }
}
