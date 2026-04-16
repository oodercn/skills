package net.ooder.skill.org.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "org.local", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OrgSkillAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OrgSkillAutoConfiguration.class);

    @Value("${org.local.data-path:./data/org}")
    private String dataPath;

    @Value("${org.local.token-expire:86400}")
    private long tokenExpireSeconds;

    @Bean
    @ConditionalOnMissingBean(OrgSkill.class)
    public OrgSkill orgSkill() {
        log.info("Initializing LocalOrgSkill with data path: {}", dataPath);
        LocalOrgSkill localOrgSkill = new LocalOrgSkill(dataPath);
        localOrgSkill.setTokenExpireMs(tokenExpireSeconds * 1000L);
        log.info("LocalOrgSkill initialized successfully");
        return localOrgSkill;
    }
}
