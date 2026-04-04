package net.ooder.skill.cmd.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.cmd")
@ConditionalOnProperty(name = "skill.cmd.enabled", havingValue = "true", matchIfMissing = true)
public class CmdAutoConfiguration {
}
