package net.ooder.bpm.config;

import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.server.JDSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class BPMServiceConfig {

    private static final Logger log = LoggerFactory.getLogger(BPMServiceConfig.class);

    @Value("${ooder.jds.home:${user.home}/.ooder/bpm}")
    private String jdsHome;

    @Value("${ooder.jds.config-name:bpmserver}")
    private String configName;

    @Value("${ooder.server.url:http://127.0.0.1:8083/bpm}")
    private String serverUrl;

    @Value("${ooder.server.port:8083}")
    private int serverPort;

    @Value("${ooder.cluster.enabled:false}")
    private boolean clusterEnabled;

    @Value("${ooder.session.enabled:false}")
    private boolean sessionEnabled;

    @Value("${ooder.cache.enabled:false}")
    private boolean cacheEnabled;

    @Value("${ooder.user.server-url:http://127.0.0.1:8083/bpm}")
    private String userServerUrl;

    @Value("${ooder.user.system-code:bpm}")
    private String systemCode;

    @Value("${ooder.user.offline:true}")
    private boolean offline;

    @PostConstruct
    public void init() {
        log.info("Initializing BPM Service Config...");
        
        try {
            Properties props = new Properties();
            props.setProperty("jds.home", jdsHome);
            props.setProperty("jds.config-name", configName);
            props.setProperty("server.url", serverUrl);
            props.setProperty("server.port", String.valueOf(serverPort));
            props.setProperty("cluster.enabled", String.valueOf(clusterEnabled));
            props.setProperty("session.enabled", String.valueOf(sessionEnabled));
            props.setProperty("cache.enabled", String.valueOf(cacheEnabled));
            props.setProperty("user.server-url", userServerUrl);
            props.setProperty("user.system-code", systemCode);
            props.setProperty("user.offline", String.valueOf(offline));
            
            JDSConfig.initForTest(props);
            CommonConfig.initForTest(props);
            
            JDSServer.setMockMode(true);
            
            log.info("BPM Service Config initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize BPM Service Config: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize BPM Service Config", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying BPM Service Config...");
        try {
            JDSConfig.reset();
            CommonConfig.reset();
            JDSServer.reset();
            log.info("BPM Service Config destroyed successfully");
        } catch (Exception e) {
            log.error("Failed to destroy BPM Service Config: {}", e.getMessage(), e);
        }
    }
}
