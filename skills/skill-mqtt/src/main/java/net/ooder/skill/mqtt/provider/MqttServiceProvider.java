package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;

import java.util.Map;

/**
 * MQTT閺堝秴濮熼幓鎰返閼板懏甯撮崣? */
public interface MqttServiceProvider {
    
    String getProviderId();
    
    String getProviderName();
    
    ProviderType getProviderType();
    
    MqttServer createServer(MqttServerConfig config);
    
    boolean isAvailable();
    
    Map<String, Object> getCapabilities();
    
    int getPriority();
    
    enum ProviderType {
        LIGHTWEIGHT,
        ENTERPRISE_SELF_HOSTED,
        CLOUD_MANAGED
    }
}
