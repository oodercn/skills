package net.ooder.skill.media.zhihu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.media.zhihu")
@ConditionalOnProperty(name = "skill.media.zhihu.enabled", havingValue = "true", matchIfMissing = true)
public class ZhihuAutoConfiguration {
}
