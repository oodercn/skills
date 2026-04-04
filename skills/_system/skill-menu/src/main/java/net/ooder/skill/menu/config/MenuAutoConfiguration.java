package net.ooder.skill.menu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.menu")
@ConditionalOnProperty(name = "skill.menu.enabled", havingValue = "true", matchIfMissing = true)
public class MenuAutoConfiguration {
}
