package net.ooder.skills.update.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skills.update")
@ConditionalOnProperty(name = "skill.update.checker.enabled", havingValue = "true", matchIfMissing = true)
public class UpdateCheckerAutoConfiguration {
}
