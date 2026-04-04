package net.ooder.skill.knowledge.share.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.knowledge.share")
@ConditionalOnProperty(name = "skill.knowledge.share.enabled", havingValue = "true", matchIfMissing = true)
public class KnowledgeShareAutoConfiguration {
}
