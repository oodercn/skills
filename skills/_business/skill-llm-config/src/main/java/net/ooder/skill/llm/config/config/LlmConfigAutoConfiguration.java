package net.ooder.skill.llm.config.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.config")
@ConditionalOnProperty(name = "skill.llm.config.enabled", havingValue = "true", matchIfMissing = true)
public class LlmConfigAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LlmConfigAutoConfiguration.class);

    public LlmConfigAutoConfiguration() {
        log.info("[LlmConfigAutoConfiguration] Initializing LLM config skill module");
    }
}
