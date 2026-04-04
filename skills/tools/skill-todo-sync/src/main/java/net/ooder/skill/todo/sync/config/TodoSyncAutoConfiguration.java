package net.ooder.skill.todo.sync.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.todo.sync")
@ConditionalOnProperty(name = "skill.todo.sync.enabled", havingValue = "true", matchIfMissing = true)
public class TodoSyncAutoConfiguration {
}
