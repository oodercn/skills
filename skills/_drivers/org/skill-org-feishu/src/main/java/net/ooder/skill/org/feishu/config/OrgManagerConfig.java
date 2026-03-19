package net.ooder.skill.org.feishu.config;

import net.ooder.skill.org.feishu.org.FeishuOrgManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrgManagerConfig {

    @Bean
    public FeishuOrgManager feishuOrgManager() {
        return new FeishuOrgManager();
    }
}
