package net.ooder.skill.market.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.market")
@ConditionalOnProperty(name = "skill.market.enabled", havingValue = "true", matchIfMissing = true)
public class MarketAutoConfiguration {
}
