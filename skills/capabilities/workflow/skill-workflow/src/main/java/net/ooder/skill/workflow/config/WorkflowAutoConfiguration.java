package net.ooder.skill.workflow.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.ooder.skill.bpm.engine.ProcessEngine;
import net.ooder.skill.bpm.engine.WorkflowClientService;
import net.ooder.skill.bpm.service.ProcessDefService;
import net.ooder.skill.bpm.service.ProcessInstService;
import net.ooder.skill.workflow.service.WorkflowService;

@Configuration
@AutoConfiguration
@ComponentScan(basePackages = "net.ooder.skill.workflow")
public class WorkflowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WorkflowService workflowService(
            ProcessDefService processDefService,
            ProcessInstService processInstService,
            WorkflowClientService workflowClientService) {
        return new WorkflowService(processDefService, processInstService, workflowClientService);
    }
}
