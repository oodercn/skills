package net.ooder.skill.payment.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.payment.wechat")
@ConditionalOnProperty(name = "skill.payment.wechat.enabled", havingValue = "true", matchIfMissing = true)
public class WechatPayAutoConfiguration {
}
