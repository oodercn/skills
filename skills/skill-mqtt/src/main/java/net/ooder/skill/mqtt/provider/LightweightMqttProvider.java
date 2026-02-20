package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import net.ooder.skill.mqtt.server.MqttServerException;
import net.ooder.skill.mqtt.server.impl.LightweightMqttServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 轻量级MQTT服务提供�?- 降级方案
 */
public class LightweightMqttProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(LightweightMqttProvider.class);
    
    public static final String PROVIDER_ID = "lightweight-mqtt";
    public static final String PROVIDER_NAME = "Ooder Lightweight MQTT Server";
    
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
        return ProviderType.LIGHTWEIGHT;
    }
    
    @Override
    public MqttServer createServer(MqttServerConfig config) {
        LightweightMqttServer server = LightweightMqttServer.getInstance();
        try {
            if (config == null) {
                config = MqttServerConfig.builder()
                    .serverId("lightweight-mqtt")
                    .port(1883)
                    .websocketPort(8083)
                    .maxConnections(10000)
                    .build();
            }
            server.initialize(config);
        } catch (MqttServerException e) {
            log.error("Failed to initialize lightweight MQTT server", e);
        }
        return server;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new HashMap<String, Object>();
        caps.put("maxConnections", 10000);
        caps.put("websocket", true);
        caps.put("ssl", false);
        caps.put("cluster", false);
        caps.put("persistence", true);
        caps.put("qos", new int[]{0, 1, 2});
        caps.put("retained", true);
        caps.put("lastWill", true);
        return caps;
    }
    
    @Override
    public int getPriority() {
        return 100;
    }
}
