package net.ooder.skill.role.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.role")
@ConditionalOnProperty(name = "skill.role.enabled", havingValue = "true", matchIfMissing = true)
public class RoleAutoConfiguration {
}
