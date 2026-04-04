package net.ooder.skill.user.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.user.auth")
@ConditionalOnProperty(name = "skill.user.auth.enabled", havingValue = "true", matchIfMissing = true)
public class UserAuthAutoConfiguration {
}
