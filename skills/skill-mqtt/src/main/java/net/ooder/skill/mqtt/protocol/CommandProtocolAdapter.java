package net.ooder.skill.mqtt.protocol;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.CommandMessage;
import net.ooder.skill.mqtt.message.MqttMessage;

import java.util.Map;

/**
 * 命令协议适配�?- 处理设备命令协议
 */
public class CommandProtocolAdapter implements NorthboundProtocolAdapter {
    
    public static final String PROTOCOL_NAME = "ooder-command";
    public static final String PROTOCOL_VERSION = "1.0";
    
    public static final String TOPIC_REQUEST = "ooder/command/{deviceType}/{deviceId}/request";
    public static final String TOPIC_RESPONSE = "ooder/command/{deviceType}/{deviceId}/response";
    
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
        if (sourceMessage instanceof CommandMessage) {
            return adaptCommandToMqtt((CommandMessage) sourceMessage, context);
        } else if (sourceMessage instanceof Map) {
            return adaptMapToMqtt((Map<?, ?>) sourceMessage, context);
        }
        return null;
    }
    
    @Override
    public Object adaptFromMqtt(MqttMessage mqttMessage) {
        CommandMessage cmd = new CommandMessage();
        cmd.setCommandId(mqttMessage.getMessageId());
        cmd.setCreateTime(mqttMessage.getTimestamp());
        
        String topic = mqttMessage.getTopic();
        parseTopicToDevice(cmd, topic);
        
        String payload = mqttMessage.getPayloadAsString();
        cmd.setCommand(payload);
        
        return cmd;
    }
    
    @Override
    public boolean supports(Object message) {
        if (message instanceof CommandMessage) {
            return true;
        }
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            return map.containsKey("command") || map.containsKey("commandId");
        }
        return false;
    }
    
    private MqttMessage adaptCommandToMqtt(CommandMessage cmd, MqttContext context) {
        String topic = buildCommandTopic(cmd);
        
        MqttMessage msg = new MqttMessage();
        msg.setTopic(topic);
        msg.setMessageId(cmd.getCommandId());
        msg.setPayload(cmd.getCommand());
        msg.setQos(1);
        msg.setTimestamp(cmd.getCreateTime());
        msg.setSourceClientId(context.getClientId());
        
        return msg;
    }
    
    @SuppressWarnings("unchecked")
    private MqttMessage adaptMapToMqtt(Map<?, ?> map, MqttContext context) {
        CommandMessage cmd = new CommandMessage();
        
        Object cmdId = map.get("commandId");
        if (cmdId != null) {
            cmd.setCommandId(cmdId.toString());
        }
        
        Object cmdName = map.get("command");
        if (cmdName != null) {
            cmd.setCommand(cmdName.toString());
        }
        
        Object targetId = map.get("targetId");
        if (targetId != null) {
            cmd.setTargetId(targetId.toString());
        }
        
        Object targetType = map.get("targetType");
        if (targetType != null) {
            cmd.setTargetType(targetType.toString());
        }
        
        Object params = map.get("params");
        if (params instanceof Map) {
            cmd.getParams().putAll((Map<? extends String, ?>) params);
        }
        
        return adaptCommandToMqtt(cmd, context);
    }
    
    private String buildCommandTopic(CommandMessage cmd) {
        String deviceType = cmd.getTargetType() != null ? cmd.getTargetType() : "device";
        String deviceId = cmd.getTargetId() != null ? cmd.getTargetId() : "unknown";
        return TOPIC_REQUEST
            .replace("{deviceType}", deviceType)
            .replace("{deviceId}", deviceId);
    }
    
    private void parseTopicToDevice(CommandMessage cmd, String topic) {
        if (topic == null || !topic.startsWith("ooder/command/")) {
            return;
        }
        
        String suffix = topic.substring("ooder/command/".length());
        String[] parts = suffix.split("/");
        
        if (parts.length >= 2) {
            cmd.setTargetType(parts[0]);
            cmd.setTargetId(parts[1]);
        }
    }
    
    public static String buildRequestTopic(String deviceType, String deviceId) {
        return TOPIC_REQUEST
            .replace("{deviceType}", deviceType)
            .replace("{deviceId}", deviceId);
    }
    
    public static String buildResponseTopic(String deviceType, String deviceId) {
        return TOPIC_RESPONSE
            .replace("{deviceType}", deviceType)
            .replace("{deviceId}", deviceId);
    }
}
