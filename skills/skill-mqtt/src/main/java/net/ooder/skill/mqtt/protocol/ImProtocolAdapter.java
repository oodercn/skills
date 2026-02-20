package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.ImMessage;
import net.ooder.skill.mqtt.message.MqttMessage;

import java.util.Map;

/**
 * IM协议适配�?- 处理即时通讯协议
 */
public class ImProtocolAdapter implements NorthboundProtocolAdapter {
    
    public static final String PROTOCOL_NAME = "ooder-im";
    public static final String PROTOCOL_VERSION = "1.0";
    
    public static final String TOPIC_P2P = "ooder/p2p/{userId}/inbox";
    public static final String TOPIC_GROUP = "ooder/group/{groupId}/broadcast";
    
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
        if (sourceMessage instanceof ImMessage) {
            return adaptImToMqtt((ImMessage) sourceMessage, context);
        } else if (sourceMessage instanceof Map) {
            return adaptMapToMqtt((Map<?, ?>) sourceMessage, context);
        }
        return null;
    }
    
    @Override
    public Object adaptFromMqtt(MqttMessage mqttMessage) {
        ImMessage im = new ImMessage();
        im.setMsgId(mqttMessage.getMessageId());
        im.setSendTime(mqttMessage.getTimestamp());
        im.setContent(mqttMessage.getPayloadAsString());
        
        String topic = mqttMessage.getTopic();
        parseTopicToTarget(im, topic);
        
        return im;
    }
    
    @Override
    public boolean supports(Object message) {
        if (message instanceof ImMessage) {
            return true;
        }
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            return map.containsKey("content") && (map.containsKey("to") || map.containsKey("from"));
        }
        return false;
    }
    
    private MqttMessage adaptImToMqtt(ImMessage im, MqttContext context) {
        String topic = buildImTopic(im);
        
        MqttMessage msg = new MqttMessage();
        msg.setTopic(topic);
        msg.setMessageId(im.getMsgId());
        msg.setPayload(im.getContent());
        msg.setQos(1);
        msg.setTimestamp(im.getSendTime());
        msg.setSourceClientId(context.getClientId());
        
        return msg;
    }
    
    @SuppressWarnings("unchecked")
    private MqttMessage adaptMapToMqtt(Map<?, ?> map, MqttContext context) {
        ImMessage im = new ImMessage();
        
        Object msgId = map.get("msgId");
        if (msgId != null) {
            im.setMsgId(msgId.toString());
        }
        
        Object from = map.get("from");
        if (from != null) {
            im.setFrom(from.toString());
        }
        
        Object to = map.get("to");
        if (to != null) {
            im.setTo(to.toString());
        }
        
        Object content = map.get("content");
        if (content != null) {
            im.setContent(content.toString());
        }
        
        Object msgType = map.get("msgType");
        if (msgType != null) {
            im.setMsgType(msgType.toString());
        }
        
        Object extra = map.get("extra");
        if (extra instanceof Map) {
            im.getExtra().putAll((Map<? extends String, ?>) extra);
        }
        
        return adaptImToMqtt(im, context);
    }
    
    private String buildImTopic(ImMessage im) {
        String msgType = im.getMsgType();
        if ("group".equals(msgType)) {
            return TOPIC_GROUP.replace("{groupId}", im.getTo());
        } else {
            return TOPIC_P2P.replace("{userId}", im.getTo());
        }
    }
    
    private void parseTopicToTarget(ImMessage im, String topic) {
        if (topic == null) {
            return;
        }
        
        if (topic.startsWith("ooder/p2p/")) {
            String suffix = topic.substring("ooder/p2p/".length());
            int slashIndex = suffix.indexOf('/');
            if (slashIndex > 0) {
                im.setTo(suffix.substring(0, slashIndex));
            } else {
                im.setTo(suffix);
            }
            im.setMsgType("p2p");
        } else if (topic.startsWith("ooder/group/")) {
            String suffix = topic.substring("ooder/group/".length());
            int slashIndex = suffix.indexOf('/');
            if (slashIndex > 0) {
                im.setTo(suffix.substring(0, slashIndex));
            } else {
                im.setTo(suffix);
            }
            im.setMsgType("group");
        }
    }
    
    public static String buildP2PTopic(String userId) {
        return TOPIC_P2P.replace("{userId}", userId);
    }
    
    public static String buildGroupTopic(String groupId) {
        return TOPIC_GROUP.replace("{groupId}", groupId);
    }
}
