package net.ooder.skill.vfs.s3.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.vfs.s3")
@ConditionalOnProperty(name = "skill.vfs.s3.enabled", havingValue = "true", matchIfMissing = true)
public class VfsS3AutoConfiguration {
}
