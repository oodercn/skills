package net.ooder.skill.test.config;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.core.impl.SkillPackageManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SdkConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SdkConfig.class);
    
    @Value("${ooder.skill.root-path:./skills}")
    private String skillRootPath;
    
    @Bean
    @ConditionalOnMissingBean
    public SkillPackageManager skillPackageManager() {
        log.info("Creating SkillPackageManager with root path: {}", skillRootPath);
        SkillPackageManagerImpl manager = new SkillPackageManagerImpl();
        manager.setSkillRootPath(skillRootPath);
        return manager;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SkillService skillService(SkillPackageManager packageManager) {
        log.info("Creating SkillService");
        return new SkillService(packageManager);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public CapabilityRegistry capabilityRegistry() {
        log.info("Creating CapabilityRegistry");
        return new CapabilityRegistry();
    }
}
