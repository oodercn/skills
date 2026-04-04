package net.ooder.skill.llm.openai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.openai")
@ConditionalOnProperty(name = "skill.llm.openai.enabled", havingValue = "true", matchIfMissing = true)
public class OpenAiAutoConfiguration {
}
