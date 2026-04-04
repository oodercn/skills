package net.ooder.skill.business.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.business")
@ConditionalOnProperty(name = "skill.business.enabled", havingValue = "true", matchIfMissing = true)
public class BusinessAutoConfiguration {
}
