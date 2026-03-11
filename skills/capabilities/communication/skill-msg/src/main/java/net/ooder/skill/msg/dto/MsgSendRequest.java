package net.ooder.skill.msg.dto;

import lombok.Data;
import java.util.List;

@Data
public class MsgSendRequest {
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toGroupId;
    private String content;
    private String type;
}
