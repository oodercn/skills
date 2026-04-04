package net.ooder.skill.discovery.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.discovery")
@ConditionalOnProperty(name = "skill.discovery.enabled", havingValue = "true", matchIfMissing = true)
public class DiscoveryAutoConfiguration {
}
