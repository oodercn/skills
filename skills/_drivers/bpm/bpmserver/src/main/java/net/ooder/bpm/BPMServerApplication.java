package net.ooder.bpm;

import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.server.JDSServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BPMServerApplication {

    public static void main(String[] args) {
        initJDSConfig();
        SpringApplication.run(BPMServerApplication.class, args);
    }
    
    private static void initJDSConfig() {
        try {
            Properties props = new Properties();
            String jdsHome = System.getProperty("java.io.tmpdir") + "/bpm-jdshome";
            props.setProperty("JDSHome", jdsHome);
            props.setProperty("jds.home", jdsHome);
            props.setProperty("jds.config-name", "bpmserver");
            props.setProperty("configName", "bpmserver");
            props.setProperty("server.url", "http://127.0.0.1:8083/bpm");
            props.setProperty("server.port", "8083");
            props.setProperty("cluster.enabled", "false");
            props.setProperty("session.enabled", "false");
            props.setProperty("cache.enabled", "false");
            props.setProperty("user.server-url", "http://127.0.0.1:8083/bpm");
            props.setProperty("user.system-code", "bpm");
            props.setProperty("user.offline", "true");
            
            CommonConfig.initForTest(props);
            JDSConfig.initForTest(props);
            CommonConfig.initForTest(props);
            
            try {
                JDSServer.setMockMode(true);
            } catch (NullPointerException e) {
                System.err.println("JDSServer.setMockMode failed (non-critical): " + e.getMessage());
            }
            
            System.out.println("JDSConfig initialized for BPM Server");
        } catch (Exception e) {
            System.err.println("Failed to initialize JDSConfig: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
