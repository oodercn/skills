package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;

/**
 * MQTT濞戝牊浼呮径鍕倞閸ｃ劍甯撮崣? */
public interface MqttMessageHandler {
    
    String getHandlerId();
    
    boolean canHandle(String topic);
    
    void handle(MqttContext context, MqttMessage message) throws Exception;
    
    int getOrder();
}
