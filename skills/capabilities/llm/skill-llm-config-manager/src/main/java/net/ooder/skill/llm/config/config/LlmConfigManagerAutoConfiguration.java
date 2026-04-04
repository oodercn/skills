package net.ooder.skill.llm.config.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.config")
@ConditionalOnProperty(name = "skill.llm.config.enabled", havingValue = "true", matchIfMissing = true)
public class LlmConfigManagerAutoConfiguration {
}
