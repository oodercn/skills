package net.ooder.bpm.config;

import net.ooder.bpm.engine.MockWorkflowClientService;
import net.ooder.bpm.engine.WorkflowClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BPMServiceConfig {

    @Value("${ooder.user.system-code:bpm}")
    private String systemCode;

    @Bean
    public WorkflowClientService workflowClientService() {
        return new MockWorkflowClientService(systemCode);
    }
}
