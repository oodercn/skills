package net.ooder.skill.vfs.minio.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.vfs.minio")
@ConditionalOnProperty(name = "skill.vfs.minio.enabled", havingValue = "true", matchIfMissing = true)
public class VfsMinioAutoConfiguration {
}
