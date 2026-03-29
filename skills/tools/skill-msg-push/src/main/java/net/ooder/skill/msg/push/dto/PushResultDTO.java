package net.ooder.skill.msg.push.dto;

import java.io.Serializable;

public class PushResultDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String messageId;
    private String channel;
    private String status;
    private String error;
    private long sendTime;
    
    public static PushResultDTO success(String messageId, String channel) {
        PushResultDTO result = new PushResultDTO();
        result.setSuccess(true);
        result.setMessageId(messageId);
        result.setChannel(channel);
        result.setStatus("SENT");
        result.setSendTime(System.currentTimeMillis());
        return result;
    }
    
    public static PushResultDTO fail(String channel, String error) {
        PushResultDTO result = new PushResultDTO();
        result.setSuccess(false);
        result.setChannel(channel);
        result.setStatus("FAILED");
        result.setError(error);
        result.setSendTime(System.currentTimeMillis());
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public long getSendTime() { return sendTime; }
    public void setSendTime(long sendTime) { this.sendTime = sendTime; }
}
