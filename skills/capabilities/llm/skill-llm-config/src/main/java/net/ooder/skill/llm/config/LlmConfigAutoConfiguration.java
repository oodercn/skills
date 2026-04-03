package net.ooder.skill.llm.config;

import net.ooder.skill.llm.config.service.LlmConfigService;
import net.ooder.skill.llm.config.service.impl.LlmConfigServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.llm.config.enabled", havingValue = "true", matchIfMissing = true)
public class LlmConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LlmConfigService.class)
    public LlmConfigService llmConfigService() {
        return new LlmConfigServiceImpl();
    }
}