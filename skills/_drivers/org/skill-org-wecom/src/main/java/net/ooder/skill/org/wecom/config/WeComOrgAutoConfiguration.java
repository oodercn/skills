package net.ooder.skill.org.wecom.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.org.wecom")
@ConditionalOnProperty(name = "skill.org.wecom.enabled", havingValue = "true", matchIfMissing = true)
public class WeComOrgAutoConfiguration {
}
