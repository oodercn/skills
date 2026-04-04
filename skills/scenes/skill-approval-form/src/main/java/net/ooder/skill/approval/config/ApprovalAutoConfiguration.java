package net.ooder.skill.approval.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.approval")
@ConditionalOnProperty(name = "skill.approval.enabled", havingValue = "true", matchIfMissing = true)
public class ApprovalAutoConfiguration {
}
