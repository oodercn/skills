package net.ooder.skill.agent.cli.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.agent.cli")
@ConditionalOnProperty(name = "skill.agent.cli.enabled", havingValue = "true", matchIfMissing = true)
public class AgentCliAutoConfiguration {
}
