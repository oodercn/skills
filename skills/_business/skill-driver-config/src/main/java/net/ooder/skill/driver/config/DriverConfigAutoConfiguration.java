package net.ooder.skill.driver.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.driver-config.enabled", havingValue = "true", matchIfMissing = true)
public class DriverConfigAutoConfiguration {
}