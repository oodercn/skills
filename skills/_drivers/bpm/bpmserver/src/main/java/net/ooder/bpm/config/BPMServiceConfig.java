package net.ooder.bpm.config;

import net.ooder.config.JDSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class BPMServiceConfig {

    private static final Logger log = LoggerFactory.getLogger(BPMServiceConfig.class);

    @PostConstruct
    public void init() {
        log.info("BPM Service Config initialized - JDSConfig should already be initialized by EarlyJDSConfigInitializer");
        
        // 验证JDSConfig是否已初始化
        try {
            if (JDSConfig.getConfigName() == null) {
                log.warn("JDSConfig may not be properly initialized");
            } else {
                log.info("JDSConfig is properly initialized with configName: {}", JDSConfig.getConfigName());
            }
        } catch (Exception e) {
            log.warn("Error checking JDSConfig status: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying BPM Service Config...");
        log.info("BPM Service Config destroyed successfully");
    }
}
