package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 北向协议适配器注册表
 */
public class ProtocolAdapterRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(ProtocolAdapterRegistry.class);
    
    private static final ProtocolAdapterRegistry INSTANCE = new ProtocolAdapterRegistry();
    
    private final Map<String, NorthboundProtocolAdapter> adapters = new ConcurrentHashMap<String, NorthboundProtocolAdapter>();
    
    private ProtocolAdapterRegistry() {
        registerDefaultAdapters();
    }
    
    public static ProtocolAdapterRegistry getInstance() {
        return INSTANCE;
    }
    
    public void register(NorthboundProtocolAdapter adapter) {
        String key = adapter.getProtocolName() + "-" + adapter.getProtocolVersion();
        adapters.put(key, adapter);
        log.info("Registered protocol adapter: {}", key);
    }
    
    public void unregister(String protocolName, String protocolVersion) {
        String key = protocolName + "-" + protocolVersion;
        adapters.remove(key);
        log.info("Unregistered protocol adapter: {}", key);
    }
    
    public NorthboundProtocolAdapter getAdapter(String protocolName) {
        for (NorthboundProtocolAdapter adapter : adapters.values()) {
            if (adapter.getProtocolName().equals(protocolName)) {
                return adapter;
            }
        }
        return null;
    }
    
    public NorthboundProtocolAdapter getAdapter(String protocolName, String protocolVersion) {
        String key = protocolName + "-" + protocolVersion;
        return adapters.get(key);
    }
    
    public MqttMessage adaptToMqtt(Object sourceMessage, MqttContext context) {
        for (NorthboundProtocolAdapter adapter : adapters.values()) {
            if (adapter.supports(sourceMessage)) {
                return adapter.adaptToMqtt(sourceMessage, context);
            }
        }
        log.warn("No adapter found for message type: {}", sourceMessage.getClass().getName());
        return null;
    }
    
    public Object adaptFromMqtt(MqttMessage mqttMessage) {
        String topic = mqttMessage.getTopic();
        NorthboundProtocolAdapter adapter = findAdapterByTopic(topic);
        
        if (adapter != null) {
            return adapter.adaptFromMqtt(mqttMessage);
        }
        
        return mqttMessage;
    }
    
    private NorthboundProtocolAdapter findAdapterByTopic(String topic) {
        if (topic == null) {
            return null;
        }
        
        if (topic.startsWith("ooder/command/")) {
            return getAdapter(CommandProtocolAdapter.PROTOCOL_NAME);
        } else if (topic.startsWith("ooder/p2p/") || topic.startsWith("ooder/group/")) {
            return getAdapter(ImProtocolAdapter.PROTOCOL_NAME);
        } else if (topic.startsWith("ooder/topic/") || topic.startsWith("ooder/broadcast/") ||
                   topic.startsWith("ooder/sensor/") || topic.startsWith("ooder/system/")) {
            return getAdapter(TopicProtocolAdapter.PROTOCOL_NAME);
        }
        
        return null;
    }
    
    private void registerDefaultAdapters() {
        register(new CommandProtocolAdapter());
        register(new ImProtocolAdapter());
        register(new TopicProtocolAdapter());
    }
    
    public void clear() {
        adapters.clear();
    }
    
    public int getAdapterCount() {
        return adapters.size();
    }
}
