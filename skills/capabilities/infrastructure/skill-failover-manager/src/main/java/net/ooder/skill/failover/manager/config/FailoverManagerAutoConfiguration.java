package net.ooder.skill.failover.manager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.failover.manager")
@ConditionalOnProperty(name = "skill.failover.manager.enabled", havingValue = "true", matchIfMissing = true)
public class FailoverManagerAutoConfiguration {
}
