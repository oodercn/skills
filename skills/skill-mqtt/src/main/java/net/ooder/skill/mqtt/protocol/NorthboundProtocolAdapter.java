package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;

/**
 * 閸栨鎮滈崡蹇氼唴闁倿鍘ら崳銊﹀复閸? */
public interface NorthboundProtocolAdapter {
    
    String getProtocolName();
    
    String getProtocolVersion();
    
    MqttMessage adaptToMqtt(Object sourceMessage, MqttContext context);
    
    Object adaptFromMqtt(MqttMessage mqttMessage);
    
    boolean supports(Object message);
}
