package net.ooder.skill.payment.unionpay.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.payment.unionpay")
@ConditionalOnProperty(name = "skill.payment.unionpay.enabled", havingValue = "true", matchIfMissing = true)
public class UnionpayAutoConfiguration {
}
