package net.ooder.skill.template.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.template")
@ConditionalOnProperty(name = "skill.template.enabled", havingValue = "true", matchIfMissing = true)
public class TemplateAutoConfiguration {
}
