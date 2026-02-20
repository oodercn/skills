package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Mosquitto企业级MQTT服务提供�?- 自建方案
 * 
 * <p>Mosquitto是一款轻量级开源MQTT Broker，适用于中小规模部署�?/p>
 * 
 * <h3>特点�?/h3>
 * <ul>
 *   <li>轻量级、低资源占用</li>
 *   <li>易于配置和部�?/li>
 *   <li>支持MQTT 5.0</li>
 *   <li>适合边缘计算场景</li>
 * </ul>
 */
public class MosquittoEnterpriseProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(MosquittoEnterpriseProvider.class);
    
    public static final String PROVIDER_ID = "mosquitto-enterprise";
    public static final String PROVIDER_NAME = "Mosquitto MQTT Broker";
    
    private String brokerUrl = "tcp://localhost:1883";
    
    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public ProviderType getProviderType() {
        return ProviderType.ENTERPRISE_SELF_HOSTED;
    }
    
    @Override
    public MqttServer createServer(MqttServerConfig config) {
        log.info("Mosquitto provider creates server connection to: {}", brokerUrl);
        return new MosquittoServerAdapter(config);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new HashMap<String, Object>();
        caps.put("maxConnections", 100000);
        caps.put("websocket", true);
        caps.put("ssl", true);
        caps.put("cluster", false);
        caps.put("persistence", true);
        caps.put("qos", new int[]{0, 1, 2});
        caps.put("retained", true);
        caps.put("lastWill", true);
        caps.put("mqtt5", true);
        return caps;
    }
    
    @Override
    public int getPriority() {
        return 60;
    }
    
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
    
    private static class MosquittoServerAdapter implements MqttServer {
        private final MqttServerConfig config;
        private ServerStatus status = ServerStatus.CREATED;
        
        MosquittoServerAdapter(MqttServerConfig config) {
            this.config = config;
        }
        
        @Override
        public String getServerId() {
            return "mosquitto-adapter";
        }
        
        @Override
        public ServerStatus getStatus() {
            return status;
        }
        
        @Override
        public void start() {
            status = ServerStatus.RUNNING;
            log.info("Mosquitto adapter started");
        }
        
        @Override
        public void stop() {
            status = ServerStatus.STOPPED;
            log.info("Mosquitto adapter stopped");
        }
        
        @Override
        public void initialize(MqttServerConfig config) {
        }
        
        @Override
        public MqttServerConfig getConfig() {
            return config;
        }
        
        @Override
        public void onConnect(net.ooder.skill.mqtt.context.MqttContext context) {
        }
        
        @Override
        public void onDisconnect(String sessionId) {
        }
        
        @Override
        public boolean isRunning() {
            return status == ServerStatus.RUNNING;
        }
        
        @Override
        public int getConnectedCount() {
            return 0;
        }
        
        @Override
        public Map<String, Object> getStatistics() {
            return new HashMap<String, Object>();
        }
    }
}
