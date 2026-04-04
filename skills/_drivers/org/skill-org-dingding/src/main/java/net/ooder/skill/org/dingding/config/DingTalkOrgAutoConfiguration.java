package net.ooder.skill.org.dingding.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.org.dingding")
@ConditionalOnProperty(name = "skill.org.dingding.enabled", havingValue = "true", matchIfMissing = true)
public class DingTalkOrgAutoConfiguration {
}
