package net.ooder.skill.notification.dto;

import java.util.List;
import java.util.Map;

public class MultiChannelMessageDTO {
    private String channel;
    private String msgType;
    private String receiver;
    private String receiverId;
    private String title;
    private String content;
    private List<String> receiverIds;
    private Map<String, Object> extra;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
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
    public List<String> getReceiverIds() { return receiverIds; }
    public void setReceiverIds(List<String> receiverIds) { this.receiverIds = receiverIds; }
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}
