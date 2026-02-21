package net.ooder.skill.msg.dto;

import lombok.Data;

@Data
public class MsgResult {
    private Boolean success;
    private String message;
    private String messageId;
    
    public static MsgResult success(String messageId) {
        MsgResult result = new MsgResult();
        result.setSuccess(true);
        result.setMessage("Message sent successfully");
        result.setMessageId(messageId);
        return result;
    }
    
    public static MsgResult fail(String message) {
        MsgResult result = new MsgResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
