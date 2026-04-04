package net.ooder.skill.org.ldap.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.org.ldap")
@ConditionalOnProperty(name = "skill.org.ldap.enabled", havingValue = "true", matchIfMissing = true)
public class LdapAutoConfiguration {
}
