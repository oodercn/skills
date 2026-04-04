package net.ooder.skill.agent.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.agent")
@ConditionalOnProperty(name = "skill.agent.enabled", havingValue = "true", matchIfMissing = true)
public class AgentAutoConfiguration {
}
