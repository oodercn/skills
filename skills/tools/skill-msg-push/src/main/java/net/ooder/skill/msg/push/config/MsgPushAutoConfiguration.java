package net.ooder.skill.msg.push.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.msg.push")
@ConditionalOnProperty(name = "skill.msg.push.enabled", havingValue = "true", matchIfMissing = true)
public class MsgPushAutoConfiguration {
}
