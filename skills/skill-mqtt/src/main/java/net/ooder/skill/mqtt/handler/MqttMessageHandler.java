package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;

/**
 * MQTT消息处理器接�? */
public interface MqttMessageHandler {
    
    String getHandlerId();
    
    boolean canHandle(String topic);
    
    void handle(MqttContext context, MqttMessage message) throws Exception;
    
    int getOrder();
}
