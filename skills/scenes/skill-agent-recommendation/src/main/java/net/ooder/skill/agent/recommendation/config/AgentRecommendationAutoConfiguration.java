package net.ooder.skill.agent.recommendation.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.agent.recommendation")
@ConditionalOnProperty(name = "skill.agent.recommendation.enabled", havingValue = "true", matchIfMissing = true)
public class AgentRecommendationAutoConfiguration {
}
