package net.ooder.skill.openwrt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.openwrt")
@ConditionalOnProperty(name = "skill.openwrt.enabled", havingValue = "true", matchIfMissing = true)
public class OpenWrtAutoConfiguration {
}
