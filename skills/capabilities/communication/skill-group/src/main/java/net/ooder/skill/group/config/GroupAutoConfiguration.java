package net.ooder.skill.group.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.group")
@ConditionalOnProperty(name = "skill.group.enabled", havingValue = "true", matchIfMissing = true)
public class GroupAutoConfiguration {
}
