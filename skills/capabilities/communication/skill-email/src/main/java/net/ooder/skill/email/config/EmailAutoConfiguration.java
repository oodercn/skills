package net.ooder.skill.email.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.email")
@ConditionalOnProperty(name = "skill.email.enabled", havingValue = "true", matchIfMissing = true)
public class EmailAutoConfiguration {
}
