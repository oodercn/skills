package net.ooder.skill.httpclient.okhttp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.httpclient.okhttp")
@ConditionalOnProperty(name = "skill.httpclient.okhttp.enabled", havingValue = "true", matchIfMissing = true)
public class OkHttpAutoConfiguration {
}
