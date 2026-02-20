package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;
import net.ooder.skill.mqtt.message.TopicMessage;

import java.util.Map;

/**
 * Topic协议适配�?- 处理订阅/广播协议
 */
public class TopicProtocolAdapter implements NorthboundProtocolAdapter {
    
    public static final String PROTOCOL_NAME = "ooder-topic";
    public static final String PROTOCOL_VERSION = "1.0";
    
    public static final String TOPIC_PREFIX = "ooder/topic/";
    public static final String BROADCAST_PREFIX = "ooder/broadcast/";
    public static final String SENSOR_PREFIX = "ooder/sensor/";
    public static final String SYSTEM_PREFIX = "ooder/system/";
    
    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }
    
    @Override
    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }
    
    @Override
    public MqttMessage adaptToMqtt(Object sourceMessage, MqttContext context) {
        if (sourceMessage instanceof TopicMessage) {
            return adaptTopicToMqtt((TopicMessage) sourceMessage, context);
        } else if (sourceMessage instanceof Map) {
            return adaptMapToMqtt((Map<?, ?>) sourceMessage, context);
        }
        return null;
    }
    
    @Override
    public Object adaptFromMqtt(MqttMessage mqttMessage) {
        TopicMessage topic = new TopicMessage();
        topic.setTopic(mqttMessage.getTopic());
        topic.setBody(mqttMessage.getPayloadAsString());
        topic.setQos(mqttMessage.getQos());
        topic.setRetained(mqttMessage.isRetained());
        topic.setCreateTime(mqttMessage.getTimestamp());
        topic.setSourceId(mqttMessage.getSourceClientId());
        
        return topic;
    }
    
    @Override
    public boolean supports(Object message) {
        if (message instanceof TopicMessage) {
            return true;
        }
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            return map.containsKey("topic") && map.containsKey("body");
        }
        return false;
    }
    
    private MqttMessage adaptTopicToMqtt(TopicMessage topic, MqttContext context) {
        MqttMessage msg = new MqttMessage();
        String fullTopic = topic.getTopic();
        if (!fullTopic.startsWith("ooder/")) {
            fullTopic = TOPIC_PREFIX + fullTopic + "/data";
        }
        msg.setTopic(fullTopic);
        msg.setPayload(topic.getBody());
        msg.setQos(topic.getQos());
        msg.setRetained(topic.isRetained());
        msg.setTimestamp(topic.getCreateTime());
        msg.setSourceClientId(context.getClientId());
        
        return msg;
    }
    
    private MqttMessage adaptMapToMqtt(Map<?, ?> map, MqttContext context) {
        TopicMessage topic = new TopicMessage();
        
        Object topicName = map.get("topic");
        if (topicName != null) {
            topic.setTopic(topicName.toString());
        }
        
        Object body = map.get("body");
        if (body != null) {
            topic.setBody(body.toString());
        }
        
        Object qos = map.get("qos");
        if (qos instanceof Number) {
            topic.setQos(((Number) qos).intValue());
        }
        
        Object retained = map.get("retained");
        if (retained instanceof Boolean) {
            topic.setRetained((Boolean) retained);
        }
        
        Object sourceId = map.get("sourceId");
        if (sourceId != null) {
            topic.setSourceId(sourceId.toString());
        }
        
        return adaptTopicToMqtt(topic, context);
    }
    
    public static String buildTopicPath(String... parts) {
        StringBuilder sb = new StringBuilder(TOPIC_PREFIX);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }
    
    public static String buildBroadcastPath(String channel) {
        return BROADCAST_PREFIX + channel;
    }
    
    public static String buildSensorPath(String sensorType, String sensorId) {
        return SENSOR_PREFIX + sensorType + "/" + sensorId + "/data";
    }
    
    public static String buildSystemPath(String eventType) {
        return SYSTEM_PREFIX + eventType;
    }
}
