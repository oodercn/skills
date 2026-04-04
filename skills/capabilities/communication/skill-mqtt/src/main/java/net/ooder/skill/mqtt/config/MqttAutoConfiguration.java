package net.ooder.skill.mqtt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.mqtt")
@ConditionalOnProperty(name = "skill.mqtt.enabled", havingValue = "true", matchIfMissing = true)
public class MqttAutoConfiguration {
}
