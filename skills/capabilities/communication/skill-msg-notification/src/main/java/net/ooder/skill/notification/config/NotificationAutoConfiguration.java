package net.ooder.skill.notification.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.notification")
@ConditionalOnProperty(name = "skill.notification.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationAutoConfiguration {
}
