package net.ooder.skill.collaboration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.collaboration")
@ConditionalOnProperty(name = "skill.collaboration.enabled", havingValue = "true", matchIfMissing = true)
public class CollaborationAutoConfiguration {
}
