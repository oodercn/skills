package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;
import net.ooder.skill.mqtt.message.TopicMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Topic消息处理�?- 处理订阅/广播消息
 */
public class TopicMessageHandler implements MqttMessageHandler {
    
    private static final Logger log = LoggerFactory.getLogger(TopicMessageHandler.class);
    
    public static final String TOPIC_PREFIX = "ooder/topic/";
    public static final String BROADCAST_PREFIX = "ooder/broadcast/";
    public static final String SENSOR_PREFIX = "ooder/sensor/";
    public static final String SYSTEM_PREFIX = "ooder/system/";
    
    @Override
    public String getHandlerId() {
        return "topic-handler";
    }
    
    @Override
    public boolean canHandle(String topic) {
        return topic != null && (
            topic.startsWith(TOPIC_PREFIX) ||
            topic.startsWith(BROADCAST_PREFIX) ||
            topic.startsWith(SENSOR_PREFIX) ||
            topic.startsWith(SYSTEM_PREFIX)
        );
    }
    
    @Override
    public void handle(MqttContext context, MqttMessage message) throws Exception {
        String topic = message.getTopic();
        String payload = message.getPayloadAsString();
        
        log.info("TopicMessage received: topic={}, from={}, qos={}", 
            topic, context.getClientId(), message.getQos());
        
        TopicMessage topicMsg = parseTopicMessage(message);
        
        if (topic.startsWith(BROADCAST_PREFIX)) {
            handleBroadcast(context, topicMsg);
        } else if (topic.startsWith(SENSOR_PREFIX)) {
            handleSensorData(context, topicMsg);
        } else if (topic.startsWith(SYSTEM_PREFIX)) {
            handleSystemMessage(context, topicMsg);
        } else {
            handleTopicMessage(context, topicMsg);
        }
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
    
    protected TopicMessage parseTopicMessage(MqttMessage message) {
        TopicMessage topicMsg = new TopicMessage();
        topicMsg.setTopic(message.getTopic());
        topicMsg.setBody(message.getPayloadAsString());
        topicMsg.setQos(message.getQos());
        topicMsg.setRetained(message.isRetained());
        topicMsg.setSourceId(message.getSourceClientId());
        return topicMsg;
    }
    
    protected void handleBroadcast(MqttContext context, TopicMessage message) {
        log.info("Broadcast message: topic={}, body={}", message.getTopic(), message.getBody());
    }
    
    protected void handleSensorData(MqttContext context, TopicMessage message) {
        log.debug("Sensor data: topic={}, body={}", message.getTopic(), message.getBody());
    }
    
    protected void handleSystemMessage(MqttContext context, TopicMessage message) {
        log.info("System message: topic={}, body={}", message.getTopic(), message.getBody());
    }
    
    protected void handleTopicMessage(MqttContext context, TopicMessage message) {
        log.info("Topic message: topic={}, body={}", message.getTopic(), message.getBody());
    }
}
