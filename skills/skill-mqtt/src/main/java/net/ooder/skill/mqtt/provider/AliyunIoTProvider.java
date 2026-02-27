package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 闂冨潡鍣锋禍鎱杘T MQTT閺堝秴濮熼幓鎰返閼?- 娴滄垶婀囬崝鈥虫櫌閻劍鏌熷? * 
 * <p>闂冨潡鍣锋禍鎱杘T楠炲啿褰撮幓鎰返娴间椒绗熺痪顪換TT濞戝牊浼呴張宥呭閵?/p>
 * 
 * <h3>閻楀湱鍋ｉ敍?/h3>
 * <ul>
 *   <li>妤傛ê褰查悽銊ｂ偓渚€鐝崣顖炴浆</li>
 *   <li>鐠佹儳顦拋銈堢槈閸滃苯鐣ㄩ崗銊ь吀閻?/li>
 *   <li>鐟欏嫬鍨鏇熸惛閸滃本鏆熼幑顔界ウ鏉?/li>
 *   <li>鐠佹儳顦ぐ鍗炵摍</li>
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
