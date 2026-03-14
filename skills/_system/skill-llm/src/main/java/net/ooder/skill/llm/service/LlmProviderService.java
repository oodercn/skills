package net.ooder.skill.llm.service;

import net.ooder.skill.llm.model.LlmProvider;

import java.util.List;

public interface LlmProviderService {

    List<LlmProvider> findAll();

    LlmProvider findById(String providerId);

    LlmProvider save(LlmProvider provider);

    void delete(String providerId);

    void enable(String providerId);

    void disable(String providerId);

    LlmProvider create(LlmProvider provider);

    LlmProvider update(LlmProvider provider);

    boolean test(String providerId);
}
