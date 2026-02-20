package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯云IoT MQTT服务提供�?- 云服务商用方�? * 
 * <p>腾讯云物联网平台提供企业级MQTT消息服务�?/p>
 * 
 * <h3>特点�?/h3>
 * <ul>
 *   <li>海量设备连接</li>
 *   <li>设备认证和安全通信</li>
 *   <li>消息转发和规则引�?/li>
 *   <li>设备状态管�?/li>
 * </ul>
 */
public class TencentIoTProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(TencentIoTProvider.class);
    
    public static final String PROVIDER_ID = "tencent-iot";
    public static final String PROVIDER_NAME = "Tencent Cloud IoT MQTT Service";
    
    private String region = "ap-guangzhou";
    private String productId;
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
        log.info("Tencent IoT provider creates connection: region={}, product={}", region, productId);
        return new TencentIoTServerAdapter(config);
    }
    
    @Override
    public boolean isAvailable() {
        return productId != null && deviceSecret != null;
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
        return 15;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public void setDeviceCredentials(String deviceName, String deviceSecret) {
        this.deviceName = deviceName;
        this.deviceSecret = deviceSecret;
    }
    
    public String getBrokerUrl() {
        return String.format("ssl://%s.iotcloud.tencentdevices.com:1883", productId);
    }
    
    private static class TencentIoTServerAdapter implements MqttServer {
        private final MqttServerConfig config;
        private ServerStatus status = ServerStatus.CREATED;
        
        TencentIoTServerAdapter(MqttServerConfig config) {
            this.config = config;
        }
        
        @Override
        public String getServerId() {
            return "tencent-iot-adapter";
        }
        
        @Override
        public ServerStatus getStatus() {
            return status;
        }
        
        @Override
        public void start() {
            status = ServerStatus.RUNNING;
            log.info("Tencent IoT adapter started");
        }
        
        @Override
        public void stop() {
            status = ServerStatus.STOPPED;
            log.info("Tencent IoT adapter stopped");
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
