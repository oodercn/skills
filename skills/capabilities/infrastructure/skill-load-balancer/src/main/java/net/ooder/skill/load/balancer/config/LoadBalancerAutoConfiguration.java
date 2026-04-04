package net.ooder.skill.load.balancer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.load.balancer")
@ConditionalOnProperty(name = "skill.load.balancer.enabled", havingValue = "true", matchIfMissing = true)
public class LoadBalancerAutoConfiguration {
}
