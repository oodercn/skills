package net.ooder.skill.chat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.chat")
@ConditionalOnProperty(name = "skill.chat.enabled", havingValue = "true", matchIfMissing = true)
public class ChatAutoConfiguration {
}
