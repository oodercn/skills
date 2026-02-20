package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云IoT MQTT服务提供�?- 云服务商用方�? * 
 * <p>阿里云IoT平台提供企业级MQTT消息服务�?/p>
 * 
 * <h3>特点�?/h3>
 * <ul>
 *   <li>高可用、高可靠</li>
 *   <li>设备认证和安全管�?/li>
 *   <li>规则引擎和数据流�?/li>
 *   <li>设备影子</li>
 * </ul>
 */
public class AliyunIoTProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(AliyunIoTProvider.class);
    
    public static final String PROVIDER_ID = "aliyun-iot";
    public static final String PROVIDER_NAME = "Aliyun IoT MQTT Service";
    
    private String regionId = "cn-shanghai";
    private String productKey;
    private String deviceName;
    private String deviceSecret;
    
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
        return ProviderType.CLOUD_MANAGED;
    }
    
    @Override
    public MqttServer createServer(MqttServerConfig config) {
        log.info("Aliyun IoT provider creates connection: region={}, product={}", regionId, productKey);
        return new AliyunIoTServerAdapter(config);
    }
    
    @Override
    public boolean isAvailable() {
        return productKey != null && deviceSecret != null;
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new HashMap<String, Object>();
        caps.put("maxConnections", "unlimited");
        caps.put("websocket", true);
        caps.put("ssl", true);
        caps.put("cluster", true);
        caps.put("persistence", true);
        caps.put("qos", new int[]{0, 1});
        caps.put("retained", false);
        caps.put("lastWill", true);
        caps.put("deviceShadow", true);
        caps.put("ruleEngine", true);
        caps.put("ota", true);
        return caps;
    }
    
    @Override
    public int getPriority() {
        return 10;
    }
    
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
    
    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
    
    public void setDeviceCredentials(String deviceName, String deviceSecret) {
        this.deviceName = deviceName;
        this.deviceSecret = deviceSecret;
    }
    
    public String getBrokerUrl() {
        return String.format("ssl://%s.iot-as-mqtt.%s.aliyuncs.com:1883", productKey, regionId);
    }
    
    private static class AliyunIoTServerAdapter implements MqttServer {
        private final MqttServerConfig config;
        private ServerStatus status = ServerStatus.CREATED;
        
        AliyunIoTServerAdapter(MqttServerConfig config) {
            this.config = config;
        }
        
        @Override
        public String getServerId() {
            return "aliyun-iot-adapter";
        }
        
        @Override
        public ServerStatus getStatus() {
            return status;
        }
        
        @Override
        public void start() {
            status = ServerStatus.RUNNING;
            log.info("Aliyun IoT adapter started");
        }
        
        @Override
        public void stop() {
            status = ServerStatus.STOPPED;
            log.info("Aliyun IoT adapter stopped");
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
