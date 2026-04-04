package net.ooder.skill.vfs.local.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.vfs.local")
@ConditionalOnProperty(name = "skill.vfs.local.enabled", havingValue = "true", matchIfMissing = true)
public class VfsLocalAutoConfiguration {
}
