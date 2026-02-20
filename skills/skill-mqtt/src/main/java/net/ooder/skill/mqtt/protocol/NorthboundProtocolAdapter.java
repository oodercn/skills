package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;

/**
 * 北向协议适配器接�? */
public interface NorthboundProtocolAdapter {
    
    String getProtocolName();
    
    String getProtocolVersion();
    
    MqttMessage adaptToMqtt(Object sourceMessage, MqttContext context);
    
    Object adaptFromMqtt(MqttMessage mqttMessage);
    
    boolean supports(Object message);
}
