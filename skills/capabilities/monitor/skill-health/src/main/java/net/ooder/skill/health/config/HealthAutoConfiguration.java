package net.ooder.skill.health.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.health")
@ConditionalOnProperty(name = "skill.health.enabled", havingValue = "true", matchIfMissing = true)
public class HealthAutoConfiguration {
}
