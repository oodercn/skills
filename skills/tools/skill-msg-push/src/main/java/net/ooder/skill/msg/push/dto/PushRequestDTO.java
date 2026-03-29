package net.ooder.skill.msg.push.dto;

import java.io.Serializable;
import java.util.Map;

public class PushRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String channel;
    private String msgType;
    private String title;
    private String content;
    private String receiver;
    private String receiverId;
    private Map<String, Object> extra;
    private String callbackUrl;
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}
