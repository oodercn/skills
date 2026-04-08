package net.ooder.skill.messaging.config;

import net.ooder.skill.messaging.service.UnifiedMessagingService;
import net.ooder.skill.messaging.service.UnifiedSessionService;
import net.ooder.skill.messaging.service.UnifiedWebSocketService;
import net.ooder.skill.messaging.service.impl.UnifiedMessagingServiceImpl;
import net.ooder.skill.messaging.service.impl.UnifiedSessionServiceImpl;
import net.ooder.skill.messaging.service.impl.UnifiedWebSocketServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingAutoConfiguration {

    @Bean
    public UnifiedMessagingService messagingService() {
        return new UnifiedMessagingServiceImpl();
    }

    @Bean
    public UnifiedSessionService sessionService() {
        return new UnifiedSessionServiceImpl();
    }

    @Bean
    public UnifiedWebSocketService webSocketService() {
        return new UnifiedWebSocketServiceImpl();
    }
}
