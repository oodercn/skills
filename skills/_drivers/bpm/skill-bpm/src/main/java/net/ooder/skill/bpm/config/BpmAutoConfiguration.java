package net.ooder.skill.bpm.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.ooder.skill.bpm.engine.ProcessEngine;
import net.ooder.skill.bpm.engine.WorkflowClientService;
import net.ooder.skill.bpm.service.ProcessDefService;
import net.ooder.skill.bpm.service.ProcessInstService;

@Configuration
@AutoConfiguration
@ComponentScan(basePackages = "net.ooder.skill.bpm")
public class BpmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProcessEngine processEngine() {
        return new ProcessEngine();
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowClientService workflowClientService() {
        return new WorkflowClientService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefService processDefService() {
        return new ProcessDefService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstService processInstService() {
        return new ProcessInstService();
    }
}
