package net.ooder.skill.media.xiaohongshu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.media.xiaohongshu")
@ConditionalOnProperty(name = "skill.media.xiaohongshu.enabled", havingValue = "true", matchIfMissing = true)
public class XiaohongshuAutoConfiguration {
}
