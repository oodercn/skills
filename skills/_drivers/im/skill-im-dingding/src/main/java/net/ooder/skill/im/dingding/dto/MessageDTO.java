package net.ooder.skill.im.dingding.dto;

import java.io.Serializable;
import java.util.Map;

public class MessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String msgType;
    private String receiver;
    private String receiverId;
    private String title;
    private String content;
    private Map<String, Object> extra;
    
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}
