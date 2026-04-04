package net.ooder.skill.media.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.media.wechat")
@ConditionalOnProperty(name = "skill.media.wechat.enabled", havingValue = "true", matchIfMissing = true)
public class WechatMediaAutoConfiguration {
}
