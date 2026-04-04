package net.ooder.skill.org.feishu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.org.feishu")
@ConditionalOnProperty(name = "skill.org.feishu.enabled", havingValue = "true", matchIfMissing = true)
public class FeishuOrgAutoConfiguration {
}
