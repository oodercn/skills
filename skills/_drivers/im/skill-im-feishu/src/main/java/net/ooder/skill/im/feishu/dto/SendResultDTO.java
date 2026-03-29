package net.ooder.skill.im.feishu.dto;

import java.io.Serializable;

public class SendResultDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String messageId;
    private String errorCode;
    private String errorMessage;
    private long sendTime;
    
    public static SendResultDTO success(String messageId) {
        SendResultDTO result = new SendResultDTO();
        result.setSuccess(true);
        result.setMessageId(messageId);
        result.setSendTime(System.currentTimeMillis());
        return result;
    }
    
    public static SendResultDTO fail(String errorCode, String errorMessage) {
        SendResultDTO result = new SendResultDTO();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        result.setSendTime(System.currentTimeMillis());
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getSendTime() { return sendTime; }
    public void setSendTime(long sendTime) { this.sendTime = sendTime; }
}
