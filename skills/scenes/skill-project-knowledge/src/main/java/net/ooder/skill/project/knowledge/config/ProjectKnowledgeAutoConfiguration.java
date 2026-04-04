package net.ooder.skill.project.knowledge.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.project.knowledge")
@ConditionalOnProperty(name = "skill.project.knowledge.enabled", havingValue = "true", matchIfMissing = true)
public class ProjectKnowledgeAutoConfiguration {
}
