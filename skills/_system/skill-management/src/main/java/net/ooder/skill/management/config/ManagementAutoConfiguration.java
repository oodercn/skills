package net.ooder.skill.management.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.management")
@ConditionalOnProperty(name = "skill.management.enabled", havingValue = "true", matchIfMissing = true)
public class ManagementAutoConfiguration {
}
