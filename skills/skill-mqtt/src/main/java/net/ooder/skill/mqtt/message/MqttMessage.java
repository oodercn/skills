package net.ooder.skill.mqtt.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT消息 - 统一消息模型
 */
public class MqttMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private String topic;
    private byte[] payload;
    private int qos = 1;
    private boolean retained;
    private boolean dup;
    private long timestamp;
    private String sourceClientId;
    private String targetClientId;
    
    private Map<String, Object> properties = new HashMap<String, Object>();
    
    public MqttMessage() {
        this.timestamp = System.currentTimeMillis();
        this.messageId = "msg-" + System.currentTimeMillis() + "-" + Integer.toHexString(hashCode());
    }
    
    public MqttMessage(String topic, byte[] payload) {
        this();
        this.topic = topic;
        this.payload = payload;
    }
    
    public MqttMessage(String topic, String payload) {
        this(topic, payload != null ? payload.getBytes() : null);
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public byte[] getPayload() {
        return payload;
    }
    
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public String getPayloadAsString() {
        return payload != null ? new String(payload) : null;
    }
    
    public void setPayload(String payload) {
        this.payload = payload != null ? payload.getBytes() : null;
    }
    
    public int getQos() {
        return qos;
    }
    
    public void setQos(int qos) {
        this.qos = qos;
    }
    
    public boolean isRetained() {
        return retained;
    }
    
    public void setRetained(boolean retained) {
        this.retained = retained;
    }
    
    public boolean isDup() {
        return dup;
    }
    
    public void setDup(boolean dup) {
        this.dup = dup;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSourceClientId() {
        return sourceClientId;
    }
    
    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public static MqttMessage create(String topic, String payload) {
        return new MqttMessage(topic, payload);
    }
    
    public static MqttMessage create(String topic, byte[] payload, int qos) {
        MqttMessage msg = new MqttMessage(topic, payload);
        msg.setQos(qos);
        return msg;
    }
}
