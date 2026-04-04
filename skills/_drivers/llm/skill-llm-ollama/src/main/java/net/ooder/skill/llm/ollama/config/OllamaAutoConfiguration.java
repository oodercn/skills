package net.ooder.skill.llm.ollama.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.ollama")
@ConditionalOnProperty(name = "skill.llm.ollama.enabled", havingValue = "true", matchIfMissing = true)
public class OllamaAutoConfiguration {
}
