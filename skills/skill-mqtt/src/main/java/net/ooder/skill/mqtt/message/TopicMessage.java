package net.ooder.skill.mqtt.message;

import java.io.Serializable;

/**
 * Topic消息 - 广播/订阅消息模型
 */
public class TopicMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String topic;
    private String body;
    private int qos = 1;
    private boolean retained;
    private String sourceId;
    private String sourceType;
    private long createTime;
    
    public TopicMessage() {
        this.createTime = System.currentTimeMillis();
    }
    
    public TopicMessage(String topic, String body) {
        this();
        this.topic = topic;
        this.body = body;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
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
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public static TopicMessage create(String topic, String body) {
        return new TopicMessage(topic, body);
    }
}
