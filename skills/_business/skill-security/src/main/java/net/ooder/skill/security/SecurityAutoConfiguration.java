package net.ooder.skill.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ooder.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityAutoConfiguration {
}