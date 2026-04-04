package net.ooder.skill.im.feishu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.im.feishu")
@ConditionalOnProperty(name = "skill.im.feishu.enabled", havingValue = "true", matchIfMissing = true)
public class FeishuImAutoConfiguration {
}
