package net.ooder.skill.msg.dto;

import lombok.Data;

@Data
public class MsgGroup {
    private String groupId;
    private String name;
    private String type;
    private String ownerId;
    private String ownerName;
    private Integer memberCount;
    private Long createTime;
    private Long updateTime;
}
