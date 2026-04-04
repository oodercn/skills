package net.ooder.skill.protocol.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.protocol")
@ConditionalOnProperty(name = "skill.protocol.enabled", havingValue = "true", matchIfMissing = true)
public class ProtocolAutoConfiguration {
}
