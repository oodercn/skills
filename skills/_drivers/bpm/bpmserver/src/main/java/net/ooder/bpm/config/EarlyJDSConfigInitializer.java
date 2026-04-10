package net.ooder.bpm.config;

import net.ooder.common.CommonConfig;
import net.ooder.common.property.Properties;
import net.ooder.config.JDSConfig;
import net.ooder.server.JDSServer;

public class EarlyJDSConfigInitializer {

    private static volatile boolean initialized = false;

    static {
        init();
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        try {
            Properties props = new Properties();
            String jdsHome = System.getProperty("user.home") + "/.ooder/bpm";
            
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
            
            try {
                JDSServer.setMockMode(true);
            } catch (Exception e) {
                System.err.println("JDSServer.setMockMode failed (non-critical): " + e.getMessage());
            }
            
            initialized = true;
            System.out.println("EarlyJDSConfigInitializer: JDSConfig initialized successfully");
        } catch (Exception e) {
            System.err.println("EarlyJDSConfigInitializer: Failed to initialize JDSConfig: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
