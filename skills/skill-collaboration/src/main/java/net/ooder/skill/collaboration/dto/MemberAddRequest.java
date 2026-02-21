package net.ooder.skill.collaboration.dto;

import lombok.Data;

@Data
public class MemberAddRequest {
    private String memberId;
    private String memberName;
    private String role;
}
