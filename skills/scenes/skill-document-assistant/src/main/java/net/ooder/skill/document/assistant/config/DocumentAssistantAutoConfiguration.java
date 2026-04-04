package net.ooder.skill.document.assistant.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.document.assistant")
@ConditionalOnProperty(name = "skill.document.assistant.enabled", havingValue = "true", matchIfMissing = true)
public class DocumentAssistantAutoConfiguration {
}
