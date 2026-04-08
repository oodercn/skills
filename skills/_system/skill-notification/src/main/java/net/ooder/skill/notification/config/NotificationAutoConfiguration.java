package net.ooder.skill.notification.config;

import net.ooder.skill.notification.service.*;
import net.ooder.skill.notification.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.notification")
@ConditionalOnProperty(name = "skill.notification.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NotificationService.class)
    public NotificationService notificationService() {
        return new NotificationServiceImpl();
    }
}
