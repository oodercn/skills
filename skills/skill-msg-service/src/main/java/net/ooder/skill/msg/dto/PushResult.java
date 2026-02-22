package net.ooder.skill.msg.dto;

/**
 * 消息推送结果
 */
public class PushResult {
    private String messageId;
    private boolean success;
    private String status;
    private String errorMessage;
    private long pushTime;

    public PushResult() {
        this.pushTime = System.currentTimeMillis();
    }

    public static PushResult success(String messageId) {
        PushResult result = new PushResult();
        result.setMessageId(messageId);
        result.setSuccess(true);
        result.setStatus("delivered");
        return result;
    }

    public static PushResult failure(String messageId, String errorMessage) {
        PushResult result = new PushResult();
        result.setMessageId(messageId);
        result.setSuccess(false);
        result.setStatus("failed");
        result.setErrorMessage(errorMessage);
        return result;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }
}
