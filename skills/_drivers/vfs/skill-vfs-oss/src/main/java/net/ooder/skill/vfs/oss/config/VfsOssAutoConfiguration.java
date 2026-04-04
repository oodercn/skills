package net.ooder.skill.vfs.oss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.vfs.oss")
@ConditionalOnProperty(name = "skill.vfs.oss.enabled", havingValue = "true", matchIfMissing = true)
public class VfsOssAutoConfiguration {
}
