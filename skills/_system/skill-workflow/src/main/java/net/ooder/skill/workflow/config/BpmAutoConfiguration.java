package net.ooder.skill.workflow.config;

import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.skill.workflow.core.BpmCoreServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(WorkflowClientService.class)
@ConditionalOnProperty(name = "skill.workflow.enabled", havingValue = "true", matchIfMissing = true)
public class BpmAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BpmAutoConfiguration.class);

    public BpmAutoConfiguration() {
        log.info("[BpmAutoConfiguration] BPM auto-configuration ready (BpmCoreServiceImpl will be auto-scanned)");
    }
}
