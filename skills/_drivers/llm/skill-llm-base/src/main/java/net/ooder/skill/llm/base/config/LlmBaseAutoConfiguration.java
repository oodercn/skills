package net.ooder.skill.llm.base.config;

import net.ooder.skill.llm.base.LlmProviderRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmBaseAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public LlmProviderRegistry llmProviderRegistry() {
        return new LlmProviderRegistry();
    }
}
