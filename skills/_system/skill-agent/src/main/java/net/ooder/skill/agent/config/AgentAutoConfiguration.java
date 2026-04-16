package net.ooder.skill.agent.config;

import net.ooder.skill.agent.service.*;
import net.ooder.skill.agent.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.agent")
@ConditionalOnProperty(name = "skill.agent.enabled", havingValue = "true", matchIfMissing = true)
public class AgentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AgentService.class)
    public AgentService agentService() {
        return new AgentServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentChatService.class)
    public AgentChatService agentChatService() {
        return new AgentChatServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentSessionService.class)
    public AgentSessionService agentSessionService() {
        return new AgentSessionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentMessageService.class)
    public AgentMessageService agentMessageService() {
        return new AgentMessageServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentLLMService.class)
    public AgentLLMService agentLLMService() {
        return new AgentLLMServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentRoleService.class)
    public AgentRoleService agentRoleService() {
        return new AgentRoleServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentHeartbeatMonitor.class)
    public AgentHeartbeatMonitor agentHeartbeatMonitor() {
        return new AgentHeartbeatMonitorImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AgentAlertMonitor.class)
    public AgentAlertMonitor agentAlertMonitor() {
        return new AgentAlertMonitorImpl();
    }

    @Bean
    @ConditionalOnMissingBean(FailoverService.class)
    public FailoverService failoverService() {
        return new FailoverServiceImpl();
    }
}
