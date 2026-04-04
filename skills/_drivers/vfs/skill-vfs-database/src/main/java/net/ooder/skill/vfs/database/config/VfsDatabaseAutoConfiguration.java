package net.ooder.skill.vfs.database.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.vfs.database")
@ConditionalOnProperty(name = "skill.vfs.database.enabled", havingValue = "true", matchIfMissing = true)
public class VfsDatabaseAutoConfiguration {
}
