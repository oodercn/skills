package net.ooder.skill.media.weibo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.media.weibo")
@ConditionalOnProperty(name = "skill.media.weibo.enabled", havingValue = "true", matchIfMissing = true)
public class WeiboAutoConfiguration {
}
