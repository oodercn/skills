package net.ooder.skill.document.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.document")
@ConditionalOnProperty(name = "skill.document.enabled", havingValue = "true", matchIfMissing = true)
public class DocumentProcessorAutoConfiguration {
}
