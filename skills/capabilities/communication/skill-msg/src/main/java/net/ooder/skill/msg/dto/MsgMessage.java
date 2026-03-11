package net.ooder.skill.msg.dto;

import lombok.Data;

@Data
public class MsgMessage {
    private String messageId;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toGroupId;
    private String content;
    private String type;
    private Long createTime;
    private Integer status;
    private Boolean isRead;
    private Boolean isRecalled;
}
