package net.ooder.skill.payment.alipay.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.payment.alipay")
@ConditionalOnProperty(name = "skill.payment.alipay.enabled", havingValue = "true", matchIfMissing = true)
public class AlipayAutoConfiguration {
}
