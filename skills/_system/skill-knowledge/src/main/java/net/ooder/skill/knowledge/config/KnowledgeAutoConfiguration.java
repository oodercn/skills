package net.ooder.skill.knowledge.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.knowledge")
@ConditionalOnProperty(name = "skill.knowledge.enabled", havingValue = "true", matchIfMissing = true)
public class KnowledgeAutoConfiguration {
}
