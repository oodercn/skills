package net.ooder.skill.report.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.report")
@ConditionalOnProperty(name = "skill.report.enabled", havingValue = "true", matchIfMissing = true)
public class ReportAutoConfiguration {
}
