package net.ooder.skill.mqtt.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * IM消息 - 点对点消息模�? */
public class ImMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String msgId;
    private String from;
    private String to;
    private String content;
    private String msgType;
    private long sendTime;
    private int status;
    private boolean needReceipt;
    
    private Map<String, Object> extra = new HashMap<String, Object>();
    
    public ImMessage() {
        this.sendTime = System.currentTimeMillis();
    }
    
    public ImMessage(String from, String to, String content) {
        this();
        this.from = from;
        this.to = to;
        this.content = content;
    }
    
    public String getMsgId() {
        return msgId;
    }
    
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getMsgType() {
        return msgType;
    }
    
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    
    public long getSendTime() {
        return sendTime;
    }
    
    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public boolean isNeedReceipt() {
        return needReceipt;
    }
    
    public void setNeedReceipt(boolean needReceipt) {
        this.needReceipt = needReceipt;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(String key, Object value) {
        extra.put(key, value);
    }
    
    public Object getExtra(String key) {
        return extra.get(key);
    }
    
    public static ImMessage create(String from, String to, String content) {
        return new ImMessage(from, to, content);
    }
}
