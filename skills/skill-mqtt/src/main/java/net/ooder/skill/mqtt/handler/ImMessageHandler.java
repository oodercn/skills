package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.context.MqttSessionManager;
import net.ooder.skill.mqtt.message.ImMessage;
import net.ooder.skill.mqtt.message.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IM消息处理�?- 处理点对点消�? */
public class ImMessageHandler implements MqttMessageHandler {
    
    private static final Logger log = LoggerFactory.getLogger(ImMessageHandler.class);
    
    public static final String P2P_PREFIX = "ooder/p2p/";
    public static final String GROUP_PREFIX = "ooder/group/";
    
    @Override
    public String getHandlerId() {
        return "im-handler";
    }
    
    @Override
    public boolean canHandle(String topic) {
        return topic != null && (
            topic.startsWith(P2P_PREFIX) ||
            topic.startsWith(GROUP_PREFIX)
        );
    }
    
    @Override
    public void handle(MqttContext context, MqttMessage message) throws Exception {
        String topic = message.getTopic();
        
        log.info("IM message received: topic={}, from={}", topic, context.getClientId());
        
        ImMessage imMsg = parseImMessage(message, context);
        
        if (topic.startsWith(P2P_PREFIX)) {
            handleP2PMessage(context, imMsg);
        } else if (topic.startsWith(GROUP_PREFIX)) {
            handleGroupMessage(context, imMsg);
        }
    }
    
    @Override
    public int getOrder() {
        return 200;
    }
    
    protected ImMessage parseImMessage(MqttMessage message, MqttContext context) {
        ImMessage imMsg = new ImMessage();
        imMsg.setMsgId(message.getMessageId());
        imMsg.setFrom(context.getUserId());
        imMsg.setContent(message.getPayloadAsString());
        imMsg.setSendTime(message.getTimestamp());
        
        String topic = message.getTopic();
        if (topic.startsWith(P2P_PREFIX)) {
            imMsg.setTo(extractUserId(topic, P2P_PREFIX));
            imMsg.setMsgType("p2p");
        } else if (topic.startsWith(GROUP_PREFIX)) {
            imMsg.setTo(extractGroupId(topic, GROUP_PREFIX));
            imMsg.setMsgType("group");
        }
        
        return imMsg;
    }
    
    protected void handleP2PMessage(MqttContext context, ImMessage message) {
        String targetUserId = message.getTo();
        log.info("P2P message: from={}, to={}, content={}", 
            message.getFrom(), targetUserId, message.getContent());
        
        MqttContext targetContext = MqttSessionManager.getInstance().getSessionByUserId(targetUserId);
        if (targetContext != null) {
            log.info("Target user {} is online, sessionId={}", targetUserId, targetContext.getSessionId());
        } else {
            log.info("Target user {} is offline, message will be stored", targetUserId);
        }
    }
    
    protected void handleGroupMessage(MqttContext context, ImMessage message) {
        String groupId = message.getTo();
        log.info("Group message: from={}, group={}, content={}", 
            message.getFrom(), groupId, message.getContent());
    }
    
    private String extractUserId(String topic, String prefix) {
        String suffix = topic.substring(prefix.length());
        int slashIndex = suffix.indexOf('/');
        if (slashIndex > 0) {
            return suffix.substring(0, slashIndex);
        }
        return suffix;
    }
    
    private String extractGroupId(String topic, String prefix) {
        String suffix = topic.substring(prefix.length());
        int slashIndex = suffix.indexOf('/');
        if (slashIndex > 0) {
            return suffix.substring(0, slashIndex);
        }
        return suffix;
    }
}
