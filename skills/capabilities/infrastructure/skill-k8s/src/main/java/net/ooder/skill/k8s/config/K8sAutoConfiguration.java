package net.ooder.skill.k8s.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.k8s")
@ConditionalOnProperty(name = "skill.k8s.enabled", havingValue = "true", matchIfMissing = true)
public class K8sAutoConfiguration {
}
