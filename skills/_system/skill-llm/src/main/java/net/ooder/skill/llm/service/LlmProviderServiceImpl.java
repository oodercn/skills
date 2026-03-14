package net.ooder.skill.llm.service;

import net.ooder.skill.llm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LlmProviderServiceImpl implements LlmProviderService {

    private static final Logger log = LoggerFactory.getLogger(LlmProviderServiceImpl.class);

    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();

    public LlmProviderServiceImpl() {
        initDefaultProviders();
    }

    private void initDefaultProviders() {
        LlmProvider openai = new LlmProvider();
        openai.setProviderId("openai");
        openai.setName("OpenAI");
        openai.setType("openai");
        openai.setEndpoint("https://api.openai.com/v1");
        openai.setModels(Arrays.asList("gpt-4", "gpt-3.5-turbo"));
        openai.setEnabled(false);
        providers.put(openai.getProviderId(), openai);

        LlmProvider deepseek = new LlmProvider();
        deepseek.setProviderId("deepseek");
        deepseek.setName("DeepSeek");
        deepseek.setType("deepseek");
        deepseek.setEndpoint("https://api.deepseek.com/v1");
        deepseek.setModels(Arrays.asList("deepseek-chat", "deepseek-coder"));
        deepseek.setEnabled(false);
        providers.put(deepseek.getProviderId(), deepseek);
    }

    @Override
    public List<LlmProvider> findAll() {
        return new ArrayList<>(providers.values());
    }

    @Override
    public LlmProvider findById(String providerId) {
        return providers.get(providerId);
    }

    @Override
    public LlmProvider save(LlmProvider provider) {
        if (provider.getProviderId() == null) {
            provider.setProviderId("provider-" + UUID.randomUUID().toString().substring(0, 8));
        }
        provider.setUpdatedAt(new Date());
        providers.put(provider.getProviderId(), provider);
        return provider;
    }

    @Override
    public void delete(String providerId) {
        providers.remove(providerId);
    }

    @Override
    public void enable(String providerId) {
        LlmProvider provider = providers.get(providerId);
        if (provider != null) {
            provider.setEnabled(true);
            provider.setUpdatedAt(new Date());
        }
    }

    @Override
    public void disable(String providerId) {
        LlmProvider provider = providers.get(providerId);
        if (provider != null) {
            provider.setEnabled(false);
            provider.setUpdatedAt(new Date());
        }
    }

    @Override
    public LlmProvider create(LlmProvider provider) {
        return save(provider);
    }

    @Override
    public LlmProvider update(LlmProvider provider) {
        return save(provider);
    }

    @Override
    public boolean test(String providerId) {
        LlmProvider provider = findById(providerId);
        if (provider == null) {
            return false;
        }
        log.info("Testing provider: {}", providerId);
        return true;
    }
}
