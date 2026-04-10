package net.ooder.bpm.config;

import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.server.JDSServer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class JDSConfigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        
        try {
            Properties props = new Properties();
            String jdsHome = env.getProperty("ooder.jds.home", System.getProperty("user.home") + "/.ooder/bpm");
            String configName = env.getProperty("ooder.jds.config-name", "bpmserver");
            String serverUrl = env.getProperty("ooder.server.url", "http://127.0.0.1:8083/bpm");
            String serverPort = env.getProperty("ooder.server.port", "8083");
            
            props.setProperty("JDSHome", jdsHome);
            props.setProperty("jds.home", jdsHome);
            props.setProperty("jds.config-name", configName);
            props.setProperty("configName", configName);
            props.setProperty("server.url", serverUrl);
            props.setProperty("server.port", serverPort);
            props.setProperty("cluster.enabled", env.getProperty("ooder.cluster.enabled", "false"));
            props.setProperty("session.enabled", env.getProperty("ooder.session.enabled", "false"));
            props.setProperty("cache.enabled", env.getProperty("ooder.cache.enabled", "false"));
            props.setProperty("user.server-url", env.getProperty("ooder.user.server-url", serverUrl));
            props.setProperty("user.system-code", env.getProperty("ooder.user.system-code", "bpm"));
            props.setProperty("user.offline", env.getProperty("ooder.user.offline", "true"));
            
            CommonConfig.initForTest(props);
            JDSConfig.initForTest(props);
            
            try {
                JDSServer.setMockMode(true);
            } catch (Exception e) {
                System.err.println("JDSServer.setMockMode failed (non-critical): " + e.getMessage());
            }
            
            System.out.println("JDSConfig initialized successfully via ApplicationContextInitializer");
        } catch (Exception e) {
            System.err.println("Failed to initialize JDSConfig: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize JDSConfig", e);
        }
    }
}
